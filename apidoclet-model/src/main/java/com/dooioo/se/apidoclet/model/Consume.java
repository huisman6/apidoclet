package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * http request header : "Accept" media type,similar to SpringMVC {@code @RequestMapping#consumes} or JAX-RS
 * {@code @Consumes}.
 */
public class Consume implements Serializable {
  private static final long serialVersionUID = 1L;
  private String contentType;

  private Consume() {
    super();
  }

  /**
   * @param contentType
   */
  private Consume(String contentType) {
    super();
    this.contentType = contentType;
  }

  /**
   * request content-type
   * 
   * @author huisman
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @author huisman
   * @param contentType HttpRequest的Content-Type
   */
  public static Consume of(String contentType) {
    if (contentType == null || contentType.trim().isEmpty()) {
      throw new IllegalArgumentException("contentType is null");
    }
    return new Consume(contentType);
  }
}
