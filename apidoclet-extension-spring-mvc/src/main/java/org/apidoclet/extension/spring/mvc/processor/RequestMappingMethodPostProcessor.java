package org.apidoclet.extension.spring.mvc.processor;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.core.spi.processor.post.RestClassMethodPostProcessor;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.RestClass.Method;

/**
 * 合并类和方法上的RequestMapping
 */
public class RequestMappingMethodPostProcessor implements RestClassMethodPostProcessor {

  @Override
  public void postProcess(Method method, RestClass restClass, ProcessorContext context) {
    if (restClass.getEndpointMapping() != null) {
      // 将类上的映射信息，合并到方法上
      method.setMapping(restClass.getEndpointMapping().combine(method.getMapping()));
    }
    // check if web-context-path exists
    String webContext = context.getOpitons().getWebCotextPath();
    if (!StringUtils.isNullOrEmpty(webContext)) {
      // prepend.... naive
      // to-do
      StringBuilder contextPath = new StringBuilder(webContext.length() + 4);
      contextPath.append(webContext);
      if (contextPath.charAt(0) != '/') {
        contextPath.append('/');
      }
      // last char
      if (contextPath.charAt(contextPath.length() - 1) == '/') {
        contextPath.deleteCharAt(contextPath.length() - 1);
      }
      if (!StringUtils.isNullOrEmpty(contextPath)) {
        // always start with '/'
        String path = method.getMapping().getPath();
        // prepend
        method.getMapping().setPath(contextPath + path);
      }
    }
  }

}
