package com.rookies4.myspringbootlab.repository;

import com.rookies4.myspringbootlab.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book sample1() {
        return new Book(
                "스프링 부트 입문",
                "홍길동",
                "9788956746425",
                30000,
                LocalDate.of(2025, 5, 7)
        );
    }

    private Book sample2() {
        return new Book(
                "JPA 프로그래밍",
                "박둘리",
                "9788956746432",
                35000,
                LocalDate.of(2025, 4, 30)
        );
    }

    @Test
    @DisplayName("도서 등록 테스트 - testCreateBook()")
    void testCreateBook() {
        Book saved = bookRepository.save(sample1());
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIsbn()).isEqualTo("9788956746425");
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 테스트 - testFindByIsbn()")
    void testFindByIsbn() {
        bookRepository.save(sample1());
        bookRepository.save(sample2());

        Optional<Book> found = bookRepository.findByIsbn("9788956746425");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("스프링 부트 입문");
    }

    @Test
    @DisplayName("저자명으로 도서 목록 조회 테스트 - testFindByAuthor()")
    void testFindByAuthor() {
        bookRepository.save(sample1());
        bookRepository.save(sample2());

        List<Book> list = bookRepository.findByAuthor("박둘리");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getIsbn()).isEqualTo("9788956746432");
    }

    @Test
    @DisplayName("도서 정보 수정 테스트 - testUpdateBook()")
    void testUpdateBook() {
        Book saved = bookRepository.save(sample2());
        saved.setPrice(39000);
        saved.setTitle("JPA 프로그래밍 (개정판)");

        Book updated = bookRepository.save(saved);

        assertThat(updated.getPrice()).isEqualTo(39000);
        assertThat(updated.getTitle()).contains("개정판");
    }

    @Test
    @DisplayName("도서 삭제 테스트 - testDeleteBook()")
    void testDeleteBook() {
        Book saved = bookRepository.save(sample1());
        Long id = saved.getId();

        bookRepository.deleteById(id);

        assertThat(bookRepository.findById(id)).isNotPresent();
    }
}
