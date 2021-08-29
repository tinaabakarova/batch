package ru.org.jpa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.org.jpa.model.Genre;

public interface GenresDaoJpa extends PagingAndSortingRepository<Genre, Long> {

    public Genre findByName(String name);
}
