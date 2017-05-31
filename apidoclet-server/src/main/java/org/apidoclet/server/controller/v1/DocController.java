package org.apidoclet.server.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apidoclet.model.FieldInfo;
import org.apidoclet.model.HeaderParam;
import org.apidoclet.model.QueryParam;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.TypeInfo;
import org.apidoclet.model.util.Types;
import org.apidoclet.server.helper.JsonFormatTool;
import org.apidoclet.server.helper.PostManCollectionHelper;
import org.apidoclet.server.model.PostManEnvironment;
import org.apidoclet.server.model.PostManMain;
import org.apidoclet.server.model.VersionGroupedRestClass;
import org.apidoclet.server.model.VersionedRestApp;
import org.apidoclet.server.model.VersionedRestMethod;
import org.apidoclet.server.service.VersionGroupedAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * page controller
 * 
 * @author huisman
 */
/**
 * @summary Copyright (c) 2017, Lianjia Group All Rights Reserved.
 */
@Controller
public class DocController {
  @Autowired
  private VersionGroupedAppService versionGroupedAppService;


  @ModelAttribute(value = "apps")
  public List<VersionedRestApp> allApps() {
    return this.versionGroupedAppService.findAll();
  }

  /**
   * home page
   */
  @RequestMapping(value = {"/"})
  public String index(Model model) {
    model.addAttribute("routing", "doc");
    model.addAttribute("currentApp", new VersionedRestApp());
    model.addAttribute("restClass", new VersionGroupedRestClass());
    model.addAttribute("method", new VersionedRestMethod());
    return "index";
  }


  /**
    * project info
    * @author huisman
   */
  @RequestMapping(value = "/v1/doc/{appId}/projectInfo")
  public String app(Model model, @PathVariable(value = "appId") String appId) {
    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
    model.addAttribute("currentApp", restApp);
    model.addAttribute("restClass", new VersionGroupedRestClass());
    model.addAttribute("method", new VersionedRestMethod());
    model.addAttribute("routing", "projectInfo");
    return "index";
  }


  @RequestMapping(value = "/v1/doc/export/{appId}/postman")
  public String imortPostman(Model model,
      @PathVariable(value = "appId") String appId) {
    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
    Assert.notNull(restApp, String.format("app(id=%s)does't exists", appId));
    model.addAttribute("currentApp", restApp);
    model.addAttribute("restClass", new VersionGroupedRestClass());
    model.addAttribute("method", new VersionedRestMethod());
    model.addAttribute("routing", "postman");
    return "index";
  }


  /**
   * method info
   * 
   * @author huisman
   */
  @RequestMapping(value = "/v1/doc/{appId}/{restClassId}/{methodId}")
  public String methodDoc(@PathVariable(value = "appId") String appId,
      @PathVariable(value = "restClassId") String restClassId, @PathVariable(
          value = "methodId") String methodId, Model model) {
    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
    Assert.notNull(restApp, String.format("app(id=%s) does't exists", appId));

    VersionGroupedRestClass restClass = restApp.getRestClass(restClassId);
    Assert.notNull(restClass, String.format(
        "api (appId=%s,restClassId=%s) does't exists", appId, restClassId));

    VersionedRestMethod method = restClass.getMethod(methodId);
    Assert.notNull(method, String.format(
        "api (appId=%s,restClassId=%s,methodId=%s) does't exists", appId,
        restClassId, methodId));

    model.addAttribute("restClass", restClass);
    model.addAttribute("currentApp", restApp);
    model.addAttribute("method", method);
    model.addAttribute("routing", "method");

    TypeInfo type = method.getOriginal().getReturnType();
    // map?
    String actualType =
        type.isMap() ? type.getMapValueType().getActualType() : type
            .getActualType();

    // simple type?
    model.addAttribute("simpleType", Types.isSimpleType(actualType));
    model.addAttribute("returnTypeName", getReturnTypeName(type, actualType));
    String returnJsonStr = toJSONString(method.getOriginal().getReturnType());
    String returnJson = JsonFormatTool.formatJson(returnJsonStr);
    model.addAttribute("returnJsonInfo", returnJson);
    PostManEnvironment postManEnvironment = new PostManEnvironment();
    postManEnvironment.setId(restApp.getId());
    postManEnvironment.setName(restApp.getOriginal().getAppName());
    // JSON string is empty
    if (StringUtils.isEmpty(returnJson)) {
      model.addAttribute("voidFlag", true);
    } else {
      model.addAttribute("voidFlag", false);
    }

    // curl snippet
    model.addAttribute("curl", getCurl(restApp, method));
    return "index";
  }

