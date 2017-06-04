package org.apidoclet.model.util;

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

import org.apidoclet.model.util.spi.TypesExtension;
import org.apidoclet.model.util.spi.TypesExtension.SimpleTypeMapping;



/**
 * deduce class type (collection? map ? class exists ? simple type instead of complex java bean?
 * etc) by class name
 */
public final class Types {
  /**
   * array dimension info,default:[]
   * 
   * e.g：[][] means two-dimension array and [] means one-dimension array
   */
  public static final String DIMENTION_STR = "[]";
  /**
   * register simple type and its default value,a simple type does't need to parse fields
   */
  private static final List<SimpeType> DEFAULT_SIMPLE_TYPES = Arrays.asList(
      new SimpeType(int.class, 0),
      new SimpeType(Integer.class, Integer.valueOf(0)), new SimpeType(
          byte.class, 0), new SimpeType(Byte.class, Byte.valueOf((byte) 0)),
      new SimpeType(short.class, 0),
      new SimpeType(Short.class, Short.valueOf((short) 0)), new SimpeType(
          float.class, 0.002),
      new SimpeType(Float.class, Float.valueOf(0.004F)), new SimpeType(
          double.class, 0.006D),
      new SimpeType(Double.class, Double.valueOf(0.008D)), new SimpeType(
          long.class, 0), new SimpeType(Long.class, Long.valueOf(0)),
      new SimpeType(boolean.class, false), new SimpeType(Boolean.class, false),
      new SimpeType(char.class, 'a'), new SimpeType(Character.class, 'a'),
      new SimpeType(Date.class, System.currentTimeMillis()), new SimpeType(
          java.util.Date.class, System.currentTimeMillis()), new SimpeType(
          String.class, "String"), new SimpeType(StringBuffer.class,
          new StringBuffer("String")), new SimpeType(StringBuilder.class,
          new StringBuilder("String")), new SimpeType(CharSequence.class,
          "String"), new SimpeType(BigDecimal.class, new BigDecimal("0.0")),
      new SimpeType(BigInteger.class, new BigInteger("0")), new SimpeType(
          Class.class, Class.class.getName()));

  static class SimpeType {
    private Class<?> type;
    private Object defaultValue;

    public SimpeType(Class<?> type, Object defaultValue) {
      this.type = type;
      this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
      return type;
    }

    public Object getDefaultValue() {
      return defaultValue;
    }
  }

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
    for (SimpeType simpleType : DEFAULT_SIMPLE_TYPES) {
      addSimpleTypeValueMapping(simpleType.getType().getName(),
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
   * test whether the given type is a simple type, usually, a simple type doesn't need to parse
   * fields
   * 
   * @param type full-qualified class name
   */
  public static boolean isSimpleType(String type) {
    if (type == null) {
      return false;
    }
    Class<?> clazz = getIfExists(type);
    if (clazz == null) {
      return SIMPLE_CLASS_TYPES.containsKey(type);
    }
    return getDefaultSimpleType(clazz) != null;
  }

  /**
   * Check whether this {@code qualifiedClassName} is a JavaBean.</p>
   * 
   * Any {@code qualifiedClassName} could be a JavaBean,except:</p>
   *  
   *   1, {@link #isJDKType(String)} return true </p>
   *   
   *   2,{@link #isSimpleType(String)} return true
   */
  public static boolean isJavaBean(String qualifiedClassName) {
    if (qualifiedClassName == null) {
      return false;
    }
    if (isJDKType(qualifiedClassName) || isSimpleType(qualifiedClassName)) {
      return false;
    }
    return true;
  }

  /**
   * check whether {@code qualifiedClassName} is provided by JDK
   */
  public static boolean isJDKType(String qualifiedClassName) {
    if (qualifiedClassName == null) {
      return false;
    }
    return qualifiedClassName.startsWith("java.");
  }

  /**
   * test whether the given class is a sub class of JDK class(Number,String,Date,Calendar)
   * 
   * return null indicates it isn't a simple type
   */
  private static Class<?> getDefaultSimpleType(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return clazz;
    }
    for (SimpeType defaultSimpleType : DEFAULT_SIMPLE_TYPES) {
      if (defaultSimpleType.getType() == clazz) {
        return clazz;
      }
      if (defaultSimpleType.getType().isAssignableFrom(clazz)) {
        return defaultSimpleType.getType();
      }
    }
    return null;
  }

  /**
   * get default value of the simple type, usually, use to create a JSON snippet
   * 
   * @param type full-qualified class name
   * @since 2016年1月16日
   */
  public static Object getSimpleTypeDefaultValue(String type) {
    Class<?> clazz = getIfExists(type);
    if (clazz != null) {
      Class<?> defaultType = getDefaultSimpleType(clazz);
      if (defaultType != null) {
        return SIMPLE_CLASS_TYPES.get(defaultType.getName());
      }
    }
    return SIMPLE_CLASS_TYPES.get(type);
  }

  /**
   * check whether {@code qualifiedTypeName} is a collection type
   */
  public static boolean isCollectionType(String qualifiedTypeName) {
    Class<?> clazz = getIfExists(qualifiedTypeName);
    if (clazz == null) {
      // classpath not found,try customized collection types
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
      if (type == null) {
        return null;
      }
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
    if (type == null) {
      return false;
    }
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
