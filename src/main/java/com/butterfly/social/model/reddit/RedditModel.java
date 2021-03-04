package com.butterfly.social.model.reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.oauth.StatefulAuthHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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

    public static RedditModel createRedditModel(String username, String password) {
        RedditModel redditModel;
        UserAgent userAgent;
        String platform = "Social Butterfly";
        String appId = "com.butterfly.social";
        String version = "v1.0";
        NetworkAdapter networkAdapter;
        Credentials credentials;
        String clientId = "Kj4MoMRTrpUJ2Q";
        String clientSecret = "14CAWMVrhheFVi0n5XDgpAAxnZV5Fw";
        RedditClient client = null;
        StatefulAuthHelper helper;
        String redirectUrl = "http://localhost:8080/";
        BufferedReader in;
        String authUrl = "";
        ServerSocket serverSocket = null;
        String accessCode = "";
        String line = "";
        Socket socket;

        Objects.requireNonNull(username, "the specified username is null");

        Objects.requireNonNull(password, "the specified password is null");

        redditModel = new RedditModel();

        username = "cs408-spring-2021";

        userAgent = new UserAgent(platform, appId, version, username);

        networkAdapter = new OkHttpNetworkAdapter(userAgent);

        credentials = Credentials.installedApp(clientId, redirectUrl);

        helper = OAuthHelper.interactive(networkAdapter, credentials);

        try {
            authUrl = helper.getAuthorizationUrl(false, false, "read");
            System.out.println(authUrl);
            serverSocket = new ServerSocket(8080);
            socket = serverSocket.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            line = in.readLine();
            while(line != null) {
                System.out.println(line);
                if(line.contains("code=")) {
                    accessCode = line.substring(line.indexOf("code=")+5, line.indexOf(" HTTP"));
                    line = line.substring(line.indexOf("/") + 1, line.indexOf(" HTTP"));
                    line = redirectUrl + line + "/";
                    System.out.println(line);
                    if(helper.isFinalRedirectUrl(line)) {
                        System.out.println("SHOULD BE WORKING");
                    }
                    break;
                }
                line = in.readLine();
            }

            System.out.println("The access code is " + accessCode);

            //credentials = Credentials.script(username, password, clientId, clientSecret);

            client = helper.onUserChallenge(line);
    
            System.out.println(client == null ? "Null" : "Not Null");

            redditModel.setClient(client);
    
            socket.close();
            serverSocket.close();
            
        } catch (IOException ie) {
            System.out.println("Uh oh spaghettio");
            ie.printStackTrace();
        }

        return redditModel;
    } //createRedditModel
}