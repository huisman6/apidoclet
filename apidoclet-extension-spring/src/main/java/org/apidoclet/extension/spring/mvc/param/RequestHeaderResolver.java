package org.apidoclet.extension.spring.mvc.param;

import java.util.Map;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.http.HeaderParamResolver;
import org.apidoclet.core.spi.http.TypeInfoResolver;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.HeaderParam;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * HTTP header resolver
 */
public class RequestHeaderResolver implements
    HeaderParamResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(),
        org.springframework.web.bind.annotation.RequestHeader.class.getName()) != null;
  }

  @Override
  public HeaderParam resolve(TypeInfoResolver typeResolver,
      Parameter parameter, String paramComment, ApiDocletOptions options) {
    AnnotationDesc requestHeader =
        AnnotationUtils.searchFirst(parameter.annotations(),
            org.springframework.web.bind.annotation.RequestHeader.class
                .getName());

    Map<String, AnnotationValue> attributes =
        AnnotationUtils.attributesFor(requestHeader);

    //String annotation value
    AnnotationValue valAttr = attributes.get("value");
    String value = (valAttr == null ? null : (String) valAttr.getValue());
    // Boolean annotation value
    AnnotationValue requiredAttr = attributes.get("required");
    Boolean required =
        (requiredAttr == null ? null : (Boolean) requiredAttr.getValue());

    AnnotationValue defaultAttr = attributes.get("defaultValue");
    String defaultValue =
        (defaultAttr == null ? null : (String) defaultAttr.getValue());

    HeaderParam headerParam =
        new HeaderParam(StringUtils.isNullOrEmpty(value) ? parameter.name()
            : value, required == null ? true : required,
            defaultValue == null ? null : defaultValue);
    headerParam.setComment(paramComment);
    headerParam.setType(typeResolver.resolve(parameter.type()));
    return headerParam;
  }

}
