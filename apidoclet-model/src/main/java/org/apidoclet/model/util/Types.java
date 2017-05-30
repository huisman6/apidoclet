package org.apidoclet.model.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apidoclet.model.util.spi.TypesExtension;
import org.apidoclet.model.util.spi.TypesExtension.SimpleTypeMapping;



/**
 * deduce class type (collection? map ? class exists ? simple type instead of complex java bean? etc) by class name
 */
public final class Types {
  /**
   * 维度信息，默认为[]。比如：[][]就是两维数组的意思,[]一维数组
   */
  public static final String DIMENTION_STR = "[]";
  // 默认简单类型
  private static final List<SimpleTypeMapping> DEFAULT_SIMPLE_TYPE_MAPPINGS =
      Arrays
          .asList(
              new SimpleTypeMapping(int.class.getName(), 0),
              new SimpleTypeMapping(Integer.class.getName(), Integer.valueOf(0)),
              new SimpleTypeMapping(byte.class.getName(), 0),
              new SimpleTypeMapping(Byte.class.getName(), Byte
                  .valueOf((byte) 0)),
              new SimpleTypeMapping(short.class.getName(), 0),
              new SimpleTypeMapping(Short.class.getName(), Short
                  .valueOf((short) 0)),
              new SimpleTypeMapping(float.class.getName(), 0.004),
              new SimpleTypeMapping(Float.class.getName(), Float
                  .valueOf(0.003F)),
              new SimpleTypeMapping(double.class.getName(), 0.002D),
              new SimpleTypeMapping(Double.class.getName(), Double
                  .valueOf(0.001D)),
              new SimpleTypeMapping(long.class.getName(), 0),
              new SimpleTypeMapping(Long.class.getName(), Long.valueOf(0)),
              new SimpleTypeMapping(boolean.class.getName(), false),
              new SimpleTypeMapping(Boolean.class.getName(), false),
              new SimpleTypeMapping(char.class.getName(), 'a'),
              new SimpleTypeMapping(Character.class.getName(), 'a'),
              new SimpleTypeMapping(Date.class.getName(), System
                  .currentTimeMillis()),
              new SimpleTypeMapping(java.util.Date.class.getName(), System
                  .currentTimeMillis()),
              new SimpleTypeMapping(String.class.getName(), "\"字符串\""),
              new SimpleTypeMapping(StringBuffer.class.getName(),
                  new StringBuffer("\"字符串\"")),
              new SimpleTypeMapping(StringBuilder.class.getName(),
                  new StringBuilder("\"字符串\"")),
              new SimpleTypeMapping(BigDecimal.class.getName(), new BigDecimal(
                  "0.0")),
              new SimpleTypeMapping(BigInteger.class.getName(), new BigInteger(
                  "0")),
              new SimpleTypeMapping(void.class.getName(), null),
              new SimpleTypeMapping(Class.class.getName(), Class.class
                  .getName()), new SimpleTypeMapping(Object.class.getName(),
                  null));
  /**
   * 简单类型及其默认值，包括基本数据类型及其封装类，日期字符串等
   */
  private static final Map<String, Object> SIMPLE_CLASS_TYPES = new HashMap<>();
  /**
   * 表示一个类型是集合类型的，包括数组，Set,List,ArrayList,LinkList,Collection
   */
  private static final Set<String> COLLECTION_TYPES = new HashSet<>(
      Arrays.asList(List.class.getName(), Set.class.getName(),
          Collection.class.getName(), AbstractCollection.class.getName()));

  static {
    // 设置简单类型及其默认值
    for (SimpleTypeMapping simpleType : DEFAULT_SIMPLE_TYPE_MAPPINGS) {
      addSimpleTypeValueMapping(simpleType.getQulifiedTypeName(),
          simpleType.getDefaultValue());
    }
    List<TypesExtension> typesExtensions =
        ServiceLoaderUtils.getServicesOrNull(TypesExtension.class);
    if (typesExtensions != null) {
      // 获取所有的类型扩展，覆盖已有的配置
      for (TypesExtension extension : typesExtensions) {
        Set<String> additionalCollectionTypes =
            extension.getAdditionalCollectionTypes();
        if (additionalCollectionTypes != null) {
          COLLECTION_TYPES.addAll(additionalCollectionTypes);
        }

        List<SimpleTypeMapping> additionalSimpleTypes =
            extension.getAdditionalSimpleTypeMapping();
        if (additionalSimpleTypes != null) {
          for (SimpleTypeMapping st : additionalSimpleTypes) {
            addSimpleTypeValueMapping(st.getQulifiedTypeName(),
                st.getDefaultValue());
          }
        }
      }

    }
  }

  private Types() {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * 添加映射信息
   * 
   * @author huisman
   */
  private static void addSimpleTypeValueMapping(String qualifiedType,
      Object defaultValue) {
    SIMPLE_CLASS_TYPES.put(qualifiedType, defaultValue);
  }


  /**
   * 判断给定的类型名称是否是简单类型
   * 
   * @param type
   */
  public static boolean isSimpleType(String type) {
    Class<?> clazz = getIfExists(type);
    if (clazz == null) {
      return SIMPLE_CLASS_TYPES.containsKey(type);
    }
    return isAssignablePrimitive(clazz);
  }

  private static boolean isAssignablePrimitive(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return true;
    }
    Class<?>[] simpleTypes =
        new Class<?>[] {Double.class, Float.class, Byte.class, Character.class,
            Integer.class, Boolean.class, Short.class, Long.class,
            String.class, StringBuffer.class, StringBuilder.class,
            java.util.Date.class, Date.class, Calendar.class, BigDecimal.class,
            BigInteger.class, Void.class};
    for (Class<?> sz : simpleTypes) {
      if (sz == clazz) {
        return true;
      }
      if (sz.isAssignableFrom(clazz)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取简单类型的默认值
   * 
   * @param type
   * @since 2016年1月16日
   */
  public static Object getSimpleTypeDefaultValue(String type) {
    return SIMPLE_CLASS_TYPES.get(type);
  }

  /**
   * 判断类型是否是集合类型，先试图加载一次，如果加载不到类，则根据类名判断
   */
  public static boolean isCollectionType(String qualifiedTypeName) {
    Class<?> clazz = getIfExists(qualifiedTypeName);
    if (clazz == null) {
      // 不在classpath路径上
      return COLLECTION_TYPES.contains(qualifiedTypeName);
    }
    if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
      return true;
    }
    return COLLECTION_TYPES.contains(qualifiedTypeName);
  }

  /**
   * 检查类型否在当前classpath上
   */
  public static boolean isPresent(String type) {
    try {
      Class.forName(type);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * 如果类型存在，则返回，否则返回null
   */
  public static Class<?> getIfExists(String type) {
    try {
      return Class.forName(type);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * 类型是否是map
   */
  public static boolean isMap(String type) {
    Class<?> clazz = getIfExists(type);
    if (clazz == null) {
      return false;
    }
    if (Map.class.isAssignableFrom(clazz)) {
      return true;
    }
    return false;
  }

  /**
   * 获取简单类型，类似 Class.getSimpleName(); null return "".
   */
  public static String getSimpleTypeName(String type) {
    if (type == null) {
      return "";
    }
    int lastDot = type.lastIndexOf(".");
    if (lastDot >= 0 && type.length() > 1) {
      return type.substring(lastDot + 1);
    }
    return type;
  }

}
