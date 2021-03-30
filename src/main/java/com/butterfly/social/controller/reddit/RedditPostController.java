package com.butterfly.social.controller.reddit;

import com.butterfly.social.controller.Post;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import javafx.application.Platform;
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
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * A controller for Reddit posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 22, 2021
 */
public final class RedditPostController {
    /**
     * The model of this Reddit post controller.
     */
    private final Model model;

    /**
     * The view of this Reddit post controller.
     */
    private final View view;

    /**
     * The IDs of this Reddit post controller.
     */
    private final Set<String> ids;

    private final Set<String> savedIds;
    /**
     * The map from boxes to posts of this Reddit post controller.
     */
    private final Map<VBox, Post> boxesToPosts;

    /**
     * The all box lock of this Reddit post controller.
     */
    private final Lock allBoxLock;

    /**
     * The executor service of this Reddit post controller.
     */
    private final ScheduledExecutorService executorService;

    /**
     * Constructs a newly allocated {@code RedditPostController} object with the specified model, view, map from boxes
     * to posts, and all box lock.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @param boxesToPosts the map from boxes to posts to be used in construction
     * @param allBoxLock the all box lock to be used in construction
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    private RedditPostController(Model model, View view, Map<VBox, Post> boxesToPosts, Lock allBoxLock) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        Objects.requireNonNull(boxesToPosts, "the specified map from boxes to posts is null");

        Objects.requireNonNull(allBoxLock, "the specified all box lock is null");

        this.model = model;

        this.view = view;

        this.ids = new HashSet<>();

        this.savedIds = new HashSet<>();

        this.boxesToPosts = boxesToPosts;

        this.allBoxLock = allBoxLock;

        this.executorService = Executors.newSingleThreadScheduledExecutor();
    } //RedditPostController

    /**
     * Returns the executor service of this Reddit post controller.
     *
     * @return the executor service of this Reddit post controller
     */
    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    } //getExecutorService

    /**
     * Returns a media accordion for the specified submission.
     *
     * @param submission the submission to be used in the operation
     * @return a media accordion for the specified submission
     * @throws NullPointerException if the specified submission is {@code null}
     */
    private Accordion getMediaAccordion(Submission submission) {
        String urlString;
        EmbeddedMedia embeddedMedia;
        String imageUrl = "i.redd.it";
        Node node;
        ScrollPane scrollPane;
        TitledPane titledPane;
        String mediaName = "Attachment 0";
        Accordion accordion;

        Objects.requireNonNull(submission, "the specified submission is null");

        urlString = submission.getUrl();

        embeddedMedia = submission.getEmbeddedMedia();

        if (urlString.contains(imageUrl)) {
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

            image = new Image(uriString, true);

            imageView = new ImageView(image);

            imageView.setCache(true);

            imageView.setCacheHint(CacheHint.SPEED);

            node = imageView;
        } else if (embeddedMedia != null) {
            EmbeddedMedia.RedditVideo video;
            URI uri;
            String uriString;
            Media media;
            MediaPlayer mediaPlayer;
            MediaView mediaView;

            video = embeddedMedia.getRedditVideo();

            if (video == null) {
                return null;
            } //end if

            urlString = video.getHlsUrl();

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

            node = mediaView;
        } else {
            return null;
        } //end if

        scrollPane = new ScrollPane(node);

        titledPane = new TitledPane(mediaName, scrollPane);

        titledPane.setFont(Font.font("Arial", 14));

        accordion = new Accordion(titledPane);

        return accordion;
    } //getMediaAccordion

    /**
     * Returns a box for the specified submission.
     *
     * @param submission the submission to be used in the operation
     * @param displayReddit whether or not to display "on Reddit" in the box
     * @return a box for the specified submission
     * @throws NullPointerException if the specified submission is {@code null}
     */
    private VBox createBox(Submission submission, boolean displayReddit) {
        String title;
        String author;
        String subreddit;
        String name;
        String textString;
        LocalDateTime dateTime;
        Scene scene;
        Text titleText;
        Label nameLabel;
        Text text;
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

        Objects.requireNonNull(submission, "the specified submission is null");

        title = submission.getTitle();

        author = submission.getAuthor();

        subreddit = submission.getSubreddit();

        name = String.format("by %s in r/%s", author, subreddit);

        textString = submission.getSelfText();

        dateTime = submission.getCreated()
                             .toInstant()
                             .atZone(ZoneId.systemDefault())
                             .toLocalDateTime();

        scene = this.view.getScene();

        titleText = new Text(title);

        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        titleText.wrappingWidthProperty()
                 .bind(scene.widthProperty());

        nameLabel = new Label(name);

        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        text = new Text(textString);

        text.setFont(Font.font("Arial", 14));

        text.wrappingWidthProperty()
            .bind(scene.widthProperty());

        accordion = getMediaAccordion(submission);

        month = dateTime.getMonth()
                        .name();

        month = month.charAt(0) + month.substring(1)
                                       .toLowerCase();

        day = dateTime.getDayOfMonth();

        year = dateTime.getYear();

        hour = dateTime.get(ChronoField.CLOCK_HOUR_OF_AMPM);

        minute = dateTime.getMinute();

        amPm = (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) ? "AM" : "PM";

        if (displayReddit) {
            format = "%s %d, %d at %02d:%02d %s on Reddit";
        } else {
            format = "%s %d, %d at %02d:%02d %s";
        } //end if

        dateTimeString = String.format(format, month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        if (accordion == null) {
            vBox = new VBox(titleText, nameLabel, text, dateTimeLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(titleText, nameLabel, text, accordion, dateTimeLabel);
        } //end if

        return vBox;
    } //createPostBox

    /**
     * Updates the posts of this Reddit post controller.
     */
    public Scene updateSavedPosts() {
        RedditModel redditModel;
        RedditClient client;
        List<Node> nodes;
        List<Node> nodeCopies;
        String id;
        VBox vBox;
        VBox vBoxCopy;
        RedditPost post;
        int count = 0;
        int maxCount = 50;
        PostView postView;
        VBox redditBox;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            return null;
        } //end if

        client = redditModel.getClient();

        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        List<PublicContribution> listing = redditModel.getSavedPosts();

        for (PublicContribution pc : listing) {
            id = pc.getId();
            Submission submission = client.submission(id).inspect();
            if (!this.savedIds.contains(id)) {
                this.savedIds.add(id);

                vBox = this.createBox(submission, false);

                vBoxCopy = this.createBox(submission, true);

                nodes.add(vBox);

                nodes.add(new Separator());

                nodeCopies.add(vBoxCopy);

                nodeCopies.add(new Separator());

                post = new RedditPost(submission);

                this.boxesToPosts.put(vBox, post);

                this.boxesToPosts.put(vBoxCopy, post);
            } //end if

            count++;

            if (count == maxCount) {
                break;
            } //end if
        } //end for

        redditBox = new VBox();

        redditBox.getChildren().addAll(0, nodes);

        Scene scene = new Scene(redditBox, 500, 300);

        return scene;

    } //updatePosts


    /**
     * Updates the posts of this Reddit post controller.
     */
    public void updatePosts() {
        RedditModel redditModel;
        RedditClient client;
        DefaultPaginator<Submission> paginator;
        int limit = 50;
        List<Node> nodes;
        List<Node> nodeCopies;
        String id;
        VBox vBox;
        VBox vBoxCopy;
        RedditPost post;
        int count = 0;
        int maxCount = 50;
        PostView postView;
        VBox redditBox;
        VBox allBox;

        redditModel = this.model.getRedditModel();

        if (redditModel == null) {
            return;
        } //end if

        client = redditModel.getClient();

        paginator = client.frontPage()
                          .sorting(SubredditSort.NEW)
                          .limit(limit)
                          .build();

        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        breakLoop:
        for (Listing<Submission> listing : paginator) {
            for (Submission submission : listing) {
                id = submission.getId();

                if (!this.ids.contains(id)) {
                    this.ids.add(id);

                    vBox = this.createBox(submission, false);

                    vBoxCopy = this.createBox(submission, true);

                    nodes.add(vBox);

                    nodes.add(new Separator());

                    nodeCopies.add(vBoxCopy);

                    nodeCopies.add(new Separator());

                    post = new RedditPost(submission);

                    this.boxesToPosts.put(vBox, post);

                    this.boxesToPosts.put(vBoxCopy, post);
                } //end if

                count++;

                if (count == maxCount) {
                    break breakLoop;
                } //end if
            } //end for
        } //end for

        postView = this.view.getPostView();

        redditBox = postView.getRedditBox();

        allBox = postView.getAllBox();

        Platform.runLater(() -> redditBox.getChildren()
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
     * Creates, and returns, a {@code createRedditPostController} object using the specified model, view, map from
     * boxes to posts, and all box lock.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @param boxesToPosts the map from boxes to posts to be used in the operation
     * @param allBoxLock the all box lock to be used in the operation
     * @return a {@code createRedditPostController} object using the specified model, view, map from boxes to posts,
     * and all box lock
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    public static RedditPostController createRedditPostController(Model model, View view, Map<VBox, Post> boxesToPosts,
                                                                  Lock allBoxLock) {
        return new RedditPostController(model, view, boxesToPosts, allBoxLock);
    } //createRedditPostController
}