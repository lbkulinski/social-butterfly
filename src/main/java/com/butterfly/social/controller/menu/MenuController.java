package com.butterfly.social.controller.menu;

import com.butterfly.social.SocialButterflyApplication;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserProfile;
import com.butterfly.social.view.MenuView;
import com.butterfly.social.view.View;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Trophy;
import net.dean.jraw.references.OtherUserReference;
import java.util.List;
import java.util.Objects;

/**
 * A controller for the menu of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
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
     * Constructs a newly allocated {@code MenuController} object with the specified model and view.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @throws NullPointerException if the specified model or view is {@code null}
     */
    private MenuController(Model model, View view) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        this.model = model;

        this.view = view;
    } //MenuController

    /**
     * Creates, and returns, a {@code MenuController} object using the specified model and view.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @return a {@code MenuController} object using the specified model and view
     * @throws NullPointerException if the specified model or view is {@code null}
     */
    public static MenuController createMenuController(Model model, View view) {
        MenuController controller;
        MenuView menuView;
        MenuItem redditLogInMenuItem;
        MenuItem redditProfileMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem instagramLogInMenuItem;
        MenuItem instagramProfileMenuItem;

        controller = new MenuController(model, view);

        menuView = controller.view.getMenuView();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        redditLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            RedditModel redditModel;

            redditModel = SocialButterflyApplication.getRedditModel();

            controller.model.setRedditModel(redditModel);
        });

        redditProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            RedditModel redditModel;
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
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Reddit Profile";

            redditModel = controller.model.getRedditModel();

            if (redditModel == null) {
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
        });

        twitterLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            TwitterModel twitterModel;

            twitterModel = SocialButterflyApplication.getTwitterModel();

            controller.model.setTwitterModel(twitterModel);
        });

        twitterProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            TwitterModel twitterModel;
            TwitterUserProfile profile;
            String name;
            String handle;
            String bio;
            int followerCount;
            int followingCount;
            boolean verified;
            String location;
            String profileText;
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Twitter Profile";

            twitterModel = controller.model.getTwitterModel();

            if (twitterModel == null) {
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
                          Location: %s""".formatted(name, handle, bio, followerCount, followingCount, verified,
                                                    location);

            alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle(title);

            alert.setHeaderText(headerText);

            alert.setContentText(profileText);

            alert.show();
        });

        instagramLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            InstagramModel instagramModel;

            instagramModel = SocialButterflyApplication.getInstagramModel();

            controller.model.setInstagramModel(instagramModel);
        });

        instagramProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            InstagramModel instagramModel;
            String name;
            String handle;
            boolean verified;
            boolean privateProfile;
            String profileText;
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Instagram Profile";

            instagramModel = controller.model.getInstagramModel();

            if (instagramModel == null) {
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
        });

        return controller;
    } //createMenuController
}