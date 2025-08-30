package com.rookies4.myspringbootlab.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publishers", indexes = {
        @Index(name = "uk_publisher_name", columnList = "name", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Publisher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    private LocalDate establishedDate;

    @Column(length = 200)
    private String address;

    /**
     * Do NOT cascade REMOVE: spec says "reject delete if books exist".
     * Keep PERSIST/MERGE for convenience.
     */
    @OneToMany(mappedBy = "publisher",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            orphanRemoval = false)
    @JsonIgnore // prevent infinite JSON loop when entities leak to responses
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    // --- helpers to keep bidirectional consistency ---
    public void addBook(Book book) {
        if (book == null) return;
        books.add(book);
        book.setPublisher(this);
    }

    public void removeBook(Book book) {
        if (book == null) return;
        books.remove(book);
        if (book.getPublisher() == this) {
            book.setPublisher(null);
        }
    }
}
