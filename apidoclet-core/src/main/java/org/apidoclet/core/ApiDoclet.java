package org.apidoclet.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apidoclet.core.provider.DefaultTypeInfoProvider;
import org.apidoclet.core.provider.EnumProvider;
import org.apidoclet.core.provider.RestClassProvider;
import org.apidoclet.core.spi.exporter.RestServicesExporter;
import org.apidoclet.core.spi.exporter.RestServicesPartitionStrategy;
import org.apidoclet.core.spi.filter.RestClassMethodFilter;
import org.apidoclet.core.spi.filter.RestClassMethodParamFilter;
import org.apidoclet.core.spi.filter.RestServiceFilter;
import org.apidoclet.core.spi.filter.TypeFilter;
import org.apidoclet.core.spi.http.HeaderParamResolver;
import org.apidoclet.core.spi.http.HttpRequestBodyResolver;
import org.apidoclet.core.spi.http.PathParamResolver;
import org.apidoclet.core.spi.http.QueryParamResolver;
import org.apidoclet.core.spi.http.TypeInfoResolver;
import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.core.spi.processor.post.RestClassMethodPostProcessor;
import org.apidoclet.core.spi.processor.post.RestClassPostProcessor;
import org.apidoclet.core.spi.processor.post.RestServicePostProcessor;
import org.apidoclet.core.spi.provider.BizCodeProvider;
import org.apidoclet.core.spi.provider.EndpointMappingProvider;
import org.apidoclet.core.spi.provider.ModelProvider;
import org.apidoclet.core.spi.provider.TypeInfoProvider;
import org.apidoclet.core.util.AnnotationUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.Artifact;
import org.apidoclet.model.BizCode;
import org.apidoclet.model.EndpointMapping;
import org.apidoclet.model.EnumInfo;
import org.apidoclet.model.HeaderParam;
import org.apidoclet.model.JavaAnnotations;
import org.apidoclet.model.JavaAnnotations.JavaAnnotation;
import org.apidoclet.model.JavaDocTags;
import org.apidoclet.model.JavaDocTags.JavaDocTag;
import org.apidoclet.model.MethodParameter;
import org.apidoclet.model.ModelInfo;
import org.apidoclet.model.PathParam;
import org.apidoclet.model.QueryParam;
import org.apidoclet.model.RequestBody;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.RestService;
import org.apidoclet.model.RestServices;
import org.apidoclet.model.TypeInfo;
import org.apidoclet.model.config.ApiDocJson;
import org.apidoclet.model.config.EnvVariable;
import org.apidoclet.model.util.StandardDocTag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

/**
 * 文档解析
 */
class ApiDoclet {
  /**
   * 命令行参数
   */
  private ApiDocletOptions options;
  /**
   * 判定哪些类提供Rest服务
   */
  private List<RestServiceFilter> restServiceFilters;

  /**
   * 判断并获取model的信息
   */
  private List<ModelProvider> modelProviders;

  /**
   * 方法过滤
   */
  private List<RestClassMethodFilter> restMethodFilters;
  /**
   * 方法参数的过滤
   */
  private List<RestClassMethodParamFilter> restClassMethodParamFilters;


  /**
   * 解析业务码
   */
  private List<BizCodeProvider> bizCodeProviders;

  /**
   * classdoc的类型信息
   */
  private List<TypeInfoProvider> typeInfoProviders;

  /**
   * 类型过滤
   */
  private List<TypeFilter> typeFilters;

  /**
   * 方法参数的解析
   */
  private List<QueryParamResolver> queryParamResolvers;
  /**
   * 方法参数的解析
   */
  private List<PathParamResolver> pathParamResolvers;

  /**
   * http request body parser
   */
  private List<HttpRequestBodyResolver> requestBodyResolvers;
  /**
   * 方法参数的解析
   */
  private List<HeaderParamResolver> headerParamResolvers;


  /**
   * 解析路径的映射信息
   */
  private List<EndpointMappingProvider> endpointMappingProviders;
  /**
   * 服务接口导出到展示的地方
   */
  private List<RestServicesExporter> restServicesExporters;

  /**
   * 多个微服务，如果将model，业务码以及其他类分组到合适的微服务中。
   */
  private List<RestServicesPartitionStrategy> restServicesPartitionStrategies =
      null;


  /**
   * 类上的后续处理
   */
  private List<RestClassPostProcessor> restClassPostProcessors;
  /**
   * 接口方法的后续处理
   */
  private List<RestClassMethodPostProcessor> restClassMethodPostProcessors;

  /**
   * 服务的后续处理
   */
  private List<RestServicePostProcessor> restServicePostProcessors;

  /**
   * 获取枚举信息
   */
  private EnumProvider enumProvider = new EnumProvider();
  /**
   * Rest类的通用解析
   */
  private RestClassProvider restClassProvider = new RestClassProvider();
  /**
   * Rest服务
   */
  private RestServiceResovler restServiceResovler = new RestServiceResovler();

