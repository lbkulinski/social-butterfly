package com.butterfly.social.controller.reddit;

import com.butterfly.social.controller.Post;
import com.butterfly.social.model.Model;
import com.butterfly.social.model.reddit.RedditModel;
import com.butterfly.social.view.MenuView;
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
import java.util.concurrent.locks.Lock;

/**
 * A controller for Reddit posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version April 18, 2021
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

    private VBox allSavedBox;

    public boolean sortByTime = true;

    public boolean updateAll = false;

    /**
     * The executor service of this Reddit post controller.
     */
    private ScheduledExecutorService executorService;

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

        this.allSavedBox = null;

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

    public VBox getAllSavedBox() {
        return this.allSavedBox;
    }

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

        titledPane.setFont(Font.font(14));

        accordion = new Accordion(titledPane);

        return accordion;
    } //getMediaAccordion

    private void displayContextMenu(VBox box, double x, double y) {
        Post post;
        RedditPost redditPost;
        Submission submission;
        String id;
        RedditModel redditModel;
        RedditClient client;
        //FavoritesResources favoritesResources;
        MenuItem save;
        ContextMenu contextMenu;

        Objects.requireNonNull(box, "the specified box is null");

        post = this.boxesToPosts.get(box);

        if (post == null) {
            return;
        } else if (!(post instanceof RedditPost)) {
            throw new IllegalStateException("a box is mapped to the wrong post type");
        } //end if

        redditPost = (RedditPost) post;

        submission = redditPost.getSubmission();

        id = submission.getId();

        redditModel = this.model.getRedditModel();

        client = redditModel.getClient();

        //favoritesResources = twitter.favorites();

        save = new MenuItem("Save Post");

        save.addEventHandler(ActionEvent.ACTION, (actionEvent) -> {
            //save tweet
            redditModel.savePost(id);
        });

        contextMenu = new ContextMenu(save);

        contextMenu.show(box, x, y);
    } //displayContextMenu

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
        MenuView menuView;
        Spinner<Integer> fontSizeSpinner;
        int size;
        Text titleText;
        String family = "Tahoma";
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

        menuView = this.view.getMenuView();

        fontSizeSpinner = menuView.getFontSizeSpinner();

        size = fontSizeSpinner.getValue();

        titleText = new Text(title);

        titleText.setFont(Font.font(family, FontWeight.BOLD, size));

        titleText.wrappingWidthProperty()
                 .bind(scene.widthProperty());

        nameLabel = new Label(name);

        nameLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        text = new Text(textString);

        text.setFont(Font.font(14));

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

        dateTimeLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        format = "%d Upvotes";

        String upvotesString = String.format(format, submission.getScore());

        Label upvotesLabel = new Label(upvotesString);

        upvotesLabel.setFont(Font.font(family, FontWeight.BOLD, size));

        if (accordion == null) {
            vBox = new VBox(titleText, nameLabel, text, dateTimeLabel, upvotesLabel);
        } else {
            accordion.prefWidthProperty()
                     .bind(scene.widthProperty());

            vBox = new VBox(titleText, nameLabel, text, accordion, dateTimeLabel, upvotesLabel);
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

    /**
     * Updates the saved posts of this Reddit post controller.
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
            vBox = this.createBox(submission, false);

            vBoxCopy = this.createBox(submission, true);

            nodes.add(vBox);

            nodes.add(new Separator());

            nodeCopies.add(vBoxCopy);

            nodeCopies.add(new Separator());

            if (!this.savedIds.contains(id)) {
                this.savedIds.add(id);

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

        if(this.allSavedBox == null) {
            this.allSavedBox = new VBox();
        }
        System.out.println("Nodes size: " + nodes.size());
        redditBox.getChildren().addAll(0, nodes);
        System.out.println("Nodes COPY size: " + nodeCopies.size());
        allSavedBox.getChildren().addAll(0, nodeCopies);

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
        
        if(sortByTime) {
            paginator = client.frontPage()
            .sorting(SubredditSort.NEW)
            .limit(limit)
            .build();
        }
        else {
            paginator = client.frontPage()
            .sorting(SubredditSort.TOP)
            .limit(limit)
            .build();
        }


        nodes = new ArrayList<>();

        nodeCopies = new ArrayList<>();

        postView = this.view.getPostView();

        redditBox = postView.getRedditBox();

        allBox = postView.getAllBox();

        if(updateAll) {
            redditBox.getChildren().clear();
            this.ids.clear();
            boxesToPosts.clear();
            updateAll = false;
        }

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
     * Resets this Reddit post controller.
     */
    public void reset() {
        PostView postView;
        VBox redditBox;
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

        redditBox = postView.getRedditBox();

        entrySet = this.boxesToPosts.entrySet();

        iterator = entrySet.iterator();

        allBox = postView.getAllBox();

        redditBox.getChildren()
                 .clear();

        while (iterator.hasNext()) {
            entry = iterator.next();

            key = entry.getKey();

            value = entry.getValue();

            if (value instanceof RedditPost) {
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