package com.dooioo.se.apidoclet.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/test")
public class SpringController {
  /**
   * 测试注释，RequestMapping和其他注解
   * 
   * @author huisman
   * @version v1
   * @param p1 requestParam-p1
   * @param header1 requestHeader-header1
   * @param path2 pathvariable-path1
   * @return 赫尔
   * @since 2016年9月10日
   * @summary 完整测试
   * @deprecated 请使用其他版本
   */
  @Deprecated
  @RequestMapping(value = "/testAll/{path1}", consumes = {
      MimeTypeUtils.APPLICATION_FORM_URLENCODED_VALUE, "Text/Plain"},
      produces = {"applicaiton/json"}, params = {"param1", "param2=kev"},
      method = RequestMethod.POST)
  @ResponseBody
  public String testAll(@RequestParam("param1") String p1,
      @RequestHeader("header1") String header1,
      @PathVariable("path1") String path2) {
    return null;
  }
}
