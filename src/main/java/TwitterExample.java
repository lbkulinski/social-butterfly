import javafx.application.Application;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.butterfly.social.User;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import twitter4j.TwitterException;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import twitter4j.Status;
import javafx.scene.Scene;
import java.time.LocalDateTime;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import javafx.stage.Stage;
import com.butterfly.social.PostView;
import javafx.scene.control.Button;
import twitter4j.RateLimitStatus;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import javafx.scene.control.Separator;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

public final class TwitterExample extends Application {
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

    private VBox createPostBox(Status status, Scene scene) {
        String name;
        String screenName;
        String textString;
        LocalDateTime dateTime;
        String combinedName;
        Label nameLabel;
        Text text;
        String month;
        int day;
        int year;
        int hour;
        int minute;
        String amPm;
        String dateTimeString;
        Label dateTimeLabel;
        VBox vBox;

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

        vBox = new VBox(nameLabel, text, dateTimeLabel);

        return vBox;
    } //createPostBox

    @Override
    public void start(Stage primaryStage) throws Exception {
        PostView postView;
        Scene scene;
        Button refreshButton;
        VBox twitterBox;
        User user;
        String title = "Social Butterfly";

        postView = PostView.createPostView(primaryStage);

        scene = postView.getScene();

        refreshButton = postView.getRefreshButton();

        twitterBox = postView.getTwitterBox();

        user = this.getUser();

        if (user == null) {
            return;
        } //end if

        refreshButton.setOnAction((actionEvent) -> {
            RateLimitStatus rateLimitStatus = null;
            String key = "/statuses/home_timeline";
            int remaining;
            List<Status> statuses;
            List<Node> nodes;
            VBox vBox;

            try {
                rateLimitStatus = user.requests.twitter.getRateLimitStatus()
                                                       .get(key);
            } catch (TwitterException e) {
                e.printStackTrace();
            } //end try catch

            if (rateLimitStatus == null) {
                String errorMessage;
                Alert alert;

                errorMessage = "Error: Unable to refresh! Please wait!";

                alert = new Alert(Alert.AlertType.ERROR, errorMessage);

                alert.showAndWait();

                return;
            } //end if

            remaining = rateLimitStatus.getRemaining();

            if (remaining == 0) {
                int seconds;
                String errorMessage;
                Alert alert;

                seconds = rateLimitStatus.getSecondsUntilReset();

                errorMessage = String.format("Error: Unable to refresh! Please wait %s seconds!", seconds);

                alert = new Alert(Alert.AlertType.ERROR, errorMessage);

                alert.showAndWait();

                return;
            } //end if

            try {
                statuses = user.requests.getTimeline(20);
            } catch (Exception e) {
                return;
            } //end try catch

            nodes = new ArrayList<>();

            for (Status status : statuses) {
                vBox = this.createPostBox(status, scene);

                nodes.add(vBox);

                nodes.add(new Separator());
            } //end for

            twitterBox.getChildren()
                      .clear();

            twitterBox.getChildren()
                      .addAll(nodes);
        });

        primaryStage.setOnCloseRequest((windowEvent -> {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("user.ser"))) {
                outputStream.writeObject(user);
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