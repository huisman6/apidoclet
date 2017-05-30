package org.apidoclet.model;

import java.io.Serializable;

/**
 * http request header abstract，similar to SpringMVC {@code @RequestHeader} or JAX-RS
 * {@code @HeaderParam}
 * 
 * @author huisman
 */
public class HeaderParam implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * required or not
   */
  private boolean required;
  /**
   * default http header value
   */
  private String defaultValue;
  /**
   * actual http header name
   */
  private String name;

  /**
   * corresponding java type description,include fields
   */
  private TypeInfo type;

  /**
   * java comment that read from souce code
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
    return "RequestHeader [required=" + required + ", defaultValue="
        + defaultValue + ", name=" + name + ", comment=" + comment + "]";
  }



}
