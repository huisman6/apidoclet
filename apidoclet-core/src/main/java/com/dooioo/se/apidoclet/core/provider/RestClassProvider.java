package com.dooioo.se.apidoclet.core.provider;

import java.util.Date;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.util.AnnotationUtils;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.JavaAnnotations;
import com.dooioo.se.apidoclet.model.JavaAnnotations.JavaAnnotation;
import com.dooioo.se.apidoclet.model.JavaDocTags;
import com.dooioo.se.apidoclet.model.JavaDocTags.JavaDocTag;
import com.dooioo.se.apidoclet.model.RestClass;
import com.dooioo.se.apidoclet.model.util.StandardDocTag;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

/**
 * 解析RestClass的基本信息、Java 注解、JavaDoc 注释
 */
public final class RestClassProvider {
  public RestClass produce(ClassDoc classDoc, ApiDocletOptions options) {
    RestClass spiClass = new RestClass();
    spiClass.setBuildAt(new Date());
    String className = classDoc.qualifiedTypeName();
    String packageName = className.substring(0, className.lastIndexOf("."));
    // 类名，包名
    spiClass.setClassName(className);
    spiClass.setDescription(StringUtils.trim(classDoc.commentText()));
    spiClass.setPackageName(packageName);

    // summary 类的简介，最好不超过20字，用于展示
    Tag[] summaryTags = classDoc.tags(StandardDocTag.TAG_SUMMARY);
    if (summaryTags == null || summaryTags.length == 0) {
      spiClass.setSummary(classDoc.simpleTypeName());
    } else {
      spiClass.setSummary(StringUtils.trim(summaryTags[0].text()));
    }

    boolean deprecated =
        AnnotationUtils.isPresent(classDoc.annotations(), Deprecated.class.getName());
    // 是否废弃的注释
    Tag[] deprecatedTags = classDoc.tags(StandardDocTag.TAG_DEPREACTED);
    if (deprecatedTags != null && deprecatedTags.length > 0) {
      deprecated = true;
      spiClass.setDeprecatedComment(StringUtils.trim(deprecatedTags[0].text()));
    }
    if (deprecated) {
      spiClass.setDeprecatedDate(new Date());
    }

    // 解析类上的其他注释
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

    // 解析类上的注解
    AnnotationDesc[] annotationDescs = classDoc.annotations();
    if (annotationDescs != null && annotationDescs.length > 0) {
      JavaAnnotations javaAnnotations = new JavaAnnotations();
      for (AnnotationDesc annotationDesc : annotationDescs) {
        // 解析所有注解
        JavaAnnotation javaAnnotation =
            new JavaAnnotation(AnnotationUtils.attributesFor(annotationDesc));
        javaAnnotation.setQualifiedClassName(annotationDesc.annotationType().qualifiedTypeName());
        javaAnnotations.add(javaAnnotation);
      }
      spiClass.setClassAnnotations(javaAnnotations);
    }
    return spiClass;
  }
}
