package com.dooioo.se.apidoclet.core.spi;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.RestClass;
import com.sun.javadoc.MethodDoc;

/**
 * 解析方法里的参数，设置Method的pathParams，queryParams,requestHeaders等
 */
public interface RestClassMethodParameterResolver {
  /**
    * 解析源代码methdoDoc，设置RestClass.Method的pathParams，queryParams,requestHeaders等
    * @author huisman
    * @param methodDoc 方法的javadoc描述
    * @param options 命令行参数
    * @param method 方法的信息
    * @param restClass 提供Rest服务的类
   */
  public void process(MethodDoc methodDoc, ApiDocletOptions options, RestClass.Method method,
      RestClass restClass);
}


