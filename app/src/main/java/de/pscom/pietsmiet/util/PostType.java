package de.pscom.pietsmiet.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.R;

@SuppressWarnings("WeakerAccess")
public class PostType {
    //Scales dynamically
    //Renamed to shorter version
    public static final int VIDEO = 0;
    public static final int PIETCAST = VIDEO + 1;
    public static final int TWITTER = PIETCAST + 1;
    public static final int FACEBOOK = TWITTER + 1;
    public static final int UPLOADPLAN = FACEBOOK + 1;

    public static List<Integer> getPossibleTypes() {
        List<Integer> navigationItems = new ArrayList<>();
        //navigationItems.add(UPLOADPLAN);
        navigationItems.add(VIDEO);
        navigationItems.add(FACEBOOK);
        //navigationItems.add(TWITTER);
        //navigationItems.add(PIETCAST);

        return navigationItems;
    }

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
            case PIETCAST:
                return "PIETCAST";
            case TWITTER:
                return "TWITTER";
            case FACEBOOK:
                return "FACEBOOK";
            case UPLOADPLAN:
                return "UPLOADPLAN";
            default:
                return "NO INT LISTED";
        }
    }

    public static int getDrawerIdForType(int postType) {
        switch (postType) {
            case VIDEO:
                return R.id.nav_video;
            case PIETCAST:
                return R.id.nav_pietcast;
            case TWITTER:
                return R.id.nav_twitter;
            case FACEBOOK:
                return R.id.nav_facebook;
            case UPLOADPLAN:
                return R.id.nav_upload_plan;
            default:
                return -1;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FACEBOOK, TWITTER, UPLOADPLAN})
    public @interface TypeNoThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VIDEO, FACEBOOK, TWITTER, PIETCAST})
    public @interface TypeThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PIETCAST, UPLOADPLAN, FACEBOOK, TWITTER, VIDEO})
    public @interface AllTypes {
    }
}