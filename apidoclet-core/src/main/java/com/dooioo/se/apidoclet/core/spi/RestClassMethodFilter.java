package com.dooioo.se.apidoclet.core.spi;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.sun.javadoc.MethodDoc;

/**
 * 判断一个提供Rest服务的类里那些方法是Rest接口
 */
public interface RestClassMethodFilter {
  /**
   * 判断当前方法是否是Rest接口
   * @param options 命令行选项
   * @author huisman
   */
  public boolean accept(MethodDoc methodDoc, ApiDocletOptions options);
}
