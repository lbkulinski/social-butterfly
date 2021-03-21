package com.butterfly.social.controller.reddit;

import com.butterfly.social.model.Model;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
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
 * @version March 21, 2021
 */
public final class RedditPostController {
    /**
     * The lock of this Reddit post controller.
     */
    private static final Lock lock;

    /**
     * The model of this Reddit post controller.
     */
    private final Model model;

    /**
     * The view of this Reddit post controller.
     */
    private final View view;

    /**
     * The background thread of this Reddit post controller.
     */
    private Thread backgroundThread;

    static {
        lock = new ReentrantLock();
    } //static

    /**
     * Constructs a newly allocated {@code RedditPostController} object with the specified model and view.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @throws NullPointerException if the specified model or view is {@code null}
     */
    private RedditPostController(Model model, View view) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        this.model = model;

        this.view = view;

        this.backgroundThread = null;
    } //RedditPostController

    /**
     * Returns the background thread of this Reddit post controller.
     *
     * @return the background thread of this Reddit post controller
     */
    public Thread getBackgroundThread() {
        return this.backgroundThread;
    } //getBackgroundThread

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
            format = "%s, %02d %d at %02d:%02d %s on Reddit";
        } else {
            format = "%s, %02d %d at %02d:%02d %s";
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
     * Creates, and returns, a {@code RedditPostController} object using the specified model, view, and all box lock.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @param allBoxLock the all box lock to be used in the operation
     * @return a {@code RedditPostController} object using the specified model, view, and all box lock
     * @throws NullPointerException if the specified model, view, or all box lock is {@code null}
     */
    public static RedditPostController createRedditPostController(Model model, View view, Lock allBoxLock) {
        RedditPostController controller;
        PostView postView;
        Button refreshButton;
        VBox redditBox;
        VBox allBox;
        Map<String, Submission> idsToSubmissions;
        Set<String> ids;

        controller = new RedditPostController(model, view);

        postView = controller.view.getPostView();

        refreshButton = postView.getRefreshButton();

        redditBox = postView.getRedditBox();

        allBox = postView.getAllBox();

        idsToSubmissions = new HashMap<>();

        controller.backgroundThread = new Thread(() -> {
            RedditModel redditModel;
            RedditClient client;
            DefaultPaginator<Submission> paginator;
            int limit = 200;
            String id;
            int maxCount = 200;
            int amount = 60_000;

            while (true) {
                redditModel = controller.model.getRedditModel();

                if (redditModel != null) {
                    client = redditModel.getClient();

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
                } //end if

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
            List<Node> nodeCopies;
            String id;
            VBox vBox;
            VBox vBoxCopy;

            comparator = Comparator.comparing(Submission::getCreated)
                                   .reversed();

            lock.lock();

            try {
                values = idsToSubmissions.values();

                submissions = new TreeSet<>(comparator);

                submissions.addAll(values);

                nodes = new ArrayList<>();

                nodeCopies = new ArrayList<>();

                for (Submission submission : submissions) {
                    id = submission.getId();

                    if (!ids.contains(id)) {
                        ids.add(id);

                        vBox = controller.createBox(submission, false);

                        vBoxCopy = controller.createBox(submission, true);

                        nodes.add(vBox);

                        nodes.add(new Separator());

                        nodeCopies.add(vBoxCopy);

                        nodeCopies.add(new Separator());
                    } //end if
                } //end for

                idsToSubmissions.clear();
            } finally {
                lock.unlock();
            } //end try finally

            redditBox.getChildren()
                     .addAll(0, nodes);

            allBoxLock.lock();

            try {
                allBox.getChildren()
                      .addAll(0, nodeCopies);
            } finally {
                allBoxLock.unlock();
            } //end try finally
        });

        return controller;
    } //createRedditPostController
}