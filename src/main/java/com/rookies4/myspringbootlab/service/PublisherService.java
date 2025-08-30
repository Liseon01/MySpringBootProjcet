package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.PublisherDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    /** 모든 출판사를 조회하며, 각 출판사의 도서 수를 포함 */
    public List<PublisherDTO.SimpleResponse> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream()
                .map(p -> {
                    Long count = bookRepository.countByPublisherId(p.getId());
                    return PublisherDTO.SimpleResponse.fromEntityWithCount(p, count == null ? 0L : count);
                })
                .collect(Collectors.toList());
    }

    /** ID로 특정 출판사를 조회(도서 리스트 포함) */
    public PublisherDTO.Response getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findByIdWithBooks(id)
                .orElseGet(() -> publisherRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                                ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", id)
                        )));
        return PublisherDTO.Response.fromEntity(publisher);
    }

    /** 이름으로 특정 출판사를 조회 */
    public PublisherDTO.Response getPublisherByName(String name) {
        Publisher publisher = publisherRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "name", name)
                ));
        return PublisherDTO.Response.fromEntity(publisher);
    }

    /** 새로운 출판사 생성(이름 중복 검증) */
    @Transactional
    public PublisherDTO.Response createPublisher(PublisherDTO.Request request) {
        if (publisherRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_DUPLICATE.getHttpStatus(),
                    ErrorCode.RESOURCE_DUPLICATE.formatMessage("Publisher", "name", request.getName())
            );
        }
        Publisher saved = publisherRepository.save(
                Publisher.builder()
                        .name(request.getName())
                        .establishedDate(request.getEstablishedDate())
                        .address(request.getAddress())
                        .build()
        );
        return PublisherDTO.Response.fromEntity(saved);
    }

    /** 기존 출판사 수정(자기 자신 제외 이름 중복 검증) */
    @Transactional
    public PublisherDTO.Response updatePublisher(Long id, PublisherDTO.Request request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", id)
                ));

        publisherRepository.findByName(request.getName()).ifPresent(dup -> {
            if (!dup.getId().equals(id)) {
                throw new BusinessException(
                        ErrorCode.RESOURCE_DUPLICATE.getHttpStatus(),
                        ErrorCode.RESOURCE_DUPLICATE.formatMessage("Publisher", "name", request.getName())
                );
            }
        });

        publisher.setName(request.getName());
        publisher.setEstablishedDate(request.getEstablishedDate());
        publisher.setAddress(request.getAddress());

        return PublisherDTO.Response.fromEntity(publisher);
    }

    /** 출판사 삭제(도서가 있으면 거부) */
    @Transactional
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.formatMessage("Publisher", "id", id)
                ));

        Long bookCount = bookRepository.countByPublisherId(id);
        if (bookCount != null && bookCount > 0) {
            // 전용 ErrorCode가 없으면 HttpStatus 직접 사용
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    String.format("Cannot delete publisher with id: %s. It has %s books", id, bookCount)
            );
            // 전용 코드 추가 시(권장):
            // throw new BusinessException(
            //     ErrorCode.PUBLISHER_HAS_BOOKS.getHttpStatus(),
            //     ErrorCode.PUBLISHER_HAS_BOOKS.formatMessage(id, bookCount)
            // );
        }

        publisherRepository.delete(publisher);
    }
}
