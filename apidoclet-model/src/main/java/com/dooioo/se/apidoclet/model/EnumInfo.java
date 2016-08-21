package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.List;

import com.dooioo.se.apidoclet.model.util.Types;

/**
 * 枚举类的抽象，当我们解析源代码的时候，会将所有遇到的枚举类型解析出来。
 * 另外，需要注意，如果枚举类型不是源代码，而是依赖jar里的class文件，则拿不到javadoc，我们最多可以用反射获取字段信息。
 */
public class EnumInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 枚举类的名称
   */
  private String className;
  /**
   * 枚举类的简单名称
   */
  private String simpleClassName;
  /**
   * 枚举类里的字段
   */
  private List<FieldInfo> fields;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
    setSimpleClassName(Types.getSimpleTypeName(className));
  }



  public String getSimpleClassName() {
    return simpleClassName;
  }

  public void setSimpleClassName(String simpleClassName) {
    this.simpleClassName = simpleClassName;
  }

  public List<FieldInfo> getFields() {
    return fields;
  }

  public void setFields(List<FieldInfo> fields) {
    this.fields = fields;
  }

  @Override
  public String toString() {
    return "SpiModel [className=" + className + ", fields=" + fields + "]";
  }
}
