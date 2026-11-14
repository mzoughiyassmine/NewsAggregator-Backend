package com.example.news_agg_backend.controller;

import com.example.news_agg_backend.dto.SourceRSSRequest;
import com.example.news_agg_backend.dto.SourceRSSResponse;
import com.example.news_agg_backend.service.SourceRSSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sources")
@RequiredArgsConstructor
public class SourceRSSController {

    private final SourceRSSService sourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SourceRSSResponse create(@Valid @RequestBody SourceRSSRequest request) {
        return sourceService.create(request);
    }

    @GetMapping
    public List<SourceRSSResponse> findAll() {
        return sourceService.findAll();
    }

    @GetMapping("/{id}")
    public SourceRSSResponse findById(@PathVariable String id) {
        return sourceService.findById(id);
    }

    @PutMapping("/{id}")
    public SourceRSSResponse update(@PathVariable String id,
                                    @Valid @RequestBody SourceRSSRequest request) {
        return sourceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        sourceService.delete(id);
    }
}