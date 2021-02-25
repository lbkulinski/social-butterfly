import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.temporal.ChronoField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.util.UUID;
import javafx.scene.control.Separator;

public final class PostViewExample extends Application {
    private static VBox createPostBox(String username, String content, Scene scene) {
        Label usernameLabel;
        Text contentText;
        LocalDateTime dateTime;
        String month;
        int day;
        int year;
        int hour;
        int minute;
        String amPm;
        String dateTimeString;
        Label dateTimeLabel;
        VBox vBox;

        usernameLabel = new Label(username);

        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        contentText = new Text(content);

        contentText.setFont(Font.font("Arial", 14));

        contentText.wrappingWidthProperty()
                   .bind(scene.widthProperty());

        dateTime = LocalDateTime.now();

        month = dateTime.getMonth()
                        .name();

        month = month.charAt(0) + month.substring(1)
                                       .toLowerCase();

        day = dateTime.getDayOfMonth();

        year = dateTime.getYear();

        hour = dateTime.get(ChronoField.CLOCK_HOUR_OF_AMPM);

        minute = dateTime.getMinute();

        if (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) {
            amPm = "AM";
        } else {
            amPm = "PM";
        } //end if

        dateTimeString = String.format("%s, %02d %d at %02d:%02d %s", month, day, year, hour, minute, amPm);

        dateTimeLabel = new Label(dateTimeString);

        dateTimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        vBox = new VBox(usernameLabel, contentText, dateTimeLabel);

        return vBox;
    } //createPostBox

    @Override
    public void start(Stage primaryStage) {
        PostView postView;
        Button refreshButton;
        VBox redditBox;
        VBox twitterBox;
        VBox instagramBox;
        VBox allBox;
        Scene scene;
        String title = "Social Butterfly";

        postView = PostView.createPostView(primaryStage);

        refreshButton = postView.getRefreshButton();

        redditBox = postView.getRedditBox();

        twitterBox = postView.getTwitterBox();

        instagramBox = postView.getInstagramBox();

        allBox = postView.getAllBox();

        scene = postView.getScene();

        refreshButton.setOnAction((actionEvent) -> {
            String username;
            String content;
            VBox postBox0;
            VBox postBox1;
            int postType;

            username = UUID.randomUUID()
                           .toString();

            content = """
                      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sed scelerisque nisi. Donec \
                      in mauris quis nibh ornare rutrum. Vivamus dapibus nisi velit, eget vehicula felis ultricies \
                      eget. Sed sed vestibulum metus. Sed ex nibh, rhoncus in finibus id, suscipit ut arcu. Quisque \
                      ac dapibus ipsum, eu lobortis elit. Donec nec eros a mi iaculis varius auctor eget leo. Fusce \
                      euismod enim nec nulla porta accumsan. Vestibulum tempus sem eget nisl venenatis, id posuere \
                      nunc porta.""";

            postBox0 = createPostBox(username, content, scene);

            postType = (int) (Math.random() * 3);

            switch (postType) {
                case 0 -> {
                    redditBox.getChildren()
                             .addAll(postBox0, new Separator());

                    username += " -- Reddit";
                } //case 0
                case 1 -> {
                    twitterBox.getChildren()
                              .addAll(postBox0, new Separator());

                    username += " -- Twitter";
                } //case 1
                case 2 -> {
                    instagramBox.getChildren()
                                .addAll(postBox0, new Separator());

                    username += " -- Instagram";
                } //case 2
            } //end switch

            postBox1 = createPostBox(username, content, scene);

            allBox.getChildren()
                  .addAll(postBox1, new Separator());
        });

        primaryStage.setTitle(title);

        primaryStage.setScene(scene);

        primaryStage.setWidth(500);

        primaryStage.setHeight(300);

        primaryStage.show();
    } //start
}