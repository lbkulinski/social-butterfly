package com.butterfly.social.model;

import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.instagram.InstagramUserRequests;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.reddit.RedditUserRequests;
import com.butterfly.social.model.twitter.TwitterModel;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

import java.io.File;
import java.util.Scanner;

public class MultiPost {
    private Model model;

    public MultiPost(Model model) {
        this.model = model;
    }

    public void createPost(boolean twitter, boolean instagram, boolean reddit, String body, String title, File media, String subreddit) throws TwitterException {
        /** Creates a post on the platforms specified by the twitter, instagram, and reddit
         * parameters. The twitter post will contain the text in body. The reddit post
         * will be posted on the specified subreddit and will contain the text from title and body.
         * The instagram post will use body as the caption and the file media for the image.
         *
         */
        if (twitter) {
            StatusUpdate statusUpdate = new StatusUpdate(body);
            this.model.getTwitterModel().getRequests().postTweet(statusUpdate);
        }
        if (instagram) {
            InstagramUserRequests instagramUserRequests = new InstagramUserRequests();
            instagramUserRequests.setIgClient(this.model.getInstagramModel().getClient());
            instagramUserRequests.post(media, body);
        }
        if (reddit) {
            RedditUserRequests redditUserRequests = new RedditUserRequests();
            redditUserRequests.setRedditClient(this.model.getRedditModel().getClient());
            redditUserRequests.post(subreddit, title, body);
        }
    }

    public static void main(String[] args) throws TwitterException {
        /* setup model for testing purposed start */
        Model model = new Model();

        TwitterModel user = new TwitterModel();
        String url = user.getAuth().getURL();
        System.out.println(url);

        Scanner in = new Scanner(System.in);
        String pin = in.nextLine();
        user.getAuth().handlePIN(pin);
        user.initializeRequests();

        InstagramModel instagramModel = InstagramModel.createInstagramModel("SocialButterflyCS407", "socialbutterfly");
        RedditModel redditModel = RedditModel.createRedditModel("cs408-spring-2021",
                "deZgyz-tekno2-dybxaf", "GaoCzV0A1aEvMA", "14CAWMVrhheFVi0n5XDgpAAxnZV5Fw");
        model.setTwitterModel(user);
        model.setInstagramModel(instagramModel);
        model.setRedditModel(redditModel);

        /* setup model end */

        MultiPost multiPost = new MultiPost(model);
        //multiPost.createPost(true, true, true, "This is the body of my first multi-post!",
        //        "My first multi-post!", new File("/Users/anudeepyakkala/Desktop/image.jpg"), "test"); // test posting on all 3 platforms
        multiPost.createPost(true, false, true, "This is the body of my second multi-post!",
                "My second multi-post", null, "test");                                     // test posting on only Twitter and Reddit
    }
}
