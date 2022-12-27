package org.example.model;

import org.example.annotation.Ignore;
import org.example.annotation.Property;

/**
 * @author anhnsq@viettel.com.vn
 */
public class Book {
  @Property(
    value = "book_name",
    required = true)
  private String name;
  @Property(value = "author")
  private Author author;
  @Ignore
  private String category;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return "Book{" +
      "name='" + name + '\'' +
      ", author='" + author + '\'' +
      ", category='" + category + '\'' +
      '}';
  }
}
