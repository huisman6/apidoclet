package org.apidoclet.model.util;

/**
 * 我们支持的javadoc tag以及自定义的tag
 */
public interface StandardDocTag {
  /*********** Standard Java Doc Tag ********************/
  String TAG_VERSION = "version";
  String TAG_AUTHOR = "author";
  String TAG_PARAM = "param";
  String TAG_SINCE = "since";
  String TAG_DEPREACTED = "deprecated";

  /**************** ApiDoclet Doc Tag *********************/
  /**
   * one concise sentence  to cover class or method's purpose
   */
  String TAG_SUMMARY = "summary";
  /**
   * endpoint's customized return type
   */
  String TAG_RETURN_TYPE = "returnType";
  /**
   * business-related  code
   */
  String TAG_BIZ_CODES = "bizCodes";
}
