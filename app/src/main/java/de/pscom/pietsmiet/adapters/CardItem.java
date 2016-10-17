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

import de.pscom.pietsmiet.util.CardType.ItemTypeNoThumbnail;
import de.pscom.pietsmiet.util.CardType.ItemTypeThumbnail;

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

public class CardItem implements Comparable<CardItem>, Parcelable {
    private String description;
    private String title;
    private int cardItemType;
    private Drawable thumbnail;
    private Date datetime;

    public CardItem() {
    }

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

    public void setThumbnail(@Nullable Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getCardItemType() {
        return cardItemType;
    }

    public void setCardItemType(int type) {
        this.cardItemType = type;
    }

    Date getDatetime() {
        return datetime;
    }

    public void setDatetime(@NonNull Date datetime) {
        this.datetime = datetime;
    }

    String getDescription() {
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

    //UPPER_CASE: CardType constants
    //CamelCase: ColorUtils constants
    int getBackgroundColor() {
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


    boolean isVideoView() {
        return cardItemType == VIDEO
                || cardItemType == STREAM
                || cardItemType == PIETCAST;
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
