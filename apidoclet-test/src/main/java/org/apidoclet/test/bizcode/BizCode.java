package org.apidoclet.test.bizcode;

public final class BizCode {
  final private String code;
  final private String message;
  public BizCode(int code, String message) {
    this.code=String.valueOf(code);
    this.message=message;
  }
  public String getCode() {
    return code;
  }
  public String getMessage() {
    return message;
  }
  

}


