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
import com.butterfly.social.model.twitter.TwitterUserRequests;
import com.butterfly.social.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import twitter4j.TwitterException;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A runner for the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version April 13, 2021
 */
public final class SocialButterflyApplication extends Application {
    /**
     * The Twitter user authentication of the {@code SocialButterflyApplication} class.
     */
    private static TwitterUserAuthentication twitterAuth;

    /**
     * The Twitter user requests of the {@code SocialButterflyApplication} class.
     */
    private static TwitterUserRequests twitterRequests;

    static {
        twitterAuth = null;

        twitterRequests = null;
    } //static

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
        String clientId = "GaoCzV0A1aEvMA";
        String clientSecret = "14CAWMVrhheFVi0n5XDgpAAxnZV5Fw";
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
        String url;
        URI uri;
        TextInputDialog inputDialog;
        String pin;

        if ((SocialButterflyApplication.twitterAuth != null) && (SocialButterflyApplication.twitterRequests != null)) {
            twitterModel = new TwitterModel(SocialButterflyApplication.twitterAuth,
                                            SocialButterflyApplication.twitterRequests);

            return twitterModel;
        } //end if

        twitterModel = new TwitterModel();

        SocialButterflyApplication.twitterAuth = twitterModel.getAuth();

        SocialButterflyApplication.twitterRequests = twitterModel.getRequests();

        try {
            url = SocialButterflyApplication.twitterAuth.getURL();
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

        SocialButterflyApplication.twitterAuth.handlePIN(pin);

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
        TwitterModel twitterModel;
        Scene scene;
        String title = "Social Butterfly";
        int width = 500;
        int height = 300;

        model = this.getModel();

        view = View.createView(primaryStage);

        allBoxLock = new ReentrantLock();

        controller = new Controller(model, view, allBoxLock);

        twitterModel = model.getTwitterModel();

        if (twitterModel != null) {
            TwitterPostController twitterPostController;
            ScheduledExecutorService executorService;
            int delay = 0;
            int period = 1;

            twitterPostController = controller.getTwitterPostController();

            executorService = twitterPostController.getExecutorService();

            executorService.scheduleAtFixedRate(twitterPostController::updatePosts, delay, period, TimeUnit.MINUTES);
        } //end if

        primaryStage.setOnCloseRequest((windowEvent) -> {
            TwitterModel currentTwitterModel;
            RedditPostController redditPostController;
            TwitterPostController twitterPostController;
            ScheduledExecutorService executorService;
            InstagramPostController instagramPostController;

            currentTwitterModel = model.getTwitterModel();

            if (currentTwitterModel != null) {
                try (var outputStream = new ObjectOutputStream(new FileOutputStream("twitter-model.ser"))) {
                    outputStream.writeObject(currentTwitterModel);
                } catch (IOException e) {
                    e.printStackTrace();
                } //end try catch
            } //end if

            redditPostController = controller.getRedditPostController();

            executorService = redditPostController.getExecutorService();

            executorService.shutdownNow();

            twitterPostController = controller.getTwitterPostController();

            executorService = twitterPostController.getExecutorService();

            executorService.shutdownNow();

            instagramPostController = controller.getInstagramPostController();

            executorService = instagramPostController.getExecutorService();

            executorService.shutdownNow();

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