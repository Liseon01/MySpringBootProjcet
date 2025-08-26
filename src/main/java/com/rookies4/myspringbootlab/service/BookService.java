package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import com.rookies4.myspringbootlab.exception.BusinessException;
import com.rookies4.myspringbootlab.repository.BookDetailRepository;
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
    private final BookDetailRepository bookDetailRepository;

    public List<BookDTO.BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(BookDTO.BookResponse::from).toList();
    }

    public BookDTO.BookResponse getBookById(Long id) {
        Book b = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,"Book not found by ID: " + id));
        return BookDTO.BookResponse.from(b);
    }

    public BookDTO.BookResponse getBookByIsbn(String isbn) {
        Book b = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Book not found by ISBN: " + isbn));
        return BookDTO.BookResponse.from(b);
    }

    public List<BookDTO.BookResponse> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(BookDTO.BookResponse::from).toList();
    }

    public List<BookDTO.BookResponse> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookDTO.BookResponse::from).toList();
    }

    @Transactional
    public BookDTO.BookResponse createBook(BookDTO.BookCreateRequest req) {
        if (bookRepository.existsByIsbn(req.getIsbn())) {
            throw new BusinessException(HttpStatus.CONFLICT, "ISBN already exists: " + req.getIsbn());
        }
        Book saved = bookRepository.save(req.toEntity()); // detail도 cascade로 저장
        return BookDTO.BookResponse.from(saved);
    }

    @Transactional
    public BookDTO.BookResponse updateBook(Long id, BookDTO.BookUpdateRequest req) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Book not found by ID: " + id));

        // ISBN 변경 시 중복 체크
        if (!book.getIsbn().equals(req.getIsbn()) && bookRepository.existsByIsbn(req.getIsbn())) {
            throw new BusinessException(HttpStatus.CONFLICT ,"ISBN already exists: " + req.getIsbn());
        }

        // 전체 수정(put)
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setPrice(req.getPrice());
        book.setPublishDate(req.getPublishDate());

        if (book.getDetail() == null) {
            book.setDetail(new BookDetail());
        }
        req.getDetailRequest().applyTo(book.getDetail());

        Book updated = bookRepository.save(book);
        return BookDTO.BookResponse.from(updated);
    }

    @Transactional
    public BookDTO.BookResponse patchBook(Long id, BookDTO.PatchRequest req) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Book not found by ID: " + id));

        if (req.getTitle() != null)        book.setTitle(req.getTitle());
        if (req.getAuthor() != null)       book.setAuthor(req.getAuthor());
        if (req.getPrice() != null)        book.setPrice(req.getPrice());
        if (req.getPublishDate() != null)  book.setPublishDate(req.getPublishDate());
        if (req.getIsbn() != null) {
            if (!book.getIsbn().equals(req.getIsbn()) && bookRepository.existsByIsbn(req.getIsbn())) {
                throw new BusinessException(HttpStatus.CONFLICT, "ISBN already exists: " + req.getIsbn());
            }
            book.setIsbn(req.getIsbn());
        }

        if (req.getDetailRequest() != null) {
            if (book.getDetail() == null) book.setDetail(new BookDetail());
            var d = req.getDetailRequest();
            if (d.getDescription() != null)  book.getDetail().setDescription(d.getDescription());
            if (d.getLanguage() != null)     book.getDetail().setLanguage(d.getLanguage());
            if (d.getPageCount() != null)    book.getDetail().setPageCount(d.getPageCount());
            if (d.getPublisher() != null)    book.getDetail().setPublisher(d.getPublisher());
            if (d.getCoverImageUrl() != null)book.getDetail().setCoverImageUrl(d.getCoverImageUrl());
            if (d.getEdition() != null)      book.getDetail().setEdition(d.getEdition());
        }

        return BookDTO.BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookDTO.BookResponse patchBookDetail(Long id, BookDTO.BookDetailPatchRequest req) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException( HttpStatus.NOT_FOUND, "Book not found by ID: " + id));

        if (book.getDetail() == null) book.setDetail(new BookDetail());
        BookDetail detail = book.getDetail();

        if (req.getDescription() != null)   detail.setDescription(req.getDescription());
        if (req.getLanguage() != null)      detail.setLanguage(req.getLanguage());
        if (req.getPageCount() != null)     detail.setPageCount(req.getPageCount());
        if (req.getPublisher() != null)     detail.setPublisher(req.getPublisher());
        if (req.getCoverImageUrl() != null) detail.setCoverImageUrl(req.getCoverImageUrl());
        if (req.getEdition() != null)       detail.setEdition(req.getEdition());

        return BookDTO.BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book exist = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException( HttpStatus.NOT_FOUND, "Book not found by ID: " + id));
        bookRepository.delete(exist); // detail도 orphanRemoval=true 로 함께 삭제
    }
}
