package org.apidoclet.core.spi.provider;

import java.util.List;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.model.BizCode;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

/**
 * 获取当前项目中的业务码，也可能没有
 */
public interface BizCodeProvider {
  
  /**
    * 获取默认提供的业务码，默认提供的业务码全局共享
    * @author huisman
    * @version v1
    * @param options 命令行选项
   */
  List<BizCode> provided(ApiDocletOptions options);
  /**
    * 根据类的元数据信息获取业务码，如果不存在，返回null
    * @author huisman
    * @param classDoc 源文件的描述
    * @param options 命令行选项
   */
  List<BizCode> produce(ClassDoc classDoc,ApiDocletOptions options);
  /**
    * 从方法上解析业务码，如果不存在，返回null
    * @author huisman
    * @param methodDoc 源文件中方法的描述
    * @param classDoc  class info
    * @param apiDocletOptions 命令行选项
   */
  List<BizCode> produce(ClassDoc classDoc,MethodDoc methodDoc,ApiDocletOptions apiDocletOptions);
}


