package de.pscom.pietsmiet.generic;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.pscom.pietsmiet.util.PostType;

import static de.pscom.pietsmiet.util.ColorUtils.Default;
import static de.pscom.pietsmiet.util.ColorUtils.Facebook;
import static de.pscom.pietsmiet.util.ColorUtils.PietSmiet;
import static de.pscom.pietsmiet.util.ColorUtils.Twitter;
import static de.pscom.pietsmiet.util.ColorUtils.Youtube;
import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.STREAM;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class Post implements Comparable<Post> {
    private String description;
    private String title;
    private int postType;
    @Nullable
    private Drawable thumbnail;
    private Date datetime;
    private int duration;

    public Post() {
    }

    /**
     * Creates a new post item
     *
     * @param title       Title of the post
     * @param description Description or message of the post
     * @param datetime    Time of the post
     * @param postType    Type of the post
     */

    public Post(String title, String description, Date datetime, @PostType.TypeNoThumbnail int postType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.postType = postType;
    }


    /**
     * Creates a new post item with a thumbnail
     *
     * @param title       Title of the post
     * @param description Description or message of the post
     * @param datetime    Time of the post
     * @param postType    Type of the post
     * @param thumbnail   Thumbnail image
     */
    public Post(String title, String description, Date datetime, @Nullable Drawable thumbnail, @PostType.TypeThumbnail int postType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.postType = postType;
    }

    /**
     * Creates a new post item with a thumbnail
     *
     * @param title       Title of the post
     * @param description Description or message of the post
     * @param datetime    Time of the post
     * @param postType    Type of the post
     * @param duration    Duration of the video / pietcast
     * @param thumbnail   Thumbnail image
     */
    public Post(String title, String description, Date datetime, @Nullable Drawable thumbnail, int duration, @PostType.TypeThumbnail int postType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.postType = postType;
        this.duration = duration;
    }

    @Nullable
    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(@Nullable Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean hasThumbnail(){
        return thumbnail != null;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int type) {
        this.postType = type;
    }

    public Date getDate() {
        return datetime;
    }

    public void setDatetime(@NonNull Date datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    //UPPER_CASE: PostType constants
    //CamelCase: ColorUtils constants
    public int getBackgroundColor() {
        String hexColor;
        switch (postType) {
            case VIDEO:
            case STREAM:
                hexColor = Youtube;
                break;
            case UPLOAD_PLAN:
            case PIETCAST:
                hexColor = PietSmiet;
                break;
            case FACEBOOK:
                hexColor = Facebook;
                break;
            case TWITTER:
                hexColor = Twitter;
                break;
            default:
                hexColor = Default;
                break;
        }
        return Color.parseColor(hexColor);
    }

    public boolean isThumbnailView() {
        return postType == VIDEO
                || postType == STREAM
                || postType == PIETCAST;
    }

    @Override
    public int compareTo(@NonNull Post item) {
        if (item.getDate() == null) return -1;
        else if (this.getDate() == null) return 1;
        return item.getDate().compareTo(this.getDate());
    }

    @Override
    public int hashCode() {
        int result = 5;
        int random = 87;
        result = random * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = random * result + getDescription().hashCode();
        result = random * result + Long.valueOf(getDate().getTime()).intValue();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Post other = (Post) obj;
        if (!this.getDescription().equals(other.getDescription())) return false;
        else if (!this.getTitle().equals(other.getTitle())) return false;
        else if (this.getDate().getTime() != other.getDate().getTime()) return false;
        return true;
    }
}
