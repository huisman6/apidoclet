package org.apidoclet.core.spi.http;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.model.PathParam;

import com.sun.javadoc.Parameter;

/**
 * 解析方法参数的PathVariable
 */
public interface PathParamResolver {

  /**
   * 是否支持此种参数的解析
   * 
   * @author huisman
   */
  boolean support(Parameter parameter, ApiDocletOptions options);

  /**
   * 解析PathParam
   * 
   * @author huisman
   * @param Parameter 参数信息的javadoc描述
   * @param options 命令行参数
   * @param typeResolver 类型信息解析，nerver null
   */
  public PathParam resolve(TypeInfoResolver typeResolver, Parameter parameter,
      String paramComment, ApiDocletOptions options);
}
