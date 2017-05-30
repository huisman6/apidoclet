package org.apidoclet.server.helper;

import java.nio.charset.Charset;
import java.util.zip.CRC32;

/**
 * @summary Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public final class DigestUtils {
  private DigestUtils() {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * null or empty ,return 0;
   */
  public static long crc32(String input) {
    if (input == null || input.isEmpty()) {
      return 0;
    }
    CRC32 crc32 = new CRC32();
    crc32.update(input.getBytes(Charset.forName("utf-8")));
    return crc32.getValue();
  }
}
