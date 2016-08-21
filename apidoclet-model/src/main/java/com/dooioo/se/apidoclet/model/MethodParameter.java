package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.List;

/**
 * 方法参数的一些context信息
 */
public class MethodParameter implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 是否可变参数
   */
  private boolean varargs;
  /**
   * 方法名
   */
  private String methodName;
  /**
   * 参数索引
   */
  private int index;
  /**
   * 参数注释
   */
  private String comment;

  /**
   * 参数类型
   */
  private String className;
  /**
   * 参数名
   */
  private String name;
  /**
   * 如果参数类型的一些描述，客户端实现的时候不能为null
   */
  private TypeInfo typeInfo;

  /**
   * 参数上的注解
   */
  private List<AnnotationInfo> parameterAnnotations;


  public List<AnnotationInfo> getParameterAnnotations() {
    return parameterAnnotations;
  }

  public void setParameterAnnotations(List<AnnotationInfo> parameterAnnotations) {
    this.parameterAnnotations = parameterAnnotations;
  }

  public boolean isVarargs() {
    return varargs;
  }

  public void setVarargs(boolean varargs) {
    this.varargs = varargs;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
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

  public TypeInfo getTypeInfo() {
    return typeInfo;
  }

  public void setTypeInfo(TypeInfo typeInfo) {
    this.typeInfo = typeInfo;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  @Override
  public String toString() {
    return "MethodParameter [varargs=" + varargs + ", methodName=" + methodName + ", index="
        + index + ", comment=" + comment + ", className=" + className + ", name=" + name
        + ", typeInfo=" + typeInfo + "]";
  }


}
