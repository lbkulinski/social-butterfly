package com.butterfly.social.controller.reddit;

import com.butterfly.social.controller.Post;
import net.dean.jraw.models.Submission;
import java.util.Objects;

/**
 * A Reddit post of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class RedditPost implements Post {
    /**
     * The submission of this Reddit post.
     */
    private final Submission submission;

    /**
     * Constructs a newly allocated {@code RedditPost} object with the specified submission.
     *
     * @param submission the submission to be used in construction
     * @throws NullPointerException if the specified submission is {@code null}
     */
    public RedditPost(Submission submission) {
        Objects.requireNonNull(submission, "the specified submission is null");

        this.submission = submission;
    } //RedditPost

    /**
     * Returns the submission of this Reddit post.
     *
     * @return the submission of this Reddit post
     */
    public Submission getSubmission() {
        return this.submission;
    } //getSubmission

    /**
     * Returns the hash code of this Reddit post.
     *
     * @return the hash code of this Reddit post
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.submission);
    } //hashCode

    /**
     * Determines whether or not the specified object is equal to this Reddit post. {@code true} is returned if and
     * only if the specified object is an instance of {@code RedditPost} and its submission is equal to this Reddit
     * post's.
     *
     * @param object the object to be used in the comparisons
     * @return {@code true}, if the specified object is equal to this Reddit post and {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof RedditPost) {
            return Objects.equals(this.submission, ((RedditPost) object).submission);
        } //end if

        return false;
    } //equals

    /**
     * Returns the {@code String} representation of this Reddit post. The {@code String} representations of two
     * Reddit posts are equal if and only if the Reddit posts are equal according to {@link RedditPost#equals(Object)}.
     *
     * @return the {@code String} representation of this Reddit post
     */
    @Override
    public String toString() {
        return "RedditPost[submission=%s]".formatted(this.submission);
    } //toString
}