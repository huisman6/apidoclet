package org.apidoclet.core;

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
public final class ApiDocletBootStrap {

  /**
   * a callback function for javadoc program which is used to determine an command line option's
   * length
   */
  public static int optionLength(String option) {
    return ApiDocletOptions.optionLength(option);
  }

  /**
   * a callback function for javadoc program which is used to validate the parsed command line
   * options
   * 
   * @param options
   * @param reporter
   */
  public static boolean validOptions(String options[][],
      DocErrorReporter reporter) {
    // allow everything
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
   * javadoc doclet entry-point
   * 
   * @param rootDoc rootDoc
   * @return true
   */
  public static boolean start(RootDoc rootDoc) {
    ApiDocletBuilder builder = new ApiDocletBuilder();
    ApiDoclet apiDoclet = builder.
    // bizcode resolvers
        enableBizCodeProviders().
        // model resolver ,not necessary
        enableModelProviders().
        // restmethod filters
        enableRestMethodFilters().
        // restservice filters
        enableRestServiceFilters().
        // Type filters
        enableSkippedTypeFilters().
        // Type converters
        enableTypeInfoProviders()
        // http endpoint mapping resolvers
        .enableEndpointMappingProviders()
        // method level post processors
        .enableRestClassMethodPostProcessors()
        // class level post processors
        .enableRestClassPostProcessors()
        // service level post processors
        .enableRestServicePostProcessors()
        // command line options
        .options(rootDoc)
        // service exporters
        .enableRestServicesExports().build();
    return apiDoclet.startParseSourceCodes(rootDoc);
  }
}
