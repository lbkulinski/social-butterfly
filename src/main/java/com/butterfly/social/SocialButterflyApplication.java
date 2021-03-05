package com.butterfly.social;

import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserAuthentication;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.RedditProfileView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import twitter4j.TwitterException;
import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A runner for the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 4, 2021
 */
public final class SocialButterflyApplication extends Application {
    /**
     * Returns the twitter model of this application.
     *
     * @return the twitter model of this application
     */
    private TwitterModel getTwitterModel() {
        TwitterModel twitterModel;
        TwitterUserAuthentication authentication;
        String url;
        TextInputDialog inputDialog;
        String pin;

        twitterModel = new TwitterModel();

        authentication = twitterModel.getAuth();

        try {
            url = authentication.getURL();
        } catch (TwitterException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        this.getHostServices()
            .showDocument(url);

        inputDialog = new TextInputDialog();

        inputDialog.setHeaderText("Please enter the pin:");

        inputDialog.showAndWait();

        pin = inputDialog.getResult();

        if (pin == null) {
            return null;
        } //end if

        authentication.handlePIN(pin);

        try {
            twitterModel.initializeRequests();
        } catch (TwitterException te) {
            te.printStackTrace();
        }

        return twitterModel;
    } //getTwitterModel

    /**
     * Returns the instagram model of this application.
     *
     * @return the instagram model of this application
     */
    private InstagramModel getInstagramModel() {
        TextInputDialog usernameInputDialog;
        String title = "Social Butterfly";
        String usernameQuestion = "What is your username?";
        String username;
        TextInputDialog passwordInputDialog;
        String passwordQuestion = "What is your password?";
        String password;
        InstagramModel instagramModel = null;

        usernameInputDialog = new TextInputDialog();

        usernameInputDialog.setTitle(title);

        usernameInputDialog.setHeaderText(usernameQuestion);

        usernameInputDialog.showAndWait();

        username = usernameInputDialog.getResult();

        passwordInputDialog = new TextInputDialog();

        passwordInputDialog.setTitle(title);

        passwordInputDialog.setHeaderText(passwordQuestion);

        passwordInputDialog.showAndWait();

        password = passwordInputDialog.getResult();

        if ((username == null) || (password == null)) {
            return null;
        } //end if

        try {
            instagramModel = InstagramModel.createInstagramModel(username, password);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } //end try catch

        return instagramModel;
    } //getInstagramModel

    /**
     * Returns the reddit model of this application.
     *
     * @return the reddit model of this application
     */
    private RedditModel getRedditModel() {
        TextInputDialog usernameInputDialog;
        String title = "Social Butterfly";
        String usernameQuestion = "What is your username?";
        String username;
        TextInputDialog passwordInputDialog;
        String passwordQuestion = "What is your password?";
        String password;
        RedditModel redditModel = null;

        usernameInputDialog = new TextInputDialog();

        usernameInputDialog.setTitle(title);

        usernameInputDialog.setHeaderText(usernameQuestion);

        usernameInputDialog.showAndWait();

        username = usernameInputDialog.getResult();

        passwordInputDialog = new TextInputDialog();

        passwordInputDialog.setTitle(title);

        passwordInputDialog.setHeaderText(passwordQuestion);

        passwordInputDialog.showAndWait();

        password = passwordInputDialog.getResult();

        if ((username == null) || (password == null)) {
            return null;
        } //end if

        try {
            redditModel = RedditModel.createRedditModel(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        } //end try catch

        return redditModel;
    } //getRedditModel

    /**
     * Starts this application.
     *
     * @param primaryStage the primary stage to be used in the operation
     */
    @Override
    public void start(Stage primaryStage) {
        PostView postView;
        Lock allBoxLock;
        File file;
        String twitterFileName = "twitter-model.ser";
        TwitterModel readTwitterModel = null;
        String title = "Social Butterfly";
        ButtonType twitterResult = null;
        TwitterModel twitterModel;
        TwitterPostController twitterPostController;
        Thread twitterThread;
        Alert instagramAlert;
        String instagramMessage = "Would you like to log into Instagram?";
        ButtonType instagramResult;
        InstagramModel instagramModel;
        InstagramPostController instagramPostController;
        Thread instagramThread;
        Alert redditAlert;
        String redditMessage = "Would you like to log into Reddit?";
        ButtonType redditResult;
        RedditModel redditModel;
        RedditPostController redditPostController;
        Thread redditThread;

        Scene scene;

        postView = PostView.createPostView(primaryStage);

        allBoxLock = new ReentrantLock();

        file = new File(twitterFileName);

        if (file.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                readTwitterModel = (TwitterModel) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } //end try catch
        } //end if

        Button redditLoginButton = new Button("Login");
        redditLoginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RedditModel newRedditModel = getRedditModel();
                if (newRedditModel != null) {
                    RedditPostController newRedditPostController = RedditPostController.createRedditPostController(newRedditModel, postView,
                            allBoxLock);
                    Thread newRedditThread = newRedditPostController.getBackgroundThread();
                    if (newRedditThread != null) {
                        primaryStage.setOnCloseRequest((windowEvent) -> {
                            newRedditThread.interrupt();
                            System.exit(0);
                        });
                        Button redditProfileButtonTwo = new Button("Reddit Profile");
                        redditProfileButtonTwo.prefWidthProperty()
                                            .bind(primaryStage.widthProperty());
                        postView.getMainBox().getChildren().add(redditProfileButtonTwo);
                        redditProfileButtonTwo.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                RedditProfileView.createRedditProfileView(newRedditModel);
                            }
                        });
                    }
                }
            }
        });

        if (readTwitterModel == null) {
            Alert twitterAlert;
            String twitterMessage = "Would you like to log into Twitter?";

            twitterAlert = new Alert(Alert.AlertType.CONFIRMATION, twitterMessage, ButtonType.YES, ButtonType.NO);

            twitterAlert.setTitle(title);

            twitterAlert.showAndWait();

            twitterResult = twitterAlert.getResult();

            if (twitterResult == ButtonType.YES) {
                twitterModel = this.getTwitterModel();
            } else {
                twitterModel = null;
            } //end if
        } else {
            twitterModel = readTwitterModel;
        } //end if

        if (twitterModel != null) {
            twitterPostController = TwitterPostController.createTwitterPostController(twitterModel, postView,
                                                                                      allBoxLock);

            twitterThread = twitterPostController.getBackgroundThread();
        } else {
            twitterThread = null;

            if (twitterResult == ButtonType.YES) {
                String message;
                Alert errorAlert;

                message = "Error: Could not sign into Twitter! Please try again later.";

                errorAlert = new Alert(Alert.AlertType.ERROR, message);

                errorAlert.showAndWait();
            } //end if
        } //end if

        instagramAlert = new Alert(Alert.AlertType.CONFIRMATION, instagramMessage, ButtonType.YES, ButtonType.NO);

        instagramAlert.setTitle(title);

        instagramAlert.showAndWait();

        instagramResult = instagramAlert.getResult();

        if (instagramResult == ButtonType.YES) {
            instagramModel = this.getInstagramModel();

            if (instagramModel == null) {
                String message;
                Alert errorAlert;

                message = "Error: Could not sign into Instagram! Please try again later.";

                errorAlert = new Alert(Alert.AlertType.ERROR, message);

                errorAlert.showAndWait();

                instagramThread = null;
            } else {
                instagramPostController = InstagramPostController.createInstagramPostController(instagramModel,
                                                                                                postView, allBoxLock);

                instagramThread = instagramPostController.getBackgroundThread();
            } //end if
        } else {
            instagramThread = null;
        } //end if

        redditAlert = new Alert(Alert.AlertType.CONFIRMATION, redditMessage, ButtonType.YES, ButtonType.NO);

        redditAlert.setTitle(title);

        redditAlert.showAndWait();

        redditResult = redditAlert.getResult();

        if (redditResult == ButtonType.YES) {
            redditModel = this.getRedditModel();

            if (redditModel == null) {
                String message;
                Alert errorAlert;

                message = "Error: Could not sign into Reddit! Please try again later.";

                errorAlert = new Alert(Alert.AlertType.ERROR, message);

                errorAlert.showAndWait();

                postView.getRedditBox().getChildren().add(redditLoginButton);

                redditThread = null;
            } else {
                redditPostController = RedditPostController.createRedditPostController(redditModel, postView,
                                                                                       allBoxLock);

                redditThread = redditPostController.getBackgroundThread();
                System.out.println("Reddit account successfully connected!");
                Button redditProfileButton = new Button("Reddit Profile");
                postView.getMainBox().getChildren().add(redditProfileButton);
                redditProfileButton.prefWidthProperty().bind(primaryStage.widthProperty());
                redditProfileButton.setOnAction(event -> RedditProfileView.createRedditProfileView(redditModel));
            } //end if
        } else {
            postView.getRedditBox().getChildren().add(redditLoginButton);

            redditThread = null;
        } //end if

        primaryStage.setOnCloseRequest((windowEvent) -> {
            if (twitterModel != null) {
                try (var outputStream = new ObjectOutputStream(new FileOutputStream("twitter-model.ser"))) {
                    outputStream.writeObject(twitterModel);
                } catch (IOException e) {
                    e.printStackTrace();
                } //end try catch
            } //end if

            if (twitterThread != null) {
                twitterThread.interrupt();
            } //end if

            if (instagramThread != null) {
                instagramThread.interrupt();
            } //end if

            if (redditThread != null) {
                redditThread.interrupt();
            } //end if

            System.exit(0);
        });

        scene = postView.getScene();

        primaryStage.setTitle(title);

        primaryStage.setScene(scene);

        primaryStage.setWidth(500);

        primaryStage.setHeight(300);

        primaryStage.show();
    } //start

    /**
     * Runs an instance of {@code SocialButterflyApplication}.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SocialButterflyApplication.launch(args);
    } //main
}