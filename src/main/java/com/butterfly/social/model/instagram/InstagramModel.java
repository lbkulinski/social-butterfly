package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;
import java.io.Serializable;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;

public final class InstagramModel implements Serializable {
    private IGClient client;

    private InstagramModel() {
        this.client = null;
    } //InstagramModel

    public IGClient getClient() {
        return this.client;
    } //getClient

    public void setClient(IGClient client) {
        this.client = client;
    } //setClient

    public static InstagramModel createInstagramModel(String username, String password) {
        InstagramModel instagramModel;
        Callable<String> inputCode;
        IGClient.Builder.LoginHandler twoFactorHandler;
        IGClient.Builder.LoginHandler challengeHandler;

        Objects.requireNonNull(username, "the specified username is null");

        Objects.requireNonNull(password, "the specified password is null");

        instagramModel = new InstagramModel();

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
            instagramModel.client = IGClient.builder()
                                            .username(username)
                                            .password(password)
                                            .onTwoFactor(twoFactorHandler)
                                            .onChallenge(challengeHandler)
                                            .login();
        } catch (IGLoginException e) {
            String message;

            message = e.getMessage();

            throw new IllegalStateException(message);
        } //end try catch

        return instagramModel;
    } //createInstagramModel
}