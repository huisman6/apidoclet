package org.apidoclet.core.spi.processor.post;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.model.RestClass;

/**
 * 对RestClass.Method做一些后续处理
 */
public interface RestClassMethodPostProcessor {
  /**
   * 给实现类一个机会去对RestClass.Method做一些后续处理，此时所有的业务码、model、枚举信息都已经解析完毕。</br> 实现类可以从 客户端可以从
   * {@code ProcessorContext} 获取所需要的上下文信息。
   * 
   * @author huisman
   */
  void postProcess(RestClass.Method method, RestClass restClass, ProcessorContext context);
}
