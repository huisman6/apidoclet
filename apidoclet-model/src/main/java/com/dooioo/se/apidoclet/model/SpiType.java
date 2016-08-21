package com.dooioo.se.apidoclet.model;

/**
 * SPI class的类型，默认是FeignClient
 */
public enum SpiType {
  /**
   * feignClient
   */
  FEIGN_CLIENT,
  /**
   * Spring 标注Controller
   */
  CONTROLLER,
  /**
   * Spring 标注 RestController
   */
  REST_CONTROLLER;
}
