package com.dooioo.se.apidoclet.core.spi.filter;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.sun.javadoc.ClassDoc;

/**
 * 判断一个类是否提供Rest服务，比如对于SpringMVC来说，<br/>
 * 如果一个类的上有注解@Controller或@RestController，我们就可以认为此类是提供Rest服务的，<br/>
 * 对于基于Spring-Cloud-Netflix的项目来说，一个接口上有注解@FeignClient则认为是提供Rest服务的 提供Rest服务的所有class都会以服务名进行分组。
 */
public interface RestServiceFilter {
  /**
   * 判断当前类是否提供Rest服务
   * 
   * @param classDoc 当前类的描述信息
   */
  boolean accept(ClassDoc classDoc, ApiDocletOptions options);

  /**
   * 推断Rest服务的服务名，比如对于@FeignClient来说，serviceId就是服务名，<br/>
   * 而普通的SpringMvc项目，则通过命令行选项 -app参数指定。
   * 
   * @param classDoc 当前源代码类的描述
   */
  String getServiceName(ClassDoc classDoc, ApiDocletOptions options);
}
