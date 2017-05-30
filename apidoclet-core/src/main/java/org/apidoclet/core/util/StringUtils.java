package org.apidoclet.core.util;

/**
 * @author huisman
 * @version 1.0.0
 * @since 2016年1月15日 Copyright (c) 2016, BookDao All Rights Reserved.
 */
public final class StringUtils {
  private StringUtils() {
    super();
    throw new UnsupportedOperationException("can't instantiate util class");
  }

  /**
   * 仅比较字符串是否为null 或者长度为0，没做trim处理
   * 
   * @author huisman
   * @param input
   * @since 2016年1月15日
   */
  public static boolean isNullOrEmpty(CharSequence input) {
    return input == null || input.length() == 0;
  }

  /**
   * null return ""
   */
  public static String trim(String input) {
    if (input == null) {
      return "";
    }
    return input.trim();
  }
}
