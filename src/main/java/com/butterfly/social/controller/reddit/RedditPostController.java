package com.butterfly.social.controller.reddit;

import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.view.PostView;
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
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A controller for Reddit posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 4, 2021
 */
public final class RedditPostController {
    /**
     * The lock of this reddit post controller.
     */
    private static final Lock lock;

    /**
     * The reddit model of this reddit post controller.
     */
    private final RedditModel redditModel;

    /**
     * The post view of this reddit post controller.
     */
    private final PostView postView;

    /**
     * The background thread of this reddit post controller.
     */
    private Thread backgroundThread;

    static {
        lock = new ReentrantLock();
    } //static

    /**
     * Constructs a newly allocated {@code RedditPostController} object with the specified reddit model and post view.
     *
     * @param redditModel the reddit model to be used in construction
     * @param postView the post view to be used in construction
     * @throws NullPointerException if the specified reddit model or post view is {@code null}
     */
    private RedditPostController(RedditModel redditModel, PostView postView) {
        Objects.requireNonNull(redditModel, "the specified reddit model is null");

        Objects.requireNonNull(postView, "the specified post view is null");

        this.redditModel = redditModel;

        this.postView = postView;

        this.backgroundThread = null;
    } //RedditPostController

    /**
     * Returns the background thread of this reddit post controller.
     *
     * @return the background thread of this reddit post controller
     */
    public Thread getBackgroundThread() {
        return this.backgroundThread;
    } //getBackgroundThread

    /**
     * Returns a media accordion for the specified submission.
     *
     * @param submission the submission to be used in the operation
     * @return a media accordion for the specified submission
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
     * @return a box for the specified submission
     */
    private VBox createBox(Submission submission) {
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
        EmbeddedMedia embeddedMedia;
        Accordion accordion;
        String month;
        int day;
        int year;
        int hour;
        int minute;
        String amPm;
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

        scene = this.postView.getScene();

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

        embeddedMedia = submission.getEmbeddedMedia();

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

        dateTimeString = String.format("%s, %02d %d at %02d:%02d %s", month, day, year, hour, minute, amPm);

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
     * Creates, and returns, a {@code RedditPostController} object using the specified reddit model and post view.
     *
     * @param redditModel the reddit model to be used in the operation
     * @param postView the post view to be used in the operation
     * @return a {@code RedditPostController} object using the specified reddit model and post view
     * @throws NullPointerException if the specified reddit model or post view is {@code null}
     */
    public static RedditPostController createRedditPostController(RedditModel redditModel, PostView postView) {
        RedditPostController controller;
        Button refreshButton;
        VBox redditBox;
        RedditClient client;
        Map<String, Submission> idsToSubmissions;
        Set<String> ids;

        controller = new RedditPostController(redditModel, postView);

        refreshButton = controller.postView.getRefreshButton();

        redditBox = controller.postView.getRedditBox();

        client = controller.redditModel.getClient();

        idsToSubmissions = new HashMap<>();

        controller.backgroundThread = new Thread(() -> {
            DefaultPaginator<Submission> paginator;
            int limit = 200;
            String id;
            int maxCount = 200;
            int amount = 60_000;

            while (true) {
                paginator = client.frontPage()
                                  .sorting(SubredditSort.NEW)
                                  .limit(limit)
                                  .build();

                lock.lock();

                try {
                    breakLoop:
                    for (Listing<Submission> listing : paginator) {
                        for (Submission submission : listing) {
                            id = submission.getId();

                            idsToSubmissions.put(id, submission);

                            if (idsToSubmissions.size() == maxCount) {
                                break breakLoop;
                            } //end if
                        } //end for
                    } //end for
                } finally {
                    lock.unlock();
                } //end try finally

                try {
                    Thread.sleep(amount);
                } catch (InterruptedException e) {
                    return;
                } //end try catch
            } //end while
        });

        controller.backgroundThread.start();

        ids = new HashSet<>();

        refreshButton.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Comparator<Submission> comparator;
            Collection<Submission> values;
            Set<Submission> submissions;
            List<Node> nodes;
            String id;
            VBox vBox;

            comparator = Comparator.comparing(Submission::getCreated)
                                   .reversed();

            lock.lock();

            try {
                values = idsToSubmissions.values();

                submissions = new TreeSet<>(comparator);

                submissions.addAll(values);

                nodes = new ArrayList<>();

                for (Submission submission : submissions) {
                    id = submission.getId();

                    if (!ids.contains(id)) {
                        ids.add(id);

                        vBox = controller.createBox(submission);

                        nodes.add(vBox);

                        nodes.add(new Separator());
                    } //end if
                } //end for

                idsToSubmissions.clear();
            } finally {
                lock.unlock();
            } //end try finally

            redditBox.getChildren()
                     .addAll(0, nodes);
        });

        return controller;
    } //createRedditPostController
}