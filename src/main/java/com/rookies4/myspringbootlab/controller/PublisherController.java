package com.rookies4.myspringbootlab.controller;

import com.rookies4.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies4.myspringbootlab.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherDTO.SimpleResponse>> getAll() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }
    @GetMapping("/by-name")
    public ResponseEntity<PublisherDTO.Response> getByNameQuery(@RequestParam String name) {
        return ResponseEntity.ok(publisherService.getPublisherByName(name));
    }

    @GetMapping("/name/{name:.+}")
    public ResponseEntity<PublisherDTO.Response> getByNamePath(@PathVariable String name) {
        return ResponseEntity.ok(publisherService.getPublisherByName(name));
    }

    @PostMapping
    public ResponseEntity<PublisherDTO.Response> create(@Valid @RequestBody PublisherDTO.Request request) {
        var body = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO.Response> update(@PathVariable Long id,
                                                        @Valid @RequestBody PublisherDTO.Request request) {
        return ResponseEntity.ok(publisherService.updatePublisher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
