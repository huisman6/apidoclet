package org.apidoclet.server.helper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apidoclet.model.HeaderParam;
import org.apidoclet.model.PathParam;
import org.apidoclet.model.QueryParam;
import org.apidoclet.model.RestClass;
import org.apidoclet.server.model.PostManFolder;
import org.apidoclet.server.model.PostManMain;
import org.apidoclet.server.model.PostManRequest;
import org.apidoclet.server.model.VersionGroupedRestClass;
import org.apidoclet.server.model.VersionedRestApp;
import org.springframework.util.StringUtils;

import com.google.common.hash.Hashing;

/**
 * Created with IntelliJ IDEA. PackageName:com.dooioo.se.lorik.apidoclet.server.helper
 *
 * @Author: Jerry.hu
 * @Create: Jerry.hu (2016-02-01 15:21)
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 * @Description: To change this template use File | Settings | File Templates.
 */
public class PostManCollectionHelper {

  List<PostManRequest> postManRequests = new ArrayList<>();
  /**
   * 是否使用占位符，使用占位符，那么所有pathvariable,param都会用占位符表示
   */
  private boolean usePlaceholder = true;
  private boolean ignoreVirtualPath = false;
  private String host;
  /**
   * 占位符
   */
  private String leftPlaceholder = "{{";
  /**
   * 右占位符
   */
  private String rightPlaceholder = "}}";

  public PostManCollectionHelper(boolean userPlaceholder,
      boolean ignoreVirtualPath, String host) {
    super();
    this.usePlaceholder = userPlaceholder;
    this.ignoreVirtualPath = ignoreVirtualPath;
    this.host = host;
  }

  private PostManMain initPostMan(VersionedRestApp restApp) {
    PostManMain postManMain = new PostManMain();
    postManMain.setName(restApp.getOriginal().getAppName());
    postManMain.setDescription(restApp.getOriginal().getAppName());
    postManMain.setId(Hashing.md5()
        .hashString(restApp.getOriginal().getApp(), StandardCharsets.UTF_8)
        .toString());
    postManMain.setOwner(restApp.getOriginal().getApp());
    postManMain.setTimestam(restApp.getLastBuiltAt().getTime());
    postManMain.setOrder(new String[] {});
    return postManMain;
  }

  public PostManMain getPostManMain(VersionedRestApp restApp) {
    PostManMain postManMain = initPostMan(restApp);
    postManMain.setFolders(formatPostManFolder(restApp));
    postManMain.setRequests(postManRequests);
    return postManMain;
  }

  public PostManMain getPostManMain(VersionedRestApp restApp,
      VersionGroupedRestClass restClass) {
    PostManMain postManMain = initPostMan(restApp);
    postManMain.setFolders(formatPostManFolder(restApp, restClass));
    postManMain.setRequests(postManRequests);
    return postManMain;
  }

  public PostManMain getPostManMain(VersionedRestApp restApp,
      VersionGroupedRestClass restClass, RestClass.Method method) {
    PostManMain postManMain = initPostMan(restApp);
    postManMain.setFolders(formatPostManFolder(restApp, restClass, method));
    postManMain.setRequests(postManRequests);
    return postManMain;
  }



