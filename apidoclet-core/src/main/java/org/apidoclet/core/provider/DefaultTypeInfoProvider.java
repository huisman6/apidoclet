package org.apidoclet.core.provider;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.provider.TypeInfoProvider;
import org.apidoclet.core.util.ClassUtils;
import org.apidoclet.model.TypeInfo;

import com.sun.javadoc.Type;

/**
 * default {@code TypeInfo} resolver,lowest priority
 */
public class DefaultTypeInfoProvider implements TypeInfoProvider {

  @Override
  public TypeInfo produce(Type type, ApiDocletOptions options) {
    return ClassUtils.getTypeInfo(type, null);
  }
}
