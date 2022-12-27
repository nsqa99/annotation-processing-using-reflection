package org.example.service;

import org.example.annotation.Bean;
import org.example.annotation.Inject;
import org.example.model.Book;
import org.example.utils.NSQAJson;

/**
 * @author anhnsq@viettel.com.vn
 */
@Bean
public class SerializationService {
  @Inject
  private NSQAJson nsqaJson;

  public String serializeBook(Book book) {
    return nsqaJson.toJson(book);
  }
}
