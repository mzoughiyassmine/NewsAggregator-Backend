package com.example.news_agg_backend.service;

import com.example.news_agg_backend.dto.FeedItem;

import java.util.List;

public class TempFeedCheck {

    public static void main(String[] args) throws Exception {
        List<FeedItem> items = new RssFeedReader().read("https://feeds.bbci.co.uk/news/rss.xml");
        System.out.println("Articles lus : " + items.size());
        items.stream().limit(3).forEach(System.out::println);
    }
}