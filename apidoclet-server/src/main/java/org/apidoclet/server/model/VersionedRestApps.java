package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apidoclet.model.RestService;
import org.apidoclet.model.RestServices;
import org.springframework.util.StringUtils;

public class VersionedRestApps implements Serializable {
  private static final long serialVersionUID = 1L;
  // default sort by imported order
  private Map<String, VersionedRestApp> restAppMap = new LinkedHashMap<>();

  public VersionedRestApps() {
    super();
  }

  public void addRestApps(RestServices restApps) {
    if (restApps != null) {
      List<RestService> apps = restApps.getApps();
      if (apps != null && !apps.isEmpty()) {
        for (RestService restApp : apps) {
          VersionedRestApp app =
              new VersionedRestApp(restApp, restApps.getModelInfos(),
                  restApps.getBizCodes(), restApps.getEnumInfos());
          VersionedRestApp existsApp = this.restAppMap.get(app.getId());

          // exists? => merge
          if (existsApp != null) {
            mergeRestApp(app, existsApp);
          }
          // keep up to date
          this.restAppMap.put(app.getId(), app);
        }
      }
    }
  }

  /**
   * merge app
   */
  private void mergeRestApp(VersionedRestApp newApp, VersionedRestApp oldApp) {
    RestService newOriginal = newApp.getOriginal();
    RestService oldOriginal = oldApp.getOriginal();
    // update last build at
    newOriginal.setLastBuildAt(newOriginal.getBuildAt());
    // remain first build at
    newOriginal.setBuildAt(oldOriginal.getBuildAt());

    Map<String, VersionGroupedRestClass> oldRestClassMap =
        oldApp.getIdToSpiClassMap();
    if (oldRestClassMap.isEmpty()) {
      return;
    }
    Map<String, VersionGroupedRestClass> newRestClassMap =
        newApp.getIdToSpiClassMap();
    if (newRestClassMap.isEmpty()) {
      return;
    }

    for (String restClassId : newRestClassMap.keySet()) {
      VersionGroupedRestClass oldRestClass = oldRestClassMap.get(restClassId);
      if (oldRestClass == null) {
        continue;
      }
      VersionGroupedRestClass newRestClass = newRestClassMap.get(restClassId);
      // exits? update build at
      newRestClass.getOriginal().setLastBuildAt(
          newRestClass.getOriginal().getBuildAt());
      newRestClass.getOriginal().setBuildAt(
          oldRestClass.getOriginal().getBuildAt());

      // merge methods
      mergeSpiMethod(newRestClass, oldRestClass);
    }

  }

  /**
   * merge rest class methods
   */
  private void mergeSpiMethod(VersionGroupedRestClass newRestClass,
      VersionGroupedRestClass oldRestClass) {
    // 检查方法的since 等
    Map<String, List<VersionedRestMethod>> newMethodMap =
        newRestClass.getMethodMap();
    if (newMethodMap == null || newMethodMap.isEmpty()) {
      return;
    }
    for (String version : newMethodMap.keySet()) {
      List<VersionedRestMethod> newMethods = newMethodMap.get(version);
      if (newMethods != null && newMethods.size() > 0) {
        for (VersionedRestMethod newMethod : newMethods) {
          VersionedRestMethod oldMethod =
              oldRestClass.getMethod(newMethod.getId());
          if (oldMethod != null) {
            // since not set?
            if (StringUtils.hasText(oldMethod.getOriginal().getSince())) {
              newMethod.getOriginal().setSince(
                  oldMethod.getOriginal().getSince());
            }
          }
        }
      }
    }
  }


  public VersionedRestApp findById(String id) {
    return this.restAppMap.get(id);
  }

  public List<String> getAppIds() {
    return new ArrayList<>(this.restAppMap.keySet());
  }

  public void removeByAppId(String appId) {
    this.restAppMap.remove(appId);
  }

  public List<VersionedRestApp> getAllApps() {
    return new ArrayList<>(this.restAppMap.values());
  }

}
