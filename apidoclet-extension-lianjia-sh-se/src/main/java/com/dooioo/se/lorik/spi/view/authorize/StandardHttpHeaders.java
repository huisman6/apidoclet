package com.dooioo.se.lorik.spi.view.authorize;

public interface StandardHttpHeaders {
	/**
	 * 员工登录后请求头会自动添加 此人登录时分配的token
	 */
	String X_Login_Token = "X-Login-Token";
	/**
	 * 员工登录后请求头会自动添加 登录人员的工号，工号为上海链家员工工号（5位或6位），如果升级8位或其他位数会特别说明
	 */
	String X_Login_UserCode = "X-Login-UserCode";
	/**
	 * 员工登录后请求头会自动添加 登录工号关联的公司ID，比如苏州链家、上海链家公司的ID
	 */
	String X_Login_CompanyId = "X-Login-CompanyId";

	/**
	 * 如果一个接口是通过API网关路由过来的，则API网关会自动在请求头、响应头里添加此HttpHeader X-Route-By是API
	 * 网关节点IP地址的加密，解密请访问：http://api.route.dooioo.org(com)/instance/{instanceId}
	 */
	String X_Route_By = "X-Route-By";

	/**
	 * 服务请求方标识，一般为调用方的spring.appliaction.name，如果此属性不存在，则默认为：Unknow;
	 */
	String X_Client_Name = "X-Client-Name";

	/**
	 * 调用方实例标识，一般为调用方的eureka.instance.id，如果此属性不存在，则默认为：Unknow;
	 */
	String X_Client_Id = "X-Client-Id";

	/**
	 * 请求方自带的安全token，将来用于身份校验
	 */
	String X_Client_Security_Token = "X-Client-Security-Token";
}
