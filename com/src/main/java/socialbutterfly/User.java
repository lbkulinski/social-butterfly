package socialbutterfly;

public class User {
    UserAuthentication auth;
    UserRequests requests;

    public User() { /* This class holds the authentication and requests objects */
        this.auth = new UserAuthentication();
        this.requests = new UserRequests();
    }

    void initializeRequests() { /* This must be done after authentication flow is complete */
        this.requests.setTwitter(this.auth.getTwitter());
    }
}
