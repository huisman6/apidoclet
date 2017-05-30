package org.apidoclet.model;

import java.io.Serializable;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * all java annotations on class or method ,regardless of {@link RetentionPolicy}
 */
public class JavaAnnotations implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * annotations
   */
  private Map<String, List<JavaAnnotation>> allAnnotations = new HashMap<>();

  public JavaAnnotations() {
    super();
  }

  /**
   * 获取某种qualifiedClassName的所有Java 注解（可能出现重复注解）
   * 
   * @author huisman
   */
  public List<JavaAnnotation> tags(String qualifiedClassName) {
    return allAnnotations.get(qualifiedClassName);
  }

  /**
   * 新增Java注解，如果重复的注解，则聚合在一起
   * 
   * @author huisman
   */
  public void add(JavaAnnotation annotation) {
    if (annotation == null || annotation.getQualifiedClassName() == null) {
      return;
    }
    List<JavaAnnotation> annotations =
        this.allAnnotations.get(annotation.getQualifiedClassName());
    if (annotations == null) {
      annotations = new ArrayList<JavaAnnotations.JavaAnnotation>();
    }
    annotations.add(annotation);
    this.allAnnotations.put(annotation.getQualifiedClassName(), annotations);
  }



  /* json-serialize */public Map<String, List<JavaAnnotation>> getAllAnnotations() {
    return allAnnotations;
  }

  /* json-deserialize */public void setAllAnnotations(
      Map<String, List<JavaAnnotation>> allAnnotations) {
      if (allAnnotations == null) {
        this.allAnnotations = new HashMap<>();
      } else {
        this.allAnnotations = allAnnotations;
      }
  }

  @Override
  public String toString() {
    return "JavaAnnotations [allAnnotations=" + allAnnotations + "]";
  }

  public static class JavaAnnotation implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 注解完整类名
     */
    private String qualifiedClassName;
    /**
     * 注解的属性值
     */
    private Map<String, AnnotationValue> attributes;

    public JavaAnnotation() {
      super();
    }

    public JavaAnnotation(Map<String, AnnotationValue> attributes) {
      this.setAttributes(attributes);
    }

    /**
     * 注解完全限定类名
     * 
     * @author huisman
     */
    public String getQualifiedClassName() {
      return this.qualifiedClassName;
    }

    public void setQualifiedClassName(String qualifiedClassName) {
      this.qualifiedClassName = qualifiedClassName;
    }

    public AnnotationValue attribute(String key) {
      return this.attributes.get(key);
    }


    /* json serialize */public Map<String, AnnotationValue> getAttributes() {
      return attributes;
    }

    /* json deserialize */public void setAttributes(
        Map<String, AnnotationValue> attributes) {
      if (attributes == null) {
        this.attributes = new HashMap<String, AnnotationValue>();
      } else {
        this.attributes = attributes;
      }
    }

    @Override
    public String toString() {
      return "JavaAnnotation [qualifiedClassName=" + qualifiedClassName
          + ", attributes=" + attributes + "]";
    }


  }
  /**
   * 注解的值，如果为数组，则转换为List
   */
  public static class AnnotationValue implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 注解的值
     */
    private Object value;
    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * @param value
     * @param defaultValue
     */
    public AnnotationValue(Object value, Object defaultValue) {
      super();
      this.value = value;
      this.defaultValue = defaultValue;
    }

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
      return "AnnotationValue [value=" + value + ", defaultValue="
          + defaultValue + "]";
    }
  }
}
