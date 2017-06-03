package org.apidoclet.core.doc;

import org.apidoclet.core.ApiDocletOptions;

import com.sun.javadoc.ClassDoc;

/**
 * class based  javadoc filter
 */
public interface ClassDocFilter {
  boolean shouldSkip(ClassDoc classDoc, ApiDocletOptions options);
}


