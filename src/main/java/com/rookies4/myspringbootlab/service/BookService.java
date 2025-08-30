package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import com.rookies4.myspringbootlab.entity.Publisher;
import com.rookies4.myspringbootlab.exception.BusinessException;
import com.rookies4.myspringbootlab.exception.ErrorCode;
import com.rookies4.myspringbootlab.repository.BookRepository;
import com.rookies4.myspringbootlab.repository.PublisherRepository;
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
    private final PublisherRepository publisherRepository;

    /** 모든 도서를 조회하며, 각 도서의 출판사 정보에 '해당 출판사의 도서 수'를 포함 */
    public List<BookDTO.Response> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::toResponseWithPublisherCount)
                .toList();
    }

    /** ID로 도서를 조회하며, 모든 관련 정보(출판사, 상세정보)를 포함 */
    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithAllDetails(id)
                .orElseGet(() -> bookRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                                ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Book", "id", id)
                        )));
        return toResponseWithPublisherCount(book);
    }

    /** ISBN으로 도서를 조회 */
    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseGet(() -> bookRepository.findByIsbn(isbn)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                                ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Book", "isbn", isbn)
                        )));
        return toResponseWithPublisherCount(book);
    }

    /** 작가명으로 도서를 검색 */
    public List<BookDTO.Response> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::toResponseWithPublisherCount)
                .toList();
    }

    /** 제목으로 도서를 검색 */
    public List<BookDTO.Response> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toResponseWithPublisherCount)
                .toList();
    }

    /** 특정 출판사의 모든 도서를 조회 */
    public List<BookDTO.Response> getBooksByPublisherId(Long publisherId) {
        // 출판사 존재 검증(404)
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", publisherId)
                ));

        Long count = bookRepository.countByPublisherId(publisherId);
        List<Book> books = bookRepository.findByPublisherId(publisherId);

        // 동일 출판사이므로 count를 한 번만 계산해서 주입
        return books.stream()
                .map(b -> {
                    BookDTO.Response resp = BookDTO.Response.fromEntity(b);
                    PublisherDTO.SimpleResponse pub =
                            PublisherDTO.SimpleResponse.fromEntityWithCount(publisher, count == null ? 0L : count);
                    resp.setPublisher(pub);
                    return resp;
                })
                .toList();
    }

    /** 도서 생성: 출판사 존재 여부와 ISBN 중복 검증 */
    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request) {
        // ISBN 중복(409)
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            // 전용 코드가 있으면 그걸 사용
            if (hasIsbnDuplicateCode()) {
                throw new BusinessException(
                        ErrorCode.ISBN_DUPLICATE.getHttpStatus(),
                        ErrorCode.ISBN_DUPLICATE.formatMessage(request.getIsbn())
                );
            }
            // 없으면 일반 중복 코드 템플릿 사용
            throw new BusinessException(
                    ErrorCode.RESOURCE_DUPLICATE.getHttpStatus(),
                    ErrorCode.RESOURCE_DUPLICATE.formatMessage("Book", "isbn", request.getIsbn())
            );
        }

        // 출판사 존재(404)
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", request.getPublisherId())
                ));

        // Book + BookDetail 구성
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publishDate(request.getPublishDate())
                .publisher(publisher)
                .build();

        if (request.getDetailRequest() != null) {
            BookDetail d = BookDetail.builder()
                    .description(request.getDetailRequest().getDescription())
                    .language(request.getDetailRequest().getLanguage())
                    .pageCount(request.getDetailRequest().getPageCount())
                    .publisher(request.getDetailRequest().getPublisher())
                    .coverImageUrl(request.getDetailRequest().getCoverImageUrl())
                    .edition(request.getDetailRequest().getEdition())
                    .build();
            book.setBookDetail(d); // 양방향 동기화
        }

        Book saved = bookRepository.save(book);
        return toResponseWithPublisherCount(saved);
    }

    /** 도서 수정: 출판사/ISBN 유효성 검증 */
    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request) {
        Book book = bookRepository.findByIdWithAllDetails(id)
                .orElseGet(() -> bookRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                                ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Book", "id", id)
                        )));

        // ISBN 변경 시 중복 체크
        if (request.getIsbn() != null && !request.getIsbn().equals(book.getIsbn())) {
            bookRepository.findByIsbn(request.getIsbn()).ifPresent(dup -> {
                if (!dup.getId().equals(book.getId())) {
                    if (hasIsbnDuplicateCode()) {
                        throw new BusinessException(
                                ErrorCode.ISBN_DUPLICATE.getHttpStatus(),
                                ErrorCode.ISBN_DUPLICATE.formatMessage(request.getIsbn())
                        );
                    }
                    throw new BusinessException(
                            ErrorCode.RESOURCE_DUPLICATE.getHttpStatus(),
                            ErrorCode.RESOURCE_DUPLICATE.formatMessage("Book", "isbn", request.getIsbn())
                    );
                }
            });
            book.setIsbn(request.getIsbn());
        }

        // 출판사 검증 및 설정
        if (request.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                            ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", request.getPublisherId())
                    ));
            book.setPublisher(publisher);
        }

        // 기타 필드 업데이트(널이면 유지)
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getPrice() != null) book.setPrice(request.getPrice());
        if (request.getPublishDate() != null) book.setPublishDate(request.getPublishDate());

        // 상세 업데이트
        if (request.getDetailRequest() != null) {
            if (book.getBookDetail() == null) {
                BookDetail d = BookDetail.builder()
                        .description(request.getDetailRequest().getDescription())
                        .language(request.getDetailRequest().getLanguage())
                        .pageCount(request.getDetailRequest().getPageCount())
                        .publisher(request.getDetailRequest().getPublisher())
                        .coverImageUrl(request.getDetailRequest().getCoverImageUrl())
                        .edition(request.getDetailRequest().getEdition())
                        .build();
                book.setBookDetail(d);
            } else {
                BookDetail d = book.getBookDetail();
                if (request.getDetailRequest().getDescription() != null) d.setDescription(request.getDetailRequest().getDescription());
                if (request.getDetailRequest().getLanguage() != null) d.setLanguage(request.getDetailRequest().getLanguage());
                if (request.getDetailRequest().getPageCount() != null) d.setPageCount(request.getDetailRequest().getPageCount());
                if (request.getDetailRequest().getPublisher() != null) d.setPublisher(request.getDetailRequest().getPublisher());
                if (request.getDetailRequest().getCoverImageUrl() != null) d.setCoverImageUrl(request.getDetailRequest().getCoverImageUrl());
                if (request.getDetailRequest().getEdition() != null) d.setEdition(request.getDetailRequest().getEdition());
            }
        }

        return toResponseWithPublisherCount(book);
    }

    /** 도서 삭제 (BookDetail은 cascade로 함께 삭제) */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Book", "id", id)
                ));
        bookRepository.delete(book);
    }

    // ===== 내부 유틸 =====

    /** Book → DTO.Response 매핑 + 출판사 도서 수 세팅 */
    private BookDTO.Response toResponseWithPublisherCount(Book book) {
        BookDTO.Response resp = BookDTO.Response.fromEntity(book);
        Publisher publisher = book.getPublisher();
        if (publisher != null) {
            Long cnt = bookRepository.countByPublisherId(publisher.getId());
            PublisherDTO.SimpleResponse pub =
                    PublisherDTO.SimpleResponse.fromEntityWithCount(publisher, cnt == null ? 0L : cnt);
            resp.setPublisher(pub);
        }
        return resp;
    }

    /** ISBN_DUPLICATE 코드가 존재하는지(템플릿 안정성) */
    private boolean hasIsbnDuplicateCode() {
        try {
            return ErrorCode.valueOf("ISBN_DUPLICATE") != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
