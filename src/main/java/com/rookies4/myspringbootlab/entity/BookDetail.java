package com.rookies4.myspringbootlab.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_detail_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "language")
    private String language;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "edition")
    private String edition;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true)  // 주인(FK)
    private Book book;

    /** 대칭 헬퍼(선택): 반대편도 함께 맞춰줌 */
    public void setBook(Book book) {
        this.book = book;
        if (book != null && book.getBookDetail() != this) {
            book.setBookDetail(this);
        }
    }
}
