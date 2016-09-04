package com.dooioo.se.apidoclet.maven.plugin;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Set of utilities methods for Javadoc.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: JavadocUtil.java 1672205 2015-04-08 22:10:02Z khmarbaise $
 * @since 2.4
 */
public class JavadocUtil
{
    /** The default timeout used when fetching url, i.e. 2000. */
    public static final int DEFAULT_TIMEOUT = 2000;

    /** Error message when VM could not be started using invoker. */
    protected static final String ERROR_INIT_VM =
        "Error occurred during initialization of VM, try to reduce the Java heap size for the MAVEN_OPTS "
        + "environnement variable using -Xms:<size> and -Xmx:<size>.";

    /**
     * Method that removes the invalid directories in the specified directories.
     * <b>Note</b>: All elements in <code>dirs</code> could be an absolute or relative against the project's base
     * directory <code>String</code> path.
     *
     * @param project the current Maven project not null
     * @param dirs the list of <code>String</code> directories path that will be validated.
     * @return a List of valid <code>String</code> directories absolute paths.
     */
    public static List<String> pruneDirs( MavenProject project, List<String> dirs )
    {
        List<String> pruned = new ArrayList<String>( dirs.size() );
        for ( String dir : dirs )
        {
            if ( dir == null )
            {
                continue;
            }

            File directory = new File( dir );
            if ( !directory.isAbsolute() )
            {
                directory = new File( project.getBasedir(), directory.getPath() );
            }

            if ( directory.isDirectory() && !pruned.contains( directory.getAbsolutePath() ) )
            {
                pruned.add( directory.getAbsolutePath() );
            }
        }

        return pruned;
    }

    /**
     * Method that removes the invalid files in the specified files.
     * <b>Note</b>: All elements in <code>files</code> should be an absolute <code>String</code> path.
     *
     * @param files the list of <code>String</code> files paths that will be validated.
     * @return a List of valid <code>File</code> objects.
     */
    protected static List<String> pruneFiles( List<String> files )
    {
        List<String> pruned = new ArrayList<String>( files.size() );
        for ( String f : files )
        {
            if ( !shouldPruneFile( f, pruned ) )
            {
                pruned.add( f );
            }
        }
 
        return pruned;
    }

