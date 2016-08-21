package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合多个{@code RestService}，是当前项目所有服务的抽象
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
   * 当前项目的所有SPI model
   */
  private List<ModelInfo> spiModels;
  /**
   * 当前项目的所有业务码
   */
  private List<BizCode> spiBizCodes;

  /**
   * 当前项目的所有枚举
   */
  private List<EnumInfo> spiEnums;

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

  public List<EnumInfo> getSpiEnums() {
    return spiEnums;
  }

  public void setSpiEnums(List<EnumInfo> spiEnums) {
    this.spiEnums = spiEnums;
  }

  public List<ModelInfo> getSpiModels() {
    return spiModels;
  }

  public void setSpiModels(List<ModelInfo> spiModels) {
    this.spiModels = spiModels;
  }

  public List<BizCode> getSpiBizCodes() {
    return spiBizCodes;
  }

  public void setSpiBizCodes(List<BizCode> spiBizCodes) {
    this.spiBizCodes = spiBizCodes;
  }

  @Override
  public String toString() {
    return "RestApps [apps=" + apps + "]";
  }
}
