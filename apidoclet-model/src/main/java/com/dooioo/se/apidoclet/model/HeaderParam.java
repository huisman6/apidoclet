package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * http 请求头，对应SpringMVC的@RequestHeader 或者JAX-RS的@HeaderParam
 * @author huisman
 */
public class HeaderParam implements Serializable {
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
   * 路径参数名
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

  /**
   * @param name
   * @param javaType
   * @param required
   * @param defaultValue
   */
  public HeaderParam(String name, boolean required, String defaultValue) {
    super();
    this.name = name;
    this.required = required;
    this.defaultValue = defaultValue;
  }

  public HeaderParam() {
    super();
  }


  public TypeInfo getType() {
    return type;
  }

  public void setType(TypeInfo type) {
    this.type = type;
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


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "RequestHeader [required=" + required + ", defaultValue=" + defaultValue + ", name="
        + name + ", comment=" + comment + "]";
  }



}
