package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * 路径参数名，对应SpringMVC的@PathVariable或JAX-RS里的 @Path。
 * 通常用来描述URI里的PATH部分。
 * @author huisman
 */
public class PathParam implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 路径参数名
   */
  private String name;

  /**
   * 对应的JAVA类型
   */
  private TypeInfo type;

  /**
   * java doc tag 注释
   */
  private String comment;

  /**
   * @param name
   * @param javaType
   */
  public PathParam(String name) {
    super();
    this.name = name;
  }

  public PathParam() {
    super();
  }

  public TypeInfo getType() {
    return type;
  }

  public void setType(TypeInfo type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }



  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return "PathParam [name=" + name + ", comment=" + comment + "]";
  }



}
