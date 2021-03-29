package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.media.MediaActionRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaCommentRequest;
import com.github.instagram4j.instagram4j.responses.IGResponse;

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

    public String post(String fileName, String caption) {
        /** Creates a post on the user's account with the
         *  specified image file and the caption paramater.
         *
         * @return id of the new post
         */

        File file = new File(fileName);
        IdObject idObj = new IdObject();

        igClient.actions().timeline()
                .uploadPhoto(file, caption)
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

    public static void main(String[] args) {
        /* Setup Instagram Model */
        InstagramModel instagramModel = InstagramModel.createInstagramModel("SocialButterflyCS407", "socialbutterfly");
        InstagramUserRequests userRequests = new InstagramUserRequests();
        userRequests.setIgClient(instagramModel.getClient());

        String id = userRequests.post("image.jpg", "My first post!");  // Test posting
        userRequests.comment(id, "My first comment!");                      // Test replying to a post
        userRequests.like(id);                                                      // Test liking a post
    }
}
