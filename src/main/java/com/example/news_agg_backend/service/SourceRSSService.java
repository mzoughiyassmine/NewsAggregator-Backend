package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.SourceRSSRequest;
import com.example.news_agg_backend.dto.SourceRSSResponse;
import com.example.news_agg_backend.model.SourceRSS;
import com.example.news_agg_backend.model.SourceStatus;
import com.example.news_agg_backend.repository.SourceRSSRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceRSSService {

    private final SourceRSSRepository sourceRepository;

    public SourceRSSResponse create(SourceRSSRequest request) {
        String url = request.url().trim();

        if (sourceRepository.existsByUrl(url)) {
            throw urlAlreadyUsed();
        }

        SourceRSS source = SourceRSS.builder()
                .name(request.name().trim())
                .url(url)
                .description(cleanDescription(request.description()))
                .status(request.status() != null ? request.status() : SourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return SourceRSSResponse.from(sourceRepository.save(source));
    }

    public List<SourceRSSResponse> findAll() {
        return sourceRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(SourceRSSResponse::from)
                .toList();
    }

    public SourceRSSResponse findById(String id) {
        return SourceRSSResponse.from(getOrThrow(id));
    }

    public SourceRSSResponse update(String id, SourceRSSRequest request) {
        SourceRSS source = getOrThrow(id);
        String url = request.url().trim();

        if (sourceRepository.existsByUrlAndIdNot(url, id)) {
            throw urlAlreadyUsed();
        }

        source.setName(request.name().trim());
        source.setUrl(url);
        source.setDescription(cleanDescription(request.description()));
        if (request.status() != null) {
            source.setStatus(request.status());
        }

        return SourceRSSResponse.from(sourceRepository.save(source));
    }

    public void delete(String id) {
        sourceRepository.delete(getOrThrow(id));
    }

    private SourceRSS getOrThrow(String id) {
        return sourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Source RSS introuvable"));
    }

    private ResponseStatusException urlAlreadyUsed() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT, "Cette URL RSS est déjà enregistrée");
    }

    private String cleanDescription(String description) {
        return (description == null || description.isBlank()) ? null : description.trim();
    }
}