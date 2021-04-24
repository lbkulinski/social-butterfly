package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.direct.DirectInboxRequest;
import com.github.instagram4j.instagram4j.requests.direct.DirectPendingInboxRequest;
import com.github.instagram4j.instagram4j.requests.direct.DirectThreadsBroadcastRequest;
import com.github.instagram4j.instagram4j.requests.feed.FeedTimelineRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaCommentRequest;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import com.github.instagram4j.instagram4j.responses.direct.DirectInboxResponse;
import okhttp3.Request;

import java.io.File;

public class InstagramUserRequests {
    private IGClient igClient;

    public InstagramUserRequests() {
        this.igClient = null;
    }

    public void setIgClient(IGClient igClient) {
        this.igClient = igClient;
    }

    private class IdObject {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public String post(File media, String caption) {
        /** Creates a post on the user's account with the
         *  specified media file and the caption paramater.
         *
         * @return id of the new post
         */

        IdObject idObj = new IdObject();

        igClient.actions().timeline()
                .uploadPhoto(media, caption)
                .thenAccept(res -> idObj.setId(res.getMedia().getId())).join();

        return idObj.getId();
    }

    public boolean comment(String postId, String message) {
        /** Adds a comment with the specified message
         *  to the post corrosponding to postId.
         *
         * @return if the comment was added successfully
         */
        IGResponse response = new MediaCommentRequest(postId, message).execute(igClient).join();
        return response.getStatus().equals("ok");
    }

    public boolean like(String postId) {
        /** Adds a like to the post corrosponding
         * to postId.
         *
         * @return if the like was added successfully
         */

        IGResponse response = new MediaActionRequest(postId, MediaActionRequest.MediaAction.LIKE).execute(igClient).join();
        return response.getStatus().equals("ok");
    }

    public String getPostAuthorInformation(TimelineMedia timelineMedia) {
        /** Obtains the user information for the post specified by
         * timelineMedia.
         *
         * @return the username and full name of the post author
         */
        String s = "Name: " +
                timelineMedia.getUser().getFull_name() +
                "\nScreen Name: " +
                timelineMedia.getUser().getUsername();
        return s;
    }

    public static void main(String[] args) {
        /* Setup Instagram Model */
        InstagramModel instagramModel = InstagramModel.createInstagramModel("SocialButterflyCS407", "socialbutterfly");
        InstagramUserRequests userRequests = new InstagramUserRequests();
        userRequests.setIgClient(instagramModel.getClient());

        File file = new File("/Users/anudeepyakkala/Desktop/image.jpg");
        String id = userRequests.post(file, "My first post!");  // Test posting
        userRequests.comment(id, "My first comment!");                      // Test replying to a post
        userRequests.like(id);                                                      // Test liking a post

        new FeedTimelineRequest().execute(instagramModel.getClient()).thenAccept(res -> {
            res.getFeed_items().forEach(item -> System.out.println(userRequests.getPostAuthorInformation(item)));  // Test getting user information from a post
        }).join();
    }
}
