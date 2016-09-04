package com.dooioo.se.apidoclet.core.spi.exporter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.RestServices;

/**
 * 将解析后的数据导出到展示的地方
 */
public interface RestServicesExporter {
   /**
    * 将解析后的Rest服务接口导出到展示的地方
    * @author huisman
    * @param restServices Rest服务接口
    * @param options 命令行选项
   */
  void exportTo(RestServices restServices,ApiDocletOptions options);
}


