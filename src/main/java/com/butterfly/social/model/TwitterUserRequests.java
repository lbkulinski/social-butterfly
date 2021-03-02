package com.butterfly.social.model;

import java.io.Serializable;
import twitter4j.Twitter;
import java.util.List;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class TwitterUserRequests implements Serializable {
    private Twitter twitter;
    private TwitterUserProfile profile;

    public TwitterUserRequests() {
        this.twitter = null;
        this.profile = null;
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
}