  @RequestMapping(value = "/api/v1/doc/{appId}/postman")
  public ResponseEntity<?> methodInfo(
      @PathVariable(value = "appId") String appId,
      @RequestParam(value = "usePlaceholder", defaultValue = "true") boolean userPlaceholder,
      @RequestParam(value = "ignoreVirtualPath", defaultValue = "false") boolean ignoreVirtualPath,
      @RequestParam(value = "host", required = false) String host) {
    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
    if (StringUtils.isEmpty(host)) {
      host = deduceHost(restApp);
    }
    PostManCollectionHelper postManCollectionHelper =
        new PostManCollectionHelper(userPlaceholder, ignoreVirtualPath, host);

    PostManMain postManMain = postManCollectionHelper.getPostManMain(restApp);
    return ResponseEntity.ok(postManMain);
  }

  @RequestMapping(value = "/api/v1/doc/{appId}/{spiClassId}/{methodId}")
  public ResponseEntity<?> methodInfo(
      @PathVariable(value = "appId") String appId, @PathVariable(
          value = "spiClassId") String spiClassId, @PathVariable(
          value = "methodId") String methodId, @RequestParam(value = "type",
          defaultValue = "all") String type) {
    VersionedRestApp restApp = versionGroupedAppService.findById(appId);
    VersionGroupedRestClass restClass = restApp.getRestClass(spiClassId);
    VersionedRestMethod method = restClass.getMethod(methodId);

    switch (type) {
      case "all":
        return ResponseEntity.ok(restClass);
      case "json":
        return ResponseEntity.ok(JsonFormatTool.formatJson(toJSONString(method
            .getOriginal().getReturnType())));
      case "curl":
        return ResponseEntity.ok(getCurl(restApp, method));
    }
    return ResponseEntity.ok(method.getOriginal().getReturnType());
  }

  /**
   * display name of the return type
   * 
   * @author huisman
   */
  private String getReturnTypeName(TypeInfo type, String actualTypeName) {
    String simpleType = Types.getSimpleTypeName(actualTypeName);
    String returnTypeName = Types.getSimpleTypeName(type.getContainerType());
    if (type.isCollection()) {
      returnTypeName = "List<" + simpleType + ">";
    } else if (type.isArray()) {
      returnTypeName = simpleType + "[]";
    } else if (type.isMap()) {
      String mapKeyType = type.getMapKeyType();
      if (mapKeyType.lastIndexOf(".") >= 0 && mapKeyType.length() > 1) {
        mapKeyType = mapKeyType.substring(mapKeyType.lastIndexOf(".") + 1);
      }
      returnTypeName =
          "Map<"
              + mapKeyType
              + ","
              + ((type.getMapValueType().isArray() || type.getMapValueType()
                  .isCollection()) ? "List<" + simpleType + ">" : simpleType)
              + ">";
    }else if(!simpleType.equals(returnTypeName)){
       //generic
       returnTypeName=returnTypeName+"<"+simpleType+">";
    }
    return returnTypeName;
  }

  /**
   * convert return type to JSON String
   */
  private String toJSONString(TypeInfo returnType) {
    // map?
    if (returnType.isMap()) {
      return processMapType(returnType, returnType.getFields());
    } else if (Types.isSimpleType(returnType.getActualType())) {
      // simple type
      return processSimpleType(returnType);
    } else {
      // complex type ,eg: java bean,enum
      return processModelType(returnType, returnType.getFields());
    }
  }


