package org.apidoclet.extension.spring.mvc.param;

import java.util.Map;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.http.QueryParamResolver;
import org.apidoclet.core.spi.http.TypeInfoResolver;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.ClassUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.QueryParam;
import org.apidoclet.model.TypeInfo;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;
import org.springframework.web.bind.annotation.RequestParam;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;

/**
 * SpringMVC参数注解的解析
 */
public class RequestParamResolver implements QueryParamResolver {

  @Override
  public boolean support(Parameter parameter, ApiDocletOptions options) {
    return AnnotationUtils.searchFirst(parameter.annotations(),
        RequestParam.class.getName()) != null;
  }

  @Override
  public QueryParam resolve(TypeInfoResolver typeResolver,Parameter parameter, String paramComment,
      ApiDocletOptions options) {
    AnnotationDesc requestParam =
        AnnotationUtils.searchFirst(parameter.annotations(),
            RequestParam.class.getName());
    Map<String, AnnotationValue> attributes =
        AnnotationUtils.attributesFor(requestParam);
    TypeInfo type = typeResolver.resolve(parameter.type());
    if (type == null) {
      type = new TypeInfo();
      type.setActualType(parameter.type().qualifiedTypeName());
      type.setContainerType(parameter.type().qualifiedTypeName());
    }

    // 属性类型为基本类型包装类型以及字符串
    AnnotationValue valAttr = attributes.get("value");
    String value = (valAttr == null ? null : (String) valAttr.getValue());

    AnnotationValue requiredAttr = attributes.get("required");
    Boolean required =
        (requiredAttr == null ? null : (Boolean) requiredAttr.getValue());

    AnnotationValue defaultAttr = attributes.get("defaultValue");
    String defaultValue =
        (defaultAttr == null ? null : (String) defaultAttr.getValue());

    QueryParam queryParam =
        new QueryParam(StringUtils.isNullOrEmpty(value) ? parameter.name()
            : value, required == null ? true : required,
            defaultValue == null ? null : defaultValue);
    // 合并枚举注释，如果有的话
    queryParam.setComment(ClassUtils.commentWithEnumIfAny(paramComment,
        parameter.type().asClassDoc()));
    queryParam.setType(type);
    return queryParam;
  }
}
