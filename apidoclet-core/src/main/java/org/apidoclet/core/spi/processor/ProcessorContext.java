package org.apidoclet.core.spi.processor;

import java.util.Map;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.model.BizCode;
import org.apidoclet.model.ModelInfo;

/**
 * @summary Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public interface ProcessorContext {
  /**
   * 根据类名查找已经解析的model信息
   * 
   * @author huisman
   */
  ModelInfo getModelInfo(String qualifiedClassName);

  /**
   * 根据业务码值查找业务码信息
   * 
   * @author huisman
   */
  BizCode getBizCode(String code);

  /**
   * 获取命令行选项
   * 
   * @author huisman
   */
  ApiDocletOptions getOpitons();

  static class Default implements ProcessorContext {
    private Map<String, ModelInfo> modelMap;
    private Map<String, BizCode> spiBizCodeMap;
    private ApiDocletOptions options;

    /**
     * @param modelMap
     * @param spiBizCodeMap
     */
    public Default(Map<String, ModelInfo> modelMap, Map<String, BizCode> spiBizCodeMap,
        ApiDocletOptions options) {
      super();
      this.modelMap = modelMap;
      this.spiBizCodeMap = spiBizCodeMap;
      this.options = options;
    }

    @Override
    public ModelInfo getModelInfo(String qualifiedClassName) {
      return this.modelMap.get(qualifiedClassName);
    }

    @Override
    public BizCode getBizCode(String code) {
      return this.spiBizCodeMap.get(code);
    }

    @Override
    public ApiDocletOptions getOpitons() {
      return this.options;
    }

  }
}
