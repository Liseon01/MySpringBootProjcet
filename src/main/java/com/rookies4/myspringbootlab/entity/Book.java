package com.rookies4.myspringbootlab.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(nullable = false, unique = true, length = 30)
    private String isbn;

    private Integer price;

    private LocalDate publishDate;

    /** NEW: Many-to-One to Publisher */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    /** EXISTING: One-to-One to BookDetail (owning side is BookDetail via book_id) */
    @JsonManagedReference
    @OneToOne(mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE) // use custom setter below to keep both sides in sync
    private BookDetail bookDetail;

    /** Keep bidirectional link Book <-> BookDetail consistent */
    public void setBookDetail(BookDetail detail) {
        this.bookDetail = detail;
        if (detail != null && detail.getBook() != this) {
            detail.setBook(this);
        }
    }
}
