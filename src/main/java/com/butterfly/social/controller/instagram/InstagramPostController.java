package com.butterfly.social.controller.instagram;

import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.view.PostView;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.models.media.ImageVersionsMeta;
import com.github.instagram4j.instagram4j.models.media.VideoVersionsMeta;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.requests.feed.FeedTimelineRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedTimelineResponse;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A controller for Instagram posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 3, 2021
 */
public final class InstagramPostController {
    /**
     * The lock of this instagram post controller.
     */
    private static final Lock lock;

    /**
     * The model of this instagram post controller.
     */
    private final InstagramModel instagramModel;

    /**
     * The post view of this instagram post controller.
     */
    private final PostView postView;

    /**
     * The background thread of this instagram post controller.
     */
    private Thread backgroundThread;

    static {
        lock = new ReentrantLock();
    } //static

    /**
     * Constructs a newly allocated {@code InstagramPostController} object with the specified instagram model and post
     * view.
     *
     * @param instagramModel the instagram model to be used in construction
     * @param postView the post view to be used in construction
     * @throws NullPointerException if the specified instagram model or post view is {@code null}
     */
    private InstagramPostController(InstagramModel instagramModel, PostView postView) {
        Objects.requireNonNull(instagramModel, "the specified instagram model is null");

        Objects.requireNonNull(postView, "the specified post view is null");

        this.instagramModel = instagramModel;

        this.postView = postView;

        this.backgroundThread = null;
    } //InstagramPostController

    /**
     * Returns the background thread of this instagram post controller.
     *
     * @return the background thread of this instagram post controller
     */
    public Thread getBackgroundThread() {
        return this.backgroundThread;
    } //getBackgroundThread

