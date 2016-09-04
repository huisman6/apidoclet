package com.dooioo.se.apidoclet.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import com.dooioo.se.apidoclet.core.ApiDocletBootStrap;

/**
 * API文档的插件，仅支持javadoc 1.5以上
 */
@Mojo(name = "deploy")
public class ApiDocletMojo extends AbstractMojo {
  private static final String OPTIONS_FILE_NAME = "options";
  private static final String PACKAGES_FILE_NAME = "packages";

  /**
   * javadoc 可执行文件的路径
   */
  @Parameter(property = "javadocExecutable")
  private String javadocExecutable;

  /**
   * Maven编译后 class文件的输出目录
   */
  @Parameter(property = "classdir", defaultValue = "${project.build.outputDirectory}")
  private String classdir;

  /** */
  @Component
  private ToolchainManager toolchainManager;

  /**
   * Specifies the maximum Java heap size to be used when launching the Javadoc tool. JVMs refer to
   * this property as the <code>-Xmx</code> parameter. Example: '512' or '512m'. The memory unit
   * depends on the JVM used. The units supported could be: <code>k</code>, <code>kb</code>,
   * <code>m</code>, <code>mb</code>, <code>g</code>, <code>gb</code>, <code>t</code>,
   * <code>tb</code>. If no unit specified, the default unit is <code>m</code>.
   */
  @Parameter(property = "maxmemory")
  private String maxmemory;

  /**
   * Specifies the minimum Java heap size to be used when launching the Javadoc tool. JVMs refer to
   * this property as the <code>-Xms</code> parameter. Example: '512' or '512m'. The memory unit
   * depends on the JVM used. The units supported could be: <code>k</code>, <code>kb</code>,
   * <code>m</code>, <code>mb</code>, <code>g</code>, <code>gb</code>, <code>t</code>,
   * <code>tb</code>. If no unit specified, the default unit is <code>m</code>.
   */
  @Parameter(property = "minmemory")
  private String minmemory;

  /**
   * The current build session instance. This is used for toolchain manager API calls.
   */
  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
  private String encoding;

  /**
   * Specifies the locale that javadoc uses when generating documentation. <br/>
   * See <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#locale">
   * locale</a>.
   */
  @Parameter(property = "locale")
  private String locale;


  /**
   * doclet 默认输出目录
   */
  @Parameter(property = "docletOutputDir",
      defaultValue = "${project.build.directory}/se-apidoclet", required = true)
  private String docletOutputDir;


  /**
   * 源文件的路径（绝对路径、或相对当前路径）
   */
  @Parameter(property = "sourcepath")
  private String sourcepath;

  /**
   * 源文件路径下，那些包生成javadoc文档
   */
  @Parameter(property = "subpackages")
  private String subpackages;
  /**
   * The Maven Project Object
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  /**
   * The Maven Settings.
   */
  @Parameter(defaultValue = "${settings}", readonly = true, required = true)
  private Settings settings;

  /**
   * 插件的执行环境
   */
  @Component
  private MojoExecution execution;



  /**
   * 默认doclet
   */
  private String doclet = ApiDocletBootStrap.class.getName();

  /**
   * Set an additional parameter(s) on the command line. This value should include quotes as
   * necessary for parameters that include spaces. Useful for a custom doclet.
   */
  @Parameter(property = "options")
  private String options;

