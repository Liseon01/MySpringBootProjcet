package com.rookies4.myspringbootlab.controller.dto;

import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class BookDTO {

    // ---------- Request DTOs ----------
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookDetailRequest {
        private String description;
        private String language;
        @Positive(message = "페이지 수는 양수여야 합니다.") private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;

        public BookDetail toEntity() {
            BookDetail d = new BookDetail();
            d.setDescription(description);
            d.setLanguage(language);
            d.setPageCount(pageCount);
            d.setPublisher(publisher);
            d.setCoverImageUrl(coverImageUrl);
            d.setEdition(edition);
            return d;
        }

        public void applyTo(BookDetail target) {
            target.setDescription(description);
            target.setLanguage(language);
            target.setPageCount(pageCount);
            target.setPublisher(publisher);
            target.setCoverImageUrl(coverImageUrl);
            target.setEdition(edition);
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookCreateRequest {
        @NotBlank private String title;
        @NotBlank private String author;

        // 간단 패턴 예시(숫자/대시/문자 최대 20)
        @Pattern(regexp = "^[0-9A-Za-z-]{5,20}$",
                message = "ISBN 형식이 올바르지 않습니다.")
        private String isbn;

        @Positive @NotNull private Integer price;

        @NotNull @PastOrPresent(message = "출간일은 미래일 수 없습니다.")
        private LocalDate publishDate;

        @NotNull(message = "상세 정보는 필수입니다.")
        private BookDetailRequest detailRequest;

        public Book toEntity() {
            Book b = new Book();
            b.setTitle(title);
            b.setAuthor(author);
            b.setIsbn(isbn);
            b.setPrice(price);
            b.setPublishDate(publishDate);

            BookDetail d = detailRequest.toEntity();
            b.setDetail(d); // 양방향 세팅
            return b;
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookUpdateRequest {
        @NotBlank private String title;
        @NotBlank private String author;
        @Pattern(regexp = "^[0-9A-Za-z-]{5,20}$") private String isbn;
        @Positive @NotNull private Integer price;
        @NotNull @PastOrPresent private LocalDate publishDate;

        @NotNull private BookDetailRequest detailRequest;
    }

    // ---------- PATCH DTOs ----------
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookDetailPatchRequest {
        private String description;
        private String language;
        @Positive(message = "페이지 수는 양수여야 합니다.") private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PatchRequest {
        // null이면 무시, 값이 오면 검증
        @Pattern(regexp = ".*\\S.*", message = "제목은 공백만 올 수 없습니다.")
        private String title;

        @Pattern(regexp = ".*\\S.*", message = "저자는 공백만 올 수 없습니다.")
        private String author;

        @Pattern(regexp = "^[0-9A-Za-z-]{5,20}$", message = "ISBN 형식 오류")
        private String isbn;

        @Positive private Integer price;

        @PastOrPresent private LocalDate publishDate;

        private BookDetailPatchRequest detailRequest;
    }

    // ---------- Response DTOs ----------
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookDetailResponse {
        private String description;
        private String language;
        private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;

        public static BookDetailResponse from(BookDetail d) {
            if (d == null) return null;
            return new BookDetailResponse(
                    d.getDescription(),
                    d.getLanguage(),
                    d.getPageCount(),
                    d.getPublisher(),
                    d.getCoverImageUrl(),
                    d.getEdition()
            );
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BookResponse {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;
        private BookDetailResponse detail;

        public static BookResponse from(Book b) {
            return new BookResponse(
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getIsbn(),
                    b.getPrice(),
                    b.getPublishDate(),
                    BookDetailResponse.from(b.getDetail())
            );
        }
    }
}
