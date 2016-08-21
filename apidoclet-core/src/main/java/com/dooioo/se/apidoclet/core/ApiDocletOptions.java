package com.dooioo.se.apidoclet.core;

import java.util.HashMap;
import java.util.Map;

import com.dooioo.se.lorik.apidoclet.contract.model.RestApp;
import com.dooioo.se.lorik.apidoclet.util.StringUtils;
import com.sun.javadoc.DocErrorReporter;

/**
 * API doclet的配置
 * 
 * @author huisman
 * @version 1.0.0
 * @since 2016年1月16日 Copyright (c) 2016, BookDao All Rights Reserved.
 */
public class ApiDocletOptions {
  /**
   * 默认选项值里的分隔符
   */
  public static final String DEFAULT_SPLIT_STR = ",";

  /**
   * 所有选项必须以-开头
   */
  public static final String DEFAULT_PREFIX = "-";

  /**
   * 是否打印所有解析后的app信息
   */
  public static final String PRINT = DEFAULT_PREFIX + "print";

  /**
   * classes文件目录
   */
  public static final String CLASS_DIR = DEFAULT_PREFIX + "classdir";
  /**
   * 默认版本号
   */
  public static final String VERSION = DEFAULT_PREFIX + "version";
  
  /**
   * 工程根目录
   */
  public static final String PROJECT_ROOT_DIR=DEFAULT_PREFIX+"projectRootDir";
  
  /**
   *  jar groupId
   */
  public static final String ARTIFACT_GROUP_ID=DEFAULT_PREFIX+"artifactGroupId";
  
  /**
   *  jar artifactId
   */
  public static final String ARTIFACT_ID=DEFAULT_PREFIX+"artifactId";
  
  /**
   *  jar ARTIFACT_VERSION
   */
  public static final String ARTIFACT_VERSION=DEFAULT_PREFIX+"artifactVersion";

  /**
   * 是否忽略虚拟路径
   */
  public static final String IGNORE_VIRTUAL_PATH = DEFAULT_PREFIX + "ignoreVirtualPath";
  /**
   * 源文件路径，java doc标注参数
   */
  public static final String SOURCE_PATH = "-sourcepath";
  /**
   * app名称 {@link RestApp#getName()}
   */
  public static final String APP = DEFAULT_PREFIX + "app";
  /**
   * 默认app 描述 {@link RestApp#getDesc()}
   */
  public static final String APP_NAME = DEFAULT_PREFIX + "appName";
  /**
   * 导出url接口
   */
  public static final String EXPORT_TO = DEFAULT_PREFIX + "exportTo";

  private Map<String, String> othersOptions = new HashMap<>();
  /**
   * 源文件编译后classes所在目录，我们可能要从源文件中查找BizCode
   */
  private String classdir;
  private String exportTo;
  private String app;
  /**
   *  应用程序名
   */
  private String appName;
  /**
   *  是否忽略虚拟路径
   */
  private boolean ignoreVirtualPath;
  /**
   * 构建者的ip地址
   */
  private String buildIpAddress;
  /**
   * 被谁构建
   */
  private String buildBy;

  /**
   * 是否打印解析后的数据
   */
  private boolean print = false;


  /**
   * 默认版本号，如果没有指定@version
   */
  private String version;

  public String getBuildIpAddress() {
    return buildIpAddress;
  }


  public void setBuildIpAddress(String buildIpAddress) {
    this.buildIpAddress = buildIpAddress;
  }


  public String getBuildBy() {
    return buildBy;
  }


  public void setBuildBy(String buildBy) {
    this.buildBy = buildBy;
  }



  public boolean isIgnoreVirtualPath() {
    return ignoreVirtualPath;
  }


  public void setIgnoreVirtualPath(boolean ignoreVirtualPath) {
    this.ignoreVirtualPath = ignoreVirtualPath;
  }

  /**
   * 添加命令行参数
   */
  public void addOption(String key, String value) {
    if (StringUtils.isNullOrEmpty(key)) {
      return;
    }
    this.othersOptions.put(key, value);
  }


  /**
   * 获取特定选项的值
   * 
   * @author huisman
   * @version v1
   */
  public String optionValue(String key) {
    return this.othersOptions.get(key);
  }

  /**
   * ROOT doc
   */
  private DocErrorReporter docReporter;

  public String getClassdir() {
    return classdir;
  }

  public void setClassdir(String classdir) {
    this.classdir = classdir;
  }

  public DocErrorReporter getDocReporter() {
    return docReporter;
  }

  public String getExportTo() {
    return exportTo;
  }

  public void setExportTo(String exportTo) {
    this.exportTo = exportTo;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setDocReporter(DocErrorReporter docReporter) {
    this.docReporter = docReporter;
  }


  public boolean isPrint() {
    return print;
  }

  public void setPrint(boolean print) {
    this.print = print;
  }


  @Override
  public String toString() {
    return "ApiDocletOptions [classdir=" + classdir + ", exportTo=" + exportTo + ", app=" + app
        + ", appName=" + appName + ", ignoreVirtualPath=" + ignoreVirtualPath + ", buildIpAddress="
        + buildIpAddress + ", buildBy=" + buildBy + ", print=" + print + ", version=" + version
        + "]";
  }



}


