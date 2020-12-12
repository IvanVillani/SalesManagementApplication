package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import twitter4j.TwitterException;

import java.util.List;

public interface ISocialService {
    void createTweet(DiscountServiceModel discount) throws TwitterException;

    List<String> getTimeLine() throws TwitterException;

    String sendDirectMessage(String recipientName, String msg) throws TwitterException;

    List<String> searchtweets() throws TwitterException;
}
