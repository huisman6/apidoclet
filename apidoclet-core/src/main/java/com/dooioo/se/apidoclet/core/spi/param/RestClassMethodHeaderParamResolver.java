package com.dooioo.se.apidoclet.core.spi.param;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.HeaderParam;
import com.sun.javadoc.Parameter;

/**
 * 解析方法参数的Http Header
 */
public interface RestClassMethodHeaderParamResolver {

  /**
   * 是否支持此种参数的解析
   * 
   * @author huisman
   */
  boolean support(Parameter parameter, ApiDocletOptions options);

  /**
   * 解析HttpHeader
   * 
   * @author huisman
   * @param Parameter 参数信息的javadoc描述
   * @param options 命令行参数
   */
  public HeaderParam resolve(Parameter parameter, String paramComment, ApiDocletOptions options);
}
