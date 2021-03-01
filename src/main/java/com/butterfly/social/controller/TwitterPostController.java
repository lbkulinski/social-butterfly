package com.butterfly.social.controller;

import java.util.concurrent.locks.Lock;
import com.butterfly.social.model.TwitterModel;
import com.butterfly.social.view.PostView;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Objects;
import javafx.scene.control.Accordion;
import twitter4j.MediaEntity;
import java.util.List;
import javafx.scene.control.TitledPane;
import java.net.URI;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.util.Arrays;
import java.util.Comparator;
import java.net.URISyntaxException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import twitter4j.Status;
import java.time.LocalDateTime;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import java.time.ZoneId;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.temporal.ChronoField;
import javafx.scene.control.Button;
import java.util.Set;
import java.util.Map;
import twitter4j.TwitterListener;
import twitter4j.Twitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.AsyncTwitter;
import java.util.HashSet;
import java.util.HashMap;
import twitter4j.TwitterAdapter;
import twitter4j.ResponseList;
import twitter4j.Paging;
import java.util.Collection;
import java.util.TreeSet;
import javafx.scene.control.Separator;

/**
 * A controller for Twitter posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 1, 2021
 */
public final class TwitterPostController {
    /**
     * The lock of this twitter post controller.
     */
    private static final Lock lock;

    /**
     * The twitter model of this twitter post controller.
     */
    private final TwitterModel twitterModel;

    /**
     * The post view of this twitter post controller.
     */
    private final PostView postView;

    /**
     * The background thread of this twitter post controller.
     */
    private Thread backgroundThread;

    static {
        lock = new ReentrantLock();
    } //static

    /**
     * Constructs a newly allocated {@code TwitterPostController} object with the specified twitter model and post
     * view.
     *
     * @param twitterModel the twitter model to be used in construction
     * @param postView the post view to be used in construction
     * @throws NullPointerException if the specified twitter model or post view is {@code null}
     */
    private TwitterPostController(TwitterModel twitterModel, PostView postView) {
        Objects.requireNonNull(twitterModel, "the specified twitter model is null");

        Objects.requireNonNull(twitterModel, "the specified post view is null");

        this.twitterModel = twitterModel;

        this.postView = postView;

        this.backgroundThread = null;
    } //TwitterPostController

    /**
     * Returns the background thread of this twitter post controller.
     *
     * @return the background thread of this twitter post controller
     */
    public Thread getBackgroundThread() {
        return this.backgroundThread;
    } //getBackgroundThread

    /**
     * Returns an accordion of the specified media entities.
     *
     * @param mediaEntities the media entities to be used in the operation
     * @return an accordion of the specified media entities
     */
    private Accordion getMediaAccordion(MediaEntity[] mediaEntities) {
        List<TitledPane> titledPanes;
        String type;
        String videoType = "video";
        String urlString;
        URI uri;
        String uriString;
        Node node;
        String mediaName;
        int mediaCount = 0;
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

            if (Objects.equals(type, videoType)) {
                MediaEntity.Variant[] variants;
                int lastIndex;
                Media media;
                MediaPlayer mediaPlayer;
                MediaView mediaView;

                variants = mediaEntity.getVideoVariants();

                Arrays.sort(variants, Comparator.comparing(MediaEntity.Variant::getBitrate));

                lastIndex = variants.length - 1;

                urlString = variants[lastIndex].getUrl();

                try {
                    uri = new URI(urlString);

                    uriString = uri.toString();
                } catch (URISyntaxException e) {
                    e.printStackTrace();

                    continue;
                } //end try catch

                media = new Media(uriString);

                mediaPlayer = new MediaPlayer(media);

                mediaView = new MediaView(mediaPlayer);

                mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);

                mediaView.setOnMouseClicked((mouseEvent) -> {
                    MediaPlayer.Status status;

                    status = mediaPlayer.getStatus();

                    switch (status) {
                        case PLAYING: {
                            mediaPlayer.pause();

                            break;
                        } //case PLAYING
                        case READY:
                        case PAUSED:
                        case STOPPED: {
                            mediaPlayer.play();
                        } //case STOPPED
                    } //end switch
                });

                node = mediaView;
            } else {
                Image image;
                ImageView imageView;

                urlString = mediaEntity.getMediaURLHttps();

                try {
                    uri = new URI(urlString);

                    uriString = uri.toString();
                } catch (URISyntaxException e) {
                    e.printStackTrace();

                    continue;
                } //end try catch

                image = new Image(uriString);

                imageView = new ImageView(image);

                node = imageView;
            } //end if

