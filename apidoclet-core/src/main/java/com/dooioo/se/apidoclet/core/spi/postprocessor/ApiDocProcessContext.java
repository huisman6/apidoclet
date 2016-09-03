package com.dooioo.se.apidoclet.core.spi.postprocessor;

import java.util.Map;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.model.BizCode;
import com.dooioo.se.apidoclet.model.ModelInfo;

/**
 * @summary Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public interface ApiDocProcessContext {
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
  BizCode getBizCode(Integer code);

  /**
   * 获取命令行选项
   * 
   * @author huisman
   */
  ApiDocletOptions getOpitons();

  static class Default implements ApiDocProcessContext {
    private Map<String, ModelInfo> modelMap;
    private Map<Integer, BizCode> spiBizCodeMap;
    private ApiDocletOptions options;

    /**
     * @param modelMap
     * @param spiBizCodeMap
     */
    public Default(Map<String, ModelInfo> modelMap, Map<Integer, BizCode> spiBizCodeMap,
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
    public BizCode getBizCode(Integer code) {
      return this.spiBizCodeMap.get(code);
    }

    @Override
    public ApiDocletOptions getOpitons() {
      return this.options;
    }

  }
}
