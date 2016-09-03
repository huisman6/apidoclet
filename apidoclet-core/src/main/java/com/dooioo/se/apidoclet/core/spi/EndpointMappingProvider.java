package com.dooioo.se.apidoclet.core.spi;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.EndpointMapping;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.SourcePosition;

/**
 * 获取Rest接口的映射信息
 */
public interface EndpointMappingProvider {
  /**
    * 解析Rest接口的映射信息 
    * @author huisman
    * @param annotationDescs 方法或类上的标注信息
    * @param options 命令选项
    * @param position 源代码的位置，方便打印错误信息
   */
  EndpointMapping produce(AnnotationDesc[] classOrMethodAnnotationDescs, ApiDocletOptions options,
      SourcePosition position);
}
