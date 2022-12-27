package org.example.utils;

/**
 * @author anhnsq@viettel.com.vn
 */
public class StringUtils {
  private StringUtils() {}

  public static String presentOrNull(String value) {
    if (value == null || value.isBlank()) return null;

    return value;
  }

  public static String pascalCaseToCamelCase(String valueInPC) {
    return valueInPC.substring(0, 1).toLowerCase() + valueInPC.substring(1);
  }
}
