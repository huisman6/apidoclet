package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dooioo.se.apidoclet.model.util.StandardDocTag;

/**
 * FeignClient接口或者Controller类名、JAX-RS JAVA Bean
 * 
 * @author huisman
 * @since 1.0.0
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public class RestClass implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * doc 文档初次生成时间
   */
  private Date buildAt;
  /**
   * api文档最后一次生成时间
   */
  private Date lastBuildAt;
  /**
   * 类功能简介，一般不超过20字，如果没有@summary tag，则会使用simpleClassName
   */
  private String summary;
  /**
   * 接口被废弃的日期
   */
  private Date deprecatedDate;
  /**
   * 方法被废弃的注释
   */
  private String deprecatedComment;
  /**
   * spi类的描述，类上的注释
   */
  private String description;

  /**
   * 类全称，比如：com.dooiooo.se.apidoclet.model.RestClass
   */
  private String className;
  /**
   * spi Class所在包，比如：com.dooiooo.se.apidoclet.model
   */
  private String packageName;

  /**
   * 类上的注解
   */
  private JavaAnnotations classAnnotations;

  /**
   * 该class里的所有Rest方法
   */
  private List<Method> methods = new ArrayList<>();

  /**
   *类上可能有映射信息
   */
  private EndpointMapping endpointMapping;
  
  /**
   * 除了 {@link StandardDocTag}之外，代码中的其他javadoc注释
   */
  private JavaDocTags additionalTags=null;

  /**
   * 类上的所有注解
   * 
   * @author huisman
   */
  public JavaAnnotations getClassAnnotations() {
    return classAnnotations;
  }

  public void setClassAnnotations(JavaAnnotations classAnnotations) {
    this.classAnnotations = classAnnotations;
  }
  

  public EndpointMapping getEndpointMapping() {
    return endpointMapping;
  }
  
  

  public JavaDocTags getAdditionalTags() {
    return additionalTags;
  }

  public void setAdditionalTags(JavaDocTags additionalTags) {
    this.additionalTags = additionalTags;
  }

  public void setEndpointMapping(EndpointMapping endpointMapping) {
    this.endpointMapping = endpointMapping;
  }

  public Date getBuildAt() {
    return buildAt;
  }

  public void setBuildAt(Date buildAt) {
    this.buildAt = buildAt;
  }

  public Date getLastBuildAt() {
    return lastBuildAt;
  }

  public void setLastBuildAt(Date lastBuildAt) {
    this.lastBuildAt = lastBuildAt;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Date getDeprecatedDate() {
    return deprecatedDate;
  }

  public void setDeprecatedDate(Date deprecatedDate) {
    this.deprecatedDate = deprecatedDate;
  }

  public String getDeprecatedComment() {
    return deprecatedComment;
  }

  public void setDeprecatedComment(String deprecatedComment) {
    this.deprecatedComment = deprecatedComment;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Method> getMethods() {
    return methods;
  }

  public void setMethods(List<Method> methods) {
    if (methods == null || methods.isEmpty()) {
      return;
    }
    this.methods.addAll(methods);
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void addMethod(Method spiMethod) {
    if (spiMethod == null) {
      return;
    }
    this.methods.add(spiMethod);
  }

  @Override
  public String toString() {
    final int maxLen = 90;
    return "SpiClass [buildAt=" + buildAt + ", lastBuildAt=" + lastBuildAt+",endpointMapping="
        +endpointMapping+ ", summary=" + summary
        + ", deprecatedDate=" + deprecatedDate + ", deprecatedComment=" + deprecatedComment
        + ", description=" + description + ", className=" + className + ", packageName="
        + packageName + ", methods="
        + (methods != null ? methods.subList(0, Math.min(methods.size(), maxLen)) : null) + "]";
  }

  /**
   * Controller或者FeignClient接口方法的描述，或者JAX-RS 类所在方法的描述
   */
  public static class Method implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char IDENTITY_SPILIT_CHAR = '#';
    /**
     * 标识符，默认由完整类名+方法名+方法返回值（如果有的话）
     */
    private String identity;
    /**
     * 方法所在类的名称
     */
    private String declaringClass;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 功能说明
     */
    private String description;
    /**
     * 版本号，对应javadoc tag @version
     */
    private String version;
    /**
     * 方法发布或者上线日期，对应javadoc tag @since
     */
    private String since;

    /**
     * 作者信息，对应javadoc tag @author
     */
    private String author;

    /**
     * 方法功能简介，一般不超过20字，如果没有@summary tag，则会使用methodName
     */
    private String summary;
    /**
     * RequestMapping或JAX-RS @PATH的封装，可能会合并类上的RequestMapping
     */
    private EndpointMapping mapping;
    /**
     * url 路径参数
     */
    private List<PathParam> pathParams;
    /**
     * url查询参数
     */
    private List<QueryParam> queryParams;

    /**
     * http 请求参数
     */
    private List<HeaderParam> requestHeaders;

    /**
     * 接口被废弃的日期，，对应javadoc tag @deprecated ,如果没指定日期，则为首次生成该字段的日期
     */
    private Date deprecatedDate;
    /**
     * 方法被废弃的注释
     */
    private String deprecatedComment;

    /**
     * 方法可能抛出的业务码
     */
    private List<BizCode> bizCodes;

    /**
     * 方法返回类型的抽象
     */
    private TypeInfo returnType;
    /**
     * 方法的参数
     */
    private List<MethodParameter> methodParameters;

    /**
     * 方法上的注释
     */
    private JavaAnnotations methodAnnotations;
    
    /**
     * 除了标准javadoctag之外的其他tags
     */
    private JavaDocTags additionalDocTags;
    
    
    public JavaDocTags getAdditionalDocTags() {
      return additionalDocTags;
    }

    public void setAdditionalDocTags(JavaDocTags additionalDocTags) {
      this.additionalDocTags = additionalDocTags;
    }

    public List<MethodParameter> getMethodParameters() {
      return methodParameters;
    }

    public List<BizCode> getBizCodes() {
      return bizCodes;
    }



    /**
     * 方法上的所有注解
     * 
     * @author huisman
     */
    public JavaAnnotations getMethodAnnotations() {
      return methodAnnotations;
    }

    public void setMethodAnnotations(JavaAnnotations methodAnnotations) {
      this.methodAnnotations = methodAnnotations;
    }

    public void setBizCodes(List<BizCode> bizCodes) {
      this.bizCodes = bizCodes;
    }


    public EndpointMapping getMapping() {
      return mapping;
    }

    public void setMapping(EndpointMapping mapping) {
      this.mapping = mapping;
    }

    public void setMethodParameters(List<MethodParameter> methodParameters) {
      this.methodParameters = methodParameters;
    }

    public Date getDeprecatedDate() {
      return deprecatedDate;
    }

    public void setDeprecatedDate(Date deprecatedDate) {
      this.deprecatedDate = deprecatedDate;
    }

    public String getDeprecatedComment() {
      return deprecatedComment;
    }

    public void setDeprecatedComment(String deprecatedComment) {
      this.deprecatedComment = deprecatedComment;
    }

    public TypeInfo getReturnType() {
      return returnType;
    }

    public void setReturnType(TypeInfo returnType) {
      this.returnType = returnType;
    }

    public List<PathParam> getPathParams() {
      return pathParams;
    }

    public List<QueryParam> getQueryParams() {
      return queryParams;
    }

    public String getIdentity() {
      return identity;
    }

    public void setIdentity(String identity) {
      this.identity = identity;
    }

    public void setPathParams(List<PathParam> pathParams) {
      this.pathParams = pathParams;
    }

    public void setQueryParams(List<QueryParam> queryParams) {
      this.queryParams = queryParams;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }

    public String getSince() {
      return since;
    }

    public void setSince(String since) {
      this.since = since;
    }

    public String getSummary() {
      return summary;
    }

    public void setSummary(String summary) {
      this.summary = summary;
    }

    public List<HeaderParam> getRequestHeaders() {
      return requestHeaders;
    }

    public void setRequestHeaders(List<HeaderParam> requestHeaders) {
      this.requestHeaders = requestHeaders;
    }


    public String getDeclaringClass() {
      return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
      this.declaringClass = declaringClass;
    }

    public String getMethodName() {
      return methodName;
    }

    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    @Override
    public String toString() {
      return "Method [identity=" + identity + ", declaringClass=" + declaringClass
          + ", methodName=" + methodName + ", description=" + description + ", version=" + version
          + ", since=" + since + ", author=" + author + ", summary=" + summary + ", mapping="
          + mapping + ", pathParams=" + pathParams + ", queryParams=" + queryParams
          + ", requestHeaders=" + requestHeaders + ", deprecatedDate=" + deprecatedDate
          + ", deprecatedComment=" + deprecatedComment + ", bizCodes=" + bizCodes + ", returnType="
          + returnType + ", methodParameters=" + methodParameters + ", methodAnnotations="
          + methodAnnotations + ", additionalDocTags=" + additionalDocTags + "]";
    }


    
  }

}
