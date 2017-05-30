package com.dooioo.se.apidoclet.test.controller;

import java.util.Map;

import javax.jws.soap.SOAPBinding.Use;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dooioo.se.apidoclet.test.bizcode.TestBizCodes;
import com.dooioo.se.apidoclet.test.model.TestHaha;
import com.dooioo.se.apidoclet.test.model.User;
import com.dooioo.se.lorik.spi.view.support.LorikRest;

@RestController
public class SpringRestController {

  /**
   * RestController测试
   * 
   * @author huisman
   * @version v1
   * @param id 编号
   * @param name 姓名
   * @return
   * @since 2016年9月11日
   * @summary RestController测试
   */
  @RequestMapping("/rest/test/{id}")
  public String restTest(@PathVariable("id") int id,
      @RequestParam("name") String name) {
    return null;
  }

  @RequestMapping("/ignoredTypes")
  public ResponseEntity<TestHaha> ignoreTypes(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }
  
  /**
    * 
    * @author huisman
    * @version v1
    * @param request
    * @param response
    * @returnType User
    * @since 2017年5月28日
    * @summary   
   */
  @RequestMapping("/testMap")
  public Map<String,User> testMap(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  /**
    * 
    * @author huisman
    * @version v1
    * @param request
    * @param response
    * @returnType {@code User} {@linkplain User} {@link User} User
    * @since 2017年5月28日
    * @summary   
   */
  @RequestMapping("/testTypeVariable")
  public <T> ResponseEntity<T> testTypeVariable(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  /**
    * 
    * @author huisman
    * @version v1
    * @param request
    * @param response
    * @returnType com.dooioo.se.apidoclet.test.model.UserGroup
    * @return
    * @since 2017年5月28日
    * @summary 
   */
  @RequestMapping("/testWildTypeExtend")
  public ResponseEntity<? extends User> testWildTypeExtend(
      HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  /**
    * @bizCodes
    *        {@link TestBizCodes#CODE1_TEST} 
    *        {@link TestBizCodes#CODE1_TEST} 
    *        {@link TestBizCodes#CODE1_TEST} 
    *        {@code TestBizCodes#NOT_FOUND}
    * @author huisman
    * @version v1
    * @param request
    * @param response
    * @return
    * @since 2017-02-28
    * @summary  真实号码
   */
  @RequestMapping("/testWildTypeSuper")
  public ResponseEntity<? super User> testWildTypeSuper(
      HttpServletRequest request, HttpServletResponse response) {
    return null;
  }
  
  @RequestMapping("/testLorikBizCodes")
  @LorikRest(codes={20001})
  public ResponseEntity<? super User> testLorikBizCodes(
      HttpServletRequest request, HttpServletResponse response) {
    return null;
  }
}
