package com.dooioo.se.apidoclet.model.util.spi;

import java.util.List;
import java.util.Set;

import com.dooioo.se.apidoclet.model.util.Types;

/**
 * type expand
 */
public interface TypesExtension {
  /**
   * 获取其他扩展的集合类型类型，可能不是Collection的子类，但是也可以理解为集合。
   * 
   * @see Types#isCollectionType(String)
   * @author huisman
   */
  Set<String> getAdditionalCollectionTypes();

  /**
   * 默认一些primitive类型及其封装类型都会被当做简单类型，但是客户端可以额外提供一些其他简单类型， 甚至可以覆盖默认配置。
   * 
   * @see Types#getSimpleTypeDefaultValue(String)
   * @author huisman
   */
  List<SimpleTypeMapping> getAdditionalSimpleTypeMapping();

  public static final class SimpleTypeMapping {
    /**
     * 类型名
     */
    private String qulifiedTypeName;
    /**
     * 对应的默认值
     */
    private Object defaultValue;

    public SimpleTypeMapping() {
      super();
    }

    /**
     * @param qulifiedTypeName 类型名
     * @param defaultValue 默认值
     */
    public SimpleTypeMapping(String qulifiedTypeName, Object defaultValue) {
      super();
      this.qulifiedTypeName = qulifiedTypeName;
      this.defaultValue = defaultValue;
    }

    public String getQulifiedTypeName() {
      return qulifiedTypeName;
    }

    public void setQulifiedTypeName(String qulifiedTypeName) {
      this.qulifiedTypeName = qulifiedTypeName;
    }

    public Object getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
      this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
      return "SimpleTypeMapping [qulifiedTypeName=" + qulifiedTypeName
          + ", defaultValue=" + defaultValue + "]";
    }

  }
}