  /**
   * 参考示例： javadoc -doclet com.dooioo.se.lorik.apidoclet.ApiDoclet \ -docletpath
   * ./target/classes:/M2_HOME/com/dooioo/se/lorik/lorik-apidoclet-contract/1.0.0-SNAPSHOT/lorik-
   * apidoclet-contract-1.0.0-SNAPSHOT.jar \ -sourcepath ./src/main/java -subpackages
   * com.dooioo.se.lorik.apidoclet\ -classdir ./target/classes -exportTo
   * http://localhost:8080/v1/restapps/import/binary -print -ignoreVirtualPath
   * 
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // 创建javadoc 命令行参数
    // ----------------------------------------------------------------------
    // Create command line for Javadoc
    // ----------------------------------------------------------------------
    String javadocExecutable;
    try {
      javadocExecutable = getJavadocExecutable();
    } catch (IOException e) {
      throw new MojoFailureException("find javadoc executable error", e);
    }

    // apidoclet临时文件所在目录
    File javadocOutputDirectory = new File(this.docletOutputDir);
    if (javadocOutputDirectory.exists() && !javadocOutputDirectory.isDirectory()) {
      throw new MojoExecutionException("IOException: docletOutputDir "
          + javadocOutputDirectory.getName() + " is not a directory.");
    }
    if (javadocOutputDirectory.exists() && !javadocOutputDirectory.canWrite()) {
      throw new MojoExecutionException("IOException: docletOutputDir"
          + javadocOutputDirectory.getName() + " is not writable.");
    }
    javadocOutputDirectory.mkdirs();

    Commandline cmd = new Commandline();
    cmd.getShell().setQuotedArgumentsEnabled(false); // for Javadoc JVM args
    // 工作目录里会生成@Options , @Packages 文件
    cmd.setWorkingDirectory(javadocOutputDirectory.getAbsolutePath());
    cmd.setExecutable(javadocExecutable);

    // 内存大小
    addMemoryArg(cmd, "-Xmx", this.maxmemory);
    addMemoryArg(cmd, "-Xms", this.minmemory);

    List<String> arguments = this.resovleArguments();
    if (arguments.size() > 0) {
      // 追加命令行参数
      addCommandLineOptions(cmd, arguments, javadocOutputDirectory);
    }

    List<String> sourcePaths = getSourcePaths();
    List<String> files = getFiles(sourcePaths);
    List<String> packageNames = getPackageNames(sourcePaths, files);
    if (!packageNames.isEmpty()) {
      addCommandLinePackages(cmd, javadocOutputDirectory, packageNames);
    }

    // 终于。。执行javadoc 命令
    executeJavadocCommandLine(cmd, javadocOutputDirectory);
  }


  /**
   * never null
   */
  private List<String> resovleArguments() {
    List<String> arguments = new ArrayList<String>();
    // 编码
    addArgIfNotEmpty(arguments, "-encoding", JavadocUtil.quotedArgument(getEncoding()));

    // see com.sun.tools.javadoc.Start#parseAndExecute(String argv[])
    addArgIfNotEmpty(arguments, "-locale", JavadocUtil.quotedArgument(this.locale));

    // 如果配置了此参数，则构造 -sourcepath -subpackage -doclet
    List<String> sourcePaths = getSourcePaths();

    if ((StringUtils.isEmpty(this.sourcepath)) && (StringUtils.isNotEmpty(this.subpackages))) {
      this.sourcepath = StringUtils.join(sourcePaths.iterator(), File.pathSeparator);
    }
    addArgIfNotEmpty(arguments, "-sourcepath",
        JavadocUtil.quotedPathArgument(getSourcePath(sourcePaths)));

    if (StringUtils.isNotEmpty(this.sourcepath)) {
      addArgIfNotEmpty(arguments, "-subpackages", this.subpackages);
    }

    addArgIfNotEmpty(arguments, "-doclet", this.doclet);
    String docletpath = getDocletPath();
    addArgIfNotEmpty(arguments, "-docletpath", docletpath);
    // class path 默认为docletpath，主要是SpringMVC、Spring Cloud类，在 lorik-apidoclet-1.0.1.jar里
    // 当前项目里的依赖暂不添加进来
    addArgIfNotEmpty(arguments, "-classpath", docletpath);

    // 添加 classdir
    addArgIfNotEmpty(arguments, "-classdir", this.classdir);
    // 添加其他options
    addArgIfNotEmpty(arguments, "-projectRootDir", this.session.getExecutionRootDirectory());
    addArgIfNotEmpty(arguments, "-artifactGroupId", this.project.getGroupId());
    addArgIfNotEmpty(arguments, "-artifactId", this.project.getArtifactId());
    addArgIfNotEmpty(arguments, "-artifactVersion", this.project.getVersion());


    addArgIfNotEmpty(arguments, null, "-protected");
    // 可选参数
    addArgIfNotEmpty(arguments, null, this.options);
    return arguments;
  }