  ApiDoclet(ApiDocletOptions options,
      List<RestServiceFilter> restServiceFilters,
      List<ModelProvider> modelProviders,
      List<RestClassMethodFilter> restMethodFilters,
      List<BizCodeProvider> bizCodeProviders,
      List<TypeInfoProvider> typeInfoProviders, List<TypeFilter> typeFilters,
      List<RestClassMethodParamFilter> restClassMethodParamFilters,
      List<HttpRequestBodyResolver> requestBodyResolvers,
      List<QueryParamResolver> queryParamResolvers,
      List<PathParamResolver> pathParamResolvers,
      List<HeaderParamResolver> headerParamResolvers,
      List<EndpointMappingProvider> endpointMappingProviders,
      List<RestServicesExporter> restServicesExporters,
      List<RestClassMethodPostProcessor> restClassMethodPostProcessors,
      List<RestClassPostProcessor> restClassPostProcessors,
      List<RestServicePostProcessor> restServicePostProcessors,
      List<RestServicesPartitionStrategy> restServicesPartitionStrategies) {
    super();
    this.options = options;
    this.restServiceFilters = restServiceFilters;
    this.modelProviders = modelProviders;
    this.restMethodFilters = restMethodFilters;
    this.bizCodeProviders = bizCodeProviders;
    this.typeInfoProviders = new ArrayList<TypeInfoProvider>();
    if (typeInfoProviders != null) {
      // 按顺序添加，外部提供的可能不是ArrayList,我们转换为顺序访问的
      this.typeInfoProviders.addAll(typeInfoProviders);
    }
    // 添加默认类型提供者，优先级最低
    this.typeInfoProviders.add(new DefaultTypeInfoProvider());
    this.typeFilters = typeFilters;
    this.restClassMethodParamFilters = restClassMethodParamFilters;
    this.requestBodyResolvers = requestBodyResolvers;
    this.pathParamResolvers = pathParamResolvers;
    this.headerParamResolvers = headerParamResolvers;
    this.queryParamResolvers = queryParamResolvers;
    this.endpointMappingProviders = endpointMappingProviders;
    this.restServicesExporters = restServicesExporters;
    this.restClassMethodPostProcessors = restClassMethodPostProcessors;
    this.restClassPostProcessors = restClassPostProcessors;
    this.restServicePostProcessors = restServicePostProcessors;
    this.restServicesPartitionStrategies = restServicesPartitionStrategies;
  }


  /**
   * 通过方法或类上的注解，解析Rest接口的映射信息。
   * 
   * @author huisman
   */
  private EndpointMapping resolveEndpointMapping(
      AnnotationDesc[] classOrMethodAnnotations, SourcePosition position) {
    if (this.endpointMappingProviders == null
        || this.endpointMappingProviders.isEmpty()) {
      return null;
    }

    for (EndpointMappingProvider provider : this.endpointMappingProviders) {
      EndpointMapping mapping =
          provider.produce(classOrMethodAnnotations, this.options, position);
      if (mapping == null) {
        continue;
      }
      return mapping;
    }
    return null;
  }

  /**
   * 判断qualifiedClassName是否属于当前服务
   * 
   * @author huisman
   */
  private boolean belongToCurrentRestService(RestService restService,
      String qualifiedClassName) {
    if (this.restServicesPartitionStrategies != null
        && !this.restServicesPartitionStrategies.isEmpty()) {
      for (RestServicesPartitionStrategy filter : this.restServicesPartitionStrategies) {
        if (filter.belongTo(restService, qualifiedClassName, this.options)) {
          return true;
        }
      }
      return false;
    }
    // 默认属于任何一个微服务。
    return true;
  }

  /**
   * 导出服务
   * 
   * @author huisman
   */
  private void exportRestServices(RestServices restServices) {
    if (this.restServicesExporters == null
        || this.restServicesExporters.isEmpty()) {
      this.options.getDocReporter().printWarning(
          "============>> 没有找到RestServicesExporter。");
      return;
    }
    for (RestServicesExporter restServicesExporter : restServicesExporters) {
      restServicesExporter.exportTo(restServices, options);
    }
  }

  /**
   * 判断当前classdoc是否提供Rest服务，如果提供Rest服务，则提供服务名，否则返回null或者空， 调用方可根据serviceName是否存在来决定是否跳过进一步解析。
   * 
   * @author huisman
   */
  private String filterRestClassAndGetServiceName(ClassDoc classDoc) {
    String serviceName = null;
    if (this.restServiceFilters == null || this.restServiceFilters.isEmpty()) {
      serviceName = deduceApp(this.options);
    } else {
      for (RestServiceFilter filter : this.restServiceFilters) {
        if (filter.accept(classDoc, this.options)) {
          serviceName = filter.getServiceName(classDoc, this.options);
          if (!StringUtils.isNullOrEmpty(serviceName)) {
            return serviceName;
          }
        }
      }
      // 都没解析到，忽略此源文件
    }
    return serviceName;
  }

  /**
   * 推断服务名称
   */
  private String deduceApp(ApiDocletOptions options) {
    if (!StringUtils.isNullOrEmpty(options.getApp())) {
      return options.getApp();
    }
    // 源文件路径
    String source = options.optionValue(ApiDocletOptions.SOURCE_PATH);
    if (StringUtils.isNullOrEmpty(source)) {
      return null;
    }
    String baseSourceDir = "src";
    int sourceIndex = source.indexOf(baseSourceDir);
    if (sourceIndex < 1) {
      // File.Separator
      return null;
    }
    String homeDir = source.substring(0, sourceIndex - 1);
    // 取运行目录的文件夹名
    Path homeDirPath = Paths.get(Paths.get(homeDir).normalize().toUri());
    if (homeDirPath.getNameCount() > 0) {
      return homeDirPath.getName(homeDirPath.getNameCount() - 1).toString();
    }
    return null;
  }



  /**
   * 解析model
   * 
   * @author huisman
   */
  private ModelInfo resolveModelInfo(ClassDoc classDoc) {
    if (this.modelProviders == null || this.modelProviders.isEmpty()) {
      return null;
    }
    for (ModelProvider provider : modelProviders) {
      if (provider.accept(classDoc, this.options)) {
        ModelInfo modelInfo = provider.produce(classDoc, this.options);
        if (modelInfo != null) {
          return modelInfo;
        }
      }
    }
    return null;
  }