  private String processSimpleType(TypeInfo simpleType) {
    // default value
    Object defaultValue =
        Types.getSimpleTypeDefaultValue(simpleType.getActualType());
    if (defaultValue == null) {
      // void
      return "";
    } else if (simpleType.isCollection() || simpleType.isArray()) {
      // simple actual type,eg: String[],String ,List<String>,boolean
      return createJsonArray(escapeDefaultValueIfString(defaultValue))
          .toString();
    } else {
      return String.valueOf(defaultValue);
    }
  }

  /**
   * convert java bean to json tring
   */
  private String processModelType(TypeInfo modelType,
      List<FieldInfo> modelFields) {
    // concat all enum fields
    Object returnValue = new JSONObject();
    if (modelFields == null || modelFields.isEmpty()) {
      if (modelType.isEnum()) {
        returnValue = "Enum value";
      }
    } else {
      JSONObject jsonObject = new JSONObject();
      for (FieldInfo field : modelFields) {
        if (field.getNestedFields() == null
            || field.getNestedFields().isEmpty()) {
          Object value = null;
          if (field.getType().isEnum()) {
            value = "Enum value";
          } else {
            value =
                escapeDefaultValueIfString(Types
                    .getSimpleTypeDefaultValue(field.getType().getActualType()));
            if (value instanceof String) {
              value = field.getComment();
            }
          }
          jsonObject.put(field.getName(), value);
        } else {
          Object value = asJsonObject(field.getNestedFields());
          if (field.getType().isArray() || field.getType().isCollection()) {
            jsonObject.put(field.getName(), createJsonArray(value));
          } else {
            jsonObject.put(field.getName(), value);
          }
        }
      }
      returnValue = jsonObject;
    }
    if (modelType.isArray() || modelType.isCollection()) {
      // array?
      return createJsonArray(returnValue).toString();
    }
    return returnValue.toString();
  }