  private String getDocletPath() {
    // 插件自己的依赖
    List<Artifact> denpendencyArtifacts =
        execution.getMojoDescriptor().getPluginDescriptor().getArtifacts();
    List<String> pathParts = new ArrayList<String>();
    for (Artifact artifact : denpendencyArtifacts) {
      pathParts.add(artifact.getFile().getAbsolutePath());
    }

    String path = StringUtils.join(pathParts.iterator(), File.pathSeparator);

    if (StringUtils.isEmpty(path) &&

    getLog().isWarnEnabled()) {
      getLog().warn(
          "No docletpath option was found. Please review <docletpath/> or <docletArtifact/>"
              + " or <doclets/>.");
    }

    return path;
  }

  /**
   * Execute the Javadoc command line
   * 
   * @param cmd not null
   * @param javadocOutputDirectory not null
   */
  private void executeJavadocCommandLine(Commandline cmd, File javadocOutputDirectory)
      throws MojoExecutionException {
    if (getLog().isDebugEnabled()) {
      // no quoted arguments
      getLog().debug(CommandLineUtils.toString(cmd.getCommandline()).replaceAll("'", ""));
    }
    CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
    CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
    try {
      int exitCode = CommandLineUtils.executeCommandLine(cmd, out, err);

      String output = (StringUtils.isEmpty(out.getOutput()) ? null : '\n' + out.getOutput().trim());

      if (exitCode != 0) {
        if (StringUtils.isNotEmpty(output)) {
          getLog().info(output);
        }
      }

      if (StringUtils.isNotEmpty(output)) {
        getLog().info(output);
      }
    } catch (CommandLineException e) {
      throw new MojoExecutionException("Unable to execute javadoc command: " + e.getMessage(), e);
    }

    // ----------------------------------------------------------------------
    // Handle Javadoc warnings
    // ----------------------------------------------------------------------

    if (StringUtils.isNotEmpty(err.getOutput()) && getLog().isWarnEnabled()) {
      getLog().warn("Javadoc Warnings");

      StringTokenizer token = new StringTokenizer(err.getOutput(), "\n");
      while (token.hasMoreTokens()) {
        String current = token.nextToken().trim();

        getLog().warn(current);
      }
    }
  }

