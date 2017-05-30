package org.apidoclet.model;

import java.io.Serializable;

/**
 * http response header "Content-Type",similar to SpringMVC {@code @RequestMapping#produces} or JAX-RS
 * {@code @Produces}.
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
