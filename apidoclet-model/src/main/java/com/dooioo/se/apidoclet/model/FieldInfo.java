package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.List;

/**
 * 字段信息
 * @author huisman
 */
public class FieldInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 字段注释，优先从getter获取，其次从字段注释获取
   */
  private String comment;

  /**
   * 字段类型
   */
  private TypeInfo type;

  /**
   * 字段名称
   */
  private String name;

  /**
   * 字段是那个类的
   */
  private String declaringClass;

  /**
   * 如果该字段代表了一个model，那么该model的所有字段 <br/>
   * 一般是返回值里才解析
   */
  private List<FieldInfo> modelFields;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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

  public String getDeclaringClass() {
    return declaringClass;
  }

  public void setDeclaringClass(String declaringClass) {
    this.declaringClass = declaringClass;
  }


  public List<FieldInfo> getModelFields() {
    return modelFields;
  }

  public void setModelFields(List<FieldInfo> modelFields) {
    this.modelFields = modelFields;
  }

  @Override
  public String toString() {
    return "FieldInfo [comment=" + comment + ", type=" + type + ", name=" + name
        + ", declaringClass=" + declaringClass + ", modelFields=" + modelFields + "]";
  }



}
