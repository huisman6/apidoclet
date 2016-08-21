package com.dooioo.se.apidoclet.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dooioo.se.lorik.apidoclet.contract.ContractConfigurer;
import com.dooioo.se.lorik.apidoclet.contract.SpiBizCodes;
import com.dooioo.se.lorik.apidoclet.contract.config.ApiDocJson;
import com.dooioo.se.lorik.apidoclet.contract.config.EnvVariable;
import com.dooioo.se.lorik.apidoclet.contract.model.FieldInfo;
import com.dooioo.se.lorik.apidoclet.contract.model.RestApp;
import com.dooioo.se.lorik.apidoclet.contract.model.RestApps;
import com.dooioo.se.lorik.apidoclet.contract.model.SpiBizCode;
import com.dooioo.se.lorik.apidoclet.contract.model.SpiClass;
import com.dooioo.se.lorik.apidoclet.contract.model.SpiEnum;
import com.dooioo.se.lorik.apidoclet.contract.model.SpiMethod;
import com.dooioo.se.lorik.apidoclet.contract.model.SpiModel;
import com.dooioo.se.lorik.apidoclet.util.ApiDocletClassLoader;
import com.dooioo.se.lorik.apidoclet.util.StringUtils;
import com.dooioo.se.lorik.spi.view.support.LorikRest.Feature;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 * This is an example of a starting class for a doclet, showing the entry-point methods. A starting
 * class must import com.sun.javadoc.* and implement the start(RootDoc) method, as described in the
 * package description. If the doclet takes command line options, it must also implement
 * optionLength and validOptions.
 * 
 * A doclet supporting the language features added since 1.1 (such as generics and annotations)
 * should indicate this by implementing languageVersion. In the absence of this the doclet should
 * not invoke any of the Doclet API methods added since 1.5, and the results of several other
 * methods are modified so as to conceal the new constructs (such as type parameters) from the
 * doclet.
 * 
 * To start the doclet, pass -doclet followed by the fully-qualified name of the starting class on
 * the javadoc tool command line.
 * 
 * 
 * @author huisman
 * @since 1.0.0
 * @see http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html
 * @see https://docs.oracle.com/javase/6/docs/technotes/guides/javadoc/doclet/overview.html
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
public class ApiDoclet {
  // 默认classes加载器，如果我们需要加载类
  public static ApiDocletClassLoader APIDOCLET_CLASSES_LOADER =
      new ApiDocletClassLoader(ApiDoclet.class.getClassLoader());

  /**
   * 支持的命令行参数为key,value两个长度的选项
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN2 =
      new HashSet<>(Arrays.asList(ApiDocletOptions.CLASS_DIR, ApiDocletOptions.VERSION,
          ApiDocletOptions.ARTIFACT_ID, ApiDocletOptions.ARTIFACT_VERSION,
          ApiDocletOptions.ARTIFACT_GROUP_ID, ApiDocletOptions.APP_NAME, ApiDocletOptions.APP,
          ApiDocletOptions.EXPORT_TO));

  /**
   * 仅支持key，长度为1的选项
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN1 =
      new HashSet<>(Arrays.asList(ApiDocletOptions.PRINT, ApiDocletOptions.IGNORE_VIRTUAL_PATH));

  /**
   * lorik feature可能响应的业务码
   */
  private static final Map<String, SpiBizCode> lorikFeatureBizCodeMap = new HashMap<>();

  static {
    lorikFeatureBizCodeMap.put(Feature.NullTo404.name(), SpiBizCodes.LORIK_REST_NULL_TO_404);
  }

  /**
   * command line option，必须有此方法。 我们支持：-classdir<br/>
   * javadoc自动调用以决定命令行option加上option的参数值的总长度。<br/>
   * (每个option可能有值，也可能无值，javadoc会把选项放在一个二维数组里，返回值可以用来设置第二维数组的长度）
   * 
   * @author huisman
   * @param option
   * @since 2016年1月16日
   */
  public static int optionLength(String option) {
    if (SUPPORT_OPTIONS_LEN2.contains(option)) {
      // 表示-classdir option只有一个值（-classdir 后紧跟的字符串)，总共2个元素
      return 2;
    } else if (SUPPORT_OPTIONS_LEN1.contains(option)) {
      return 1;
    }
    // 0 不支持其他option,2默认都是key value
    return 2;
  }

  /**
   * javadoc 自动调用以验证option是否符合需要。
   * 
   * @param options
   * @param reporter
   * @since 2016年1月16日
   */
  public static boolean validOptions(String options[][], DocErrorReporter reporter) {
    return true;
  }

  /**
   * NOTE: Without this method present and returning LanguageVersion.JAVA_1_5, Javadoc will not
   * process generics because it assumes LanguageVersion.JAVA_1_1
   * 
   * @return language version (hard coded to LanguageVersion.JAVA_1_5)
   */
  public static LanguageVersion languageVersion() {
    return LanguageVersion.JAVA_1_5;
  }


