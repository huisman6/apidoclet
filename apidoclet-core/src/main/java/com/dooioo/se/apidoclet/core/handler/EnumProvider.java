package com.dooioo.se.apidoclet.core.handler;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.model.EnumInfo;
import com.sun.javadoc.ClassDoc;

public final  class EnumProvider {
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    return classDoc.isEnum();
  }

  public EnumInfo handle(ClassDoc classDoc, ApiDocletOptions options) {
    // 解析枚举
    EnumInfo spiEnum = new EnumInfo();
    spiEnum.setClassName(classDoc.qualifiedTypeName());
    spiEnum.setFields(ClassUtils.getFieldInfos(classDoc, null));
    return spiEnum;
  }
}


