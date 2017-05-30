package org.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RestEndpoint映射信息的抽象，包含HttpMethod、RequestParam、PathParam、
 * HeaderParam、条件参数等，EndpointMapping用于描述一个如何访问一个Rest接口。
 */
public class EndpointMapping implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * request mapping 访问路径 (value)
   */
  private String path;
  /**
   * Http 请求方法 (method)，默认为GET
   */
  private HttpMethod method;
  /**
   * 可处理的Request Content-Type。
   */
  private List<Consume> consumes;
  /**
   * 响应给客户端的Content-Type，通常优先根据兼容客户端的Accept请求头
   */
  private List<Produce> produces;

  /**
   * 条件参数（http请求参数），可以为null
   */
  private List<ParamCondition> requstParamConditions;
  /**
   * 条件参数（头部），可以为null
   */
  private List<HeaderCondition> requestHeaderConditions;

  public EndpointMapping() {
    super();
  }


  /**
   * @param path 完整的请求路径，如果是多个EndpointMapping,应该combine起来
   * @param method Http请求Method
   */
  public EndpointMapping(String path, HttpMethod method) {
    super();
    this.path = path;
    this.method = method;
  }

  public List<ParamCondition> getRequstParamConditions() {
    return requstParamConditions;
  }

  public void setRequstParamConditions(List<ParamCondition> requstParamConditions) {
    this.requstParamConditions = requstParamConditions;
  }


  public List<HeaderCondition> getRequestHeaderConditions() {
    return requestHeaderConditions;
  }


  public void setRequestHeaderConditions(List<HeaderCondition> requestHeaderConditions) {
    this.requestHeaderConditions = requestHeaderConditions;
  }


  public String getPath() {
    return path;
  }


  public void setPath(String path) {
    this.path = path;
  }


  public HttpMethod getMethod() {
    return method;
  }


  public void setMethod(HttpMethod method) {
    this.method = method;
  }


  public List<Consume> getConsumes() {
    return consumes;
  }

  public void setConsumes(List<Consume> consumes) {
    this.consumes = consumes;
  }


  public List<Produce> getProduces() {
    return produces;
  }


  public void setProduces(List<Produce> produces) {
    this.produces = produces;
  }


  /**
   * 合并两个requst mapping（also @path)
   */
  public EndpointMapping combine(EndpointMapping provided) {
    // fullMapping
    EndpointMapping fullMapping = new EndpointMapping();
    // 当前路径
    String combinedPath = (this.path == null ? "" : this.path);

    // 待追加的路径
    if (!isEmpty(provided.path)) {
      boolean hasSlash = provided.path.startsWith("/");
      if (hasSlash) {
        // 待追加的路径以/结尾，移除一个"/"
        if (combinedPath.endsWith("/")) {
          combinedPath = combinedPath.substring(0, combinedPath.lastIndexOf("/"));
        }
      } else {
        if (combinedPath.endsWith("/")) {
          hasSlash = true;
        }
      }
      combinedPath = combinedPath + (hasSlash ? "" : "/") + provided.path;
    }
    // 没有指明路径？
    if (isEmpty(combinedPath)) {
      combinedPath = "/";
    }
    fullMapping.setPath(combinedPath);

    // 默认以provided的方法为准，其次是本Mapping
    if (provided.getMethod() == null) {
      fullMapping.setMethod(this.method);
    } else {
      fullMapping.setMethod(provided.getMethod());
    }

    if (fullMapping.getMethod() == null) {
      // 不指明method,则设置为GET
      fullMapping.setMethod(HttpMethod.of("GET"));
    }


    // 其余的取并集

    List<Consume> consumeConditions = new ArrayList<>();
    if (this.consumes != null && !this.consumes.isEmpty()) {
      consumeConditions.addAll(this.consumes);
    }

    if (provided.consumes != null && !provided.consumes.isEmpty()) {
      consumeConditions.addAll(provided.consumes);
    }
    fullMapping.setConsumes(consumeConditions);


    // 其余的取并集
    List<Produce> produceConditions = new ArrayList<>();
    if (this.produces != null && !this.produces.isEmpty()) {
      produceConditions.addAll(this.produces);
    }

    if (provided.produces != null && !provided.produces.isEmpty()) {
      produceConditions.addAll(provided.produces);
    }
    fullMapping.setProduces(produceConditions);


    // 其余的取并集
    List<ParamCondition> paramConditions = new ArrayList<>();
    if (this.requstParamConditions != null && !this.requstParamConditions.isEmpty()) {
      paramConditions.addAll(this.requstParamConditions);
    }

    if (provided.requstParamConditions != null && !provided.requstParamConditions.isEmpty()) {
      paramConditions.addAll(provided.requstParamConditions);
    }
    fullMapping.setRequstParamConditions(paramConditions);

    // 其余的取并集
    List<HeaderCondition> headerConditions = new ArrayList<>();
    if (this.requestHeaderConditions != null && !this.requestHeaderConditions.isEmpty()) {
      headerConditions.addAll(this.requestHeaderConditions);
    }

    if (provided.requestHeaderConditions != null && !provided.requestHeaderConditions.isEmpty()) {
      headerConditions.addAll(provided.requestHeaderConditions);
    }
    fullMapping.setRequestHeaderConditions(headerConditions);

    return fullMapping;
  }

  private boolean isEmpty(String input) {
    return input == null || input.trim().isEmpty();
  }


  @Override
  public String toString() {
    return "EndpointMapping [path=" + path + ", method=" + method + ", consumes=" + consumes
        + ", produces=" + produces + "]";
  }



  public static class RequestCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_SPLIT_CHAR = "=";
  }

  /**
   * 映射信息的参数条件
   */
  public static class ParamCondition extends RequestCondition {
    private static final long serialVersionUID = 1L;
    /**
     * 原生字符串，name和value是使用 {@link #DEFAULT_SPLIT_CHAR} 后分隔出来的key/value队
     */
    private String name;
    private String value;

    /**
     * @param raw
     * @param name
     * @param value
     * @param kind
     */
    public ParamCondition(String name, String value) {
      super();
      this.name = name;
      this.value = value;
    }

    public ParamCondition() {
      super();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "ConditionParam [ name=" + name + ", value=" + value + "]";
    }
  }


  /**
   * header中条件参数
   */
  public static class HeaderCondition extends RequestCondition {
    private static final long serialVersionUID = 1L;
    /**
     * 原生字符串，name和value是使用 {@link #DEFAULT_SPLIT_CHAR} 后分隔出来的key/value队
     */
    private String name;
    private String value;

    /**
     * @param raw
     * @param name
     * @param value
     * @param kind
     */
    public HeaderCondition(String name, String value) {
      super();
      this.name = name;
      this.value = value;
    }

    public HeaderCondition() {
      super();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "HeaderCondition [ name=" + name + ", value=" + value + "]";
    }
  }
}
