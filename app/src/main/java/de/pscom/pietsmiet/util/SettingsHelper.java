package de.pscom.pietsmiet.util;

import android.content.Context;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;

public class SettingsHelper {
    public static boolean boolUploadplanNotification;
    public static boolean boolVideoNotification;
    public static boolean boolNewsNotification;
    public static boolean boolPietcastNotification;

    public static void loadAllSettings(Context context) {
        boolUploadplanNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        boolVideoNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, true);
        boolNewsNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
        boolPietcastNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
    }

}
