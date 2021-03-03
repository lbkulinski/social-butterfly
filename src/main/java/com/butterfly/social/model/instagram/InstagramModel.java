package com.butterfly.social.model.instagram;

import java.io.Serializable;
import com.github.instagram4j.instagram4j.IGClient;

public final class InstagramModel implements Serializable {
    private final InstagramUserAuthentication auth;
    private final InstagramUserRequests requests;

    public InstagramModel() {
        this.auth = new InstagramUserAuthentication();
        this.requests = new InstagramUserRequests();
    } //TwitterModel

    public void initializeRequests() {
        IGClient client;

        client = this.auth.getClient();

        this.requests.setClient(client);
    } //initializeRequests

    public IGClient getClient() {
        return this.auth.getClient();
    } //getClient

    public InstagramUserAuthentication getAuth() {
        return this.auth;
    } //getAuth

    public InstagramUserRequests getRequests() {
        return this.requests;
    } //getRequests
}