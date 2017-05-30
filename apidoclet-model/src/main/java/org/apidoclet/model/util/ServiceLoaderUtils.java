package org.apidoclet.model.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 使用ServiceLoader加载spi实现类，所有spi实现类必须提供无参构造函数
 */
public final class ServiceLoaderUtils {
  private ServiceLoaderUtils() {
    super();
  }

  /**
   * 加载SPI的实现类，如果没找到，则返回null
   * 
   * @author huisman
   * @param spi
   * @param classLoader
   */
  public static <T> List<T> getServicesOrNull(Class<T> spi, ClassLoader classLoader) {
    ServiceLoader<T> serviceLoader = ServiceLoader.load(spi, classLoader);
    if (serviceLoader != null) {
      Iterator<T> iterator = serviceLoader.iterator();
      List<T> services = new ArrayList<T>();
      while (iterator.hasNext()) {
        services.add(iterator.next());
      }
      return services.isEmpty() ? null : services;
    }
    return null;
  }

  /**
   * 加载SPI实现类，如果没找到，则返回null
   * 
   * @author huisman
   */
  public static <T> List<T> getServicesOrNull(Class<T> spi) {
    return getServicesOrNull(spi, Thread.currentThread().getContextClassLoader());
  }
}
