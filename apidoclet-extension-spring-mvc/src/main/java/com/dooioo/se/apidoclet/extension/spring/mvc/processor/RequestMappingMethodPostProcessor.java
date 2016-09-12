package com.dooioo.se.apidoclet.extension.spring.mvc.processor;

import com.dooioo.se.apidoclet.core.spi.processor.ApiDocProcessContext;
import com.dooioo.se.apidoclet.core.spi.processor.RestClassMethodPostProcessor;
import com.dooioo.se.apidoclet.model.RestClass;
import com.dooioo.se.apidoclet.model.RestClass.Method;

/**
 * 合并类和方法上的RequestMapping
 */
public class RequestMappingMethodPostProcessor implements RestClassMethodPostProcessor {

  @Override
  public void postProcess(Method method, RestClass restClass, ApiDocProcessContext context) {
    if (restClass.getEndpointMapping() != null) {
      // 将类上的映射信息，合并到方法上
      method.setMapping(restClass.getEndpointMapping().combine(method.getMapping()));
    }
  }

}
