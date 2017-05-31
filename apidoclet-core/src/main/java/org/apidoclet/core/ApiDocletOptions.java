package org.apidoclet.core;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apidoclet.core.util.ApiDocletClassLoader;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.RestService;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 * api doclet command-line options
 * 
 * @author huisman
 */
public class ApiDocletOptions {
  /**
   * options have key and value - length = 2
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN2 = new HashSet<String>(
      Arrays.asList(ApiDocletOptions.CLASS_DIR, ApiDocletOptions.VERSION,
          ApiDocletOptions.ARTIFACT_ID, ApiDocletOptions.ARTIFACT_VERSION,
          ApiDocletOptions.ARTIFACT_GROUP_ID, ApiDocletOptions.APP_NAME,
          ApiDocletOptions.APP, ApiDocletOptions.EXPORT_TO, ApiDocletOptions.WEB_CONTEXT_PATH));

  /**
   * option length = 1 , only have one option key
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN1 = new HashSet<String>(
      Arrays.asList(ApiDocletOptions.PRINT,
          ApiDocletOptions.IGNORE_VIRTUAL_PATH));

  /**
   * the default value delimiter that use to split an option value
   */
  public static final String DEFAULT_VALUE_DILIMETER = ",";

  /**
   * all option key prefix
   */
  public static final String DEFAULT_PREFIX = "-";

  /**
   * indicates print the parsed doc to console
   */
  public static final String PRINT = DEFAULT_PREFIX + "print";

  /**
   * compiled classes output directory path, apidoclet classloader use it to find class file
   */
  public static final String CLASS_DIR = DEFAULT_PREFIX + "classdir";
  /**
   * default rest api version
   */
  public static final String VERSION = DEFAULT_PREFIX + "version";

  /**
   * web application's context path,prepend it to request path
   */
  public static final String WEB_CONTEXT_PATH = DEFAULT_PREFIX + "webContextPath";

  /**
   * workspace or project root directory
   */
  public static final String PROJECT_ROOT_DIR = DEFAULT_PREFIX
      + "projectRootDir";

  /**
   * jar groupId
   */
  public static final String ARTIFACT_GROUP_ID = DEFAULT_PREFIX
      + "artifactGroupId";

  /**
   * jar artifactId
   */
  public static final String ARTIFACT_ID = DEFAULT_PREFIX + "artifactId";

  /**
   * jar artifact version
   */
  public static final String ARTIFACT_VERSION = DEFAULT_PREFIX
      + "artifactVersion";

  /**
   * 是否忽略虚拟路径
   */
  public static final String IGNORE_VIRTUAL_PATH = DEFAULT_PREFIX
      + "ignoreVirtualPath";
  /**
   * source java file directory
   */
  public static final String SOURCE_PATH = "-sourcepath";
  /**
   * could be spring.application.name or FeignClient#name, global service identifier
   * 
   * @see {@link RestService#getName()}
   */
  public static final String APP = DEFAULT_PREFIX + "app";
  /**
   * {@code -app } web-ui display name
   */
  public static final String APP_NAME = DEFAULT_PREFIX + "appName";
  /**
   * where should we export the parsed doc to
   */
  public static final String EXPORT_TO = DEFAULT_PREFIX + "exportTo";

  /**
   * non-standard apidoclet options
   */
  private Map<String, String> othersOptions = new HashMap<String, String>();

  /**
   * apidoclet classloader , sometimes we need to invoke a class field( reflect)
   */
  private ClassLoader apidocletClassLoader = null;
  /**
   * compiled classes output directory, apidoclet classloader use it to locate a class file if necessary
   */
  private String classdir;
  /**
   * where should we export our parsed doc to
   */
  private String exportTo;
  /**
   * service global identify
   */
  private String app;
  /**
   * app's web-ui display name
   */
  private String appName;
  /**
   * 是否忽略虚拟路径
   */
  private boolean ignoreVirtualPath;
  /**
   * IP address
   */
  private String buildIpAddress;
  /**
   * who build
   */
  private String buildBy;

  /**
   * whether output the parsed doc to console or not
   */
  private boolean print = false;

  /**
   * workspace or project root directory
   */
  private String projectRootDir;

  /**
   * web application's context
   */
  private String webCotextPath;


  /**
   * default method version when {@code @version} doc tag doesn't exists
   */
  private String version;

  private ApiDocletOptions() {
    super();
  }

  /**
   * 当前项目的根目录
   */
  public String getProjectRootDir() {
    return this.projectRootDir;
  }


  /**
   * 发起构建者的IP地址
   * 
   * @author huisman
   */
  public String getBuildIpAddress() {
    return buildIpAddress;
  }

  /**
   * 被谁构建
   * 
   * @author huisman
   */
  public String getBuildBy() {
    return buildBy;
  }

  /**
   * 是否忽略虚拟路径，如果不忽略虚拟路径，生成api接口地址的时候会自动把应用的app添加到path前。<br/>
   * 比如，某个应用的app：users，接口路径为：/v1/friends/{userId}，如果ignoreVirtualPath=false，<br/>
   * 最终文档的接口地址为：/users/v1/friends/{userId},如果ignoreVirtualPath=true,则文档中接口地址为：/v1/friends/{userId}
   */
  public boolean isIgnoreVirtualPath() {
    return ignoreVirtualPath;
  }

  /**
   * 获取特定选项的值
   * 
   * @author huisman
   * @version v1
   */
  public String optionValue(String key) {
    return this.othersOptions.get(key);
  }


  /**
   * @return the web application's context
   */
  public String getWebCotextPath() {
    return this.webCotextPath;
  }



