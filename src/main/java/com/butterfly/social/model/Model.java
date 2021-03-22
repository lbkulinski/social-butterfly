package com.butterfly.social.model;

import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;

/**
 * A model of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class Model {
    /**
     * The Reddit model of this model.
     */
    private RedditModel redditModel;

    /**
     * The Twitter model of this model.
     */
    private TwitterModel twitterModel;

    /**
     * The Instagram model of this model.
     */
    private InstagramModel instagramModel;

    /**
     * Constructs a newly allocated {@code Model} object with the specified Reddit model, Twitter model, and Instagram
     * model.
     *
     * @param redditModel the Reddit model to be used in construction
     * @param twitterModel the Twitter model to be used in construction
     * @param instagramModel the Instagram model to be used in construction
     */
    public Model(RedditModel redditModel, TwitterModel twitterModel, InstagramModel instagramModel) {
        this.redditModel = redditModel;

        this.twitterModel = twitterModel;

        this.instagramModel = instagramModel;
    } //Model

    /**
     * Constructs a newly allocated {@code Model} object with a default Reddit model, Twitter model, and Instagram
     * model of {@code null}.
     */
    public Model() {
        this(null, null, null);
    } //Model

    /**
     * Returns the Reddit model of this model
     *
     * @return the Reddit model of this model
     */
    public RedditModel getRedditModel() {
        return this.redditModel;
    } //getRedditModel

    /**
     * Returns the Twitter model of this model
     *
     * @return the Twitter model of this model
     */
    public TwitterModel getTwitterModel() {
        return this.twitterModel;
    } //getTwitterModel

    /**
     * Returns the Instagram model of this model
     *
     * @return the Instagram model of this model
     */
    public InstagramModel getInstagramModel() {
        return this.instagramModel;
    } //getInstagramModel

    /**
     * Updates the Reddit model of this model with the specified Reddit model.
     *
     * @param redditModel the Reddit model to be used in the update
     */
    public void setRedditModel(RedditModel redditModel) {
        this.redditModel = redditModel;
    } //setRedditModel

    /**
     * Updates the Twitter model of this model with the specified Twitter model.
     *
     * @param twitterModel the Twitter model to be used in the update
     */
    public void setTwitterModel(TwitterModel twitterModel) {
        this.twitterModel = twitterModel;
    } //setTwitterModel

    /**
     * Updates the Instagram model of this model with the specified Instagram model.
     *
     * @param instagramModel the Instagram model to be used in the update
     */
    public void setInstagramModel(InstagramModel instagramModel) {
        this.instagramModel = instagramModel;
    } //setInstagramModel
}