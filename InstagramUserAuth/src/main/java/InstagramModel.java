import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedTimelineRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedTimelineResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InstagramModel {
    public static void main(String[] args) {

        InstagramModel instagramModel = new InstagramModel();

        IGClient client = instagramModel.login("SocialButterflyCS407", "socialbutterfly");

        System.out.println(instagramModel.getFullname(client));
        System.out.println(instagramModel.getUsername(client));
        System.out.println(instagramModel.getProfilePic(client));
        /*
        for (TimelineMedia timelineMedia : instagramModel.getPostFeed(client, 4)) {
            System.out.println(timelineMedia.toString());
            System.out.println("\n\n\n");
        }
        */
    }

    public IGClient login(String username, String password) {
        try {
            return IGClient.builder().username("SocialButterflyCS407").password("socialbutterfly").login();
        }
        catch (IGLoginException e) {
            System.out.println("Failed to login\n");
            return null;
        }
    }

    public Profile getProfile(IGClient client) {
        return client.getSelfProfile();
    }

    public String getUsername(IGClient client) {
        return client.getSelfProfile().getUsername();
    }

    public String getFullname(IGClient client) {
        return client.getSelfProfile().getFull_name();
    }

    public String getProfilePic(IGClient client) {
        return client.getSelfProfile().getProfile_pic_url();
    }

    public List<TimelineMedia> getPostFeed(IGClient client, int numPosts) {
        FeedIterable<FeedTimelineRequest, FeedTimelineResponse> feed = client.actions().timeline().feed();
        List<TimelineMedia> posts = new ArrayList<>();

        if (numPosts < 0) {
            for (FeedTimelineResponse feedTimelineResponse : feed) {
                posts.addAll(feedTimelineResponse.getFeed_items());
            }
        }
        else {
            Iterator it = feed.iterator();
            FeedTimelineResponse response = (FeedTimelineResponse) it.next();
            int counter = 0;
            while (counter < numPosts) {
                if (counter + response.getFeed_items().size() > numPosts) {
                    for (TimelineMedia post : response.getFeed_items()) {
                        posts.add(post);
                        counter++;
                        if (counter == numPosts) {
                            break;
                        }
                    }
                }
                else {
                    posts.addAll(response.getFeed_items());
                    counter += response.getFeed_items().size();
                    if (it.hasNext()) {
                        response = (FeedTimelineResponse) it.next();
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return posts;
    }
}
