package com.dooioo.se.apidoclet.spring.mvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.filter.SkippedTypeFilter;
import com.sun.javadoc.Type;

/**
 * 忽略Rest接口中的一些参数,比如 HttpServletRequest
 */
public class ServletWebSkippedTypeFilter implements SkippedTypeFilter {
  private Set<String> ignoredTypes = new HashSet<>(Arrays.asList("java.io.InputStream",
      "java.io.OutputStream", "java.io.PrintWriter", "javax.servlet.http.HttpServletRequest",
      "javax.servlet.http.HttpServletResponse", "org.springframework.ui.Model"));

  @Override
  public boolean ignored(Type type, ApiDocletOptions options) {
    return ignoredTypes.contains(type.qualifiedTypeName());
  }

}
