package com.dooioo.se.apidoclet.core.util;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dooioo.se.apidoclet.model.FieldInfo;
import com.dooioo.se.apidoclet.model.TypeInfo;
import com.dooioo.se.apidoclet.model.util.Types;
import com.dooioo.se.apidoclet.model.util.Types.SpiView;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/**
 * @author huisman
 * @version 1.0.0
 * @since 2016年1月16日 Copyright (c) 2016, BookDao All Rights Reserved.
 */
public final class ClassUtils {
  /**
   * GETTER的前缀
   */
  private static final String GETTER_PREFIX = "get";
  private static final int GETTER_PREFIX_LEN = GETTER_PREFIX.length();
  /**
   * Boolean值
   */
  private static final String IS_PREFIX = "is";
  private static final int IS_PREFIX_LEN = IS_PREFIX.length();

  private static final Map<String, String> paginationCommentMap = new HashMap<>();

  static {
    paginationCommentMap.put("pageList", "实体对象列表");
    paginationCommentMap.put("pageSize", "每页记录数");
    paginationCommentMap.put("pageNo", "当前页码，从1开始");
    paginationCommentMap.put("totalCount", "总记录数");
    paginationCommentMap.put("totalPage", "总页数");
    paginationCommentMap.put("nextPage", "下一页页码");
    paginationCommentMap.put("prePage", "上一页页码");
    paginationCommentMap.put("firstPage", "是否第一页");
    paginationCommentMap.put("lastPage", "是否最后一页");
  }

