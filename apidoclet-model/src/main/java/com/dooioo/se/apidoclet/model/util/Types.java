package com.dooioo.se.apidoclet.model.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 判断一些类是否集合类型或简单类型，最好判断之前先加载一下
 */
public final class Types {
  /**
   * 维度信息，默认为[]。比如：[][]就是两维数组的意思,[]一维数组
   */
  public static final String DIMENTION_STR = "[]";
  /**
   * 简单类型及其默认值，包括基本数据类型及其封装类，日期字符串等
   */
  private static final Map<String, Object> SIMPLE_CLASS_TYPES = new HashMap<>();
  /**
   * 表示一个类型是集合类型的，包括数组，Set,List,ArrayList,LinkList,Collection
   */
  private static final Set<String> COLLECTION_TYPES = new HashSet<>(Arrays.asList(
      List.class.getName(), Set.class.getName(), Collection.class.getName(),
      AbstractCollection.class.getName()));

  static {
    SIMPLE_CLASS_TYPES.put(int.class.getName(), 0);
    SIMPLE_CLASS_TYPES.put(Integer.class.getName(), Integer.valueOf(0));

    SIMPLE_CLASS_TYPES.put(byte.class.getName(), 0);
    SIMPLE_CLASS_TYPES.put(Byte.class.getName(), Byte.valueOf((byte) 0));

    SIMPLE_CLASS_TYPES.put(short.class.getName(), 0);
    SIMPLE_CLASS_TYPES.put(Short.class.getName(), Short.valueOf((short) 0));

    SIMPLE_CLASS_TYPES.put(float.class.getName(), 0.004);
    SIMPLE_CLASS_TYPES.put(Float.class.getName(), Float.valueOf(0.003F));

    SIMPLE_CLASS_TYPES.put(double.class.getName(), 0.002D);
    SIMPLE_CLASS_TYPES.put(Double.class.getName(), Double.valueOf(0.001D));

    SIMPLE_CLASS_TYPES.put(long.class.getName(), 0);
    SIMPLE_CLASS_TYPES.put(Long.class.getName(), Long.valueOf(0));

    SIMPLE_CLASS_TYPES.put(boolean.class.getName(), false);
    SIMPLE_CLASS_TYPES.put(Boolean.class.getName(), false);


    SIMPLE_CLASS_TYPES.put(char.class.getName(), 'a');
    SIMPLE_CLASS_TYPES.put(Character.class.getName(), 'a');

    SIMPLE_CLASS_TYPES.put(Date.class.getName(), System.currentTimeMillis());
    SIMPLE_CLASS_TYPES.put(java.util.Date.class.getName(), System.currentTimeMillis());

    SIMPLE_CLASS_TYPES.put(String.class.getName(), "\"字符串\"");
    SIMPLE_CLASS_TYPES.put(StringBuffer.class.getName(), new StringBuffer("\"字符串\""));
    SIMPLE_CLASS_TYPES.put(StringBuilder.class.getName(), new StringBuilder("\"字符串\""));

    SIMPLE_CLASS_TYPES.put(BigDecimal.class.getName(), new BigDecimal("0.0"));
    SIMPLE_CLASS_TYPES.put(BigInteger.class.getName(), new BigInteger("0"));

    SIMPLE_CLASS_TYPES.put(void.class.getName(), null);
    SIMPLE_CLASS_TYPES.put(Class.class.getName(), Class.class.getName());
  }

  private Types() {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * 判断给定的类型名称是否是简单类型
   * 
   * @param type
   */
  public static boolean isSimpleType(String type) {
    return SIMPLE_CLASS_TYPES.containsKey(type);
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
  public static boolean isCollectionType(String type) {
    Class<?> clazz = getIfExists(type);
    if (clazz == null) {
      return COLLECTION_TYPES.contains(type);
    }
    if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
      return true;
    }
    return COLLECTION_TYPES.contains(type);
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
