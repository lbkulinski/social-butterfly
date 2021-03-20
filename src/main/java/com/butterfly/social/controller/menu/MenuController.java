package com.butterfly.social.controller.menu;

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
 * @version March 20, 2021
 */
public final class MenuController {
    /**
     * The Reddit model of this menu controller.
     */
    private final RedditModel redditModel;

    /**
     * The Twitter model of this menu controller.
     */
    private final TwitterModel twitterModel;

    /**
     * The Instagram model of this menu controller.
     */
    private final InstagramModel instagramModel;

    /**
     * The view of this menu controller.
     */
    private final View view;

    /**
     * Constructs a newly allocated {@code MenuController} object with the specified Reddit model, Twitter model,
     * Instagram model, and view.
     *
     * @param redditModel the Reddit model to be used in construction
     * @param twitterModel the Twitter model to be used in construction
     * @param instagramModel the Instagram model to be used in construction
     * @param view the view to be used in construction
     * @throws NullPointerException if the specified view is {@code null}
     */
    private MenuController(RedditModel redditModel, TwitterModel twitterModel, InstagramModel instagramModel,
                           View view) {
        Objects.requireNonNull(view, "the specified view is null");

        this.redditModel = redditModel;

        this.twitterModel = twitterModel;

        this.instagramModel = instagramModel;

        this.view = view;
    } //MenuController

    /**
     * Creates, and returns, a {@code MenuController} object using the specified Reddit model, Twitter model, Instagram
     * model, and view.
     *
     * @param redditModel the Reddit model to be used in the operation
     * @param twitterModel the Twitter model to be used in the operation
     * @param instagramModel the Instagram model to be used in the operation
     * @param view the view to be used in the operation
     * @return a {@code MenuController} object using the specified Reddit model, Twitter model, Instagram model, and
     * view
     * @throws NullPointerException if the specified Reddit model, Twitter model, Instagram model, or view is
     * {@code null}
     */
    public static MenuController createMenuController(RedditModel redditModel, TwitterModel twitterModel,
                                                      InstagramModel instagramModel, View view) {
        MenuController controller;
        MenuView menuView;
        MenuItem redditLogInMenuItem;
        MenuItem redditProfileMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem instagramLogInMenuItem;
        MenuItem instagramProfileMenuItem;

        controller = new MenuController(redditModel, twitterModel, instagramModel, view);

        menuView = controller.view.getMenuView();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        redditLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
        });

        redditProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
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

            if (controller.redditModel == null) {
                return;
            } //end if

            handle = controller.redditModel.getUsername();

            client = controller.redditModel.getClient();

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
        });

        twitterProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
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

            if (controller.twitterModel == null) {
                return;
            } //end if

            profile = controller.twitterModel.getRequests()
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
        });

        instagramProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            String name;
            String handle;
            boolean verified;
            boolean privateProfile;
            String profileText;
            Alert alert;
            String title = "Social Butterfly";
            String headerText = "Instagram Profile";

            if (controller.instagramModel == null) {
                return;
            } //end if

            name = controller.instagramModel.getFullName();

            handle = controller.instagramModel.getUsername();

            verified = controller.instagramModel.isVerified();

            privateProfile = controller.instagramModel.isPrivate();

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