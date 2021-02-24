package socialbutterfly;

import java.util.*;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws Exception{
        /* Here I've included a sample flow of how this authentication is handled */
        User user = new User();
        String url = user.auth.getURL(); /* This stores the URL needed to sign in and authenticate */
        System.out.println(url);

        /**  Here the app will launch the URL and the user will sign in
         *   and be presented with a PIN that they must enter on a
         *   new screen that the app will present for input
         */
        Scanner in = new Scanner(System.in);
        String pin = in.nextLine();
        user.auth.handlePIN(pin); /* This will complete authentication of the UserAuthentication class */
        user.initializeRequests(); /* Now the requests object is ready to be used */
        String timeline = user.requests.timelineToString(user.requests.getTimeline(10));
        System.out.println(timeline);
        /** The TweetFactory object (twitter) will be the one used to complete requests */
    }
}
