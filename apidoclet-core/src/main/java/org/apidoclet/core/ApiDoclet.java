package org.apidoclet.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
 * the customized java doc doclet,responsible for organizing all extension SPI components that are
 * used to analysis java source code
 */
class ApiDoclet {
  /**
   * apidoclet command line options, an option must be start with prefix '-'. eg: -exportTo , -app
   */
  private ApiDocletOptions options;
  /**
   * filters that use to determine whether a class contains any endpoint or not
   */
  private List<RestServiceFilter> restServiceFilters;

  /**
   * parse and provide model(-view-controller) info
   */
  private List<ModelProvider> modelProviders;

  /**
   * filters that use to determine whether a method could be accessed via HTTP
   */
  private List<RestClassMethodFilter> restMethodFilters;
  /**
   * use to filter java method parameters
   */
  private List<RestClassMethodParamFilter> restClassMethodParamFilters;


  /**
   * filter and parse BizCode (may be called Error Code?)
   */
  private List<BizCodeProvider> bizCodeProviders;

  /**
   * convert java-doc {@code Type} to {@code TypeInfo}
   */
  private List<TypeInfoProvider> typeInfoProviders;

  /**
   * filters that use to ignore some no-need-to-be-documented java type ,eg : HttpServletRequest
   */
  private List<TypeFilter> typeFilters;

  /**
   * convert javadoc {@code Parameter} to {@code QueryParam} if this parameter is annotated with
   * given http-query-param-related Annotation
   * 
   * e.g: @RequestParam in Spring Web MVC or @QueryParam in JAX-RS
   */
  private List<QueryParamResolver> queryParamResolvers;
  /**
   * convert javadoc {@code Parameter} to {@code PathParam} if this parameter is annotated with
   * given http-url-path-related Annotation
   * 
   * e.g: @PathVariable in Spring Web MVC or @PathParam in JAX-RS
   */
  private List<PathParamResolver> pathParamResolvers;

  /**
   * convert javadoc {@code Parameter} to {@code RequestBody} if this parameter is annotated with
   * given http-request-entity-related Annotation
   * 
   * e.g: @RequestBody in Spring Web MVC or @FormParam in JAX-RS
   */
  private List<HttpRequestBodyResolver> requestBodyResolvers;
  /**
   * convert javadoc {@code Parameter} to {@code HeaderParam} if this parameter is annotated with
   * given http-header-related Annotation
   * 
   * e.g: @RequestHeader in Spring Web MVC or @HeaderParam in JAX-RS
   */
  private List<HeaderParamResolver> headerParamResolvers;


  /**
   * construct an {@code EndpointMapping} by parsing {@code AnnotationDesc} on class or method
   * 
   * e.g: @RequestMapping in Spring Web MVC or @Path in JAX-RS
   */
  private List<EndpointMappingProvider> endpointMappingProviders;
  /**
   * export the parsed RestServices} to api-doclet server or any external available doc-server .
   * 
   * by default, we always attempt to export the parsed {@code RestServices} to
   * URL-"http://localhost:8089/v1/apps/import"(local api-doclet server is running with port 8089 )
   * for test, you can override the default export url by passing option :
   * 
   * -exportTo http://you-apidoclet-server-domain/v1/apps/import
   */
  private List<RestServicesExporter> restServicesExporters;

  /**
   * rarely, a project may contain two or more RestService, we use
   * {@code RestServicesPartitionStrategy} to determine which {@code RestService} these
   * {@code RestClass}/{@code BizCode}/{@code ModelInfo}/{@code EnumInfo} should belong to
   */
  private List<RestServicesPartitionStrategy> restServicesPartitionStrategies =
      null;


  /**
   * {@code RestClass} post processor,give the apidoclet-extension a last chance to modify
   * {@code RestClass}'s metadata before we start to export the parsed rest service
   */
  private List<RestClassPostProcessor> restClassPostProcessors;
  /**
   * RestClass.Method post processor,give the apidoclet-extension a last chance to modify
   * RestClass.Method's metadata before we start to export the parsed rest service
   */
  private List<RestClassMethodPostProcessor> restClassMethodPostProcessors;

