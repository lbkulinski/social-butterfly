package socialbutterfly;

import java.util.List;

import twitter4j.Status;
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
}
