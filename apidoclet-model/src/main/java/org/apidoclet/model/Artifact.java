package org.apidoclet.model;

import java.io.Serializable;

/**
 * Maven artifact description,maybe any dependency management artifact，include README.MD 
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public class Artifact implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * readme file name
   */
  private String readmeFileName;
  /**
   * README.MD content, extra project description
   */
  private String readmeFileContent;

  /**
   * artifact coordinate: groupId
   */
  private String groupId;
  /**
   * artifact coordinate: artifactId
   */
  private String artifactId;
  /**
   * artifact coordinate: release version
   */
  private String version;
  
  public String getReadmeFileName() {
    return readmeFileName;
  }

  public void setReadmeFileName(String readmeFileName) {
    this.readmeFileName = readmeFileName;
  }

  public String getReadmeFileContent() {
    return readmeFileContent;
  }

  public void setReadmeFileContent(String readmeFileContent) {
    this.readmeFileContent = readmeFileContent;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "Artifact [readmeFileName=" + readmeFileName
        + ", readmeFileContent=" + readmeFileContent + ", groupId=" + groupId
        + ", artifactId=" + artifactId + ", version=" + version + "]";
  }

}