    /**
     * Returns the photo node for the specified image media.
     *
     * @param imageMedia the image media to be used in the operation
     * @return the photo node for the specified image media
     */
    private Node getPhotoNode(TimelineImageMedia imageMedia) {
        List<ImageVersionsMeta> metaList;
        int lastIndex;
        ImageVersionsMeta meta;
        String urlString;
        URI uri;
        String uriString;
        Image image;
        ImageView imageView;

        Objects.requireNonNull(imageMedia, "the specified image media is null");

        metaList = imageMedia.getImage_versions2()
                             .getCandidates();

        lastIndex = metaList.size() - 1;

        meta = metaList.get(lastIndex);

        urlString = meta.getUrl();

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
     * Returns the photo node for the specified image carousel item.
     *
     * @param imageCarouselItem the image carousel item to be used in the operation
     * @return the photo node for the specified image carousel item
     */
    private Node getPhotoNode(ImageCaraouselItem imageCarouselItem) {
        List<ImageVersionsMeta> metaList;
        int lastIndex;
        ImageVersionsMeta meta;
        String urlString;
        URI uri;
        String uriString;
        Image image;
        ImageView imageView;

        Objects.requireNonNull(imageCarouselItem, "the specified image carousel item is null");

        metaList = imageCarouselItem.getImage_versions2()
                                    .getCandidates();

        lastIndex = metaList.size() - 1;

        meta = metaList.get(lastIndex);

        urlString = meta.getUrl();

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
     * Returns the video node for the specified video media.
     *
     * @param videoMedia the video media to be used in the operation
     * @return the video node for the specified video media
     */
    private Node getVideoNode(TimelineVideoMedia videoMedia) {
        List<VideoVersionsMeta> metaList;
        int lastIndex;
        VideoVersionsMeta meta;
        String urlString;
        URI uri;
        String uriString;
        Media media;
        MediaPlayer mediaPlayer;
        MediaView mediaView;

        Objects.requireNonNull(videoMedia, "the specified video media is null");

        metaList = videoMedia.getVideo_versions();

        lastIndex = metaList.size() - 1;

        meta = metaList.get(lastIndex);

        urlString = meta.getUrl();

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
     * Returns the photo node for the specified video carousel item.
     *
     * @param videoCarouselItem the video carousel item to be used in the operation
     * @return the photo node for the specified video carousel item
     */
    private Node getVideoNode(VideoCaraouselItem videoCarouselItem) {
        List<VideoVersionsMeta> metaList;
        int lastIndex;
        VideoVersionsMeta meta;
        String urlString;
        URI uri;
        String uriString;
        Media media;
        MediaPlayer mediaPlayer;
        MediaView mediaView;

        Objects.requireNonNull(videoCarouselItem, "the specified video carousel item is null");

        metaList = videoCarouselItem.getVideo_versions();

        lastIndex = metaList.size() - 1;

        meta = metaList.get(lastIndex);

        urlString = meta.getUrl();

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
     * Returns an accordion of the specified media.
     *
     * @param media the media to be used in the operation
     * @return an accordion of the specified media
     */
    private Accordion getMediaAccordion(TimelineMedia media) {
        List<TitledPane> titledPanes;
        List<Node> nodes;
        Node node;
        String mediaName;
        int mediaCount = 1;
        ScrollPane scrollPane;
        TitledPane titledPane;
        TitledPane[] array;
        Accordion accordion;

        Objects.requireNonNull(media, "the specified media is null");

        titledPanes = new ArrayList<>();

        nodes = new ArrayList<>();

        if (media instanceof TimelineImageMedia) {
            node = this.getPhotoNode((TimelineImageMedia) media);

            nodes.add(node);
        } else if (media instanceof TimelineVideoMedia) {
            node = this.getVideoNode((TimelineVideoMedia) media);

            nodes.add(node);
        } else if (media instanceof TimelineCarouselMedia) {
            List<CaraouselItem> carouselItems;

            carouselItems = ((TimelineCarouselMedia) media).getCarousel_media();

            for (CaraouselItem carouselItem : carouselItems) {
                if (carouselItem instanceof ImageCaraouselItem) {
                    node = this.getPhotoNode((ImageCaraouselItem) carouselItem);
                } else if (carouselItem instanceof VideoCaraouselItem) {
                    node = this.getVideoNode((VideoCaraouselItem) carouselItem);
                } else {
                    String message;

                    message = String.format("Invalid carousel type: %s", media.getClass().getName());

                    throw new IllegalStateException(message);
                } //end if

                nodes.add(node);
            } //end for
        } else {
            return null;
        } //end if

        for (Node currentNode : nodes) {
            mediaName = String.format("Attachment %d", mediaCount);

            mediaCount++;

            scrollPane = new ScrollPane(currentNode);

            titledPane = new TitledPane(mediaName, scrollPane);

            titledPane.setFont(Font.font("Arial", 14));

            titledPanes.add(titledPane);
        } //end for

        array = new TitledPane[titledPanes.size()];

        titledPanes.toArray(array);

        accordion = new Accordion(array);

        return accordion;
    } //getMediaAccordion

    /**
     * Returns a box for the specified media.
     *
     * @param media the media to be used in the operation
     * @return a box for the specified media
     */
    private VBox createBox(TimelineMedia media) {
        String username;
        String textString;
        long creationTime;
        LocalDateTime dateTime;
        Label nameLabel;
        Scene scene;
        Text text;
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

        Objects.requireNonNull(media, "the specified status is null");

        username = media.getUser()
                        .getUsername();

        textString = media.getCaption()
                          .getText();

        creationTime = media.getCaption()
                            .getCreated_at_utc();

        dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationTime), ZoneId.systemDefault());

        nameLabel = new Label(username);

        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        scene = this.postView.getScene();

        text = new Text(textString);

        text.setFont(Font.font("Arial", 14));

        text.wrappingWidthProperty()
            .bind(scene.widthProperty());

        accordion = getMediaAccordion(media);

        month = dateTime.getMonth()
                        .name();

        month = month.charAt(0) + month.substring(1)
                                       .toLowerCase();

        day = dateTime.getDayOfMonth();

        year = dateTime.getYear();

        hour = dateTime.get(ChronoField.CLOCK_HOUR_OF_AMPM);

        minute = dateTime.getMinute();

        amPm = (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) ? "AM" : "PM";

        dateTimeString = String.format("%s, %d %d at %02d:%02d %s", month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        if (accordion == null) {
            vBox = new VBox(nameLabel, text, dateTimeLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(nameLabel, text, accordion, dateTimeLabel);
        } //end if

        vBox.setCache(true);

        vBox.setCacheHint(CacheHint.SPEED);

        return vBox;
    } //createPostBox

    /**
     * Creates, and returns, a {@code InstagramPostController} object using the specified instagram model and post
     * view.
     *
     * @param instagramModel the instagram model to be used in the operation
     * @param postView the post view to be used in the operation
     * @return a {@code InstagramPostController} object using the specified instagram model and post view
     * @throws NullPointerException if the specified instagram model or post view is {@code null}
     */
    public static InstagramPostController createInstagramPostController(InstagramModel instagramModel,
                                                                        PostView postView) {
        InstagramPostController controller;
        Button refreshButton;
        VBox instagramBox;
        IGClient client;
        Set<String> ids;
        Map<String, TimelineMedia> idsToMedia;

        controller = new InstagramPostController(instagramModel, postView);

        refreshButton = controller.postView.getRefreshButton();

        instagramBox = controller.postView.getInstagramBox();

        client = controller.instagramModel.getClient();

        ids = new HashSet<>();

        idsToMedia = new HashMap<>();

        controller.backgroundThread = new Thread(() -> {
            FeedIterable<FeedTimelineRequest, FeedTimelineResponse> feedIterable;
            List<TimelineMedia> mediaList;
            String id;
            int maxCount = 200;
            int amount = 60_000;

            while (true) {
                feedIterable = client.actions()
                                     .timeline()
                                     .feed();

                lock.lock();

                try {
                    breakLoop:
                    for (FeedTimelineResponse response : feedIterable) {
                        mediaList = response.getFeed_items();

                        for (TimelineMedia media : mediaList) {
                            id = media.getId();

                            idsToMedia.put(id, media);

                            if (idsToMedia.size() == maxCount) {
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

        refreshButton.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            Comparator<TimelineMedia> comparator;
            Collection<TimelineMedia> values;
            Set<TimelineMedia> mediaSet;
            List<Node> nodes;
            String id;
            VBox vBox;

            comparator = Comparator.<TimelineMedia>comparingLong((media -> media.getCaption()
                                                                                .getCreated_at_utc()))
                                   .reversed();

            lock.lock();

            try {
                values = idsToMedia.values();

                values = values.stream()
                               .filter(value -> Objects.nonNull(value.getCaption()))
                               .collect(Collectors.toCollection(HashSet::new));

                mediaSet = new TreeSet<>(comparator);

                mediaSet.addAll(values);

                nodes = new ArrayList<>();

                for (TimelineMedia media : mediaSet) {
                    id = media.getId();

                    if (!ids.contains(id)) {
                        ids.add(id);

                        vBox = controller.createBox(media);

                        nodes.add(vBox);

                        nodes.add(new Separator());
                    } //end if
                } //end for

                idsToMedia.clear();
            } finally {
                lock.unlock();
            } //end try finally

            instagramBox.getChildren()
                        .addAll(0, nodes);
        });

        return controller;
    } //createTwitterPostController
}