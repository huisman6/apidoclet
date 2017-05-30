package com.dooioo.se.apidoclet.core.spi.processor.post;

import com.dooioo.se.apidoclet.core.spi.processor.ProcessorContext;
import com.dooioo.se.apidoclet.model.RestClass;
import com.dooioo.se.apidoclet.model.RestService;

/**
 * Rest类的后续处理
 */
public interface RestClassPostProcessor {
  /**
   * 给实现类一个机会去对RestClass做一些后续处理，此时所有的业务码、model、枚举信息都已经解析完毕。</br> 实现类可以从{@code ProcessorContext}
   * 获取所需要的信息
   * 
   * @author huisman
   */
  void postProcess(RestClass restClass, RestService restService, ProcessorContext context);
}
