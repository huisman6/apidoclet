package com.dooioo.se.apidoclet.core.spi.exporter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.RestService;

/**
 * 一个项目下如果有多个RestService分组，那么我们就得决定，某个model或者业务码或着类是属于哪个微服务的。
 */
public interface RestServicesPartitionStrategy {
  /**
   * 判断某个类是否属于当前的微服务
   * 
   * @author huisman
   * @version v1
   * @param qualifiedClassName 类的全称
   * @param options 命令行选项
   */
  boolean belongTo(RestService service, String qualifiedClassName,
      ApiDocletOptions options);

}
