package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.exception.BusinessException;
import com.rookies4.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO.BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream().map(BookDTO.BookResponse::from).toList();
    }

    public BookDTO.BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Book not found by ID: " + id));
        return BookDTO.BookResponse.from(book);
    }

    public BookDTO.BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,"Book not found by ISBN: " + isbn));
        return BookDTO.BookResponse.from(book);
    }

    public List<BookDTO.BookResponse> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author)
                .stream().map(BookDTO.BookResponse::from).toList();
    }

    @Transactional
    public BookDTO.BookResponse createBook(BookDTO.BookCreateRequest request) {
        Book saved = bookRepository.save(request.toEntity());
        return BookDTO.BookResponse.from(saved);
    }

    @Transactional
    public BookDTO.BookResponse updateBook(Long id, BookDTO.BookUpdateRequest request) {
        Book exist = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,"Book not found by ID: " + id));

        if (request.getTitle() != null)       exist.setTitle(request.getTitle());
        if (request.getAuthor() != null)      exist.setAuthor(request.getAuthor());
        if (request.getPrice() != null)       exist.setPrice(request.getPrice());
        if (request.getPublishDate() != null) exist.setPublishDate(request.getPublishDate());

        Book updated = bookRepository.save(exist);
        return BookDTO.BookResponse.from(updated);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book exist = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,"Book not found by ID: " + id));
        bookRepository.delete(exist);
    }
}
