package ru.org.jpa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.org.jpa.model.Book;

public interface BooksDaoJpa extends PagingAndSortingRepository<Book, Long> {
    public Book findByName(String name);
}