  /**
   * convert map to json string
   */
  private String processMapType(TypeInfo mapType, List<FieldInfo> mapModelFields) {
    JSONObject jsonObj = new JSONObject();
    // map value
    TypeInfo valueType = mapType.getMapValueType();
    // nested map,shit,ignore
    if (valueType.isMap()) {
      return jsonObj.toString();
    }
    Object mapValue = null;
    if (Types.isSimpleType(valueType.getActualType())) {
      // escape value
      Object defaultValue =
          escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(valueType
              .getActualType()));
      // void
      mapValue =
          (defaultValue == null ? "" : (valueType.isArray()
              || valueType.isCollection() ? createJsonArray(defaultValue)
              : defaultValue));
    } else if (mapModelFields != null && mapModelFields.size() > 0) {
      JSONObject model = asJsonObject(mapModelFields);
      if (valueType.isArray() || valueType.isCollection()) {
        mapValue = createJsonArray(model);
      } else {
        mapValue = model;
      }
    } else {
      mapValue = new JSONObject();
    }
    Object keyValue =
        escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(mapType
            .getMapKeyType()));
    jsonObj.put(String.valueOf(keyValue), mapValue);
    return jsonObj.toString();
  }

  /**
   * escape default value if it's a string value(escape "")
   */
  private Object escapeDefaultValueIfString(Object defaultValue) {
    if (defaultValue instanceof String) {
      return String.valueOf(defaultValue).replace("\"", "");
    }
    return defaultValue;
  }

  /**
   * convert java bean fields to JSON object
   */
  private JSONObject asJsonObject(List<FieldInfo> fields) {
    JSONObject jsonObject = new JSONObject();
    if (fields == null || fields.size() < 1) {
      return jsonObject;
    }
    for (FieldInfo field : fields) {
      TypeInfo type = field.getType();
      if (field.getNestedFields() != null && field.getNestedFields().size() > 0) {
        if (type.isArray() || type.isCollection()) {
          jsonObject.put(field.getName(),
              createJsonArray(asJsonObject(field.getNestedFields())));
        } else {
          jsonObject
              .put(field.getName(), asJsonObject(field.getNestedFields()));
        }
      } else {
        Object value = null;
        if (field.getType().isEnum()) {
          value = "enum value";
        } else {
          value =
              escapeDefaultValueIfString(Types.getSimpleTypeDefaultValue(type
                  .getActualType()));
          if (value instanceof String) {
            // return doc comment if actual type is string
            value = field.getComment();
          }
        }
        jsonObject.put(field.getName(),
            type.isArray() || type.isCollection() ? createJsonArray(value)
                : value);
      }
    }
    return jsonObject;
  }

  /**
   * create JSON array
   */
  private JSONArray createJsonArray(Object obj) {
    JSONArray jsonArray = new JSONArray();
    jsonArray.add(obj);
    return jsonArray;

  }

  /**
   * create CURL code snippet
   */
  private String getCurl(VersionedRestApp restApp, VersionedRestMethod method) {
    StringBuilder sb = new StringBuilder("curl ");
    RestClass.Method original = method.getOriginal();
    String methodStr =
        original.getMapping().getMethod().getName().toUpperCase();
    if (methodStr.equals("POST") || methodStr.equals("PUT")) {
      sb.append(" -X ").append(methodStr);
    }
    sb.append(" '" + deduceHost(restApp));
    // to-do
    // if (restApp.getOriginal().isIgnoreVirtualPath()) {
    sb.append(original.getMapping().getPath());
    // } else {
    // sb.append("/").append(restApp.getOriginal().getApp().replace("-", "/"))
    // .append(original.getMapping().getPath());
    // }
    if (methodStr.equals("GET") || methodStr.equals("DELETE")) {
      sb.append(
          getRequestParams(original) != null ? getRequestParams(original).get(
              "get") : "").append("'\\");
      sb.append(getHeader(original));
    } else {
      sb.append("' \\\n").append(
          "-H 'Content-Type:application/x-www-form-urlencoded;"
              + "charset=utf-8' \\");
      sb.append(getHeader(original));
      sb.append(getRequestParams(original) != null ? getRequestParams(original)
          .get("post") : "");
    }
    if (sb.lastIndexOf("\\") > 0) {
      return sb.substring(0, sb.lastIndexOf("\\"));
    } else {
      return sb.toString();
    }

  }

  private String deduceHost(VersionedRestApp restApp) {
    String defaultHost = "http://localhost:8080";
    if (restApp == null || restApp.getOriginal() == null) {
      return defaultHost;
    }
    // ApiDocJson config = restApp.getOriginal().getApiDocJson();
    return defaultHost;
  }

  private String getHeader(RestClass.Method original) {
    List<HeaderParam> requestHeaders = original.getRequestHeaders();
    StringBuilder sb = new StringBuilder();
    for (HeaderParam requestHeader : requestHeaders) {
      String defaultValue = requestHeader.getDefaultValue();
      sb.append("\n-H '")
          .append(requestHeader.getName())
          .append(
              ":" + (StringUtils.hasText(defaultValue) ? defaultValue : "")
                  + "' \\");
    }
    return sb.toString();
  }

  private Map<String, String> getRequestParams(RestClass.Method original) {
    StringBuilder getSB = new StringBuilder("?");
    StringBuilder postSB = new StringBuilder();
    Map<String, String> queryMap = new HashMap<>();
    List<QueryParam> queryParams = original.getQueryParams();
    if (queryParams == null || queryParams.size() == 0) {
      return null;
    }
    for (QueryParam queryParam : original.getQueryParams()) {
      getSB
          .append(queryParam.getName())
          .append("=")
          .append(
              StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam
                  .getDefaultValue() : "").append("&");
      postSB
          .append("\n-d '")
          .append(queryParam.getName())
          .append("=")
          .append(
              StringUtils.hasText(queryParam.getDefaultValue()) ? queryParam
                  .getDefaultValue() : "").append("' \\");
    }
    queryMap.put("get", getSB.substring(0, getSB.lastIndexOf("&")));
    queryMap.put("post", postSB.toString());
    return queryMap;
  }

}
