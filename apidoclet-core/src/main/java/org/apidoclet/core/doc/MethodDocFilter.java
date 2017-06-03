package org.apidoclet.core.doc;

import org.apidoclet.core.ApiDocletOptions;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * method based javadoc filter
 */
public interface MethodDocFilter {
    boolean shouldSkip(ClassDoc classDoc,MethodDoc methodDoc,ApiDocletOptions apiDocletOptions);
}


