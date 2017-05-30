package com.dooioo.se.apidoclet.extension.spring.mvc.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.filter.TypeFilter;

/**
 * 忽略Rest接口中的一些参数,比如 HttpServletRequest
 */
public class ServletWebSkippedTypeFilter implements TypeFilter {
  private Set<String> ignoredTypes = new HashSet<>(Arrays.asList(
      "java.io.InputStream", "InputStream", "java.io.OutputStream",
      "OutputStream", "java.io.PrintWriter", "PrintWriter",
      "javax.servlet.http.HttpServletRequest", "HttpServletRequest",
      "javax.servlet.http.HttpServletResponse", "HttpServletResponse",
      "org.springframework.ui.Model", "Model"));

  @Override
  public boolean ignored(String qualifiedTypeName, ApiDocletOptions options) {
    return ignoredTypes.contains(qualifiedTypeName);
  }

}
