package org.apidoclet.model;

/**
 * Rest Service Category
 */
public enum ServiceType {
  /**
   * class annotated by Spring Cloud Netflix Annotation {@code @FeignClient}
   */
  FEIGN_CLIENT,
  /**
   *  class annotated by Spring-Web-MVC annotation {@code  @Controller} 
   */
  CONTROLLER,
  /**
   *  class annotated by Spring-Web-MVC annotation {@code  @RestController} 
   */
  REST_CONTROLLER;
}
