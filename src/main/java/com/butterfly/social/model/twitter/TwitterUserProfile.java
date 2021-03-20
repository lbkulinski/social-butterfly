package com.butterfly.social.model.twitter;

import java.io.Serializable;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Date;

public final class TwitterUserProfile implements Serializable {
    //private ImageView profilePicture;
    //private ImageView bannerImage;
    private final String name;
    private final String bio;
    private final int followerCount;
    private final int followingCount;
    private final boolean isVerified;
    private final String handle;
    private final String location;
    private final Date joined;

    public TwitterUserProfile(Twitter twitter) throws TwitterException {
        User profile;

        profile = twitter.showUser(twitter.getId());

        //this.profilePicture = this.getImage(this.profile.get400x400ProfileImageURLHttps());
        //this.bannerImage = this.getImage(this.profile.getProfileBanner1500x500URL());
        this.name = profile.getName();
        this.bio = profile.getDescription();
        this.followerCount = profile.getFollowersCount();
        this.followingCount = profile.getFriendsCount();
        this.isVerified = profile.isVerified();
        this.handle = profile.getScreenName();
        this.location = profile.getLocation();
        this.joined = profile.getCreatedAt();
    }

    /*
    public ImageView getProfilePicture() {
        return this.profilePicture;
    }

    public ImageView getBannerImage() {
        return this.bannerImage;
    }
     */

    public String getName() {
        return this.name;
    }

    public String getBio() {
        return this.bio;
    }

    public int getFollowerCount() {
        return this.followerCount;
    }

    public int getFollowingCount() {
        return this.followingCount;
    }

    public boolean isVerified() {
        return this.isVerified;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getLocation() {
        return this.location;
    }

    public Date getDateCreated() {
        return this.joined;
    }

    private ImageView getImage(String urlString) {
        URI uri;
        String uriString;
        Image image;
        ImageView imageView;

        try {
            uri = new URI(urlString);

            uriString = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        image = new Image(uriString);

        imageView = new ImageView(image);

        return imageView;
    }
}