package com.dooioo.se.apidoclet.core.spi.provider;

import java.util.List;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.BizCode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * 获取当前项目中的业务码，也可能没有
 */
public interface BizCodeProvider {
  
  /**
    * 获取默认提供的业务码
    * @author huisman
    * @version v1
    * @param options 命令行选项
   */
  List<BizCode> provided(ApiDocletOptions options);
  /**
    * 从类上获取业务码，如果不存在，返回null
    * @author huisman
    * @param classDoc 源文件的描述
    * @param options 命令行选项
   */
  List<BizCode> produce(ClassDoc classDoc,ApiDocletOptions options);
  /**
    * 从方法上解析业务码，如果不存在，返回null
    * @author huisman
    * @param methodDoc 源文件中方法的描述
    * @param apiDocletOptions 命令行选项
   */
  List<BizCode> produce(MethodDoc methodDoc,ApiDocletOptions apiDocletOptions);
}