  private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp) {
    Map<String, VersionGroupedRestClass> spiClassMap =
        restApp.getIdToSpiClassMap();
    List<PostManFolder> postManFolders = new ArrayList<>();
    for (Entry<String, VersionGroupedRestClass> entry : spiClassMap.entrySet()) {
      VersionGroupedRestClass spiClass = entry.getValue();
      PostManFolder postManFolder = initPostManFolder(spiClass);
      postManFolder.setOwner(restApp.getOriginal().getApp());
      postManFolder.setOrder(formatPostManRequestAndOrder(spiClass
          .getOriginal().getMethods(), restApp));
      postManFolders.add(postManFolder);
    }

    return postManFolders;
  }

  public boolean isUsePlaceholder() {
    return usePlaceholder;
  }



  public boolean isIgnoreVirtualPath() {
    return ignoreVirtualPath;
  }

  public String getHost() {
    return host;
  }

  private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp,
      VersionGroupedRestClass restClass) {
    List<PostManFolder> postManFolders = new ArrayList<>();
    PostManFolder postManFolder = initPostManFolder(restClass);
    postManFolder.setOwner(restApp.getOriginal().getApp());
    postManFolder.setOrder(formatPostManRequestAndOrder(restClass.getOriginal()
        .getMethods(), restApp));
    return postManFolders;
  }

  private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp,
      VersionGroupedRestClass restClass, RestClass.Method method) {
    List<PostManFolder> postManFolders = new ArrayList<>();
    PostManFolder postManFolder = initPostManFolder(restClass);
    postManFolder.setOwner(restApp.getOriginal().getApp());
    postManFolder.setOrder(formatPostManRequestAndOrder(method, restApp));
    return postManFolders;
  }


  private PostManFolder initPostManFolder(VersionGroupedRestClass restClass) {
    PostManFolder postManFolder = new PostManFolder();
    postManFolder.setId(Hashing
        .md5()
        .hashString(restClass.getOriginal().getClassName(),
            StandardCharsets.UTF_8).toString());
    postManFolder.setName(restClass.getOriginal().getSummary());
    postManFolder.setDescription(restClass.getOriginal().getDescription());
    return postManFolder;
  }

  private List<String> formatPostManRequestAndOrder(
      List<RestClass.Method> restMethods, VersionedRestApp restApp) {
    List<String> order = new ArrayList<>();
    for (RestClass.Method method : restMethods) {
      order.add(Hashing.md5()
          .hashString(method.getIdentity(), StandardCharsets.UTF_8).toString());
      postManRequests.add(initPostManRequest(method, restApp));
    }
    return order;
  }

  private List<String> formatPostManRequestAndOrder(
      RestClass.Method restMethod, VersionedRestApp restApp) {
    List<String> order = new ArrayList<>();
    order.add(Hashing.md5()
        .hashString(restMethod.getIdentity(), StandardCharsets.UTF_8)
        .toString());
    postManRequests.add(initPostManRequest(restMethod, restApp));
    return order;
  }

  private PostManRequest initPostManRequest(RestClass.Method method,
      VersionedRestApp restApp) {
    PostManRequest postManRequest = new PostManRequest();
    postManRequest.setId(Hashing.md5()
        .hashString(method.getIdentity(), StandardCharsets.UTF_8).toString());
    postManRequest.setName(method.getSummary());
    postManRequest.setDescription(method.getDescription());
    postManRequest.setFolder(Hashing.md5()
        .hashString(restApp.getOriginal().getApp(), StandardCharsets.UTF_8)
        .toString());
    postManRequest.setMethod(method.getMapping().getMethod().getName());
    postManRequest.setPathVariables(getPathParams(method));
    postManRequest.setHeaders(getHeaders(method));
    postManRequest.setUrl(getUrl(restApp, postManRequest, method));
    postManRequest.setCollectionId(postManRequest.getFolder());
    return postManRequest;
  }

  /**
   * 设置post 请求的头部参数，目前暂时支持application/json、application/x-www-form-urlencoded
   *
   * @param spiMethod
   * @return
   */
  private String getHeaders(RestClass.Method restMethod) {
    String methodStr =
        restMethod.getMapping().getMethod().getName().toUpperCase();
    StringBuilder sb = new StringBuilder();
    if (methodStr.equals("GET") || methodStr.equals("DELETE")) {
      sb.append("Content-Type: application/json");
    } else {
      sb.append("Content-Type:application/x-www-form-urlencoded");
    }
    List<HeaderParam> requestHeaders = restMethod.getRequestHeaders();
    for (HeaderParam requestHeader : requestHeaders) {
      String defaultValue = requestHeader.getDefaultValue();
      sb.append("\n")
          .append(requestHeader.getName())
          .append(":")
          .append(
              this.isUsePlaceholder() ? getPlaceholderVariable(requestHeader
                  .getName())
                  : (StringUtils.hasText(defaultValue) ? defaultValue : ""));
    }
    return sb.toString();
  }

  private String getPlaceholderVariable(String variableName) {
    return this.leftPlaceholder + variableName + this.rightPlaceholder;
  }

  /**
   * 获取请求的url
   *
   * @param restApp 当前的服务名称
   * @param request postman 请求的参数
   * @param spiMethod 微服务中方法
   * @return
   */
  @SuppressWarnings("unchecked")
  private String getUrl(VersionedRestApp restApp, PostManRequest request,
      RestClass.Method restMethod) {
    String url = this.host + restMethod.getMapping().getPath();
    String methodStr =
        restMethod.getMapping().getMethod().getName().toUpperCase();
    Map<String, Object> requestParamMap = getRequestParams(restMethod);
    if (methodStr.equals("GET") || methodStr.equals("DELETE")) {
      request.setDataMode("params");
      if (requestParamMap != null) {
        url += requestParamMap.get("get");
      }
    } else {
      request.setDataMode("urlencoded");
      if (requestParamMap != null) {
        request.setData((List<PostManRequest.PostData>) requestParamMap
            .get("post"));
      }
    }
    if (usePlaceholder) {
      return url.replace("{", this.leftPlaceholder).replace("}",
          this.rightPlaceholder);
    } else {
      return url;
    }
  }

  /**
   * 获取url 中的路径参数
   *
   * @param spiMethod
   * @return
   */
  private static Object getPathParams(RestClass.Method restMethod) {
    List<PathParam> pathParams = restMethod.getPathParams();
    Map<String, String> pathParamMap = new HashMap<>();
    for (PathParam pathParam : pathParams) {
      pathParamMap.put(pathParam.getName(), pathParam.getComment());
    }
    return pathParamMap;
  }

  /**
   * 获取请求的参数，暂时支持post表单，和get url params 模式
   *
   * @param original
   * @return
   */
  private Map<String, Object> getRequestParams(RestClass.Method original) {
    StringBuilder getSB = new StringBuilder("?");
    Map<String, Object> queryMap = new HashMap<>();
    List<PostManRequest.PostData> postDatas = new ArrayList<>();
    List<QueryParam> queryParams = original.getQueryParams();
    if (queryParams == null || queryParams.size() == 0) {
      return null;
    }
    for (QueryParam queryParam : original.getQueryParams()) {
      if (this.usePlaceholder) {
        getSB.append(queryParam.getName()).append("=").append("{")
            .append(queryParam.getName()).append("}").append("&");
      } else {
        getSB
            .append(queryParam.getName())
            .append("=")
            .append(
                StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam
                    .getDefaultValue() : "").append("&");
      }

      PostManRequest.PostData postData = new PostManRequest.PostData();
      postData.setKey(queryParam.getName());
      if (usePlaceholder) {
        postData.setValue(getPlaceholderVariable(queryParam.getName()));
      } else {
        postData
            .setValue(StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam
                .getDefaultValue() : "");
      }
      postDatas.add(postData);
    }
    queryMap.put("get", getSB.substring(0, getSB.lastIndexOf("&")));
    queryMap.put("post", postDatas);
    return queryMap;
  }

}
