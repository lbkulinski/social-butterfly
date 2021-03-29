package com.butterfly.social.model.reddit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubmissionKind;
import net.dean.jraw.references.InboxReference;
import net.dean.jraw.references.SubmissionReference;

public class RedditUserRequests {
    private RedditClient redditClient;

    public RedditUserRequests() {
        this.redditClient = null;
    }

    public void setRedditClient(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    public SubmissionReference post(String subreddit, String title, String content) {
        /** Creates a post to the specified subreddit with
         * the specified title and content.
         *
         * @return the submission reference of the post
         */

        return redditClient.subreddit(subreddit).submit(SubmissionKind.SELF, title, content, false);
    }

    public void comment(String id, String comment) {
        /** Adds a comment with the specified message
         *  to the post corresponding to the submissionReference.
         *
         */
        SubmissionReference submissionReference = redditClient.submission(id);
        submissionReference.reply(comment);
    }

    public void upvote(String id) {
        /** Upvotes the post corresponding to the
         * submissionReference.
         *
         */
        SubmissionReference submissionReference = redditClient.submission(id);
        submissionReference.upvote();
    }

    public String getUserInfo(String id) {
        /** @return the username of the author of the post
         * specified by id.
         *
         */
        SubmissionReference submissionReference = redditClient.submission(id);
        return submissionReference.inspect().getAuthor();
    }

    public void sendPrivateMessage(String username, String subject, String message) {
        /** Sends a message to the user specified by username
         * with the the title as @param subject and the content as
         * message.
         *
         */
        InboxReference inboxReference = redditClient.me().inbox();
        inboxReference.compose(username, subject, message);
    }

    public static void main(String[] args) {
        /* Setup Reddit Client */
        RedditModel redditModel = RedditModel.createRedditModel("cs408-spring-2021",
                "deZgyz-tekno2-dybxaf", "GaoCzV0A1aEvMA", "14CAWMVrhheFVi0n5XDgpAAxnZV5Fw");
        RedditUserRequests userRequests = new RedditUserRequests();
        userRequests.setRedditClient(redditModel.getClient());

        SubmissionReference submissionReference = userRequests.post("test", "My first Post!",
                "This is my first post!");                                                              // Test making a post
        userRequests.comment(submissionReference.inspect().getId(), "This is my first comment!");      // Test replying to a post
        userRequests.upvote(submissionReference.inspect().getId());                                             // Test upvoting a post

        System.out.println(userRequests.getUserInfo(submissionReference.inspect().getId()));                    // Test obtaining user information from a post


        userRequests.sendPrivateMessage("User_Simulator", "First message",
                "This is my first message!");                                                           // Test sending a private message
    }

}
