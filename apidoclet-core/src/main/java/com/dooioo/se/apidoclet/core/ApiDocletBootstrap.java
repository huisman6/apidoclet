package com.dooioo.se.apidoclet.core;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 ** This is an example of a starting class for a doclet, showing the entry-point methods. A starting
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
 * @see http://docs.oracle.com/javase/1.5.0/docs/guide/javadoc/doclet/spec/index.html
 * @see https://docs.oracle.com/javase/6/docs/technotes/guides/javadoc/doclet/overview.html
 */
public final class ApiDocletBootstrap {

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
    return ApiDocletOptions.optionLength(option);
  }

  /**
   * javadoc 自动调用以验证option是否符合需要。
   * 
   * @param options
   * @param reporter
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
   * javadoc解析完源代码之后，会回调此方法
   * 
   * @param rootDoc rootDoc
   * @return true
   */
  public static boolean start(RootDoc rootDoc) {
    ApiDocletBuilder builder = new ApiDocletBuilder();
    ApiDoclet apiDoclet = builder.
    // 业务码的解析
        enableBizCodeProviders().
        // 方法参数解析
        enableMethodParameterResolvers().
        // model 解析
        enableModelProviders().
        // 方法过滤
        enableRestMethodFilters().
        // rest服务类的过滤
        enableRestServiceFilters().
        // 类型过滤
        enableSkippedTypeFilters().
        // 将javadoc 的Type转换为TypeInfo
        enableTypeInfoProviders()
        // request mapping信息的解析
        .enableEndpointMappingProviders()
        // 方法级别的后续处理
        .enableRestClassMethodPostProcessors()
        // 类的后续处理
        .enableRestClassPostProcessors()
        // 微服务的后续处理
        .enableRestServicePostProcessors()
        // 命令行启动参数
        .options(rootDoc)
        // 解析后的数据导出
        .enableRestServicesExports().build();
    return apiDoclet.startParseSourceCodes(rootDoc);
  }
}
