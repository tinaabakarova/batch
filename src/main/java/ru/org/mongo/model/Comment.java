package ru.org.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Comment {
    @Id
    private String id;
    private String comment;
    private Book book;
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
        return id.equals(comment1.id) && comment.equals(comment1.comment) && book.equals(comment1.book) && userName.equals(comment1.userName);
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
