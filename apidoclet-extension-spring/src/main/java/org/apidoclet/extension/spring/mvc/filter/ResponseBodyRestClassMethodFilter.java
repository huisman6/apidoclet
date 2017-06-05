package org.apidoclet.extension.spring.mvc.filter;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.filter.RestClassMethodFilter;
import org.apidoclet.core.util.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 *  * Spring-Web-MVC Controller method support
 */
public class ResponseBodyRestClassMethodFilter implements RestClassMethodFilter {

  @Override
  public boolean accept(MethodDoc methodDoc, ApiDocletOptions options) {
    ClassDoc spiClass = methodDoc.containingClass();
    // first , check if @RequestMapping exsits
    boolean hasRequestMappingOnMethod =
        AnnotationUtils.isPresent(methodDoc.annotations(), RequestMapping.class.getName());;
   if (!hasRequestMappingOnMethod) {
      return false;
    }
    if (AnnotationUtils.isPresent(spiClass.annotations(), Controller.class.getName())) {
      //if @Controller annotation exists, double check if @ResponseBody annotation exists on this method
      return AnnotationUtils.isPresent(methodDoc.annotations(), ResponseBody.class.getName());
    } else if (AnnotationUtils.isPresent(spiClass.annotations(), RestController.class.getName())) {
      //@RestController annotation exists
      return true;
    }
    return false;
  }

}
