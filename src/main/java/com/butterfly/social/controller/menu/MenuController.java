package com.butterfly.social.controller.menu;

import com.butterfly.social.SocialButterflyApplication;
import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserProfile;
import com.butterfly.social.view.MenuView;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Trophy;
import net.dean.jraw.references.OtherUserReference;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * A controller for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 22, 2021
 */
public final class MenuController {
    /**
     * The model of this menu controller.
     */
    private final Model model;

    /**
     * The view of this menu controller.
     */
    private final View view;

    /**
     * The Reddit post controller of this menu controller.
     */
    private final RedditPostController redditPostController;

    /**
     * The Twitter post controller of this menu controller.
     */
    private final TwitterPostController twitterPostController;

    /**
     * The Instagram post controller of this menu controller.
     */
    private final InstagramPostController instagramPostController;

    /**
     * Constructs a newly allocated {@code MenuController} object with the specified model, view, Reddit post
     * controller, Twitter post controller, and Instagram post controller.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @param redditPostController the Reddit post controller to be used in construction
     * @param twitterPostController the Twitter post controller to be used in construction
     * @param instagramPostController the Instagram post controller to be used in construction
     * @throws NullPointerException if the specified model, view, Reddit post controller, Twitter post controller, or
     * Instagram post controller is {@code null}
     */
    private MenuController(Model model, View view, RedditPostController redditPostController,
                           TwitterPostController twitterPostController,
                           InstagramPostController instagramPostController) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        Objects.requireNonNull(redditPostController, "the specified Reddit post controller is null");

        Objects.requireNonNull(twitterPostController, "the specified Twitter post controller is null");

        Objects.requireNonNull(instagramPostController, "the specified Instagram post controller is null");

        this.model = model;

        this.view = view;

        this.redditPostController = redditPostController;

        this.twitterPostController = twitterPostController;

