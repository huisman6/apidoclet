package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * 业务码的抽象，包含：code (业务码）和message（简要说明，一般响应给客户端）， comment(注释，详细说明，通常展示在文档中，帮助使用者理解此业务码的cotext信息）
 * @author huisman
 */
public class BizCode implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * biz code identification,MUST be global unique 
   */
  private String code;
  /**
   * biz code description
   */
  private String message;
  /**
   * biz code java comment
   */
  private String comment;

  /**
   * return the field name if the biz code  declares in  another class as a field
   */
  private String name;

  /**
   * full qualified type name of the class or interface that declares the biz code, may be null
   */
  private String declaredClass;

  /**
   * @param code
   * @param message
   */
  public BizCode(String code, String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public BizCode(String code, String message, String comment) {
    super();
    this.code = code;
    this.message = message;
    this.comment = comment;
  }

  public BizCode() {
    super();
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDeclaredClass() {
    return declaredClass;
  }

  public void setDeclaredClass(String declaredClass) {
    this.declaredClass = declaredClass;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }



  @Override
  public String toString() {
    return "BizCode [code=" + code + ", message=" + message + ", comment="
        + comment + ", name=" + name + ", declaredClass=" + declaredClass + "]";
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    // only code matters
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    // only code matters
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BizCode other = (BizCode) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    return true;
  }



}
