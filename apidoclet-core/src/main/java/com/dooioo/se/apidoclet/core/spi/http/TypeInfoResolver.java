package com.dooioo.se.apidoclet.core.spi.http;

import com.dooioo.se.apidoclet.model.TypeInfo;
import com.sun.javadoc.Type;

/**
 * 解析类型信息
 */
public interface TypeInfoResolver {

  /**
   * 返回类型信息
   */
  TypeInfo resolve(Type type);
}
