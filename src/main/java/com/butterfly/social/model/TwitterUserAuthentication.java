package com.butterfly.social.model;

import java.io.Serializable;
import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;

public final class TwitterUserAuthentication implements Serializable {
    private static final String consumerKey = "c3Ku51cVby3ITwZ6vaGgnqCzV";
    private static final String consumerSecret = "JK5CMek6mbUvFZpWtsww5RjdaMc74o9ifJxijT32cKB0cAwA60";
    private final Twitter twitter;
    private RequestToken requestToken;
    private AccessToken accessToken;

    public TwitterUserAuthentication() {
        this.twitter = TwitterFactory.getSingleton();
        this.twitter.setOAuthConsumer(consumerKey, consumerSecret);
        this.requestToken = null; /* This value will be initialized in getURL() */
        this.accessToken = null; /* This value will be initialized in handlePIN() */
    } //TwitterUserAuthentication

    public String getURL() throws TwitterException {
        this.requestToken = this.twitter.getOAuthRequestToken();

        return this.requestToken.getAuthorizationURL();
    } //String

    public Twitter getTwitter() {
        return this.twitter;
    } //getTwitter

    public void handlePIN(String pin) {
        try {
            if (pin.length() > 0) {
                this.accessToken = this.twitter.getOAuthAccessToken(this.requestToken, pin);
            } else {
                this.accessToken = this.twitter.getOAuthAccessToken();
            } //end if
        } catch (TwitterException te) {
            if (401 == te.getStatusCode()) {
                System.out.println("Unable to get the access token.");
            } else {
                te.printStackTrace();
            } //end if
        } //end try catch

        this.twitter.setOAuthAccessToken(this.accessToken);
    } //handlePIN
}