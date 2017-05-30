package org.apidoclet.test.model;

import java.util.Date;

/**
 * @summary
 * Copyright (c) 2016, Lianjia Group All Rights Reserved. 
 */
public class UserGroup {
   /**
   * 群组名
   */
  private String name;
   /**
   * 创建时间
   */
  private Date ctime;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Date getCtime() {
    return ctime;
  }
  public void setCtime(Date ctime) {
    this.ctime = ctime;
  }
  @Override
  public String toString() {
    return "UserGroup [name=" + name + ", ctime=" + ctime + "]";
  }
  
  
}


