package com.butterfly.social.model;

import java.io.Serializable;
import twitter4j.Twitter;
import java.util.List;
import twitter4j.Status;
import twitter4j.TwitterException;

public final class TwitterUserRequests implements Serializable {
    private Twitter twitter;

    public TwitterUserRequests() {
        this.twitter = null;
    } //TwitterUserRequests

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    } //setTwitter

    public Twitter getTwitter() {
        return this.twitter;
    } //getTwitter

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