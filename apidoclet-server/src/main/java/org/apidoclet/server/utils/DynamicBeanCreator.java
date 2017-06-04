package org.apidoclet.server.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apidoclet.model.FieldInfo;
import org.apidoclet.model.TypeInfo;
import org.apidoclet.model.util.Types;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamicBeanCreator {
  public static Object instantiate(final TypeInfo returnType) {
    if (returnType.isVoid()) {
      return null;
    }
    // consider Map?
    if (returnType.isMap()) {
      Map<Object, Object> map = new HashMap<>();
      map.put(Types.getSimpleTypeDefaultValue(returnType.getMapKeyType()),
          instantiate(returnType.getMapValueType()));
      return map;
    } else {
      // simple type or JDK class
      if (Types.isJDKType(returnType.getActualType())
          || Types.isSimpleType(returnType.getActualType())) {
        Object value =
            Types.getSimpleTypeDefaultValue(returnType.getActualType());
        if (value == null) {
          // not simple type
          try {
            value =
                BeanUtils
                    .instantiate(Class.forName(returnType.getActualType()));
          } catch (Exception e) {
          }
        }
        if (returnType.getActualType().equals(returnType.getContainerType())) {
          return value;
        } else {
          // array or collection
          if (returnType.isArray() || returnType.isCollection()) {
            return Arrays.asList(value);
          } else {
            return value;
          }
        }
      }
      try {
        // 复杂类型
        final BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setNamingPolicy(new NamingPolicy() {
          @Override
          public String getClassName(final String prefix, final String source,
              final Object key, final Predicate names) {
            return returnType.getActualType();
          }
        });
        List<FieldInfo> fields = returnType.getFields();
        Map<String, Object> fieldValueMap = new HashMap<>();
        if (fields != null && fields.size() > 0) {
          for (FieldInfo fieldInfo : fields) {
            // recursion
            Object fieldValue = instantiate(fieldInfo.getType());
            // property prefix
            fieldValueMap.put("$cglib_prop_" + fieldInfo.getName(), fieldValue);
            beanGenerator.addProperty(fieldInfo.getName(),
                fieldValue.getClass());
          }
        }
        Object returnValue = beanGenerator.create();
        for (Field addedField : returnValue.getClass().getDeclaredFields()) {
          if (fieldValueMap.containsKey(addedField.getName())) {
            addedField.setAccessible(true);
            addedField
                .set(returnValue, fieldValueMap.get(addedField.getName()));
          }
        }
        if (returnType.isArray() || returnType.isCollection()) {
          return Arrays.asList(returnValue);
        }
        return returnValue;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  }

  public static void main(String[] args) {
    TypeInfo returnType = new TypeInfo();
    returnType.setActualType("org.apidoclet.test.model.User");
    returnType.setContainerType("org.apidoclet.test.model.User");
    // field 1
    FieldInfo field = new FieldInfo();
    field.setName("userName");
    TypeInfo fieldType = new TypeInfo();
    fieldType.setActualType(String.class.getName());
    fieldType.setContainerType(String.class.getName());
    field.setType(fieldType);

    // field2 --include nested fields
    FieldInfo field2 = new FieldInfo();
    field2.setName("userGroup");
    TypeInfo field2Type = new TypeInfo();
    field2Type.setActualType("org.apidoclet.test.model.UserGroup");
    field2Type.setContainerType(List.class.getName());
    field2Type.setCollection(true);
    field2.setType(field2Type);
    // field2's nested fields
    FieldInfo nestedField = new FieldInfo();
    nestedField.setName("group");
    TypeInfo nestedFieldType = new TypeInfo();
    nestedFieldType.setActualType("java.lang.String");
    nestedFieldType.setContainerType(List.class.getName());
    nestedFieldType.setArray(true);
    nestedField.setType(nestedFieldType);
    field2Type.setFields(Arrays.asList(nestedField));


    // return type all fields
    returnType.setFields(Arrays.asList(field, field2));

    ObjectMapper objectMapper = new ObjectMapper();
    Object value = instantiate(returnType);
    try {
      System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(value));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
