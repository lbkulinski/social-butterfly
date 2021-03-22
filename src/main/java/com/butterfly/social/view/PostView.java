package com.butterfly.social.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import java.util.Objects;

/**
 * A view for posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 22, 2021
 */
public final class PostView {
    /**
     * The tab pane of this post view.
     */
    private final TabPane tabPane;

    /**
     * The Reddit box of this post view.
     */
    private final VBox redditBox;

    /**
     * The Twitter box of this post view.
     */
    private final VBox twitterBox;

    /**
     * The Instagram box of this post view.
     */
    private final VBox instagramBox;

    /**
     * The all box of this post view.
     */
    private final VBox allBox;

    /**
     * Constructs a newly allocated {@code PostView} object.
     */
    private PostView() {
        this.tabPane = new TabPane();

        this.redditBox = new VBox();

        this.twitterBox = new VBox();

        this.instagramBox = new VBox();

        this.allBox = new VBox();
    } //PostView

    /**
     * Returns the tab pane of this post view.
     *
     * @return the tab pane of this post view
     */
    public TabPane getTabPane() {
        return this.tabPane;
    } //getTabPane

    /**
     * Returns the Reddit box of this post view.
     *
     * @return the Reddit box of this post view
     */
    public VBox getRedditBox() {
        return this.redditBox;
    } //getRedditBox

    /**
     * Returns the Twitter box of this post view.
     *
     * @return the Twitter box of this post view
     */
    public VBox getTwitterBox() {
        return this.twitterBox;
    } //getTwitterBox

    /**
     * Returns the Instagram box of this post view.
     *
     * @return the Instagram box of this post view
     */
    public VBox getInstagramBox() {
        return this.instagramBox;
    } //getInstagramBox

    /**
     * Returns the all box of this post view.
     *
     * @return the all box of this post view
     */
    public VBox getAllBox() {
        return this.allBox;
    } //getAllBox

    /**
     * Creates, and returns, a {@code PostView} object using the specified primary stage and scene.
     *
     * @param primaryStage the primary stage to be used in the operation
     * @param scene the scene to be used in the operation
     * @return a {@code PostView} object using the specified primary stage and scene
     * @throws NullPointerException if the specified primary stage or scene is {@code null}
     */
    public static PostView createPostView(Stage primaryStage, Scene scene) {
        PostView postView;
        ScrollPane redditScrollPane;
        ScrollPane twitterScrollPane;
        ScrollPane instagramScrollPane;
        ScrollPane allScrollPane;
        Tab redditTab;
        String redditText = "Reddit";
        Tab twitterTab;
        String twitterText = "Twitter";
        Tab instagramTab;
        String instagramText = "Instagram";
        Tab allTab;
        String allText = "All";

        Objects.requireNonNull(primaryStage, "the specified primary stage is null");

        Objects.requireNonNull(scene, "the specified primary stage is null");

        postView = new PostView();

        redditScrollPane = new ScrollPane(postView.redditBox);

        twitterScrollPane = new ScrollPane(postView.twitterBox);

        instagramScrollPane = new ScrollPane(postView.instagramBox);

        allScrollPane = new ScrollPane(postView.allBox);

        redditTab = new Tab(redditText);

        redditTab.setClosable(false);

        redditTab.setContent(redditScrollPane);

        twitterTab = new Tab(twitterText);

        twitterTab.setClosable(false);

        twitterTab.setContent(twitterScrollPane);

        instagramTab = new Tab(instagramText);

        instagramTab.setClosable(false);

        instagramTab.setContent(instagramScrollPane);

        allTab = new Tab(allText);

        allTab.setClosable(false);

        allTab.setContent(allScrollPane);

        postView.tabPane.getTabs()
                        .addAll(redditTab, twitterTab, instagramTab, allTab);

        postView.tabPane.prefWidthProperty()
                        .bind(scene.widthProperty());

        postView.tabPane.prefHeightProperty()
                        .bind(scene.heightProperty());

        return postView;
    } //createPostView
}