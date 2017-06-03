package org.apidoclet.core.spi.filter;

import org.apidoclet.core.ApiDocletOptions;

import com.sun.javadoc.ClassDoc;

/**
 * 判断一个类是否提供Rest服务，比如对于SpringMVC来说，<br/>
 * 如果一个类的上有注解@Controller或@RestController，我们就可以认为此类是提供Rest服务的，<br/>
 * 对于基于Spring-Cloud-Netflix的项目来说，一个接口上有注解@FeignClient则认为是提供Rest服务的 提供Rest服务的所有class都会以服务名进行分组。
 */
public interface RestClassFilter {
  /**
   * 判断当前类是否提供Rest服务
   * 
   * @param classDoc 当前类的描述信息
   */
  boolean accept(ClassDoc classDoc, ApiDocletOptions options);

  /**
   * 获取RestClass上可能存在的关于服务名的annotation，比如对于@FeignClient来说，serviceId就是服务名，<br/>
   * 
   * 如果不存在，可以返回为null
   * 
   * @param classDoc 当前源代码类的描述
   */
  String getServiceIdIfAny(ClassDoc classDoc, ApiDocletOptions options);
}
