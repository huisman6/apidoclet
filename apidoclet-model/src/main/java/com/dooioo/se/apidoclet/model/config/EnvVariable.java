package com.dooioo.se.apidoclet.model.config;

import java.io.Serializable;

/**
 * 头部环境参数
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public class EnvVariable implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 环境参数的headers
   */
  String[] headers;

  /**
   * 每一行记录
   */
  Row[] rows;

  /**
   * 
   * @return the headers
   */
  public String[] getHeaders() {
    return headers;
  }



  /**
   * 
   * @param headers
   */
  public void setHeaders(String[] headers) {
    this.headers = headers;
  }



  /**
   * 
   * @return the rows
   */
  public Row[] getRows() {
    return rows;
  }

  /**
   * 
   * @param rows
   */
  public void setRows(Row[] rows) {
    this.rows = rows;
  }

  public static class Row implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 每一列的值
     */
    private String[] columns;

    /**
     * 
     * @return the columns
     */
    public String[] getColumns() {
      return columns;
    }

    /**
     * 
     * @param columns
     */
    public void setColumns(String[] columns) {
      this.columns = columns;
    }

  }
}
