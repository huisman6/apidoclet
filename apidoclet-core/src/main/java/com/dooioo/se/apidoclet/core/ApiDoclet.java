package com.dooioo.se.apidoclet.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dooioo.se.apidoclet.core.spi.BizCodeProvider;
import com.dooioo.se.apidoclet.core.spi.EndpointMappingProvider;
import com.dooioo.se.apidoclet.core.spi.ModelProvider;
import com.dooioo.se.apidoclet.core.spi.RestClassMethodFilter;
import com.dooioo.se.apidoclet.core.spi.RestClassMethodParameterResolver;
import com.dooioo.se.apidoclet.core.spi.RestServiceFilter;
import com.dooioo.se.apidoclet.core.spi.RestServicesExporter;
import com.dooioo.se.apidoclet.core.spi.SkippedTypeFilter;
import com.dooioo.se.apidoclet.core.spi.TypeInfoProvider;
import com.dooioo.se.apidoclet.core.spi.postprocessor.ApiDocProcessContext;
import com.dooioo.se.apidoclet.core.spi.postprocessor.RestClassMethodPostProcessor;
import com.dooioo.se.apidoclet.core.spi.postprocessor.RestClassPostProcessor;
import com.dooioo.se.apidoclet.core.spi.postprocessor.RestServicePostProcessor;
import com.dooioo.se.apidoclet.core.util.ApiDocletClassLoader;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.Artifact;
import com.dooioo.se.apidoclet.model.BizCode;
import com.dooioo.se.apidoclet.model.EndpointMapping;
import com.dooioo.se.apidoclet.model.EnumInfo;
import com.dooioo.se.apidoclet.model.ModelInfo;
import com.dooioo.se.apidoclet.model.RestClass;
import com.dooioo.se.apidoclet.model.RestService;
import com.dooioo.se.apidoclet.model.RestServices;
import com.dooioo.se.apidoclet.model.TypeInfo;
import com.dooioo.se.apidoclet.model.config.ApiDocJson;
import com.dooioo.se.apidoclet.model.config.EnvVariable;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Type;

/**
 * 文档解析
 */
class ApiDoclet {
  // 默认classes加载器，如果我们需要加载类
  public static ApiDocletClassLoader APIDOCLET_CLASSES_LOADER = new ApiDocletClassLoader(
      ApiDoclet.class.getClassLoader());

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
   * 哪些方法是rest接口
   */
  private List<RestClassMethodFilter> restMethodFilters;

  /**
   * 解析业务码
   */
  private List<BizCodeProvider> bizCodeProviders;

  /**
   * classdoc的类型信息
   */
  private List<TypeInfoProvider> typeInfoProviders;

  /**
   * 忽略哪些类型
   */
  private List<SkippedTypeFilter> skippedTypeFilters;

  /**
   * 方法参数的解析
   */
  private List<RestClassMethodParameterResolver> methodParameterResolvers;

  /**
   * 解析路径的映射信息
   */
  private List<EndpointMappingProvider> endpointMappingProviders;

  /**
   * 获取枚举信息
   */
  private EnumProvider enumProvider = new EnumProvider();

  /**
   * 服务接口导出到展示的地方
   */
  private List<RestServicesExporter> restServicesExporters;

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

  public ApiDoclet(ApiDocletOptions options, List<RestServiceFilter> restServiceFilters,
      List<ModelProvider> modelProviders, List<RestClassMethodFilter> restMethodFilters,
      List<BizCodeProvider> bizCodeProviders, List<TypeInfoProvider> typeInfoProviders,
      List<SkippedTypeFilter> skippedTypeFilters,
      List<RestClassMethodParameterResolver> methodParameterResolvers,
      List<EndpointMappingProvider> endpointMappingProviders,
      List<RestServicesExporter> restServicesExporters,
      List<RestClassMethodPostProcessor> restClassMethodPostProcessors,
      List<RestClassPostProcessor> restClassPostProcessors,
      List<RestServicePostProcessor> restServicePostProcessors) {
    super();
    this.options = options;
    this.restServiceFilters = restServiceFilters;
    this.modelProviders = modelProviders;
    this.restMethodFilters = restMethodFilters;
    this.bizCodeProviders = bizCodeProviders;
    this.typeInfoProviders = typeInfoProviders;
    this.skippedTypeFilters = skippedTypeFilters;
    this.methodParameterResolvers = methodParameterResolvers;
    this.endpointMappingProviders = endpointMappingProviders;
    this.restServicesExporters = restServicesExporters;
    this.restClassMethodPostProcessors = restClassMethodPostProcessors;
    this.restClassPostProcessors = restClassPostProcessors;
    this.restServicePostProcessors = restServicePostProcessors;
  }


