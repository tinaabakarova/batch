package ru.org.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.org.mongo.model.Author;

public interface AuthorDaoMongo extends MongoRepository<Author, String> {
    public Author findByName(String name);
}
