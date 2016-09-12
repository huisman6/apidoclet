package com.dooioo.se.apidoclet.core;

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

import com.dooioo.se.apidoclet.core.util.ApiDocletClassLoader;
import com.dooioo.se.apidoclet.core.util.StringUtils;
import com.dooioo.se.apidoclet.model.RestService;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

/**
 * API doclet的配置，除了标准参数外，其他的参数保存在othersOptions
 * 
 * @author huisman
 */
public class ApiDocletOptions {
  /**
   * 支持的命令行参数为key,value两个长度的选项
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN2 = new HashSet<>(
      Arrays.asList(ApiDocletOptions.CLASS_DIR, ApiDocletOptions.VERSION,
          ApiDocletOptions.ARTIFACT_ID, ApiDocletOptions.ARTIFACT_VERSION,
          ApiDocletOptions.ARTIFACT_GROUP_ID, ApiDocletOptions.APP_NAME,
          ApiDocletOptions.APP, ApiDocletOptions.EXPORT_TO));

  /**
   * 仅支持key，长度为1的选项
   */
  private static final Set<String> SUPPORT_OPTIONS_LEN1 = new HashSet<>(
      Arrays.asList(ApiDocletOptions.PRINT,
          ApiDocletOptions.IGNORE_VIRTUAL_PATH));

  /**
   * 默认选项值里的分隔符
   */
  public static final String DEFAULT_SPLIT_STR = ",";

  /**
   * 所有选项必须以-开头
   */
  public static final String DEFAULT_PREFIX = "-";

  /**
   * 是否打印所有解析后的app信息
   */
  public static final String PRINT = DEFAULT_PREFIX + "print";

  /**
   * classes文件目录
   */
  public static final String CLASS_DIR = DEFAULT_PREFIX + "classdir";
  /**
   * 默认版本号
   */
  public static final String VERSION = DEFAULT_PREFIX + "version";

  /**
   * 工程根目录
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
   * jar ARTIFACT_VERSION
   */
  public static final String ARTIFACT_VERSION = DEFAULT_PREFIX
      + "artifactVersion";

  /**
   * 是否忽略虚拟路径
   */
  public static final String IGNORE_VIRTUAL_PATH = DEFAULT_PREFIX
      + "ignoreVirtualPath";
  /**
   * 源文件路径，java doc标注参数
   */
  public static final String SOURCE_PATH = "-sourcepath";
  /**
   * app名称 {@link RestService#getName()}
   */
  public static final String APP = DEFAULT_PREFIX + "app";
  /**
   * 默认app 描述 {@link RestService#getAppName()}
   */
  public static final String APP_NAME = DEFAULT_PREFIX + "appName";
  /**
   * 导出url接口
   */
  public static final String EXPORT_TO = DEFAULT_PREFIX + "exportTo";

  /**
   * 不能修改
   */
  private Map<String, String> othersOptions = null;

  /**
   * api 文档的类加载器，根据classdir和classpath加载特定类
   */
  private ClassLoader apidocletClassLoader = null;
  /**
   * 源文件编译后classes所在目录，我们可能要从源文件中查找BizCode
   */
  private String classdir;
  private String exportTo;
  private String app;
  /**
   * 应用程序名
   */
  private String appName;
  /**
   * 是否忽略虚拟路径
   */
  private boolean ignoreVirtualPath;
  /**
   * 构建者的ip地址
   */
  private String buildIpAddress;
  /**
   * 被谁构建
   */
  private String buildBy;

  /**
   * 是否打印解析后的数据
   */
  private boolean print = false;

  private String projectRootDir;


  /**
   * 默认版本号，如果没有指定@version
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
    rootDoc.printNotice("命令行参数如下：");
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
      if (input.length == 1) {
        otherOptions.put(options[i][0], null);
      } else {
        otherOptions.put(options[i][0], options[i][1]);
      }
      switch (options[i][0]) {
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

      apiDocletOptions.buildBy = (System.getProperty("user.name"));
      String ipAddress = "unknown";
      try {
        ipAddress = InetAddress.getLocalHost().getHostAddress();
      } catch (Exception e) {
        e.printStackTrace();
      }
      apiDocletOptions.buildIpAddress =
          (ipAddress + " " + System.getProperty("os.name"));

      rootDoc.printNotice(Arrays.toString(options[i]));
    }

    rootDoc.printNotice("\n");

    // 没有的话，取当前目录
    if (StringUtils.isNullOrEmpty(apiDocletOptions.getClassdir())) {
      apiDocletOptions.classdir = (transformClassDir("."));
    }

    rootDoc.printNotice(apiDocletOptions.toString());
    rootDoc.printNotice("\n");

    apiDocletOptions.docReporter = (rootDoc);
    // 不可修改
    apiDocletOptions.othersOptions = Collections.unmodifiableMap(otherOptions);
    
    // 默认classes加载器，如果我们需要加载类
    String classdir=apiDocletOptions.classdir;
    
    apiDocletOptions.apidocletClassLoader =
        new ApiDocletClassLoader(ApiDoclet.class.getClassLoader(),classdir);
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
