package ru.org.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.org.mongo.model.Genre;

public interface GenreDaoMongo extends MongoRepository<Genre, String>{
    public Genre findByName(String name);
}
