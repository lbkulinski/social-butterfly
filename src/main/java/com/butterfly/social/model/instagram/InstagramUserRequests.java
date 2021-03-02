package com.butterfly.social.model.instagram;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.feed.FeedIterable;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedTimelineRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedTimelineResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class InstagramUserRequests implements Serializable {
    private IGClient client;

    public InstagramUserRequests() {
        this.client = null;
    } //InstagramUserRequests

    public void setClient(IGClient client) {
        this.client = client;
    } //setClient

    public IGClient getClient() {
        return this.client;
    } //getClient

    public List<TimelineMedia> getPostFeed(int numPosts) {
        FeedIterable<FeedTimelineRequest, FeedTimelineResponse> feed = getClient().actions().timeline().feed();
        List<TimelineMedia> posts = new ArrayList<>();

        if (numPosts < 0) {
            for (FeedTimelineResponse feedTimelineResponse : feed) {
                posts.addAll(feedTimelineResponse.getFeed_items());
            } //end for
        } else {
            Iterator<FeedTimelineResponse> it = feed.iterator();
            FeedTimelineResponse response = it.next();
            int counter = 0;

            while (counter < numPosts) {
                if (counter + response.getFeed_items().size() > numPosts) {
                    for (TimelineMedia post : response.getFeed_items()) {
                        posts.add(post);

                        counter++;

                        if (counter == numPosts) {
                            break;
                        } //end if
                    } //end for
                } else {
                    posts.addAll(response.getFeed_items());

                    counter += response.getFeed_items().size();

                    if (it.hasNext()) {
                        response = it.next();
                    } else {
                        break;
                    } //end if
                } //end if
            } //end while
        } //end if

        return posts;
    } //getPostFeed
}