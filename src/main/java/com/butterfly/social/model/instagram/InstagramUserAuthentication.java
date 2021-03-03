package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;

import java.io.Serializable;

public final class InstagramUserAuthentication implements Serializable {
    private IGClient client;

    public InstagramUserAuthentication() {
        this.client = null;
    } //InstagramUserAuthentication

    public IGClient login(String username, String password) {
        try {
            this.client = IGClient.builder()
                                  .username(username)
                                  .password(password)
                                  .login();

            return this.client;
        } catch (IGLoginException e) {
            e.printStackTrace();

            return null;
        } //end try catch
    } //login

    public IGClient getClient() {
        return this.client;
    } //getClient

    public void setClient(IGClient client) {
        this.client = client;
    } //setClient
}