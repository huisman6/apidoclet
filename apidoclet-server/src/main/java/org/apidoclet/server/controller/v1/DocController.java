//package org.apidoclet.server.controller.v1;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.dooioo.se.lorik.apidoclet.contract.Types;
//import com.dooioo.se.lorik.apidoclet.contract.config.ApiDocJson;
//import com.dooioo.se.lorik.apidoclet.contract.model.FieldInfo;
//import com.dooioo.se.lorik.apidoclet.contract.model.QueryParam;
//import com.dooioo.se.lorik.apidoclet.contract.model.RequestHeader;
//import com.dooioo.se.lorik.apidoclet.contract.model.ReturnType;
//import com.dooioo.se.lorik.apidoclet.contract.model.SpiMethod;
//import com.dooioo.se.lorik.apidoclet.contract.model.TypeInfo;
//import com.dooioo.se.lorik.apidoclet.server.helper.JsonFormatTool;
//import com.dooioo.se.lorik.apidoclet.server.helper.PostManCollectionHelper;
//import com.dooioo.se.lorik.apidoclet.server.model.PostManEnvironment;
//import com.dooioo.se.lorik.apidoclet.server.model.PostManMain;
//import com.dooioo.se.lorik.apidoclet.server.model.VersionGroupedSpiClass;
//import com.dooioo.se.lorik.apidoclet.server.model.VersionedRestApp;
//import com.dooioo.se.lorik.apidoclet.server.model.VersionedSpiMethod;
//import com.dooioo.se.lorik.apidoclet.server.service.VersionGroupedAppService;
//import com.dooioo.se.lorik.core.web.result.Assert;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@Controller
//public class DocController {
//
//  /**
//   * 首页
//   * 
//   * @return
//   */
//  @RequestMapping(value = {"/"})
//  public String index() {
//    return "homepage";
//  }
//
//
//  @RequestMapping(value = "/v1/doc/{appId}/bizcode")
//  public String app(Model model, @PathVariable(value = "appId") String appId) {
//    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
//    Assert.found(restApp != null, "系统不存在");
//    model.addAttribute("currentApp", restApp);
//    model.addAttribute("spi", new VersionGroupedRestClass());
//    model.addAttribute("method", new VersionedRestMethod());
//    model.addAttribute("routing", "bizcode");
//    return "index";
//  }
//
//
//  @RequestMapping(value = "/v1/doc/{appId}/export/postman")
//  public String imortPostman(Model model, @PathVariable(value = "appId") String appId) {
//    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
//    Assert.found(restApp != null, "系统不存在");
//    model.addAttribute("currentApp", restApp);
//    model.addAttribute("spi", new VersionGroupedRestClass());
//    model.addAttribute("method", new VersionedRestMethod());
//    model.addAttribute("routing", "postman");
//    return "index";
//  }
//
//
//  /**
//   * 查看某个app的接口
//   * 
//   * @author huisman
//   * @version v1
//   * @param appId
//   * @param methodId
//   * @return
//   * @since 2016年1月18日
//   * @summary
//   */
//  @RequestMapping(value = "/v1/doc/{appId}/{spiClassId}/{methodId}")
//  public String methodDoc(@PathVariable(value = "appId") String appId,
//      @PathVariable(value = "spiClassId") String spiClassId,
//      @PathVariable(value = "methodId") String methodId, Model model) {
//    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
//    Assert.found(restApp != null, "系统不存在");
//
//    VersionGroupedRestClass spi = restApp.getSpi(spiClassId);
//    Assert.found(spi != null, "接口SPI不存在");
//
//    VersionedRestMethod method = spi.getMethod(methodId);
//    Assert.found(method != null, "API接口不存在");
//
//    model.addAttribute("spi", spi);
//    model.addAttribute("currentApp", restApp);
//    model.addAttribute("method", method);
//    model.addAttribute("routing", "method");
//
//    TypeInfo type = method.getOriginal().getReturnType().getType();
//    // map时，取value的type
//    String actualType =
//        type.isMap() ? type.getMapValueType().getActualType() : type.getActualType();
//
//    // 是否是简单类型
//    model.addAttribute("simpleType", Types.isSimpleType(actualType));
//    model.addAttribute("returnTypeName", getReturnTypeName(type, actualType));
//
//    String returnJsonStr = null;
//    ApiDocJson configJson = restApp.getOriginal().getApiDocJson();
//    if (configJson != null && configJson.getConfig() != null) {
//      String returnJsonKey = spi.getOriginal().getClassName() + "."
//          + method.getOriginal().getMethodName() + ApiDocJson.CONFIG_KEY_RETURN_JSON_SUFFIX;
//      returnJsonStr = configJson.getConfig().get(returnJsonKey);
//    }
//
//    if (StringUtils.isEmpty(returnJsonStr)) {
//      returnJsonStr = toJSONString(method.getOriginal().getReturnType());
//    }
//    String returnJson = JsonFormatTool.formatJson(returnJsonStr);
//    // json 返回值
//    model.addAttribute("returnJsonInfo", returnJson);
//    PostManEnvironment postManEnvironment = new PostManEnvironment();
//    postManEnvironment.setId(restApp.getId());
//    postManEnvironment.setName(restApp.getOriginal().getAppName());
//    // json字符串为空,则当做无返回值
//    if (StringUtils.isEmpty(returnJson)) {
//      model.addAttribute("voidFlag", true);
//    } else {
//      model.addAttribute("voidFlag", false);
//    }
//
//    // curl 示例
//    model.addAttribute("curl", getCurl(restApp, method));
//    return "index";
//  }
//
//  @RequestMapping(value = "/api/v1/doc/{appId}/postman")
//  public ResponseEntity<?> methodInfo(@PathVariable(value = "appId") String appId,
//      @RequestParam(value = "usePlaceholder", defaultValue = "true") boolean userPlaceholder,
//      @RequestParam(value = "ignoreVirtualPath", defaultValue = "false") boolean ignoreVirtualPath,
//      @RequestParam(value = "host", required = false) String host) {
//    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
//    Assert.found(restApp != null, "系统不存在");
//    PostManCollectionHelper postManCollectionHelper =
//        new PostManCollectionHelper(userPlaceholder, ignoreVirtualPath, host);
//
//    PostManMain postManMain = postManCollectionHelper.getPostManMain(restApp);
//    return ResponseEntity.ok(postManMain);
//  }
//
//  @RequestMapping(value = "/api/v1/doc/{appId}/{spiClassId}/{methodId}")
//  public ResponseEntity<?> methodInfo(@PathVariable(value = "appId") String appId,
//      @PathVariable(value = "spiClassId") String spiClassId,
//      @PathVariable(value = "methodId") String methodId,
//      @RequestParam(value = "type", defaultValue = "all") String type) {
//    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
//    Assert.found(restApp != null, "系统不存在");
//
//    VersionGroupedRestClass spi = restApp.getSpi(spiClassId);
//    Assert.found(spi != null, "接口SPI不存在");
//
//    VersionedRestMethod method = spi.getMethod(methodId);
//
//    Assert.found(method != null, "API接口不存在");
//    switch (type) {
//      case "all":
//        return ResponseEntity.ok(spi);
//      case "json":
//        return ResponseEntity
//            .ok(JsonFormatTool.formatJson(toJSONString(method.getOriginal().getReturnType())));
//      case "curl":
//        return ResponseEntity.ok(getCurl(restApp, method));
//    }
//    return ResponseEntity.ok(method.getOriginal().getReturnType());
//  }
//
//  /**
//   * 页面响应字段显示的类型
//   */
//  private String getReturnTypeName(TypeInfo type, String actualTypeName) {
//    String simpleType = Types.getSimpleTypeName(actualTypeName);
//    // 页面显示的响应值类型
//    String returnTypeName = Types.getSimpleTypeName(type.getContainerType());
//    if (type.isCollection()) {
//      returnTypeName = "List<" + simpleType + ">";
//    } else if (type.isArray()) {
//      returnTypeName = simpleType + "[]";
//    } else if (type.isMap()) {
//      String mapKeyType = type.getMapKeyType();
//      if (mapKeyType.lastIndexOf(".") >= 0 && mapKeyType.length() > 1) {
//        mapKeyType = mapKeyType.substring(mapKeyType.lastIndexOf(".") + 1);
//      }
//      returnTypeName = "Map<" + mapKeyType + ","
//          + ((type.getMapValueType().isArray() || type.getMapValueType().isCollection())
//              ? "List<" + simpleType + ">" : simpleType)
//          + ">";
//    }
//    return returnTypeName;
//  }
//
//  /**
//   * 将return type 转换为json String
//   */
//  private String toJSONString(ReturnType returnType) {
//    // 返回值类型
//    TypeInfo typeInfo = returnType.getType();
//    // 如果是map
//    if (typeInfo.isMap()) {
//      return processMapType(typeInfo, returnType.getFields());
//    } else if (Types.isSimpleType(typeInfo.getActualType())) {
//      // 实际类型是简单类型
//      return processSimpleType(typeInfo);
//    } else {
//      // 实际类型是model,枚举
//      return processModelType(typeInfo, returnType.getFields());
//    }
//  }
//
//
//  private String processSimpleType(TypeInfo simpleType) {
//    // 非map，但实际类型是简单类型
//    Object defaultValue = Types.getSimpleTypeDefaultValue(simpleType.getActualType());
//    if (defaultValue == null) {
//      // void
//      return "";
//    } else if (simpleType.isCollection() || simpleType.isArray()) {
//      // 如果实际类型是简单类型，比如 String[],String ,List<String> ,boolean
//      return createJsonArray(escapeDefaultValueIfString(defaultValue)).toString();
//    } else {
//      return String.valueOf(defaultValue);
//    }
//  }
//
//  /**
//   * 处理model类型
//   */
//  private String processModelType(TypeInfo modelType, List<FieldInfo> modelFields) {
//    // 枚举类型
//    // 其余的实际类型统一当做model来处理
//    Object returnValue = new JSONObject();
//    if (modelFields == null || modelFields.isEmpty()) {
//      if (modelType.isEnum()) {
//        returnValue = "枚举值";
//      }
//    } else {
//      JSONObject jsonObject = new JSONObject();
//      for (FieldInfo field : modelFields) {
//        if (field.getModelFields() == null || field.getModelFields().isEmpty()) {
//          Object value = null;
//          if (field.getType().isEnum()) {
//            value = "枚举值";
//          } else {
//            value = escapeDefaultValueIfString(
//                Types.getSimpleTypeDefaultValue(field.getType().getActualType()));
//            if (value instanceof String) {
//              value = field.getComment();
//            }
//          }
//          jsonObject.put(field.getName(), value);
//        } else {
//          Object value = asJsonObject(field.getModelFields());
//          if (field.getType().isArray() || field.getType().isCollection()) {
//            jsonObject.put(field.getName(), createJsonArray(value));
//          } else {
//            jsonObject.put(field.getName(), value);
//          }
//        }
//      }
//      returnValue = jsonObject;
//    }
//    if (modelType.isArray() || modelType.isCollection()) {
//      // 数组类型
//      return createJsonArray(returnValue).toString();
//    }
//    return returnValue.toString();
//  }
//
//  /**
//   * 处理map类型
//   */
//  private String processMapType(TypeInfo mapType, List<FieldInfo> mapModelFields) {
//    JSONObject jsonObj = new JSONObject();
//    // map value
//    TypeInfo valueType = mapType.getMapValueType();
//    // map里嵌套map，shit,不处理了。
//    if (valueType.isMap()) {
//      return jsonObj.toString();
//    }
//    Object mapValue = null;
//    if (Types.isSimpleType(valueType.getActualType())) {
//      // 先转义
//      Object defaultValue =
//          escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(valueType.getActualType()));
//      // void
//      mapValue = (defaultValue == null ? ""
//          : (valueType.isArray() || valueType.isCollection() ? createJsonArray(defaultValue)
//              : defaultValue));
//    } else if (mapModelFields != null && mapModelFields.size() > 0) {
//      // map的value类型的字段值保存在ReturnType里
//      JSONObject model = asJsonObject(mapModelFields);
//      if (valueType.isArray() || valueType.isCollection()) {
//        mapValue = createJsonArray(model);
//      } else {
//        mapValue = model;
//      }
//    } else {
//      mapValue = new JSONObject();
//    }
//    Object keyValue =
//        escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(mapType.getMapKeyType()));
//    jsonObj.put(keyValue, mapValue);
//    return jsonObj.toString();
//  }
//
//  /**
//   * 默认值如果是字符串，转义""
//   */
//  private Object escapeDefaultValueIfString(Object defaultValue) {
//    if (defaultValue instanceof String) {
//      return String.valueOf(defaultValue).replace("\"", "");
//    }
//    return defaultValue;
//  }
//
//  /**
//   * 将model的字段转为JSON Object
//   */
//  private JSONObject asJsonObject(List<FieldInfo> fields) {
//    JSONObject jsonObject = new JSONObject();
//    if (fields == null || fields.size() < 1) {
//      return jsonObject;
//    }
//    for (FieldInfo field : fields) {
//      TypeInfo type = field.getType();
//      if (field.getModelFields() != null && field.getModelFields().size() > 0) {
//        if (type.isArray() || type.isCollection()) {
//          jsonObject.put(field.getName(), createJsonArray(asJsonObject(field.getModelFields())));
//        } else {
//          jsonObject.put(field.getName(), asJsonObject(field.getModelFields()));
//        }
//      } else {
//        Object value = null;
//        if (field.getType().isEnum()) {
//          value = "枚举值";
//        } else {
//          value = escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(type.getActualType()));
//          if (value instanceof String) {
//            // 如果是字符串，返回注释值
//            value = field.getComment();
//          }
//        }
//        jsonObject.put(field.getName(),
//            type.isArray() || type.isCollection() ? createJsonArray(value) : value);
//      }
//    }
//    return jsonObject;
//  }
//
//  /**
//   * json Array
//   */
//  private JSONArray createJsonArray(Object obj) {
//    JSONArray jsonArray = new JSONArray();
//    jsonArray.add(obj);
//    return jsonArray;
//
//  }
//
//  private String getCurl(VersionedRestApp restApp, VersionedRestMethod method) {
//    StringBuilder sb = new StringBuilder("curl ");
//    SpiMethod original = method.getOriginal();
//    String methodStr = original.getMapping().getMethod().getName().toUpperCase();
//    if (methodStr.equals("POST") || methodStr.equals("PUT")) {
//      sb.append(" -X ").append(methodStr);
//    }
//    sb.append(" 'http://api.route.dooioo.org");
//    if (restApp.getOriginal().isIgnoreVirtualPath()) {
//      sb.append(original.getMapping().getPath());
//    } else {
//      sb.append("/").append(restApp.getOriginal().getApp().replace("-", "/"))
//          .append(original.getMapping().getPath());
//    }
//    if (methodStr.equals("GET") || methodStr.equals("DELETE")) {
//      sb.append(getRequestParams(original) != null ? getRequestParams(original).get("get") : "")
//          .append("'\\");
//      sb.append(getHeader(original));
//    } else {
//      sb.append("' \\\n")
//          .append("-H 'Content-Type:application/x-www-form-urlencoded;" + "charset=utf-8' \\");
//      sb.append(getHeader(original));
//      sb.append(getRequestParams(original) != null ? getRequestParams(original).get("post") : "");
//    }
//    if (sb.lastIndexOf("\\") > 0) {
//      return sb.substring(0, sb.lastIndexOf("\\"));
//    } else {
//      return sb.toString();
//    }
//
//  }
//
//  private String getHeader(SpiMethod original) {
//    List<RequestHeader> requestHeaders = original.getRequestHeaders();
//    Set<String> ignoredHeaders =
//        new HashSet<>(Arrays.asList("X-Login-Token".toLowerCase(), "X-Login-UserCode".toLowerCase(),
//            "X-Login-CompanyId".toLowerCase(), "X-Token".toLowerCase()));
//
//    StringBuilder sb = new StringBuilder();
//    if (!original.isLoginNeedless()) {
//      sb.append("\n-H '").append("X-Token").append(":' \\");
//    }
//    for (RequestHeader requestHeader : requestHeaders) {
//      if (ignoredHeaders.contains(requestHeader.getName().toLowerCase())) {
//        continue;
//      }
//      String defaultValue = requestHeader.getDefaultValue();
//      sb.append("\n-H '").append(requestHeader.getName())
//          .append(":" + (StringUtils.hasText(defaultValue) ? defaultValue : "") + "' \\");
//    }
//    return sb.toString();
//
//  }
//
//  private Map<String, String> getRequestParams(SpiMethod original) {
//    StringBuilder getSB = new StringBuilder("?");
//    StringBuilder postSB = new StringBuilder();
//    Map<String, String> queryMap = new HashMap<>();
//    List<QueryParam> queryParams = original.getQueryParams();
//    if (queryParams == null || queryParams.size() == 0) {
//      return null;
//    }
//    for (QueryParam queryParam : original.getQueryParams()) {
//      getSB.append(queryParam.getName()).append("=")
//          .append(
//              StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam.getDefaultValue() : "")
//          .append("&");
//      postSB.append("\n-d '").append(queryParam.getName()).append("=")
//          .append(
//              StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam.getDefaultValue() : "")
//          .append("' \\");
//    }
//    queryMap.put("get", getSB.substring(0, getSB.lastIndexOf("&")));
//    queryMap.put("post", postSB.toString());
//    return queryMap;
//  }
//
//}
