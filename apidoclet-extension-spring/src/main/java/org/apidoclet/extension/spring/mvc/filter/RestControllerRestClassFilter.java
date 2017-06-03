package org.apidoclet.extension.spring.mvc.filter;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.filter.RestClassFilter;
import org.apidoclet.core.util.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.sun.javadoc.ClassDoc;

/**
 * Spring-Web-MVC Controller support
 */
public class RestControllerRestClassFilter implements RestClassFilter {

  @Override
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    // @Controller or @RestController presents
    return AnnotationUtils.isPresent(classDoc.annotations(),
        Controller.class.getName())
        || AnnotationUtils.isPresent(classDoc.annotations(),
            RestController.class.getName());
  }

  @Override
  public String getServiceIdIfAny(ClassDoc classDoc, ApiDocletOptions options) {
    //have no idea about serviceId
    return null;
  }

}
