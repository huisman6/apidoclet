package org.apidoclet.model;

import java.io.Serializable;

/**
 * http method description, GET/POST/PUT/HEAD/TRACE...etc
 * @author huisman
 */
public class HttpMethod implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * http method(upper-case)
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