  /**
   * 获取javadoc 的可执行文件的路径
   */
  private String getJavadocExecutable() throws IOException {
    Toolchain tc = getToolchain();

    if (tc != null) {
      getLog().info("Toolchain in maven-javadoc-plugin: " + tc);
      if (javadocExecutable != null) {
        getLog().warn(
            "Toolchains are ignored, 'javadocExecutable' parameter is set to " + javadocExecutable);
      } else {
        javadocExecutable = tc.findTool("javadoc");
      }
    }

    String javadocCommand = "javadoc" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");

    File javadocExe;

    // ----------------------------------------------------------------------
    // The javadoc executable is defined by the user
    // ----------------------------------------------------------------------
    if (StringUtils.isNotEmpty(javadocExecutable)) {
      javadocExe = new File(javadocExecutable);

      if (javadocExe.isDirectory()) {
        javadocExe = new File(javadocExe, javadocCommand);
      }

      if (SystemUtils.IS_OS_WINDOWS && javadocExe.getName().indexOf('.') < 0) {
        javadocExe = new File(javadocExe.getPath() + ".exe");
      }

      if (!javadocExe.isFile()) {
        throw new IOException("The javadoc executable '" + javadocExe
            + "' doesn't exist or is not a file. Verify the <javadocExecutable/> parameter.");
      }

      return javadocExe.getAbsolutePath();
    }

    // ----------------------------------------------------------------------
    // Try to find javadocExe from System.getProperty( "java.home" )
    // By default, System.getProperty( "java.home" ) = JRE_HOME and JRE_HOME
    // should be in the JDK_HOME
    // ----------------------------------------------------------------------
    // For IBM's JDK 1.2
    if (SystemUtils.IS_OS_AIX) {
      javadocExe =
          new File(SystemUtils.getJavaHome() + File.separator + ".." + File.separator + "sh",
              javadocCommand);
    }
    // For Apple's JDK 1.6.x (and older?) on Mac OSX
    // CHECKSTYLE_OFF: MagicNumber
    else if (SystemUtils.IS_OS_MAC_OSX && SystemUtils.JAVA_VERSION_FLOAT < 1.7f)
    // CHECKSTYLE_ON: MagicNumber
    {
      javadocExe = new File(SystemUtils.getJavaHome() + File.separator + "bin", javadocCommand);
    } else {
      javadocExe =
          new File(SystemUtils.getJavaHome() + File.separator + ".." + File.separator + "bin",
              javadocCommand);
    }

    // ----------------------------------------------------------------------
    // Try to find javadocExe from JAVA_HOME environment variable
    // ----------------------------------------------------------------------
    if (!javadocExe.exists() || !javadocExe.isFile()) {
      Properties env = CommandLineUtils.getSystemEnvVars();
      String javaHome = env.getProperty("JAVA_HOME");
      if (StringUtils.isEmpty(javaHome)) {
        throw new IOException("The environment variable JAVA_HOME is not correctly set.");
      }
      if ((!new File(javaHome).getCanonicalFile().exists())
          || (new File(javaHome).getCanonicalFile().isFile())) {
        throw new IOException("The environment variable JAVA_HOME=" + javaHome
            + " doesn't exist or is not a valid directory.");
      }

      javadocExe = new File(javaHome + File.separator + "bin", javadocCommand);
    }

    if (!javadocExe.getCanonicalFile().exists() || !javadocExe.getCanonicalFile().isFile()) {
      throw new IOException("The javadoc executable '" + javadocExe
          + "' doesn't exist or is not a file. Verify the JAVA_HOME environment variable.");
    }

    return javadocExe.getAbsolutePath();
  }

  private String getSourcePath(List<String> sourcePaths) {
    String sourcePath = null;

    if (StringUtils.isEmpty(subpackages) || StringUtils.isNotEmpty(sourcepath)) {
      sourcePath = StringUtils.join(sourcePaths.iterator(), File.pathSeparator);
    }

    return sourcePath;
  }

  /**
   * @param cmd not null
   * @param arguments not null
   * @param javadocOutputDirectory not null
   * @throws MavenReportException if any
   * @see <a href=
   *      "http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#argumentfiles">
   *      Reference Guide, Command line argument files</a>
   * @see #OPTIONS_FILE_NAME
   */
  private void addCommandLineOptions(Commandline cmd, List<String> arguments,
      File javadocOutputDirectory) throws MojoExecutionException {
    File optionsFile = new File(javadocOutputDirectory, OPTIONS_FILE_NAME);

    StringBuilder options = new StringBuilder();
    options.append(StringUtils.join(arguments.toArray(new String[arguments.size()]),
        SystemUtils.LINE_SEPARATOR));

    try {
      FileUtils.fileWrite(optionsFile.getAbsolutePath(), null /* platform encoding */,
          options.toString());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write '" + optionsFile.getName()
          + "' temporary file for command execution", e);
    }