  /**
   * {@code RestService} post processor,give the apidoclet-extension a last chance to modify
   * {@code RestService}'s metadata before we start to export the parsed rest service
   */
  private List<RestServicePostProcessor> restServicePostProcessors;

  /**
   * filter and convert {@code java.lang.Enum} to {@code org.apidoclet.model.EnumInfo}
   */
  private EnumProvider enumProvider = new EnumProvider();
  /**
   * default {@code RestClass} provider,responsible for parsing javadoc tags and annotations on the
   * eligible (filtered by {@code RestServiceFilter}) class .
   */
  private RestClassProvider restClassProvider = new RestClassProvider();
  /**
   * construct a {@code RestService} , analysis the javadoc type (ClassDoc/MethodDoc/AnnotationDesc)
   * by calling corresponding filters/providers/resolvers to produce a {@code RestClass} or
   * {@code RestClass.Method}
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
      // customized TypeInfo provider
      this.typeInfoProviders.addAll(typeInfoProviders);
    }
    // add fall-back TypeInfo provider, lowest priority
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
   * resolve {@code AnnotationDesc} on the class or method to retrieve {@code EndpointMapping}
   * metadata
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
   * 
   * determine whether the given qualifiedClassName belongs to this {@code restService}
   * 
   * @param restService
   * @param qualifiedClassName
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
    // default true
    return true;
  }

  /**
   * export the parsed restServices
   * 
   * @param restServices
   * @author huisman
   */
  private void exportRestServices(RestServices restServices) {
    if (this.restServicesExporters == null
        || this.restServicesExporters.isEmpty()) {
      this.options.getDocReporter().printWarning(
          "====> can't find any RestServicesExporter");
      return;
    }
    for (RestServicesExporter restServicesExporter : restServicesExporters) {
      restServicesExporter.exportTo(restServices, options);
    }
  }

  /**
   * deduce RestService identity
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
    }
    return serviceName;
  }

  /**
   * deduce the default RestService via apidoclet options
   */
  private String deduceApp(ApiDocletOptions options) {
    // first check whether "-app" option exists?
    if (!StringUtils.isNullOrEmpty(options.getApp())) {
      return options.getApp();
    }
    // source path
    String source = options.optionValue(ApiDocletOptions.SOURCE_PATH);
    if (StringUtils.isNullOrEmpty(source)) {
      return null;
    }
    // conventional maven directory ?
    String baseSourceDir = "src";
    int sourceIndex = source.indexOf(baseSourceDir);
    if (sourceIndex < 1) {
      // File.Separator
      return null;
    }
    String homeDir = source.substring(0, sourceIndex - 1);
    // fall-back: return the project name
    // e.g: /Users/someone/git/apidoclet/apidoclet-core/src/main/java will return apidoclet-core
    Path homeDirPath = Paths.get(Paths.get(homeDir).normalize().toUri());
    if (homeDirPath.getNameCount() > 0) {
      return homeDirPath.getName(homeDirPath.getNameCount() - 1).toString();
    }
    return null;
  }



