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
 * @version March 2, 2021
 */
public final class SocialButterflyApplication extends Application {
    /**
     * Returns the twitter model of this application.
     *
     * @return the twitter model of this application
     */
    private TwitterModel getTwitterModel() {
        File file;
        TwitterModel twitterModel = null;

        file = new File("twitter-model.ser");

        if (file.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                twitterModel = (TwitterModel) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } //end try catch
        } //end if

        if (twitterModel == null) {
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
                Alert alert;
                String message = "Error: pin is null! Goodbye!";

                alert = new Alert(Alert.AlertType.ERROR, message);

                alert.show();

                return null;
            } //end if

            authentication.handlePIN(pin);

            twitterModel.initializeRequests();
        } //end if

        return twitterModel;
    } //getTwitterModel

    /**
     * Returns the instagram model of this application.
     *
     * @return the instagram model of this application
     */
    private InstagramModel getInstagramModel() {
        InstagramModel instagramModel;
        String username;
        String password;
        Callable<String> inputCode;
        IGClient.Builder.LoginHandler twoFactorHandler;
        IGClient.Builder.LoginHandler challengeHandler;
        IGClient igClient = null;

        instagramModel = new InstagramModel();

        username = System.getProperty("username");

        password = System.getProperty("password");

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
        TwitterModel twitterModel;
        InstagramModel instagramModel;
        TwitterPostController twitterPostController;
        InstagramPostController instagramPostController;
        Thread twitterThread;
        Thread instagramThread = null;
        Thread tempThread;
        String title = "Social Butterfly";
        Scene scene;

        postView = PostView.createPostView(primaryStage);

        twitterModel = this.getTwitterModel();

        instagramModel = this.getInstagramModel();

        twitterPostController = TwitterPostController.createTwitterPostController(twitterModel, postView);

        twitterThread = twitterPostController.getBackgroundThread();

        if (instagramModel != null) {
            instagramPostController = InstagramPostController.createInstagramPostController(instagramModel, postView);

            instagramThread = instagramPostController.getBackgroundThread();
        } //end if

        tempThread = instagramThread;

        primaryStage.setOnCloseRequest((windowEvent) -> {
            try (var outputStream = new ObjectOutputStream(new FileOutputStream("twitter-model.ser"))) {
                outputStream.writeObject(twitterModel);
            } catch (IOException e) {
                e.printStackTrace();
            } //end try catch

            twitterThread.interrupt();

            if (tempThread != null) {
                tempThread.interrupt();
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
        if (args.length == 2) {
            System.setProperty("username", args[0]);

            System.setProperty("password", args[1]);
        } //end if

        SocialButterflyApplication.launch(args);
    } //main
}