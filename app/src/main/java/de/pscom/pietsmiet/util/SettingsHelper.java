package de.pscom.pietsmiet.util;

import android.content.Context;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_LOAD_HD_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_SOURCE_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_BEARER;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceInt;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceString;

public class SettingsHelper {
    public static boolean boolUploadplanNotification;
    public static boolean boolVideoNotification;
    public static boolean boolNewsNotification;
    public static boolean boolPietcastNotification;
    public static String stringTwitterBearer;

    public static int intQualityLoadHDImages;
    public static final int TYPE_HD_NEVER = 0;
    public static final int TYPE_HD_WIFI = 1;
    public static final int TYPE_HD_ALWAYS = 2;

    public static int intSourceVideo;
    public static final int TYPE_SOURCE_VIDEO_PIETSMIET = 0;
    public static final int TYPE_SOURCE_VIDEO_YOUTUBE = 1;
    public static final int TYPE_SOURCE_VIDEO_ALL = 2;


    public static void loadAllSettings(Context context) {
        boolUploadplanNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        boolVideoNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, true);
        boolNewsNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
        boolPietcastNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
        stringTwitterBearer = getSharedPreferenceString(context, KEY_TWITTER_BEARER, "AAAAAAAAAAAAAAAAAAAAABETxQAAAAAAQXmiTXMnIIgG0Qx%2FOPsllsLM6gE%3DqyVdj97gZAREeIgccnOcxL2H8fcIOHWPOV1NTmWCdb9m4Dnlde");
        intQualityLoadHDImages = getSharedPreferenceInt(context, KEY_QUALITY_IMAGE_LOAD_HD_SETTING, TYPE_HD_WIFI);
        intSourceVideo = getSharedPreferenceInt(context, KEY_SOURCE_VIDEO_SETTING, TYPE_SOURCE_VIDEO_PIETSMIET);
    }

    public static boolean shouldLoadHDImages(Context c) {
        if(intQualityLoadHDImages == TYPE_HD_ALWAYS) return true;
        if(intQualityLoadHDImages == TYPE_HD_WIFI && NetworkUtil.isConnectedWifi(c)) return true;
        return false;
    }

}
