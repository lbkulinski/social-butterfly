package com.butterfly.social.controller.menu;

import com.butterfly.social.SocialButterflyApplication;
import com.butterfly.social.controller.Post;
import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserProfile;
import com.butterfly.social.model.twitter.TwitterUserRequests;
import com.butterfly.social.view.MenuView;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import com.github.instagram4j.instagram4j.responses.direct.DirectInboxResponse;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.github.instagram4j.instagram4j.requests.direct.DirectInboxRequest;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import jdk.dynalink.NoSuchDynamicMethodException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.*;
import net.dean.jraw.references.OtherUserReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import net.dean.jraw.models.Message;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import twitter4j.DirectMessage;
import twitter4j.User;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
     * Attempts to log the user out of their Reddit account.
     */
    private void logoutReddit() {
        RedditModel redditModel;
        Alert alert;
        String title = "Social Butterfly";
        redditModel = this.model.getRedditModel();
        if (redditModel == null) {
            String message = "You are not signed into Reddit!";
            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        }
        this.redditPostController.clearRedditFeed();

        //this.redditPostController.getExecutorService().shutdownNow();

        this.model.setRedditModel(null);

        ObservableList<VBox> insta = (ObservableList) this.view.getPostView().getInstagramBox().getChildren();
        ObservableList<VBox> twitter = (ObservableList) this.view.getPostView().getTwitterBox().getChildren();

        Map<VBox, Post> twitterMap = this.twitterPostController.getBoxesToPosts();
        Map<VBox, Post> instaMap = this.instagramPostController.getBoxesToPosts();

        //ObservableList<VBox> instaClone = FXCollections.checkedObservableList(insta);
        //ObservableList<VBox> twitterClone = FXCollections.observableArrayList(twitter);

        /*for(VBox node : insta) {
            try {
                (Observable) node.clone();
            }catch (NoSuchDynamicMethodException e)
        }*/
        System.out.println("MAP TWI "   + twitterMap);
        System.out.println("MAP IN "    + instaMap);
        //System.out.println("INSTA OBV LIST "    +insta);
        //System.out.println("TWITTER OBV LIST " + twitter);
        ArrayList<VBox> instaClone = new ArrayList<>(instaMap.keySet());
        System.out.println("LKASJDNFKAJSDF: "+ instaClone);
        ArrayList<VBox> twitterClone = new ArrayList<>(twitterMap.keySet());
        System.out.println("sadjKLSAJDNFLKSAJDNF "+ twitterClone);
        ArrayList<VBox> copyT = new ArrayList<>();
        ArrayList<VBox> copyI = new ArrayList<>();
        ArrayList<VBox> copyT2 = new ArrayList<>();
        ArrayList<VBox> copyI2 = new ArrayList<>();

        for(int i = 0; i < twitter.size() - 1; i++) {
            copyT.add(new VBox(twitter.get(i)));
            copyT2.add(new VBox(twitter.get(i)));
        }
        for(int i = 0; i < insta.size() - 1; i++) {
            copyI.add(new VBox(insta.get(i)));
            copyI2.add(new VBox(insta.get(i)));
        }

        System.out.println("CopyT 1 "+ copyT.get(0));
        System.out.println("CopyI 1 "+ copyI.get(0));
        System.out.println("insta2 obs "+copyI2.get(0));
        System.out.println("twitter2 obs "+copyT2.get(0));
        this.view.getPostView().getAllBox().getChildren().addAll(copyI2);

        this.view.getPostView().getAllBox().getChildren().addAll(copyT2);

        this.view.getPostView().getTwitterBox().getChildren().addAll(copyT);

        this.view.getPostView().getInstagramBox().getChildren().addAll(copyI);

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setContentText("Successfully logged out!");

        alert.show();

    }
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
        MenuItem redditLogoutMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem twitterFollowUserMenuItem;
        MenuItem redditSavedPostsMenuItem;
        MenuItem redditMessagesMenuItem;
        MenuItem twitterLogInMenuItem;
        MenuItem twitterProfileMenuItem;
        MenuItem twitterMessagesMenuItem;
        MenuItem twitterSavedPostsMenuItem;
        MenuItem instagramLogInMenuItem;
        MenuItem instagramProfileMenuItem;
        MenuItem instagramMessagesMenuItem;
        MenuItem instagramBioMenuItem;
        MenuItem instagramSearchMenuItem;
        MenuItem instagramProfilePictureItem;
        MenuItem instagramFollowUserMenuItem;
        MenuItem instagramSavedPostsMenuItem;
        MenuItem timeSortMenuItem;
        MenuItem popularitySortMenuItem;
        RadioMenuItem lightRadioMenuItem;
        RadioMenuItem darkRadioMenuItem;
        RadioMenuItem tabRadioMenuItem;
        RadioMenuItem splitRadioMenuItem;
        MenuItem allSavedPostsRadioMenuItem;

        controller = new MenuController(model, view, redditPostController, twitterPostController,
                                        instagramPostController);

        menuView = controller.view.getMenuView();

        redditLogInMenuItem = menuView.getRedditLogInMenuItem();

        redditProfileMenuItem = menuView.getRedditProfileMenuItem();


        redditFollowUserMenuItem = menuView.getRedditFollowUserMenuItem();

        redditLogoutMenuItem = menuView.getRedditLogoutMenuItem();

        redditSavedPostsMenuItem = menuView.getRedditSavedPostsMenuItem();

        redditMessagesMenuItem = menuView.getRedditMessagesMenuItem();

        twitterLogInMenuItem = menuView.getTwitterLogInMenuItem();

        twitterProfileMenuItem = menuView.getTwitterProfileMenuItem();

        twitterFollowUserMenuItem = menuView.getTwitterFollowUserMenuItem();

        twitterMessagesMenuItem = menuView.getTwitterMessagesMenuItem();

        twitterSavedPostsMenuItem = menuView.getTwitterSavedPostsMenuItem();

        instagramLogInMenuItem = menuView.getInstagramLogInMenuItem();

        instagramProfileMenuItem = menuView.getInstagramProfileMenuItem();

        instagramMessagesMenuItem = menuView.getInstagramMessagesMenuItem();

        instagramBioMenuItem = menuView.getInstagramBioMenuItem();

        instagramSearchMenuItem = menuView.getInstagramSearchMenuItem();

        instagramProfilePictureItem = menuView.getInstagramProfilePictureMenuItem();

        instagramFollowUserMenuItem = menuView.getInstagramFollowUserMenuItem();

        instagramSavedPostsMenuItem = menuView.getInstagramSavedPostsMenuItem();

        timeSortMenuItem = menuView.getTimeSortMenuItem();

        popularitySortMenuItem = menuView.getPopularitySortMenuItem();

        lightRadioMenuItem = menuView.getLightRadioMenuItem();

        darkRadioMenuItem = menuView.getDarkRadioMenuItem();

        tabRadioMenuItem = menuView.getTabRadioMenuItem();

        splitRadioMenuItem = menuView.getSplitRadioMenuItem();

        allSavedPostsRadioMenuItem = menuView.getAllSavedPostsRadioMenuItem();

        redditLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToReddit());

        redditProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewRedditProfile());

        redditFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followRedditUser());

        redditLogoutMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logoutReddit());
        
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

        twitterLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToTwitter());

        twitterProfileMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.viewTwitterProfile());

        twitterFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followTwitterUser());
        
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

        instagramLogInMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.logInToInstagram());

        instagramProfileMenuItem.addEventHandler(ActionEvent.ACTION,
                                                 (actionEvent) -> controller.viewInstagramProfile());

        instagramBioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.editInstagramBio());

        instagramSearchMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.searchUsersOnInstagram());

        instagramProfilePictureItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.setInstagramProfilePicture());


        instagramFollowUserMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.followInstagramUser());
        
        instagramSavedPostsMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Scene scene = instagramPostController.updateSavedPosts();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.setResizable(true);
            stage.show();
        });

        instagramMessagesMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            //DirectInboxResponse pleaseWork = new DirectInboxRequest().execute(controller.model.getInstagramModel().getClient()).join();
            //System.out.println(pleaseWork.getInbox().getThreads().get(0).getItems().get(0).getClient_context());
        });

        lightRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToLightMode());

        darkRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToDarkMode());

        tabRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToTabPane());

        splitRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> controller.switchToSplitPane());

        allSavedPostsRadioMenuItem.addEventHandler(ActionEvent.ACTION, (actionevent) -> {
            Scene scene = controller.getAllSavedPostsScene();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Saved Posts");
            stage.show();
        });

        timeSortMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
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

        popularitySortMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
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

        return controller;
    } //createMenuController
}