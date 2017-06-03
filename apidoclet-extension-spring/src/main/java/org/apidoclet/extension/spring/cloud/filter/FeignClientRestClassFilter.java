package org.apidoclet.extension.spring.cloud.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.filter.RestClassFilter;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;
import org.springframework.cloud.netflix.feign.FeignClient;

import com.sun.javadoc.ClassDoc;

/**
 * spring-cloud-netflix {@code FeignClient} interface support
 */
public class FeignClientRestClassFilter implements RestClassFilter {
  private static final Set<String> candidateFields = new HashSet<>(
      Arrays.asList("value", "name", "serviceId"));

  @Override
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    // @FeignClient annotation presents
    return AnnotationUtils.isPresent(classDoc.annotations(),
        FeignClient.class.getName());
  }

  @Override
  public String getServiceIdIfAny(ClassDoc classDoc, ApiDocletOptions options) {
    Map<String, AnnotationValue> attributes =
        AnnotationUtils.attributesFor(classDoc.annotations(),
            FeignClient.class.getName());
    if (attributes == null || attributes.isEmpty()) {
      return null;
    }
    for (String attribute : attributes.keySet()) {
      // find candidate field
      if (candidateFields.contains(attribute)) {
        String app = String.valueOf(attributes.get(attribute));
        if (StringUtils.isNullOrEmpty(app)) {
          continue;
        }
        return app;
      }
    }
    options.getDocReporter().printWarning(
        classDoc.position(),
        "found class:" + FeignClient.class.getName()
            + ",but can't resolve feignclient serviceId");
    return null;
  }

}
