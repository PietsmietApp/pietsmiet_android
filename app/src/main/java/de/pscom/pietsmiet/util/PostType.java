package de.pscom.pietsmiet.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("WeakerAccess")
public class PostType {
    //Scales dynamically
    //Renamed to shorter version
    public static final int VIDEO = 0;
    public static final int STREAM = VIDEO + 1;
    public static final int PIETCAST = STREAM + 1;
    public static final int TWITTER = PIETCAST + 1;
    public static final int FACEBOOK = TWITTER + 1;
    public static final int UPLOAD_PLAN = FACEBOOK + 1;

    /**
     * Converts from int to name
     *
     * @param i Post type as int
     * @return String Name of Posttype
     */
    public static String getName(@AllTypes int i) {
        switch (i) {
            case VIDEO:
                return "VIDEO";
            case STREAM:
                return "STREAM";
            case PIETCAST:
                return "PIETCAST";
            case TWITTER:
                return "TWITTER";
            case FACEBOOK:
                return "FACEBOOK";
            case UPLOAD_PLAN:
                return "UPLOAD_PLAN";
            default:
                return "NO INT LISTED";
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FACEBOOK, TWITTER, UPLOAD_PLAN})
    public @interface TypeNoThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIDEO, FACEBOOK, TWITTER, PIETCAST, STREAM})
    public @interface TypeThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PIETCAST, UPLOAD_PLAN, FACEBOOK, TWITTER, STREAM, VIDEO})
    public @interface AllTypes {
    }
}