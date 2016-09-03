package com.dooioo.se.apidoclet.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author huisman
 * @version 1.0.0
 * @since 2016年1月16日 Copyright (c) 2016, BookDao All Rights Reserved.
 */
public class ApiDocletClassLoader extends ClassLoader {
  /**
   * class文件所在目录
   */
  private String classdir;

  public void setClassdir(String classdir) {
    this.classdir = classdir;
  }

  /**
   * @param parent
   */
  public ApiDocletClassLoader(ClassLoader parent) {
    super(parent);
  }


  /**
   * 
   */
  public ApiDocletClassLoader(String classdir) {
    super();
    this.classdir = classdir;
  }
  
 


  /**
   * @param parent
   */
  public ApiDocletClassLoader(ClassLoader parent, String classdir) {
    super(parent);
    this.classdir = classdir;
  }



  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
      throw new ClassNotFoundException(name + "not found in :" + this.classdir, e);
    }
  }

  /**
   * 从指定目录加载类文件
   * 
   * @author huisman
   */
  private InputStream  loadClassFile(String name) {
    //jdk 类，直接返回
    if (name.startsWith("java.")) {
       return null;
    }
    File classfile =new File(this.classdir,name.replace('.', '/') + ".class");
    System.out.println("loading class file:"+classfile);
    try {
      return new FileInputStream(classfile);
    } catch (FileNotFoundException e) {
      //ignored use parent classloader to load
    }
    return null;
  }

}
