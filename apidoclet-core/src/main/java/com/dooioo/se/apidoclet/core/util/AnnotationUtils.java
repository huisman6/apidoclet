package com.dooioo.se.apidoclet.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dooioo.se.apidoclet.model.JavaAnnotations.AnnotationValue;
import com.dooioo.se.apidoclet.model.util.Types;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Type;

/**
 * @author huisman
 * @version 1.0.0
 * @since 2016年1月13日 Copyright (c) 2016, BookDao All Rights Reserved.
 */
public final class AnnotationUtils {
  private AnnotationUtils() {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * 从方法的标注中查找指定名称的标注，并返回所有可用属性值，包括默认值。<br>
   * 使用HashMap存放key/value，key为标注属性名，value为AnnotationValue#getValue，key重复就覆盖<br>
   * 请注意，标注里的值（AnnotationValue#getValue()):
   * <ul>
   * <li>a wrapper class for a primitive type 属性为基本类型</li>
   * <li>String 属性为字符串</li>
   * <li>Type (representing a class literal) 属性为类型 Class</li>
   * <li>FieldDoc (representing an enum constant) 属性为枚举常量</li>
   * <li>AnnotationDesc 属性为注解</li>
   * <li>AnnotationValue[] 属性值为数组值，任何类型的数组，比如：String[]</li>
   * </ul>
   * 
   * @author huisman
   * @param annotationDescs 待筛选的所有标注
   * @param searchedAnnotation 完全限定名称，不能是简单类名
   * @return never null ,empty if not found
   * @since 2016年1月13日
   */
  public static Map<String, AnnotationValue> attributesFor(
      AnnotationDesc[] annotationDescs, String searchedAnnotation) {
    if (annotationDescs != null && annotationDescs.length > 0) {
      // search
      for (AnnotationDesc annotationDesc : annotationDescs) {
        if (annotationDesc.annotationType().qualifiedTypeName()
            .equals(searchedAnnotation)) {
          // finally ,found
          return attributesFor(annotationDesc);
        }
      }
    }
    return Collections.emptyMap();
  }

  /**
   * 搜索单个标注所有属性，但只返回被客户端设置了的属性，默认值不返回。 <br>
   * 使用HashMap存放key/value，key为标注属性名，value为AnnotationValue#getValue，key重复就覆盖<br>
   * 请注意，标注里的值（AnnotationValue#getValue()):
   * <ul>
   * <li>a wrapper class for a primitive type 属性为基本类型</li>
   * <li>String 属性为字符串</li>
   * <li>Type (representing a class literal) 属性为类型 Class，我们返回Class的类名称</li>
   * <li>FieldDoc (representing an enum constant) 属性为枚举常量，我们返回实际类型</li>
   * <li>AnnotationDesc 属性为标注，我们返回注解的类名</li>
   * <li>AnnotationValue[] 属性值为数组值，任何类型的数组，比如：String[]</li>
   * </ul>
   * 
   * @author huisman
   */
  public static Map<String, AnnotationValue> attributesFor(
      AnnotationDesc annotationDesc) {
    Map<String, AnnotationValue> attributes = new HashMap<String,AnnotationValue>();
    if (annotationDesc != null) {
      ElementValuePair[] pairs = annotationDesc.elementValues();
      if (pairs != null && pairs.length > 0) {
        for (ElementValuePair ev : pairs) {
          // 设置默认值
          attributes.put(ev.element().name(), new AnnotationValue(
              deduceAnnotationValue(ev.value()), deduceAnnotationValue(ev
                  .element().defaultValue())));
        }
      }
      return attributes;
    }
    return attributes;
  }

  /**
   * 注意此方法如果注解的属性值为注解或者Class时，我们返回类型名。 <br/>
   * 如果是常量或者字符串以及简单类型，则返回本身；<br/>
   * 如果注解属性为数组，则返回数组，但是数据类型为上述的类型。
   * 
   * @author huisman
   * @see http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html
   */
  private static Object deduceAnnotationValue(
      com.sun.javadoc.AnnotationValue annotationValue) {
    if (annotationValue == null) {
      return null;
    }
    Object value = annotationValue.value();
    if (value == null || value instanceof String
        || Types.isSimpleType(value.getClass().getName())) {
      // 简单类型，直接返回
      return value;
    } else if (value instanceof FieldDoc) {
      // 枚举常量
      FieldDoc enumConstant = FieldDoc.class.cast(value);
      //枚举字段，直接返回字段名称即可
      return enumConstant.name();
    } else if (value instanceof Type) {
      // Class类型，直接返回类全名
      return Type.class.cast(value).qualifiedTypeName();
    } else if (value instanceof AnnotationDesc) {
      // 注解，要不要转换为JavaAnnotation?
      AnnotationDesc ad = AnnotationDesc.class.cast(value);
      return ad.annotationType().qualifiedTypeName();
    } else if (value instanceof com.sun.javadoc.AnnotationValue[]) {
      // annotationValue类型，数组类型
      com.sun.javadoc.AnnotationValue[] jas =
          (com.sun.javadoc.AnnotationValue[]) value;
      List<Object> values = new ArrayList<Object>();
      for (com.sun.javadoc.AnnotationValue jav : jas) {
        values.add(deduceAnnotationValue(jav));
      }
      return values;
    }
    return value;
  }

  /**
   * 检查特定类型的注解是否存在。
   * 
   * @author huisman
   * @param annotationDescs
   * @param searchedAnnotation
   */
  public static boolean isPresent(AnnotationDesc[] annotationDescs,
      String searchedAnnotation) {
    return searchFirst(annotationDescs, searchedAnnotation) != null;
  }

  /**
   * 搜索最优先出现的标注，不存在则返回null
   * 
   * @author huisman
   * @param annotationDescs 待筛选的所有标注
   * @param searchedAnnotation 标注类全称
   * @since 2016年1月13日
   */
  public static AnnotationDesc searchFirst(AnnotationDesc[] annotationDescs,
      String searchedAnnotation) {
    if (annotationDescs == null || annotationDescs.length == 0) {
      return null;
    }

    for (AnnotationDesc annotationDesc : annotationDescs) {
      if (annotationDesc.annotationType().qualifiedTypeName()
          .equals(searchedAnnotation)) {
        return annotationDesc;
      }
    }
    return null;
  }
}
