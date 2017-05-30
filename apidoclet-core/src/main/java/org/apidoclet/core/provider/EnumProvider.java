package org.apidoclet.core.provider;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.util.ClassUtils;
import org.apidoclet.model.EnumInfo;

import com.sun.javadoc.ClassDoc;

public final  class EnumProvider {
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    return classDoc.isEnum();
  }

  public EnumInfo handle(ClassDoc classDoc, ApiDocletOptions options) {
    // resolve enum
    EnumInfo spiEnum = new EnumInfo();
    spiEnum.setClassName(classDoc.qualifiedTypeName());
    spiEnum.setFields(ClassUtils.getFieldInfos(classDoc, null));
    return spiEnum;
  }
}


