package com.rookies4.myspringbootlab.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    private Integer price;

    private LocalDate publishDate;

    @JsonManagedReference
    @OneToOne(mappedBy = "book",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE)      // Lombok 자동 setter 막기 (아래 커스텀 setter 사용)
    private BookDetail bookDetail;

    /** 양방향 고정: Book ↔ BookDetail 동시 세팅 */
    public void setBookDetail(BookDetail detail) {
        this.bookDetail = detail;
        if (detail != null && detail.getBook() != this) {
            detail.setBook(this);   // FK(book_id) 채움
        }
    }
}
