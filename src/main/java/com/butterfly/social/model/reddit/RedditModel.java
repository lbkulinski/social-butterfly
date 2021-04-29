package com.butterfly.social.model.reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.RedditException;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.UserHistorySort;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.references.UserReference;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.PublicContributionReference;

import java.util.LinkedList;
import net.dean.jraw.models.Message;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.BarebonesPaginator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class RedditModel {
    private RedditClient client;
    private String username;
    private List<String> blockedUsers;

    private RedditModel() {
        this.client = null;
        this.blockedUsers = new ArrayList<>();
        updateBlockedUsers();
    } //RedditModel

    public RedditClient getClient() {
        return this.client;
    } //getClient

    public void setClient(RedditClient client) {
        this.client = client;
    } //setClient

    public void setUsername(String username) {
        this.username = username;
    } //setUsername

    public String getUsername() {
        return this.username;
    } //getUsername

    public List<PublicContribution> getSavedPosts() {
        DefaultPaginator<PublicContribution<?>> build = this.client
                .me().history("saved").limit(100).sorting(UserHistorySort.NEW).build();
        List<PublicContribution> output = new LinkedList<>();
        try {
            Listing<PublicContribution<?>> savedItems = build.next();
            output.addAll(savedItems);
            if (savedItems.isEmpty()) {
                return null;
            }
        } catch (Exception ste) {
            ste.printStackTrace();
        }
        return output;
    }

    public List<String> getBlockedUsers() {
        return this.blockedUsers;
    }

    public void savePost(String id) {
        this.client.submission(id).save();
    }

    public void downvotePost(String id) {
        this.client.submission(id).downvote();
    }

    public void updateBlockedUsers() {
        File file = new File("reddit-blocked-users.txt");

        blockedUsers = new ArrayList<String>();

        try {
            //file.createNewFile();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line.replace("\n", "");
                blockedUsers.add(line);
            }
            scanner.close();
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

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

        redditModel.setUsername(username);

        return redditModel;
    } //createRedditModel

    public boolean followRedditUser(String username) {
        boolean success = true;
        try {
            this.client.subreddit("u_" + username).subscribe();
            success = true;
        }catch (RedditException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean blockRedditUser(String username) {
        File f = new File("reddit-blocked-users.txt");
        boolean hasDuplicate = false;
        try {
            //f.createNewFile();
            Scanner scanner = new Scanner(f);
            FileWriter writer = new FileWriter(f, true);
            BufferedWriter out = new BufferedWriter(writer);
            while(scanner.hasNextLine()) {
                String tempLine = scanner.nextLine();
                if(username == tempLine) { // if duplicate
                    hasDuplicate = true;
                    break;
                }
            }
            if(!hasDuplicate) {
                out.write("" + username + "\n");
            }
            scanner.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateBlockedUsers();
        try {
            this.client.subreddit("u_" + username).subscribe();
        }catch (RedditException e) {
            e.printStackTrace();
        }
        return !hasDuplicate;
    }

    public boolean followSubreddit(String subreddit) {
        boolean success = true;
        try {
            this.client.subreddit(subreddit).subscribe();
            success = true;
        }catch (RedditException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean unfollowSubreddit(String subreddit) {
        boolean success = true;
        try {
            this.client.subreddit(subreddit).unsubscribe();
            success = true;
        }catch (RedditException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public List<Message> getDirectMessages() {
        if(this.client == null) {
            return null;
        }
        BarebonesPaginator<Message> unread = this.client.me().inbox().iterate("messages").build();

        List<Message> messages = new ArrayList<Message>();

        Listing<Message> page = unread.next();
        if(page.isEmpty()) {
            System.out.println("Page is empty!");
        }
        for(Message m : page.getChildren()) {
            messages.add(m);
            System.out.println("Message in unread page");
        }

        return messages;
    }
}