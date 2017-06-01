package com.dooioo.se.lorik.spi.view;

import com.dooioo.se.lorik.spi.view.support.BizCode;

/**
 * SPI VIEW内置的一些业务码
 */
public interface SpiBuiltinBizCode {

  /**
   * API网关未登录，当报此错误时，需要先向API网关申请Token
   */
  BizCode API_GATEWAY_UNLOGIN = new BizCode(900001, "API网关未登录");

  /**
   * token已过期，需重新申请
   */
  BizCode API_GATEWAY_XTOKEN_EXPIRED = new BizCode(900002, "Token 已过期，请及时申请。");
  /**
   * 网络原因导致校验失败（API网关查询缓存失败），重试即可解决
   */
  BizCode API_GATEWAY_XTOKEN_NETWORK_CAUSE_FAILED = new BizCode(900003, "网络原因导致Token校验失败");

  /**
   *  非法请求，比如伪造X-Login-Token、X-Client-Security-Token
   */
  BizCode INVALID_REQUEST = new BizCode(900004, "非法请求");
  
  /**
   * 服务端接口返回值为Null
   */
  BizCode LORIK_REST_NULL_TO_404 = new BizCode(910000, "资源不存在");
}
