package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.List;

import com.dooioo.se.apidoclet.model.util.Types;

/**
 * rest响应的model
 * 
 * @author huisman
 */
public class ModelInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * model类的名称
   */
  private String className;
  /**
   * model类的简单名称
   */
  private String simpleClassName;
  /**
   * model里的字段
   */
  private List<FieldInfo> fields;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
    setSimpleClassName(Types.getSimpleTypeName(className));
  }

  public List<FieldInfo> getFields() {
    return fields;
  }

  public void setFields(List<FieldInfo> fields) {
    this.fields = fields;
  }


  public String getSimpleClassName() {
    return simpleClassName;
  }

  public void setSimpleClassName(String simpleClassName) {
    this.simpleClassName = simpleClassName;
  }

  @Override
  public String toString() {
    return "Model [className=" + className + ", fields=" + fields + "]";
  }
}
