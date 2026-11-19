package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.FeedItem;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class RssFeedReader {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public List<FeedItem> read(String feedUrl) throws IOException, FeedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(feedUrl))
                .timeout(REQUEST_TIMEOUT)
                .header("User-Agent", "NewsAggregator/1.0")
                .header("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml")
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Lecture du flux interrompue", e);
        }

        try (InputStream body = response.body()) {
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new IOException("Le serveur a répondu avec le code HTTP " + status);
            }

            SyndFeed feed = new SyndFeedInput().build(new XmlReader(body));
            return feed.getEntries().stream()
                    .map(this::toItem)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    private FeedItem toItem(SyndEntry entry) {
        String title = clean(entry.getTitle());
        String link = clean(entry.getLink());

        // Sans titre ou sans lien, l'article est inutilisable (le lien sert aussi à détecter les doublons)
        if (title == null || link == null) {
            return null;
        }

        SyndContent content = entry.getDescription();
        String description = content != null ? clean(content.getValue()) : null;
        Date date = entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate();

        return new FeedItem(title, link, description, toLocalDateTime(date),
                ArticleImageExtractor.extract(entry));    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String clean(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}