  /**
   * access point
   * 
   * @since 1.0.0
   * @param rootDoc rootDoc
   * @return true
   */
  public static boolean start(RootDoc rootDoc) {
    // 命令行选项
    ApiDocletOptions options = ContractConfigurer.readCommandLineOptions(rootDoc);
    // 配置类加载器的根目录
    APIDOCLET_CLASSES_LOADER.setClassdir(options.getClassdir());

    ClassDoc[] classDocs = rootDoc.classes();
    // 所有解析出来的微服务
    Map<String, RestApp> restAppMap = new HashMap<>();
    // 所有静态、公开访问的业务码字段
    List<SpiBizCode> bizCodes = new ArrayList<>();
    // 所有枚举
    List<SpiEnum> spiEnums = new ArrayList<>();
    // 所有SPI model
    Map<String, SpiModel> spiModelMap = new HashMap<>();
    // spibizcode 和lorik code的映射
    Map<Integer, SpiBizCode> spiBizCodeMap = new HashMap<>();

    for (ClassDoc classDoc : classDocs) {
      // 所有枚举
      if (ContractConfigurer.getDefaultSpiEnumFilter().accept(classDoc, options)) {
        SpiEnum senum = ContractConfigurer.getDefaultSpiEnumFilter().handle(classDoc, options);
        if (senum != null) {
          spiEnums.add(senum);
        }
      }
      // 判断是否是FeignClient
      if (ContractConfigurer.getDefaultRestAppFilter().accept(classDoc, options)) {
        // 解析rest app -> FeiClient
        ContractConfigurer.resolveRestApp(classDoc, options, restAppMap);
      } else if (ContractConfigurer.getDefaultSpiModelFilter().accept(classDoc, options)) {
        // 解析所有的SPImodel，最后遍历所有解析的rest app ,
        // 如果rest app里的任Spi class的包前缀包含某个model的包前缀，则说明此model是此rest app的
        SpiModel model = ContractConfigurer.getDefaultSpiModelFilter().handle(classDoc, options);
        if (model != null) {
          spiModelMap.put(model.getClassName(), model);
        }
      } else if (ContractConfigurer.getDefaultBizCodeFilter().accept(classDoc, options)) {
        // 解析业务码 业务码必须为static /public 字段，最后遍历所有解析的rest app ,
        // 如果rest app里的任Spi class的包前缀包含某个BizCode的包前缀，则说明此BizCode是此rest app的
        List<SpiBizCode> handledCodes =
            ContractConfigurer.getDefaultBizCodeFilter().handle(classDoc, options);
        if (handledCodes != null && handledCodes.size() > 0) {
          bizCodes.addAll(handledCodes);
          for (SpiBizCode spiBizCode : handledCodes) {
            spiBizCodeMap.put(spiBizCode.getCode(), spiBizCode);
          }
        }
      }
    }
    // 最后，解析返回值，根据包名合并model/bizcode到每个rest app里
    RestApps restApps = new RestApps();
    restApps.setSpiBizCodes(bizCodes);
    restApps.setSpiEnums(spiEnums);
    restApps.setSpiModels(new ArrayList<>(spiModelMap.values()));

    // 只有一个
    boolean emptyOrOnlyOne = (restAppMap.isEmpty() || restAppMap.size() == 1);
    // 解析apidoclet json
    ApiDocJson apiDocJson = parseApiDocletJson(options);
    for (String appName : restAppMap.keySet()) {
      RestApp restApp = restAppMap.get(appName);
      restApp.setApiDocJson(apiDocJson);
      // 后续处理
      // 等所有model解析了之后，最后解析一次返回值的字段是泛型model的、业务码
      postProcessSpiClass(restApp, spiModelMap, spiBizCodeMap);
      // 处理附加信息，比如构建时间，构件信息
      postProcessApp(restApp, options);

      restApps.addApp(restApp);

      if (emptyOrOnlyOne) {
        // 如果只有一个rest app，model/bizcode全给它
        restApp.setSpiModels(restApps.getSpiModels());
        restApp.setSpiBizCodes(restApps.getSpiBizCodes());
        restApp.setSpiEnums(restApps.getSpiEnums());
      } else {
        // 划分业务码以及model stat
        ContractConfigurer.partitionBizCodes(bizCodes, restApp);
        ContractConfigurer.partitionSpiEnums(spiEnums, restApp);
        ContractConfigurer.partitionSpiModels(restApps.getSpiModels(), restApp);

      }
    }

    if (options.isPrint()) {
      options.getDocReporter().printNotice("\n");
      options.getDocReporter()
          .printNotice("finally resolved =========================================== as follow ");
      options.getDocReporter().printNotice("\n");
      options.getDocReporter().printNotice(restApps.toString());
      options.getDocReporter().printNotice("\n");
    }

    // 分发数据
    ContractConfigurer.exportRestApps(restApps, options);

    return true;
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
        options.getDocReporter()
            .printNotice("apidoclet.json not found ,ignored,file path: " + path.toAbsolutePath());
        return apiDocJson;
      }

