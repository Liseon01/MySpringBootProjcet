package com.rookies4.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_details")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA requires a no-arg constructor
@AllArgsConstructor
@Builder
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String description;

    private String language;

    private Integer pageCount;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    private String edition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true)
    private Book book;
}
