package com.dooioo.se.apidoclet.core;

import java.util.List;

import com.dooioo.se.apidoclet.core.spi.exporter.RestServicesExporter;
import com.dooioo.se.apidoclet.core.spi.exporter.RestServicesPartitionStrategy;
import com.dooioo.se.apidoclet.core.spi.filter.RestClassMethodFilter;
import com.dooioo.se.apidoclet.core.spi.filter.RestServiceFilter;
import com.dooioo.se.apidoclet.core.spi.filter.SkippedTypeFilter;
import com.dooioo.se.apidoclet.core.spi.param.RestClassMethodHeaderParamResolver;
import com.dooioo.se.apidoclet.core.spi.param.RestClassMethodPathParamResolver;
import com.dooioo.se.apidoclet.core.spi.param.RestClassMethodQueryParamResolver;
import com.dooioo.se.apidoclet.core.spi.processor.RestClassMethodPostProcessor;
import com.dooioo.se.apidoclet.core.spi.processor.RestClassPostProcessor;
import com.dooioo.se.apidoclet.core.spi.processor.RestServicePostProcessor;
import com.dooioo.se.apidoclet.core.spi.provider.BizCodeProvider;
import com.dooioo.se.apidoclet.core.spi.provider.EndpointMappingProvider;
import com.dooioo.se.apidoclet.core.spi.provider.ModelProvider;
import com.dooioo.se.apidoclet.core.spi.provider.TypeInfoProvider;
import com.dooioo.se.apidoclet.model.util.ServiceLoaderUtils;
import com.sun.javadoc.RootDoc;

/**
 * @summary Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
final class ApiDocletBuilder {
  /**
   * 命令行参数
   */
  private ApiDocletOptions options;
  /**
   * 判定哪些类提供Rest服务
   */
  private List<RestServiceFilter> restServiceFilters;

  /**
   * 判断并获取model的信息
   */
  private List<ModelProvider> modelProviders;

  /**
   * 哪些方法是rest接口
   */
  private List<RestClassMethodFilter> restMethodFilters;

  /**
   * 解析业务码
   */
  private List<BizCodeProvider> bizCodeProviders;

  /**
   * classdoc的类型信息
   */
  private List<TypeInfoProvider> typeInfoProviders;

  /**
   * 忽略哪些类型
   */
  private List<SkippedTypeFilter> skippedTypeFilters;

  /**
   * 路径映射信息
   */
  private List<EndpointMappingProvider> endpointMappingProviders;

  /**
   * 服务接口导出到展示的地方
   */
  private List<RestServicesExporter> restServicesExporters;

  /**
   * 类上的后续处理
   */
  private List<RestClassPostProcessor> restClassPostProcessors;
  /**
   * 接口方法的后续处理
   */
  private List<RestClassMethodPostProcessor> restClassMethodPostProcessors;

  /**
   * 服务的后续处理
   */
  private List<RestServicePostProcessor> restServicePostProcessors;

  ApiDocletBuilder() {
    super();
  }

  /**
   * ApiDoclet的启动选项
   */
  ApiDocletBuilder options(RootDoc rootDoc) {
    this.options = ApiDocletOptions.readFromCommandLine(rootDoc);
    return this;
  }

  /**
   * 启用RestClass过滤机制
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServiceFilters() {
    this.restServiceFilters =
        ServiceLoaderUtils.getServicesOrNull(RestServiceFilter.class);
    return this;
  }

  /**
   * 查找所有ModelProvider，用于过滤、收集model信息。
   * 
   * @author huisman
   */
  ApiDocletBuilder enableModelProviders() {
    this.modelProviders =
        ServiceLoaderUtils.getServicesOrNull(ModelProvider.class);
    return this;
  }

  /**
   * 获取所有可以提供业务吗
   * 
   * @author huisman
   */
  ApiDocletBuilder enableBizCodeProviders() {
    this.bizCodeProviders =
        ServiceLoaderUtils.getServicesOrNull(BizCodeProvider.class);
    return this;
  }

  /**
   * 查找哪些方法是Rest接口，比如，对于SpringMVC项目来说，方法上有@RequestMapping和@ResponseBody才算Rest接口
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestMethodFilters() {
    this.restMethodFilters =
        ServiceLoaderUtils.getServicesOrNull(RestClassMethodFilter.class);
    return this;
  }

  /**
   * 将一个classdoc的类型信息映射成我们的TypeInfo信息
   * 
   * @author huisman
   */
  ApiDocletBuilder enableTypeInfoProviders() {
    this.typeInfoProviders =
        ServiceLoaderUtils.getServicesOrNull(TypeInfoProvider.class);
    return this;
  }

  /**
   * 判断是否可以忽略某些参数
   * 
   * @author huisman
   */
  ApiDocletBuilder enableSkippedTypeFilters() {
    this.skippedTypeFilters =
        ServiceLoaderUtils.getServicesOrNull(SkippedTypeFilter.class);
    return this;
  }

  /**
   * 获取所有的解析RestMapping信息的实现类
   * 
   * @author huisman
   */
  ApiDocletBuilder enableEndpointMappingProviders() {
    this.endpointMappingProviders =
        ServiceLoaderUtils.getServicesOrNull(EndpointMappingProvider.class);
    return this;
  }

  /**
   * 解析后的数据导出
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServicesExports() {
    this.restServicesExporters =
        ServiceLoaderUtils.getServicesOrNull(RestServicesExporter.class);
    return this;
  }

  /**
   * 方法的后续处理
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestClassMethodPostProcessors() {
    this.restClassMethodPostProcessors =
        ServiceLoaderUtils
            .getServicesOrNull(RestClassMethodPostProcessor.class);
    return this;
  }

  /**
   * 类的后续处理
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestClassPostProcessors() {
    this.restClassPostProcessors =
        ServiceLoaderUtils.getServicesOrNull(RestClassPostProcessor.class);
    return this;
  }

  /**
   * 服务的后续处理
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServicePostProcessors() {
    this.restServicePostProcessors =
        ServiceLoaderUtils.getServicesOrNull(RestServicePostProcessor.class);
    return this;
  }



  ApiDoclet build() {
    return new ApiDoclet(this.options, this.restServiceFilters,
        this.modelProviders, this.restMethodFilters, this.bizCodeProviders,
        this.typeInfoProviders, this.skippedTypeFilters,
        ServiceLoaderUtils
            .getServicesOrNull(RestClassMethodQueryParamResolver.class),
        ServiceLoaderUtils
            .getServicesOrNull(RestClassMethodPathParamResolver.class),
        ServiceLoaderUtils
            .getServicesOrNull(RestClassMethodHeaderParamResolver.class),
        this.endpointMappingProviders, this.restServicesExporters,
        this.restClassMethodPostProcessors, this.restClassPostProcessors,
        this.restServicePostProcessors,
        ServiceLoaderUtils
            .getServicesOrNull(RestServicesPartitionStrategy.class));
  }

}
