package org.apidoclet.extension.spring.mvc.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.filter.TypeFilter;

/**
 * ignored types
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