  /**
   * 主要用于javadoc执行过程中打印错误信息
   */
  private DocErrorReporter docReporter;

  /**
   * 当前项目编译后classes文件输出的目录,maven项目的话为：${project}/target/classes
   * 
   * @author huisman
   */
  public String getClassdir() {
    return classdir;
  }



  /* non-public */Map<String, String> getOthersOptions() {
    return othersOptions;
  }

  /**
   * 用于javadoc解析执行过程中打印错误信息
   */
  public DocErrorReporter getDocReporter() {
    return docReporter;
  }

  /**
   * 序列化后的信息导出到那个url
   * 
   * @author huisman
   */
  public String getExportTo() {
    return exportTo;
  }



  /**
   * API文档的类加载器，会从classdir以及classpath里搜索加载特定类
   * 
   * @author huisman
   */
  public ClassLoader getApidocletClassLoader() {
    return apidocletClassLoader;
  }

  /**
   * 当前应用的标识，如果是微服务，则为微服务的服务名称
   */
  public String getApp() {
    return app;
  }

  /**
   * 当前应用app的文字描述，用于展示
   * 
   * @author huisman
   */
  public String getAppName() {
    return appName;
  }

  /**
   * 默认版本号
   * 
   * @author huisman
   */
  public String getVersion() {
    return version;
  }


  /**
   * 是否打印解析后model信息
   * 
   * @author huisman
   */
  public boolean isPrint() {
    return print;
  }



  @Override
  public String toString() {
    return "ApiDocletOptions [classdir=" + classdir + ", exportTo=" + exportTo
        + ", app=" + app + ", appName=" + appName + ", ignoreVirtualPath="
        + ignoreVirtualPath + ", buildIpAddress=" + buildIpAddress
        + ", buildBy=" + buildBy + ", print=" + print + ", version=" + version
        + "]";
  }

  /**
   * 将classdir转换为绝对路径
   */
  private static String transformClassDir(String classdir) {
    if (StringUtils.isNullOrEmpty(classdir)) {
      classdir = ".";
    }
    // 不是绝对路径
    if (!Paths.get(classdir).isAbsolute()) {
      Path base = Paths.get(new File("").getAbsolutePath(), classdir);
      return base.normalize().toString();
    }
    return classdir;
  }

  /**
   * 从命令参数里读取我们需要的选项信息
   * 
   * @author huisman
   */
  public static ApiDocletOptions readFromCommandLine(RootDoc rootDoc) {
    rootDoc.printNotice("available options：");

    // 程序启动参数
    String[][] options = rootDoc.options();
    ApiDocletOptions apiDocletOptions = new ApiDocletOptions();
    Map<String, String> otherOptions = new HashMap<>();
    for (int i = 0; i < options.length; i++) {
      String[] input = options[i];
      if (input.length < 1) {
        continue;
      }
      if (StringUtils.isNullOrEmpty(options[i][0])) {
        continue;
      }

      rootDoc.printNotice(Arrays.toString(options[i]));

      if (input.length == 1) {
        otherOptions.put(options[i][0], null);
      } else {
        otherOptions.put(options[i][0], options[i][1]);
      }
      switch (options[i][0]) {
        case ApiDocletOptions.WEB_CONTEXT_PATH:
          apiDocletOptions.webCotextPath = StringUtils.trim(options[i][1]);
          break;
        case ApiDocletOptions.CLASS_DIR:
          apiDocletOptions.classdir = transformClassDir(options[i][1]);
          break;
        case ApiDocletOptions.PROJECT_ROOT_DIR:
          apiDocletOptions.projectRootDir = options[i][1];
          break;
        case ApiDocletOptions.VERSION:
          apiDocletOptions.version = (options[i][1]);
          break;

        case ApiDocletOptions.APP:
          apiDocletOptions.app = (options[i][1]);
          break;

        case ApiDocletOptions.APP_NAME:
          apiDocletOptions.appName = (options[i][1]);
          break;

        case ApiDocletOptions.EXPORT_TO:
          apiDocletOptions.exportTo = (options[i][1]);
          break;
        case ApiDocletOptions.PRINT:
          apiDocletOptions.print = (true);
          break;
        case ApiDocletOptions.IGNORE_VIRTUAL_PATH:
          apiDocletOptions.ignoreVirtualPath = (true);
        default:
          break;
      }
    }

    rootDoc.printNotice("\n");
    apiDocletOptions.buildBy = (System.getProperty("user.name"));
    String ipAddress = "unknown";
    try {
      ipAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {}
    apiDocletOptions.buildIpAddress =
        (ipAddress + " " + System.getProperty("os.name"));

    // fall back to current directory
    if (StringUtils.isNullOrEmpty(apiDocletOptions.getClassdir())) {
      apiDocletOptions.classdir = (transformClassDir("."));
    }

    apiDocletOptions.docReporter = (rootDoc);
    // 不可修改
    apiDocletOptions.othersOptions = Collections.unmodifiableMap(otherOptions);

    // 默认classes加载器，如果我们需要加载类
    String classdir = apiDocletOptions.classdir;

    apiDocletOptions.apidocletClassLoader =
        new ApiDocletClassLoader(ApiDoclet.class.getClassLoader(), classdir);
    return apiDocletOptions;
  }


  /**
   * command line option，必须有此方法。 我们支持：-classdir<br/>
   * javadoc自动调用以决定命令行option加上option的参数值的总长度。<br/>
   * (每个option可能有值，也可能无值，javadoc会把选项放在一个二维数组里，返回值可以用来设置第二维数组的长度）
   * 
   * @author huisman
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

}
