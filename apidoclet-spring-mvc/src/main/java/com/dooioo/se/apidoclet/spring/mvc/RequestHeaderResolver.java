package com.dooioo.se.apidoclet.spring.mvc;

import java.util.Map;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.param.RestClassMethodHeaderParamResolver;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.HeaderParam;
import com.dooioo.se.apidoclet.model.JavaAnnotations.AnnotationValue;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * 解析http header
 */
public class RequestHeaderResolver implements RestClassMethodHeaderParamResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(),
        org.springframework.web.bind.annotation.RequestHeader.class.getName()) != null;
  }

  @Override
  public HeaderParam resolve(Parameter parameter, String paramComment, ApiDocletOptions options) {
    AnnotationDesc requestHeader =
        AnnotationUtils.searchFirst(parameter.annotations(),
            org.springframework.web.bind.annotation.RequestHeader.class.getName());

    Map<String, AnnotationValue> attributes = AnnotationUtils.attributesFor(requestHeader);

    // 属性类型为基本类型包装类型以及字符串
    String value = (String) attributes.get("value").getValue();
    Boolean required = (Boolean) attributes.get("required").getValue();
    String defaultValue = (String) attributes.get("defaultValue").getValue();

    HeaderParam headerParam =
        new HeaderParam(StringUtils.isNullOrEmpty(value) ? parameter.name() : value,
            required == null ? true : required, defaultValue == null ? null : defaultValue);
    headerParam.setComment(paramComment);
    headerParam.setType(ClassUtils.getTypeInfo(parameter.type(), null));
    return headerParam;
  }

}
