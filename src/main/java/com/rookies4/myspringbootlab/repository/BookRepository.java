package com.rookies4.myspringbootlab.repository;

import com.rookies4.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 기본 조회
    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    // BookDetail 즉시 로딩
    @Query("SELECT b FROM Book b JOIN FETCH b.bookDetail WHERE b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id);

    @Query("SELECT b FROM Book b JOIN FETCH b.bookDetail WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn);

    // 존재 여부
    boolean existsByIsbn(String isbn);

    // Publisher 관련
    List<Book> findByPublisherId(Long publisherId);

    Long countByPublisherId(@Param("publisherId") Long publisherId);

    // BookDetail + Publisher 모두 즉시 로딩
    @Query("SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.bookDetail " +
            "LEFT JOIN FETCH b.publisher " +
            "WHERE b.id = :id")
    Optional<Book> findByIdWithAllDetails(@Param("id") Long id);
}
