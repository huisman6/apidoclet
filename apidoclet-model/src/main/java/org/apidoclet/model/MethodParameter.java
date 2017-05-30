package org.apidoclet.model;

import java.io.Serializable;

/**
 * java method parameter description,include parameter comment、parameter annotation、parameter type etc
 */
public class MethodParameter implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * varargs? , see {@link java.lang.reflect.Method#isVarArgs()}
   */
  private boolean varargs;
  
  /**
   * method name ,see {@link java.lang.reflect.Method#getName()}
   */
  private String methodName;
  /**
   * parameter index ,see {@link java.lang.reflect.Method#getParameters()}
   */
  private int index;
  /**
   * parameter comment, see {@code @param} java doc tag
   */
  private String comment;

  /**
   * parameter type ,full qualified type name
   */
  private String className;
  /**
   * parameter name,see {@code Parameter#getName()}
   */
  private String name;
  /**
   * parameter type info,not null
   */
  private TypeInfo typeInfo;

  /**
   * all java annotations on this parameter
   */
  private JavaAnnotations parameterAnnotations;


  public JavaAnnotations getParameterAnnotations() {
    return parameterAnnotations;
  }

  public void setParameterAnnotations(JavaAnnotations parameterAnnotations) {
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
