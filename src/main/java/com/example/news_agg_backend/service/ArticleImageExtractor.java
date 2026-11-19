package com.example.news_agg_backend.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import org.jdom2.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ArticleImageExtractor {

    private static final Pattern IMG_TAG =
            Pattern.compile("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMAGE_EXT =
            Pattern.compile("\\.(jpe?g|png|webp|gif)(\\?.*)?$", Pattern.CASE_INSENSITIVE);

    private ArticleImageExtractor() {}

    public static String extract(SyndEntry entry) {
        // 1. Pièces jointes de type image
        for (SyndEnclosure enclosure : entry.getEnclosures()) {
            String type = enclosure.getType();
            if (type != null && type.startsWith("image/") && isHttp(enclosure.getUrl())) {
                return enclosure.getUrl();
            }
        }

        // 2. media:thumbnail et media:content
        for (Element element : entry.getForeignMarkup()) {
            String name = element.getName();
            if ("thumbnail".equals(name) || "content".equals(name)) {
                String url = element.getAttributeValue("url");
                String type = element.getAttributeValue("type");
                String medium = element.getAttributeValue("medium");
                boolean looksLikeImage = "thumbnail".equals(name)
                        || (type != null && type.startsWith("image/"))
                        || "image".equals(medium)
                        || (url != null && IMAGE_EXT.matcher(url).find());
                if (looksLikeImage && isHttp(url)) {
                    return url;
                }
            }
        }

        // 3. Première balise <img> du résumé ou du contenu
        if (entry.getDescription() != null) {
            String found = firstImg(entry.getDescription().getValue());
            if (found != null) return found;
        }
        for (SyndContent content : entry.getContents()) {
            String found = firstImg(content.getValue());
            if (found != null) return found;
        }
        return null;
    }

    private static String firstImg(String html) {
        if (html == null) return null;
        Matcher m = IMG_TAG.matcher(html);
        while (m.find()) {
            String url = m.group(1).trim().replace("&amp;", "&");
            if (isHttp(url)) return url;
        }
        return null;
    }

    // On n'accepte que http(s) : jamais un autre type de lien venu d'un flux externe
    private static boolean isHttp(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}