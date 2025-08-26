package com.rookies4.myspringbootlab.repository;

import com.rookies4.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Spring Data JPA repository bound to Book entity. */
public interface BookRepository extends JpaRepository<Book, Long> {

    // === 기존 메서드 유지 ===
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAuthor(String author);

    // === 2-4 과제용 추가 ===

    // ISBN 중복 체크
    boolean existsByIsbn(String isbn);

    // 검색(부분 일치, 대소문자 무시) – 서비스가 contains 호출해도 컴파일되도록 추가
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Book + BookDetail 함께 로딩 (1:1 fetch join)
    @Query("select b from Book b left join fetch b.detail where b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id);

    @Query("select b from Book b left join fetch b.detail where b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn);
}
