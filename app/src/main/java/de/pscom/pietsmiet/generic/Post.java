package de.pscom.pietsmiet.generic;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.repository.FacebookRepository;
import de.pscom.pietsmiet.repository.FirebaseRepository;
import de.pscom.pietsmiet.repository.MainRepository;
import java.util.Date;

import de.pscom.pietsmiet.repository.TwitterRepository;
import de.pscom.pietsmiet.repository.YoutubeRepository;
import de.pscom.pietsmiet.util.PsLog;

public class Post extends ViewItem implements Comparable<ViewItem> {
    @Nullable
    private String description;
    private String title;
    private PostType postType;
    @Nullable
    private Drawable thumbnail;
    @Nullable
    private String thumbnailUrl;
    @Nullable
    private String thumbnailHDUrl;
    @Nullable
    private String username;
    
    private long api_ID;
    private int duration;
    private String url;
    private boolean isThumbnailHD = false;

    private Post(PostBuilder builder) {
        super(ViewItem.TYPE_POST, builder.date);
        description = builder.description;
        title = builder.title;
        postType = builder.postType;
        thumbnailUrl = builder.thumbnailUrl;
        thumbnailHDUrl = builder.thumbnailHDUrl;
        username = builder.username;
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

    public PostType getPostType() {
        return postType;
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

    @Override
    public int hashCode() {
        int result = 5;
        int random = 87;
        result = random * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = random * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = random * result + Long.valueOf(getDate().getTime()).intValue();
        result = random * result + getPostType().ordinal();
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
    public static class PostBuilder implements Comparable<PostBuilder> {
        private boolean empty = false;
        private String title;
        private PostType postType;
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


        public PostBuilder( PostType postType ) {
            this.postType = postType;
        }

        public PostBuilder empty(){
            empty = true;
            return this;
        }

        public Date getDate() {
            return date;
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
            if (empty){
                return null;
            }
            if (title == null || title.isEmpty()) {
                PsLog.e("Title is not given");
                return null;
            }
            if (date == null) {
                PsLog.e("Date is not given");
                return null;
            }
            if (postType == PostType.UPLOADPLAN && (description == null || description.isEmpty())) {
                PsLog.e("Uploadplan with no description");
                return null;
            }

            if(postType == null){
                PsLog.e("Not a valid type!");
                return null;
            }
            return new Post(this);
        }

        @Override
        public int compareTo(@NonNull PostBuilder post) {
            if( post.getDate().getTime() > this.date.getTime() ) {
                return 1;
            } else if ( post.getDate().getTime() < this.getDate().getTime() ) {
                return -1;
            }
            return 0;
        }
    }

    public enum PostType {
        YOUTUBE(YoutubeRepository.class, "Youtube", R.id.nav_video_yt, R.color.youtube),
        PS_VIDEO(FirebaseRepository.class, "PS.de Videos", R.id.nav_video_ps, R.color.pietsmiet),
        PIETCAST(FirebaseRepository.class, "Pietcast", R.id.nav_pietcast, R.color.pietsmiet),
        TWITTER(TwitterRepository.class, "Twitter", R.id.nav_twitter, R.color.twitter),
        FACEBOOK(FacebookRepository.class, "Facebook", R.id.nav_facebook, R.color.facebook),
        UPLOADPLAN(FirebaseRepository.class, "Uploadplan", R.id.nav_upload_plan, R.color.pietsmiet),
        NEWS(FirebaseRepository.class, "PS.de News", R.id.nav_ps_news, R.color.pietsmiet);
        public final String name;
        public final Class<? extends MainRepository> aClass;
        public final int drawerId;
        public final int colorId;

        public static PostType getByDrawerId(int did) {
            for (PostType p: values()) {
                if(p.drawerId == did) return p;
            }
            return null;
        }

        /***
         * Creates a PostType.
         * @param aClass The Class Object
         * @param name The name
         * @param drawerId Resource drawer id
         * @param colorId Resource color id
         */
        PostType(Class<? extends MainRepository> aClass, String name, int drawerId, int colorId) {
            this.aClass = aClass;
            this.name = name;
            this.drawerId = drawerId;
            this.colorId = colorId;
        }
    }
}