    cmd.createArg().setValue("@" + OPTIONS_FILE_NAME);
  }

  private void addCommandLinePackages(Commandline cmd, File javadocOutputDirectory,
      List<String> packageNames) throws MojoExecutionException {
    File packagesFile = new File(javadocOutputDirectory, PACKAGES_FILE_NAME);

    try {
      FileUtils.fileWrite(packagesFile.getAbsolutePath(), null /* platform encoding */,
          StringUtils.join(packageNames.iterator(), SystemUtils.LINE_SEPARATOR));
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write '" + packagesFile.getName()
          + "' temporary file for command execution", e);
    }

    cmd.createArg().setValue("@" + PACKAGES_FILE_NAME);
  }

  /**
   * 获取源文件路径下的所有目录（即包名）
   */
  protected List<String> getFiles(List<String> sourcePaths) {
    List<String> files = new ArrayList<String>();
    if (StringUtils.isEmpty(subpackages)) {
      // 暂时不实现要排除的包名
      String[] excludedPackages = new String[] {};

      for (String sourcePath : sourcePaths) {
        File sourceDirectory = new File(sourcePath);
        // 忽略 源文件的过滤
        JavadocUtil.addFilesFromSource(files, sourceDirectory, null, null, excludedPackages);
      }
    }

    return files;
  }


  /**
   * 获取源文件路径
   */
  protected List<String> getSourcePaths() {
    List<String> sourcePaths;

    if (StringUtils.isEmpty(sourcepath)) {
      // 获取当前路径
      sourcePaths =
          new ArrayList<String>(JavadocUtil.pruneDirs(project, getProjectSourceRoots(project)));
      if (project.getExecutionProject() != null) {
        sourcePaths.addAll(JavadocUtil.pruneDirs(project, getExecutionProjectSourceRoots(project)));
      }

    } else {
      // 如果配置了，就用这个路径
      sourcePaths = new ArrayList<String>(Arrays.asList(JavadocUtil.splitPath(sourcepath)));
      sourcePaths = JavadocUtil.pruneDirs(project, sourcePaths);
    }
    sourcePaths = JavadocUtil.pruneDirs(project, sourcePaths);

    return sourcePaths;
  }

  /**
   * 获取项目根目录
   */
  protected List<String> getProjectSourceRoots(MavenProject p) {
    if ("pom".equals(p.getPackaging().toLowerCase())) {
      return Collections.emptyList();
    }

    return (p.getCompileSourceRoots() == null ? Collections.<String>emptyList()
        : new LinkedList<String>(p.getCompileSourceRoots()));
  }

  protected List<String> getExecutionProjectSourceRoots(MavenProject p) {
    if ("pom".equals(p.getExecutionProject().getPackaging().toLowerCase())) {
      return Collections.emptyList();
    }

    return (p.getExecutionProject().getCompileSourceRoots() == null ? Collections
        .<String>emptyList() : new LinkedList<String>(p.getExecutionProject()
        .getCompileSourceRoots()));
  }

  /**
   * 添加内存参数
   */
  private void addMemoryArg(Commandline cmd, String arg, String memory) {
    if (StringUtils.isNotEmpty(memory)) {
      try {
        cmd.createArg().setValue("-J" + arg + JavadocUtil.parseJavadocMemory(memory));
      } catch (IllegalArgumentException e) {
        if (getLog().isErrorEnabled()) {
          getLog()
              .error("Malformed memory pattern for '" + arg + memory + "'. Ignore this option.");
        }
      }
    }
  }

  /**
   * 项目编码
   */
  private String getEncoding() {
    return (StringUtils.isEmpty(encoding)) ? ReaderFactory.FILE_ENCODING : encoding;
  }

  private Toolchain getToolchain() {
    Toolchain tc = null;
    if (toolchainManager != null) {
      tc = toolchainManager.getToolchainFromBuildContext("jdk", session);
    }

    return tc;
  }

  /**
   * @param sourcePaths could be null
   * @param files not null
   * @return the list of package names for files in the sourcePaths
   */
  private List<String> getPackageNames(List<String> sourcePaths, List<String> files) {
    return getPackageNamesOrFilesWithUnnamedPackages(sourcePaths, files, true);
  }

  /**
   * @param sourcePaths could be null
   * @param files not null
   * @return a list files with unnamed package names for files in the sourecPaths
   */
  @SuppressWarnings("unused")
  private List<String> getFilesWithUnnamedPackages(List<String> sourcePaths, List<String> files) {
    return getPackageNamesOrFilesWithUnnamedPackages(sourcePaths, files, false);
  }

  /**
   * @param sourcePaths not null, containing absolute and relative paths
   * @param files not null, containing list of quoted files
   * @param onlyPackageName boolean for only package name
   * @return a list of package names or files with unnamed package names, depending the value of the
   *         unnamed flag
   * @see #getFiles(List)
   * @see #getSourcePaths()
   */
  private List<String> getPackageNamesOrFilesWithUnnamedPackages(List<String> sourcePaths,
      List<String> files, boolean onlyPackageName) {
    List<String> returnList = new ArrayList<String>();

    if (!StringUtils.isEmpty(sourcepath)) {
      return returnList;
    }

    for (String currentFile : files) {
      currentFile = currentFile.replace('\\', '/');

      for (String currentSourcePath : sourcePaths) {
        currentSourcePath = currentSourcePath.replace('\\', '/');

        if (!currentSourcePath.endsWith("/")) {
          currentSourcePath += "/";
        }

        if (currentFile.contains(currentSourcePath)) {
          String packagename = currentFile.substring(currentSourcePath.length() + 1);

          /*
           * Remove the miscellaneous files
           * http://docs.oracle.com/javase/1.4.2/docs/tooldocs/solaris/javadoc.html#unprocessed
           */
          if (packagename.contains("doc-files")) {
            continue;
          }

          if (onlyPackageName && packagename.lastIndexOf("/") != -1) {
            packagename = packagename.substring(0, packagename.lastIndexOf("/"));
            packagename = packagename.replace('/', '.');

            if (!returnList.contains(packagename)) {
              returnList.add(packagename);
            }
          }
          if (!onlyPackageName && packagename.lastIndexOf("/") == -1) {
            returnList.add(currentFile);
          }
        }
      }
    }

    return returnList;
  }

  /**
   * Convenience method to add an argument to the <code>command line</code> if the the value is not
   * null or empty.
   * <p/>
   * Moreover, the value could be comma separated.
   *
   * @param arguments a list of arguments, not null
   * @param key the argument name.
   * @param value the argument value to be added.
   * @see #addArgIfNotEmpty(java.util.List, String, String, boolean)
   */
  private void addArgIfNotEmpty(List<String> arguments, String key, String value) {
    addArgIfNotEmpty(arguments, key, value, false);
  }

  /**
   * Convenience method to add an argument to the <code>command line</code> if the the value is not
   * null or empty.
   * <p/>
   * Moreover, the value could be comma separated.
   *
   * @param arguments a list of arguments, not null
   * @param key the argument name.
   * @param value the argument value to be added.
   * @param repeatKey repeat or not the key in the command line
   * @param splitValue if <code>true</code> given value will be tokenized by comma
   */
  private void addArgIfNotEmpty(List<String> arguments, String key, String value,
      boolean repeatKey, boolean splitValue) {
    if (StringUtils.isNotEmpty(value)) {
      if (StringUtils.isNotEmpty(key)) {
        arguments.add(key);
      }

      if (splitValue) {
        StringTokenizer token = new StringTokenizer(value, ",");
        while (token.hasMoreTokens()) {
          String current = token.nextToken().trim();

          if (StringUtils.isNotEmpty(current)) {
            arguments.add(current);

            if (token.hasMoreTokens() && repeatKey) {
              arguments.add(key);
            }
          }
        }
      } else {
        arguments.add(value);
      }
    }
  }

  /**
   * Convenience method to add an argument to the <code>command line</code> if the the value is not
   * null or empty.
   * <p/>
   * Moreover, the value could be comma separated.
   *
   * @param arguments a list of arguments, not null
   * @param key the argument name.
   * @param value the argument value to be added.
   * @param repeatKey repeat or not the key in the command line
   */
  private void addArgIfNotEmpty(List<String> arguments, String key, String value, boolean repeatKey) {
    addArgIfNotEmpty(arguments, key, value, repeatKey, true);
  }


}
