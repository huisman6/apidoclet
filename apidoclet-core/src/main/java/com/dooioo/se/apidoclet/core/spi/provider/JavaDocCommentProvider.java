package com.dooioo.se.apidoclet.core.spi.provider;

import com.sun.javadoc.Type;

/**
 * 获取某个类型特定字段的注释
 */
public interface JavaDocCommentProvider {
  /**
   * 获取fieldOrMethodName上的注释
   * 
   * @param type class类型
   * @param fieldOrMethodName 字段或者方法名
   * @param options 命令行选项
   */
  String getComment(Type type, String fieldOrMethodName);
}
