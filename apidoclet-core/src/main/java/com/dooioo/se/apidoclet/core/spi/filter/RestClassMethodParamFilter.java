package com.dooioo.se.apidoclet.core.spi.filter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.sun.javadoc.Parameter;

/**
 * 参数过滤
 */
public interface RestClassMethodParamFilter {

  /**
    * 是否过滤掉方法的参数
    * @author huisman
    * @version v1
    * @param parameter 方法参数
    * @param options API Doc命令行参数
    * @since 2016年9月24日
   */
  boolean shouldSkipped(Parameter parameter, ApiDocletOptions options);

}
