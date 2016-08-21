package com.dooioo.se.apidoclet.model;

import java.io.Serializable;

/**
 * maven Artifact 信息的抽象，同时包含readme.md/readme.txt的信息
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public class Artifact implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * readme 文件名
   */
  private String readmeFileName;
  /**
   * readme 文件内容，可用作API的一些说明
   */
  private String readmeFileContent;

  /**
   * 构件groupId
   */
  private String groupId;
  /**
   * 构件artifactId
   */
  private String artifactId;
  /**
   * 构件版本号
   */
  private String version;

  public Artifact() {
    super();
  }

  /**
   * @param readmeFileName
   */
  public void setReadmeFileName(String readmeFileName) {
    this.readmeFileName = readmeFileName;
  }

  /**
   * @param readmeFileContent
   */
  public void setReadmeFileContent(String readmeFileContent) {
    this.readmeFileContent = readmeFileContent;
  }

  /**
   * @param groupId
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * @param artifactId
   */
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  /**
   * @param version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * readme 文件名
   * 
   * @return the readmeFileName
   */
  public String getReadmeFileName() {
    return readmeFileName;
  }

  /**
   * readme 文件内容
   * 
   * @return the readmeFileContent
   */
  public String getReadmeFileContent() {
    return readmeFileContent;
  }

  /**
   * 构件groupId
   * 
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * 构件artifactId
   * 
   * @return the artifactId
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * 构件版本号
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }



}
