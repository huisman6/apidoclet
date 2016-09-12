package com.dooioo.se.lorik.spi.view.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解仅在API网关访问（api.route.dooioo.org/com）时生效。<br>
 * 支持以下特性：
 * <ul>
 * <li>方法返回结果为null时，统一响应404。</li>
 * <li>列举业务码，可以展示在API文档里。</li>
 * </ul>
 * 
 * 用法示例如下：
 * 
 * <pre>
 *    <code>
 *     public interface  CitySpiV1{
 *         \@LorikRest(value={Feature.NullTo404},codes={"20001:城市不存在","20011:城市已删除"})
 *         \@LoginNeedless
 *         public City findByIdV1(@PathVariable("id")int id);
 *         
 *     }
 *   </code>
 * </pre>
 * 
 * @author huisman
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface LorikRest {

  Feature[] value() default {};

  /**
   * 业务码集合
   */
  int[] codes() default {};

  public static enum Feature {
    /**
     * 将返回结果为null的统一转换响应为404异常
     */
    NullTo404;
  }
}
