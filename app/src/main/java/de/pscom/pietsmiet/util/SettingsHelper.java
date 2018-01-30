package de.pscom.pietsmiet.util;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import de.pscom.pietsmiet.generic.Post;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_APP_FIRST_RUN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_FACEBOOK;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETCAST;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_NEWS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_UPLOADPLAN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_VIDEOS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_TWITTER;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_YOUTUBE_VIDEOS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_LOAD_HD_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_BEARER;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceInt;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceString;

public class SettingsHelper {
    public static boolean boolAppFirstRun;

    public static boolean boolUploadplanNotification;
    public static boolean boolVideoNotification;
    public static boolean boolNewsNotification;
    public static boolean boolPietcastNotification;

    //SWITCHES
    public static boolean boolCategoryYoutubeVideos;
    public static boolean boolCategoryPietsmietVideos;
    public static boolean boolCategoryPietsmietNews;
    public static boolean boolCategoryPietsmietUploadplan;
    public static boolean boolCategoryPietcast;
    public static boolean boolCategoryTwitter;
    public static boolean boolCategoryFacebook;

    public static String stringTwitterBearer;
    public static String stringFirebaseDbUrl;
    public static String stringFeedbackUrl;
    public static String stringPietstreamUrl;
    public static String stringTwitchChannelIDPietstream;

    public static int intQualityLoadHDImages;
    public static final int TYPE_HD_NEVER = 0;
    public static final int TYPE_HD_WIFI = 1;
    public static final int TYPE_HD_ALWAYS = 2;

    public static void loadAllSettings(Context context) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        boolAppFirstRun = getSharedPreferenceBoolean(context, KEY_APP_FIRST_RUN, true);

        boolUploadplanNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        boolVideoNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
        boolNewsNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
        boolPietcastNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
        stringTwitterBearer = getSharedPreferenceString(context, KEY_TWITTER_BEARER, null);
        stringFirebaseDbUrl = mFirebaseRemoteConfig.getString("FIREBASE_DB_URL");
        stringFeedbackUrl = mFirebaseRemoteConfig.getString("url_feedback");
        stringPietstreamUrl = mFirebaseRemoteConfig.getString("url_pietstream");
        stringTwitchChannelIDPietstream = mFirebaseRemoteConfig.getString("twitch_channel_id_pietstream");
        intQualityLoadHDImages = getSharedPreferenceInt(context, KEY_QUALITY_IMAGE_LOAD_HD_SETTING, TYPE_HD_ALWAYS);

        // SWITCHES
        boolCategoryYoutubeVideos = getSharedPreferenceBoolean(context, KEY_CATEGORY_YOUTUBE_VIDEOS, false);
        boolCategoryPietsmietVideos = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_VIDEOS, true);
        boolCategoryPietsmietNews = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_NEWS, true);
        boolCategoryPietsmietUploadplan = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_UPLOADPLAN, true);
        boolCategoryPietcast = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETCAST, true);
        boolCategoryTwitter = getSharedPreferenceBoolean(context, KEY_CATEGORY_TWITTER, true);
        boolCategoryFacebook = getSharedPreferenceBoolean(context, KEY_CATEGORY_FACEBOOK, true);
    }

    public static boolean shouldLoadHDImages(Context c) {
        return intQualityLoadHDImages == TYPE_HD_ALWAYS ||
                (intQualityLoadHDImages == TYPE_HD_WIFI && new NetworkUtil(c).isConnectedWifi());
    }

    public static String getSharedPreferenceKeyForType(Post.PostType type) {
        switch (type) {
            case PS_VIDEO:
                return KEY_CATEGORY_PIETSMIET_VIDEOS;
            case YOUTUBE:
                return KEY_CATEGORY_YOUTUBE_VIDEOS;
            case PIETCAST:
                return KEY_CATEGORY_PIETCAST;
            case TWITTER:
                return KEY_CATEGORY_TWITTER;
            case FACEBOOK:
                return KEY_CATEGORY_FACEBOOK;
            case UPLOADPLAN:
                return KEY_CATEGORY_PIETSMIET_UPLOADPLAN;
            case NEWS:
                return KEY_CATEGORY_PIETSMIET_NEWS;
            default:
                return "";
        }
    }

    public static boolean getSettingsValueForType(Post.PostType type) {
        switch (type) {
            case PS_VIDEO:
                return boolCategoryPietsmietVideos;
            case YOUTUBE:
                return boolCategoryYoutubeVideos;
            case PIETCAST:
                return boolCategoryPietcast;
            case TWITTER:
                return boolCategoryTwitter;
            case FACEBOOK:
                return boolCategoryFacebook;
            case UPLOADPLAN:
                return boolCategoryPietsmietUploadplan;
            case NEWS:
                return boolCategoryPietsmietNews;
            default:
                return true;
        }
    }


    public static boolean isOnlyType(Post.PostType type) {
        for (Post.PostType i : Post.PostType.values()) {
            if (getSettingsValueForType(i) && i != type) return false;
        }
        return true;
    }

}
