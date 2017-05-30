package org.apidoclet.extension.spring.mvc.processor;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.core.spi.processor.post.RestClassMethodPostProcessor;
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
  }

}
