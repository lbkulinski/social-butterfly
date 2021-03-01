package socialbutterfly;

import java.util.List;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;

public class UserRequests {

    Twitter twitter;

    public UserRequests() {
        this.twitter = null;
    }

    void setTwitter(Twitter twitter) { 
        this.twitter = twitter;
    }

    Twitter getTwitter() {
        return this.twitter;
    }

    List<Status> getTimeline(int length) throws Exception {
        /** This returns the first 20 tweets of the user's
         * timeline in the form of List<Status>. The way
         * in which to parse Status objects is shown in
         * the function timelineToString(List<Status>) 
         * below.
         */
        List<Status> statuses = twitter.getHomeTimeline();
        return statuses;
    }

    String timelineToString(List<Status> timeline) {
        String result = "";
        for (Status status : timeline) {
            result += status.getUser().getName() + ": " + status.getText() + "\n\n";
        }
        return result;
    }

    Status postTweet(StatusUpdate statusUpdate) throws TwitterException {
        /** Posts a new tweet to the user's account containing 
         * the information in statusUpdate. If a replytostatusid 
         * is included in statusUpdate, this tweet will be added 
         * as a reply to the specified tweet. 
         * 
         * @return status object that is created with the text 
         * paramater
         */
        Status status = twitter.updateStatus(statusUpdate);
        return status;
    }

    Status favoriteTweet(long id) throws TwitterException {
        /** Favorites the status specified by the id 
         *  paramater. 
         * 
         * @return the favorited status
         */
        Status status = twitter.createFavorite(id);
        return status;
    }
}
