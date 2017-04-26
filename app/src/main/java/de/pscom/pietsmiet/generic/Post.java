package de.pscom.pietsmiet.generic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.Date;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.YOUTUBE;

public class Post implements Comparable<Post> {

    private static final String strTitleIsNotGiven = "Title is not given";
    private static final String strDateIsNotGiven ="Date is not given";
    private static final String strUploadplanWihNoDescription = "Uploadplan with no description";
    private static final String strNotAValidType ="Not a valid type!";

    @Nullable
    private String description;
    private String title;
    private int postType;
    @Nullable
    private Drawable thumbnail;
    @Nullable
    private String thumbnailUrl;
    @Nullable
    private String thumbnailHDUrl;
    @Nullable
    private String username;
    
    private long api_ID;
    private Date datetime;
    private int duration;
    private String url;
    private boolean isThumbnailHD = false;

    //todo remove bad getter / setter methods -> Performance
    private Post(PostBuilder builder) {
        description = builder.description;
        title = builder.title;
        postType = builder.postType;
        thumbnailUrl = builder.thumbnailUrl;
        thumbnailHDUrl = builder.thumbnailHDUrl;
        username = builder.username;
        datetime = builder.date;
        duration = builder.duration;
        url = builder.url;
        api_ID = builder.api_ID;
    }

    @Nullable
    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(@Nullable Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isThumbnailHD() { return isThumbnailHD; }

    public void setIsThumbnailHD(boolean isHD) { this.isThumbnailHD = isHD; }

    @Nullable
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Nullable
    public String getThumbnailHDUrl() {
        return thumbnailHDUrl;
    }

    public int getPostType() {
        return postType;
    }

    public Date getDate() {
        return datetime;
    }

    @Nullable
    public String getUsername() { return username; }

    @Nullable
    public String getDescription() {
        return description;
    }

    public long getId() {
        return api_ID;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

    //UPPER_CASE: PostType constants
    //CamelCase: ColorUtils constants
    public int getBackgroundColor(Context c) {
        int hexColor;
        switch (postType) {
            case PS_VIDEO:
                hexColor = ContextCompat.getColor(c, R.color.pietsmiet);
                break;
            case YOUTUBE:
                hexColor = ContextCompat.getColor(c, R.color.youtube);
                break;
            case UPLOADPLAN:
            case NEWS:
            case PIETCAST:
                hexColor = ContextCompat.getColor(c, R.color.pietsmiet);
                break;
            case FACEBOOK:
                hexColor = ContextCompat.getColor(c, R.color.facebook);
                break;
            case TWITTER:
                hexColor = ContextCompat.getColor(c, R.color.twitter);
                break;
            default:
                hexColor = ContextCompat.getColor(c, R.color.pietsmiet);
                break;
        }
        return hexColor;
    }

    @Override
    public int compareTo(@NonNull Post item) {
        return item.getDate().compareTo(this.getDate());
    }

    @Override
    public int hashCode() {
        int result = 5;
        int random = 87;
        result = random * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = random * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = random * result + Long.valueOf(getDate().getTime()).intValue();
        result = random * result + getPostType();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        Post other = (Post) obj;
        if (this.getDescription() != null) {
            if (other.getDescription() == null) return false;
            if (!this.getDescription().equals(other.getDescription())) return false;
        } else if (other.getDescription() != null) return false;

        if (this.getTitle() != null) {
            if (other.getTitle() == null) return false;
            if (!this.getTitle().equals(other.getTitle())) return false;
        } else if (other.getTitle() != null) return false;

        if (this.getDate().getTime() != other.getDate().getTime()) return false;
        else if (this.getPostType() != other.getPostType()) return false;
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class PostBuilder {
        private String title;
        private int postType;
        @Nullable
        private String description;
        @Nullable
        private String thumbnailUrl;
        @Nullable
        private String thumbnailHDUrl;
        @Nullable
        private String username;
        private long api_ID;
        private Date date;
        private int duration;
        private String url;


        public PostBuilder(@PostType.AllTypes int postType) {
            this.postType = postType;
        }

        public PostBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public PostBuilder username(@Nullable String username) {
            this.username = username;
            return this;
        }

        public PostBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostBuilder thumbnailUrl(@Nullable String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public PostBuilder thumbnailHDUrl(@Nullable String thumbnailHDUrl) {
            this.thumbnailHDUrl = thumbnailHDUrl;
            return this;
        }

        public PostBuilder id(long api_ID) {
            this.api_ID = api_ID;
            return this;
        }

        public PostBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public PostBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public PostBuilder url(String url) {
            this.url = url;
            return this;
        }

        public Post build() {
            if (title == null || title.isEmpty()) {
                PsLog.e(strTitleIsNotGiven);
                return null;
            }
            if (date == null) {
                PsLog.e(strDateIsNotGiven);
                return null;
            }
            if (postType == UPLOADPLAN && (description == null || description.isEmpty())) {
                PsLog.e(strUploadplanWihNoDescription);
                return null;
            }

            if(!PostType.getPossibleTypes().contains(postType)){
                PsLog.e(strNotAValidType);
                return null;
            }
            return new Post(this);
        }
    }
}
