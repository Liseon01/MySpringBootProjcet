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

    // GET /api/books
    @GetMapping
    public ResponseEntity<List<BookDTO.BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // GET /api/books/isbn/{isbn}
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.BookResponse> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    // GET /api/books/search/author?author=Robert
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.BookResponse>> getBooksByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(author));
    }

    // GET /api/books/search/title?title=Clean
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.BookResponse>> getBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<BookDTO.BookResponse> createBook(@RequestBody @Valid BookDTO.BookCreateRequest request) {
        var saved = bookService.createBook(request);
        return ResponseEntity.created(URI.create("/api/books/" + saved.getId())).body(saved);
    }

    // PUT /api/books/{id} (전체 수정)
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.BookResponse> updateBook(@PathVariable Long id,
                                                           @RequestBody @Valid BookDTO.BookUpdateRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    // PATCH /api/books/{id} (부분 수정)
    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO.BookResponse> patchBook(@PathVariable Long id,
                                                          @RequestBody @Valid BookDTO.PatchRequest request) {
        return ResponseEntity.ok(bookService.patchBook(id, request));
    }

    // PATCH /api/books/{id}/detail (상세만 부분 수정)
    @PatchMapping("/{id}/detail")
    public ResponseEntity<BookDTO.BookResponse> patchBookDetail(@PathVariable Long id,
                                                                @RequestBody @Valid BookDTO.BookDetailPatchRequest request) {
        return ResponseEntity.ok(bookService.patchBookDetail(id, request));
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
