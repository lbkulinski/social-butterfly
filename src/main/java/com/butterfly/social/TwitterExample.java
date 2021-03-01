package com.butterfly.social;

import javafx.application.Application;
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
import javafx.stage.Stage;
import twitter4j.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class TwitterExample extends Application {
    private static final Lock lock;

    static {
        lock = new ReentrantLock();
    } //static

    private User getUser() throws TwitterException {
        File file;
        User user;
        String url;
        TextInputDialog inputDialog;
        String pin;

        file = new File("user.ser");

        if (file.exists()) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                return ((User) inputStream.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } //end try catch
        } //end if

        user = new User();

        url = user.auth.getURL();

        this.getHostServices()
            .showDocument(url);

        inputDialog = new TextInputDialog();

        inputDialog.setHeaderText("Please enter the pin:");

        inputDialog.showAndWait();

        pin = inputDialog.getResult();

        if (pin == null) {
            System.out.println("Error: pin is null! Goodbye!");

            return null;
        } //end if

        user.auth.handlePIN(pin);

        user.initializeRequests();

        return user;
    } //getUser

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

    private VBox createPostBox(Status status, Scene scene) {
        String name;
        String screenName;
        String textString;
        LocalDateTime dateTime;
        String combinedName;
        Label nameLabel;
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

        Objects.requireNonNull(scene, "the specified scene is null");

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        PostView postView;
        Scene scene;
        Button refreshButton;
        VBox twitterBox;
        Set<Long> ids;
        User user;
        Map<Long, Status> idsToStatuses;
        TwitterListener twitterListener;
        AsyncTwitterFactory factory;
        AsyncTwitter asyncTwitter;
        Thread backgroundThread;

        String title = "Social Butterfly";

        postView = PostView.createPostView(primaryStage);

        scene = postView.getScene();

        refreshButton = postView.getRefreshButton();

        twitterBox = postView.getTwitterBox();

        ids = new HashSet<>();

        user = this.getUser();

        if (user == null) {
            return;
        } //end if

        idsToStatuses = new LinkedHashMap<>();

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

        factory = new AsyncTwitterFactory();

        asyncTwitter = factory.getInstance(user.requests.twitter);

        asyncTwitter.addListener(twitterListener);

        backgroundThread = new Thread(() -> {
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

        backgroundThread.start();

        refreshButton.setOnAction((actionEvent) -> {
            Collection<Status> values;
            List<Status> statuses;
            List<Node> nodes;
            long id;
            VBox vBox;

            lock.lock();

            try {
                values = idsToStatuses.values();

                statuses = new ArrayList<>(values);

                nodes = new ArrayList<>();

                for (Status status : statuses) {
                    id = status.getId();

                    if (!ids.contains(id)) {
                        ids.add(id);

                        vBox = this.createPostBox(status, scene);

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

        primaryStage.setOnCloseRequest((windowEvent -> {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("user.ser"))) {
                outputStream.writeObject(user);

                backgroundThread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } //end try catch
        }));

        primaryStage.setTitle(title);

        primaryStage.setScene(scene);

        primaryStage.setWidth(500);

        primaryStage.setHeight(300);

        primaryStage.show();
    } //main
}