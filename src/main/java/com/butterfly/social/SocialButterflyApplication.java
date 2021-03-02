package com.butterfly.social;

import javafx.application.Application;
import java.io.File;
import com.butterfly.social.model.TwitterModel;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.butterfly.social.model.TwitterUserAuthentication;
import javafx.scene.control.TextInputDialog;
import twitter4j.TwitterException;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.butterfly.social.view.PostView;
import com.butterfly.social.controller.TwitterPostController;
import javafx.scene.Scene;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

/**
 * A runner for the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version February 28, 2021
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

            try {
                twitterModel.initializeRequests();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } //end if

        return twitterModel;
    } //getTwitterModel

    /**
     * Starts this application.
     *
     * @param primaryStage the primary stage to be used in the operation
     */
    @Override
    public void start(Stage primaryStage) {
        TwitterModel twitterModel;
        PostView postView;
        TwitterPostController twitterPostController;
        Thread backgroundThread;
        String title = "Social Butterfly";
        Scene scene;

        twitterModel = this.getTwitterModel();

        postView = PostView.createPostView(primaryStage);

        twitterPostController = TwitterPostController.createTwitterPostController(twitterModel, postView);

        backgroundThread = twitterPostController.getBackgroundThread();

        primaryStage.setOnCloseRequest((windowEvent) -> {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("twitter-model.ser"))) {
                outputStream.writeObject(twitterModel);
            } catch (IOException e) {
                e.printStackTrace();
            } //end try catch

            backgroundThread.interrupt();
        });

        scene = postView.getScene();

        primaryStage.setTitle(title);

        primaryStage.setScene(scene);

        primaryStage.setWidth(500);

        primaryStage.setHeight(300);

        primaryStage.show();
    } //start
    public static void main(String[] args) {
        launch();
    }
}