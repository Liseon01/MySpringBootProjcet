package com.rookies4.myspringbootlab.controller;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /** GET /api/books : 모든 도서 */
    @GetMapping
    public ResponseEntity<List<BookDTO.Response>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /** GET /api/books/{id} : ID로 조회(출판사/상세 포함) */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.Response> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /** GET /api/books/isbn/{isbn} : ISBN으로 조회 */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.Response> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    /** GET /api/books/search/author?author=xxx : 작가로 검색 */
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.Response>> getBooksByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    /** GET /api/books/search/title?title=xxx : 제목으로 검색 */
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.Response>> getBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    /** POST /api/books : 도서 생성 */
    @PostMapping
    public ResponseEntity<BookDTO.Response> createBook(@RequestBody @Valid BookDTO.Request request) {
        BookDTO.Response saved = bookService.createBook(request);
        return ResponseEntity.created(URI.create("/api/books/" + saved.getId())).body(saved);
    }

    /** PUT /api/books/{id} : 도서 수정(전체) */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.Response> updateBook(@PathVariable Long id,
                                                       @RequestBody @Valid BookDTO.Request request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /** DELETE /api/books/{id} : 도서 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
