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

    public static RedditModel createRedditModel(String username, String password, String clientId,
                                                String clientSecret) {
        RedditModel redditModel;
        UserAgent userAgent;
        String platform = "Social Butterfly";
        String appId = "com.butterfly.social";
        String version = "v1.0";
        NetworkAdapter networkAdapter;
        Credentials credentials;
        RedditClient client;

        Objects.requireNonNull(username, "the specified username is null");

        Objects.requireNonNull(password, "the specified password is null");

        Objects.requireNonNull(clientId, "the specified client ID is null");

        Objects.requireNonNull(clientSecret, "the specified client secret is null");

        redditModel = new RedditModel();

        userAgent = new UserAgent(platform, appId, version, username);

        networkAdapter = new OkHttpNetworkAdapter(userAgent);

        credentials = Credentials.script(username, password, clientId, clientSecret);

        client = OAuthHelper.automatic(networkAdapter, credentials);

        redditModel.setClient(client);

        return redditModel;
    } //createRedditModel
}