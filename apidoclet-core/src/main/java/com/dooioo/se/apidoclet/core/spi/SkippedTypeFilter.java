package com.dooioo.se.apidoclet.core.spi;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.sun.javadoc.Type;

/**
 * 有一些类型我们希望可以忽略，比如 HttpServletRequet等
 */
public interface SkippedTypeFilter {
  /**
   * 是否忽略当前类型的解析
   * @param type javadoc type
   * @param options 命令行选项
   */
  boolean ignored(Type type, ApiDocletOptions options);
}
