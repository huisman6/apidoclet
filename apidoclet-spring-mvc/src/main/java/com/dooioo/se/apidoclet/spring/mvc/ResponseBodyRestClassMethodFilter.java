package com.dooioo.se.apidoclet.spring.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.filter.RestClassMethodFilter;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * 对于SpringMVC来说，只有方法包含@ResponseEntity，才算数据接口
 */
public class ResponseBodyRestClassMethodFilter implements RestClassMethodFilter {

  @Override
  public boolean accept(MethodDoc methodDoc, ApiDocletOptions options) {
    ClassDoc spiClass = methodDoc.containingClass();
    // 如果是Controller，则检查方法上是否有ResponseBody
    boolean hasRequestMappingOnMethod =
        AnnotationUtils.isPresent(methodDoc.annotations(), RequestMapping.class.getName());;
   if (!hasRequestMappingOnMethod) {
      return false;
    }
    if (AnnotationUtils.isPresent(spiClass.annotations(), Controller.class.getName())) {
      return AnnotationUtils.isPresent(methodDoc.annotations(), ResponseBody.class.getName());
    } else if (AnnotationUtils.isPresent(spiClass.annotations(), RestController.class.getName())) {
      return true;
    }
    return false;
  }

}
