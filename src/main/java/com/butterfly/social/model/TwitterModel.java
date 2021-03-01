package com.butterfly.social.model;

import twitter4j.Twitter;

public final class TwitterModel {
    private final TwitterUserAuthentication auth;
    private final TwitterUserRequests requests;

    public TwitterModel() {
        this.auth = new TwitterUserAuthentication();
        this.requests = new TwitterUserRequests();
    } //TwitterModel

    public void initializeRequests() {
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