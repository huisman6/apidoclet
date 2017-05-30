package org.apidoclet.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 类加载器，加载一些class文件，方便反射，可以指定加载的目录，但必须为绝对路径。
 */
public class ApiDocletClassLoader extends ClassLoader {
  /**
   * class文件所在目录
   */
  private String[] classpaths;

  public void setClassdir(String... classpaths) {
    this.classpaths = classpaths;
  }

  /**
   * @param parent
   */
  public ApiDocletClassLoader(ClassLoader parent) {
    super(parent);
  }

  public ApiDocletClassLoader(String... classpaths) {
    super();
    this.classpaths = classpaths;
  }



  /**
   * @param parent
   */
  public ApiDocletClassLoader(ClassLoader parent, String... classpaths) {
    super(parent);
    this.classpaths = classpaths;
  }



  @Override
  protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    Class<?> result = loadClassInternal(name);
    if (result != null) {
      if (resolve) {
        super.resolveClass(result);
      }
      return result;
    } else {
      return super.loadClass(name, resolve);
    }
  }


  /**
   * 加载我们的目标类
   */
  private Class<?> loadClassInternal(String name) throws ClassNotFoundException {
    Class<?> result = findLoadedClass(name);
    if (result == null) {
      byte[] bytes = loadBytesForClass(name);
      if (bytes != null) {
        result = defineClass(name, bytes, 0, bytes.length);
      }
    }
    return result;
  }

  private byte[] loadBytesForClass(String name) throws ClassNotFoundException {
    try (InputStream is = loadClassFile(name);) {
      if (is == null) {
        return null;
      }
      // buffer block
      ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
      byte[] buffer = new byte[4096];
      int lenOfRead = -1;
      while ((lenOfRead = is.read(buffer)) > 0) {
        out.write(buffer, 0, lenOfRead);
      }
      out.flush();
      return out.toByteArray();
    } catch (Exception e) {
      throw new ClassNotFoundException(name + "not found in :"
          + Arrays.toString(classpaths), e);
    }
  }

  /**
   * 从指定目录加载类文件
   * 
   * @author huisman
   * @throws IOException
   */
  private InputStream loadClassFile(String name) throws IOException {
    // jdk 类，直接返回
    if (name.startsWith("java.")) {
      return null;
    }
    if (this.classpaths == null || this.classpaths.length == 0) {
      return null;
    }
    // 扫描所有classpath
    for (String classpath : classpaths) {
      if (StringUtils.isNullOrEmpty(classpath)) {
        continue;
      }
      if (!classpath.endsWith(File.separator)) {
        classpath = classpath + File.separatorChar;
      }
      //absolute class file path
      String path =
          classpath + name.replace('.', File.separatorChar) + ".class";
      File file = new File(path);
      if (file.exists() && file.isFile()) {
        return new FileInputStream(path);
      }
    }
    return null;
  }
}
