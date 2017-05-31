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
 * deduce class type (collection? map ? class exists ? simple type instead of complex java bean?
 * etc) by class name
 */
public final class Types {
  /**
   * array dimension info,default:[]. eg：[][] means two-dimension array and [] means one-dimension
   * array
   */
  public static final String DIMENTION_STR = "[]";
  /**
   * register simple type and its default value,a simple type does't need to parse fields
   */
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
              new SimpleTypeMapping(String.class.getName(), "\"String\""),
              new SimpleTypeMapping(StringBuffer.class.getName(),
                  new StringBuffer("\"String\"")),
              new SimpleTypeMapping(StringBuilder.class.getName(),
                  new StringBuilder("\"String\"")),
              new SimpleTypeMapping(BigDecimal.class.getName(), new BigDecimal(
                  "0.0")),
              new SimpleTypeMapping(BigInteger.class.getName(), new BigInteger(
                  "0")),
              new SimpleTypeMapping(void.class.getName(), null),
              new SimpleTypeMapping(Class.class.getName(), Class.class
                  .getName()), new SimpleTypeMapping(Object.class.getName(),
                  null));
  /**
   * simple type and its default value mapping
   */
  private static final Map<String, Object> SIMPLE_CLASS_TYPES = new HashMap<>();
  /**
   * collection type ,include array,Set,List,ArrayList,LinkList,Collection etc;
   */
  private static final Set<String> COLLECTION_TYPES = new HashSet<>(
      Arrays.asList(List.class.getName(), Set.class.getName(),
          Collection.class.getName(), AbstractCollection.class.getName()));

  static {
    // register default simple type
    for (SimpleTypeMapping simpleType : DEFAULT_SIMPLE_TYPE_MAPPINGS) {
      addSimpleTypeValueMapping(simpleType.getQulifiedTypeName(),
          simpleType.getDefaultValue());
    }
    // detect provided simple type and collection type by JAVA SPI mechanism
    List<TypesExtension> typesExtensions =
        ServiceLoaderUtils.getServicesOrNull(TypesExtension.class);
    if (typesExtensions != null) {
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
    throw new UnsupportedOperationException();
  }

  private static void addSimpleTypeValueMapping(String qualifiedType,
      Object defaultValue) {
    SIMPLE_CLASS_TYPES.put(qualifiedType, defaultValue);
  }


  /**
   * test whether the given type is simple type, usually, a simple type doesn't need to parse fields
   * 
   * @param type full-qualified class name
   */
  public static boolean isSimpleType(String type) {
    Class<?> clazz = getIfExists(type);
    if (clazz == null) {
      return SIMPLE_CLASS_TYPES.containsKey(type);
    }
    return isAssignablePrimitive(clazz);
  }

  /**
   * test whether the given class is a sub class of JDK class(Number,String,Date,Calendar)
   */
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
   * get default value of the simple type, usually, use to create a JSON snippet
   * 
   * @param type full-qualified class name
   * @since 2016年1月16日
   */
  public static Object getSimpleTypeDefaultValue(String type) {
    return SIMPLE_CLASS_TYPES.get(type);
  }

  /**
   * check whether {@code qualifiedTypeName} is a collection type
   */
  public static boolean isCollectionType(String qualifiedTypeName) {
    Class<?> clazz = getIfExists(qualifiedTypeName);
    if (clazz == null) {
      // absent in classpath
      return COLLECTION_TYPES.contains(qualifiedTypeName);
    }
    if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
      return true;
    }
    return COLLECTION_TYPES.contains(qualifiedTypeName);
  }

  /**
   * check whether {@code type} exists in classpath, use {@code Class#forName(String)}
   * 
   * @param type full-qualified class name
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
   * get the class object by its qualified name (use {@code Class#forName(String)}) if it exists in
   * classpath,otherwise return null
   * 
   * @param type full-qualified class name
   */
  public static Class<?> getIfExists(String type) {
    try {
      return Class.forName(type);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * check whether {@code type} is or a sub class of {@code java.util.Map}
   * 
   * @param type full-qualified class name
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
   * get the simple type name,similar to {@code Class.getSimpleName()}. if {@code type } is null,
   * return empty string("").
   * 
   * @param type full-qualified class name
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
