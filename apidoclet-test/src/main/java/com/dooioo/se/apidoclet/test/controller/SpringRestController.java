package com.dooioo.se.apidoclet.test.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dooioo.se.apidoclet.test.model.User;

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
  public ResponseEntity<String> ignoreTypes(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }
  
  @RequestMapping("/testMap")
  public Map<String,User> testMap(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  @RequestMapping("/testTypeVariable")
  public <T> ResponseEntity<T> testTypeVariable(HttpServletRequest request,
      HttpServletResponse response) {
    return null;
  }

  @RequestMapping("/testWildTypeExtend")
  public ResponseEntity<? extends User> testWildTypeExtend(
      HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

  @RequestMapping("/testWildTypeSuper")
  public ResponseEntity<? super User> testWildTypeSuper(
      HttpServletRequest request, HttpServletResponse response) {
    return null;
  }
}
