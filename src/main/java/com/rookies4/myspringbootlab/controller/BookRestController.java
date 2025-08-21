package com.rookies4.myspringbootlab.controller;

import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.exception.BusinessException;
import com.rookies4.myspringbootlab.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books/dd")
public class BookRestController {

    private final BookRepository bookRepository;

    public BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = bookRepository.save(book);
        return ResponseEntity.created(URI.create("/api/books/" + saved.getId()))
                .body(saved);
    }

    @GetMapping
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getUserById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping({"/isbn/{isbn}", "/isbn/{isbn}/"})
    public Book getUserByIsbn(@PathVariable String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "Book not found by ISBN: " + isbn));
    }

    @GetMapping("/author/{author}")
    public List<Book> findByAuthor(@PathVariable String author) {
        return bookRepository.findByAuthor(author);
    }


    @PutMapping("/{id}")
    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetail) {
        Book existBook = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Book Not Found"));
        existBook.setPrice(bookDetail.getPrice());

        Book updatedBook = bookRepository.save(existBook);
        return ResponseEntity.ok(updatedBook);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // ResponseEntity<Void>
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204, ResponseEntity<Void>
    }
}