      Object jsonObject = JSONObject.parse(Files.readAllBytes(path),
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
          //有headers 才设置env
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
   * 设置rest app附加属性
   */
  private static void postProcessApp(RestApp restApp, ApiDocletOptions options) {
    restApp.setBuildAt(new Date());
    restApp.setBuildBy(options.getBuildBy());
    restApp.setBuildIpAddress(options.getBuildIpAddress());

    // 构件信息
    restApp.setArtifact(ContractConfigurer.resolveArtifactIfAny(options));
  }


  /**
   * 最后再解析下返回值、业务码，如果字段是model的话，比如是Pagination<br>
   * 此时，所有model已经解析，可以根据字段的实际类型，重新设置泛型model字段的字段了。
   * 
   * @author huisman
   */
  private static void postProcessSpiClass(RestApp app, Map<String, SpiModel> modelMap,
      Map<Integer, SpiBizCode> spiBizCodeMap) {
    List<SpiClass> spiClasses = app.getSpiClasses();
    if (spiClasses != null && !spiClasses.isEmpty()) {
      for (SpiClass spiClass : spiClasses) {
        postProcessSpiMethod(spiClass, modelMap, spiBizCodeMap);
      }
    }
  }


  /**
   * 解析字段中可能类型为model的，仅支持三层嵌套
   */
  private static void postProcessSpiMethod(SpiClass spiClass, Map<String, SpiModel> modelMap,
      Map<Integer, SpiBizCode> spiBizCodeMap) {
    List<SpiMethod> methods = spiClass.getMethods();
    if (methods != null && !methods.isEmpty()) {
      for (SpiMethod method : methods) {
        // 如果没有版本号，则尝试解析版本号
        setAnyVersion(method);
        // 解析业务码
        transformSpiBizCode(method, spiBizCodeMap);
        // 尝试解析字段里的model。。的字段
        List<FieldInfo> fieldInfos = method.getReturnType().getFields();
        if (fieldInfos == null || fieldInfos.isEmpty()) {
          continue;
        }
        for (FieldInfo fieldInfo : fieldInfos) {
          if (!modelMap.containsKey(fieldInfo.getType().getActualType())) {
            continue;
          }

          List<FieldInfo> modelFields =
              modelMap.get(fieldInfo.getType().getActualType()).getFields();
          // 仅支持三层嵌套
          if (modelFields != null && modelFields.size() > 0) {
            for (FieldInfo innerField : modelFields) {
              if (!modelMap.containsKey(innerField.getType().getActualType())) {
                continue;
              }
              innerField
                  .setModelFields(modelMap.get(innerField.getType().getActualType()).getFields());
            }
          }
          // 从model里获取字段
          fieldInfo.setModelFields(modelFields);
        }

      }
    }
  }


  /**
   * 转换业务码
   */
  private static void transformSpiBizCode(SpiMethod spiMethod,
      Map<Integer, SpiBizCode> spiBizCodeMap) {
    // 方法的业务码
    Set<SpiBizCode> methodBizCodes = new HashSet<>();
    // 如果需要登录
    if (!spiMethod.isLoginNeedless()) {
      methodBizCodes.addAll(
          Arrays.asList(SpiBizCodes.API_GATEWAY_UNLOGIN, SpiBizCodes.API_GATEWAY_XTOKEN_EXPIRED,
              SpiBizCodes.API_GATEWAY_XTOKEN_NETWORK_CAUSE_FAILED, SpiBizCodes.INVALID_REQUEST));
    }

    // 业务特性
    String[] features = spiMethod.getLorikFeatures();
    if (features != null && features.length > 0) {
      for (String feature : features) {
        SpiBizCode sbc = lorikFeatureBizCodeMap.get(feature);
        if (sbc == null) {
          continue;
        }
        methodBizCodes.add(sbc);
      }
    }

    // 方法自己配置的
    int[] codes = spiMethod.getLorikCodes();
    if (codes != null && codes.length > 0) {
      for (int code : codes) {
        SpiBizCode sbc = spiBizCodeMap.get(code);
        if (sbc == null) {
          continue;
        }
        methodBizCodes.add(sbc);
      }
    }

    // 设置业务码
    spiMethod.setBizCodes(new ArrayList<>(methodBizCodes));

  }

  /**
   * 查找任何版本号，如果没有提供，先从request path中提取，最后返回默认值
   */
  private static void setAnyVersion(SpiMethod restAccessPoint) {
    String version = restAccessPoint.getVersion();
    // 如果没指定版本号
    if (StringUtils.isNullOrEmpty(version)) {
      // 没有则从request path中获取
      String path = restAccessPoint.getMapping().getPath();
      // 指定了request path
      if (!StringUtils.isNullOrEmpty(path)) {
        // 以版本号前缀开始，则截取版本号
        if (path.startsWith(RestApp.VERSION_PREFIX_IN_PATH)) {
          int firstSlashIndex = path.indexOf("/");
          int secondSlashIndex = path.indexOf("/", firstSlashIndex + 1);
          if (firstSlashIndex >= 0 && secondSlashIndex > 0) {
            version = path.substring(firstSlashIndex + 1, secondSlashIndex);
          }
        }
      }

      // 最终，还是没有解析出来实际版本，设置默认值
      if (StringUtils.isNullOrEmpty(version)) {
        version = RestApp.DEFAULT_VERSION;
      }
      restAccessPoint.setVersion(version);
    }
  }

}
