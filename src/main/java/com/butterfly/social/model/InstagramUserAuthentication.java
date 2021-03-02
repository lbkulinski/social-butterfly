package com.butterfly.social.model;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import twitter4j.Twitter;

import java.io.Serializable;

public final class InstagramUserAuthentication implements Serializable {

    private IGClient client;

    public InstagramUserAuthentication() {
        this.client = login("SocialButterflyCS407", "socialbutterfly");
    } //TwitterUserAuthentication

    public IGClient login(String username, String password) {
        try {
            return IGClient.builder().username(username).password(password).login();
        }
        catch (IGLoginException e) {
            System.out.println("Failed to login\n");
            return null;
        }
    } //IGClient

    public IGClient getClient() {
        return this.client;
    } //getTwitter
}