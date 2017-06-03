package org.apidoclet.model;

import java.io.Serializable;

/**
 * Http Request Entity, similar to SpringMVC {@code @RequestBody} 
 */
public class RequestBody implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 是否必须
   */
  private boolean required;
  /**
   * 默认值
   */
  private String defaultValue;
  /**
   * 参数名
   */
  private String name;

  /**
   * 对应的JAVA类型
   */
  private TypeInfo type;

  /**
   * JavaDoc 备注
   */
  private String comment;

  public RequestBody() {
    super();
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TypeInfo getType() {
    return type;
  }

  public void setType(TypeInfo type) {
    this.type = type;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return "RequestBody [required=" + required + ", defaultValue="
        + defaultValue + ", name=" + name + ", type=" + type + ", comment="
        + comment + "]";
  }

}
