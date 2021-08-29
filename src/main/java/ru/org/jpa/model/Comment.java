package ru.org.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMENTS")
@NamedEntityGraph(name = "otus-comments-entity-graph",
        attributeNodes = {@NamedAttributeNode("book")})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "COMMENT", nullable = false)
    private String comment;
    @BatchSize(size = 5)
    @ManyToOne(targetEntity = Book.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "BOOK", nullable = false)
    private Book book;
    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    public Comment(String comment, Book book, String userName) {
        this.comment = comment;
        this.book = book;
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment1 = (Comment) o;
        return id == comment1.id && comment.equals(comment1.comment) && book.equals(comment1.book) && userName.equals(comment1.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, book, userName);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", book=" + book +
                ", userName='" + userName + '\'' +
                '}';
    }
}
