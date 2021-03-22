package com.butterfly.social.controller;

import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.menu.MenuController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.Model;
import com.butterfly.social.view.View;
import javafx.scene.layout.VBox;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * A controller of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class Controller {
    /**
     * The map from boxes to posts of this controller.
     */
    private final Map<VBox, Post> boxesToPosts;

    /**
     * The menu controller of this controller.
     */
    private final MenuController menuController;

    /**
     * The Reddit post controller of this controller.
     */
    private final RedditPostController redditPostController;

    /**
     * The Twitter post controller of this controller.
     */
    private final TwitterPostController twitterPostController;

    /**
     * The Instagram post controller of this controller.
     */
    private final InstagramPostController instagramPostController;

    /**
     * Constructs a newly allocated {@code Controller} object with the specified model, view, and all box lock.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @param allBoxLock the all box lock to be used in construction
     * @throws NullPointerException if the specified model, view, or all box lock is {@code null}
     */
    public Controller(Model model, View view, Lock allBoxLock) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        Objects.requireNonNull(allBoxLock, "the specified all box lock is null");

        this.boxesToPosts = new ConcurrentHashMap<>();

        this.menuController = MenuController.createMenuController(model, view);

        this.redditPostController = RedditPostController.createRedditPostController(model, view, allBoxLock,
                                                                                    this.boxesToPosts);

        this.twitterPostController = TwitterPostController.createTwitterPostController(model, view, allBoxLock,
                                                                                       this.boxesToPosts);

        this.instagramPostController = InstagramPostController.createInstagramPostController(model, view, allBoxLock,
                                                                                             this.boxesToPosts);
    } //Controller

    /**
     * Returns the map from boxes to posts of this controller.
     *
     * @return the map from boxes to posts of this controller
     */
    public Map<VBox, Post> getBoxesToPosts() {
        return this.boxesToPosts;
    } //getBoxesToPosts

    /**
     * Returns the menu controller of this controller
     *
     * @return the menu controller of this controller
     */
    public MenuController getMenuController() {
        return this.menuController;
    } //getMenuController

    /**
     * Returns the Reddit post controller of this controller
     *
     * @return the Reddit post controller of this controller
     */
    public RedditPostController getRedditPostController() {
        return this.redditPostController;
    } //getRedditPostController

    /**
     * Returns the Twitter post controller of this controller
     *
     * @return the Twitter post controller of this controller
     */
    public TwitterPostController getTwitterPostController() {
        return this.twitterPostController;
    } //getTwitterPostController

    /**
     * Returns the Instagram post controller of this controller
     *
     * @return the Instagram post controller of this controller
     */
    public InstagramPostController getInstagramPostController() {
        return this.instagramPostController;
    } //getInstagramPostController
}