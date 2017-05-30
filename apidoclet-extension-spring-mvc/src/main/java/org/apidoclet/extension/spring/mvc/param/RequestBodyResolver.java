package org.apidoclet.extension.spring.mvc.param;

import java.util.Map;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.http.HttpRequestBodyResolver;
import org.apidoclet.core.spi.http.TypeInfoResolver;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;
import org.springframework.web.bind.annotation.RequestBody;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * parsing requestBody
 */
public class RequestBodyResolver implements HttpRequestBodyResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(),
        RequestBody.class.getName()) != null;
  }

  @Override
  public org.apidoclet.model.RequestBody resolve(
      TypeInfoResolver typeResolver, Parameter parameter, String paramComment,
      ApiDocletOptions options) {
    AnnotationDesc pathVariable =
        AnnotationUtils.searchFirst(parameter.annotations(),
            RequestBody.class.getName());
    Map<String, AnnotationValue> attributes =
        AnnotationUtils.attributesFor(pathVariable);
    // 属性类型为基本类型包装类型以及字符串
    AnnotationValue requiredAttr = attributes.get("required");
    Boolean required =
        (requiredAttr == null ? null : (Boolean) requiredAttr.getValue());
    org.apidoclet.model.RequestBody requestBody =
        new org.apidoclet.model.RequestBody();
    requestBody.setName(parameter.name());
    requestBody.setRequired(required == null ? false : required);
    requestBody.setComment(paramComment);
    requestBody.setType(typeResolver.resolve(parameter.type()));
    return requestBody;
  }

}