  /**
   * 判断是否是Rest Endpoint，即这个方法可以解析为RestClass.Method
   * 
   * @author huisman
   */
  private boolean acceptThisMethod(MethodDoc methodDoc) {
    if (this.restMethodFilters == null || this.restMethodFilters.isEmpty()) {
      return true;
    }
    for (RestClassMethodFilter filter : restMethodFilters) {
      if (filter.accept(methodDoc, this.options)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取默认提供的业务码
   * 
   * @author huisman
   */
  private List<BizCode> resolveProvidedBizCodes() {
    List<BizCode> bizCodes = new ArrayList<BizCode>();
    if (this.bizCodeProviders != null && !this.bizCodeProviders.isEmpty()) {
      for (BizCodeProvider provider : bizCodeProviders) {
        List<BizCode> providedBizCodes = provider.provided(this.options);
        if (providedBizCodes != null && !providedBizCodes.isEmpty()) {
          bizCodes.addAll(providedBizCodes);
        }
      }
    }
    return bizCodes;
  }

  /**
   * 获取类上可能提供的业务码信息
   * 
   * @author huisman
   */
  private List<BizCode> resolveClassBizCodes(ClassDoc classDoc) {
    List<BizCode> bizCodes = new ArrayList<BizCode>();
    if (this.bizCodeProviders != null && !this.bizCodeProviders.isEmpty()) {
      for (BizCodeProvider provider : bizCodeProviders) {
        List<BizCode> providedBizCodes =
            provider.produce(classDoc, this.options);
        if (providedBizCodes != null && !providedBizCodes.isEmpty()) {
          bizCodes.addAll(providedBizCodes);
        }
      }
    }
    return bizCodes;
  }

  /**
   * 获取方法上可能提供的业务码信息
   * 
   * @author huisman
   */
  private Set<BizCode> resolveMethodBizCodes(ClassDoc classDoc,
      MethodDoc methodDoc) {
    Set<BizCode> bizCodes = new HashSet<BizCode>();
    if (this.bizCodeProviders != null && !this.bizCodeProviders.isEmpty()) {
      for (BizCodeProvider provider : bizCodeProviders) {
        List<BizCode> providedBizCodes =
            provider.produce(classDoc, methodDoc, this.options);
        if (providedBizCodes != null && !providedBizCodes.isEmpty()) {
          bizCodes.addAll(providedBizCodes);
        }
      }
    }
    return bizCodes;
  }

  /**
   * 将JavaDoc的Type转换为TypeInfo
   * 
   * @author huisman
   * @param type javadoc中的类型
   */
  private TypeInfo resolveTypeInfo(Type type) {
    if (this.typeInfoProviders != null && !this.typeInfoProviders.isEmpty()) {
      for (TypeInfoProvider provider : typeInfoProviders) {
        if (shouldSkipType(type.qualifiedTypeName())) {
          continue;
        }
        TypeInfo info = provider.produce(type, this.options);
        if (info != null) {
          return info;
        }
      }
    }
    return null;
  }


  /**
   * 是否可以跳过此类型
   * 
   * @author huisman
   */
  private boolean shouldSkipType(String qualifiedTypeName) {
    if (this.typeFilters == null || this.typeFilters.isEmpty()) {
      return false;
    }
    for (TypeFilter filter : typeFilters) {
      if (filter.ignored(qualifiedTypeName, this.options)) {
        options.getDocReporter().printNotice(
            "ignored type:" + qualifiedTypeName);
        return true;
      }
    }
    return false;
  }

  /**
   * 处理方法的QueryParam参数
   * 
   * @author huisman
   */
  private QueryParam resolveQueryParam(Parameter parameter, String paramComment) {
    if (this.queryParamResolvers != null && !this.queryParamResolvers.isEmpty()) {
      for (QueryParamResolver resolver : this.queryParamResolvers) {
        if (resolver.support(parameter, this.options)) {
          QueryParam qp = resolver.resolve(new TypeInfoResolver() {

            @Override
            public TypeInfo resolve(Type type) {
              return ApiDoclet.this.resolveTypeInfo(type);
            }
          }, parameter, paramComment, this.options);
          if (qp == null) {
            continue;
          }
          return qp;
        }
      }
    }
    return null;
  }


  /**
   * 是否可以跳过此方法参数
   * 
   * @author huisman
   */
  private boolean shouldSkipParameter(Parameter parameter) {
    if (this.restClassMethodParamFilters == null
        || this.restClassMethodParamFilters.isEmpty()) {
      return false;
    }
    for (RestClassMethodParamFilter filter : this.restClassMethodParamFilters) {
      if (filter.shouldSkipped(parameter, this.options)) {
        options.getDocReporter().printNotice(
            "ignored parameter:" + parameter.name());
        return true;
      }
    }
    return false;
  }

  /**
   * 处理方法的RequestBody参数
   * 
   * @author huisman
   */
  private RequestBody resolveRequestBody(Parameter parameter,
      String paramComment) {
    if (this.requestBodyResolvers != null
        && !this.requestBodyResolvers.isEmpty()) {
      for (HttpRequestBodyResolver resolver : this.requestBodyResolvers) {
        if (resolver.support(parameter, this.options)) {
          RequestBody rb = resolver.resolve(new TypeInfoResolver() {

            @Override
            public TypeInfo resolve(Type type) {
              return ApiDoclet.this.resolveTypeInfo(type);
            }
          }, parameter, paramComment, this.options);
          if (rb == null) {
            continue;
          }
          return rb;
        }
      }
    }
    return null;
  }

  /**
   * 处理方法的PathParam参数
   * 
   * @author huisman
   */
  private PathParam resolvePathParam(Parameter parameter, String paramComment) {
    if (this.pathParamResolvers != null && !this.pathParamResolvers.isEmpty()) {
      for (PathParamResolver resolver : this.pathParamResolvers) {
        if (resolver.support(parameter, this.options)) {
          PathParam qp = resolver.resolve(new TypeInfoResolver() {

            @Override
            public TypeInfo resolve(Type type) {
              return ApiDoclet.this.resolveTypeInfo(type);
            }
          }, parameter, paramComment, this.options);
          if (qp == null) {
            continue;
          }
          return qp;
        }
      }
    }
    return null;
  }


  /**
   * 处理方法的PathParam参数
   * 
   * @author huisman
   */
  private HeaderParam resolveHeaderParam(Parameter parameter,
      String paramComment) {
    if (this.headerParamResolvers != null
        && !this.headerParamResolvers.isEmpty()) {
      for (HeaderParamResolver resolver : this.headerParamResolvers) {
        if (resolver.support(parameter, this.options)) {
          HeaderParam qp = resolver.resolve(new TypeInfoResolver() {

            @Override
            public TypeInfo resolve(Type type) {
              return ApiDoclet.this.resolveTypeInfo(type);
            }
          }, parameter, paramComment, this.options);
          if (qp == null) {
            continue;
          }
          return qp;
        }
      }
    }
    return null;
  }


  /**
   * 开始解析所有源代码，如果解析失败，则返回false
   * 
   * @author huisman
   */
  public boolean startParseSourceCodes(RootDoc rootDoc) {
    ClassDoc[] classDocs = rootDoc.classes();
    // 所有解析出来的微服务
    Map<String, RestService> restAppMap = new HashMap<>();
    // 所有静态、公开访问的业务码字段
    List<BizCode> bizCodes = new ArrayList<>();
    // 所有枚举
    List<EnumInfo> spiEnums = new ArrayList<>();
    // 所有model(pojo)
    Map<String, ModelInfo> spiModelMap = new HashMap<>();
    // 业务码值和BizCode的映射
    Map<String, BizCode> spiBizCodeMap = new HashMap<String, BizCode>();

    for (ClassDoc classDoc : classDocs) {
      if (shouldSkipType(classDoc.qualifiedTypeName())) {
        continue;
      }
      // 所有枚举
      if (this.enumProvider.accept(classDoc, this.options)) {
        EnumInfo senum = this.enumProvider.handle(classDoc, this.options);
        if (senum != null) {
          spiEnums.add(senum);
        }
      }
      // 解析model
      ModelInfo modelInfo = this.resolveModelInfo(classDoc);
      // 解析所有的SPImodel，最后遍历所有解析的rest app ,
      if (modelInfo != null) {
        spiModelMap.put(modelInfo.getClassName(), modelInfo);
      }
      // 业务码信息
      List<BizCode> providedBizCodes = this.resolveClassBizCodes(classDoc);
      if (providedBizCodes != null) {
        bizCodes.addAll(providedBizCodes);
      }
      // 解析RestService
      restServiceResovler.resolveRestService(classDoc, options, restAppMap);
    }
    // 默认提供的业务码
    List<BizCode> providedBizCodes = this.resolveProvidedBizCodes();
    if (providedBizCodes != null) {
      bizCodes.addAll(providedBizCodes);
    }
    for (BizCode bizCode : providedBizCodes) {
      spiBizCodeMap.put(bizCode.getCode(), bizCode);
    }
    // 最后，解析返回值，根据包名合并model/bizcode到每个rest app里
    RestServices restApps = new RestServices();
    restApps.setBizCodes(bizCodes);
    restApps.setEnumInfos(spiEnums);
    restApps.setModelInfos(new ArrayList<>(spiModelMap.values()));

    // 只有一个
    boolean emptyOrOnlyOne = (restAppMap.isEmpty() || restAppMap.size() == 1);
    // 解析apidoclet json
    ApiDocJson apiDocJson = parseApiDocletJson(options);
    for (String appName : restAppMap.keySet()) {
      RestService restApp = restAppMap.get(appName);
      restApp.setApiDocJson(apiDocJson);
      // 后续处理
      ProcessorContext context =
          new ProcessorContext.Default(spiModelMap, spiBizCodeMap, this.options);
      // 等所有model解析了之后，最后解析一次返回值的字段是泛型model的、业务码
      postProcessSpiClass(restApp, context);
      // 处理附加信息，比如构建时间，maven构件信息
      postProcessRestService(restApp, context);

      restApps.addApp(restApp);

      if (emptyOrOnlyOne) {
        // 如果只有一个rest app，model/bizcode全给它
        restApp.setModelInfos(restApps.getModelInfos());
        restApp.setBizCodes(new HashSet<>(restApps.getBizCodes()));
        restApp.setEnumInfos(restApps.getEnumInfos());
      } else {
        // 划分业务码以及model stat
        partitionBizCodes(bizCodes, restApp);
        partitionEnums(spiEnums, restApp);
        partitionModels(restApps.getModelInfos(), restApp);
      }
    }

    if (options.isPrint()) {
      options.getDocReporter().printNotice("\n");
      options.getDocReporter().printNotice(
          "finally resolved ============================== as follow json ");
      options.getDocReporter().printNotice(JSON.toJSONString(restApps));
      options.getDocReporter().printNotice("\n");
    }

    // 分发解析后的数据
    this.exportRestServices(restApps);
    return true;
  }

  /**
   * 将枚举划分到不同的微服务里
   */
  private void partitionEnums(List<EnumInfo> spiEnums, RestService restApp) {
    if (spiEnums == null || spiEnums.isEmpty()) {
      return;
    }
    List<EnumInfo> currentAppEnums = new ArrayList<>();
    for (EnumInfo spiEnum : spiEnums) {
      if (this.belongToCurrentRestService(restApp, spiEnum.getClassName())) {
        currentAppEnums.add(spiEnum);
      }
    }
    restApp.setEnumInfos(currentAppEnums);
  }

  /**
   * 将model划分到不同的微服务里
   */
  private void partitionModels(List<ModelInfo> models, RestService restApp) {
    if (models == null || models.isEmpty()) {
      return;
    }

    List<ModelInfo> currentAppModels = new ArrayList<>();
    for (ModelInfo spiModel : models) {
      if (this.belongToCurrentRestService(restApp, spiModel.getClassName())) {
        currentAppModels.add(spiModel);
      }
    }
    restApp.setModelInfos(currentAppModels);
  }

  /**
   * 将业务码划分到不同的微服务里
   */
  private void partitionBizCodes(List<BizCode> spiBizCodes, RestService restApp) {
    if (spiBizCodes == null || spiBizCodes.isEmpty()) {
      return;
    }
    List<BizCode> currentAppBizCodes = new ArrayList<>();
    for (BizCode bizCode : spiBizCodes) {
      if (this.belongToCurrentRestService(restApp, bizCode.getDeclaredClass())) {
        currentAppBizCodes.add(bizCode);
      }
    }
    restApp.setBizCodes(new HashSet<BizCode>(currentAppBizCodes));
  }

  /**
   * 解析apidoclet json
   */
  private ApiDocJson parseApiDocletJson(ApiDocletOptions options) {
    ApiDocJson apiDocJson = new ApiDocJson();
    String projectRootDir =
        options.optionValue(ApiDocletOptions.PROJECT_ROOT_DIR);
    try {
      Path path = Paths.get(projectRootDir, "apidoclet.json");

      if (!path.toFile().exists()) {
        options.getDocReporter().printNotice(
            "apidoclet.json not found ,ignored,file path: "
                + path.toAbsolutePath());
        return apiDocJson;
      }

      Object jsonObject =
          JSONObject.parse(Files.readAllBytes(path),
              com.alibaba.fastjson.parser.Feature.AllowComment);

      // null or not
      if (!(jsonObject instanceof JSONObject)) {
        return apiDocJson;
      }
      JSONObject configJson = (JSONObject) jsonObject;

      // env配置
      JSONObject envJson = configJson.getJSONObject("env");
      // env 如果没配置，默认为null，server设置默认值。
      if (envJson != null && !envJson.isEmpty()) {
        EnvVariable ev = new EnvVariable();
        JSONArray array = envJson.getJSONArray("headers");
        if (array != null && array.size() > 0) {
          String[] headers = new String[array.size()];
          for (int i = 0; i < headers.length; i++) {
            headers[i] = array.getString(i);
          }
          ev.setHeaders(headers);

          // 其他配置
          JSONArray rows = envJson.getJSONArray("rows");
          int rowSize = 0;
          if (rows != null && (rowSize = rows.size()) > 0) {
            // 生成每一列的值
            EnvVariable.Row[] allRows = new EnvVariable.Row[rowSize];
            for (int i = 0; i < rowSize; i++) {
              JSONArray columns = rows.getJSONArray(i);
              if (columns == null || columns.isEmpty()) {
                continue;
              }
              EnvVariable.Row row = new EnvVariable.Row();
              // 每列的值不能超过headers的数量
              String[] columnValues = new String[headers.length];
              int columnLen =
                  (headers.length > columns.size()) ? columns.size()
                      : headers.length;
              for (int j = 0; j < columnLen; j++) {
                columnValues[j] = columns.getString(j);
              }
              row.setColumns(columnValues);
              allRows[i] = row;
            }
            ev.setRows(allRows);
          }
          // 有headers 才设置env
          apiDocJson.setEnv(ev);
        }
      }

      // 其他apidoclet.json
      Set<String> keySet = configJson.keySet();
      if (keySet != null && !keySet.isEmpty()) {
        for (String configKey : keySet) {
          if ("env".equals(configKey)) {
            continue;
          }
          apiDocJson.getConfig()
              .put(configKey, configJson.getString(configKey));
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return apiDocJson;
  }

  /**
   * 设置rest app构建信息
   */
  private void postProcessRestService(RestService restApp,
      ProcessorContext context) {
    // 客户端实现
    if (this.restServicePostProcessors != null
        && !this.restServicePostProcessors.isEmpty()) {
      for (RestServicePostProcessor processor : restServicePostProcessors) {
        processor.postProcess(restApp, context);
      }
    }
    restApp.setBuildAt(new Date());
    restApp.setBuildBy(context.getOpitons().getBuildBy());
    restApp.setBuildIpAddress(context.getOpitons().getBuildIpAddress());
    // 构件信息
    restApp.setArtifact(this.resolveArtifactIfAny(context.getOpitons()));
  }

  /**
   * 最后再解析下返回值、业务码，如果字段是model的话，比如是Pagination<br>
   * 此时，所有model已经解析，可以根据字段的实际类型，重新设置泛型model字段的字段了。
   * 
   * @author huisman
   */
  private void postProcessSpiClass(RestService app, ProcessorContext context) {
    List<RestClass> spiClasses = app.getRestClasses();
    if (spiClasses != null && !spiClasses.isEmpty()) {
      for (RestClass spiClass : spiClasses) {
        // class最后做一些处理
        postProccessRestClass(app, spiClass, context);
        // 方法做最后的处理
        postProcessSpiMethod(spiClass, context);
      }
    }
  }

  private void postProccessRestClass(RestService restService,
      RestClass restClass, ProcessorContext context) {
    if (this.restClassPostProcessors != null
        && !this.restClassPostProcessors.isEmpty()) {
      for (RestClassPostProcessor processor : restClassPostProcessors) {
        processor.postProcess(restClass, restService, context);
      }
    }
  }


  /**
   * 对SpiMethod做后续处理
   */
  private void postProcessSpiMethod(RestClass spiClass, ProcessorContext context) {
    List<RestClass.Method> methods = spiClass.getMethods();
    if (methods != null && !methods.isEmpty()) {
      for (RestClass.Method method : methods) {
        // 先调用客户端实现的postProcessRestMethod
        if (this.restClassMethodPostProcessors != null
            && !this.restClassMethodPostProcessors.isEmpty()) {
          for (RestClassMethodPostProcessor processor : restClassMethodPostProcessors) {
            processor.postProcess(method, spiClass, context);
          }
        }
        // 如果没有版本号，则尝试解析版本号
        setAnyVersion(method);
      }
    }
  }



  /**
   * 查找任何版本号，如果没有提供，先从request path中提取，最后返回默认值
   */
  private static void setAnyVersion(RestClass.Method restAccessPoint) {
    String version = restAccessPoint.getVersion();
    // 如果没指定版本号
    if (StringUtils.isNullOrEmpty(version)) {
      // 没有则从request path中获取
      String path = null;
      EndpointMapping mapping = restAccessPoint.getMapping();
      if (mapping != null) {
        path = mapping.getPath();
      }
      // 指定了request path
      if (!StringUtils.isNullOrEmpty(path)) {
        // 以版本号前缀开始，则截取版本号
        if (path.startsWith(RestService.VERSION_PREFIX_IN_PATH)) {
          int firstSlashIndex = path.indexOf("/");
          int secondSlashIndex = path.indexOf("/", firstSlashIndex + 1);
          if (firstSlashIndex >= 0 && secondSlashIndex > 0) {
            version = path.substring(firstSlashIndex + 1, secondSlashIndex);
          }
        }
      }
      // 最终，还是没有解析出来实际版本，设置默认值
      if (StringUtils.isNullOrEmpty(version)) {
        version = RestService.DEFAULT_VERSION;
      }
      restAccessPoint.setVersion(version);
    }
  }

  /**
   * 解析当前构件信息
   */
  private Artifact resolveArtifactIfAny(ApiDocletOptions options) {
    org.apidoclet.model.Artifact artifact = new Artifact();
    artifact
        .setGroupId(options.optionValue(ApiDocletOptions.ARTIFACT_GROUP_ID));
    artifact.setArtifactId(options.optionValue(ApiDocletOptions.ARTIFACT_ID));
    artifact.setVersion(options.optionValue(ApiDocletOptions.ARTIFACT_VERSION));

    // 读取readme
    String projectRootDir =
        options.optionValue(ApiDocletOptions.PROJECT_ROOT_DIR);
    if (!StringUtils.isNullOrEmpty(projectRootDir)) {
      File rootDir = new File(projectRootDir);
      if (!rootDir.isDirectory()) {
        return artifact;
      }
      File[] candicateFiles = rootDir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.toLowerCase().startsWith("readme");
        }
      });
      if (candicateFiles != null && candicateFiles.length > 0) {
        // 只取第一个
        File readme = candicateFiles[0];
        artifact.setReadmeFileName(readme.getName());

        try (BufferedReader reader =
            Files.newBufferedReader(Paths.get(readme.getAbsolutePath()))) {
          StringBuilder cotentSb = new StringBuilder(500);
          char[] buffer = new char[256];
          int count = -1;
          while ((count = reader.read(buffer)) > 0) {
            cotentSb.append(buffer, 0, count);
          }
          artifact.setReadmeFileContent(cotentSb.toString());
        } catch (IOException e) {
          options.getDocReporter().printError(e.getMessage());
        }
      }
    }
    return artifact;
  }

  /**
   * 内部类，解析RestSerice
   */
  class RestServiceResovler {
    public void resolveRestService(ClassDoc classDoc, ApiDocletOptions options,
        Map<String, RestService> resolvedRestServiceMap) {

      String serviceName =
          ApiDoclet.this.filterRestClassAndGetServiceName(classDoc);
      if (StringUtils.isNullOrEmpty(serviceName)) {
        return;
      }
      // 推断appName，如果找不到，则默认为serviceName
      String providedAppName =
          options.optionValue(ApiDocletOptions.DEFAULT_PREFIX + serviceName);
      if (StringUtils.isNullOrEmpty(providedAppName)) {
        providedAppName = options.getAppName();
      }
      if (StringUtils.isNullOrEmpty(providedAppName)) {
        providedAppName = serviceName;
      }
      // 解析rest Service 只生成appName,app，其他字段不解析
      RestService app = new RestService(serviceName, providedAppName);
      // 如果已经存在，则使用之前存在的REST app，我们只根据app（服务名）分组
      if (resolvedRestServiceMap.containsKey(app.getApp())) {
        // 追加spi class
        app = resolvedRestServiceMap.get(app.getApp());
      }
      // 生成Rest的javadoc tag/Java Annotation。
      RestClass spiClass =
          ApiDoclet.this.restClassProvider.produce(classDoc, options);
      // 解析类上可能有的mapping信息
      spiClass.setEndpointMapping(ApiDoclet.this.resolveEndpointMapping(
          classDoc.annotations(), classDoc.position()));

      options.getDocReporter().printNotice(
          "found rest class:" + classDoc.qualifiedName());
      app.addSpiClass(spiClass);

      // 解析所有方法
      processMethods(classDoc, options, spiClass);

      ClassDoc[] interfaces = classDoc.interfaces();
      if (interfaces != null && interfaces.length > 0) {
        for (ClassDoc superInterface : interfaces) {
          processMethods(superInterface, options, spiClass);
        }
      }
      resolvedRestServiceMap.put(app.getApp(), app);
    }

    /**
     * 解析提供Rest服务的方法。
     */
    private void processMethods(ClassDoc classDoc, ApiDocletOptions options,
        RestClass restClass) {
      options.getDocReporter().printNotice(
          "processing methods in class: " + classDoc.qualifiedName());
      // 找到我们感兴趣的类了,开始解析方法
      MethodDoc[] methods = classDoc.methods();
      if (methods == null || methods.length == 0) {
        options.getDocReporter().printNotice("methods not found.");
        return;
      }
      for (MethodDoc methodDoc : methods) {
        if (ApiDoclet.this.acceptThisMethod(methodDoc)) {
          // 初始化方法，初始化注解、javadoc注释、returnTpe等
          RestClass.Method method = processRestClassMethod(classDoc, methodDoc);
          // 解析方法上可能有的业务码
          method.setBizCodes(ApiDoclet.this.resolveMethodBizCodes(classDoc,
              methodDoc));

          // 添加进RestClass里
          restClass.addMethod(method);
        } else {
          options.getDocReporter().printWarning(
              methodDoc.position(),
              "method ignored:" + methodDoc.name() + ",returyType:"
                  + methodDoc.returnType().qualifiedTypeName());
        }
      }
    }

    /**
     * 初始化Method,设置方法名，方法所在的类，方法的唯一标识identity</br> 解析EndpointMapping信息以及JavaDoc Tag、Java注解。
     */
    private RestClass.Method processRestClassMethod(ClassDoc classDoc,
        MethodDoc methodDoc) {
      RestClass.Method restMethod = new RestClass.Method();
      // 设置元数据信息
      String declaringClassName =
          methodDoc.containingClass().qualifiedTypeName();
      restMethod.setDeclaredClass(declaringClassName);
      restMethod.setMethodName(methodDoc.name());

      // 方法标识：类名#方法名#返回类型，例如: com.xxx.xxx.ICityService#findById(int,string)
      StringBuilder identityStr =
          new StringBuilder(declaringClassName
              + RestClass.Method.IDENTITY_SPILIT_CHAR
              + restMethod.getMethodName() + "(");

      // 生成方法唯一ID
      Parameter[] parameters = methodDoc.parameters();
      if (parameters != null && parameters.length > 0) {
        for (Parameter parameter : parameters) {
          identityStr.append(parameter.typeName() + ",");
        }
        identityStr.deleteCharAt(identityStr.length() - 1);
      }
      identityStr.append(")");
      restMethod.setIdentity(identityStr.toString());

      // parse endpoint path info
      restMethod.setMapping(ApiDoclet.this.resolveEndpointMapping(
          methodDoc.annotations(), methodDoc.position()));

      // parse java doc tags and annotations
      processMethodDocTagsAndAnnotations(methodDoc, restMethod, options);

      // parse method parameters
      processMethodParameter(methodDoc, restMethod, options);

      // method return type
      processMethodReturnType(classDoc, methodDoc, restMethod, options);
      return restMethod;
    }

    private void processMethodParameter(MethodDoc methodDoc,
        RestClass.Method method, ApiDocletOptions options) {
      // 参数注释,参数名=参数注释
      ParamTag[] paramTags = methodDoc.paramTags();
      Map<String, String> paramCommentMap = new HashMap<>();
      boolean anyComment = false;
      if (paramTags != null && paramTags.length > 0) {
        anyComment = true;
        for (ParamTag paramTag : paramTags) {
          paramCommentMap.put(paramTag.parameterName(),
              StringUtils.trim(paramTag.parameterComment()));
        }
      }

      // 查询参数，路径参数，条件参数
      List<PathParam> pathParams = new ArrayList<PathParam>();
      List<QueryParam> queryParams = new ArrayList<QueryParam>();
      List<HeaderParam> requestHeaders = new ArrayList<HeaderParam>();
      // 方法原始参数
      List<MethodParameter> methodParameters = new ArrayList<>();
      Parameter[] parameters = methodDoc.parameters();
      int len = parameters.length;
      for (int i = 0; i < len; i++) {
        Parameter parameter = parameters[i];
        // 是否跳过参数
        if (ApiDoclet.this.shouldSkipParameter(parameter)) {
          continue;
        }
        // 是否跳过此类型
        if (ApiDoclet.this.shouldSkipType(parameter.type().qualifiedTypeName())) {
          continue;
        }
        // 备注
        String comment =
            anyComment ? paramCommentMap.get(parameter.name()) : null;
        // 处理方法参数
        methodParameters.add(resolveMethodParameter(methodDoc, parameter,
            comment, i, i == (len - 1)));

        // 解析参数
        QueryParam qp = null;
        PathParam pp = null;
        HeaderParam hp = null;
        RequestBody requestBody = null;
        if ((qp = ApiDoclet.this.resolveQueryParam(parameter, comment)) != null) {
          // 可变参数
          if (i == (len - 1) && methodDoc.isVarArgs() && qp.getType() != null) {
            qp.getType().setArray(true);
          }
          queryParams.add(qp);
        } else if ((pp = ApiDoclet.this.resolvePathParam(parameter, comment)) != null) {
          pathParams.add(pp);
        } else if ((hp = ApiDoclet.this.resolveHeaderParam(parameter, comment)) != null) {
          requestHeaders.add(hp);
        } else if ((requestBody =
            ApiDoclet.this.resolveRequestBody(parameter, comment)) != null) {
          // only allow one request body to set
          method.setRequestBody(requestBody);
        }
        // may be more feature..
      }

      method.setRequestHeaders(requestHeaders);
      method.setQueryParams(queryParams);
      method.setPathParams(pathParams);
      method.setMethodParameters(methodParameters);
    }

    /**
     * 解析方法参数
     * 
     * @author huisman
     * @param methodDoc 方法
     * @param parameter 参数
     * @param comment 参数注释
     * @param paramIndex 参数索引
     * @param lastParam 是否是最后一个参数
     */
    private MethodParameter resolveMethodParameter(MethodDoc methodDoc,
        Parameter parameter, String comment, int paramIndex, boolean lastParam) {
      // 方法参数
      MethodParameter methodParameter = new MethodParameter();
      methodParameter.setComment(comment);
      methodParameter.setClassName(parameter.type().qualifiedTypeName());
      methodParameter.setIndex(paramIndex);
      methodParameter.setMethodName(methodDoc.name());
      methodParameter.setName(parameter.name());

      // java规范，可变参数只能是最后一个参数
      methodParameter.setVarargs(false);
      if (lastParam) {
        if (methodDoc.isVarArgs()) {
          methodParameter.setVarargs(true);
        }
      }
      // 参数类型
      Type type = parameter.type();
      // 解析方法类型
      methodParameter.setTypeInfo(ApiDoclet.this.resolveTypeInfo(type));

      // 解析参数注解
      AnnotationDesc[] annotationDescs = parameter.annotations();
      if (annotationDescs != null && annotationDescs.length > 0) {
        JavaAnnotations javaAnnotations = new JavaAnnotations();
        for (AnnotationDesc annotationDesc : annotationDescs) {
          // 解析所有注解
          JavaAnnotation javaAnnotation =
              new JavaAnnotation(AnnotationUtils.attributesFor(annotationDesc));
          javaAnnotation.setQualifiedClassName(annotationDesc.annotationType()
              .qualifiedTypeName());
          javaAnnotations.add(javaAnnotation);
        }
        methodParameter.setParameterAnnotations(javaAnnotations);
      }
      return methodParameter;
    }

    private void processMethodReturnType(ClassDoc classDoc,
        MethodDoc methodDoc, RestClass.Method restMethod,
        ApiDocletOptions options) {
      // customize return type
      Tag[] returnTypeTags = methodDoc.tags(StandardDocTag.TAG_RETURN_TYPE);
      if (returnTypeTags != null && returnTypeTags.length > 0) {
        Tag firstReturnTypeTag = returnTypeTags[0];
        // @returnType User
        // @returnType org.apidoclet.test.User
        // @returnType {@linkplain User}
        // @returnType {@link User}
        // @returnType {@code User}
        Tag[] inlineTags = firstReturnTypeTag.inlineTags();
        if (inlineTags != null) {
          for (Tag tag : inlineTags) {
            if (!StringUtils.isNullOrEmpty(tag.text())) {
              ClassDoc candidateReturnType =
                  classDoc.findClass(tag.text().trim());
              if (candidateReturnType != null
                  && candidateReturnType instanceof Type) {
                // match
                TypeInfo typeInfo =
                    ApiDoclet.this.resolveTypeInfo((Type) candidateReturnType);
                if (typeInfo != null) {
                  restMethod.setReturnType(typeInfo);
                  return;
                }
              }
            }
          }
        }
      }
      // default return type
      restMethod.setReturnType(ApiDoclet.this.resolveTypeInfo(methodDoc
          .returnType()));
    }

    private void processMethodDocTagsAndAnnotations(MethodDoc methodDoc,
        RestClass.Method restMethod, ApiDocletOptions options) {
      // 版本号
      Tag[] versionTags = methodDoc.tags(StandardDocTag.TAG_VERSION);
      String version =
          ((versionTags == null || versionTags.length == 0) ? ""
              : versionTags[0].text().trim());
      // 如果版本号不存在则设置为默认版本号。RequestMapping解析的时候，如果发现version依旧为空，则从requestPath中解析
      // 这是在ApiDoclet的最后几步执行此逻辑
      if (StringUtils.isNullOrEmpty(version)) {
        version = options.getVersion();
      }
      restMethod.setVersion(version);
      // 作者
      Tag[] authorTags = methodDoc.tags(StandardDocTag.TAG_AUTHOR);
      StringBuilder authors = new StringBuilder();
      if (authorTags != null && authorTags.length > 0) {
        for (Tag tag : authorTags) {
          authors.append("," + StringUtils.trim(tag.text()));
        }
        authors.deleteCharAt(0);
      }
      restMethod.setAuthor(authors.toString());

      // since,如果不存在，则new Date()
      Tag[] sinceTags = methodDoc.tags(StandardDocTag.TAG_SINCE);
      String since =
          ((sinceTags == null || sinceTags.length == 0 || StringUtils
              .isNullOrEmpty(sinceTags[0].text())) ? new SimpleDateFormat(
              "yyyy-MM-dd").format(new Date()) : StringUtils.trim(sinceTags[0]
              .text()));
      restMethod.setSince(since);

      boolean deprecated =
          AnnotationUtils.isPresent(methodDoc.annotations(),
              Deprecated.class.getName());
      Tag[] deprecatedTags = methodDoc.tags(StandardDocTag.TAG_DEPREACTED);
      if (deprecatedTags != null && deprecatedTags.length > 0) {
        deprecated = true;
        restMethod.setDeprecatedComment(StringUtils.trim(deprecatedTags[0]
            .text()));
      }
      if (deprecated) {
        restMethod.setDeprecatedDate(new Date());
      }

      // method web UI display name,take the method name as fall back  
      Tag[] summaryTags = methodDoc.tags(StandardDocTag.TAG_SUMMARY);
      String summary =
          ((summaryTags == null || summaryTags.length == 0 || StringUtils
              .isNullOrEmpty(summaryTags[0].text())) ? methodDoc.name()
              : StringUtils.trim(summaryTags[0].text()));
      restMethod.setSummary(summary);

      String comment = methodDoc.commentText();
      // method java-doc comment
      restMethod.setDescription(StringUtils.trim(comment));
      
      // additional java doc tags,exclude the StandardDocTag
      Tag[] allTags = methodDoc.tags();
      Set<String> skippedTags =
          new HashSet<>(Arrays.asList('@' + StandardDocTag.TAG_VERSION,
              '@' + StandardDocTag.TAG_AUTHOR,
              '@' + StandardDocTag.TAG_DEPREACTED,
              '@' + StandardDocTag.TAG_PARAM,
              '@' + StandardDocTag.TAG_SINCE, '@' + StandardDocTag.TAG_SUMMARY,
              '@' + StandardDocTag.TAG_RETURN_TYPE,
              '@' + StandardDocTag.TAG_BIZ_CODES));
      JavaDocTags docTags = new JavaDocTags();
      if (allTags != null && allTags.length > 0) {
        for (Tag tag : allTags) {
          if (skippedTags.contains(tag.kind())) {
            continue;
          }
          docTags.add(new JavaDocTag(tag.kind(), tag.name(), tag.text()));
        }
      }
      restMethod.setAdditionalDocTags(docTags);

      // parse all annotations on this method
      AnnotationDesc[] annotationDescs = methodDoc.annotations();
      if (annotationDescs != null && annotationDescs.length > 0) {
        JavaAnnotations javaAnnotations = new JavaAnnotations();
        for (AnnotationDesc annotationDesc : annotationDescs) {
          // 解析所有注解
          JavaAnnotation javaAnnotation =
              new JavaAnnotation(AnnotationUtils.attributesFor(annotationDesc));
          javaAnnotation.setQualifiedClassName(annotationDesc.annotationType()
              .qualifiedTypeName());
          javaAnnotations.add(javaAnnotation);
        }
        restMethod.setMethodAnnotations(javaAnnotations);
      }
    }
  }
}
