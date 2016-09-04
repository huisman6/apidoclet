package com.dooioo.se.apidoclet.spring.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.filter.RestServiceFilter;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.dooioo.se.apidoclet.model.RestService;
import com.sun.javadoc.ClassDoc;

/**
 * 过滤RestController
 */
public class RestControllerRestServiceFilter implements RestServiceFilter {

  @Override
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    // 如果是Controller或者RestController，我们就认为可以提供Rest服务
    return AnnotationUtils.isPresent(classDoc.annotations(), Controller.class.getName())
        || AnnotationUtils.isPresent(classDoc.annotations(), RestController.class.getName());
  }

  @Override
  public boolean include(RestService service, String qualifiedClassName, ApiDocletOptions options) {
    return false;
  }

  @Override
  public String getServiceName(ClassDoc classDoc, ApiDocletOptions options) {
    return options.getApp();
  }

}
