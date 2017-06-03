package org.apidoclet.extension.spring.cloud.filter;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.filter.RestClassMethodFilter;
import org.apidoclet.core.util.AnnotationUtils;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 *  spring-cloud-netflix {@code FeignClient} method support
 */
public class FeignClientRestClassMethodFilter implements RestClassMethodFilter {

  @Override
  public boolean accept(MethodDoc methodDoc, ApiDocletOptions options) {
    ClassDoc spiClass = methodDoc.containingClass();
    // check if @RequestMapping exists
    boolean hasRequestMappingOnMethod =
        AnnotationUtils.isPresent(methodDoc.annotations(), RequestMapping.class.getName());;
    if (!hasRequestMappingOnMethod) {
      return false;
    }
    return AnnotationUtils.isPresent(spiClass.annotations(), FeignClient.class.getName());
  }

}
