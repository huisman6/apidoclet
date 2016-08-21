package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解信息
 */
public class AnnotationInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 注解完整类名
   */
  private String className;
  /**
   * 注解的属性值
   */
  private Map<String, AnnotationValue> attributes;

  public AnnotationInfo() {
    super();
    attributes = new HashMap<String, AnnotationValue>();
  }



  /**
    * 注解完全限定类名
    * @author huisman
   */
  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public AnnotationValue attribute(String key) {
    return this.attributes.get(key);
  }

  public class AnnotationValue implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 注解的值
     */
    private Object value;
    /**
     * 默认值
     */
    private Object defaultValue;

    public AnnotationValue() {
      super();
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    public Object getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
      this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
      return "AnnotationValue [value=" + value + ", defaultValue=" + defaultValue + "]";
    }


  }


}
