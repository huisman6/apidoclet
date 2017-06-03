package org.apidoclet.core.spi.http;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.model.HeaderParam;

import com.sun.javadoc.Parameter;

/**
 * 解析方法参数的Http Header
 */
public interface HeaderParamResolver {

  /**
   */
  boolean support(Parameter parameter, ApiDocletOptions options);

  /**
   * 解析HttpHeader
   * 
   * @author huisman
   * @param Parameter 参数信息的javadoc描述
   * @param options 命令行参数
   * @param typeResolver 类型信息解析，nerver null
   */
  public HeaderParam resolve(TypeInfoResolver typeResolver,
      Parameter parameter, String paramComment, ApiDocletOptions options);
}
