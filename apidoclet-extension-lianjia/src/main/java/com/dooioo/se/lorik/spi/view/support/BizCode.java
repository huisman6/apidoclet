package com.dooioo.se.lorik.spi.view.support;


/**
 * 标准业务码
 */
public final class BizCode {

  private int code;
  private String message;

  public BizCode(int code, String message) {
    super();
    this.code = code;
    if (message == null) {
      message = "";
    }
    this.message = message;
  }

  public int getCode() {
    return this.code;
  }

  public String getMessage() {
    return this.message;
  }
}
