package org.apidoclet.core.spi.processor.post;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.RestService;

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