    /**
     * Determine whether a file should be excluded from the provided list of paths, based on whether
     * it exists and is already present in the list.
     * @param f The files.
     * @param pruned The list of pruned files..
     * @return true if the file could be pruned false otherwise.
     */
    public static boolean shouldPruneFile( String f, List<String> pruned )
    {
        if ( f != null )
        {
            File file = new File( f );
            if ( file.isFile() && ( isEmpty( pruned ) || !pruned.contains( f ) ) )
            {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Copy from {@link org.apache.maven.project.MavenProject#getCompileArtifacts()}
     * @param artifacts not null
     * @return list of compile artifacts with compile scope
     * @deprecated since 2.5, using {@link #getCompileArtifacts(Set, boolean)} instead of.
     */
    protected static List<Artifact> getCompileArtifacts( Set<Artifact> artifacts )
    {
        return getCompileArtifacts( artifacts, false );
    }

    /**
     * Copy from {@link org.apache.maven.project.MavenProject#getCompileArtifacts()}
     * @param artifacts not null
     * @param withTestScope flag to include or not the artifacts with test scope
     * @return list of compile artifacts with or without test scope.
     */
    protected static List<Artifact> getCompileArtifacts( Set<Artifact> artifacts, boolean withTestScope )
    {
        List<Artifact> list = new ArrayList<Artifact>( artifacts.size() );

        for ( Artifact a : artifacts )
        {
            // TODO: classpath check doesn't belong here - that's the other method
            if ( a.getArtifactHandler().isAddedToClasspath() )
            {
                // TODO: let the scope handler deal with this
                if ( withTestScope )
                {
                    if ( Artifact.SCOPE_COMPILE.equals( a.getScope() )
                        || Artifact.SCOPE_PROVIDED.equals( a.getScope() )
                        || Artifact.SCOPE_SYSTEM.equals( a.getScope() )
                        || Artifact.SCOPE_TEST.equals( a.getScope() ) )
                    {
                        list.add( a );
                    }
                }
                else
                {
                    if ( Artifact.SCOPE_COMPILE.equals( a.getScope() ) || Artifact.SCOPE_PROVIDED.equals( a.getScope() )
                        || Artifact.SCOPE_SYSTEM.equals( a.getScope() ) )
                    {
                        list.add( a );
                    }
                }
            }
        }

        return list;
    }

    /**
     * Convenience method to wrap an argument value in single quotes (i.e. <code>'</code>). Intended for values
     * which may contain whitespaces.
     * <br/>
     * To prevent javadoc error, the line separator (i.e. <code>\n</code>) are skipped.
     *
     * @param value the argument value.
     * @return argument with quote
     */
    protected static String quotedArgument( String value )
    {
        String arg = value;

        if ( StringUtils.isNotEmpty( arg ) )
        {
            if ( arg.contains( "'" ) )
            {
                arg = StringUtils.replace( arg, "'", "\\'" );
            }
            arg = "'" + arg + "'";

            // To prevent javadoc error
            arg = StringUtils.replace( arg, "\n", " " );
        }

        return arg;
    }

    /**
     * Convenience method to format a path argument so that it is properly interpreted by the javadoc tool. Intended
     * for path values which may contain whitespaces.
     *
     * @param value the argument value.
     * @return path argument with quote
     */
    protected static String quotedPathArgument( String value )
    {
        String path = value;

        if ( StringUtils.isNotEmpty( path ) )
        {
            path = path.replace( '\\', '/' );
            if ( path.contains( "\'" ) )
            {
                String split[] = path.split( "\'" );
                path = "";

                for ( int i = 0; i < split.length; i++ )
                {
                    if ( i != split.length - 1 )
                    {
                        path = path + split[i] + "\\'";
                    }
                    else
                    {
                        path = path + split[i];
                    }
                }
            }
            path = "'" + path + "'";
        }

        return path;
    }

    /**
     * Method that gets the files or classes that would be included in the javadocs using the subpackages
     * parameter.
     *
     * @param sourceDirectory the directory where the source files are located
     * @param fileList        the list of all files found in the sourceDirectory
     * @param excludePackages package names to be excluded in the javadoc
     * @return a StringBuilder that contains the appended file names of the files to be included in the javadoc
     */
    protected static List<String> getIncludedFiles( File sourceDirectory, String[] fileList, String[] excludePackages )
    {
        List<String> files = new ArrayList<String>();

        for ( String aFileList : fileList )
        {
            boolean include = true;
            for ( int k = 0; k < excludePackages.length && include; k++ )
            {
                // handle wildcards (*) in the excludePackageNames
                String[] excludeName = excludePackages[k].split( "[*]" );

                if ( excludeName.length == 0 )
                {
                    continue;
                }

                if ( excludeName.length > 1 )
                {
                    int u = 0;
                    while ( include && u < excludeName.length )
                    {
                        if ( !"".equals( excludeName[u].trim() ) && aFileList.contains( excludeName[u] ) )
                        {
                            include = false;
                        }
                        u++;
                    }
                }
                else
                {
                    if ( aFileList.startsWith( sourceDirectory.toString() + File.separatorChar + excludeName[0] ) )
                    {
                        if ( excludeName[0].endsWith( String.valueOf( File.separatorChar ) ) )
                        {
                            int i = aFileList.lastIndexOf( File.separatorChar );
                            String packageName = aFileList.substring( 0, i + 1 );
                            File currentPackage = new File( packageName );
                            File excludedPackage = new File( sourceDirectory, excludeName[0] );
                            if ( currentPackage.equals( excludedPackage )
                                && aFileList.substring( i ).contains( ".java" ) )
                            {
                                include = true;
                            }
                            else
                            {
                                include = false;
                            }
                        }
                        else
                        {
                            include = false;
                        }
                    }
                }
            }

            if ( include )
            {
                files.add( quotedPathArgument( aFileList ) );
            }
        }

        return files;
    }

    /**
     * Method that gets the complete package names (including subpackages) of the packages that were defined
     * in the excludePackageNames parameter.
     *
     * @param sourceDirectory     the directory where the source files are located
     * @param excludePackagenames package names to be excluded in the javadoc
     * @return a List of the packagenames to be excluded
     */
    protected static List<String> getExcludedPackages( String sourceDirectory, String[] excludePackagenames )
    {
        List<String> files = new ArrayList<String>();
        for ( String excludePackagename : excludePackagenames )
        {
            String[] fileList = FileUtils.getFilesFromExtension( sourceDirectory, new String[] { "java" } );
            for ( String aFileList : fileList )
            {
                String[] excludeName = excludePackagename.split( "[*]" );
                int u = 0;
                while ( u < excludeName.length )
                {
                    if ( !"".equals( excludeName[u].trim() ) && aFileList.contains( excludeName[u] )
                        && !sourceDirectory.contains( excludeName[u] ) )
                    {
                        files.add( aFileList );
                    }
                    u++;
                }
            }
        }

        List<String> excluded = new ArrayList<String>();
        for ( String file : files )
        {
            int idx = file.lastIndexOf( File.separatorChar );
            String tmpStr = file.substring( 0, idx );
            tmpStr = tmpStr.replace( '\\', '/' );
            String[] srcSplit = tmpStr.split( Pattern.quote( sourceDirectory.replace( '\\', '/' ) + '/' ) );
            String excludedPackage = srcSplit[1].replace( '/', '.' );

            if ( !excluded.contains( excludedPackage ) )
            {
                excluded.add( excludedPackage );
            }
        }

        return excluded;
    }

    /**
     * Convenience method that gets the files to be included in the javadoc.
     *
     * @param sourceDirectory the directory where the source files are located
     * @param files the variable that contains the appended filenames of the files to be included in the javadoc
     * @param excludePackages the packages to be excluded in the javadocs
     * @param sourceFileIncludes files to include.
     * @param sourceFileExcludes files to exclude.
     */
    protected static void addFilesFromSource( List<String> files, File sourceDirectory,
                                              List<String> sourceFileIncludes,
                                              List<String> sourceFileExcludes,
                                              String[] excludePackages )
    {
        DirectoryScanner ds = new DirectoryScanner();
        if ( sourceFileIncludes == null )
        {
            sourceFileIncludes = Collections.singletonList( "**/*.java" );
        }
        ds.setIncludes( sourceFileIncludes.toArray( new String[sourceFileIncludes.size()] ) );
        if ( sourceFileExcludes != null && sourceFileExcludes.size() > 0 )
        {
            ds.setExcludes( sourceFileExcludes.toArray( new String[sourceFileExcludes.size()] ) );
        }
        ds.setBasedir( sourceDirectory );
        ds.scan();

        String[] fileList = ds.getIncludedFiles();
        String[] pathList = new String[fileList.length];
        for ( int x = 0; x < fileList.length; x++ )
        {
            pathList[x] = new File( sourceDirectory, fileList[x] ).getAbsolutePath();
        }


        if (  pathList.length != 0 )
        {
            List<String> tmpFiles = getIncludedFiles( sourceDirectory, pathList, excludePackages );
            files.addAll( tmpFiles );
        }
    }

    /**
     * Call the Javadoc tool and parse its output to find its version, i.e.:
     * <pre>
     * javadoc.exe(or .sh) -J-version
     * </pre>
     *
     * @param javadocExe not null file
     * @return the javadoc version as float
     * @throws IOException if javadocExe is null, doesn't exist or is not a file
     * @throws CommandLineException if any
     * @throws IllegalArgumentException if no output was found in the command line
     * @throws PatternSyntaxException if the output contains a syntax error in the regular-expression pattern.
     * @see #parseJavadocVersion(String)
     */
    protected static float getJavadocVersion( File javadocExe )
        throws IOException, CommandLineException, IllegalArgumentException
    {
        if ( ( javadocExe == null ) || ( !javadocExe.exists() ) || ( !javadocExe.isFile() ) )
        {
            throw new IOException( "The javadoc executable '" + javadocExe + "' doesn't exist or is not a file. " );
        }

        Commandline cmd = new Commandline();
        cmd.setExecutable( javadocExe.getAbsolutePath() );
        cmd.setWorkingDirectory( javadocExe.getParentFile() );
        cmd.createArg().setValue( "-J-version" );

        CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

        int exitCode = CommandLineUtils.executeCommandLine( cmd, out, err );

        if ( exitCode != 0 )
        {
            StringBuilder msg = new StringBuilder( "Exit code: " + exitCode + " - " + err.getOutput() );
            msg.append( '\n' );
            msg.append( "Command line was:" + CommandLineUtils.toString( cmd.getCommandline() ) );
            throw new CommandLineException( msg.toString() );
        }

        if ( StringUtils.isNotEmpty( err.getOutput() ) )
        {
            return parseJavadocVersion( err.getOutput() );
        }
        else if ( StringUtils.isNotEmpty( out.getOutput() ) )
        {
            return parseJavadocVersion( out.getOutput() );
        }

        throw new IllegalArgumentException( "No output found from the command line 'javadoc -J-version'" );
    }

    /**
     * Parse the output for 'javadoc -J-version' and return the javadoc version recognized.
     * <br/>
     * Here are some output for 'javadoc -J-version' depending the JDK used:
     * <table>
     * <tr>
     *   <th>JDK</th>
     *   <th>Output for 'javadoc -J-version'</th>
     * </tr>
     * <tr>
     *   <td>Sun 1.4</td>
     *   <td>java full version "1.4.2_12-b03"</td>
     * </tr>
     * <tr>
     *   <td>Sun 1.5</td>
     *   <td>java full version "1.5.0_07-164"</td>
     * </tr>
     * <tr>
     *   <td>IBM 1.4</td>
     *   <td>javadoc full version "J2RE 1.4.2 IBM Windows 32 build cn1420-20040626"</td>
     * </tr>
     * <tr>
     *   <td>IBM 1.5 (French JVM)</td>
     *   <td>javadoc version complète de "J2RE 1.5.0 IBM Windows 32 build pwi32pdev-20070426a"</td>
     * </tr>
     * <tr>
     *   <td>FreeBSD 1.5</td>
     *   <td>java full version "diablo-1.5.0-b01"</td>
     * </tr>
     * <tr>
     *   <td>BEA jrockit 1.5</td>
     *   <td>java full version "1.5.0_11-b03"</td>
     * </tr>
     * </table>
     *
     * @param output for 'javadoc -J-version'
     * @return the version of the javadoc for the output.
     * @throws PatternSyntaxException if the output doesn't match with the output pattern
     * <tt>(?s).*?([0-9]+\\.[0-9]+)(\\.([0-9]+))?.*</tt>.
     * @throws IllegalArgumentException if the output is null
     */
    protected static float parseJavadocVersion( String output )
        throws IllegalArgumentException
    {
        if ( StringUtils.isEmpty( output ) )
        {
            throw new IllegalArgumentException( "The output could not be null." );
        }

        Pattern pattern = Pattern.compile( "(?s).*?([0-9]+\\.[0-9]+)(\\.([0-9]+))?.*" );

        Matcher matcher = pattern.matcher( output );
        if ( !matcher.matches() )
        {
            throw new PatternSyntaxException( "Unrecognized version of Javadoc: '" + output + "'", pattern.pattern(),
                                              pattern.toString().length() - 1 );
        }

        String version = matcher.group( 3 );
        if ( version == null )
        {
            version = matcher.group( 1 );
        }
        else
        {
            version = matcher.group( 1 ) + version;
        }

        return Float.parseFloat( version );
    }

    /**
     * Parse a memory string which be used in the JVM arguments <code>-Xms</code> or <code>-Xmx</code>.
     * <br/>
     * Here are some supported memory string depending the JDK used:
     * <table>
     * <tr>
     *   <th>JDK</th>
     *   <th>Memory argument support for <code>-Xms</code> or <code>-Xmx</code></th>
     * </tr>
     * <tr>
     *   <td>SUN</td>
     *   <td>1024k | 128m | 1g | 1t</td>
     * </tr>
     * <tr>
     *   <td>IBM</td>
     *   <td>1024k | 1024b | 128m | 128mb | 1g | 1gb</td>
     * </tr>
     * <tr>
     *   <td>BEA</td>
     *   <td>1024k | 1024kb | 128m | 128mb | 1g | 1gb</td>
     * </tr>
     * </table>
     *
     * @param memory the memory to be parsed, not null.
     * @return the memory parsed with a supported unit. If no unit specified in the <code>memory</code> parameter,
     * the default unit is <code>m</code>. The units <code>g | gb</code> or <code>t | tb</code> will be converted
     * in <code>m</code>.
     * @throws IllegalArgumentException if the <code>memory</code> parameter is null or doesn't match any pattern.
     */
    protected static String parseJavadocMemory( String memory )
        throws IllegalArgumentException
    {
        if ( StringUtils.isEmpty( memory ) )
        {
            throw new IllegalArgumentException( "The memory could not be null." );
        }

        Pattern p = Pattern.compile( "^\\s*(\\d+)\\s*?\\s*$" );
        Matcher m = p.matcher( memory );
        if ( m.matches() )
        {
            return m.group( 1 ) + "m";
        }

        p = Pattern.compile( "^\\s*(\\d+)\\s*k(b)?\\s*$", Pattern.CASE_INSENSITIVE );
        m = p.matcher( memory );
        if ( m.matches() )
        {
            return m.group( 1 ) + "k";
        }

        p = Pattern.compile( "^\\s*(\\d+)\\s*m(b)?\\s*$", Pattern.CASE_INSENSITIVE );
        m = p.matcher( memory );
        if ( m.matches() )
        {
            return m.group( 1 ) + "m";
        }

        p = Pattern.compile( "^\\s*(\\d+)\\s*g(b)?\\s*$", Pattern.CASE_INSENSITIVE );
        m = p.matcher( memory );
        if ( m.matches() )
        {
            return ( Integer.parseInt( m.group( 1 ) ) * 1024 ) + "m";
        }

        p = Pattern.compile( "^\\s*(\\d+)\\s*t(b)?\\s*$", Pattern.CASE_INSENSITIVE );
        m = p.matcher( memory );
        if ( m.matches() )
        {
            return ( Integer.parseInt( m.group( 1 ) ) * 1024 * 1024 ) + "m";
        }

        throw new IllegalArgumentException( "Could convert not to a memory size: " + memory );
    }

    /**
     * Validate if a charset is supported on this platform.
     *
     * @param charsetName the charsetName to be check.
     * @return <code>true</code> if the given charset is supported by the JVM, <code>false</code> otherwise.
     */
    protected static boolean validateEncoding( String charsetName )
    {
        if ( StringUtils.isEmpty( charsetName ) )
        {
            return false;
        }

        OutputStream ost = new ByteArrayOutputStream();
        OutputStreamWriter osw = null;
        try
        {
            osw = new OutputStreamWriter( ost, charsetName );
        }
        catch ( UnsupportedEncodingException exc )
        {
            return false;
        }
        finally
        {
            IOUtil.close( osw );
        }

        return true;
    }

    /**
     * For security reasons, if an active proxy is defined and needs an authentication by
     * username/password, hide the proxy password in the command line.
     *
     * @param cmdLine a command line, not null
     * @param settings the user settings
     * @return the cmdline with '*' for the http.proxyPassword JVM property
     */
    protected static String hideProxyPassword( String cmdLine, Settings settings )
    {
        if ( cmdLine == null )
        {
            throw new IllegalArgumentException( "cmdLine could not be null" );
        }

        if ( settings == null )
        {
            return cmdLine;
        }

        Proxy activeProxy = settings.getActiveProxy();
        if ( activeProxy != null && StringUtils.isNotEmpty( activeProxy.getHost() )
            && StringUtils.isNotEmpty( activeProxy.getUsername() )
            && StringUtils.isNotEmpty( activeProxy.getPassword() ) )
        {
            String pass = "-J-Dhttp.proxyPassword=\"" + activeProxy.getPassword() + "\"";
            String hidepass =
                "-J-Dhttp.proxyPassword=\"" + StringUtils.repeat( "*", activeProxy.getPassword().length() ) + "\"";

            return StringUtils.replace( cmdLine, pass, hidepass );
        }

        return cmdLine;
    }

   

    /**
     * Copy the given url to the given file.
     *
     * @param url not null url
     * @param file not null file where the url will be created
     * @throws IOException if any
     * @since 2.6
     */
    protected static void copyResource( URL url, File file )
        throws IOException
    {
        if ( file == null )
        {
            throw new IOException( "The file can't be null." );
        }
        if ( url == null )
        {
            throw new IOException( "The url could not be null." );
        }

        InputStream is = url.openStream();
        if ( is == null )
        {
            throw new IOException( "The resource " + url + " doesn't exists." );
        }

        if ( !file.getParentFile().exists() )
        {
            file.getParentFile().mkdirs();
        }

        OutputStream os = null;
        try
        {
            os = new FileOutputStream( file );

            IOUtil.copy( is, os );
        }
        finally
        {
            IOUtil.close( is );

            IOUtil.close( os );
        }
    }

    /**
     * Read the given file and return the content or null if an IOException occurs.
     *
     * @param javaFile not null
     * @param encoding could be null
     * @return the content with unified line separator of the given javaFile using the given encoding.
     * @see FileUtils#fileRead(File, String)
     * @since 2.6.1
     */
    protected static String readFile( final File javaFile, final String encoding )
    {
        try
        {
            return FileUtils.fileRead( javaFile, encoding );
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    /**
     * Split the given path with colon and semi-colon, to support Solaris and Windows path.
     * Examples:
     * <pre>
     * splitPath( "/home:/tmp" )     = ["/home", "/tmp"]
     * splitPath( "/home;/tmp" )     = ["/home", "/tmp"]
     * splitPath( "C:/home:C:/tmp" ) = ["C:/home", "C:/tmp"]
     * splitPath( "C:/home;C:/tmp" ) = ["C:/home", "C:/tmp"]
     * </pre>
     *
     * @param path which can contain multiple paths separated with a colon (<code>:</code>) or a
     * semi-colon (<code>;</code>), platform independent. Could be null.
     * @return the path splitted by colon or semi-colon or <code>null</code> if path was <code>null</code>.
     * @since 2.6.1
     */
    protected static String[] splitPath( final String path )
    {
        if ( path == null )
        {
            return null;
        }

        List<String> subpaths = new ArrayList<String>();
        PathTokenizer pathTokenizer = new PathTokenizer( path );
        while ( pathTokenizer.hasMoreTokens() )
        {
            subpaths.add( pathTokenizer.nextToken() );
        }

        return subpaths.toArray( new String[subpaths.size()] );
    }

    /**
     * Unify the given path with the current System path separator, to be platform independent.
     * Examples:
     * <pre>
     * unifyPathSeparator( "/home:/tmp" ) = "/home:/tmp" (Solaris box)
     * unifyPathSeparator( "/home:/tmp" ) = "/home;/tmp" (Windows box)
     * </pre>
     *
     * @param path which can contain multiple paths by separating them with a colon (<code>:</code>) or a
     * semi-colon (<code>;</code>), platform independent. Could be null.
     * @return the same path but separated with the current System path separator or <code>null</code> if path was
     * <code>null</code>.
     * @since 2.6.1
     * @see #splitPath(String)
     * @see File#pathSeparator
     */
    protected static String unifyPathSeparator( final String path )
    {
        if ( path == null )
        {
            return null;
        }

        return StringUtils.join( splitPath( path ), File.pathSeparator );
    }



    /**
     * A Path tokenizer takes a path and returns the components that make up
     * that path.
     *
     * The path can use path separators of either ':' or ';' and file separators
     * of either '/' or '\'.
     *
     * @version revision 439418 taken on 2009-09-12 from Ant Project
     * (see http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PathTokenizer.java)
     */
    private static class PathTokenizer
    {
        /**
         * A tokenizer to break the string up based on the ':' or ';' separators.
         */
        private StringTokenizer tokenizer;

        /**
         * A String which stores any path components which have been read ahead
         * due to DOS filesystem compensation.
         */
        private String lookahead = null;

        /**
         * A boolean that determines if we are running on Novell NetWare, which
         * exhibits slightly different path name characteristics (multi-character
         * volume / drive names)
         */
        private boolean onNetWare = Os.isFamily( "netware" );

        /**
         * Flag to indicate whether or not we are running on a platform with a
         * DOS style filesystem
         */
        private boolean dosStyleFilesystem;

        /**
         * Constructs a path tokenizer for the specified path.
         *
         * @param path The path to tokenize. Must not be <code>null</code>.
         */
        public PathTokenizer( String path )
        {
            if ( onNetWare )
            {
                // For NetWare, use the boolean=true mode, so we can use delimiter
                // information to make a better decision later.
                tokenizer = new StringTokenizer( path, ":;", true );
            }
            else
            {
                // on Windows and Unix, we can ignore delimiters and still have
                // enough information to tokenize correctly.
                tokenizer = new StringTokenizer( path, ":;", false );
            }
            dosStyleFilesystem = File.pathSeparatorChar == ';';
        }

        /**
         * Tests if there are more path elements available from this tokenizer's
         * path. If this method returns <code>true</code>, then a subsequent call
         * to nextToken will successfully return a token.
         *
         * @return <code>true</code> if and only if there is at least one token
         * in the string after the current position; <code>false</code> otherwise.
         */
        public boolean hasMoreTokens()
        {
            return lookahead != null || tokenizer.hasMoreTokens();

        }

        /**
         * Returns the next path element from this tokenizer.
         *
         * @return the next path element from this tokenizer.
         *
         * @exception NoSuchElementException if there are no more elements in this
         *            tokenizer's path.
         */
        public String nextToken()
            throws NoSuchElementException
        {
            String token;
            if ( lookahead != null )
            {
                token = lookahead;
                lookahead = null;
            }
            else
            {
                token = tokenizer.nextToken().trim();
            }

            if ( !onNetWare )
            {
                if ( token.length() == 1 && Character.isLetter( token.charAt( 0 ) ) && dosStyleFilesystem
                    && tokenizer.hasMoreTokens() )
                {
                    // we are on a dos style system so this path could be a drive
                    // spec. We look at the next token
                    String nextToken = tokenizer.nextToken().trim();
                    if ( nextToken.startsWith( "\\" ) || nextToken.startsWith( "/" ) )
                    {
                        // we know we are on a DOS style platform and the next path
                        // starts with a slash or backslash, so we know this is a
                        // drive spec
                        token += ":" + nextToken;
                    }
                    else
                    {
                        // store the token just read for next time
                        lookahead = nextToken;
                    }
                }
            }
            else
            {
                // we are on NetWare, tokenizing is handled a little differently,
                // due to the fact that NetWare has multiple-character volume names.
                if ( token.equals( File.pathSeparator ) || token.equals( ":" ) )
                {
                    // ignore ";" and get the next token
                    token = tokenizer.nextToken().trim();
                }

                if ( tokenizer.hasMoreTokens() )
                {
                    // this path could be a drive spec, so look at the next token
                    String nextToken = tokenizer.nextToken().trim();

                    // make sure we aren't going to get the path separator next
                    if ( !nextToken.equals( File.pathSeparator ) )
                    {
                        if ( nextToken.equals( ":" ) )
                        {
                            if ( !token.startsWith( "/" ) && !token.startsWith( "\\" ) && !token.startsWith( "." )
                                && !token.startsWith( ".." ) )
                            {
                                // it indeed is a drive spec, get the next bit
                                String oneMore = tokenizer.nextToken().trim();
                                if ( !oneMore.equals( File.pathSeparator ) )
                                {
                                    token += ":" + oneMore;
                                }
                                else
                                {
                                    token += ":";
                                    lookahead = oneMore;
                                }
                            }
                            // implicit else: ignore the ':' since we have either a
                            // UNIX or a relative path
                        }
                        else
                        {
                            // store the token just read for next time
                            lookahead = nextToken;
                        }
                    }
                }
            }
            return token;
        }
    }
    
    static List<String> toList( String src )
    {
        return toList( src, null, null );
    }
    
    static List<String> toList( String src, String elementPrefix, String elementSuffix )
    {
        if ( StringUtils.isEmpty( src ) )
        {
            return null;
        }
        
        List<String> result = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer( src, "[,:;]" );
        StringBuilder sb = new StringBuilder( 256 );
        while ( st.hasMoreTokens() )
        {
            sb.setLength( 0 );
            if ( StringUtils.isNotEmpty( elementPrefix ) )
            {
                sb.append( elementPrefix );
            }
            
            sb.append( st.nextToken() );
            
            if ( StringUtils.isNotEmpty( elementSuffix ) )
            {
                sb.append( elementSuffix );
            }
            
            result.add( sb.toString() );
        }
        
        return result;
    }
    
    static <T> List<T> toList( T[] multiple )
    {
        return toList( null, multiple );
    }
    
    static <T> List<T> toList( T single, T[] multiple )
    {
        if ( single == null && ( multiple == null || multiple.length < 1 ) )
        {
            return null;
        }
        
        List<T> result = new ArrayList<T>();
        if ( single != null )
        {
            result.add( single );
        }
        
        if ( multiple != null && multiple.length > 0 )
        {
            result.addAll( Arrays.asList( multiple ) );
        }
        
        return result;
    }
    
    // TODO: move to plexus-utils or use something appropriate from there
    public static String toRelative( File basedir, String absolutePath )
    {
        String relative;

        absolutePath = absolutePath.replace( '\\', '/' );
        String basedirPath = basedir.getAbsolutePath().replace( '\\', '/' );

        if ( absolutePath.startsWith( basedirPath ) )
        {
            relative = absolutePath.substring( basedirPath.length() );
            if ( relative.startsWith( "/" ) )
            {
                relative = relative.substring( 1 );
            }
            if ( relative.length() <= 0 )
            {
                relative = ".";
            }
        }
        else
        {
            relative = absolutePath;
        }

        return relative;
    }
    
    /**
     * Convenience method to determine that a collection is not empty or null.
     */
    public static boolean isNotEmpty( final Collection<?> collection )
    {
        return collection != null && !collection.isEmpty();
    }
    
    /**
     * Convenience method to determine that a collection is empty or null.
     */
    public static boolean isEmpty( final Collection<?> collection )
    {
        return collection == null || collection.isEmpty();
    }
   

    static boolean equalsIgnoreCase( String value, String... strings )
    {
        for ( String s : strings )
        {
            if ( s.equalsIgnoreCase( value ) )
            {
                return true;
            }
        }
        return false;
    }

    static boolean equals( String value, String... strings )
    {
        for ( String s : strings )
        {
            if ( s.equals( value ) )
            {
                return true;
            }
        }
        return false;
    }
}