  /**
   * 通过方法或类上的注解，解析Rest接口的映射信息。
   * 
   * @author huisman
   */
  private EndpointMapping resolveEndpointMapping(AnnotationDesc[] classOrMethodAnnotations,
      SourcePosition position) {
    if (this.endpointMappingProviders == null || this.endpointMappingProviders.isEmpty()) {
      return null;
    }

    for (EndpointMappingProvider provider : this.endpointMappingProviders) {
      EndpointMapping mapping = provider.produce(classOrMethodAnnotations, this.options, position);
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
  private boolean belongToCurrentRestService(RestService restService, String qualifiedClassName) {
    if (this.restServiceFilters != null && !this.restServiceFilters.isEmpty()) {
      for (RestServiceFilter filter : this.restServiceFilters) {
        if (filter.include(restService, qualifiedClassName, this.options)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /**
   * 导出服务
   * 
   * @author huisman
   */
  private void exportRestServices(RestServices restServices) {
    if (this.restServicesExporters == null || this.restServicesExporters.isEmpty()) {
      this.options.getDocReporter().printWarning("============>> 没有找到RestServicesExporter。");
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
            break;
          }
        }
      }
    }
    return serviceName;
  }

  /**
   * 推断服务名称
   */
  private String deduceApp(ApiDocletOptions options) {
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
        List<BizCode> providedBizCodes = provider.produce(classDoc, this.options);
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
  private List<BizCode> resolveMethodBizCodes(MethodDoc methodDoc) {
    List<BizCode> bizCodes = new ArrayList<BizCode>();
    if (this.bizCodeProviders != null && !this.bizCodeProviders.isEmpty()) {
      for (BizCodeProvider provider : bizCodeProviders) {
        List<BizCode> providedBizCodes = provider.produce(methodDoc, this.options);
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
        if (shouldSkipType(type)) {
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
  private boolean shouldSkipType(Type type) {
    if (this.skippedTypeFilters == null || this.skippedTypeFilters.isEmpty()) {
      return false;
    }
    for (SkippedTypeFilter filter : skippedTypeFilters) {
      if (filter.ignored(type, this.options)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 处理方法的参数
   * 
   * @author huisman
   * @param methodDoc
   * @param restClass
   * @param method
   */
  private void processRestClassMethodParameters(MethodDoc methodDoc, RestClass restClass,
      RestClass.Method method) {
    if (this.methodParameterResolvers != null && !this.methodParameterResolvers.isEmpty()) {
      for (RestClassMethodParameterResolver resolver : methodParameterResolvers) {
        resolver.process(methodDoc, options, method, restClass);
      }
    }
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
    Map<Integer, BizCode> spiBizCodeMap = new HashMap<>();

    for (ClassDoc classDoc : classDocs) {
      if (shouldSkipType(classDoc.getElementType())) {
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
      ApiDocProcessContext context =
          new ApiDocProcessContext.Default(spiModelMap, spiBizCodeMap, this.options);
      // 等所有model解析了之后，最后解析一次返回值的字段是泛型model的、业务码
      postProcessSpiClass(restApp, context);
      // 处理附加信息，比如构建时间，构件信息
      postProcessApp(restApp, context);

      restApps.addApp(restApp);

      if (emptyOrOnlyOne) {
        // 如果只有一个rest app，model/bizcode全给它
        restApp.setModelInfos(restApps.getModelInfos());
        restApp.setBizCodes(restApps.getBizCodes());
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
          "finally resolved =========================================== as follow ");
      options.getDocReporter().printNotice("\n");
      options.getDocReporter().printNotice(restApps.toString());
      options.getDocReporter().printNotice("\n");
    }

    // 分发解析后的数据
    exportRestServices(restApps);
    return true;
  }

  /**
   * 将枚举划分到不同的微服务里
   */
  public void partitionEnums(List<EnumInfo> spiEnums, RestService restApp) {
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
  public void partitionModels(List<ModelInfo> models, RestService restApp) {
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
  public void partitionBizCodes(List<BizCode> spiBizCodes, RestService restApp) {
    if (spiBizCodes == null || spiBizCodes.isEmpty()) {
      return;
    }
    List<BizCode> currentAppBizCodes = new ArrayList<>();
    for (BizCode bizCode : spiBizCodes) {
      if (this.belongToCurrentRestService(restApp, bizCode.getContainingClass())) {
        currentAppBizCodes.add(bizCode);
      }
    }
    restApp.setBizCodes(currentAppBizCodes);
  }

  /**
   * 解析apidoclet json
   */
  private static ApiDocJson parseApiDocletJson(ApiDocletOptions options) {
    ApiDocJson apiDocJson = new ApiDocJson();
    String projectRootDir = options.optionValue(ApiDocletOptions.PROJECT_ROOT_DIR);
    try {
      Path path = Paths.get(projectRootDir, "apidoclet.json");

      if (!path.toFile().exists()) {
        options.getDocReporter().printNotice(
            "apidoclet.json not found ,ignored,file path: " + path.toAbsolutePath());
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
              int columnLen = (headers.length > columns.size()) ? columns.size() : headers.length;
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
          apiDocJson.getConfig().put(configKey, configJson.getString(configKey));
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
  private void postProcessApp(RestService restApp, ApiDocProcessContext context) {
    // 客户端实现
    if (this.restServicePostProcessors != null && !this.restServicePostProcessors.isEmpty()) {
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
  private void postProcessSpiClass(RestService app, ApiDocProcessContext context) {
    List<RestClass> spiClasses = app.getRestClasses();
    if (spiClasses != null && !spiClasses.isEmpty()) {
      for (RestClass spiClass : spiClasses) {
        // class最后做一些处理
        postProccessRestClass(spiClass, context);
        // 方法做最后的处理
        postProcessSpiMethod(spiClass, context);
      }
    }
  }

  private void postProccessRestClass(RestClass restClass, ApiDocProcessContext context) {
    if (this.restClassPostProcessors != null && !this.restClassPostProcessors.isEmpty()) {
      for (RestClassPostProcessor processor : restClassPostProcessors) {
        processor.postProcess(restClass, context);
      }
    }
  }


  /**
   * 对SpiMethod做后续处理
   */
  private void postProcessSpiMethod(RestClass spiClass, ApiDocProcessContext context) {
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
      String path = restAccessPoint.getMapping().getPath();
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
    com.dooioo.se.apidoclet.model.Artifact artifact = new Artifact();
    artifact.setGroupId(options.optionValue(ApiDocletOptions.ARTIFACT_GROUP_ID));
    artifact.setArtifactId(options.optionValue(ApiDocletOptions.ARTIFACT_ID));
    artifact.setVersion(options.optionValue(ApiDocletOptions.ARTIFACT_VERSION));

    // 读取readme
    String projectRootDir = options.optionValue(ApiDocletOptions.PROJECT_ROOT_DIR);
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

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(readme.getAbsolutePath()))) {
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


  static class EnumProvider {
    public boolean accept(ClassDoc classDoc, ApiDocletOptions options) {
      return classDoc.isEnum();
    }

    public EnumInfo handle(ClassDoc classDoc, ApiDocletOptions options) {
      // 解析SPI 枚举
      EnumInfo spiEnum = new EnumInfo();
      spiEnum.setClassName(classDoc.qualifiedTypeName());
      spiEnum.setFields(ClassUtils.getFieldInfos(classDoc, null));
      return spiEnum;
    }
  }
}
