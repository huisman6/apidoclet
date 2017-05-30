package com.dooioo.se.apidoclet.core.spi.filter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;

/**
 * 有一些类型我们希望可以忽略，比如 HttpServletRequet等
 */
public interface TypeFilter {
  /**
   * 是否忽略当前类型的解析，注意，如果此类型不是你本项目的源代码，则qualifiedTypeName只会返回SimpleClassName。
   * 比如，javax.servlet.HttpServletRequest，此类不是你源代码里的类型，则qualifiedTypeName=HttpServletRequest。
   * @param type 类型的全限定名称
   * @param options 命令行选项
   */
  boolean ignored(String qualifiedTypeName, ApiDocletOptions options);
}
