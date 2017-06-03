package org.apidoclet.core;

import java.util.List;

import org.apidoclet.core.spi.exporter.RestServicesExporter;
import org.apidoclet.core.spi.exporter.RestServicesPartitionStrategy;
import org.apidoclet.core.spi.filter.RestClassMethodFilter;
import org.apidoclet.core.spi.filter.RestClassMethodParamFilter;
import org.apidoclet.core.spi.filter.RestClassFilter;
import org.apidoclet.core.spi.filter.TypeFilter;
import org.apidoclet.core.spi.http.HeaderParamResolver;
import org.apidoclet.core.spi.http.HttpRequestBodyResolver;
import org.apidoclet.core.spi.http.PathParamResolver;
import org.apidoclet.core.spi.http.QueryParamResolver;
import org.apidoclet.core.spi.processor.post.RestClassMethodPostProcessor;
import org.apidoclet.core.spi.processor.post.RestClassPostProcessor;
import org.apidoclet.core.spi.processor.post.RestServicePostProcessor;
import org.apidoclet.core.spi.provider.BizCodeProvider;
import org.apidoclet.core.spi.provider.EndpointMappingProvider;
import org.apidoclet.core.spi.provider.ModelProvider;
import org.apidoclet.core.spi.provider.TypeInfoProvider;
import org.apidoclet.model.util.ServiceLoaderUtils;

import com.sun.javadoc.RootDoc;

/**
 * apidoclet builder
 */
/* non-public */final class ApiDocletBuilder {
  /**
   * command line options
   */
  private ApiDocletOptions options;
  /**
   * rest service filter
   */
  private List<RestClassFilter> restClassFilters;

  /**
   * modelinfo provider
   */
  private List<ModelProvider> modelProviders;

  /**
   * RestMethod filter
   */
  private List<RestClassMethodFilter> restMethodFilters;

  /**
   * BizCodes provider
   */
  private List<BizCodeProvider> bizCodeProviders;

  /**
   * javadoc type to TypeInfo converter
   */
  private List<TypeInfoProvider> typeInfoProviders;

  /**
   * ignored type filter
   */
  private List<TypeFilter> typeFilters;

  /**
   * endpoint mapping resolver
   */
  private List<EndpointMappingProvider> endpointMappingProviders;

  /**
   * exporter
   */
  private List<RestServicesExporter> restServicesExporters;

  /**
   * class-level post processor
   */
  private List<RestClassPostProcessor> restClassPostProcessors;
  /**
   * method-level post processor;
   */
  private List<RestClassMethodPostProcessor> restClassMethodPostProcessors;

  /**
   * RestService post processor
   */
  private List<RestServicePostProcessor> restServicePostProcessors;

  ApiDocletBuilder() {
    super();
  }

  /**
   * parse command line options
   */
  ApiDocletBuilder options(RootDoc rootDoc) {
    this.options = ApiDocletOptions.readFromCommandLine(rootDoc);
    return this;
  }

  /**
   * enable RestService filters
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServiceFilters() {
    this.restClassFilters =
        ServiceLoaderUtils.getServicesOrNull(RestClassFilter.class);
    return this;
  }

  /**
   * enable customized model providers
   * 
   * @author huisman
   */
  ApiDocletBuilder enableModelProviders() {
    this.modelProviders =
        ServiceLoaderUtils.getServicesOrNull(ModelProvider.class);
    return this;
  }

  /**
   * enable customized BizCode providers
   * 
   * @author huisman
   */
  ApiDocletBuilder enableBizCodeProviders() {
    this.bizCodeProviders =
        ServiceLoaderUtils.getServicesOrNull(BizCodeProvider.class);
    return this;
  }

  /**
   * enable customized RestMethod filters
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestMethodFilters() {
    this.restMethodFilters =
        ServiceLoaderUtils.getServicesOrNull(RestClassMethodFilter.class);
    return this;
  }

  /**
   * enable customized TypeInfo converters
   * 
   * @author huisman
   */
  ApiDocletBuilder enableTypeInfoProviders() {
    this.typeInfoProviders =
        ServiceLoaderUtils.getServicesOrNull(TypeInfoProvider.class);
    return this;
  }

  /**
   * enable customized type filters
   * 
   * @author huisman
   */
  ApiDocletBuilder enableSkippedTypeFilters() {
    this.typeFilters = ServiceLoaderUtils.getServicesOrNull(TypeFilter.class);
    return this;
  }

  /**
   * enable customized endpoint resolvers
   * 
   * @author huisman
   */
  ApiDocletBuilder enableEndpointMappingProviders() {
    this.endpointMappingProviders =
        ServiceLoaderUtils.getServicesOrNull(EndpointMappingProvider.class);
    return this;
  }

  /**
   * enable customized exporters
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServicesExports() {
    this.restServicesExporters =
        ServiceLoaderUtils.getServicesOrNull(RestServicesExporter.class);
    return this;
  }

  /**
   * enable customized method level post processors
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
   * enable class-level post process
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestClassPostProcessors() {
    this.restClassPostProcessors =
        ServiceLoaderUtils.getServicesOrNull(RestClassPostProcessor.class);
    return this;
  }

  /**
   * enable service level post process
   * 
   * @author huisman
   */
  ApiDocletBuilder enableRestServicePostProcessors() {
    this.restServicePostProcessors =
        ServiceLoaderUtils.getServicesOrNull(RestServicePostProcessor.class);
    return this;
  }

  /**
   * construct a full-functional apidoclet
   */
  ApiDoclet build() {
    return new ApiDoclet(this.options, this.restClassFilters,
        this.modelProviders, this.restMethodFilters, this.bizCodeProviders,
        this.typeInfoProviders, this.typeFilters,
        ServiceLoaderUtils.getServicesOrNull(RestClassMethodParamFilter.class),
        ServiceLoaderUtils.getServicesOrNull(HttpRequestBodyResolver.class),
        ServiceLoaderUtils.getServicesOrNull(QueryParamResolver.class),
        ServiceLoaderUtils.getServicesOrNull(PathParamResolver.class),
        ServiceLoaderUtils.getServicesOrNull(HeaderParamResolver.class),
        this.endpointMappingProviders, this.restServicesExporters,
        this.restClassMethodPostProcessors, this.restClassPostProcessors,
        this.restServicePostProcessors,
        ServiceLoaderUtils
            .getServicesOrNull(RestServicesPartitionStrategy.class));
  }

}