  /**
   * filter and parse model
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
   * filter rest method
   * 
   * @see ApiDoclet#restMethodFilters
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
   * @see ApiDoclet#bizCodeProviders
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
   * @see ApiDoclet#bizCodeProviders
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
   * @see ApiDoclet#bizCodeProviders
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
   * @see ApiDoclet#typeInfoProviders
   * @author huisman
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
   * @see ApiDoclet#typeFilters
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
   * @see ApiDoclet#queryParamResolvers
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
   * @see ApiDoclet#restClassMethodParamFilters
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
   * @see ApiDoclet#requestBodyResolvers
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
   * @see ApiDoclet#pathParamResolvers
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
   * @see ApiDoclet#headerParamResolvers
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
   * entrypoint,start parsing javadoc
   * 
   * @return true only if parsing succeed
   * @author huisman
   */
  public boolean startParseSourceCodes(RootDoc rootDoc) {
    ClassDoc[] classDocs = rootDoc.classes();
    // all parsed RestService,group by its app identity
    Map<String, RestService> restAppMap = new HashMap<>();
    // all bizcodes within current project
    List<BizCode> bizCodes = new ArrayList<>();
    // all enuminfos within current project
    List<EnumInfo> spiEnums = new ArrayList<>();
    // all modelinfos with current project
    Map<String, ModelInfo> spiModelMap = new HashMap<>();
    // code to BizCode mapping
    Map<String, BizCode> spiBizCodeMap = new HashMap<String, BizCode>();

    for (ClassDoc classDoc : classDocs) {
      if (shouldSkipType(classDoc.qualifiedTypeName())) {
        continue;
      }
      // filter and parse enuminfo
      if (this.enumProvider.accept(classDoc, this.options)) {
        EnumInfo senum = this.enumProvider.handle(classDoc, this.options);
        if (senum != null) {
          spiEnums.add(senum);
        }
      }
      // filter and parse enuminfo,not necessary
      ModelInfo modelInfo = this.resolveModelInfo(classDoc);
      if (modelInfo != null) {
        spiModelMap.put(modelInfo.getClassName(), modelInfo);
      }
      // parsing bizcodes
      List<BizCode> providedBizCodes = this.resolveClassBizCodes(classDoc);
      if (providedBizCodes != null) {
        bizCodes.addAll(providedBizCodes);
      }
      // parsing RestService
      restServiceResovler.resolveRestService(classDoc, options, restAppMap);
    }
    // provided bizcodes
    List<BizCode> providedBizCodes = this.resolveProvidedBizCodes();
    if (providedBizCodes != null) {
      bizCodes.addAll(providedBizCodes);
    }
    for (BizCode bizCode : providedBizCodes) {
      spiBizCodeMap.put(bizCode.getCode(), bizCode);
    }

    // bizcodes/models/enums are shared by all RestService
    RestServices restApps = new RestServices();
    restApps.setBizCodes(bizCodes);
    restApps.setEnumInfos(spiEnums);
    restApps.setModelInfos(new ArrayList<>(spiModelMap.values()));

    // in most cases, a project = one RestService
    boolean emptyOrOnlyOne = (restAppMap.isEmpty() || restAppMap.size() == 1);
    // apidoclet customized configuration
    ApiDocJson apiDocJson = parseApiDocletJson(options);
    for (String appName : restAppMap.keySet()) {
      RestService restApp = restAppMap.get(appName);
      restApp.setApiDocJson(apiDocJson);
      // post processors
      ProcessorContext context =
          new ProcessorContext.Default(spiModelMap, spiBizCodeMap, this.options);
      postProcessRestClasses(restApp, context);
      // rest service post processors ,to handle build time/artifact info etc;
      postProcessRestService(restApp, context);

      restApps.addApp(restApp);

      if (emptyOrOnlyOne) {
        // only exists one rest app?all models/bizcodes/enums belong to it
        restApp.setModelInfos(restApps.getModelInfos());
        restApp.setBizCodes(new HashSet<>(restApps.getBizCodes()));
        restApp.setEnumInfos(restApps.getEnumInfos());
      } else {
        // partition bizCodes/enums/modelinfos
        partitionBizCodes(bizCodes, restApp);
        partitionEnums(spiEnums, restApp);
        partitionModels(restApps.getModelInfos(), restApp);
      }
    }

    if (options.isPrint()) {
      options.getDocReporter().printNotice("\n");
      options.getDocReporter().printNotice("finally resolved (json format) ");
      options.getDocReporter().printNotice(JSON.toJSONString(restApps));
      options.getDocReporter().printNotice("\n");
    }

    // invoke exporter
    this.exportRestServices(restApps);
    return true;
  }

