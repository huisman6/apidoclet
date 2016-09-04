package com.dooioo.se.apidoclet.core.spi.filter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;

/**
 * 有一些类型我们希望可以忽略，比如 HttpServletRequet等
 */
public interface SkippedTypeFilter {
  /**
   * 是否忽略当前类型的解析
   * @param type 类型的全限定名称
   * @param options 命令行选项
   */
  boolean ignored(String qualifiedTypeName, ApiDocletOptions options);
}
