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
    public static final int YOUTUBE = 0;
    public static final int PS_VIDEO = YOUTUBE + 1;
    public static final int PIETCAST = PS_VIDEO + 1;
    public static final int TWITTER = PIETCAST + 1;
    public static final int FACEBOOK = TWITTER + 1;
    public static final int UPLOADPLAN = FACEBOOK + 1;
    public static final int NEWS = UPLOADPLAN + 1;

    public static List<Integer> getPossibleTypes() {
        List<Integer> navigationItems = new ArrayList<>();
        navigationItems.add(UPLOADPLAN);
        navigationItems.add(YOUTUBE);
        navigationItems.add(PS_VIDEO);
        navigationItems.add(FACEBOOK);
        navigationItems.add(TWITTER);
        navigationItems.add(PIETCAST);
        navigationItems.add(NEWS);

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
            case PS_VIDEO:
                return "Pietsmiet.de Videos";
            case YOUTUBE:
                return "YouTube Videos";
            case PIETCAST:
                return "Pietcast";
            case TWITTER:
                return "Twitter";
            case FACEBOOK:
                return "Facebook";
            case UPLOADPLAN:
                return "Uploadplan";
            case NEWS:
                return "News";
            default:
                return "Unbekannte Kategorie";
        }
    }

    public static int getDrawerIdForType(int postType) {
        switch (postType) {
            case PS_VIDEO:
                return R.id.nav_video_ps;
            case YOUTUBE:
                return R.id.nav_video_yt;
            case PIETCAST:
                return R.id.nav_pietcast;
            case TWITTER:
                return R.id.nav_twitter;
            case FACEBOOK:
                return R.id.nav_facebook;
            case UPLOADPLAN:
                return R.id.nav_upload_plan;
            case NEWS:
                return R.id.nav_ps_news;
            default:
                return -1;
        }
    }

    public static int getTypeForDrawerId(int dId) {
        switch (dId) {
            case R.id.nav_video_ps:
                return PS_VIDEO;
            case R.id.nav_video_yt:
                return YOUTUBE;
            case R.id.nav_pietcast:
                return PIETCAST;
            case R.id.nav_twitter:
                return TWITTER;
            case R.id.nav_facebook:
                return FACEBOOK;
            case R.id.nav_upload_plan:
                return UPLOADPLAN;
            case R.id.nav_ps_news:
                return NEWS;
            default:
                return -1;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PIETCAST, UPLOADPLAN, FACEBOOK, TWITTER, PS_VIDEO, NEWS, YOUTUBE})
    public @interface AllTypes {
    }
}