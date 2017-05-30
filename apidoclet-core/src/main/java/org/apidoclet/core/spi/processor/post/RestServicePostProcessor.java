package org.apidoclet.core.spi.processor.post;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.model.RestService;

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
  void postProcess(RestService restService,ProcessorContext context);
  
}


