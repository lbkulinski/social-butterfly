package com.butterfly.social.controller;

import com.butterfly.social.model.TwitterModel;
import com.butterfly.social.view.PostView;
import java.util.Objects;

/**
 * A controller for Twitter posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * February 28, 2021
 */
public final class TwitterPostController {
    /**
     * The twitter model of this twitter post controller.
     */
    private final TwitterModel twitterModel;

    /**
     * The post view of this twitter post controller.
     */
    private final PostView postView;

    /**
     * Constructs a newly allocated {@code TwitterPostController} object with the specified twitter model and post
     * view.
     *
     * @param twitterModel the twitter model to be used in construction
     * @param postView the post view to be used in construction
     * @throws NullPointerException if the specified twitter model or post view is {@code null}
     */
    private TwitterPostController(TwitterModel twitterModel, PostView postView) {
        Objects.requireNonNull(twitterModel, "the specified twitter model is null");

        Objects.requireNonNull(twitterModel, "the specified post view is null");

        this.twitterModel = twitterModel;

        this.postView = postView;
    } //TwitterPostController

    public static TwitterPostController createTwitterPostController(TwitterModel twitterModel, PostView postView) {
        TwitterPostController controller;

        controller = new TwitterPostController(twitterModel, postView);

        return controller;
    } //createTwitterPostController
}