package com.butterfly.social.controller.instagram;

import com.butterfly.social.controller.Post;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.instagram.InstagramModel;
import com.butterfly.social.view.MenuView;
import com.butterfly.social.view.PostView;
import com.butterfly.social.view.View;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.media.ImageVersionsMeta;
import com.github.instagram4j.instagram4j.models.media.UserTags;
import com.github.instagram4j.instagram4j.models.media.VideoVersionsMeta;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.responses.feed.FeedTimelineResponse;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLOutput;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * A controller for Instagram posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version April 18, 2021
 */
public final class InstagramPostController {
    /**
     * The model of this Instagram post controller.
     */
    private final Model model;

    /**
     * The view of this Instagram post controller.
     */
    private final View view;

    /**
     * The IDs of this Instagram post controller.
     */
    private final Set<String> ids;

    private final Set<String> savedIds;

    /**
     * The map from boxes to posts of this Instagram post controller.
     */
    private final Map<VBox, Post> boxesToPosts;

    /**
     * The all box lock of this Instagram post controller.
     */
    private final Lock allBoxLock;

    /**
     * The executor service of this Instagram post controller.
     */
    private ScheduledExecutorService executorService;

    private VBox allSavedBox;

    public boolean sortByTime = true;

    public boolean updateAll = false;

