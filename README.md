### Lorik

**Lorik** ,取自 Rainbow  **Lorik**eet（[彩虹鹦鹉](https://en.wikipedia.org/wiki/Rainbow_lorikeet)），是一种多彩而美丽的鸟。

![彩虹鹦鹉](https://raw.githubusercontent.com/huisman6/lorik/master/rainbow_lorikeet.jpg)

**Lorik**项目旨在为上海链家SE团队的微服务开发提供基础支持，并且功能会不断完善。

**Lorik**项目目前分为四部分：

* lorik-apidoclet
根据SPI接口的Java Doc 自动生成Rest接口文档。
例如，以下SPI接口：  

<pre><code class=“java”>
@FeignClient("loupan-server")
public interface IDistrictService{
	/**
	 * 根据区域ID查找区域，方法的详细说明
	 * 说明，说明。
	 * @author huisman
	 * @version v1
	 * @param  districtId 区域ID
	 * @param  userCode 工号
	 * @since 2016-01-01
	 * @summary 根据ID查找区域 
	 */
	@LorikRest(codes={“200001:区域不存在”,”200002:区域已认证”})
	@RequestMapping(value = "/v1/district/{districtId}", method = RequestMethod.GET)
	District  findDistrict(@PathVariable(value = "districtId") long districtId,@RequestParam(value=“userCode”,required=false,defaultValue=“8080”)Integer userCode);
}
</code></pre>

将会被转换为API 文档：

|  字段  | 说明|
| :------------ | :-----------| 
| Request Path  | GET /v1/district/{districtId}?userCode={userCode}  |
| 接口功能  | 根据ID查找区域 （since 2016-01-01)         |
| 接口说明  | 根据区域ID查找区域，方法的详细说明<br>说明，说明。      |
| 版本号  | v1          |
| 路径参数 | @PathVariable districtId，区域ID, 类型:Long，必填       |
| 请求参数 | @RequestParam userCode，工号，类型:Integer，可选，默认值8080      |
| 错误码|200001:区域不存在<br>200002:区域已认证 |
| 返回值| District json 结构|
| author|huisman|

API文档的展示格式可能会调整，接下来准备支持Rest接口自动化测试。

* **lorik-core**  
对开发符合SE 微服务规范的Spring Cloud 项目提供基础支持，不仅支持Spring Cloud项目，也支持普通的Spring MVC 项目（Spring MVC 3.1及以上）。 
* **lorik-spi-view**  
 提供ListView 和BeanView，允许接口返回这两种类型，以便客户端随意转换返回的Model。扩展了Spring Cloud 对 Netflix Feign 的配置，对Model提供视图支持。
* **lorik-spi-security-plugin**  
 SPI安全插件，对SPI调用提供更高级别身份校验。