            mediaName = String.valueOf(mediaCount);

            mediaCount++;

            scrollPane = new ScrollPane(node);

            titledPane = new TitledPane(mediaName, scrollPane);

            titledPanes.add(titledPane);
        } //end for

        array = new TitledPane[titledPanes.size()];

        titledPanes.toArray(array);

        accordion = new Accordion(array);

        return accordion;
    } //getMediaAccordion

    /**
     * Returns a box for the specified status.
     *
     * @param status the status to be used in the operation
     * @return a box for the specified status
     */
    private VBox createBox(Status status) {
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

        scene = this.postView.getScene();

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

        dateTimeString = String.format("%s, %02d %d at %02d:%02d %s", month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        if (accordion == null) {
            vBox = new VBox(nameLabel, text, dateTimeLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(nameLabel, text, accordion, dateTimeLabel);
        } //end if

        return vBox;
    } //createPostBox

    /**
     * Creates, and returns, a {@code TwitterPostController} object using the specified twitter model and post view.
     *
     * @param twitterModel the twitter model to be used in the operation
     * @param postView the post view to be used in the operation
     * @return a {@code TwitterPostController} object using the specified twitter model and post view
     * @throws NullPointerException if the specified twitter model or post view is {@code null}
     */
    public static TwitterPostController createTwitterPostController(TwitterModel twitterModel, PostView postView) {
        TwitterPostController controller;
        Button refreshButton;
        VBox twitterBox;
        Set<Long> ids;
        Map<Long, Status> idsToStatuses;
        TwitterListener twitterListener;
        Twitter twitter;
        AsyncTwitterFactory factory;
        AsyncTwitter asyncTwitter;

        controller = new TwitterPostController(twitterModel, postView);

        refreshButton = controller.postView.getRefreshButton();

        twitterBox = postView.getTwitterBox();

        ids = new HashSet<>();

        idsToStatuses = new HashMap<>();

        twitterListener = new TwitterAdapter() {
            @Override
            public void gotHomeTimeline(ResponseList<Status> statuses) {
                long id;

                lock.lock();

                try {
                    for (Status status : statuses) {
                        id = status.getId();

                        idsToStatuses.put(id, status);
                    } //end for
                } finally {
                    lock.unlock();
                } //end try finally
            } //gotHomeTimeline
        };

        twitter = controller.twitterModel.getTwitter();

        factory = new AsyncTwitterFactory();

        asyncTwitter = factory.getInstance(twitter);

        asyncTwitter.addListener(twitterListener);

        controller.backgroundThread = new Thread(() -> {
            Paging paging;
            int count = 200;
            int amount = 60_000;

            paging = new Paging();

            paging.setCount(count);

            while (true) {
                asyncTwitter.getHomeTimeline(paging);

                try {
                    Thread.sleep(amount);
                } catch (InterruptedException e) {
                    return;
                } //end try catch
            } //end while
        });

        controller.backgroundThread.start();

        refreshButton.setOnAction((actionEvent) -> {
            Comparator<Status> comparator;
            Collection<Status> values;
            Set<Status> statuses;
            List<Node> nodes;
            long id;
            VBox vBox;

            comparator = Comparator.comparing(Status::getCreatedAt)
                                   .reversed();

            lock.lock();

            try {
                values = idsToStatuses.values();

                statuses = new TreeSet<>(comparator);

                statuses.addAll(values);

                nodes = new ArrayList<>();

                for (Status status : statuses) {
                    id = status.getId();

                    if (!ids.contains(id)) {
                        ids.add(id);

                        vBox = controller.createBox(status);

                        nodes.add(vBox);

                        nodes.add(new Separator());
                    } //end if
                } //end for

                idsToStatuses.clear();
            } finally {
                lock.unlock();
            } //end try finally

            twitterBox.getChildren()
                      .addAll(0, nodes);
        });

        return controller;
    } //createTwitterPostController
}