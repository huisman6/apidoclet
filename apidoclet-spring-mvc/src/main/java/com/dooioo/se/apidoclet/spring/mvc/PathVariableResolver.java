package com.dooioo.se.apidoclet.spring.mvc;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.param.RestClassMethodPathParamResolver;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.PathParam;
import com.dooioo.se.apidoclet.model.JavaAnnotations.AnnotationValue;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * PathParam解析
 */
public class PathVariableResolver implements RestClassMethodPathParamResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(), PathVariable.class.getName()) != null;
  }

  @Override
  public PathParam resolve(Parameter parameter, String paramComment, ApiDocletOptions options) {
    AnnotationDesc pathVariable =
        AnnotationUtils.searchFirst(parameter.annotations(), PathVariable.class.getName());
    Map<String, AnnotationValue> attributes = AnnotationUtils.attributesFor(pathVariable);
    // 属性类型为基本类型包装类型以及字符串
    String value = (String) attributes.get("value").getValue();
    PathParam param = new PathParam(StringUtils.isNullOrEmpty(value) ? parameter.name() : value);
    param.setComment(paramComment);
    param.setType(ClassUtils.getTypeInfo(parameter.type(), null));
    return param;
  }

}
