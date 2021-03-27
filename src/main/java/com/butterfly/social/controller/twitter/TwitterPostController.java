package com.butterfly.social.controller.twitter;

import com.butterfly.social.controller.Post;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.twitter.TwitterModel;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import twitter4j.*;
import twitter4j.api.FavoritesResources;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * A controller for Twitter posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 26, 2021
 */
public final class TwitterPostController {
    /**
     * The model of this Twitter post controller.
     */
    private final Model model;

    /**
     * The view of this Twitter post controller.
     */
    private final View view;

    /**
     * The IDs of this Twitter post controller.
     */
    private final Set<Long> ids;

    /**
     * The map from boxes to posts of this Twitter post controller.
     */
    private final Map<VBox, Post> boxesToPosts;

    /**
     * The all box lock of this Twitter post controller.
     */
    private final Lock allBoxLock;

    /**
     * The executor service of this Twitter post controller.
     */
    private final ScheduledExecutorService executorService;

    /**
     * Constructs a newly allocated {@code TwitterPostController} object with the specified model, view, map from boxes
     * to posts, and all box lock.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @param boxesToPosts the map from boxes to posts to be used in construction
     * @param allBoxLock the all box lock to be used in construction
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    private TwitterPostController(Model model, View view, Map<VBox, Post> boxesToPosts, Lock allBoxLock) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        Objects.requireNonNull(boxesToPosts, "the specified map from boxes to posts is null");

        Objects.requireNonNull(allBoxLock, "the specified all box lock is null");

        this.model = model;

        this.view = view;

        this.ids = new HashSet<>();

        this.boxesToPosts = boxesToPosts;

        this.allBoxLock = allBoxLock;

        this.executorService = Executors.newSingleThreadScheduledExecutor();
    } //TwitterPostController

    /**
     * Returns the executor service of this Twitter post controller.
     *
     * @return the executor service of this Twitter post controller
     */
    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    } //getExecutorService

    /**
     * Returns the video node for the specified media entity.
     *
     * @param mediaEntity the media entity to be used in the operation
     * @return the video node for the specified media entity
     * @throws NullPointerException if the specified media entity is {@code null}
     */
    private Node getVideoNode(MediaEntity mediaEntity) {
        MediaEntity.Variant[] variants;
        int index;
        String urlString;
        URI uri;
        String uriString;
        Media media;
        MediaPlayer mediaPlayer;
        MediaView mediaView;

        Objects.requireNonNull(mediaEntity, "the specified media entity is null");

        variants = mediaEntity.getVideoVariants();

        Arrays.sort(variants, Comparator.comparing(MediaEntity.Variant::getBitrate));

        index = variants.length / 2;

        urlString = variants[index].getUrl();

        try {
            uri = new URI(urlString);

            uriString = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        media = new Media(uriString);

        mediaPlayer = new MediaPlayer(media);

        mediaView = new MediaView(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);

        mediaView.setOnMouseClicked((mouseEvent) -> {
            MediaPlayer.Status status;

            status = mediaPlayer.getStatus();

            switch (status) {
                case PLAYING -> mediaPlayer.pause();
                case READY, PAUSED, STOPPED -> mediaPlayer.play();
            } //end switch
        });

        mediaView.setCache(true);

        mediaView.setCacheHint(CacheHint.SPEED);

        return mediaView;
    } //getVideoNode

    /**
     * Returns the photo node for the specified media entity.
     *
     * @param mediaEntity the media entity to be used in the operation
     * @return the photo node for the specified media entity
     * @throws NullPointerException if the specified media entity is {@code null}
     */
    private Node getPhotoNode(MediaEntity mediaEntity) {
        String urlString;
        URI uri;
        String uriString;
        Image image;
        ImageView imageView;

        Objects.requireNonNull(mediaEntity, "the specified media entity is null");

        urlString = mediaEntity.getMediaURLHttps();

        try {
            uri = new URI(urlString);

            uriString = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        image = new Image(uriString, true);

        imageView = new ImageView(image);

        imageView.setCache(true);

        imageView.setCacheHint(CacheHint.SPEED);

        return imageView;
    } //getPhotoNode

    /**
     * Returns the GIF node for the specified media entity.
     *
     * @param mediaEntity the media entity to be used in the operation
     * @return the GIF node for the specified media entity
     * @throws NullPointerException if the specified media entity is {@code null}
     */
    private Node getGifNode(MediaEntity mediaEntity) {
        MediaEntity.Variant[] variants;
        int index;
        String urlString;
        URI uri;
        String uriString;
        Media media;
        MediaPlayer mediaPlayer;
        MediaView mediaView;

        Objects.requireNonNull(mediaEntity, "the specified media entity is null");

        variants = mediaEntity.getVideoVariants();

        Arrays.sort(variants, Comparator.comparing(MediaEntity.Variant::getBitrate));

        index = variants.length / 2;

        urlString = variants[index].getUrl();

        try {
            uri = new URI(urlString);

            uriString = uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();

            return null;
        } //end try catch

        media = new Media(uriString);

        mediaPlayer = new MediaPlayer(media);

        mediaView = new MediaView(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);

            mediaPlayer.play();
        });

        mediaPlayer.setAutoPlay(true);

        mediaView.setCache(true);

        mediaView.setCacheHint(CacheHint.SPEED);

        return mediaView;
    } //getGifNode

    /**
     * Returns an accordion of the specified media entities.
     *
     * @param mediaEntities the media entities to be used in the operation
     * @return an accordion of the specified media entities
     * @throws NullPointerException if the specified array of media entities is {@code null}
     */
    private Accordion getMediaAccordion(MediaEntity[] mediaEntities) {
        List<TitledPane> titledPanes;
        String type;
        Node node;
        String mediaName;
        int mediaCount = 1;
        ScrollPane scrollPane;
        TitledPane titledPane;
        TitledPane[] array;
        Accordion accordion;

        Objects.requireNonNull(mediaEntities, "the specified array of media entities is null");

        if (mediaEntities.length == 0) {
            return null;
        } //end if

        titledPanes = new ArrayList<>();

        for (MediaEntity mediaEntity : mediaEntities) {
            type = mediaEntity.getType();

            node = switch (type) {
                case "video" -> this.getVideoNode(mediaEntity);
                case "photo" -> this.getPhotoNode(mediaEntity);
                case "animated_gif" -> this.getGifNode(mediaEntity);
                default -> {
                    String message;

                    message = String.format("Invalid type: %s", type);

                    throw new IllegalStateException(message);
                } //default
            };

            if (node != null) {
                mediaName = String.format("Attachment %d", mediaCount);

                mediaCount++;

                scrollPane = new ScrollPane(node);

                titledPane = new TitledPane(mediaName, scrollPane);

                titledPane.setFont(Font.font("Arial", 14));

                titledPanes.add(titledPane);
            } //end if
        } //end for

        array = new TitledPane[titledPanes.size()];

        titledPanes.toArray(array);

        accordion = new Accordion(array);

        return accordion;
    } //getMediaAccordion

    /**
     * Returns a remove favorite menu item using the specified favorites resources, ID, and box.
     *
     * @param favoritesResources the favorites resources to be used in the operation
     * @param id the ID to be used in the operation
     * @param box the box to be used in the operation
     * @return a remove favorite menu item using the specified favorites resources, ID, and box
     * @throws NullPointerException if the specified favorites resources or box is {@code null}
     */
    private MenuItem createRemoveFavoriteMenuItem(FavoritesResources favoritesResources, long id, VBox box) {
        MenuItem removeFavoriteMenuItem;

        Objects.requireNonNull(favoritesResources, "the specified favorites resources is null");

        Objects.requireNonNull(box, "the specified box is null");

        removeFavoriteMenuItem = new MenuItem("Remove Favorite");

        removeFavoriteMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Status newStatus;
            TwitterPost newTwitterPost;

            try {
                newStatus = favoritesResources.destroyFavorite(id);

                newTwitterPost = new TwitterPost(newStatus);

                this.boxesToPosts.put(box, newTwitterPost);
            } catch (TwitterException e) {
                e.printStackTrace();
            } //end try catch
        });

        return removeFavoriteMenuItem;
    } //createRemoveFavoriteMenuItem

    /**
     * Returns a favorite menu item using the specified favorites resources, ID, and box.
     *
     * @param favoritesResources the favorites resources to be used in the operation
     * @param id the ID to be used in the operation
     * @param box the box to be used in the operation
     * @return a favorite menu item using the specified favorites resources, ID, and box
     * @throws NullPointerException if the specified favorites resources or box is {@code null}
     */
    private MenuItem createFavoriteMenuItem(FavoritesResources favoritesResources, long id, VBox box) {
        MenuItem favoriteMenuItem;

        Objects.requireNonNull(favoritesResources, "the specified favorites resources is null");

        Objects.requireNonNull(box, "the specified box is null");

        favoriteMenuItem = new MenuItem("Favorite");

        favoriteMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Status newStatus;
            TwitterPost newTwitterPost;

            try {
                newStatus = favoritesResources.createFavorite(id);

                newTwitterPost = new TwitterPost(newStatus);

                this.boxesToPosts.put(box, newTwitterPost);
            } catch (TwitterException e) {
                e.printStackTrace();
            } //end try catch
        });

        return favoriteMenuItem;
    } //createFavoriteMenuItem

    /**
     * Returns a remove retweet menu item using the specified Twitter, ID, and box.
     *
     * @param twitter the Twitter to be used in the operation
     * @param id the ID to be used in the operation
     * @param box the box to be used in the operation
     * @return a remove retweet menu item using the specified Twitter, ID, and box
     * @throws NullPointerException if the specified Twitter or box is {@code null}
     */
    private MenuItem createRemoveRetweetMenuItem(Twitter twitter, long id, VBox box) {
        MenuItem removeRetweetMenuItem;

        Objects.requireNonNull(twitter, "the specified Twitter is null");

        Objects.requireNonNull(box, "the specified box is null");

        removeRetweetMenuItem = new MenuItem("Remove Retweet");

        removeRetweetMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Status newStatus;
            TwitterPost newTwitterPost;

            try {
                newStatus = twitter.unRetweetStatus(id);

                newTwitterPost = new TwitterPost(newStatus);

                this.boxesToPosts.put(box, newTwitterPost);
            } catch (TwitterException e) {
                e.printStackTrace();
            } //end try catch
        });

        return removeRetweetMenuItem;
    } //createRemoveRetweetMenuItem

    /**
     * Returns a retweet menu item using the specified Twitter, ID, and box.
     *
     * @param twitter the Twitter to be used in the operation
     * @param id the ID to be used in the operation
     * @param box the box to be used in the operation
     * @return a retweet menu item using the specified Twitter, ID, and box
     * @throws NullPointerException if the specified Twitter or box is {@code null}
     */
    private MenuItem createRetweetMenuItem(Twitter twitter, long id, VBox box) {
        MenuItem retweetMenuItem;

        Objects.requireNonNull(twitter, "the specified Twitter is null");

        Objects.requireNonNull(box, "the specified box is null");

        retweetMenuItem = new MenuItem("Retweet");

        retweetMenuItem.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Status newStatus;
            TwitterPost newTwitterPost;

            try {
                newStatus = twitter.retweetStatus(id);

                newTwitterPost = new TwitterPost(newStatus);

                this.boxesToPosts.put(box, newTwitterPost);
            } catch (TwitterException e) {
                e.printStackTrace();
            } //end try catch
        });

        return retweetMenuItem;
    } //createRetweetMenuItem

    /**
     * Handles a mouse click on the specified box at the location of the specified x coordinate and y coordinate.
     *
     * @param box the box to be used in the operation
     * @param x the x coordinate to be used in the operation
     * @param y the y coordinate to be used in the operation
     * @throws NullPointerException if the specified box is {@code null}
     */
    private void handleMouseClick(VBox box, double x, double y) {
        Post post;
        TwitterPost twitterPost;
        Status status;
        long id;
        TwitterModel twitterModel;
        Twitter twitter;
        FavoritesResources favoritesResources;
        MenuItem menuItem0;
        MenuItem menuItem1;
        ContextMenu contextMenu;

        Objects.requireNonNull(box, "the specified box is null");

        post = this.boxesToPosts.get(box);

        if (post == null) {
            return;
        } else if (!(post instanceof TwitterPost)) {
            throw new IllegalStateException("a box is mapped to the wrong post type");
        } //end if

        twitterPost = (TwitterPost) post;

        status = twitterPost.getStatus();

        id = status.getId();

        twitterModel = this.model.getTwitterModel();

        twitter = twitterModel.getTwitter();

        favoritesResources = twitter.favorites();

        if (status.isFavorited()) {
            menuItem0 = this.createRemoveFavoriteMenuItem(favoritesResources, id, box);
        } else {
            menuItem0 = this.createFavoriteMenuItem(favoritesResources, id, box);
        } //end if

        if (status.isRetweeted()) {
            menuItem1 = this.createRemoveRetweetMenuItem(twitter, id, box);
        } else {
            menuItem1 = this.createRetweetMenuItem(twitter, id, box);
        } //end if

        contextMenu = new ContextMenu(menuItem0, menuItem1);

        contextMenu.show(box, x, y);
    } //handleMouseClick

    /**
     * Returns a box for the specified status.
     *
     * @param status the status to be used in the operation
     * @param displayTwitter whether or not to display "on Twitter" in the box
     * @return a box for the specified status
     * @throws NullPointerException if the specified status is {@code null}
     */
    private VBox createBox(Status status, boolean displayTwitter) {
        String name;
        String screenName;
        String textString;
        LocalDateTime dateTime;
        String combinedName;
        Label nameLabel;
        Scene scene;
        Text text;
        MediaEntity[] mediaEntities;
        Accordion accordion;
        String month;
        int day;
        int year;
        int hour;
        int minute;
        String amPm;
        String format;
        String dateTimeString;
        Label dateTimeLabel;
        VBox vBox;

        Objects.requireNonNull(status, "the specified status is null");

        name = status.getUser()
                     .getName();

        screenName = status.getUser()
                           .getScreenName();

        textString = status.getText();

        dateTime = status.getCreatedAt()
                         .toInstant()
                         .atZone(ZoneId.systemDefault())
                         .toLocalDateTime();

        combinedName = name + " @" + screenName;

        nameLabel = new Label(combinedName);

        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        scene = this.view.getScene();

        text = new Text(textString);

        text.setFont(Font.font("Arial", 14));

        text.wrappingWidthProperty()
            .bind(scene.widthProperty());

        mediaEntities = status.getMediaEntities();

        accordion = getMediaAccordion(mediaEntities);

        month = dateTime.getMonth()
                        .name();

        month = month.charAt(0) + month.substring(1)
                                       .toLowerCase();

        day = dateTime.getDayOfMonth();

        year = dateTime.getYear();

        hour = dateTime.get(ChronoField.CLOCK_HOUR_OF_AMPM);

        minute = dateTime.getMinute();

        amPm = (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) ? "AM" : "PM";

        if (displayTwitter) {
            format = "%s %d, %d at %02d:%02d %s on Twitter";
        } else {
            format = "%s %d, %d at %02d:%02d %s";
        } //end if

        dateTimeString = String.format(format, month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        if (accordion == null) {
            vBox = new VBox(nameLabel, text, dateTimeLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(nameLabel, text, accordion, dateTimeLabel);
        } //end if

        vBox.setOnContextMenuRequested((contextMenuEvent) -> {
            double screenX;
            double screenY;

            screenX = contextMenuEvent.getScreenX();

            screenY = contextMenuEvent.getScreenY();

            this.handleMouseClick(vBox, screenX, screenY);
        });

        return vBox;
    } //createPostBox

    /**
     * Updates the posts of this Twitter post controller.
     */
    public void updatePosts() {
        TwitterModel twitterModel;
        Twitter twitter;
        Paging paging;
        int count = 50;
        List<Status> statuses;
        List<Node> nodes;
        List<Node> nodeCopies;
        long id;
        VBox vBox;
        VBox vBoxCopy;
        TwitterPost post;
        PostView postView;
        VBox twitterBox;
        VBox allBox;

        twitterModel = this.model.getTwitterModel();

        if (twitterModel == null) {
            return;
        } //end if

        twitter = twitterModel.getTwitter();

        paging = new Paging();

        paging.setCount(count);

        try {
            statuses = twitter.getHomeTimeline(paging);
        } catch (TwitterException e) {
            e.printStackTrace();

            return;
        } //end try catch

        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        for (Status status : statuses) {
            id = status.getId();

            if (!this.ids.contains(id)) {
                this.ids.add(id);

                vBox = this.createBox(status, false);

                vBoxCopy = this.createBox(status, true);

                nodes.add(vBox);

                nodes.add(new Separator());

                nodeCopies.add(vBoxCopy);

                nodeCopies.add(new Separator());

                post = new TwitterPost(status);

                this.boxesToPosts.put(vBox, post);

                this.boxesToPosts.put(vBoxCopy, post);
            } //end if
        } //end for

        postView = this.view.getPostView();

        twitterBox = postView.getTwitterBox();

        allBox = postView.getAllBox();

        Platform.runLater(() -> twitterBox.getChildren()
                                          .addAll(0, nodes));

        Platform.runLater(() -> {
            this.allBoxLock.lock();

            try {
                allBox.getChildren()
                      .addAll(0, nodeCopies);
            } finally {
                this.allBoxLock.unlock();
            } //end try finally
        });
    } //updatePosts

    /**
     * Creates, and returns, a {@code TwitterPostController} object using the specified model, view, map from boxes to
     * posts, and all box lock.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @param boxesToPosts the map from boxes to posts to be used in the operation
     * @param allBoxLock the all box lock to be used in the operation
     * @return a {@code TwitterPostController} object using the specified model, view, map from boxes to posts, and all
     * box lock
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    public static TwitterPostController createTwitterPostController(Model model, View view,
                                                                    Map<VBox, Post> boxesToPosts, Lock allBoxLock) {
        return new TwitterPostController(model, view, boxesToPosts, allBoxLock);
    } //createTwitterPostController
}