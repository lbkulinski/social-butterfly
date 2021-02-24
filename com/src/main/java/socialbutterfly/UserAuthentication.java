package socialbutterfly;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.*;
import twitter4j.TwitterException;


public class UserAuthentication {

    final String consumerKey = "c3Ku51cVby3ITwZ6vaGgnqCzV";
    final String consumerSecret = "JK5CMek6mbUvFZpWtsww5RjdaMc74o9ifJxijT32cKB0cAwA60";
    Twitter twitter;
    RequestToken requestToken;
    AccessToken accessToken;

    public UserAuthentication() {
        this.twitter = TwitterFactory.getSingleton();
        this.twitter.setOAuthConsumer(consumerKey, consumerSecret);
        this.requestToken = null; /* This value will be initialized in getURL() */
        this.accessToken = null; /* This value will be initialized in handlePIN() */
    }

    String getURL() throws TwitterException {
        this.requestToken = this.twitter.getOAuthRequestToken();
        return this.requestToken.getAuthorizationURL();
    }

    Twitter getTwitter() {
        return this.twitter;
    }

    void handlePIN(String pin) {
        try{
            if(pin.length() > 0) {
                this.accessToken = this.twitter.getOAuthAccessToken(this.requestToken, pin);
            }
            else{
                this.accessToken = this.twitter.getOAuthAccessToken();
            }
        } catch (TwitterException te) {
            if(401 == te.getStatusCode()) {
                System.out.println("Unable to get the access token.");
            }
            else{
                te.printStackTrace();
            }
        }
        this.twitter.setOAuthAccessToken(this.accessToken);
    }
}