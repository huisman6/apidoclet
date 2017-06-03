package org.apidoclet.core.provider;

import java.util.Date;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.JavaAnnotations;
import org.apidoclet.model.JavaDocTags;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.JavaAnnotations.JavaAnnotation;
import org.apidoclet.model.JavaDocTags.JavaDocTag;
import org.apidoclet.model.util.StandardDocTag;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

/**
 * parse the common metadata/javadoc tags/annotations of rest class 
 */
public final class RestClassProvider {
  public RestClass produce(ClassDoc classDoc, ApiDocletOptions options) {
    RestClass spiClass = new RestClass();
    String className = classDoc.qualifiedTypeName();
    String packageName = className.substring(0, className.lastIndexOf("."));
    // className,package info
    spiClass.setClassName(className);
    spiClass.setDescription(StringUtils.trim(classDoc.commentText()));
    spiClass.setPackageName(packageName);

    // class summary - for web ui display
    Tag[] summaryTags = classDoc.tags(StandardDocTag.TAG_SUMMARY);
    if (summaryTags == null || summaryTags.length == 0) {
      spiClass.setSummary(classDoc.simpleTypeName());
    } else {
      spiClass.setSummary(StringUtils.trim(summaryTags[0].text()));
    }

    boolean deprecated =
        AnnotationUtils.isPresent(classDoc.annotations(),
            Deprecated.class.getName());
    // deprecated tag exists?
    Tag[] deprecatedTags = classDoc.tags(StandardDocTag.TAG_DEPREACTED);
    if (deprecatedTags != null && deprecatedTags.length > 0) {
      deprecated = true;
      spiClass.setDeprecatedComment(StringUtils.trim(deprecatedTags[0].text()));
    }
    if (deprecated) {
      spiClass.setDeprecatedDate(new Date());
    }

    // other java doc tags
    Tag[] allTags = classDoc.tags();
    JavaDocTags docTags = new JavaDocTags();
    if (allTags != null && allTags.length > 0) {
      for (Tag tag : allTags) {
        if (StandardDocTag.TAG_DEPREACTED.equalsIgnoreCase(tag.kind())
            || StandardDocTag.TAG_SUMMARY.equalsIgnoreCase(tag.kind())) {
          continue;
        }
        docTags.add(new JavaDocTag(tag.kind(), tag.name(), tag.text()));
      }
    }
    spiClass.setAdditionalTags(docTags);

    // all java annotations on this class
    AnnotationDesc[] annotationDescs = classDoc.annotations();
    if (annotationDescs != null && annotationDescs.length > 0) {
      JavaAnnotations javaAnnotations = new JavaAnnotations();
      for (AnnotationDesc annotationDesc : annotationDescs) {
        JavaAnnotation javaAnnotation =
            new JavaAnnotation(AnnotationUtils.attributesFor(annotationDesc));
        javaAnnotation.setQualifiedClassName(annotationDesc.annotationType()
            .qualifiedTypeName());
        javaAnnotations.add(javaAnnotation);
      }
      spiClass.setClassAnnotations(javaAnnotations);
    }
    return spiClass;
  }
}
