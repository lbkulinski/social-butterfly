package com.butterfly.social.model.reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import java.util.Objects;

public final class RedditModel {
    private RedditClient client;

    private RedditModel() {
        this.client = null;
    } //RedditModel

    public RedditClient getClient() {
        return this.client;
    } //getClient

    public void setClient(RedditClient client) {
        this.client = client;
    } //setClient

    public RedditModel createRedditModel(String username, String password) {
        RedditModel redditModel;
        UserAgent userAgent;
        String platform = "Social Butterfly";
        String appId = "com.butterfly.social";
        String version = "v1.0";
        NetworkAdapter networkAdapter;
        Credentials credentials;
        String clientId = "GaoCzV0A1aEvMA";
        String clientSecret = "14CAWMVrhheFVi0n5XDgpAAxnZV5Fw";
        RedditClient client;

        Objects.requireNonNull(username, "the specified username is null");

        Objects.requireNonNull(password, "the specified password is null");

        redditModel = new RedditModel();

        userAgent = new UserAgent(platform, appId, version, username);

        networkAdapter = new OkHttpNetworkAdapter(userAgent);

        credentials = Credentials.script(username, password, clientId, clientSecret);

        client = OAuthHelper.automatic(networkAdapter, credentials);

        redditModel.setClient(client);

        return redditModel;
    } //createRedditModel
}