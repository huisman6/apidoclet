package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * Http的查询参数，对应SpringMVC 的@RequestParam 或者JAX-RS里的@QueryParam
 * @author huisman
 */
public class QueryParam implements Serializable {
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
   * 备注，param tag
   */
  private String comment;

  /**
   * @param name
   * @param javaType
   * @param required
   * @param defaultValue
   */
  public QueryParam(String name, boolean required, String defaultValue) {
    super();
    this.name = name;
    this.required = required;
    this.defaultValue = defaultValue;
  }


  public TypeInfo getType() {
    return type;
  }



  public void setType(TypeInfo type) {
    this.type = type;
  }

  public QueryParam() {
    super();
  }


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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


  @Override
  public String toString() {
    return "QueryParam [required=" + required + ", defaultValue=" + defaultValue + ", name=" + name
        + ", comment=" + comment + "]";
  }



}