  /**
   * partition enums
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
   * partition models
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
   * partition bizcodes
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
   * parse apidoclet customized configuration
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

      // env config
      JSONObject envJson = configJson.getJSONObject("env");
      if (envJson != null && !envJson.isEmpty()) {
        EnvVariable ev = new EnvVariable();
        JSONArray array = envJson.getJSONArray("headers");
        if (array != null && array.size() > 0) {
          String[] headers = new String[array.size()];
          for (int i = 0; i < headers.length; i++) {
            headers[i] = array.getString(i);
          }
          ev.setHeaders(headers);

          JSONArray rows = envJson.getJSONArray("rows");
          int rowSize = 0;
          if (rows != null && (rowSize = rows.size()) > 0) {
            EnvVariable.Row[] allRows = new EnvVariable.Row[rowSize];
            for (int i = 0; i < rowSize; i++) {
              JSONArray columns = rows.getJSONArray(i);
              if (columns == null || columns.isEmpty()) {
                continue;
              }
              EnvVariable.Row row = new EnvVariable.Row();
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
          apiDocJson.setEnv(ev);
        }
      }

      // others in apidoclet.json
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
   * post process RestService common metadata
   */
  private void postProcessRestService(RestService restApp,
      ProcessorContext context) {
    if (this.restServicePostProcessors != null
        && !this.restServicePostProcessors.isEmpty()) {
      for (RestServicePostProcessor processor : restServicePostProcessors) {
        processor.postProcess(restApp, context);
      }
    }
    restApp.setBuildAt(new Date());
    restApp.setLastBuildAt(new Date());
    restApp.setBuildBy(context.getOpitons().getBuildBy());
    restApp.setBuildIpAddress(context.getOpitons().getBuildIpAddress());
    // artifact info
    restApp.setArtifact(this.resolveArtifactIfAny(context.getOpitons()));
  }

  /**
   * @see ApiDoclet#restClassPostProcessors
   * @author huisman
   */
  private void postProcessRestClasses(RestService app, ProcessorContext context) {
    List<RestClass> spiClasses = app.getRestClasses();
    if (spiClasses != null && !spiClasses.isEmpty()) {
      for (RestClass spiClass : spiClasses) {
        // invoke all class post processor
        postProccessRestClass(app, spiClass, context);
        // invoke all method post processor
        postProcessSpiMethod(spiClass, context);
      }
    }
  }

  /**
   * 
   * @see ApiDoclet#restClassPostProcessors
   * @author huisman
   */
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
   * @see ApiDoclet#restClassMethodPostProcessors
   */
  private void postProcessSpiMethod(RestClass spiClass, ProcessorContext context) {
    List<RestClass.Method> methods = spiClass.getMethods();
    if (methods != null && !methods.isEmpty()) {
      for (RestClass.Method method : methods) {
        // invoke customized implementation post processor
        if (this.restClassMethodPostProcessors != null
            && !this.restClassMethodPostProcessors.isEmpty()) {
          for (RestClassMethodPostProcessor processor : restClassMethodPostProcessors) {
            processor.postProcess(method, spiClass, context);
          }
        }
        // attempt to resolve api version
        setAnyVersion(method);
      }
    }
  }



  /**
   * api version resolver( spi?)
   */
  private static void setAnyVersion(RestClass.Method restAccessPoint) {
    String version = restAccessPoint.getVersion();
    if (StringUtils.isNullOrEmpty(version)) {
      String path = null;
      EndpointMapping mapping = restAccessPoint.getMapping();
      if (mapping != null) {
        path = mapping.getPath();
      }
      if (!StringUtils.isNullOrEmpty(path)) {
        if (path.startsWith(RestService.VERSION_PREFIX_IN_PATH)) {
          int firstSlashIndex = path.indexOf("/");
          int secondSlashIndex = path.indexOf("/", firstSlashIndex + 1);
          if (firstSlashIndex >= 0 && secondSlashIndex > 0) {
            version = path.substring(firstSlashIndex + 1, secondSlashIndex);
          }
        }
      }
      if (StringUtils.isNullOrEmpty(version)) {
        version = RestService.DEFAULT_VERSION;
      }
      restAccessPoint.setVersion(version);
    }
  }

