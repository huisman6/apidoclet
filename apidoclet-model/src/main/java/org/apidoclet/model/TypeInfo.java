package org.apidoclet.model;

import java.io.Serializable;
import java.util.List;

import org.apidoclet.model.util.Types;

/**
 * java type wrapper,如果是map的话，我们仅支持key为简单类型，如果是数组的话，仅支持一维数组
 * @author huisman
 */
public class TypeInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 是否是数组
   */
  private boolean isArray;
  /**
   * 是否是集合
   */
  private boolean isCollection;
  
  /**
   * 是否是void类型
   */
  private boolean isVoid;

  /**
   * 是否是枚举
   */
  private boolean isEnum;

  /**
   * 是否是map
   */
  private boolean isMap;

  /**
   * 字段的实际类型，如果是泛型，例如：List<Employee>，则值为Employee； Map<String,List<Employee>>，则值为employee；如果是普通类，则为类名
   */
  private String actualType;

  /**
   * {@link Class#getSimpleName()}
   */
  private String simpleActualType;

  /**
   * 如果是map的话，key的类型，我们只支持简单类型, 参考{@link Types#isSimpleType(String)}
   */
  private String mapKeyType;

  /**
   * 如果是map类型的话，value值的类型信息
   */
  private TypeInfo mapValueType;

  /**
   * 字段的容器类型，如果是泛型，例如：List<Employee>，则值为List.class； Map<String,List
   * <Employee>>，则值为Map.class；如果是普通类，则和actualType值一样
   */
  private String containerType;
  
  /**
   * 类型如果是普通JAVA Bean，则会包含字段信息
   */
  private List<FieldInfo> fields;

  public TypeInfo() {
    super();
  }

  public boolean isVoid() {
    return isVoid;
  }



  public void setVoid(boolean isVoid) {
    this.isVoid = isVoid;
  }



  public boolean isArray() {
    return isArray;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public void setEnum(boolean isEnum) {
    this.isEnum = isEnum;
  }

  public void setArray(boolean isArray) {
    this.isArray = isArray;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public void setCollection(boolean isCollection) {
    this.isCollection = isCollection;
  }

  public boolean isMap() {
    return isMap;
  }

  public void setMap(boolean isMap) {
    this.isMap = isMap;
  }

  public String getActualType() {
    return actualType;
  }

  public void setActualType(String actualType) {
    this.actualType = actualType;
    setSimpleActualType(Types.getSimpleTypeName(actualType));
  }

  public String getMapKeyType() {
    return mapKeyType;
  }

  public void setMapKeyType(String mapKeyType) {
    this.mapKeyType = mapKeyType;
  }

  public TypeInfo getMapValueType() {
    return mapValueType;
  }

  public void setMapValueType(TypeInfo mapValueType) {
    this.mapValueType = mapValueType;
  }

  public String getContainerType() {
    return containerType;
  }

  public void setContainerType(String containerType) {
    this.containerType = containerType;
  }


  public String getSimpleActualType() {
    return simpleActualType;
  }

  public void setSimpleActualType(String simpleActualType) {
    this.simpleActualType = simpleActualType;
  }

  public List<FieldInfo> getFields() {
    return fields;
  }

  public void setFields(List<FieldInfo> fields) {
    this.fields = fields;
  }

  @Override
  public String toString() {
    return "TypeInfo [isArray=" + isArray + ", isCollection=" + isCollection + ", isEnum=" + isEnum
        + ", isMap=" + isMap + ", actualType=" + actualType + ", simpleActualType="
        + simpleActualType + ", mapKeyType=" + mapKeyType + ", mapValueType=" + mapValueType
        + ", containerType=" + containerType + "]";
  }


}
