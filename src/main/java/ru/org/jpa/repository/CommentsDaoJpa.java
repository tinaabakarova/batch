package ru.org.jpa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.org.jpa.model.Comment;

public interface CommentsDaoJpa extends PagingAndSortingRepository<Comment, Long> {
     public Iterable<Comment> findAllByBookId(Long id);
}
