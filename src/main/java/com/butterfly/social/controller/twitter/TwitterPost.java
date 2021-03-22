package com.butterfly.social.controller.twitter;

import com.butterfly.social.controller.Post;
import twitter4j.Status;
import java.util.Objects;

/**
 * A Twitter post of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class TwitterPost implements Post {
    /**
     * The status of this Twitter post.
     */
    private final Status status;

    /**
     * Constructs a newly allocated {@code TwitterPost} object with the specified status.
     *
     * @param status the status to be used in construction
     * @throws NullPointerException if the specified status is {@code null}
     */
    public TwitterPost(Status status) {
        Objects.requireNonNull(status, "the specified status is null");

        this.status = status;
    } //TwitterPost

    /**
     * Returns the status of this Twitter post.
     *
     * @return the status of this Twitter post
     */
    public Status getStatus() {
        return this.status;
    } //getStatus

    /**
     * Returns the hash code of this Twitter post.
     *
     * @return the hash code of this Twitter post
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.status);
    } //hashCode

    /**
     * Determines whether or not the specified object is equal to this Twitter post. {@code true} is returned if and
     * only if the specified object is an instance of {@code TwitterPost} and its status is equal to this Twitter
     * post's.
     *
     * @param object the object to be used in the comparisons
     * @return {@code true}, if the specified object is equal to this Twitter post and {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof TwitterPost) {
            return Objects.equals(this.status, ((TwitterPost) object).status);
        } //end if

        return false;
    } //equals

    /**
     * Returns the {@code String} representation of this Twitter post. The {@code String} representations of two
     * Twitter posts are equal if and only if the Twitter posts are equal according to
     * {@link TwitterPost#equals(Object)}.
     *
     * @return the {@code String} representation of this Twitter post
     */
    @Override
    public String toString() {
        return "TwitterPost[status=%s]".formatted(this.status);
    } //toString
}
