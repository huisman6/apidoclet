package com.dooioo.se.apidoclet.extension.sh.lianjie.se.provider;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.provider.ModelProvider;
import org.apidoclet.core.util.ClassUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.ModelInfo;
import org.apidoclet.model.util.Types;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

/**
 * 基于命名约定或者根据包来确认哪些类是model.
 */
public class NameConventionOrPackageBasedModelProvider implements ModelProvider {
  /**
   * model在哪些包中，可通过命令行参数确定
   */
  private static final String modelPackagesOption = "-modelPackages";

  @Override
  public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
    if (classDoc == null || classDoc.isInterface() || classDoc.isAbstract()
        || classDoc.isAnnotationType() || classDoc.isException()
        || classDoc.isPrivate() || classDoc.isEnum()
        || !Types.isSimpleType(classDoc.qualifiedTypeName())) {
      return false;
    }
    PackageDoc pdoc = classDoc.containingPackage();
    // name convention
    if (pdoc.name().indexOf(".model")>0) {
      return true;
    }
    String pks = options.optionValue(modelPackagesOption);
    if (StringUtils.isNullOrEmpty(pks)) {
      return false;
    }
    String[] packages = pks.split(",");
    if (packages == null || packages.length == 0) {
      return false;
    }
    for (String option : packages) {
      if (pdoc.name().startsWith(option)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ModelInfo produce(ClassDoc classDoc, ApiDocletOptions options) {
    ModelInfo model = new ModelInfo();
    model.setClassName(classDoc.qualifiedTypeName());
    model.setFields(ClassUtils.getFieldInfos(classDoc, null));
    return model;
  }

}
