package org.example.model;

import org.example.annotation.Property;

/**
 * @author anhnsq@viettel.com.vn
 */
public class Author {
  @Property("author_name")
  private String name;
  @Property("book_nums")
  private int numberOfBooks;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getNumberOfBooks() {
    return numberOfBooks;
  }

  public void setNumberOfBooks(int numberOfBooks) {
    this.numberOfBooks = numberOfBooks;
  }

  @Override
  public String toString() {
    return "Author{" +
      "name='" + name + '\'' +
      ", numberOfBooks=" + numberOfBooks +
      '}';
  }
}
