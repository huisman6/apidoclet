package com.dooioo.se.apidoclet.extension.spring.mvc.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.provider.EndpointMappingProvider;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.Consume;
import com.dooioo.se.apidoclet.model.EndpointMapping;
import com.dooioo.se.apidoclet.model.EndpointMapping.HeaderCondition;
import com.dooioo.se.apidoclet.model.EndpointMapping.ParamCondition;
import com.dooioo.se.apidoclet.model.HttpMethod;
import com.dooioo.se.apidoclet.model.JavaAnnotations;
import com.dooioo.se.apidoclet.model.JavaAnnotations.AnnotationValue;
import com.dooioo.se.apidoclet.model.Produce;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.SourcePosition;

/**
 * 解析映射信息
 */
public class RequestMappingEndpointMappingProvider implements
    EndpointMappingProvider {

  @Override
  @SuppressWarnings("unchecked")
  public EndpointMapping produce(AnnotationDesc[] classOrMethodAnnotationDescs,
      ApiDocletOptions options, SourcePosition position) {
    AnnotationDesc requestMapping =
        AnnotationUtils.searchFirst(classOrMethodAnnotationDescs,
            RequestMapping.class.getName());
    if (requestMapping == null) {
      return null;
    }
    EndpointMapping mapping = new EndpointMapping();
    Map<String, JavaAnnotations.AnnotationValue> attributes =
        AnnotationUtils.attributesFor(requestMapping);
    AnnotationValue urlAttr = attributes.get("value");
    // 数组值是以annotationValue封装的，我们会将数组转换为List<
    List<String> urls =
        (urlAttr == null ? null : (List<String>) urlAttr.getValue());
    String requestPath = "";
    if (urls == null || urls.isEmpty()) {
      options.getDocReporter().printWarning(position,
          "NOT FOUND request mapping urls ");
    } else {
      if (urls.size() > 1) {
        options.getDocReporter().printWarning(position,
            "FOUND multiple url mapping , will use first url");
      }
      // 基本类型，可直接toString，我们只取第一个路径
      requestPath = (String) urls.get(0);
    }
    mapping.setPath(requestPath);

    // 判断请求方法
    // 数组值是以annotationValue封装的
    AnnotationValue methodAttr = attributes.get("method");
    List<String> methods =
        (methodAttr == null ? null : (List<String>) methodAttr.getValue());
    if (methods == null || methods.isEmpty()) {
      mapping.setMethod(HttpMethod.of("GET"));
      options.getDocReporter().printWarning(position,
          " 没有指明 RequestMethod，默认使用GET");
    } else {
      if (methods.size() > 1) {
        options.getDocReporter().printWarning(position,
            "RequestMapping指定了多个RequestMethod，将选定第一个。");
      }
      // 枚举对象返回 FieldDoc
      mapping.setMethod(HttpMethod.of(methods.get(0)));
    }
    // 解析条件参数params,headers,consumes,produces
    resolveConditionParams(attributes, mapping);
    return mapping;
  }

  /**
   * 解析条件参数，never return null;
   * 
   * @author huisman
   * @param attributes
   * @since 2016年1月15日
   */
  @SuppressWarnings("unchecked")
  private void resolveConditionParams(Map<String, AnnotationValue> attributes,
      EndpointMapping mapping) {
    List<ParamCondition> paramConditions = new ArrayList<>();
    // params,headers,consumes,produces
    AnnotationValue paramAttr = attributes.get("params");
    List<String> params =
        (paramAttr == null ? null : (List<String>) paramAttr.getValue());
    if (params != null && !params.isEmpty()) {
      for (String annotationValue : params) {
        // condition query param ，可能值： param={"userCode","cityId=31000"}
        String raw = annotationValue;
        if (StringUtils.isNullOrEmpty(raw)) {
          continue;
        }
        // 查询字符串
        ParamCondition cp = new ParamCondition();
        String[] kv = raw.split(ParamCondition.DEFAULT_SPLIT_CHAR);
        cp.setName(kv[0]);
        if (kv.length > 1) {
          cp.setValue(kv[1]);
        }
        paramConditions.add(cp);
      }
    }

    List<HeaderCondition> headerConditions = new ArrayList<>();
    // headers,consumes,produces
    AnnotationValue headerAttr = attributes.get("headers");
    List<String> headers =
        (headerAttr == null ? null : (List<String>) headerAttr.getValue());
    if (headers != null && headers.size() > 0) {
      for (String raw : headers) {
        // condition header param ，可能值： headers={"X-Login-UserCode","X-Company-Id=1"}
        if (StringUtils.isNullOrEmpty(raw)) {
          continue;
        }
        // request header
        HeaderCondition cp = new HeaderCondition();
        String[] kv = raw.split(HeaderCondition.DEFAULT_SPLIT_CHAR);
        cp.setName(kv[0]);
        if (kv.length > 1) {
          cp.setValue(kv[1]);
        }
        headerConditions.add(cp);
      }
    }

    List<Consume> consumeConditions = new ArrayList<>();
    // consumes,produces
    AnnotationValue consumeAttr = attributes.get("consumes");
    List<String> consumes =
        (consumeAttr == null ? null : (List<String>) consumeAttr.getValue());
    if (consumes != null && consumes.size() > 0) {
      for (String annotationValue : consumes) {
        // condition request content-type ，可能值： consumes={"application/json"}
        // consumes header
        consumeConditions.add(Consume.of(annotationValue));
      }
    }

    // produces
    List<Produce> produceConditions = new ArrayList<>();
    AnnotationValue produceAttr = attributes.get("produces");
    List<String> produces =
        (produceAttr == null ? null : (List<String>) produceAttr.getValue());
    if (produces != null && produces.size() > 0) {
      for (String annotationValue : produces) {
        // condition response content-type ，可能值： produces={"application/json"}
        // response content-type header
        produceConditions.add(Produce.of(annotationValue));
      }
    }

    mapping.setRequstParamConditions(paramConditions);
    mapping.setRequestHeaderConditions(headerConditions);
    mapping.setConsumes(consumeConditions);
    mapping.setProduces(produceConditions);
  }

}
