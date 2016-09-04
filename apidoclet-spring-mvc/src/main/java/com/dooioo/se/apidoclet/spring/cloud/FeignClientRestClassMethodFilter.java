package com.dooioo.se.apidoclet.spring.cloud;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.filter.RestClassMethodFilter;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * 过滤FeignClient方法
 */
public class FeignClientRestClassMethodFilter implements RestClassMethodFilter {

  @Override
  public boolean accept(MethodDoc methodDoc, ApiDocletOptions options) {
    ClassDoc spiClass = methodDoc.containingClass();
    // 如果是Controller，则检查方法上是否有ResponseBody
    boolean hasRequestMappingOnMethod =
        AnnotationUtils.isPresent(methodDoc.annotations(), RequestMapping.class.getName());;
    if (!hasRequestMappingOnMethod) {
      return false;
    }
    return AnnotationUtils.isPresent(spiClass.annotations(), FeignClient.class.getName());
  }

}
