package org.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apidoclet.model.config.ApiDocJson;

/**
 * Rest微服务，对于Spring-Cloud-Netflix来说，注册在Eureka里的 VipAddress（虚拟主机）唯一标识一个服务， 对于其他服务化架构来说，可以看做服务的注册名。
 * 
 * @author huisman
 */
public class RestService implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * REST默认版本号
   */
  public static final String DEFAULT_VERSION = "v0";
  /**
   * request path中的版本号前缀
   */
  public static final String VERSION_PREFIX_IN_PATH = "/v";

  /**
   * 服务打包后的jar信息
   */
  private Artifact artifact;
  /**
   * app的名称，唯一标识一个APP，一般是FeignClient的value
   */
  private String app;
  /**
   * app的说明，一般显示在文档中。
   */
  private String appName;

  /**
   * 谁构建的，一般取 System里的环境变量
   */
  private String buildBy;
  /**
   * 构建者的IP地址
   */
  private String ipAddress;

  /**
   * 微服务的所有SPI或者SpringMVC的Controller
   */
  private List<RestClass> restClasses = new ArrayList<>();

  /**
   * 此微服务的SPI model
   */
  private List<ModelInfo> modelInfos;
  /**
   * 此微服务的所有业务码
   */
  private Set<BizCode> bizCodes;

  /**
   * 微服务的所有枚举值
   */
  private List<EnumInfo> enumInfos;

  /**
   * apidoc的一些配置，比如环境参数
   */
  private ApiDocJson apiDocJson;

  /**
   * 
   * @return the apiDocJson
   */
  public ApiDocJson getApiDocJson() {
    return apiDocJson;
  }

  /**
   * 
   * @param apiDocJson
   */
  public void setApiDocJson(ApiDocJson apiDocJson) {
    this.apiDocJson = apiDocJson;
  }


  /**
   * SPI jar的一些信息，包括readme信息
   * 
   * @return the artifact
   */
  public Artifact getArtifact() {
    return artifact;
  }


  /**
   * @param artifact
   */
  public void setArtifact(Artifact artifact) {
    this.artifact = artifact;
  }


  public RestService() {
    super();
  }

  public RestService(String app, String appName) {
    super();
    this.appName = appName;
    this.app = app;
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

  public List<RestClass> getRestClasses() {
    return this.restClasses;
  }


  public void addSpiClass(RestClass spiClass) {
    if (spiClass != null) {
      this.restClasses.add(spiClass);
    }
  }

  public void setSpiClasses(List<RestClass> spiClasses) {
    if (spiClasses != null) {
      this.restClasses.addAll(spiClasses);
    }
  }

  public List<ModelInfo> getModelInfos() {
    return modelInfos;
  }

  public void setModelInfos(List<ModelInfo> modelInfos) {
    this.modelInfos = modelInfos;
  }

  public Set<BizCode> getBizCodes() {
    return bizCodes;
  }

  public void setBizCodes(Set<BizCode> bizCodes) {
    this.bizCodes = bizCodes;
  }

  public List<EnumInfo> getEnumInfos() {
    return enumInfos;
  }

  public void setEnumInfos(List<EnumInfo> enumInfos) {
    this.enumInfos = enumInfos;
  }


  public String getBuildBy() {
    return buildBy;
  }

  public void setBuildBy(String buildBy) {
    this.buildBy = buildBy;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  @Override
  public String toString() {
    final int maxLen = 90;
    return "RestApp [app=" + app + ", appName=" + appName + ", buildBy="
        + buildBy + ", ipAddress=" + ipAddress + ", spiClasses="
        + (restClasses != null ? toString(restClasses, maxLen) : null)
        + ", models="
        + (modelInfos != null ? toString(modelInfos, maxLen) : null)
        + ", bizCodes="
        + (bizCodes != null ? toString(bizCodes, maxLen) : null) + "]";
  }

  private String toString(Collection<?> collection, int maxLen) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    int i = 0;
    for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
        && i < maxLen; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(iterator.next());
    }
    builder.append("]");
    return builder.toString();
  }



}
