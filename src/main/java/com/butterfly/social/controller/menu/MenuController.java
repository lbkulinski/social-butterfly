package com.butterfly.social.controller.menu;

import com.butterfly.social.SocialButterflyApplication;
import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.MultiPost;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.instagram.InstagramUserRequests;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.reddit.RedditUserRequests;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserProfile;
import com.butterfly.social.view.MenuView;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.*;
import net.dean.jraw.references.OtherUserReference;
import java.io.File;
import net.dean.jraw.models.Message;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import twitter4j.DirectMessage;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A controller for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version April 18, 2021
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
        MenuView menuView;
        Menu redditMenu;
        Menu allMenu;
        MenuItem redditProfileMenuItem;
        MenuItem redditSavedPostsMenuItem;
        MenuItem redditUpvotedPostsMenuItem;
        MenuItem redditBlockedUsersMenuItem;
        MenuItem redditMessagesMenuItem;
        MenuItem redditFollowUserMenuItem;
        MenuItem redditFollowSubredditMenuItem;
        MenuItem redditUnfollowSubredditMenuItem;
        MenuItem redditBlockUserMenuItem;
        MenuItem redditLogOutMenuItem;
        MenuItem allLikedPostsMenuItem;
        MenuItem allSavedPostsRadioMenuItem;
        MenuItem multiPostMenuItem;
        MenuItem redditDirectMessageMenuItem;
        MenuItem redditPostMenuItem;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        redditModel = this.model.getRedditModel();

        if (redditModel != null) {
            throw new IllegalStateException("the user is already logged into Reddit");
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

        menuView = this.view.getMenuView();

        redditMenu = menuView.getRedditMenu();

        allMenu = menuView.getAllMenu();

        redditPostMenuItem = menuView.getRedditPostMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();

        redditSavedPostsMenuItem = menuView.getRedditSavedPostsMenuItem();

        redditBlockedUsersMenuItem = menuView.getRedditBlockedUsersMenuItem();

        redditMessagesMenuItem = menuView.getRedditMessagesMenuItem();

        redditFollowUserMenuItem = menuView.getRedditFollowUserMenuItem();

        redditFollowSubredditMenuItem = menuView.getRedditFollowSubredditMenuItem();

        redditUnfollowSubredditMenuItem = menuView.getRedditUnfollowSubredditMenuItem();

        redditBlockUserMenuItem = menuView.getRedditBlockUserMenuItem();

        redditLogOutMenuItem = menuView.getRedditLogOutMenuItem();

        allSavedPostsRadioMenuItem = menuView.getAllSavedPostsRadioMenuItem();

        multiPostMenuItem = menuView.getMultiPostMenuItem();

        redditDirectMessageMenuItem = menuView.getRedditSendMessageMenuItem();

        redditMenu.getItems()
                  .clear();

        redditMenu.getItems()
                  .addAll(redditProfileMenuItem,
                          new SeparatorMenuItem(), redditSavedPostsMenuItem, new SeparatorMenuItem(),
                          redditMessagesMenuItem, new SeparatorMenuItem(), redditFollowUserMenuItem,
                          new SeparatorMenuItem(), redditFollowSubredditMenuItem, new SeparatorMenuItem(),
                          redditUnfollowSubredditMenuItem, new SeparatorMenuItem(), redditBlockUserMenuItem,
                          new SeparatorMenuItem(), redditBlockedUsersMenuItem, new SeparatorMenuItem(),
                          redditDirectMessageMenuItem, new SeparatorMenuItem(), redditPostMenuItem,
                          new SeparatorMenuItem(), redditLogOutMenuItem);

        allMenu.getItems()
               .clear();

        allMenu.getItems()
               .addAll(allSavedPostsRadioMenuItem, new SeparatorMenuItem(), multiPostMenuItem);

        executorService = this.redditPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.redditPostController::updatePosts, delay, period, TimeUnit.MINUTES);
    } //logInToReddit

    /**
     * Attempts to log the user out of their Reddit account.
     */
    private void logOutOfReddit() {
        RedditModel redditModel;
        TwitterModel twitterModel;
        InstagramModel instagramModel;
        MenuView menuView;
        Menu redditMenu;
        Menu allMenu;
        MenuItem redditLogInMenuItem;

        redditModel = this.model.getRedditModel();

        twitterModel = this.model.getTwitterModel();

        instagramModel = this.model.getInstagramModel();

        if (redditModel == null) {
            throw new IllegalStateException("the user is not logged into Reddit");
        } //end if

        this.model.setRedditModel(null);

        this.redditPostController.reset();

        menuView = this.view.getMenuView();

        redditMenu = menuView.getRedditMenu();

        allMenu = menuView.getAllMenu();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditMenu.getItems()
                  .clear();

        redditMenu.getItems()
                  .add(redditLogInMenuItem);

        if ((twitterModel == null) && (instagramModel == null)) {
            allMenu.getItems()
                   .clear();
        } //end if
    } //logOutOfReddit

    /**
     * Attempts to view the user's Reddit profile.
     */
    private void viewRedditProfile() {
        RedditModel redditModel;
        Alert alert;
        Popup profile;
        String handle;
        RedditClient client;
        OtherUserReference reference;
        String name;
        Account account;
        int karma = 0;
        List<Trophy> trophies;
        List<ImageView> trophyURLs;
        StringBuilder stringBuilder;
        String trophyString;
        Text profileText;
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

        trophyURLs = new ArrayList<>();

        trophies.forEach(trophy -> {
            String trophyName;

            trophyName = trophy.getFullName();

            stringBuilder.append(trophyName);

            stringBuilder.append(",\n");

            if (trophy.getIcon70() != null) {
                System.out.println("hey: " + trophy.getIcon70());
                trophyURLs.add(new ImageView(new Image(trophy.getIcon70())));
            }
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

        profileText = new Text("""
                      Name: %s
                      
                      Handle: %s
                      
                      Karma: %d
                      
                      Trophies: %s""".formatted(name, handle, karma, trophyString));

        /* alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(headerText);

        alert.setContentText(profileText);

        alert.show(); */
        Stage redditProfile = new Stage();
        redditProfile.setTitle("Reddit Profile");
        VBox vBox = new VBox(profileText);
        vBox.getChildren().addAll(trophyURLs);
        Scene scene = new Scene(vBox,500,500);
        redditProfile.setScene(scene);
        redditProfile.show();
    } //viewRedditProfile

    /**
     * Attempts to follow a reddit user specified by the user logged in
     */
    private void followRedditUser() {
        RedditModel redditModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to follow ";
        String searchUser;
        TextInputDialog userInputDialog;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            String message = "You are not signed into Reddit!";

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

        boolean followed = redditModel.followRedditUser(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if (followed == false) {
            alert.setHeaderText("There was a problem following that user");
        }
        else {
            alert.setHeaderText("Successfully followed!");
        }
        alert.show();
    }

    /**
     * Attempts to follow a reddit subreddit specified by the user logged in
     */
    private void followSubreddit() {
        RedditModel redditModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the name of the subreddit you wish to follow ";
        String searchUser;
        TextInputDialog userInputDialog;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            String message = "You are not signed into Reddit!";

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

        boolean followed = redditModel.followSubreddit(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if (followed == false) {
            alert.setHeaderText("There was a problem following that subreddit");
        }
        else {
            alert.setHeaderText("Successfully followed!");
        }
        alert.show();
    }

        /**
     * Attempts to unfollow a reddit subreddit specified by the user logged in
     */
    private void unfollowSubreddit() {
        RedditModel redditModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the name of the subreddit you wish to unfollow ";
        String searchUser;
        TextInputDialog userInputDialog;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            String message = "You are not signed into Reddit!";

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

        boolean followed = redditModel.unfollowSubreddit(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if (followed == false) {
            alert.setHeaderText("There was a problem unfollowing that subreddit");
        }
        else {
            alert.setHeaderText("Successfully unfollowed!");
        }
        alert.show();

        redditPostController.updateAll = true;
        redditPostController.updatePosts();
    }

    /**
     * Attempts to log the user into their Twitter account.
     */
    private void logInToTwitter() {
        TwitterModel twitterModel;
        MenuView menuView;
        Menu twitterMenu;
        Menu allMenu;
        MenuItem twitterTrendingMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem twitterMessagesMenuItem;
        MenuItem twitterDirectMessageMenuItem;
        MenuItem twitterSavedPostsMenuItem;
        MenuItem twitterLikedPostsMenuItem;
        MenuItem twitterBlockedUsersMenuItem;
        MenuItem twitterFollowUserMenuItem;
        MenuItem twitterBlockUserMenuItem;
        MenuItem twitterLogOutMenuItem;
        MenuItem allSavedPostsRadioMenuItem;
        MenuItem multiPostMenuItem;
        MenuItem twitterPostMenuItem;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        twitterModel = this.model.getTwitterModel();

        if (twitterModel != null) {
            throw new IllegalStateException("the user is already logged into Twitter");
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

        menuView = this.view.getMenuView();

        twitterMenu = menuView.getTwitterMenu();

        allMenu = menuView.getAllMenu();

        twitterPostMenuItem = menuView.getTwitterPostMenuItem();

        twitterTrendingMenuItem = menuView.getTwitterTrendingMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        twitterMessagesMenuItem = menuView.getTwitterMessagesMenuItem();

        twitterDirectMessageMenuItem = menuView.getTwitterSendMessagesmenuItem();

        twitterSavedPostsMenuItem = menuView.getTwitterSavedPostsMenuItem();

        twitterLikedPostsMenuItem = menuView.getTwitterLikedPostsMenuItem();

        twitterBlockedUsersMenuItem = menuView.getTwitterBlockedUsersMenuItem();

        twitterFollowUserMenuItem = menuView.getTwitterFollowUserMenuItem();

        twitterBlockUserMenuItem = menuView.getTwitterBlockUserMenuItem();

        twitterLogOutMenuItem = menuView.getTwitterLogOutMenuItem();

        allSavedPostsRadioMenuItem = menuView.getAllSavedPostsRadioMenuItem();

        multiPostMenuItem = menuView.getMultiPostMenuItem();

        twitterMenu.getItems()
                   .clear();

        twitterMenu.getItems()
                   .addAll(twitterProfileMenuItem, new SeparatorMenuItem(), twitterMessagesMenuItem,
                           new SeparatorMenuItem(), twitterLikedPostsMenuItem, new SeparatorMenuItem(),
                           twitterSavedPostsMenuItem, new SeparatorMenuItem(), twitterDirectMessageMenuItem,
                           new SeparatorMenuItem(), twitterFollowUserMenuItem, new SeparatorMenuItem(),
                           twitterTrendingMenuItem, new SeparatorMenuItem(),twitterBlockUserMenuItem,
                           new SeparatorMenuItem(), twitterBlockedUsersMenuItem, new SeparatorMenuItem(),
                           twitterPostMenuItem, new SeparatorMenuItem(), twitterLogOutMenuItem);

        allMenu.getItems()
               .clear();

        allMenu.getItems()
               .addAll(allSavedPostsRadioMenuItem, new SeparatorMenuItem(), multiPostMenuItem);

        executorService = this.twitterPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.twitterPostController::updatePosts, delay, period, TimeUnit.MINUTES);
    } //logInToTwitter

    /**
     * Attempts to log the user out of their Reddit account.
     */
    private void logOutOfTwitter() {
        RedditModel redditModel;
        TwitterModel twitterModel;
        InstagramModel instagramModel;
        MenuView menuView;
        Menu twitterMenu;
        Menu allMenu;
        MenuItem twitterLogInMenuItem;
        Path path;
        String fileName = "twitter-model.ser";

        redditModel = this.model.getRedditModel();

        twitterModel = this.model.getTwitterModel();

        instagramModel = this.model.getInstagramModel();

        if (twitterModel == null) {
            throw new IllegalStateException("the user is not logged into Twitter");
        } //end if

        this.model.setTwitterModel(null);

        this.twitterPostController.reset();

        menuView = this.view.getMenuView();

        twitterMenu = menuView.getTwitterMenu();

        allMenu = menuView.getAllMenu();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterMenu.getItems()
                   .clear();

        twitterMenu.getItems()
                   .add(twitterLogInMenuItem);

        if ((redditModel == null) && (instagramModel == null)) {
            allMenu.getItems()
                   .clear();
        } //end if

        path = Path.of(fileName);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        } //end try catch
    } //logOutOfTwitter

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
     * Attempts to follow a user specified by the user logged in
     */

    private void followTwitterUser() {
        TwitterModel twitterModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to follow ";
        String searchUser;
        TextInputDialog userInputDialog;

        twitterModel = this.model.getTwitterModel();

        if (twitterModel == null) {
            String message = "You are not signed into Twitter!";

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
        boolean success = twitterModel.getRequests().followTwitterUser(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if(success == false) {
            alert.setHeaderText("Could not follow user");
        }
        else {
            alert.setHeaderText("User successfully followed!");
        }
        alert.show();
    }

    private void blockTwitterUser() {
        TwitterModel twitterModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to block ";
        String searchUser;
        TextInputDialog userInputDialog;

        twitterModel = this.model.getTwitterModel();

        if (twitterModel == null) {
            String message = "You are not signed into Twitter!";

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
        boolean success = twitterModel.getRequests().blockTwitterUser(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if(success == false) {
            alert.setHeaderText("Could not block user");
        }
        else {
            alert.setHeaderText("User successfully blocked!");
        }
        alert.show();

        twitterPostController.updateAll = true;
        twitterPostController.updatePosts();
    }

    private void blockRedditUser() {
        RedditModel redditModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to block ";
        String searchUser;
        TextInputDialog userInputDialog;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            String message = "You are not signed into Reddit!";

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

        boolean followed = redditModel.blockRedditUser(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if (followed == false) {
            alert.setHeaderText("There was a problem blocking that user");
        }
        else {
            alert.setHeaderText("Successfully blocked!");
        }
        alert.show();
    }

    private void blockInstagramUser() {
        InstagramModel instagramModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to block ";
        String searchUser;
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

        boolean followed = instagramModel.blockInstagramUser(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        if (followed == false) {
            alert.setHeaderText("There was a problem blocking that user");
        }
        else {
            alert.setHeaderText("Successfully blocked!");
        }
        alert.show();

        instagramPostController.updateAll = true;
        instagramPostController.updatePosts();
    }

    /**
     * Attempts to log the user into their Instagram account.
     */
    private void logInToInstagram() {
        InstagramModel instagramModel;
        MenuView menuView;
        Menu instagramMenu;
        Menu allMenu;
        MenuItem instagramProfileMenuItem;
        MenuItem instagramBioMenuItem;
        MenuItem instagramSearchMenuItem;
        MenuItem instagramProfilePictureItem;
        MenuItem instagramSavedPostsMenuItem;
        MenuItem instagramLikedPostsMenuItem;
        MenuItem instagramStoryItem;
        MenuItem instagramMessagesMenuItem;
        MenuItem instagramFollowUserMenuItem;
        MenuItem instagramLogOutMenuItem;
        MenuItem instagramBlockUserMenuItem;
        MenuItem allSavedPostsRadioMenuItem;
        MenuItem multiPostMenuItem;
        MenuItem instagramPostMenuItem;
        ScheduledExecutorService executorService;
        int delay = 0;
        int period = 1;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel != null) {
            throw new IllegalStateException("the user is already logged into Instagram");
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

        menuView = this.view.getMenuView();

        instagramMenu = menuView.getInstagramMenu();

        allMenu = menuView.getAllMenu();

        instagramPostMenuItem = menuView.getInstagramPostMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        instagramBioMenuItem = menuView.getInstagramBioMenuItem();

        instagramSearchMenuItem = menuView.getInstagramSearchMenuItem();

        instagramProfilePictureItem = menuView.getInstagramProfilePictureMenuItem();

        instagramStoryItem = menuView.getInstagramStoryMenuItem();

        instagramSavedPostsMenuItem = menuView.getInstagramSavedPostsMenuItem();

        instagramLikedPostsMenuItem = menuView.getInstagramLikedPostsMenuItem();

        instagramMessagesMenuItem = menuView.getInstagramMessagesMenuItem();

        instagramFollowUserMenuItem = menuView.getInstagramFollowUserMenuItem();

        instagramLogOutMenuItem = menuView.getInstagramLogOutMenuItem();

        instagramBlockUserMenuItem = menuView.getInstagramBlockUserMenuItem();

        allSavedPostsRadioMenuItem = menuView.getAllSavedPostsRadioMenuItem();

        multiPostMenuItem = menuView.getMultiPostMenuItem();

        instagramMenu.getItems()
                     .clear();

        instagramMenu.getItems()
                     .addAll(instagramProfileMenuItem, new SeparatorMenuItem(), instagramBioMenuItem,
                             new SeparatorMenuItem(), instagramSearchMenuItem, new SeparatorMenuItem(),
                             instagramProfilePictureItem, new SeparatorMenuItem(), instagramStoryItem,
                             new SeparatorMenuItem(), instagramLikedPostsMenuItem, new SeparatorMenuItem(),
                             instagramSavedPostsMenuItem, new SeparatorMenuItem(), instagramBlockUserMenuItem,
                             new SeparatorMenuItem(), instagramMessagesMenuItem, new SeparatorMenuItem(),
                             instagramFollowUserMenuItem, new SeparatorMenuItem(), instagramPostMenuItem,
                             new SeparatorMenuItem(), instagramLogOutMenuItem);


        allMenu.getItems()
               .clear();

        allMenu.getItems()
               .addAll(allSavedPostsRadioMenuItem, new SeparatorMenuItem(), multiPostMenuItem);

        executorService = this.instagramPostController.getExecutorService();

        executorService.scheduleAtFixedRate(this.instagramPostController::updatePosts, delay, period,
                                            TimeUnit.MINUTES);
    } //logInToInstagram

    /**
     * Attempts to log the user out of their Instagram account.
     */
    private void logOutOfInstagram() {
        RedditModel redditModel;
        TwitterModel twitterModel;
        InstagramModel instagramModel;
        MenuView menuView;
        Menu instagramMenu;
        Menu allMenu;
        MenuItem instagramLogInMenuItem;

        redditModel = this.model.getRedditModel();

        twitterModel = this.model.getTwitterModel();

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            throw new IllegalStateException("the user is not logged into Instagram");
        } //end if

        this.model.setInstagramModel(null);

        this.instagramPostController.reset();

        menuView = this.view.getMenuView();

        instagramMenu = menuView.getInstagramMenu();

        allMenu = menuView.getAllMenu();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramMenu.getItems()
                     .clear();

        instagramMenu.getItems()
                     .add(instagramLogInMenuItem);

        if ((redditModel == null) && (twitterModel == null)) {
            allMenu.getItems()
                   .clear();
        } //end if
    } //logOutOfInstagram

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

    public Scene getAllSavedPostsScene() {
        this.instagramPostController.updateSavedPosts();
        this.redditPostController.updateSavedPosts();
        this.twitterPostController.getSavedPosts();
        VBox temp = new VBox();
        if(this.instagramPostController.getAllSavedBox() != null) {
            temp.getChildren().addAll(this.instagramPostController.getAllSavedBox().getChildren());
        }
        if(this.redditPostController.getAllSavedBox() != null) {
            temp.getChildren().addAll(this.redditPostController.getAllSavedBox().getChildren());
        }
        if(this.twitterPostController.getAllSavedBox() != null) {
            temp.getChildren().addAll(this.twitterPostController.getAllSavedBox().getChildren());
        }

        return new Scene(temp, 500, 300);
    }

    /**
     * Allows users to set their profile picture on instagram
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
     * Allows users to make a post to their instagram story
     */
    private void makeInstagramStoryPost() {
        FileChooser fileChooser = new FileChooser();
        InstagramModel instagramModel;

        instagramModel = this.model.getInstagramModel();

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            instagramModel.makeStoryPost(selectedFile);
        }
    }

    public void showTwitterTrending() {
        List<String> list;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Trends");
        alert.setHeaderText("Top Trends on Twitter");
        StringBuilder sb = new StringBuilder("");

        TwitterModel twitterModel = this.model.getTwitterModel();
        try {
            list = twitterModel.getRequests().getTrending();
        } catch (TwitterException e) {
            return;
        }
        list.forEach(item -> sb.append(item).append("\n"));

        alert.setContentText(sb.toString());
        alert.show();
    }

    public void sendTwitterDirectMessage() {
        Optional<String> text;
        String username;
        String message;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Direct Message");
        dialog.setHeaderText("Username");
        dialog.setContentText("Enter the username of the user you want to message.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            username = text.get();
        } else {
            return;
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Message");
        dialog.setContentText("Enter the message.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            message = text.get();
        } else {
            return;
        }
        TwitterModel twitterModel = this.model.getTwitterModel();
        try {
            twitterModel.getRequests().sendDirectMessage(username, message);
        } catch (TwitterException e) {

        }
    }

    public void sendRedditDirectMessage() {
        Optional<String> text;
        String username;
        String message;
        String subject;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Direct Message");
        dialog.setHeaderText("Username");
        dialog.setContentText("Enter the username of the user you want to message.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            username = text.get();
        } else {
            return;
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Subject");
        dialog.setContentText("Enter a title for the message.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            subject = text.get();
        } else {
            return;
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Message");
        dialog.setContentText("Enter the message.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            message = text.get();
        } else {
            return;
        }
        RedditModel redditModel = this.model.getRedditModel();
        RedditUserRequests userRequests = new RedditUserRequests();
        userRequests.setRedditClient(redditModel.getClient());
        userRequests.sendPrivateMessage(username, subject, message);
    }

    public void makeInstagramPost() {
        File file;
        Optional<ButtonType> selection;

        ButtonType buttonOk = new ButtonType("Ok");
        ButtonType buttonCancel = new ButtonType("Cancel");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Media Selection");
        alert.setHeaderText(null);
        alert.getButtonTypes().setAll(buttonOk, buttonCancel);
        alert.setContentText("You will be prompted to select an image file for your post.");
        selection = alert.showAndWait();
        if (selection.get() != buttonOk) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        alert.setAlertType(Alert.AlertType.ERROR);

        Stage stage = new Stage();
        stage.setTitle("Instagram Post");
        stage.setResizable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label lblContent = new Label("Please enter the caption for your post.");

        TextArea textArea = new TextArea();
        Button postButton = new Button("Post");
        Button cancelButton = new Button("Cancel");
        ButtonBar buttonbar = new ButtonBar();
        buttonbar.setPadding(new Insets(10));
        buttonbar.getButtons().addAll(postButton, cancelButton);

        postButton.setOnAction(actionEvent -> {
            if (textArea.getText().length() > 2200) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Post Error");
                alert.setContentText("Your post exceeded the character limit for Instagram.");
                alert.showAndWait();
                return;
            }
            if (this.model.getInstagramModel() != null) {
                InstagramUserRequests instagramUserRequests = new InstagramUserRequests();
                instagramUserRequests.setIgClient(this.model.getInstagramModel().getClient());
                instagramUserRequests.post(file, textArea.getText());
                stage.close();
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        gridPane.add(lblContent, 0, 0);
        gridPane.add(textArea, 0, 1);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridPane, buttonbar);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void makeRedditPost() {
        Optional<String> text;
        String subreddit;
        String title;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Subreddit");
        dialog.setContentText("Please specify a subreddit for your post.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            subreddit = text.get();
        } else {
            return;
        }

        dialog.getEditor().clear();
        dialog.setTitle("Title");
        dialog.setContentText("Please specify a title for your post.");
        text = dialog.showAndWait();
        if (text.isPresent()) {
            title = text.get();
        } else {
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Reddit Post");
        stage.setResizable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label lblContent = new Label("Please enter the content of your post.");

        TextArea textArea = new TextArea();
        Button postButton = new Button("Post");
        Button cancelButton = new Button("Cancel");
        ButtonBar buttonbar = new ButtonBar();
        buttonbar.setPadding(new Insets(10));
        buttonbar.getButtons().addAll(postButton, cancelButton);

        postButton.setOnAction(actionEvent -> {
            if (this.model.getRedditModel() != null) {
                RedditUserRequests redditUserRequests = new RedditUserRequests();
                redditUserRequests.setRedditClient(this.model.getRedditModel().getClient());
                redditUserRequests.post(subreddit, title, textArea.getText());
                stage.close();
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        gridPane.add(lblContent, 0, 0);
        gridPane.add(textArea, 0, 1);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridPane, buttonbar);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void makeTwitterPost() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        Stage stage = new Stage();
        stage.setTitle("Twitter Post");
        stage.setResizable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label lblContent = new Label("Please enter the content of your post.");

        TextArea textArea = new TextArea();
        Button postButton = new Button("Post");
        Button cancelButton = new Button("Cancel");
        ButtonBar buttonbar = new ButtonBar();
        buttonbar.setPadding(new Insets(10));
        buttonbar.getButtons().addAll(postButton, cancelButton);

        postButton.setOnAction(actionEvent -> {
            if (textArea.getText().length() > 280) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Twitter Post Error");
                alert.setContentText("Your post exceeded the character limit for Twitter.");
                alert.showAndWait();
                return;
            }
            try {
                if (this.model.getTwitterModel() != null) {
                    this.model.getTwitterModel().getRequests().postTweet(new StatusUpdate(textArea.getText()));
                    stage.close();
                }
            } catch (TwitterException e) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Multi-Post Error");
                alert.setContentText("There was an error with your Twitter Post. Please try again.");
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            stage.close();
        });

        gridPane.add(lblContent, 0, 0);
        gridPane.add(textArea, 0, 1);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridPane, buttonbar);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    public void makeMultiPost() {
        Optional<ButtonType> selection;
        Optional<String> text;
        String subreddit = null;
        File file = null;
        String title =null;
        boolean postInstagram = false;
        boolean postTwitter = false;
        boolean postReddit = false;
        InstagramModel instagramModel = this.model.getInstagramModel();
        TwitterModel twitterModel = this.model.getTwitterModel();
        RedditModel redditModel= this.model.getRedditModel();

        ButtonType buttonYes = new ButtonType("Yes");
        ButtonType buttonNo = new ButtonType("No");
        ButtonType buttonOk = new ButtonType("Ok");
        ButtonType buttonCancel = new ButtonType("Cancel");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        if (instagramModel != null) {
            alert.setTitle("Instagram");
            alert.setHeaderText("Would you like add this post to your Instagram?");
            alert.getButtonTypes().setAll(buttonYes, buttonNo);
            selection = alert.showAndWait();
            if (selection.get() == buttonYes) {
                postInstagram = true;
            } else if (selection.get() == buttonNo) {
                postInstagram = false;
            } else {
                return;
            }
        }

        if (redditModel != null) {
            alert.setTitle("Reddit");
            alert.setHeaderText("Would you like add this post to your Reddit?");
            alert.getButtonTypes().setAll(buttonYes, buttonNo);
            selection = alert.showAndWait();
            if (selection.get() == buttonYes) {
                postReddit = true;
            } else if (selection.get() == buttonNo) {
                postReddit = false;
            } else {
                return;
            }
        }

        if (twitterModel != null) {
            alert.setTitle("Twitter");
            alert.setHeaderText("Would you like add this post to your Twitter?");
            alert.getButtonTypes().setAll(buttonYes, buttonNo);
            selection = alert.showAndWait();
            if (selection.get() == buttonYes) {
                postTwitter = true;
            } else if (selection.get() == buttonNo) {
                postTwitter = false;
            } else {
                return;
            }
        }

        TextInputDialog dialog = new TextInputDialog();
        if (postReddit) {
            dialog.setTitle("Subreddit");
            dialog.setContentText("Please specify a subreddit for your post.");
            text = dialog.showAndWait();
            if (text.isPresent()) {
                subreddit = text.get();
            } else {
                return;
            }
        }

        if (postReddit) {
            dialog.getEditor().clear();
            dialog.setTitle("Title");
            dialog.setContentText("Please specify a title for your post.");
            text = dialog.showAndWait();
            if (text.isPresent()) {
                title = text.get();
            } else {
                return;
            }
        }

        if (postInstagram) {
            alert.setTitle("Media Selection");
            alert.setHeaderText(null);
            alert.getButtonTypes().setAll(buttonOk, buttonCancel);
            alert.setContentText("You will be prompted to select an image file for your post.");
            selection = alert.showAndWait();
            if (selection.get() != buttonOk) {
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
            file = fileChooser.showOpenDialog(null);
            if (file == null) {
                return;
            }
        }

        Stage stage = new Stage();
        stage.setTitle("Multi Post");
        stage.setResizable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        Label lblContent = new Label("Please enter the content of your post.");

        TextArea textArea = new TextArea();
        Button postButton = new Button("Post");
        Button cancelButton = new Button("Cancel");
        ButtonBar buttonbar = new ButtonBar();
        buttonbar.setPadding(new Insets(10));
        buttonbar.getButtons().addAll(postButton, cancelButton);

        File finalFile = file;
        boolean finalPostTwitter = postTwitter;
        boolean finalPostInstagram = postInstagram;
        boolean finalPostReddit = postReddit;
        String finalTitle = title;
        String finalSubreddit = subreddit;

        alert.getButtonTypes().setAll(buttonOk);
        alert.setAlertType(Alert.AlertType.ERROR);
        postButton.setOnAction(actionEvent -> {
            if (this.model.getTwitterModel() != null && textArea.getText().length() > 280) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Multi-Post Error");
                alert.setContentText("Your post exceeded the character limit for Twitter.");
                alert.showAndWait();
                return;
            }
            if (this.model.getInstagramModel() != null && textArea.getText().length() >2200) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Multi-Post Error");
                alert.setContentText("Your post exceeded the character limit for Instagram.");
                alert.showAndWait();
                return;
            }
            MultiPost multiPost= new MultiPost(this.model);
            try {
                multiPost.createPost(finalPostTwitter,
                        finalPostInstagram,
                        finalPostReddit,
                        textArea.getText(),
                        finalTitle,
                        finalFile,
                        finalSubreddit);
                stage.close();
            } catch (TwitterException e) {
                stage.close();
                alert.setTitle("Error");
                alert.setHeaderText("Multi-Post Error");
                alert.setContentText("There was an error with your Multi-Post. Please try again.");
                alert.showAndWait();
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            stage.close();
            return;
        });

        gridPane.add(lblContent, 0, 0);
        gridPane.add(textArea, 0, 1);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(gridPane, buttonbar);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void followInstagramUser() {
        InstagramModel instagramModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Enter the username of the account you wish to follow ";
        String searchResults = "";
        String searchUser;
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (searchResponse != null) {
            for (int i = 0; (i < 10) && (i < searchResponse.getNum_results()); i++) {
                searchResults += searchResponse.getUsers().get(i).getUsername() + "\n";
            }
        }
        if(!searchResponse.toString().contains(searchUser)) {
            String message = "There was a problem following that user";
            alert = new Alert(Alert.AlertType.ERROR, message);
            alert.show();
            return;
        }

        instagramModel.searchForUsers(searchUser);

        instagramModel.followInstagramProfile(searchUser);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText("Successfully followed!");

        alert.show();
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
     * Updates the font size of the posts to the specified size.
     *
     * @param size the size to be used in the operation
     */
    private void updateFontSize(int size) {
        PostView postView;
        VBox redditBox;
        VBox twitterBox;
        VBox instagramBox;
        VBox allBox;
        String family = "Tahoma";

        postView = this.view.getPostView();

        redditBox = postView.getRedditBox();

        twitterBox = postView.getTwitterBox();

        instagramBox = postView.getInstagramBox();

        allBox = postView.getAllBox();

        for (Node node0 : redditBox.getChildren()) {
            if (node0 instanceof VBox) {
                for (Node node1 : ((VBox) node0).getChildren()) {
                    if (node1 instanceof Label) {
                        ((Label) node1).setFont(Font.font(family, FontWeight.BOLD, size));
                    } else if (node1 instanceof Text) {
                        ((Text) node1).setFont(Font.font(family, size));
                    } else if (node1 instanceof Accordion) {
                        for (TitledPane titledPane : ((Accordion) node1).getPanes()) {
                            titledPane.setFont(Font.font(family, size));
                        } //end for
                    } //end if
                } //end for
            } //end if
        } //end for

        for (Node node0 : twitterBox.getChildren()) {
            if (node0 instanceof VBox) {
                for (Node node1 : ((VBox) node0).getChildren()) {
                    if (node1 instanceof Label) {
                        ((Label) node1).setFont(Font.font(family, FontWeight.BOLD, size));
                    } else if (node1 instanceof Text) {
                        ((Text) node1).setFont(Font.font(family, size));
                    } else if (node1 instanceof Accordion) {
                        for (TitledPane titledPane : ((Accordion) node1).getPanes()) {
                            titledPane.setFont(Font.font(family, size));
                        } //end for
                    } //end if
                } //end for
            } //end if
        } //end for

        for (Node node0 : instagramBox.getChildren()) {
            if (node0 instanceof VBox) {
                for (Node node1 : ((VBox) node0).getChildren()) {
                    if (node1 instanceof Label) {
                        ((Label) node1).setFont(Font.font(family, FontWeight.BOLD, size));
                    } else if (node1 instanceof Text) {
                        ((Text) node1).setFont(Font.font(family, size));
                    } else if (node1 instanceof Accordion) {
                        for (TitledPane titledPane : ((Accordion) node1).getPanes()) {
                            titledPane.setFont(Font.font(family, size));
                        } //end for
                    } //end if
                } //end for
            } //end if
        } //end for

        for (Node node0 : allBox.getChildren()) {
            if (node0 instanceof VBox) {
                for (Node node1 : ((VBox) node0).getChildren()) {
                    if (node1 instanceof Label) {
                        ((Label) node1).setFont(Font.font(family, FontWeight.BOLD, size));
                    } else if (node1 instanceof Text) {
                        ((Text) node1).setFont(Font.font(family, size));
                    } else if (node1 instanceof Accordion) {
                        for (TitledPane titledPane : ((Accordion) node1).getPanes()) {
                            titledPane.setFont(Font.font(family, size));
                        } //end for
                    } //end if
                } //end for
            } //end if
        } //end for
    } //updateFontSize

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
        MenuItem redditFollowUserMenuItem;
        MenuItem redditLogOutMenuItem;
        MenuItem redditBlockUserMenuItem;
        MenuItem twitterFollowUserMenuItem;
        MenuItem twitterBlockUserMenuItem;
        MenuItem redditFollowSubredditMenuItem;
        MenuItem redditUnfollowSubredditMenuItem;
        MenuItem redditSavedPostsMenuItem;
        MenuItem redditMessagesMenuItem;
        MenuItem twitterTrendingMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterLogOutMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem twitterMessagesMenuItem;
        MenuItem twitterDirectMessageMenuItem;
        MenuItem redditDirectMessageMenuItem;
        MenuItem twitterSavedPostsMenuItem;
        MenuItem twitterLikedPostsMenuItem;
        MenuItem twitterBlockedUsersMenuItem;
        MenuItem redditBlockedUsersMenuItem;
        MenuItem instagramLogInMenuItem;
        MenuItem instagramLogOutMenuItem;
        MenuItem instagramProfileMenuItem;
        MenuItem instagramBioMenuItem;
        MenuItem instagramSearchMenuItem;
        MenuItem instagramProfilePictureItem;
        MenuItem instagramPostMenuItem;
        MenuItem twitterPostMenuItem;
        MenuItem redditPostMenuItem;
        MenuItem instagramStoryItem;
        MenuItem instagramFollowUserMenuItem;
        MenuItem instagramSavedPostsMenuItem;
        MenuItem instagramBlockUserMenuItem;
        MenuItem multiPostMenuItem;
        RadioMenuItem timeSortRadioMenuItem;
        RadioMenuItem popularitySortRadioMenuItem;
        RadioMenuItem lightRadioMenuItem;
        RadioMenuItem darkRadioMenuItem;
        RadioMenuItem tabRadioMenuItem;
        RadioMenuItem splitRadioMenuItem;
        Spinner<Integer> fontSizeSpinner;
        MenuItem allSavedPostsRadioMenuItem;
        MenuItem allLikedPostsMenuItem;
        TwitterModel twitterModel0;
        Menu twitterMenu;
        Menu allMenu;

        controller = new MenuController(model, view, redditPostController, twitterPostController,
                                        instagramPostController);

        menuView = controller.view.getMenuView();

        twitterPostMenuItem = menuView.getTwitterPostMenuItem();

        instagramPostMenuItem = menuView.getInstagramPostMenuItem();

        redditPostMenuItem = menuView.getRedditPostMenuItem();

        twitterDirectMessageMenuItem = menuView.getTwitterSendMessagesmenuItem();

        twitterTrendingMenuItem = menuView.getTwitterTrendingMenuItem();

        redditDirectMessageMenuItem = menuView.getRedditSendMessageMenuItem();

        multiPostMenuItem = menuView.getMultiPostMenuItem();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditLogOutMenuItem = menuView.getRedditLogOutMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();

        redditFollowUserMenuItem = menuView.getRedditFollowUserMenuItem();

        redditFollowSubredditMenuItem = menuView.getRedditFollowSubredditMenuItem();

        redditUnfollowSubredditMenuItem = menuView.getRedditUnfollowSubredditMenuItem();

        redditSavedPostsMenuItem = menuView.getRedditSavedPostsMenuItem();

        redditMessagesMenuItem = menuView.getRedditMessagesMenuItem();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterLogOutMenuItem = menuView.getTwitterLogOutMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        twitterFollowUserMenuItem = menuView.getTwitterFollowUserMenuItem();

        twitterBlockUserMenuItem = menuView.getTwitterBlockUserMenuItem();

        redditBlockUserMenuItem = menuView.getRedditBlockUserMenuItem();

        instagramBlockUserMenuItem = menuView.getInstagramBlockUserMenuItem();

        twitterMessagesMenuItem = menuView.getTwitterMessagesMenuItem();

        twitterLikedPostsMenuItem = menuView.getTwitterLikedPostsMenuItem();

        twitterSavedPostsMenuItem = menuView.getTwitterSavedPostsMenuItem();

        twitterBlockedUsersMenuItem = menuView.getTwitterBlockedUsersMenuItem();

        redditBlockedUsersMenuItem = menuView.getRedditBlockedUsersMenuItem();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramLogOutMenuItem = menuView.getInstagramLogOutMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        instagramBioMenuItem = menuView.getInstagramBioMenuItem();

        instagramSearchMenuItem = menuView.getInstagramSearchMenuItem();

        instagramProfilePictureItem = menuView.getInstagramProfilePictureMenuItem();

        instagramStoryItem = menuView.getInstagramStoryMenuItem();

        instagramFollowUserMenuItem = menuView.getInstagramFollowUserMenuItem();

        instagramSavedPostsMenuItem = menuView.getInstagramSavedPostsMenuItem();

        timeSortRadioMenuItem = menuView.getTimeSortRadioMenuItem();

        popularitySortRadioMenuItem = menuView.getPopularitySortRadioMenuItem();

        lightRadioMenuItem = menuView.getLightRadioMenuItem();

        darkRadioMenuItem = menuView.getDarkRadioMenuItem();

        tabRadioMenuItem = menuView.getTabRadioMenuItem();

        splitRadioMenuItem = menuView.getSplitRadioMenuItem();

        fontSizeSpinner = menuView.getFontSizeSpinner();

        allSavedPostsRadioMenuItem = menuView.getAllSavedPostsRadioMenuItem();

        allLikedPostsMenuItem = menuView.getAllLikedPostsMenuItem();

        twitterPostMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent)  -> controller.makeTwitterPost());

        redditPostMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent)  -> controller.makeRedditPost());

        instagramPostMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent)  -> controller.makeInstagramPost());

        redditLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToReddit());

        redditLogOutMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logOutOfReddit());

        redditProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewRedditProfile());

        redditFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followRedditUser());

        redditFollowSubredditMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followSubreddit());

        redditUnfollowSubredditMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.unfollowSubreddit());

        redditSavedPostsMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Scene scene = redditPostController.updateSavedPosts();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.setResizable(true);
            stage.show();
        });

        redditMessagesMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            RedditModel redditModel = controller.model.getRedditModel();
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Direct Messages";
            String message = "";
            List<Message> messages = redditModel.getDirectMessages();
            for(Message m : messages) {
                Date date = m.getCreated();
                DateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy hh:mm");

                String author = m.getAuthor();
                String messageBody = m.getBody();
                String strDate = dateFormat.format(date);
                String subject = m.getSubject();
                String dest = m.getDest();
                String temp = """
                              Sent: %s
                              From: %s
                              To: %s
                              Subject: %s
                              Message: %s
                              --------------------
                              """.formatted(strDate, author, dest, subject, messageBody);
                System.out.println(temp);
                message += temp;
            }
            alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle(title);

            alert.setHeaderText(headerText);

            TextArea area = new TextArea(message);
            area.setWrapText(true);
            area.setEditable(false);

            alert.getDialogPane().setContent(area);
            alert.setResizable(true);

            alert.show();
        });

        redditDirectMessageMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.sendRedditDirectMessage());

        twitterDirectMessageMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent)-> controller.sendTwitterDirectMessage());

        twitterTrendingMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.showTwitterTrending());

        twitterLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToTwitter());

        twitterLogOutMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logOutOfTwitter());

        twitterProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewTwitterProfile());

        twitterFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followTwitterUser());

        twitterBlockUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.blockTwitterUser());

        redditBlockUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.blockRedditUser());

        instagramBlockUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.blockInstagramUser());

        twitterMessagesMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            TwitterModel twitterModel;
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Direct Messages";
            twitterModel = controller.model.getTwitterModel();
            try{
                List<DirectMessage> messages = twitterModel.getRequests().getDirectMessages();
                String messagesText = "";
                for (int i = 0; i < messages.size(); i++) {
                    DirectMessage dm = messages.get(i);
                    User sender = twitterModel.getTwitter().users().showUser(dm.getSenderId());
                    User recipient = twitterModel.getTwitter().users().showUser(dm.getRecipientId());
                    Date date = dm.getCreatedAt();
                    DateFormat dateFormat = new SimpleDateFormat("mm-dd-yyyy hh:mm");

                    String senderScreenName = sender.getScreenName();
                    String recipientScreenName = recipient.getScreenName();
                    String strDate = dateFormat.format(date);
                    String message = dm.getText();

                    String temp = """
                                  Sent: %s
                                  Sender: %s
                                  Recipient: %s
                                  Message: %s
                                  --------------------
                                  """.formatted(strDate, senderScreenName, recipientScreenName, message);

                    messagesText += temp;
                }
                alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                TextArea area = new TextArea(messagesText);
                area.setWrapText(true);
                area.setEditable(false);

                alert.getDialogPane().setContent(area);
                alert.setResizable(true);

                alert.show();
            } catch (Exception te) {
                //handle exception
                alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                alert.setContentText("Error: Couldn't load DM data!\n" + te.getStackTrace());

                alert.show();
            }


        });

        twitterSavedPostsMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Scene scene = twitterPostController.getSavedPosts();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.setResizable(true);
            stage.setHeight(300);
            stage.setWidth(500);
            stage.show();
        });


        twitterLikedPostsMenuItem.addEventHandler((ActionEvent.ACTION), (actionEvent) -> {
            Scene scene = twitterPostController.getLikedPosts();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Liked Posts");
            stage.setResizable(true);
            stage.setHeight(300);
            stage.setWidth(500);
            stage.show();
        });

        twitterBlockedUsersMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            TwitterModel twitterModel;
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Blocked Users";
            twitterModel = controller.model.getTwitterModel();
            try{
                List<String> blocked = twitterModel.getRequests().getBlockedUsers();
                String blockedText = "";
                for (String user : blocked) {
                    blockedText += user + "\n";
                }
                alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                TextArea area = new TextArea(blockedText);
                area.setWrapText(true);
                area.setEditable(false);

                alert.getDialogPane().setContent(area);
                alert.setResizable(true);

                alert.show();
            } catch (Exception te) {
                //handle exception
                alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                alert.setContentText("Error: Couldn't load blocked user data!\n" + te.getStackTrace());

                alert.show();
            }

        });

        redditBlockedUsersMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            RedditModel redditModel = controller.model.getRedditModel();
            List<String> blockedUsers = redditModel.getBlockedUsers();
            String message = "";
            for (String user : blockedUsers) {
                message += user + "\n";
            }
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Blocked Users";
            try{
                alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                TextArea area = new TextArea(message);
                area.setWrapText(true);
                area.setEditable(false);

                alert.getDialogPane().setContent(area);
                alert.setResizable(true);

                alert.show();
            } catch (Exception te) {
                //handle exception
                alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle(title);

                alert.setHeaderText(headerText);

                alert.setContentText("Error: Couldn't load blocked user data!\n" + te.getStackTrace());

                alert.show();
            }

        });

        instagramLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToInstagram());

        instagramLogOutMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logOutOfInstagram());

        instagramProfileMenuItem.addEventHandler(ActionEvent.ACTION,
                                                 (actionEvent) -> controller.viewInstagramProfile());

        instagramBioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.editInstagramBio());

        instagramSearchMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.searchUsersOnInstagram());

        instagramProfilePictureItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.setInstagramProfilePicture());

        instagramStoryItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.makeInstagramStoryPost());

        instagramFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followInstagramUser());

        instagramSavedPostsMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Scene scene = instagramPostController.updateSavedPosts();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.setResizable(true);
            stage.show();
        });

        multiPostMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.makeMultiPost());

        lightRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToLightMode());

        darkRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToDarkMode());

        tabRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToTabPane());

        splitRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToSplitPane());

        fontSizeSpinner.valueProperty()
                       .addListener((observable, oldValue, newValue) -> controller.updateFontSize(newValue));

        allSavedPostsRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Scene scene = controller.getAllSavedPostsScene();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.show();
        });

        timeSortRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            controller.redditPostController.sortByTime = true;
            controller.instagramPostController.sortByTime = true;
            controller.twitterPostController.sortByTime = true;
            controller.redditPostController.updateAll = true;
            controller.instagramPostController.updateAll = true;
            controller.twitterPostController.updateAll = true;
            controller.redditPostController.updatePosts();
            controller.instagramPostController.updatePosts();
            controller.twitterPostController.updatePosts();
        });

        popularitySortRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            controller.redditPostController.sortByTime = false;
            controller.instagramPostController.sortByTime = false;
            controller.twitterPostController.sortByTime = false;
            controller.twitterPostController.updateAll = true;
            controller.instagramPostController.updateAll = true;
            controller.redditPostController.updateAll = true;
            controller.redditPostController.updatePosts();
            controller.instagramPostController.updatePosts();
            controller.twitterPostController.updatePosts();
        });

        twitterModel0 = controller.model.getTwitterModel();

        if (twitterModel0 != null) {
            twitterMenu = menuView.getTwitterMenu();

            allMenu = menuView.getAllMenu();

            twitterMenu.getItems()
                       .clear();

            twitterMenu.getItems()
                       .addAll(twitterProfileMenuItem, new SeparatorMenuItem(), twitterMessagesMenuItem,
                               new SeparatorMenuItem(), twitterLikedPostsMenuItem, new SeparatorMenuItem(),
                               twitterSavedPostsMenuItem, new SeparatorMenuItem(), twitterBlockUserMenuItem,
                               new SeparatorMenuItem(), twitterBlockedUsersMenuItem, new SeparatorMenuItem(), 
                               twitterFollowUserMenuItem, new SeparatorMenuItem(), twitterLogOutMenuItem);



            allMenu.getItems()
                   .clear();

            allMenu.getItems()
                   .addAll(allSavedPostsRadioMenuItem, new SeparatorMenuItem(), multiPostMenuItem);
        } //end if

        return controller;
    } //createMenuController
}