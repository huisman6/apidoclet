package org.apidoclet.core.spi.provider;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.model.ModelInfo;

import com.sun.javadoc.ClassDoc;

/**
 * 判断并解析ModelInfo，通常是解析接口返回的model信息
 */
public interface ModelProvider {
  /**
   * 判断当前类是不是一个接口model
   * @param classDoc 当前类的描述信息
   */
  boolean accept(ClassDoc classDoc,ApiDocletOptions options);
  
   /**
    * 生成一个modelInfo，主要是字段的信息
    * @param classDoc 当前源代码类的描述
    */
   ModelInfo produce(ClassDoc classDoc,ApiDocletOptions options);
}
