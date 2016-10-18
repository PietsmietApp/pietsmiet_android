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

import de.pscom.pietsmiet.util.CardType;

import static de.pscom.pietsmiet.util.CardType.FACEBOOK;
import static de.pscom.pietsmiet.util.CardType.PIETCAST;
import static de.pscom.pietsmiet.util.CardType.STREAM;
import static de.pscom.pietsmiet.util.CardType.TWITTER;
import static de.pscom.pietsmiet.util.CardType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.CardType.VIDEO;
import static de.pscom.pietsmiet.util.ColorUtils.Default;
import static de.pscom.pietsmiet.util.ColorUtils.Facebook;
import static de.pscom.pietsmiet.util.ColorUtils.PietSmiet;
import static de.pscom.pietsmiet.util.ColorUtils.Twitter;
import static de.pscom.pietsmiet.util.ColorUtils.Youtube;

public class Post implements Comparable<Post>, Parcelable {
    private String description;
    private String title;
    private int cardItemType;
    @Nullable
    private Drawable thumbnail;
    private Date datetime;
    private int duration;

    public Post() {
    }

    /**
     * Creates a new card item
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     */

    public Post(String title, String description, Date datetime, @CardType.ItemTypeNoThumbnail int cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.cardItemType = cardItemType;
    }


    /**
     * Creates a new card item with a thumbnail
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     * @param thumbnail    Thumbnail image
     */
    public Post(String title, String description, Date datetime, @Nullable Drawable thumbnail, @CardType.ItemTypeThumbnail int cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.cardItemType = cardItemType;
    }

    /**
     * Creates a new card item with a thumbnail
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     * @param duration     Duration of the video / pietcast
     * @param thumbnail    Thumbnail image
     */
    public Post(String title, String description, Date datetime, @Nullable Drawable thumbnail, int duration, @CardType.ItemTypeThumbnail int cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.cardItemType = cardItemType;
        this.duration = duration;
    }

    @Nullable
    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(@Nullable Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getCardItemType() {
        return cardItemType;
    }

    public void setCardItemType(int type) {
        this.cardItemType = type;
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


    //UPPER_CASE: CardType constants
    //CamelCase: ColorUtils constants
    public int getBackgroundColor() {
        String hexColor;
        switch (cardItemType) {
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
        return cardItemType == VIDEO
                || cardItemType == STREAM
                || cardItemType == PIETCAST;
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
        dest.writeInt(cardItemType);
        dest.writeParcelable(bitmap, flags);
        dest.writeLong(datetime.getTime());
    }

    // "De-parcel object
    private Post(Parcel in) {
        description = in.readString();
        title = in.readString();
        cardItemType = in.readInt();
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
