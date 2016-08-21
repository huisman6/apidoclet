package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * 业务码的抽象，包含：code (业务码）和message（简要说明，一般响应给客户端），
 * comment(注释，详细说明，通常展示在文档中，帮助使用者理解此业务码的cotext信息）
 * @author huisman
 */
public class BizCode implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 业务码
   */
  private int code;
  /**
   * 业务码说明
   */
  private String message;
  /**
   * BizCode注释
   */
  private String comment;

  /**
   * @param code
   * @param message
   */
  public BizCode(int code, String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public BizCode(int code, String message, String comment) {
    super();
    this.code = code;
    this.message = message;
    this.comment = comment;
  }
  
  public BizCode() {
    super();
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
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
    return "BizCode [code=" + code + ", message=" + message + ", comment=" + comment + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + code;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BizCode other = (BizCode) obj;
    if (code != other.code)
      return false;
    return true;
  }



}