  private ClassUtils() {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * 获取特定字段的值
   * 
   * @param obj instance
   * @param fieldName 字段名
   * @since 2016年1月16日
   */
  @SuppressWarnings("unchecked")
  public static <T> T getFieldValue(Object obj, String fieldName) {
    if (fieldName == null || obj == null) {
      return null;
    }
    try {
      Class<?> clazz = obj.getClass();
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (T) field.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取classdoc里的所有符合JAVA BEAN规范的字段（只要包含Getter)。<br/>
   * 我们不根据字段查找，依据getter/is来查找,getter必须是public的<br/>
   * 返回类型，请不要使用复杂类型。目前只解析基本类型、泛型、数组(一维)、通配符指定上下界、类型变量指明bound的类型 <br/>
   * 但我们可指定当遇到泛型参数时，应该解析成什么类型(providedActualType)
   * 
   * @param fieldName
   * @return never null
   * @since 2016年1月16日
   */
  public static List<FieldInfo> getFieldInfos(ClassDoc classDoc, String providedActualType) {
    if (classDoc == null) {
      return Collections.<FieldInfo>emptyList();
    }

    List<FieldInfo> fieldInfos = new ArrayList<>();

    // 首先判断是否是枚举
    if (classDoc.isEnum()) {
      FieldDoc[] enums = classDoc.enumConstants();
      if (enums != null && enums.length > 0) {
        for (FieldDoc fieldDoc : enums) {
          FieldInfo fieldInfo = new FieldInfo();
          TypeInfo type = new TypeInfo();
          type.setActualType(fieldDoc.type().qualifiedTypeName());
          type.setEnum(true);
          type.setContainerType(fieldDoc.type().qualifiedTypeName());

          fieldInfo.setDeclaringClass(classDoc.qualifiedName());
          fieldInfo.setComment(StringUtils.trim(fieldDoc.commentText()));
          fieldInfo.setType(type);
          fieldInfo.setName(fieldDoc.name());
          fieldInfos.add(fieldInfo);
        }
      }
      return fieldInfos;
    }
    // filter method ,only public/protected
    MethodDoc[] methodDocs = classDoc.methods();
    if (methodDocs == null || methodDocs.length == 0) {
      return fieldInfos;
    }
    // do not filter field
    Map<String, String> fieldCommentMap = new HashMap<>();
    for (FieldDoc fd : classDoc.fields(false)) {
      if (fd.isStatic()) {
        continue;
      }
      fieldCommentMap.put(fd.name(), fd.commentText());
    }
    // public getter
    for (MethodDoc methodDoc : methodDocs) {
      String methodName = methodDoc.name();
      Type returnType = methodDoc.returnType();

      // 只查找getter
      if (!methodDoc.isPublic()
          || (!methodName.startsWith(GETTER_PREFIX) && !methodName.startsWith(IS_PREFIX))
          || methodDoc.isStatic() || methodDoc.parameters().length > 0
          || void.class.getName().equals(returnType.qualifiedTypeName())) {
        continue;
      }
      // boolean才生成is前缀，Boolean对应getter
      if (methodName.startsWith(IS_PREFIX)
          && !boolean.class.getName().equals(returnType.qualifiedTypeName())) {
        // 返回类型不是boolean
        continue;
      }

      FieldInfo fieldInfo = new FieldInfo();
      TypeInfo type = getTypeInfo(returnType, providedActualType);
      if (type == null) {
        continue;
      }
      fieldInfo.setType(type);

      fieldInfo.setDeclaringClass(methodDoc.containingClass().qualifiedName());
      if (methodName.startsWith(GETTER_PREFIX)) {
        fieldInfo.setName(Introspector.decapitalize(methodName.substring(GETTER_PREFIX_LEN)));
      } else if (methodName.startsWith(IS_PREFIX)) {
        // boolean 字段
        fieldInfo.setName(Introspector.decapitalize(methodName.substring(IS_PREFIX_LEN)));
      }
      // 注释优先取方法上的注释，其次是字段上的
      String comment = StringUtils.trim(methodDoc.commentText());
      if (StringUtils.isNullOrEmpty(comment)) {
        // 字段上的
        comment = StringUtils.trim(fieldCommentMap.get(fieldInfo.getName()));
      }
      // 还是没找到，则返回getter的@return 注释
      if (StringUtils.isNullOrEmpty(comment)) {
        Tag[] returnTags = methodDoc.tags("return");
        if (returnTags != null && returnTags.length > 0) {
          comment = returnTags[0].text();
        }
      }

      // 如果有枚举，将枚举值串起来生成注释
      fieldInfo.setComment(commentWithEnumIfAny(comment, returnType.asClassDoc()));

      fieldInfos.add(fieldInfo);
    }
    return fieldInfos;
  }

  /**
   * 生成枚举注释，never null，不是枚举 返回"";
   */
  public static String commentWithEnumIfAny(String originalComment, ClassDoc fieldTypeDoc) {
    // 如果是枚举，我们把所有枚举值拼成注释字符串
    StringBuilder enumCommentBuilder = new StringBuilder(StringUtils.trim(originalComment));
    if (fieldTypeDoc == null) {
      return enumCommentBuilder.toString();
    }
    if (fieldTypeDoc != null && fieldTypeDoc.isEnum()) {
      List<FieldInfo> enumFields = ClassUtils.getFieldInfos(fieldTypeDoc, null);
      if (enumFields != null && !enumFields.isEmpty()) {
        if (enumCommentBuilder.length() > 0) {
          enumCommentBuilder.append("</br>");
        }
        enumCommentBuilder.append(fieldTypeDoc.simpleTypeName() + "是枚举类型，可选值：");
        for (FieldInfo enumField : enumFields) {
          enumCommentBuilder.append(enumField.getName());
          String enumComment = StringUtils.trim(enumField.getComment());
          if (!StringUtils.isNullOrEmpty(enumComment)) {
            enumCommentBuilder.append("(" + enumComment + ")");
          }
          enumCommentBuilder.append(",");
        }
        enumCommentBuilder.deleteCharAt(enumCommentBuilder.length() - 1);
      }
    }
    return enumCommentBuilder.toString();
  }

  /**
   * 获取类型信息
   * 
   * @author huisman
   */
  public static TypeInfo getTypeInfo(Type elementType, String providedActualType) {
    if (elementType == null) {
      return null;
    }
    TypeInfo type = new TypeInfo();
    // null，说明不是泛型，检查是否是数组
    String dimension = elementType.dimension();
    if (!StringUtils.isNullOrEmpty(dimension) && dimension.indexOf(Types.DIMENTION_STR) >= 0) {
      // ArrayType，javadoc没有此类型，需要判断dimension信息
      type.setArray(true);
      type.setActualType(elementType.qualifiedTypeName());
      type.setContainerType(elementType.qualifiedTypeName());
      ClassDoc cdoc = elementType.asClassDoc();
      if (cdoc != null) {
        type.setEnum(cdoc.isEnum());
      }
    } else if (elementType instanceof ClassDoc) {
      // 类型是java 类
      // 简单类型，设置字段类型
      ClassDoc cd = elementType.asClassDoc();
      type.setActualType(cd.qualifiedTypeName());
      type.setContainerType(cd.qualifiedTypeName());
      type.setEnum(cd.isEnum());
    } else if (elementType.isPrimitive()) {
      // 原始类型 int,char
      type.setActualType(elementType.qualifiedTypeName());
      type.setContainerType(elementType.qualifiedTypeName());
    } else if (elementType instanceof ParameterizedType) {
      // 特殊处理ResponseEntity
      // 泛型
      ParameterizedType pt = elementType.asParameterizedType();
      String ptTypeName = pt.qualifiedTypeName();

      if (Types.isCollectionType(elementType.qualifiedTypeName())) {
        type.setCollection(true);
      } else if (Types.isMap(elementType.qualifiedTypeName())) {
        type.setMap(true);
      }
      // 有泛型信息,如果是ListView，则转成List.class
      if (SpiView.LIST_VIEW.equals(ptTypeName)) {
        type.setContainerType(List.class.getName());
      } else {
        type.setContainerType(ptTypeName);
      }

      // 解析实际类型
      Type[] actualTypes = pt.typeArguments();
      // 如果是map的话有两个参数,其他泛型只支持一个参数
      if (actualTypes == null || (type.isMap() && actualTypes.length != 2)
          || (!type.isMap() && actualTypes.length != 1)) {
        // 解析泛型，只解析一层。
        return null;
      }
      if (type.isMap()) {
        // 如果是map，则有两个泛型参数，我们仅支持简单类型
        if (actualTypes[0] instanceof ClassDoc
            && Types.isSimpleType(actualTypes[0].qualifiedTypeName())) {
          type.setMapKeyType(actualTypes[0].qualifiedTypeName());
          // ?递归？
          type.setMapValueType(getTypeInfo(actualTypes[1], providedActualType));
        } else {
          return null;
        }
      } else {
        // 其他的参数,只取第一个泛型参数
        ClassDoc actualClass = findAnyFirstClassDoc(actualTypes[0]);
        if (actualClass == null) {
          // 无法解析实际类型，如果没有提供实际类型，则返回null
          if (StringUtils.isNullOrEmpty(providedActualType)) {
            return null;
          }
          type.setActualType(providedActualType);
        } else {
          type.setActualType(actualClass.qualifiedTypeName());
        }

        // 如果是BEAN VIEW 或者ReponseEntity
        if (SpiView.BEAN_VIEW.equals(ptTypeName)) {
          // BeanView实际类型是泛型参数
          type.setContainerType(
              actualClass == null ? providedActualType : actualClass.qualifiedTypeName());
        } else if (ResponseEntity.class.getName().equals(ptTypeName)) {
          // 如果是ResponseEntity，根据实际参数判断是否是集合类型
          type.setCollection(Types.isCollectionType(actualTypes[0].qualifiedTypeName()));
          type.setContainerType(actualTypes[0].qualifiedTypeName());
        }
      }
    }
    return type;
  }

  /**
   * 将返回类型转换为我们规定的类型：ReturyType。</br>
   * 暂时不支持返回Annotaiton，TypeVariable,比如List<T>，以及WildcardType，比如List<? extends Model>，List<? super
   * Model>
   */
  public static ReturnType toReturnType(Type type) {
    // 解析返回类型，可能重新封装成Class,或者ParameterizedType
    ReturnType returnType = new ReturnType();
    TypeInfo typeInfo = getTypeInfo(type, null);
    returnType.setType(typeInfo);
    if (typeInfo == null) {
      return returnType;
    }
    if (typeInfo.isArray() || typeInfo.isEnum() || type instanceof ClassDoc) {
      // 是数组 or 普通 java class类型
      ClassDoc classDoc = type.asClassDoc();

      if (Types.isSimpleType(typeInfo.getActualType()) || typeInfo.isEnum()
          || Types.isWebType(typeInfo.getActualType())) {
        // 如果是简单类型或web servlet api，不需要指定字段
        return returnType;
      }
      // 主动解析实际类型，递归调用父类
      List<FieldInfo> fieldInfos = new ArrayList<>();
      fieldInfos.addAll(ClassUtils.getFieldInfos(classDoc, typeInfo.getActualType()));
      ClassDoc superDoc = classDoc.superclass();
      while (superDoc != null && !superDoc.qualifiedTypeName().equals(Object.class.getName())) {
        fieldInfos.addAll(ClassUtils.getFieldInfos(superDoc, typeInfo.getActualType()));
        superDoc = superDoc.superclass();
      }
      returnType.setFields(fieldInfos);
      return returnType;
    } else if (type.isPrimitive()) {
      // 原始简单类型
      return returnType;
    } else if (type instanceof com.sun.javadoc.ParameterizedType) {
      // 接下来判断是否是泛型参数，比如：List<Model>,ListView<Model>,BeanView<Model>，Set<Model>等
      // 但是不支持TypeVariable,比如List<T>，以及WildcardType，比如List<? extends Model>，List<? super Model>
      ParameterizedType parameterizedType = type.asParameterizedType();
      // 实际参数，仅限一个/两个参数
      Type[] actualTypes = parameterizedType.typeArguments();
      // 实际的类型
      ClassDoc actualClass = null;
      if (typeInfo.isMap() && actualTypes.length == 2) {
        // map的话，取第二个
        actualClass = findAnyFirstClassDoc(actualTypes[1]);
      } else {
        // 实际的类型
        actualClass = findAnyFirstClassDoc(actualTypes[0]);
      }

      // 如果是Spi view/集合、数组、枚举，则返回实际类型的字段
      if (actualClass != null) {
        if (Types.isSpiViewType(type.qualifiedTypeName()) || typeInfo.isArray()
            || typeInfo.isCollection() || typeInfo.isMap() || typeInfo.isEnum()) {
          // 不是简单类型
          if (!Types.isSimpleType(typeInfo.getActualType())) {
            // 解析实际类型的字段
            returnType.setFields(ClassUtils.getFieldInfos(actualClass, typeInfo.getActualType()));
          }
        } else {
          List<FieldInfo> fieldInfos =
              ClassUtils.getFieldInfos(parameterizedType.asClassDoc(), typeInfo.getActualType());
          // 。。。分页。。。需要特殊处理
          if (parameterizedType.qualifiedTypeName().equals(Pagination.class.getName())) {
            postProcessPaginationType(fieldInfos);
          }
          returnType.setFields(fieldInfos);
        }

      }

      return returnType;
    }
    throw new UnsupportedOperationException("invalid type:" + type.qualifiedTypeName()
        + ", only support ParameterizedType&Class&Primitive Type Array 。");
  }

  /**
   * 如果是Pagination类型则后处理
   */
  private static void postProcessPaginationType(List<FieldInfo> fieldInfos) {
    if (fieldInfos == null || fieldInfos.size() < 1) {
      return;
    }
    for (FieldInfo fd : fieldInfos) {
      String comment = paginationCommentMap.get(fd.getName());
      if (StringUtils.isNullOrEmpty(comment)) {
        continue;
      }
      if (StringUtils.isNullOrEmpty(fd.getComment())) {
        fd.setComment(comment);
      }
    }
  }

  /**
   * 查找类型所代表的ClassDoc，如果类型是ClassDoc则直接返回，如果是泛型，我们只检查第一个泛型参数
   */
  private static ClassDoc findAnyFirstClassDoc(Type type) {
    if (type == null) {
      return null;
    }
    if (type instanceof ClassDoc) {
      // class 直接返回
      return type.asClassDoc();
    } else if (type instanceof ParameterizedType) {
      // 泛型参数
      ParameterizedType pt = type.asParameterizedType();
      Type[] pts = pt.typeArguments();
      if (pts != null && pts.length > 0) {
        return findAnyFirstClassDoc(pts[0]);
      }
    } else if (type instanceof TypeVariable) {
      // 类型变量
      TypeVariable tv = type.asTypeVariable();
      Type[] tb = tv.bounds();
      if (tb != null && tb.length > 0 && tb[0] instanceof ClassDoc) {
        return tb[0].asClassDoc();
      }
    } else if (type instanceof WildcardType) {
      // 通配符类型
      WildcardType wt = type.asWildcardType();
      Type[] st = wt.superBounds();
      Type[] et = wt.extendsBounds();
      if (st != null && st.length > 0 && st[0] instanceof ClassDoc) {
        return st[0].asClassDoc();
      } else if (et != null && et.length > 0 && et[0] instanceof ClassDoc) {
        return et[0].asClassDoc();
      }
    }
    return null;
  }
}
