package ru.org.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.org.mongo.model.Book;

public interface BookDaoMongo extends MongoRepository<Book, String> {
    public Book findByName(String name);
}
