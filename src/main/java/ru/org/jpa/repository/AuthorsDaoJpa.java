package ru.org.jpa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.org.jpa.model.Author;

public interface AuthorsDaoJpa extends PagingAndSortingRepository<Author, Long> {

    public Author findByName(String name);
}
