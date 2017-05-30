package org.apidoclet.test.model;

/**
 * @summary Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public class User {
  /**
   * 用户名
   */
  private String userName;
  /**
   * 用户编号
   */
  private long id;
  
  /**
   * 用户所属分组
   */
  private UserGroup group;

  @Override
  public String toString() {
    return "User [userName=" + userName + ", id=" + id + "]";
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public UserGroup getGroup() {
    return group;
  }

  public void setGroup(UserGroup group) {
    this.group = group;
  }

  

}