    /**
     * Constructs a newly allocated {@code InstagramPostController} object with the specified model, view, map from
     * boxes to posts, and all box lock.
     *
     * @param model the model to be used in construction
     * @param view the view to be used in construction
     * @param boxesToPosts the map from boxes to posts to be used in construction
     * @param allBoxLock the all box lock to be used in construction
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    private InstagramPostController(Model model, View view, Map<VBox, Post> boxesToPosts, Lock allBoxLock) {
        Objects.requireNonNull(model, "the specified model is null");

        Objects.requireNonNull(view, "the specified view is null");

        Objects.requireNonNull(boxesToPosts, "the specified map from boxes to posts is null");

        Objects.requireNonNull(allBoxLock, "the specified all box lock is null");

        this.model = model;

        this.view = view;

        this.ids = new HashSet<>();

        this.savedIds = new HashSet<>();

        this.boxesToPosts = boxesToPosts;

        this.allSavedBox = new VBox();

        this.allBoxLock = allBoxLock;

        this.executorService = Executors.newSingleThreadScheduledExecutor();
    } //InstagramPostController

    /**
     * Returns the executor service of this Instagram post controller.
     *
     * @return the executor service of this Instagram post controller
     */
    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    } //getExecutorService

    public VBox getAllSavedBox() {
        //updateSavedPosts();
        return this.allSavedBox;
    }

    /**
     * Determines whether or not the specified media is an ad.
     *
     * @param media the media to be used in the operation
     * @return {@code true}, if the specified media is an ad and {@code false} otherwise
     * @throws NullPointerException if the specified media is {@code null}
     */
    private boolean isAd(TimelineMedia media) {
        Map<?, ?> map;
        String injectedKey = "injected";
        String label;
        String labelKey = "label";
        boolean ad = false;
        String sponsoredLabel = "Sponsored";

        Objects.requireNonNull(media, "the specified media is null");

        try {
            map = (Map<?, ?>) media.get(injectedKey);

            if (map != null) {
                label = (String) map.get(labelKey);

                ad = sponsoredLabel.equalsIgnoreCase(label);
            } //end if
        } catch (Exception e) {
            e.printStackTrace();
        } //end try catch

        return ad;
    } //isAd

    /**
     * Returns the photo node for the specified image media.
     *
     * @param imageMedia the image media to be used in the operation
     * @return the photo node for the specified image media
     * @throws NullPointerException if the specified image media is {@code null}
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
     * @throws NullPointerException if the specified image carousel item is {@code null}
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
     * @throws NullPointerException if the specified video media is {@code null}
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
     * @throws NullPointerException if the specified video carousel item is {@code null}
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
     * @throws NullPointerException if the specified media is {@code null}
     */
    private Accordion getMediaAccordion(TimelineMedia media) {
        List<TitledPane> titledPanes;
        List<Node> nodes;
        Node node;
        MenuView menuView;
        Spinner<Integer> fontSizeSpinner;
        int size;
        String mediaName;
        int mediaCount = 1;
        ScrollPane scrollPane;
        TitledPane titledPane;
        String family = "Tahoma";
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

        menuView = this.view.getMenuView();

        fontSizeSpinner = menuView.getFontSizeSpinner();

        size = fontSizeSpinner.getValue();

        for (Node currentNode : nodes) {
            mediaName = String.format("Attachment %d", mediaCount);

            mediaCount++;

            scrollPane = new ScrollPane(currentNode);

            titledPane = new TitledPane(mediaName, scrollPane);

            titledPane.setFont(Font.font(family, size));

            titledPanes.add(titledPane);
        } //end for

        array = new TitledPane[titledPanes.size()];

        titledPanes.toArray(array);

        accordion = new Accordion(array);

        return accordion;
    } //getMediaAccordion

    private void displayTaggedUsers(List<UserTags.UserTag> userTags) {
        InstagramModel instagramModel;
        Alert alert;
        String title = "Social Butterfly";
        String headerText = "Users tagged in this post:";
        String taggedUsers = "";

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            String message = "You are not signed into Instagram!";

            alert = new Alert(Alert.AlertType.ERROR, message);

            alert.show();

            return;
        } //end if

        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(headerText);

        for (int i = 0; i < userTags.size(); i++) {
            if (userTags.get(i) != null) {
                taggedUsers += "Name: " + userTags.get(i).getUser().getFull_name() + "\n" + "User: " + userTags.get(i).getUser().getUsername() + "\n";
            }
            else {
                break;
            }
        }

        alert.setContentText(taggedUsers);

        alert.show();
    } //searchUsersOnInstagram

    private void displayContextMenu(VBox box, double x, double y) {
        Post post;
        InstagramPost instagramPost;
        TimelineMedia media;
        String id;
        InstagramModel instagramModel;
        instagramModel = this.model.getInstagramModel();
        //RedditClient client;
        IGClient client = instagramModel.getClient();
        //FavoritesResources favoritesResources;
        MenuItem save;
        MenuItem tagged;
        ContextMenu contextMenu;

        Objects.requireNonNull(box, "the specified box is null");

        post = this.boxesToPosts.get(box);

        if (post == null) {
            return;
        } else if (!(post instanceof InstagramPost)) {
            throw new IllegalStateException("a box is mapped to the wrong post type");
        } //end if

        instagramPost = (InstagramPost) post;

        media = instagramPost.getMedia();

        id = media.getId();

        client = instagramModel.getClient();

        save = new MenuItem("Save Post");

        save.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            //save tweet
            instagramModel.savePost(id);
        });

        contextMenu = new ContextMenu(save);

        if (media.getUsertags() != null) {
            tagged = new MenuItem("Tagged users");

            tagged.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
                displayTaggedUsers(media.getUsertags().getIn());
            });

            contextMenu.getItems().add(tagged);
        }

        contextMenu.show(box, x, y);
    } //displayContextMenu

    /**
     * Returns a box for the specified media.
     *
     * @param media the media to be used in the operation
     * @param displayInstagram whether or not to display "on Instagram" in the box
     * @return a box for the specified media
     * @throws NullPointerException if the specified media is {@code null}
     */
    private VBox createBox(TimelineMedia media, boolean displayInstagram) {
        String username;
        String location;
        String textString;
        long creationTime;
        LocalDateTime dateTime;
        MenuView menuView;
        Spinner<Integer> fontSizeSpinner;
        int size;
        Label nameLabel;
        Label locationLabel;
        String family = "Tahoma";
        Scene scene;
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

        Objects.requireNonNull(media, "the specified status is null");

        username = media.getUser()
                        .getUsername();

        if (media.getLocation() != null) {
            location = media.getLocation().getName();
        }
        else {
            location = "";
        }

        textString = media.getCaption()
                          .getText();

        if (media.getUsertags() != null) {
            //System.out.println(media.getUsertags().getIn().get(0).getUser().getUsername() + "    " + media.getUsertags().getIn().get(0).getUser().getFull_name() + "\n"+ "\n"+ "\n");
        }

        creationTime = media.getCaption()
                            .getCreated_at_utc();

        dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationTime), ZoneId.systemDefault());

        menuView = this.view.getMenuView();

        fontSizeSpinner = menuView.getFontSizeSpinner();

        size = fontSizeSpinner.getValue();

        nameLabel = new Label(username);

        nameLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        locationLabel = new Label(location);

        scene = this.view.getScene();

        text = new Text(textString);

        text.setFont(Font.font(family, size));

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

        if (displayInstagram) {
            format = "%s %d, %d at %02d:%02d %s on Instagram";
        } else {
            format = "%s %d, %d at %02d:%02d %s";
        } //end if

        dateTimeString = String.format(format, month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        format = "%d Likes";

        String likesString = String.format(format, media.getLike_count());

        Label likesLabel = new Label(likesString);

        likesLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        format = "%d Comments";

        String commentsString = String.format(format, media.getComment_count());

        Label commentsLabel = new Label(commentsString);

        commentsLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        if (accordion == null) {
            vBox = new VBox(nameLabel, locationLabel, text, dateTimeLabel, likesLabel, commentsLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(nameLabel, locationLabel, text, accordion, dateTimeLabel, likesLabel, commentsLabel);
        } //end if

        vBox.setOnContextMenuRequested((contextMenuEvent) -> {
            double screenX;
            double screenY;

            screenX = contextMenuEvent.getScreenX();

            screenY = contextMenuEvent.getScreenY();

            this.displayContextMenu(vBox, screenX, screenY);
        });

        return vBox;
    } //createPostBox
    
    public Scene updateSavedPosts() {
        InstagramModel instagramModel;
        List<TimelineMedia> feedItems;
        List<Node> nodes;
        List<Node> nodeCopies;
        String id;
        VBox vBox;
        VBox vBoxCopy;
        InstagramPost post;
        VBox instagramBox;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            return null;
        } //end if

        feedItems = instagramModel.getSavedPosts();

        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        for (TimelineMedia media : feedItems) {
            id = media.getId();
            
            vBox = this.createBox(media, false);

            vBoxCopy = this.createBox(media, true);

            nodes.add(vBox);

            nodes.add(new Separator());

            nodeCopies.add(vBoxCopy);

            nodeCopies.add(new Separator());

            if (!this.savedIds.contains(id)) {
                this.savedIds.add(id);
                this.ids.add(id);

                post = new InstagramPost(media);

                this.boxesToPosts.put(vBox, post);

                this.boxesToPosts.put(vBoxCopy, post);
            } //end if
        } //end for

        instagramBox = new VBox();
        if(this.allSavedBox == null) {
            this.allSavedBox = new VBox();
        }

        instagramBox.getChildren().addAll(0, nodes);

        allSavedBox.getChildren().addAll(0, nodeCopies);

        Scene scene = new Scene(instagramBox, 500, 300);

        return scene;
    }

    /**
     * Updates the posts of this Instagram post controller.
     */
    public void updatePosts() {
        InstagramModel instagramModel;
        IGClient client;
        Iterable<FeedTimelineResponse> iterable;
        Comparator<TimelineMedia> comparator;
        Set<TimelineMedia> mediaSet;
        List<TimelineMedia> feedItems;
        List<Node> nodes;
        List<Node> nodeCopies;
        String id;
        VBox vBox;
        VBox vBoxCopy;
        InstagramPost post;
        int count = 0;
        int maxCount = 50;
        PostView postView;
        VBox instagramBox;
        VBox allBox;

        instagramModel = this.model.getInstagramModel();

        if (instagramModel == null) {
            return;
        } //end if

        client = instagramModel.getClient();

        iterable = client.actions()
                         .timeline()
                         .feed();

        if(sortByTime) {
            comparator = Comparator.<TimelineMedia>comparingLong(media -> media.getCaption()
            .getCreated_at_utc())
            .reversed();
        }
        else {
            comparator = Comparator.<TimelineMedia>comparingLong(media -> media.getLike_count())
            .reversed();
        }


        mediaSet = new TreeSet<>(comparator);

        postView = this.view.getPostView();

        instagramBox = postView.getInstagramBox();

        allBox = postView.getAllBox();

        if(updateAll) {
            instagramBox.getChildren().clear();
            this.ids.clear();
            boxesToPosts.clear();
            updateAll = false;
        }

        breakLoop:
        for (FeedTimelineResponse response : iterable) {
            feedItems = response.getFeed_items();

            for (TimelineMedia media : feedItems) {
                if (this.isAd(media)) {
                    continue;
                } //end if

                mediaSet.add(media);

                count++;

                if (count == maxCount) {
                    break breakLoop;
                } //end if
            } //end for
        } //end for

        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        for (TimelineMedia media : mediaSet) {
            id = media.getId();

            if (!this.ids.contains(id)) {
                this.ids.add(id);

                vBox = this.createBox(media, false);

                vBoxCopy = this.createBox(media, true);

                nodes.add(vBox);

                nodes.add(new Separator());

                nodeCopies.add(vBoxCopy);

                nodeCopies.add(new Separator());

                post = new InstagramPost(media);

                this.boxesToPosts.put(vBox, post);

                this.boxesToPosts.put(vBoxCopy, post);
            } //end if
        } //end for

        Platform.runLater(() -> instagramBox.getChildren()
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
     * Resets this Instagram post controller.
     */
    public void reset() {
        PostView postView;
        VBox instagramBox;
        Set<Map.Entry<VBox, Post>> entrySet;
        Iterator<Map.Entry<VBox, Post>> iterator;
        VBox allBox;
        Map.Entry<VBox, Post> entry;
        VBox key;
        Post value;
        List<Node> children;
        int separatorIndex;
        int size;

        this.executorService.shutdownNow();

        this.executorService = Executors.newSingleThreadScheduledExecutor();

        this.ids.clear();

        postView = this.view.getPostView();

        instagramBox = postView.getInstagramBox();

        entrySet = this.boxesToPosts.entrySet();

        iterator = entrySet.iterator();

        allBox = postView.getAllBox();

        instagramBox.getChildren()
                    .clear();

        while (iterator.hasNext()) {
            entry = iterator.next();

            key = entry.getKey();

            value = entry.getValue();

            if (value instanceof InstagramPost) {
                iterator.remove();

                this.allBoxLock.lock();

                try {
                    children = allBox.getChildren();

                    separatorIndex = children.indexOf(key) + 1;

                    size = children.size();

                    if (separatorIndex < size) {
                        children.remove(separatorIndex);
                    } //end if

                    children.remove(key);
                } finally {
                    this.allBoxLock.unlock();
                } //end try finally
            } //end if
        } //end while
    } //reset

    /**
     * Creates, and returns, a {@code InstagramPostController} object using the specified model, view, map from boxes
     * to posts, and all box lock.
     *
     * @param model the model to be used in the operation
     * @param view the view to be used in the operation
     * @param boxesToPosts the map from boxes to posts to be used in the operation
     * @param allBoxLock the all box lock to be used in the operation
     * @return a {@code InstagramPostController} object using the specified model, view, map from boxes to posts, and
     * all box lock
     * @throws NullPointerException if the specified model, view, map from boxes to posts, or all box lock is
     * {@code null}
     */
    public static InstagramPostController createInstagramPostController(Model model, View view,
                                                                        Map<VBox, Post> boxesToPosts,
                                                                        Lock allBoxLock) {
        return new InstagramPostController(model, view, boxesToPosts, allBoxLock);
    } //createInstagramPostController

    public Map<VBox, Post> getBoxesToPosts() {
        return this.boxesToPosts;
    }
}