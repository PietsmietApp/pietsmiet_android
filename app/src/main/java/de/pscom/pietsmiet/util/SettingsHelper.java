package de.pscom.pietsmiet.util;

import android.content.Context;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_DISPLAY_NOTIF_QUESTION;
import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_FEEDBACK_URL;
import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_FIREBASE_DB_URL;
import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_PARAM_TRUE;
import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_PIETSTREAM_URL;
import static de.pscom.pietsmiet.util.FirebaseUtil.CONFIG_TWITCH_CHANNEL_ID;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_APP_FIRST_RUN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_FACEBOOK;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETCAST;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_NEWS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_UPLOADPLAN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_PIETSMIET_VIDEOS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_TWITTER;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_CATEGORY_YOUTUBE_VIDEOS;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFICATION_QUESTION_SHOWN;
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
    public static boolean boolNotificationQuestionShown;

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
    public static boolean stringShouldShowNotifQuestion;

    public static int intQualityLoadHDImages;
    private static final int TYPE_HD_NEVER = 0;
    private static final int TYPE_HD_WIFI = 1;
    private static final int TYPE_HD_ALWAYS = 2;

    public static void loadAllSettings(Context context) {
        // General purpose
        boolAppFirstRun = getSharedPreferenceBoolean(context, KEY_APP_FIRST_RUN, true);
        boolNotificationQuestionShown = getSharedPreferenceBoolean(context, KEY_NOTIFICATION_QUESTION_SHOWN, false);

        // Settings
        boolUploadplanNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        boolVideoNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
        boolNewsNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
        boolPietcastNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
        stringTwitterBearer = getSharedPreferenceString(context, KEY_TWITTER_BEARER, null);
        intQualityLoadHDImages = getSharedPreferenceInt(context, KEY_QUALITY_IMAGE_LOAD_HD_SETTING, TYPE_HD_ALWAYS);

        // Drawer categories
        boolCategoryYoutubeVideos = getSharedPreferenceBoolean(context, KEY_CATEGORY_YOUTUBE_VIDEOS, false);
        boolCategoryPietsmietVideos = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_VIDEOS, true);
        boolCategoryPietsmietNews = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_NEWS, true);
        boolCategoryPietsmietUploadplan = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETSMIET_UPLOADPLAN, true);
        boolCategoryPietcast = getSharedPreferenceBoolean(context, KEY_CATEGORY_PIETCAST, true);
        boolCategoryTwitter = getSharedPreferenceBoolean(context, KEY_CATEGORY_TWITTER, true);
        boolCategoryFacebook = getSharedPreferenceBoolean(context, KEY_CATEGORY_FACEBOOK, true);

        // Remote config
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        stringFirebaseDbUrl = mFirebaseRemoteConfig.getString(CONFIG_FIREBASE_DB_URL);
        stringFeedbackUrl = mFirebaseRemoteConfig.getString(CONFIG_FEEDBACK_URL);
        stringPietstreamUrl = mFirebaseRemoteConfig.getString(CONFIG_PIETSTREAM_URL);
        stringTwitchChannelIDPietstream = mFirebaseRemoteConfig.getString(CONFIG_TWITCH_CHANNEL_ID);
        stringShouldShowNotifQuestion = (mFirebaseRemoteConfig.getString(CONFIG_DISPLAY_NOTIF_QUESTION) == CONFIG_PARAM_TRUE);
    }

    public static boolean shouldLoadHDImages(Context c) {
        return intQualityLoadHDImages == TYPE_HD_ALWAYS ||
                (intQualityLoadHDImages == TYPE_HD_WIFI && new NetworkUtil(c).isConnectedWifi());
    }

    public static String getSharedPreferenceKeyForType(@PostType.AllTypes int type) {
        switch (type) {
            case PostType.PS_VIDEO:
                return KEY_CATEGORY_PIETSMIET_VIDEOS;
            case PostType.YOUTUBE:
                return KEY_CATEGORY_YOUTUBE_VIDEOS;
            case PostType.PIETCAST:
                return KEY_CATEGORY_PIETCAST;
            case PostType.TWITTER:
                return KEY_CATEGORY_TWITTER;
            case PostType.FACEBOOK:
                return KEY_CATEGORY_FACEBOOK;
            case PostType.UPLOADPLAN:
                return KEY_CATEGORY_PIETSMIET_UPLOADPLAN;
            case PostType.NEWS:
                return KEY_CATEGORY_PIETSMIET_NEWS;
            default:
                return "";
        }
    }

    public static boolean getSettingsValueForType(@PostType.AllTypes int type) {
        switch (type) {
            case PostType.PS_VIDEO:
                return boolCategoryPietsmietVideos;
            case PostType.YOUTUBE:
                return boolCategoryYoutubeVideos;
            case PostType.PIETCAST:
                return boolCategoryPietcast;
            case PostType.TWITTER:
                return boolCategoryTwitter;
            case PostType.FACEBOOK:
                return boolCategoryFacebook;
            case PostType.UPLOADPLAN:
                return boolCategoryPietsmietUploadplan;
            case PostType.NEWS:
                return boolCategoryPietsmietNews;
            default:
                return true;
        }
    }


    public static boolean isOnlyType(@PostType.AllTypes int type) {
        for (int i : getPossibleTypes()) {
            if (getSettingsValueForType(i) && i != type) return false;
        }
        return true;
    }

}
