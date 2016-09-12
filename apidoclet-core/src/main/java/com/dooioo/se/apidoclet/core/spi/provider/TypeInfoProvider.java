package com.dooioo.se.apidoclet.core.spi.provider;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.TypeInfo;
import com.sun.javadoc.Type;

/**
 * 获取一个类型的描述信息，类型可能为泛型、Class、枚举信息等，所有需要类型信息的地方都会调用此接口
 */
public interface TypeInfoProvider {
  /**
   * 将某个classdoc的类型信息解析成我们的model TypeInfo
   * 
   * @author huisman
   * @param type classdoc的类型信息
   * @param options 命令行选项
   */
  TypeInfo produce(Type type, ApiDocletOptions options);
}
