package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * 对应SpringMVC RequestMapping里的produces以及JAX-RS的@Produce.
 */
public class Produce implements Serializable {
  private static final long serialVersionUID = 1L;
  private String contentType;

  private Produce() {
    super();
  }

  /**
   * @param contentType
   */
  private Produce(String contentType) {
    super();
    this.contentType = contentType;
  }

  /**
   * HttpResponse content-type
   * 
   * @author huisman
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @author huisman
   * @param contentType HttpResponse的Content-Type
   */
  public static Produce of(String contentType) {
    if (contentType == null || contentType.trim().isEmpty()) {
      throw new IllegalArgumentException("contentType is null");
    }
    return new Produce(contentType);
  }
}
