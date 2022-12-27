package org.example;

import org.example.core.BeanContainer;
import org.example.model.Author;
import org.example.model.Book;
import org.example.service.SerializationService;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Book book = new Book();
        Author author = new Author();
        author.setName("Piers Morgan");
        author.setNumberOfBooks(-1);
        book.setName("Cristiano Ronaldo");
        book.setAuthor(author);
        book.setCategory("Comedy");
//        NSQAJson nsqaJson = new NSQAJson();
//        System.out.println(nsqaJson.toJson(book));

        BeanContainer container = new BeanContainer();
        Map<Class<?>, Object> mapBeanToInstances = container.processBeans(Main.class);
        SerializationService serializeService = (SerializationService) mapBeanToInstances.get(SerializationService.class);

        System.out.println(serializeService.serializeBook(book));
    }
}