package de.pscom.pietsmiet.generic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.pscom.pietsmiet.util.PostType;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.STREAM;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;
import static de.pscom.pietsmiet.util.ColorUtils.Default;
import static de.pscom.pietsmiet.util.ColorUtils.Facebook;
import static de.pscom.pietsmiet.util.ColorUtils.PietSmiet;
import static de.pscom.pietsmiet.util.ColorUtils.Twitter;
import static de.pscom.pietsmiet.util.ColorUtils.Youtube;

public class Post implements Comparable<Post>, Parcelable {
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
     * @param title        Title of the post
     * @param description  Description or message of the post
     * @param datetime     Time of the post
     * @param postType Type of the post
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
     * @param title        Title of the post
     * @param description  Description or message of the post
     * @param datetime     Time of the post
     * @param postType Type of the post
     * @param thumbnail    Thumbnail image
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
     * @param title        Title of the post
     * @param description  Description or message of the post
     * @param datetime     Time of the post
     * @param postType Type of the post
     * @param duration     Duration of the video / pietcast
     * @param thumbnail    Thumbnail image
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

    public boolean isVideoView() {
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bitmap bitmap = ((BitmapDrawable) thumbnail).getBitmap();

        dest.writeString(description);
        dest.writeString(title);
        dest.writeInt(postType);
        dest.writeParcelable(bitmap, flags);
        dest.writeLong(datetime.getTime());
    }

    // "De-parcel object
    private Post(Parcel in) {
        description = in.readString();
        title = in.readString();
        postType = in.readInt();
        Bitmap bitmap = in.readParcelable(getClass().getClassLoader());
        //noinspection deprecation
        thumbnail = new BitmapDrawable(bitmap);
        datetime = new Date(in.readLong());
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
