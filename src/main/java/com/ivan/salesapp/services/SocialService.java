package com.ivan.salesapp.services;

import com.ivan.salesapp.constants.TwitterConstants;
import com.ivan.salesapp.domain.models.service.DiscountServiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class SocialService implements ISocialService, TwitterConstants {
    private final Twitter twitter;

    @Autowired
    public SocialService(Twitter twitter) {
        this.twitter = twitter;
    }

    @Async
    @Override
    public void createTweet(DiscountServiceModel discount) throws TwitterException {
        Twitter twitter = this.twitter;
        twitter.updateStatus(assembleTweet(discount));
    }

    @Async
    @Override
    public List<String> getTimeLine() throws TwitterException {
        Twitter twitter = this.twitter;

        return twitter.getHomeTimeline().stream()
                .map(Status::getText)
                .collect(toList());
    }

    @Async
    @Override
    public String sendDirectMessage(String recipientName, String msg) throws TwitterException {

        Twitter twitter = this.twitter;
        DirectMessage message = twitter.sendDirectMessage(recipientName, msg);
        return message.getText();
    }

    @Async
    @Override
    public List<String> searchtweets() throws TwitterException {

        Twitter twitter = this.twitter;
        Query query = new Query("source:twitter4j baeldung");
        QueryResult result = twitter.search(query);

        return result.getTweets().stream()
                .map(Status::getText)
                .collect(toList());
    }

    @Async
    private String assembleTweet(DiscountServiceModel discount){
        return String.format(TWEET_TEMPLATE,
                discount.getCreator().toUpperCase(),
                discount.getProduct().getName(),
                discount.getProduct().getPrice(),
                discount.getPrice());
    }
}
