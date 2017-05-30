package com.dooioo.se.apidoclet.model;

/**
 * Rest Service Category
 */
public enum ServiceType {
  /**
   * Spring Cloud Netflix Annotation {@code @FeignClient}
   */
  FEIGN_CLIENT,
  /**
   * Spring-Web-MVC annotation {@code  @Controller} 
   */
  CONTROLLER,
  /**
   * Spring-Web-MVC annotation {@code  @RestController} 
   */
  REST_CONTROLLER;
}
