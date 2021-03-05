package com.butterfly.social.view;

import com.butterfly.social.model.reddit.RedditModel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import net.dean.jraw.models.Trophy;

import java.util.List;
import java.util.Objects;

public class RedditProfileView {
    public static void createRedditProfileView (RedditModel redditModel) {
        Stage redditProfile = new Stage();
        redditProfile.setTitle("Reddit Profile");
        Text usernameLabel = new Text(10, 40, "Username");
        usernameLabel.setFont(new Font (40));

        Text usernameText = new Text(5, 20,"u/" + redditModel.getUsername());
        usernameText.setFont(new Font(20));

        Text trophiesLabel = new Text(10, 40, "Trophies");
        trophiesLabel.setFont(new Font(40));

        List<Trophy> trophiesList = redditModel.getClient().user(redditModel.getUsername()).trophies();
        Text trophies;
        if (trophiesList.isEmpty()) {
            trophies = new Text(5,20, "No trophies yet!");
        }
        else {
            trophies = new Text(5,20,trophiesList.toString());
        }

        trophies.setFont(new Font(20));

        Text karmaLabel = new Text(10, 40, "Karma");
        karmaLabel.setFont(new Font(40));
        int commentKarma = redditModel.getClient().user(redditModel.getUsername()).query().getAccount().getCommentKarma();
        int linkKarma = redditModel.getClient().user(redditModel.getUsername()).query().getAccount().getLinkKarma();
        int totalKarma = commentKarma + linkKarma;
        String totalKarmaS = Integer.toString(totalKarma);
        Text totalKarmaText = new Text(5, 20, totalKarmaS);
        totalKarmaText.setFont(new Font(20));


        VBox vBox = new VBox(usernameLabel,usernameText,trophiesLabel,trophies, karmaLabel, totalKarmaText);
        Scene scene = new Scene(vBox, 500, 700);
        redditProfile.setScene(scene);
        redditProfile.show();
    }
}