        this.instagramPostController = instagramPostController;
    } //MenuController

    /**
     * Attempts to log the user into their Reddit account.
     */
    private void logInToReddit() {
        RedditModel redditModel;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        redditModel = this.model.getRedditModel();

        if (redditModel != null) {
            Alert alert;
            String message = "You are already signed into Reddit!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        redditModel = SocialButterflyApplication.getRedditModel();

        if (redditModel == null) {
            Alert alert;
            String message = "You could not be signed into Reddit! Please try again later.";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        this.model.setRedditModel(redditModel);

        executorService = this.redditPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.redditPostController::updatePosts, delay, period, TimeUnit.MINUTES);
    } //logInToReddit

    /**
     * Attempts to view the user's Reddit profile.
     */
    private void viewRedditProfile() {
        RedditModel redditModel;
        Alert alert;
        String handle;
        RedditClient client;
        OtherUserReference reference;
        String name;
        Account account;
        int karma = 0;
        List<Trophy> trophies;
        StringBuilder stringBuilder;
        String trophyString;
        String profileText;
        String title = "Social Butterfly";
        String headerText = "Reddit Profile";

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            String message = "You are not signed into Reddit!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        handle = redditModel.getUsername();

        client = redditModel.getClient();

        reference = client.user(handle);

        name = reference.query()
                        .getName();

        account = reference.query().getAccount();

        if (account != null) {
            karma += account.getLinkKarma();

            karma += account.getCommentKarma();
        } //end if

        trophies = reference.trophies();

        stringBuilder = new StringBuilder();

        trophies.forEach(trophy -> {
            String trophyName;

            trophyName = trophy.getFullName();

            stringBuilder.append(trophyName);

            stringBuilder.append(",\n");
        });

        if (stringBuilder.isEmpty()) {
            trophyString = "None";
        } else {
            int length;
            int startIndex;

            length = stringBuilder.length();

            startIndex = length - 2;

            stringBuilder.delete(startIndex, length);

            trophyString = stringBuilder.toString();
        } //end if

        profileText = """
                      Name: %s
                      Handle: %s
                      Karma: %d
                      Trophies: %s""".formatted(name, handle, karma, trophyString);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(headerText);

        alert.setContentText(profileText);

        alert.show();
    } //viewRedditProfile

    /**
     * Attempts to log the user into their Twitter account.
     */
    private void logInToTwitter() {
        TwitterModel twitterModel;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        twitterModel = this.model.getTwitterModel();

        if (twitterModel != null) {
            Alert alert;
            String message = "You are already signed into Twitter!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        twitterModel = SocialButterflyApplication.getTwitterModel();

        if (twitterModel == null) {
            Alert alert;
            String message = "You could not be signed into Twitter! Please try again later.";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        this.model.setTwitterModel(twitterModel);

        executorService = this.twitterPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.twitterPostController::updatePosts, delay, period, TimeUnit.MINUTES);
    } //logInToTwitter

    /**
     * Attempts to view the user's Twitter profile.
     */
    private void viewTwitterProfile() {
        TwitterModel twitterModel;
        Alert alert;
        TwitterUserProfile profile;
        String name;
        String handle;
        String bio;
        int followerCount;
        int followingCount;
        boolean verified;
        String location;
        String profileText;
        String title = "Social Butterfly";
        String headerText = "Twitter Profile";

        twitterModel = this.model.getTwitterModel();

        if (twitterModel == null) {
            String message = "You are not signed into Twitter!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        profile = twitterModel.getRequests()
                              .getProfile();

        name = profile.getName();

        handle = profile.getHandle();

        bio = profile.getBio();

        followerCount = profile.getFollowerCount();

        followingCount = profile.getFollowingCount();

        verified = profile.isVerified();

        location = profile.getLocation();

        profileText = """
                      Name: %s
                      Handle: %s
                      Bio: %s
                      Followers: %d
                      Following: %d
                      Verified: %b
                      Location: %s""".formatted(name, handle, bio, followerCount, followingCount, verified, location);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(headerText);

        alert.setContentText(profileText);

        alert.show();
    } //viewTwitterProfile

    /**
     * Attempts to log the user into their Instagram account.
     */
    private void logInToInstagram() {
        InstagramModel instagramModel;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel != null) {
            Alert alert;
            String message = "You are already signed into Instagram!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        instagramModel = SocialButterflyApplication.getInstagramModel();

        if (instagramModel == null) {
            Alert alert;
            String message = "You could not be signed into Instagram! Please try again later.";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        this.model.setInstagramModel(instagramModel);

        executorService = this.instagramPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.instagramPostController::updatePosts, delay, period,
                                            TimeUnit.MINUTES);
    } //logInToInstagram

    /**
     * Attempts to view the user's Instagram profile.
     */
    private void viewInstagramProfile() {
        InstagramModel instagramModel;
        Alert alert;
        String name;
        String handle;
        boolean verified;
        boolean privateProfile;
        String profileText;
        String title = "Social Butterfly";
        String headerText = "Instagram Profile";

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            String message = "You are not signed into Instagram!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        name = instagramModel.getFullName();

        handle = instagramModel.getUsername();

        verified = instagramModel.isVerified();

        privateProfile = instagramModel.isPrivate();

        profileText = """
                      Name: %s
                      Handle: %s
                      Verified: %b
                      Private: %b""".formatted(name, handle, verified, privateProfile);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(headerText);

        alert.setContentText(profileText);

        alert.show();
    } //viewInstagramProfile

    /**
     * Edits the user's Instagram bio.
     */
    private void editInstagramBio() {
        InstagramModel instagramModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter your new bio ";
        String newBio;
        TextInputDialog bioInputDialog;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            String message = "You are not signed into Instagram!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        bioInputDialog = new TextInputDialog();

        bioInputDialog.setTitle(title);

        bioInputDialog.setHeaderText(headerText);

        bioInputDialog.showAndWait();

        newBio = bioInputDialog.getResult();

        if (newBio != null) {
            instagramModel.setBio(newBio);
        }
    } //editInstagramBio

    /**
     * Attempts to search for other users on Instagram.
     */
    private void searchUsersOnInstagram() {
        InstagramModel instagramModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter a username to search for ";
        String resultHeaderText = "Usernames found similar to ";
        String searchUser;
        String searchResults = "";
        TextInputDialog userInputDialog;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            String message = "You are not signed into Instagram!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        userInputDialog = new TextInputDialog();

        userInputDialog.setTitle(title);

        userInputDialog.setHeaderText(headerText);

        userInputDialog.showAndWait();

        searchUser = userInputDialog.getResult();

        if (searchUser.isBlank() || searchUser.isEmpty()) {
            return;
        }

        UsersSearchResponse searchResponse = null;
        try {
            searchResponse = instagramModel.searchForUsers(searchUser).get();
        } catch (InterruptedException e) {
            resultHeaderText = "No results found";
            e.printStackTrace();
        } catch (ExecutionException e) {
            resultHeaderText = "No results found";
            e.printStackTrace();
        }

        if (searchResponse != null) {
            for (int i = 0; (i < 10) && (i < searchResponse.getNum_results()); i++) {
                searchResults += searchResponse.getUsers().get(i).getUsername() + "\n";
            }
        }

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        resultHeaderText += searchUser;

        alert.setHeaderText(resultHeaderText);

        alert.setContentText(searchResults);

        alert.show();
    } //searchUsersOnInstagram

    /**
     * Allows users
     */
    private void setInstagramProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        InstagramModel instagramModel;

        instagramModel = this.model.getInstagramModel();

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            instagramModel.setProfilePicture(selectedFile);
        }
    }

    /**
     * Switches the user interface to light mode.
     */
    private void switchToLightMode() {
        Scene scene;

        scene = this.view.getScene();

        scene.getStylesheets()
             .clear();
    } //switchToLightMode

    /**
     * Switches the user interface to dark mode.
     */
    private void switchToDarkMode() {
        String fileName = "dark-theme.css";
        URL url;
        String externalForm;
        Scene scene;

        url = MenuController.class.getResource(fileName);

        if (url == null) {
            Alert alert;
            String message = "Dark mode could not be enabled! Please try again later.";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        externalForm = url.toExternalForm();

        scene = this.view.getScene();

        scene.getStylesheets()
             .clear();

        scene.getStylesheets()
             .add(externalForm);
    } //switchToDarkMode

    /**
     * Switches the user interface to use a tab pane.
     */
    private void switchToTabPane() {
        VBox mainBox;
        PostView postView;
        TabPane tabPane;
        boolean contains;
        SplitPane splitPane;
        VBox redditBox;
        VBox twitterBox;
        VBox instagramBox;
        VBox allBox;
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
        Scene scene;
        MenuView menuView;
        MenuBar menuBar;

        mainBox = this.view.getMainBox();

        postView = this.view.getPostView();

        tabPane = postView.getTabPane();

        contains = mainBox.getChildren()
                          .contains(tabPane);

        if (contains) {
            return;
        } //end if

        mainBox.getChildren()
               .clear();

        splitPane = postView.getSplitPane();

        splitPane.getItems()
                 .clear();

        redditBox = postView.getRedditBox();

        twitterBox = postView.getTwitterBox();

        instagramBox = postView.getInstagramBox();

        allBox = postView.getAllBox();

        redditScrollPane = new ScrollPane(redditBox);

        twitterScrollPane = new ScrollPane(twitterBox);

        instagramScrollPane = new ScrollPane(instagramBox);

        allScrollPane = new ScrollPane(allBox);

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

        scene = this.view.getScene();

        tabPane.getTabs()
               .addAll(redditTab, twitterTab, instagramTab, allTab);

        tabPane.prefWidthProperty()
               .bind(scene.widthProperty());

        tabPane.prefHeightProperty()
               .bind(scene.heightProperty());

        menuView = this.view.getMenuView();

        menuBar = menuView.getMenuBar();

        mainBox.getChildren()
               .addAll(menuBar, tabPane);
    } //switchToTabPane

    /**
     * Switches the user interface to use a split pane.
     */
    private void switchToSplitPane() {
        VBox mainBox;
        PostView postView;
        SplitPane splitPane;
        boolean contains;
        TabPane tabPane;
        VBox redditBox;
        VBox twitterBox;
        VBox instagramBox;
        ScrollPane redditScrollPane;
        ScrollPane twitterScrollPane;
        ScrollPane instagramScrollPane;
        double position0;
        double position1;
        Scene scene;
        MenuView menuView;
        MenuBar menuBar;

        mainBox = this.view.getMainBox();

        postView = this.view.getPostView();

        splitPane = postView.getSplitPane();

        contains = mainBox.getChildren()
                          .contains(splitPane);

        if (contains) {
            return;
        } //end if

        mainBox.getChildren()
               .clear();

        tabPane = postView.getTabPane();

        tabPane.getTabs()
               .clear();

        redditBox = postView.getRedditBox();

        twitterBox = postView.getTwitterBox();

        instagramBox = postView.getInstagramBox();

        redditScrollPane = new ScrollPane(redditBox);

        twitterScrollPane = new ScrollPane(twitterBox);

        instagramScrollPane = new ScrollPane(instagramBox);

        splitPane.getItems()
                 .addAll(redditScrollPane, twitterScrollPane, instagramScrollPane);

        position0 = 1.0 / 3.0;

        position1 = 2.0 / 3.0;

        splitPane.setDividerPositions(position0, position1);

        scene = this.view.getScene();

        splitPane.prefWidthProperty()
                 .bind(scene.widthProperty());

        splitPane.prefHeightProperty()
                 .bind(scene.heightProperty());

        menuView = this.view.getMenuView();

        menuBar = menuView.getMenuBar();

        mainBox.getChildren()
               .addAll(menuBar, splitPane);
    } //switchToSplitPane

    /**
     * Creates, and returns, a {@code MenuController} object using the specified model, view, Reddit post controller,
     * Twitter post controller, and Instagram post controller.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @param redditPostController the Reddit post controller to be used in construction
     * @param twitterPostController the Twitter post controller to be used in construction
     * @param instagramPostController the Instagram post controller to be used in construction
     * @return a {@code MenuController} object using the specified model, view, Reddit post controller, Twitter post
     * controller, and Instagram post controller
     * @throws NullPointerException if the specified model, view, Reddit post controller, Twitter post controller, or
     * Instagram post controller is {@code null}
     */
    public static MenuController createMenuController(Model model, View view,
                                                      RedditPostController redditPostController,
                                                      TwitterPostController twitterPostController,
                                                      InstagramPostController instagramPostController) {
        MenuController controller;
        MenuView menuView;
        MenuItem redditLogInMenuItem;
        MenuItem redditProfileMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem instagramLogInMenuItem;
        MenuItem instagramProfileMenuItem;
        MenuItem instagramBioMenuItem;
        MenuItem instagramSearchMenuItem;
        MenuItem instagramProfilePictureItem;
        RadioMenuItem lightRadioMenuItem;
        RadioMenuItem darkRadioMenuItem;
        RadioMenuItem tabRadioMenuItem;
        RadioMenuItem splitRadioMenuItem;

        controller = new MenuController(model, view, redditPostController, twitterPostController,
                                        instagramPostController);

        menuView = controller.view.getMenuView();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        instagramBioMenuItem = menuView.getInstagramBioMenuItem();

        instagramSearchMenuItem = menuView.getInstagramSearchMenuItem();

        instagramProfilePictureItem = menuView.getInstagramProfilePictureMenuItem();

        lightRadioMenuItem = menuView.getLightRadioMenuItem();

        darkRadioMenuItem = menuView.getDarkRadioMenuItem();

        tabRadioMenuItem = menuView.getTabRadioMenuItem();

        splitRadioMenuItem = menuView.getSplitRadioMenuItem();

        redditLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToReddit());

        redditProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewRedditProfile());

        twitterLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToTwitter());

        twitterProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewTwitterProfile());

        instagramLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToInstagram());

        instagramProfileMenuItem.addEventHandler(ActionEvent.ACTION,
                                                 (actionEvent) -> controller.viewInstagramProfile());

        instagramBioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.editInstagramBio());

        instagramSearchMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.searchUsersOnInstagram());

        instagramProfilePictureItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.setInstagramProfilePicture());

        lightRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToLightMode());

        darkRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToDarkMode());

        tabRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToTabPane());

        splitRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToSplitPane());

        return controller;
    } //createMenuController
}