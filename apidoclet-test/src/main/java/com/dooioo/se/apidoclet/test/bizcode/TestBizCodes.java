package com.dooioo.se.apidoclet.test.bizcode;

import com.dooioo.se.lorik.spi.view.support.BizCode;

public interface TestBizCodes {
   /**
   * 呵呵哒
   */
  BizCode CODE1_TEST=new BizCode(20001, "测试2000");
   /**
   *巴拉拉
   */
  BizCode CODE2_TEST=new BizCode(20002, "测试2000");
  
  /**
   * 记录不存在
   */
  int NOT_FOUND=100002;
}


