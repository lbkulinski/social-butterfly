package com.butterfly.social.model.twitter;

import java.io.Serializable;
import twitter4j.Twitter;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;

import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class TwitterUserRequests implements Serializable {
    Thread backgroundDMThread;
    private Twitter twitter;
    private AsyncTwitter asyncTwitter;
    private AsyncTwitterFactory factory;
    private TwitterUserProfile profile;

    public TwitterUserRequests() {
        this.twitter = null;
        this.profile = null;
        factory = new AsyncTwitterFactory();
    } //TwitterUserRequests

    public void setTwitter(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.profile = new TwitterUserProfile(twitter);
    } //setTwitter

    public Twitter getTwitter() {
        return this.twitter;
    } //getTwitter

    public TwitterUserProfile getProfile() {
        return this.profile;
    }

    public List<Status> getTimeline() throws TwitterException {
        return twitter.getHomeTimeline();
    } //getTimeline

    public String timelineToString(List<Status> timeline) {
        StringBuilder result = new StringBuilder();

        for (Status status : timeline) {
            result.append(status.getUser().getName())
                  .append(": ").append(status.getText())
                  .append("\n\n");
        } //end for

        return result.toString();
    } //timelineToString

    public List<DirectMessage> getDirectMessages() throws TwitterException {
        /*
        asyncTwitter = factory.getInstance(twitter);
        this.backgroundDMThread = new Thread(() -> {
            Paging paging;
            int count = 200;
            int amount = 60_000;

            paging = new Paging();

            paging.setCount(count);

            while (true) {
                asyncTwitter.getDirectMessages(paging);
                try {
                    Thread.sleep(amount);
                } catch (InterruptedException e) {
                    return;
                } //end try catch
            } //end while
        });
        backgroundDMThread.start();
        */
        DirectMessageList responses = twitter.getDirectMessages(20);
        ArrayList<DirectMessage> messages = new ArrayList<DirectMessage>();
        for (int i = 0; i < responses.size(); i++) {
            messages.add(responses.get(i));
        }
        return messages;
    }
}