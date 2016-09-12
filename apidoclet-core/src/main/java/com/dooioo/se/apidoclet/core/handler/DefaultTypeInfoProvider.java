package com.dooioo.se.apidoclet.core.handler;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.provider.TypeInfoProvider;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.model.TypeInfo;
import com.sun.javadoc.Type;

/**
 * 默认类型解析实现，优先级最低
 */
public class DefaultTypeInfoProvider implements TypeInfoProvider {

  @Override
  public TypeInfo produce(Type type, ApiDocletOptions options) {
    return ClassUtils.getTypeInfo(type, null);
  }
}
