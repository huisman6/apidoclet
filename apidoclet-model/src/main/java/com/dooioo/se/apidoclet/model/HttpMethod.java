package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * Http访问method
 * @author huisman
 */
public class HttpMethod implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * Http Method ,大写
   */
  private String name;

  public String getName() {
    return name;
  }
  private HttpMethod(String name) {
    super();
    this.name = name;
  }
  private HttpMethod() {
    super();
  }

  @Override
  public String toString() {
    return "HttpMethod [name=" + name + "]";
  }

  /**
   *  http method
   */
  public static HttpMethod of(String method) {
    return new HttpMethod(method.toUpperCase());
  }
}
