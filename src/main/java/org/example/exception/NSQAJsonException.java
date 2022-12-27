package org.example.exception;

/**
 * @author anhnsq@viettel.com.vn
 */
public class NSQAJsonException extends RuntimeException {
  public NSQAJsonException(String message) {
    super(message);
  }

  public NSQAJsonException(String message, Throwable cause) {
    super(message, cause);
  }
}
