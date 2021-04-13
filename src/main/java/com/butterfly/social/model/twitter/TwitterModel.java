package com.butterfly.social.model.twitter;

import java.io.Serializable;
import java.util.Objects;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public final class TwitterModel implements Serializable {
    private final TwitterUserAuthentication auth;
    private final TwitterUserRequests requests;

    public TwitterModel(TwitterUserAuthentication auth, TwitterUserRequests requests) {
        Objects.requireNonNull(auth, "the specified auth is null");

        Objects.requireNonNull(requests, "the specified requests are null");

        this.auth = auth;
        this.requests = requests;
    } //TwitterModel

    public TwitterModel() {
        this.auth = new TwitterUserAuthentication();
        this.requests = new TwitterUserRequests();
    } //TwitterModel

    public void initializeRequests() throws TwitterException{
        Twitter twitter;

        twitter = this.auth.getTwitter();

        this.requests.setTwitter(twitter);
    } //initializeRequests

    public Twitter getTwitter() {
        return this.auth.getTwitter();
    } //getTwitter

    public TwitterUserAuthentication getAuth() {
        return this.auth;
    } //getAuth

    public TwitterUserRequests getRequests() {
        return this.requests;
    } //getRequests
}