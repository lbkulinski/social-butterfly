package com.butterfly.social.model.twitter;

import java.io.Serializable;

import twitter4j.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.IDs;
import twitter4j.PagableResponseList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class TwitterUserRequests implements Serializable {
    Thread backgroundDMThread;
    private Twitter twitter;
    private AsyncTwitter asyncTwitter;
    private AsyncTwitterFactory factory;
    private TwitterUserProfile profile;

    public TwitterUserRequests() {
        this.twitter = null;
        this.profile = null;
        factory = new AsyncTwitterFactory();
    } //TwitterUserRequests

    public void setTwitter(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        this.profile = new TwitterUserProfile(twitter);
    } //setTwitter

    public Twitter getTwitter() {
        return this.twitter;
    } //getTwitter

    public TwitterUserProfile getProfile() {
        return this.profile;
    }

    public List<Status> getTimeline() throws TwitterException {
        return twitter.getHomeTimeline();
    } //getTimeline

    public String timelineToString(List<Status> timeline) {
        StringBuilder result = new StringBuilder();

        for (Status status : timeline) {
            result.append(status.getUser().getName())
                  .append(": ").append(status.getText())
                  .append("\n\n");
        } //end for

        return result.toString();
    } //timelineToString

    public List<DirectMessage> getDirectMessages() throws TwitterException {
        /*
        asyncTwitter = factory.getInstance(twitter);
        this.backgroundDMThread = new Thread(() -> {
            Paging paging;
            int count = 200;
            int amount = 60_000;

            paging = new Paging();

            paging.setCount(count);

            while (true) {
                asyncTwitter.getDirectMessages(paging);
                try {
                    Thread.sleep(amount);
                } catch (InterruptedException e) {
                    return;
                } //end try catch
            } //end while
        });
        backgroundDMThread.start();
        */
        DirectMessageList responses = twitter.getDirectMessages(20);
        ArrayList<DirectMessage> messages = new ArrayList<DirectMessage>();
        for (int i = 0; i < responses.size(); i++) {
            messages.add(responses.get(i));
        }
        return messages;
    }

    public Status postTweet(StatusUpdate statusUpdate) throws TwitterException {
        /** Posts a new tweet to the user's account containing
         * the information in statusUpdate. If a replytostatusid
         * is included in statusUpdate, this tweet will be added
         * as a reply to the specified tweet.
         *
         * @return status object that is created with the text
         * paramater
         */
        Status status = twitter.updateStatus(statusUpdate);
        return status;
    }

    public Status favoriteTweet(long id) throws TwitterException {
        /** Favorites the status specified by the id
         *  paramater.
         *
         * @return the favorited status
         */
        Status status = twitter.createFavorite(id);
        return status;
    }

    public String getTweetInformation(long id) throws TwitterException {
        /** Obtains information about the status specified
         * by the id paramter.
         *
         * @return information about the status
         */
        Status status = twitter.showStatus(id);

        String s = "Name: " +
                status.getUser().getName() +
                "\nScreen Name: " +
                status.getUser().getScreenName();
        return s;
    }

    public DirectMessage sendDirectMessage(long id, String message) throws TwitterException {
        /** Sends a message containing the text @param message
         * to the user specified by id.
         *
         * @return DirectMessage object
         */
        DirectMessage directMessage = twitter.sendDirectMessage(id, message);
        return directMessage;
    }

    public DirectMessage sendDirectMessage(String screenName, String message) throws TwitterException {
        /** Sends a message containing the text @param message
         * to the user specified by screenname.
         *
         * @return DirectMessage object
         */
        DirectMessage directMessage = twitter.sendDirectMessage(screenName, message);
        return directMessage;
    }

    public boolean followTwitterUser(String username) {
        boolean val = true;
        try {
            twitter.createFriendship(username);
        } catch (TwitterException e) {
            e.printStackTrace();
            val = false;
        }
        return val;
    }

    public boolean blockTwitterUser(String username) {
        boolean val = true;
        try {
            twitter.createBlock(username);
        } catch (TwitterException e) {
            e.printStackTrace();
            val = false;
        }
        return val;
    }

    public List<String> getBlockedUsers() {
        List<String> usernames = new ArrayList<String>();
        try {
            PagableResponseList<User> blocked = twitter.getBlocksList();
            for(User blockedUser : blocked) {
                usernames.add(blockedUser.getScreenName());
            }
        } catch (TwitterException te) {
            System.out.println("Didn't work.");
        }
        return usernames;
    }
    
    public List<String> getTrending() throws TwitterException {
        List<String> trends = new ArrayList<>();
        Trends trending = twitter.getPlaceTrends(23424977);
        for (Trend trend : trending.getTrends()) {
            trends.add(trend.getName());
        }
        return trends;
    }

    public static void main(String[] args) throws TwitterException {
        /* Setup twitter model */
        TwitterModel user = new TwitterModel();
        String url = user.getAuth().getURL();
        System.out.println(url);

        Scanner in = new Scanner(System.in);
        String pin = in.nextLine();
        user.getAuth().handlePIN(pin);
        user.initializeRequests();

        user.getRequests().getTrending();
        /*

        StatusUpdate statusUpdate = new StatusUpdate("test tweet");
        Status status = user.getRequests().postTweet(statusUpdate);  // Test posting a tweet
        user.getRequests().favoriteTweet(status.getId());            // Test liking a tweet
        StatusUpdate statusUpdateReply = new StatusUpdate("test reply");
        statusUpdateReply.setInReplyToStatusId(status.getId());
        user.getRequests().postTweet(statusUpdateReply);             // Test replying to a tweet

        System.out.println(user.getRequests().getTweetInformation(status.getId())); // Test getting user info from a tweet


        user.getRequests().sendDirectMessage("JonFreier", "hello"); // Test sending a direct message

         */
    }
}