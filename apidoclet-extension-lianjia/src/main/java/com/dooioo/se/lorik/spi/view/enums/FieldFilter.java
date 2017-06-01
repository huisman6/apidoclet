package com.dooioo.se.lorik.spi.view.enums;

/**
 * 字段过滤级别，我们可以根据数据表中字段查询的频率来分组，short是基本数据，比如id,name，是使用次数最多的字段; medium比short增加了一些信息，比如备注，拼音等;
 * full返回所有字段，比如坐标信息。 full是所有字段。
 * 
 * @summary
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public enum FieldFilter {
  /**
   * 返回model的所有字段
   */
  FULL,
  /**
   * 返回中等数量的字段
   */
  MEDIUM,
  /**
   * 返回最基本的业务字段
   */
  SHORT;
}
