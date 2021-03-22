package com.butterfly.social;

import com.butterfly.social.controller.Controller;
import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.reddit.RedditPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserAuthentication;
import com.butterfly.social.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import twitter4j.TwitterException;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A runner for the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class SocialButterflyApplication extends Application {
    /**
     * Returns the Reddit model of the {@code SocialButterflyApplication} class.
     *
     * @return the Reddit model of the {@code SocialButterflyApplication} class
     */
    public static RedditModel getRedditModel() {
        TextInputDialog usernameInputDialog;
        String title = "Social Butterfly";
        String usernameQuestion = "What is your username?";
        String username;
        TextInputDialog passwordInputDialog;
        String passwordQuestion = "What is your password?";
        String password;
        TextInputDialog clientIdInputDialog;
        String clientIdQuestion = "What is your client ID?";
        String clientId;
        TextInputDialog clientSecretInputDialog;
        String clientSecretQuestion = "What is your client secret?";
        String clientSecret;
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

        clientIdInputDialog = new TextInputDialog();

        clientIdInputDialog.setTitle(title);

        clientIdInputDialog.setHeaderText(clientIdQuestion);

        clientIdInputDialog.showAndWait();

        clientId = clientIdInputDialog.getResult();

        clientSecretInputDialog = new TextInputDialog();

        clientSecretInputDialog.setTitle(title);

        clientSecretInputDialog.setHeaderText(clientSecretQuestion);

        clientSecretInputDialog.showAndWait();

        clientSecret = clientSecretInputDialog.getResult();

        if ((username == null) || (password == null) || (clientId == null) || (clientSecret == null)) {
            return null;
        } //end if

        try {
            redditModel = RedditModel.createRedditModel(username, password, clientId, clientSecret);
        } catch (Exception e) {
            e.printStackTrace();
        } //end try catch

        return redditModel;
    } //getRedditModel

    /**
     * Returns the Twitter model of the {@code SocialButterflyApplication} class using the specified application.
     *
     * @return the Twitter model of the {@code SocialButterflyApplication} class using the specified application
     */
    public static TwitterModel getTwitterModel() {
        TwitterModel twitterModel;
        TwitterUserAuthentication authentication;
        String url;
        URI uri;
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

        uri = URI.create(url);

        try {
            Desktop.getDesktop()
                   .browse(uri);
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } //end try catch

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
     * Returns the Instagram model of the {@code SocialButterflyApplication} class.
     *
     * @return the Instagram model of the {@code SocialButterflyApplication} class
     */
    public static InstagramModel getInstagramModel() {
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
     * Returns the model of this application.
     *
     * @return the model of this application
     */
    private Model getModel() {
        Model model;
        File file;
        String twitterFileName = "twitter-model.ser";
        TwitterModel twitterModel = null;

        model = new Model();

        file = new File(twitterFileName);

        if (file.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                twitterModel = (TwitterModel) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } //end try catch
        } //end if

        if (twitterModel != null) {
            model.setTwitterModel(twitterModel);
        } //end if

        return model;
    } //getModel

    /**
     * Starts this application.
     *
     * @param primaryStage the primary stage to be used in the operation
     */
    @Override
    public void start(Stage primaryStage) {
        Model model;
        View view;
        Lock allBoxLock;
        Controller controller;
        Scene scene;
        String title = "Social Butterfly";
        int width = 500;
        int height = 300;

        model = this.getModel();

        view = View.createView(primaryStage);

        allBoxLock = new ReentrantLock();

        controller = new Controller(model, view, allBoxLock);

        primaryStage.setOnCloseRequest((windowEvent) -> {
            TwitterModel twitterModel;
            RedditPostController redditPostController;
            Thread redditThread;
            TwitterPostController twitterPostController;
            Thread twitterThread;
            InstagramPostController instagramPostController;
            Thread instagramThread;

            twitterModel = model.getTwitterModel();

            redditPostController = controller.getRedditPostController();

            redditThread = redditPostController.getBackgroundThread();

            twitterPostController = controller.getTwitterPostController();

            twitterThread = twitterPostController.getBackgroundThread();

            instagramPostController = controller.getInstagramPostController();

            instagramThread = instagramPostController.getBackgroundThread();

            if (twitterModel != null) {
                try (var outputStream = new ObjectOutputStream(new FileOutputStream("twitter-model.ser"))) {
                    outputStream.writeObject(twitterModel);
                } catch (IOException e) {
                    e.printStackTrace();
                } //end try catch
            } //end if

            if (redditThread != null) {
                redditThread.interrupt();
            } //end if

            if (twitterThread != null) {
                twitterThread.interrupt();
            } //end if

            if (instagramThread != null) {
                instagramThread.interrupt();
            } //end if

            System.exit(0);
        });

        scene = view.getScene();

        primaryStage.setTitle(title);

        primaryStage.setScene(scene);

        primaryStage.setWidth(width);

        primaryStage.setHeight(height);

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