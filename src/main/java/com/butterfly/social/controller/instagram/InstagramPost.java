package com.butterfly.social.controller.instagram;

import com.butterfly.social.controller.Post;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import java.util.Objects;

/**
 * An Instagram post of the Social Butterfly application.
 *
 * @author Logan Kulinski, lbk@purdue.edu
 * @version March 21, 2021
 */
public final class InstagramPost implements Post {
    /**
     * The media of this Instagram post.
     */
    private final TimelineMedia media;

    /**
     * Constructs a newly allocated {@code InstagramPost} object with the specified media.
     *
     * @param media the media to be used in construction
     * @throws NullPointerException if the specified media is {@code null}
     */
    public InstagramPost(TimelineMedia media) {
        Objects.requireNonNull(media, "the specified media is null");

        this.media = media;
    } //InstagramPost

    /**
     * Returns the media of this Instagram post.
     *
     * @return the media of this Instagram post
     */
    public TimelineMedia getMedia() {
        return this.media;
    } //getMedia

    /**
     * Returns the hash code of this Instagram post.
     *
     * @return the hash code of this Instagram post
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.media);
    } //hashCode

    /**
     * Determines whether or not the specified object is equal to this Instagram post. {@code true} is returned if and
     * only if the specified object is an instance of {@code InstagramPost} and its media is equal to this Instagram
     * post's.
     *
     * @param object the object to be used in the comparisons
     * @return {@code true}, if the specified object is equal to this Instagram post and {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof InstagramPost) {
            return Objects.equals(this.media, ((InstagramPost) object).media);
        } //end if

        return false;
    } //equals

    /**
     * Returns the {@code String} representation of this Instagram post. The {@code String} representations of two
     * Instagram posts are equal if and only if the Instagram posts are equal according to
     * {@link InstagramPost#equals(Object)}.
     *
     * @return the {@code String} representation of this Instagram post
     */
    @Override
    public String toString() {
        return "InstagramPost[media=%s]".formatted(this.media);
    } //toString
}