package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.List;

import com.dooioo.se.apidoclet.model.util.Types;

/**
 * 
 * conventional java bean - Model（-View-Controller)</br>
 * mostly,it's the return type of Rest Endpoint
 * @author huisman
 */
public class ModelInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * full qualified java bean class name, see {@link Class#getName()}
   */
  private String className;
  /**
   * simple class name , see {@link Class#getSimpleName()}
   */
  private String simpleClassName;
  /**
   * fields that have public getter
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
