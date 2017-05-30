package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apidoclet.model.RestClass;
import org.apidoclet.server.helper.DigestUtils;

/**
 * group rest methods by method version
 * 
 * @author huisman
 */
public class VersionGroupedRestClass implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * original rest class
   */
  private RestClass original;
  /**
   * rest class id
   */
  private String id;
  /**
   * {@code RestService#getApp()}
   */
  private String app;
  /**
   * version => methods
   */
  private Map<String, List<VersionedRestMethod>> methodMap = new HashMap<>();
  /**
   * {@code VersionedRestMethod#getId()} => method
   */
  private Map<String, VersionedRestMethod> idToMethodMap = new HashMap<>();

  public VersionGroupedRestClass() {
    super();

  }

  public VersionGroupedRestClass(RestClass original, String app) {
    super();
    if (original == null) {
      throw new IllegalArgumentException("spiclass original is null");
    }
    this.app = app;
    this.original = original;
    this.id = String.valueOf(DigestUtils.crc32(this.app + original.getClassName()));

    List<RestClass.Method> restMethods = original.getMethods();
    if (restMethods != null && !restMethods.isEmpty()) {
      for (RestClass.Method method : restMethods) {
        // group methods by version
        VersionedRestMethod versionAccessPoint = new VersionedRestMethod(this.app, method);
        String version = versionAccessPoint.getVersion();
        idToMethodMap.put(versionAccessPoint.getId(), versionAccessPoint);

        if (this.methodMap.containsKey(version)) {
          this.methodMap.get(version).add(versionAccessPoint);
        } else {
          List<VersionedRestMethod> accessPoints = new ArrayList<>();
          accessPoints.add(versionAccessPoint);
          this.methodMap.put(version, accessPoints);
        }
      }
    }
  }


  /**
   * spi class属于哪个微服务
   */
  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public void setOriginal(RestClass original) {
    this.original = original;
  }

  /**
   * spi 的ID
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * 版本号对应的所有SPI方法
   */
  public Map<String, List<VersionedRestMethod>> getMethodMap() {
    return this.methodMap;
  }



  /**
   * 返回原始的SPI class信息
   */
  public RestClass getOriginal() {
    return original;
  }


  /**
   * 当前spi class的ID
   */
  public String getId() {
    return id;
  }


  /**
   * 根据方法ID查找方法
   * 
   * @param methodId
   */
  public VersionedRestMethod getMethod(String methodId) {
    return this.idToMethodMap.get(methodId);
  }


}
