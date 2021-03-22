package com.butterfly.social.view;

import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * A view of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 22, 2021
 */
public final class View {
    /**
     * The main box of this view.
     */
    private final VBox mainBox;

    /**
     * The scene of this view.
     */
    private final Scene scene;

    /**
     * The menu view of this view.
     */
    private final MenuView menuView;

    /**
     * The post view of this view.
     */
    private final PostView postView;

    /**
     * Constructs a newly allocated {@code View} object with the specified primary stage.
     *
     * @param primaryStage the primary stage to be used in construction
     * @throws NullPointerException if the specified primary stage is {@code null}
     */
    private View(Stage primaryStage) {
        Objects.requireNonNull(primaryStage, "the specified primary stage is null");

        this.mainBox = new VBox();

        this.scene = new Scene(this.mainBox);

        this.menuView = MenuView.createMenuView();

        this.postView = PostView.createPostView(primaryStage, this.scene);
    } //View

    /**
     * Returns the main box of this view.
     *
     * @return the main box of this view
     */
    public VBox getMainBox() {
        return this.mainBox;
    } //getMainBox

    /**
     * Returns the scene of this view.
     *
     * @return the scene of this view
     */
    public Scene getScene() {
        return this.scene;
    } //getScene

    /**
     * Returns the menu view of this view.
     *
     * @return the menu view of this view
     */
    public MenuView getMenuView() {
        return this.menuView;
    } //getMenuView

    /**
     * Returns the post view of this view.
     *
     * @return the post view of this view
     */
    public PostView getPostView() {
        return this.postView;
    } //getPostView

    /**
     * Creates, and returns, a {@code View} object.
     *
     * @return a {@code View} object
     */
    public static View createView(Stage primaryStage) {
        View view;
        MenuBar menuBar;
        TabPane tabPane;

        view = new View(primaryStage);

        menuBar = view.menuView.getMenuBar();

        tabPane = view.postView.getTabPane();

        view.mainBox.getChildren()
                    .addAll(menuBar, tabPane);

        return view;
    } //createView
}