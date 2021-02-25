import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

/**
 * A view for posts of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * February 25, 2021
 */
public final class PostView {
    /**
     * The main box of this post view.
     */
    private final VBox mainBox;

    /**
     * The refresh button of this post view.
     */
    private final Button refreshButton;

    /**
     * The tab pane of this post view.
     */
    private final TabPane tabPane;

    /**
     * The Reddit box of this post view.
     */
    private final VBox redditBox;

    /**
     * The Twitter box of this post view.
     */
    private final VBox twitterBox;

    /**
     * The Instagram box of this post view.
     */
    private final VBox instagramBox;

    /**
     * The all box of this post view.
     */
    private final VBox allBox;

    /**
     * The scene of this post view.
     */
    private final Scene scene;

    /**
     * Constructs a newly allocated {@code PostView} object.
     */
    private PostView() {
        String buttonText = "Refresh";

        this.mainBox = new VBox();

        this.refreshButton = new Button(buttonText);

        this.tabPane = new TabPane();

        this.redditBox = new VBox();

        this.twitterBox = new VBox();

        this.instagramBox = new VBox();

        this.allBox = new VBox();

        this.scene = new Scene(this.mainBox);
    } //PostView

    /**
     * Returns the main box of this post view.
     *
     * @return the main box of this post view
     */
    public VBox getMainBox() {
        return this.mainBox;
    } //getMainBox

    /**
     * Returns the refresh button of this post view.
     *
     * @return the refresh button of this post view
     */
    public Button getRefreshButton() {
        return this.refreshButton;
    } //getRefreshButton

    /**
     * Returns the tab pane of this post view.
     *
     * @return the tab pane of this post view
     */
    public TabPane getTabPane() {
        return this.tabPane;
    } //getTabPane

    /**
     * Returns the Reddit box of this post view.
     *
     * @return the Reddit box of this post view
     */
    public VBox getRedditBox() {
        return this.redditBox;
    } //getRedditBox

    /**
     * Returns the Twitter box of this post view.
     *
     * @return the Twitter box of this post view
     */
    public VBox getTwitterBox() {
        return this.twitterBox;
    } //getTwitterBox

    /**
     * Returns the Instagram box of this post view.
     *
     * @return the Instagram box of this post view
     */
    public VBox getInstagramBox() {
        return this.instagramBox;
    } //getInstagramBox

    /**
     * Returns the all box of this post view.
     *
     * @return the all box of this post view
     */
    public VBox getAllBox() {
        return this.allBox;
    } //getAllBox

    /**
     * Returns the scene of this post view.
     *
     * @return the scene of this post view
     */
    public Scene getScene() {
        return this.scene;
    } //getScene
}