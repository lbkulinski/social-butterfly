import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.feed.FeedTimelineRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaCommentRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest.MediaAction;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import com.github.instagram4j.instagram4j.responses.feed.FeedTimelineResponse;

import java.io.File;
import java.nio.file.Files;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
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

        String id = instagramModel.post("path to image file", "My first post!", client);
        instagramModel.comment(id, "My first comment!", client);
        instagramModel.like(id, client);
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
    private class IdObject {
        String id;
        public IdObject(String id) {
            this.id = id;
        }
    }

    public String post(String fileName, String caption, IGClient client) {
        /** Creates a post on the user's account with the 
         *  specified image file and the caption paramater. 
         * 
         * @return id of the new post
         */

        File file = new File(fileName);
        IdObject idObj = new IdObject("");

        client.actions().timeline()
        .uploadPhoto(file, caption)
        .thenAccept(res -> {
            idObj.id = res.getMedia().getId();
        }).join();

        return idObj.id;
    }

    public boolean comment(String postId, String message, IGClient client) {
        /** Adds a comment with the specified message
         *  to the post corrosponding to postId.
         * 
         * @return if the comment was added successfully 
         */
        IGResponse response = new MediaCommentRequest(postId, message).execute(client).join();
        return response.getStatus().equals("ok");
    }

    public boolean like(String postId, IGClient client) {
        /** Adds a like to the post corrosponding
         * to postId.
         * 
         * @return if the like was added successfully
         */

        IGResponse response = new MediaActionRequest(postId, MediaAction.LIKE).execute(client).join();
        return response.getStatus().equals("ok");
    }
}
