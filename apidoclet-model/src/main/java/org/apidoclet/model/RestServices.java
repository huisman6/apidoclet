package org.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合多个{@code RestService}，是当前项目所有服务的抽象，
 * 通常情况下，只有一个项目只有单个服务。
 * 
 * @author huisman
 */
public class RestServices implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 当前项目的所有微服务
   */
  private List<RestService> apps = new ArrayList<RestService>();

  /**
   * 当前项目的所有的model
   */
  private List<ModelInfo> modelInfos;
  /**
   * 当前项目的所有业务码
   */
  private List<BizCode> bizCodes;

  /**
   * 当前项目的所有枚举
   */
  private List<EnumInfo> enumInfos;

  public List<RestService> getApps() {
    return apps;
  }

  public void setApps(List<RestService> apps) {
    if (apps != null && apps.size() > 0) {
      this.apps.addAll(apps);
    }
  }

  public void addApp(RestService app) {
    if (app != null) {
      this.apps.add(app);
    }
  }
  public List<ModelInfo> getModelInfos() {
    return modelInfos;
  }

  public void setModelInfos(List<ModelInfo> modelInfos) {
    this.modelInfos = modelInfos;
  }

  public List<BizCode> getBizCodes() {
    return bizCodes;
  }

  public void setBizCodes(List<BizCode> bizCodes) {
    this.bizCodes = bizCodes;
  }

  public List<EnumInfo> getEnumInfos() {
    return enumInfos;
  }

  public void setEnumInfos(List<EnumInfo> enumInfos) {
    this.enumInfos = enumInfos;
  }

  @Override
  public String toString() {
    return "RestApps [apps=" + apps + "]";
  }
}
