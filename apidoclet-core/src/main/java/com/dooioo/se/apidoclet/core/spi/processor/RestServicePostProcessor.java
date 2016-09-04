package com.dooioo.se.apidoclet.core.spi.processor;

import com.dooioo.se.apidoclet.model.RestService;

/**
 * 服务级别的后续处理
 */
public interface RestServicePostProcessor {
  /**
    * 对某个微服务进行后续处理
    * @author huisman
    * @param restService
    * @param context
   */
  void postProcess(RestService restService,ApiDocProcessContext context);
  
}


