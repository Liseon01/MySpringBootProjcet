package com.rookies4.myspringbootlab.repository;

import com.rookies4.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {

    Optional<BookDetail> findByBookId(Long bookId);

    @Query("select d from BookDetail d join fetch d.book where d.id = :id")
    Optional<BookDetail> findByIdWithBook(Long id);

    List<BookDetail> findByPublisherContainingIgnoreCase(String publisher);
}
