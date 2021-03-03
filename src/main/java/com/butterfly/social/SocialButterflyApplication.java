package com.butterfly.social;

import com.butterfly.social.controller.instagram.InstagramPostController;
import com.butterfly.social.controller.twitter.TwitterPostController;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.model.twitter.TwitterUserAuthentication;
import com.butterfly.social.view.PostView;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import twitter4j.TwitterException;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * A runner for the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 3, 2021
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

        twitterModel.initializeRequests();

        return twitterModel;
    } //getTwitterModel

    /**
     * Returns the instagram model of this application.
     *
     * @return the instagram model of this application
     */
    private InstagramModel getInstagramModel() {
        InstagramModel instagramModel;
        TextInputDialog usernameInputDialog;
        String title = "Social Butterfly";
        String usernameQuestion = "What is your username?";
        String username;
        TextInputDialog passwordInputDialog;
        String passwordQuestion = "What is your password?";
        String password;
        Callable<String> inputCode;
        IGClient.Builder.LoginHandler twoFactorHandler;
        IGClient.Builder.LoginHandler challengeHandler;
        IGClient igClient;

        instagramModel = new InstagramModel();

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

        inputCode = () -> {
            Scanner scanner;
            String pin;

            scanner = new Scanner(System.in);

            System.out.print("Please enter the pin: ");

            pin = scanner.nextLine();

            scanner.close();

            return pin;
        };

        twoFactorHandler = (client, response) -> IGChallengeUtils.resolveTwoFactor(client, response, inputCode);

        challengeHandler = (client, response) -> IGChallengeUtils.resolveChallenge(client, response, inputCode);

        try {
            igClient = IGClient.builder()
                               .username(username)
                               .password(password)
                               .onTwoFactor(twoFactorHandler)
                               .onChallenge(challengeHandler)
                               .login();
        } catch (IGLoginException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        instagramModel.getAuth()
                      .setClient(igClient);

        instagramModel.getRequests()
                      .setClient(igClient);

        return instagramModel;
    } //getInstagramModel

    /**
     * Starts this application.
     *
     * @param primaryStage the primary stage to be used in the operation
     */
    @Override
    public void start(Stage primaryStage) {
        PostView postView;
        File file;
        String twitterFileName = "twitter-model.ser";
        TwitterModel readTwitterModel = null;
        String title = "Social Butterfly";
        TwitterModel twitterModel;
        TwitterPostController twitterPostController;
        Thread twitterThread;
        Alert instagramAlert;
        String instagramMessage = "Would you like to log into Instagram?";
        ButtonType instagramResult;
        InstagramModel instagramModel;
        InstagramPostController instagramPostController;
        Thread instagramThread;
        Scene scene;

        postView = PostView.createPostView(primaryStage);

        file = new File(twitterFileName);

        if (file.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                readTwitterModel = (TwitterModel) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } //end try catch
        } //end if

        if (readTwitterModel == null) {
            Alert twitterAlert;
            String twitterMessage = "Would you like to log into Twitter?";
            ButtonType twitterResult;

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
            twitterPostController = TwitterPostController.createTwitterPostController(twitterModel, postView);

            twitterThread = twitterPostController.getBackgroundThread();
        } else {
            twitterThread = null;
        } //end if

        instagramAlert = new Alert(Alert.AlertType.CONFIRMATION, instagramMessage, ButtonType.YES, ButtonType.NO);

        instagramAlert.setTitle(title);

        instagramAlert.showAndWait();

        instagramResult = instagramAlert.getResult();

        if (instagramResult == ButtonType.YES) {
            instagramModel = this.getInstagramModel();

            if (instagramModel == null) {
                instagramThread = null;
            } else {
                instagramPostController = InstagramPostController.createInstagramPostController(instagramModel,
                                                                                                postView);

                instagramThread = instagramPostController.getBackgroundThread();
            } //end if
        } else {
            instagramThread = null;
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