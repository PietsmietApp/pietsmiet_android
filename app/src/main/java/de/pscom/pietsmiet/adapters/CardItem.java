package de.pscom.pietsmiet.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.pscom.pietsmiet.util.CardTypes;
import de.pscom.pietsmiet.util.CardTypes.ItemTypeNoThumbnail;
import de.pscom.pietsmiet.util.CardTypes.ItemTypeThumbnail;
import de.pscom.pietsmiet.util.ColorUtils;

import static de.pscom.pietsmiet.util.CardTypes.TYPE_FACEBOOK;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_IS_VIDEO;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_PIETCAST;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_STREAM;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_TWITTER;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.CardTypes.TYPE_VIDEO;

public class CardItem implements Comparable<CardItem>, Parcelable {
    private String description;
    private String title;
    private int cardItemType;
    private Drawable thumbnail;
    private Date datetime;


    /**
     * Creates a new card item
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     */

    public CardItem(String title, String description, Date datetime, @ItemTypeNoThumbnail int cardItemType) {
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
    public CardItem(String title, String description, Date datetime, Drawable thumbnail, @ItemTypeThumbnail int cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.cardItemType = cardItemType;
    }


    @Nullable
    Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    int getCardItemType() {
        return cardItemType;
    }

    public void setCardItemType(int type) {
        this.cardItemType = type;
    }

    Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    int getBackgroundColor() {
        String hexColor;
        switch (cardItemType) {
            case TYPE_VIDEO:
            case TYPE_STREAM:
                hexColor = ColorUtils.Youtube;
                break;
            case TYPE_UPLOAD_PLAN:
            case TYPE_PIETCAST:
                hexColor = ColorUtils.PietSmiet;
                break;
            case TYPE_FACEBOOK:
                hexColor = ColorUtils.Facebook;
                break;
            case TYPE_TWITTER:
                hexColor = ColorUtils.Twitter;
                break;
            default:
                hexColor = ColorUtils.Default;
                break;
        }
        return Color.parseColor(hexColor);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    boolean isVideoView() {
        return (cardItemType & TYPE_IS_VIDEO) == TYPE_IS_VIDEO ;
    }

    @Override
    public int compareTo(@NonNull CardItem item) {
        if (item.getDatetime() == null) return -1;
        else if (this.getDatetime() == null) return 1;
        return item.getDatetime().compareTo(this.getDatetime());
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
    private CardItem(Parcel in) {
        description = in.readString();
        title = in.readString();
        cardItemType = in.readInt();
        Bitmap bitmap = in.readParcelable(getClass().getClassLoader());
        //noinspection deprecation
        thumbnail = new BitmapDrawable(bitmap);
        datetime = new Date(in.readLong());
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator<CardItem>() {
        public CardItem createFromParcel(Parcel in) {
            return new CardItem(in);
        }

        public CardItem[] newArray(int size) {
            return new CardItem[size];
        }
    };


}
