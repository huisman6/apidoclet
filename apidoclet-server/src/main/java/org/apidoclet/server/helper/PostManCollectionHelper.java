//package org.apidoclet.server.helper;
//
//import com.dooioo.se.lorik.apidoclet.contract.model.PathParam;
//import com.dooioo.se.lorik.apidoclet.contract.model.QueryParam;
//import com.dooioo.se.lorik.apidoclet.contract.model.RequestHeader;
//import com.dooioo.se.lorik.apidoclet.contract.model.SpiMethod;
//import com.dooioo.se.lorik.apidoclet.server.model.*;
//import com.dooioo.se.lorik.core.security.EncryptProvider;
//import org.springframework.util.StringUtils;
//
//import java.util.*;
//import java.util.Map.Entry;
//
///**
// * Created with IntelliJ IDEA. PackageName:com.dooioo.se.lorik.apidoclet.server.helper
// *
// * @Author: Jerry.hu
// * @Create: Jerry.hu (2016-02-01 15:21)
// * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
// * @Description: To change this template use File | Settings | File Templates.
// */
//public class PostManCollectionHelper {
//
//		List<PostManRequest> postManRequests = new ArrayList<> ();
//		/**
//		 * 是否使用占位符，使用占位符，那么所有pathvariable,param都会用占位符表示
//		 */
//		private boolean usePlaceholder = true;
//		private boolean ignoreVirtualPath = false;
//		private String host;
//		/**
//		 * 占位符
//		 */
//		private String leftPlaceholder = "{{";
//		/**
//		 * 右占位符
//		 */
//		private String rightPlaceholder = "}}";
//
//		private Set<String> ignoredHeaders =
//				new HashSet<> (Arrays.asList ("X-Login-Token".toLowerCase (), "X-Login-UserCode".toLowerCase (), "X-Login-CompanyId".toLowerCase (), "X-Token".toLowerCase ()));
//
//
//
//		public PostManCollectionHelper() {
//				this (false);
//		}
//
//		public PostManCollectionHelper(boolean userPlaceholder,
//				boolean ignoreVirtualPath,
//				String host) {
//				super ();
//				this.usePlaceholder = userPlaceholder;
//				this.ignoreVirtualPath = ignoreVirtualPath;
//				this.host = host;
//		}
//
//		public PostManCollectionHelper(boolean userPlaceholder) {
//				this (userPlaceholder, false, null);
//		}
//
//		private PostManMain initPostMan(VersionedRestApp restApp) {
//				PostManMain postManMain = new PostManMain ();
//				postManMain.setName (restApp.getOriginal ()
//																		.getAppName ());
//				postManMain.setDescription (restApp.getOriginal ()
//																					 .getAppName ());
//				postManMain.setId (EncryptProvider.md5Hex (restApp.getOriginal ()
//																													.getApp (), true));
//				postManMain.setOwner (EncryptProvider.md5Hex (restApp.getOriginal ()
//																														 .getApp (), true));
//				postManMain.setTimestam (restApp.getOriginal ()
//																				.getBuildAt ()
//																				.getTime ());
//				postManMain.setOrder (new String[] {});
//				return postManMain;
//		}
//
//		public PostManMain getPostManMain(VersionedRestApp restApp) {
//				PostManMain postManMain = initPostMan (restApp);
//				postManMain.setFolders (formatPostManFolder (restApp));
//				postManMain.setRequests (postManRequests);
//				return postManMain;
//		}
//
//		public PostManMain getPostManMain(VersionedRestApp restApp,
//				VersionGroupedRestClass spiClass) {
//				PostManMain postManMain = initPostMan (restApp);
//				postManMain.setFolders (formatPostManFolder (restApp, spiClass));
//				postManMain.setRequests (postManRequests);
//				return postManMain;
//		}
//
//		public PostManMain getPostManMain(VersionedRestApp restApp,
//				VersionGroupedRestClass spiClass,
//				SpiMethod method) {
//				PostManMain postManMain = initPostMan (restApp);
//				postManMain.setFolders (formatPostManFolder (restApp, spiClass, method));
//				postManMain.setRequests (postManRequests);
//				return postManMain;
//		}
//
//
//
//		private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp) {
//				Map<String, VersionGroupedRestClass> spiClassMap = restApp.getIdToSpiClassMap ();
//				List<PostManFolder> postManFolders = new ArrayList<> ();
//				for (Entry<String, VersionGroupedRestClass> entry : spiClassMap.entrySet ()) {
//						VersionGroupedRestClass spiClass = entry.getValue ();
//						PostManFolder postManFolder = initPostManFolder (spiClass);
//						postManFolder.setOwner (EncryptProvider.md5Hex (restApp.getOriginal ()
//																																	 .getApp (), true));
//						postManFolder.setOrder (formatPostManRequestAndOrder (spiClass.getOriginal ()
//																																					.getMethods (), restApp));
//						postManFolders.add (postManFolder);
//				}
//
//				return postManFolders;
//		}
//
//		public boolean isUsePlaceholder() {
//				return usePlaceholder;
//		}
//
//
//
//		public boolean isIgnoreVirtualPath() {
//				return ignoreVirtualPath;
//		}
//
//		public String getHost() {
//				return host;
//		}
//
//		private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp,
//				VersionGroupedRestClass spiClass) {
//				List<PostManFolder> postManFolders = new ArrayList<> ();
//				PostManFolder postManFolder = initPostManFolder (spiClass);
//				postManFolder.setOwner (EncryptProvider.md5Hex (restApp.getOriginal ()
//																															 .getApp (), true));
//				postManFolder.setOrder (formatPostManRequestAndOrder (spiClass.getOriginal ()
//																																			.getMethods (), restApp));
//				return postManFolders;
//		}
//
//		private List<PostManFolder> formatPostManFolder(VersionedRestApp restApp,
//				VersionGroupedRestClass spiClass,
//				SpiMethod method) {
//				List<PostManFolder> postManFolders = new ArrayList<> ();
//				PostManFolder postManFolder = initPostManFolder (spiClass);
//				postManFolder.setOwner (EncryptProvider.md5Hex (restApp.getOriginal ()
//																															 .getApp (), true));
//				postManFolder.setOrder (formatPostManRequestAndOrder (method, restApp));
//				return postManFolders;
//		}
//
//
//		private PostManFolder initPostManFolder(VersionGroupedRestClass spiClass) {
//				PostManFolder postManFolder = new PostManFolder ();
//				postManFolder.setId (EncryptProvider.md5Hex (spiClass.getOriginal ()
//																														 .getClassName (), true));
//				postManFolder.setName (spiClass.getOriginal ()
//																			 .getSummary ());
//				postManFolder.setDescription (spiClass.getOriginal ()
//																							.getDescription ());
//				return postManFolder;
//		}
//
//		private List<String> formatPostManRequestAndOrder(List<SpiMethod> spiMethods,
//				VersionedRestApp restApp) {
//				List<String> order = new ArrayList<> ();
//				for (SpiMethod spiMethod : spiMethods) {
//						order.add (EncryptProvider.md5Hex (spiMethod.getIdentity (), true));
//						postManRequests.add (initPostManRequest (spiMethod, restApp));
//				}
//				return order;
//		}
//
//		private List<String> formatPostManRequestAndOrder(SpiMethod spiMethod,
//				VersionedRestApp restApp) {
//				List<String> order = new ArrayList<> ();
//				order.add (EncryptProvider.md5Hex (spiMethod.getIdentity (), true));
//				postManRequests.add (initPostManRequest (spiMethod, restApp));
//				return order;
//		}
//
//		private PostManRequest initPostManRequest(SpiMethod spiMethod,
//				VersionedRestApp restApp) {
//				PostManRequest postManRequest = new PostManRequest ();
//				postManRequest.setId (EncryptProvider.md5Hex (spiMethod.getIdentity (), true));
//				postManRequest.setName (spiMethod.getSummary ());
//				postManRequest.setDescription (spiMethod.getDescription ());
//				postManRequest.setFolder (EncryptProvider.md5Hex (restApp.getOriginal ()
//																																 .getApp (), true));
//				postManRequest.setMethod (spiMethod.getMapping ()
//																					 .getMethod ()
//																					 .getName ());
//				postManRequest.setPathVariables (getPathParams (spiMethod));
//				postManRequest.setHeaders (getHeaders (spiMethod));
//				postManRequest.setUrl (getUrl (restApp, postManRequest, spiMethod));
//				postManRequest.setCollectionId (EncryptProvider.md5Hex (restApp.getOriginal ()
//																																			 .getApp (), true));
//				return postManRequest;
//		}
//
//		/**
//		 * 设置post 请求的头部参数，目前暂时支持application/json、application/x-www-form-urlencoded
//		 *
//		 * @param spiMethod
//		 * @return
//		 */
//		private String getHeaders(SpiMethod spiMethod) {
//				String methodStr = spiMethod.getMapping ()
//																		.getMethod ()
//																		.getName ()
//																		.toUpperCase ();
//				StringBuilder sb = new StringBuilder ();
//				if (methodStr.equals ("GET") || methodStr.equals ("DELETE")) {
//						sb.append ("Content-Type: application/json");
//				}
//				else {
//						sb.append ("Content-Type:application/x-www-form-urlencoded");
//				}
//				List<RequestHeader> requestHeaders = spiMethod.getRequestHeaders ();
//				if (!spiMethod.isLoginNeedless ()) {
//						// 是否使用占位符
//						sb.append ("\n")
//							.append ("X-Token:")
//							.append (this.isUsePlaceholder () ? getPlaceholderVariable ("X-Token") : "");
//				}
//				for (RequestHeader requestHeader : requestHeaders) {
//						if (ignoredHeaders.contains (requestHeader.getName ()
//																											.toLowerCase ())) {
//								continue;
//						}
//						String defaultValue = requestHeader.getDefaultValue ();
//						sb.append ("\n")
//							.append (requestHeader.getName ())
//							.append (":")
//							.append (this.isUsePlaceholder () ?
//									getPlaceholderVariable (requestHeader.getName ()) :
//									(StringUtils.hasText (defaultValue) ? defaultValue : ""));
//				}
//				return sb.toString ();
//		}
//
//		private String getPlaceholderVariable(String variableName) {
//				return this.leftPlaceholder + variableName + this.rightPlaceholder;
//		}
//
//		/**
//		 * 获取请求的url
//		 *
//		 * @param restApp   当前的服务名称
//		 * @param request   postman 请求的参数
//		 * @param spiMethod 微服务中方法
//		 * @return
//		 */
//		@SuppressWarnings("unchecked")
//		private String getUrl(VersionedRestApp restApp,
//				PostManRequest request,
//				SpiMethod spiMethod) {
//				String url = StringUtils.hasText (this.host) ? this.host : "http://api.route.dooioo.org";
//				if (restApp.getOriginal ()
//									 .isIgnoreVirtualPath () || this.ignoreVirtualPath) {
//						url += spiMethod.getMapping ()
//														.getPath ();
//				}
//				else {
//						url += "/" + restApp.getOriginal ()
//																.getApp ()
//																.replace ("-", "/") + spiMethod.getMapping ()
//																															 .getPath ();
//				}
//				String methodStr = spiMethod.getMapping ()
//																		.getMethod ()
//																		.getName ()
//																		.toUpperCase ();
//				Map<String, Object> requestParamMap = getRequestParams (spiMethod);
//				if (methodStr.equals ("GET") || methodStr.equals ("DELETE")) {
//						request.setDataMode ("params");
//						if (requestParamMap != null) {
//								url += requestParamMap.get ("get");
//						}
//				}
//				else {
//						request.setDataMode ("urlencoded");
//						if (requestParamMap != null) {
//								request.setData ((List<PostManRequest.PostData>) requestParamMap.get ("post"));
//						}
//
//				}
//				if (usePlaceholder) {
//						return url.replace ("{", this.leftPlaceholder)
//											.replace ("}", this.rightPlaceholder);
//				}
//				else {
//						return url;
//				}
//		}
//
//		/**
//		 * 获取url 中的路径参数
//		 *
//		 * @param spiMethod
//		 * @return
//		 */
//		private static Object getPathParams(SpiMethod spiMethod) {
//				List<PathParam> pathParams = spiMethod.getPathParams ();
//				Map<String, String> pathParamMap = new HashMap<> ();
//				for (PathParam pathParam : pathParams) {
//						pathParamMap.put (pathParam.getName (), pathParam.getComment ());
//				}
//				return pathParamMap;
//		}
//
//		/**
//		 * 获取请求的参数，暂时支持post表单，和get url params 模式
//		 *
//		 * @param original
//		 * @return
//		 */
//		private Map<String, Object> getRequestParams(SpiMethod original) {
//				StringBuilder getSB = new StringBuilder ("?");
//				Map<String, Object> queryMap = new HashMap<> ();
//				List<PostManRequest.PostData> postDatas = new ArrayList<> ();
//				List<QueryParam> queryParams = original.getQueryParams ();
//				if (queryParams == null || queryParams.size () == 0) {
//						return null;
//				}
//				for (QueryParam queryParam : original.getQueryParams ()) {
//						if (this.usePlaceholder) {
//								getSB.append (queryParam.getName ())
//										 .append ("=")
//										 .append ("{")
//										 .append (queryParam.getName ())
//										 .append ("}")
//										 .append ("&");
//						}
//						else {
//								getSB.append (queryParam.getName ())
//										 .append ("=")
//										 .append (StringUtils.hasText (queryParam.getDefaultValue ()) ? queryParam.getDefaultValue () : "")
//										 .append ("&");
//						}
//
//						PostManRequest.PostData postData = new PostManRequest.PostData ();
//						postData.setKey (queryParam.getName ());
//						if (usePlaceholder) {
//								postData.setValue (getPlaceholderVariable (queryParam.getName ()));
//						}
//						else {
//								postData.setValue (StringUtils.hasText (queryParam.getDefaultValue ()) ? queryParam.getDefaultValue () : "");
//						}
//						postDatas.add (postData);
//				}
//				queryMap.put ("get", getSB.substring (0, getSB.lastIndexOf ("&")));
//				queryMap.put ("post", postDatas);
//				return queryMap;
//		}
//
//}
