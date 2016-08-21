package com.dooioo.se.apidoclet.model.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
  * api 文档 的配置文件
  * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
*/
public class ApiDocJson  implements Serializable{
  private static final long serialVersionUID = 1L;
  
  /**
   * 返回json示例的key的后缀
   */
  public static final  String CONFIG_KEY_RETURN_JSON_SUFFIX=".returnJson";
  
  /**
   * 环境参数的配置
   */
  private EnvVariable env;
  
  /**
   * apidoc的其他配置，必须是key=value
   */
  private Map<String,String> config=new HashMap<>();
  
  
   
  /**
   * 环境参数的配置
   * @return the env
   */
  public EnvVariable getEnv() {
    return env;
  }



  /**
  *  环境参数的配置
   * @param env
   */
  public void setEnv(EnvVariable env) {
    this.env = env;
  }



  /**
   * 除了环境参数外的其他配置
   * @return the config
   */
  public Map<String, String> getConfig() {
    return config;
  }



  /**
   * @param config
   */
  public void setConfig(Map<String, String> config) {
    this.config = config;
  }
}
