package org.apidoclet.extension.spring.mvc.param;

import java.util.Map;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.http.PathParamResolver;
import org.apidoclet.core.spi.http.TypeInfoResolver;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.PathParam;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;
import org.springframework.web.bind.annotation.PathVariable;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * PathParam解析
 */
public class PathVariableResolver implements PathParamResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(),
        PathVariable.class.getName()) != null;
  }

  @Override
  public PathParam resolve(TypeInfoResolver typeResolver, Parameter parameter,
      String paramComment, ApiDocletOptions options) {
    AnnotationDesc pathVariable =
        AnnotationUtils.searchFirst(parameter.annotations(),
            PathVariable.class.getName());
    Map<String, AnnotationValue> attributes =
        AnnotationUtils.attributesFor(pathVariable);
    // 属性类型为基本类型包装类型以及字符串
    AnnotationValue pathAttr = attributes.get("value");
    String value = (pathAttr == null ? null : (String) pathAttr.getValue());
    PathParam param =
        new PathParam(StringUtils.isNullOrEmpty(value) ? parameter.name()
            : value);
    param.setComment(paramComment);
    param.setType(typeResolver.resolve(parameter.type()));
    return param;
  }

}
