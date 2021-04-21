package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest;
import com.github.instagram4j.instagram4j.requests.users.UsersUsernameInfoRequest;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedSavedRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest.MediaAction;
import com.github.instagram4j.instagram4j.responses.feed.FeedSavedResponse;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import com.github.instagram4j.instagram4j.requests.IGGetRequest;
import com.github.instagram4j.instagram4j.requests.direct.DirectInboxRequest;
import com.github.instagram4j.instagram4j.responses.direct.DirectInboxResponse;
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils;


import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.instagram4j.instagram4j.requests.friendships.FriendshipsActionRequest.FriendshipsAction.CREATE;

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

    public String getUsername() {
        return this.client.getSelfProfile().getUsername();
    }

    public String getFullName() {
        return this.client.getSelfProfile().getFull_name();
    }

    public String getProfilePic() {
        return this.client.getSelfProfile().getProfile_pic_url();
    }

    public boolean hasAnonymousProfilePicture() {
        return this.client.getSelfProfile().isHas_anonymous_profile_picture();
    }

    public boolean isPrivate() {
        return this.client.getSelfProfile().is_private();
    }

    public boolean isVerified() {
        return this.client.getSelfProfile().is_verified();
    }

    public void setBio(String newBio) { this.client.actions().account().setBio(newBio); }

    public CompletableFuture<UsersSearchResponse> searchForUsers(String searchUser) { return this.client.actions().search().searchUser(searchUser); }

    public void setProfilePicture(File newProfilePicture) {
        this.client.actions().account().setProfilePicture(newProfilePicture);
    }

    public void makeStoryPost(File newPost) {
        this.client.actions().story().uploadPhoto(newPost);
    }


    public void followInstagramProfile(String username) {
        new UsersUsernameInfoRequest(username).execute(this.client).thenAccept(userResponse -> {
            long pk = userResponse.getUser().getPk();
            FriendshipsActionRequest friendshipRequest = new FriendshipsActionRequest(pk, CREATE);
            IGResponse response = friendshipRequest.execute(this.client).join();
            if (response.getStatus().equals("ok")) {
                System.out.println("success");
            }
            else {
                System.out.println("error");
            }
        });
    }

    public List<String> getDirectMessages() {
        DirectInboxRequest dmRequest = new DirectInboxRequest();
        DirectInboxResponse dmResponse = dmRequest.execute(this.client).join();
        dmResponse.getInbox().getThreads().forEach(thread -> {
            String threadID = thread.getThread_id();
            String threadTitle = thread.getThread_title();
            List<Profile> profiles = thread.getUsers();
            thread.getItems();

        });
        return null;
    }

    public void savePost(String id) {
        this.client.actions().timeline();
        IGResponse ir = new MediaActionRequest(id, MediaAction.SAVE).execute(this.client).join();
    }


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

    public List<TimelineMedia> getSavedPosts() {
        FeedSavedResponse response = new FeedSavedRequest().execute(this.client).join();
        return response.getItems();
    }
}