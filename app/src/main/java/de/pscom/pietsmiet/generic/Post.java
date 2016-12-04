package de.pscom.pietsmiet.generic;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.ColorUtils.Default;
import static de.pscom.pietsmiet.util.ColorUtils.Facebook;
import static de.pscom.pietsmiet.util.ColorUtils.PietSmiet;
import static de.pscom.pietsmiet.util.ColorUtils.Twitter;
import static de.pscom.pietsmiet.util.ColorUtils.Youtube;
import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class Post implements Comparable<Post> {
    @Nullable
    private String description;
    private String title;
    private int postType;
    @Nullable
    private Drawable thumbnail;
    private Date datetime;
    private int duration;
    private String url;

    private Post(PostBuilder builder) {
        description = builder.description;
        title = builder.title;
        postType = builder.postType;
        thumbnail = builder.thumbnail;
        datetime = builder.date;
        duration = builder.duration;
        url = builder.url;
    }

    @Nullable
    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public boolean hasThumbnail() {
        return thumbnail != null;
    }

    public int getPostType() {
        return postType;
    }

    public Date getDate() {
        return datetime;
    }

    @Nullable
    public String getDescription() {
        return description;
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

    public void setUrl(String url) {
        if (url != null && !url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            this.url = url;
        }
    }


    //UPPER_CASE: PostType constants
    //CamelCase: ColorUtils constants
    public int getBackgroundColor() {
        String hexColor;
        switch (postType) {
            case VIDEO:
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
                || postType == PIETCAST;
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

    public static class PostBuilder {
        private String title;
        private int postType;
        @Nullable
        private String description;
        @Nullable
        private Drawable thumbnail;
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

        public PostBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostBuilder thumbnail(@Nullable Drawable thumbnail) {
            this.thumbnail = thumbnail;
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
                PsLog.e("Title is not given");
                return null;
            }
            if (date == null) {
                PsLog.e("Date is not given");
                return null;
            }
            /*if ((description == null || description.isEmpty()) && thumbnail == null) {
                PsLog.e("No thumbnail and no description given");
                return null;
            }*/ //fixme use another way for retrieving the thumbnails from cache in dbhelper, then uncomment
            return new Post(this);
        }
    }
}