  /**
   * resolve artifact
   */
  private Artifact resolveArtifactIfAny(ApiDocletOptions options) {
    org.apidoclet.model.Artifact artifact = new Artifact();
    artifact
        .setGroupId(options.optionValue(ApiDocletOptions.ARTIFACT_GROUP_ID));
    artifact.setArtifactId(options.optionValue(ApiDocletOptions.ARTIFACT_ID));
    artifact.setVersion(options.optionValue(ApiDocletOptions.ARTIFACT_VERSION));

    // readme.MD
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
        // only choose first
        File readme = candicateFiles[0];
        artifact.setReadmeFileName(readme.getName());

        try (BufferedReader reader =
            Files.newBufferedReader(Paths.get(readme.getAbsolutePath()),
                StandardCharsets.UTF_8)) {
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

  /* non-public */class RestServiceResovler {
    public void resolveRestService(ClassDoc classDoc, ApiDocletOptions options,
        Map<String, RestService> resolvedRestServiceMap) {

      String serviceName =
          ApiDoclet.this.filterRestClassAndGetServiceName(classDoc);
      if (StringUtils.isNullOrEmpty(serviceName)) {
        return;
      }
      // deduce service identity
      String providedAppName =
          options.optionValue(ApiDocletOptions.DEFAULT_PREFIX + serviceName);
      if (StringUtils.isNullOrEmpty(providedAppName)) {
        providedAppName = options.getAppName();
      }
      if (StringUtils.isNullOrEmpty(providedAppName)) {
        providedAppName = serviceName;
      }
      RestService app = new RestService(serviceName, providedAppName);
      // use already existed RestService
      if (resolvedRestServiceMap.containsKey(app.getApp())) {
        app = resolvedRestServiceMap.get(app.getApp());
      }
      // processing RestClass
      RestClass spiClass =
          ApiDoclet.this.restClassProvider.produce(classDoc, options);
      // resolve EndpointMapping on this class
      spiClass.setEndpointMapping(ApiDoclet.this.resolveEndpointMapping(
          classDoc.annotations(), classDoc.position()));

      options.getDocReporter().printNotice(
          "found rest class:" + classDoc.qualifiedName());
      app.addSpiClass(spiClass);

      // processing methods
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
     * parse all the RestClass.Methods in this class
     */
    private void processMethods(ClassDoc classDoc, ApiDocletOptions options,
        RestClass restClass) {
      options.getDocReporter().printNotice(
          "processing methods in class: " + classDoc.qualifiedName());

      MethodDoc[] methods = classDoc.methods();
      if (methods == null || methods.length == 0) {
        options.getDocReporter().printNotice("methods not found.");
        return;
      }
      for (MethodDoc methodDoc : methods) {
        if (ApiDoclet.this.acceptThisMethod(methodDoc)) {
          // construct a RestMethod,responsible for parsing javadoc tags/javadoc comment/method
          // returnType etc;
          RestClass.Method method = processRestClassMethod(classDoc, methodDoc);
          // resolve bizcodes on this method if any
          method.setBizCodes(ApiDoclet.this.resolveMethodBizCodes(classDoc,
              methodDoc));

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
     * parse one RestMethod
     */
    private RestClass.Method processRestClassMethod(ClassDoc classDoc,
        MethodDoc methodDoc) {
      RestClass.Method restMethod = new RestClass.Method();
      // method metadata
      String declaringClassName =
          methodDoc.containingClass().qualifiedTypeName();
      restMethod.setDeclaredClass(declaringClassName);
      restMethod.setMethodName(methodDoc.name());

      // generate method identity,format: methodName(parameterType,parameterType)
      // exclude the access modifier
      StringBuilder identityStr =
          new StringBuilder(restMethod.getMethodName() + "(");

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

    /**
     * parse method parameters
     */
    private void processMethodParameter(MethodDoc methodDoc,
        RestClass.Method method, ApiDocletOptions options) {
      // @param tag
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

      List<PathParam> pathParams = new ArrayList<PathParam>();
      List<QueryParam> queryParams = new ArrayList<QueryParam>();
      List<HeaderParam> requestHeaders = new ArrayList<HeaderParam>();
      List<MethodParameter> methodParameters = new ArrayList<>();
      Parameter[] parameters = methodDoc.parameters();
      int len = parameters.length;
      for (int i = 0; i < len; i++) {
        Parameter parameter = parameters[i];
        // ignore this parameter?
        if (ApiDoclet.this.shouldSkipParameter(parameter)) {
          continue;
        }
        // skip this type?
        if (ApiDoclet.this.shouldSkipType(parameter.type().qualifiedTypeName())) {
          continue;
        }
        // javadoc comment
        String comment =
            anyComment ? paramCommentMap.get(parameter.name()) : null;
        methodParameters.add(resolveMethodParameter(methodDoc, parameter,
            comment, i, i == (len - 1)));

        // parameter resolver
        QueryParam qp = null;
        PathParam pp = null;
        HeaderParam hp = null;
        RequestBody requestBody = null;
        if ((qp = ApiDoclet.this.resolveQueryParam(parameter, comment)) != null) {
          // var-arg
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
     * parse one parameter
     * 
     * @param methodDoc javadoc method
     * @param parameter javadoc parameter
     * @param comment parameter comment
     * @param paramIndex parameter index,from zero
     * @param lastParam true only if this is the last parameter
     * @author huisman
     */
    private MethodParameter resolveMethodParameter(MethodDoc methodDoc,
        Parameter parameter, String comment, int paramIndex, boolean lastParam) {
      // method parameter metadata
      MethodParameter methodParameter = new MethodParameter();
      methodParameter.setComment(comment);
      methodParameter.setClassName(parameter.type().qualifiedTypeName());
      methodParameter.setIndex(paramIndex);
      methodParameter.setMethodName(methodDoc.name());
      methodParameter.setName(parameter.name());
      methodParameter.setVarargs(false);

      if (lastParam) {
        // var-arg ,must be last parameter according to JLS
        if (methodDoc.isVarArgs()) {
          methodParameter.setVarargs(true);
        }
      }
      // parameter type
      Type type = parameter.type();
      methodParameter.setTypeInfo(ApiDoclet.this.resolveTypeInfo(type));

      // parse annotations on this parameter
      AnnotationDesc[] annotationDescs = parameter.annotations();
      if (annotationDescs != null && annotationDescs.length > 0) {
        JavaAnnotations javaAnnotations = new JavaAnnotations();
        for (AnnotationDesc annotationDesc : annotationDescs) {
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

    /**
     * parse method return type
     */
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

    /**
     * parse api version and javadoc tags/comments on this method
     */
    private void processMethodDocTagsAndAnnotations(MethodDoc methodDoc,
        RestClass.Method restMethod, ApiDocletOptions options) {
      // api version
      Tag[] versionTags = methodDoc.tags(StandardDocTag.TAG_VERSION);
      String version =
          ((versionTags == null || versionTags.length == 0) ? ""
              : versionTags[0].text().trim());
      if (StringUtils.isNullOrEmpty(version)) {
        version = options.getVersion();
      }
      restMethod.setVersion(version);
      // api author
      Tag[] authorTags = methodDoc.tags(StandardDocTag.TAG_AUTHOR);
      StringBuilder authors = new StringBuilder();
      if (authorTags != null && authorTags.length > 0) {
        for (Tag tag : authorTags) {
          authors.append("," + StringUtils.trim(tag.text()));
        }
        authors.deleteCharAt(0);
      }
      restMethod.setAuthor(authors.toString());

      // @since not exists ,set to current date as fallback;
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
              '@' + StandardDocTag.TAG_PARAM, '@' + StandardDocTag.TAG_SINCE,
              '@' + StandardDocTag.TAG_SUMMARY,
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
          // adapt AnnotationDesc to JavaAnnotation
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
