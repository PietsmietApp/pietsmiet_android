package de.pscom.pietsmiet.util;

import android.content.Context;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_FORCE_HD_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.setSharedPreferenceBoolean;

public class SettingsHelper {
    public static boolean FLAG_INIT_SETTINGS_LOADED = false;

    private static boolean boolUploadplanNotification_Init;
    private static boolean boolVideoNotification_Init;
    private static boolean boolNewsNotification_Init;
    private static boolean boolPietcastNotification_Init;
    private static boolean boolForceHDImages_Init;
    private static boolean boolWifiOnlyHDImages_Init;

    public static boolean boolUploadplanNotification;
    public static boolean boolVideoNotification;
    public static boolean boolNewsNotification;
    public static boolean boolPietcastNotification;
    public static boolean boolForceHDImages;
    public static boolean boolWifiOnlyHDImages;

    public static void loadAllSettings(Context context) {
        boolUploadplanNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        boolVideoNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, true);
        boolNewsNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
        boolPietcastNotification = getSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
        boolForceHDImages = getSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_FORCE_HD_SETTING, false);
        boolWifiOnlyHDImages = getSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING, false);

        rebaseInitVars();
        FLAG_INIT_SETTINGS_LOADED = true;
    }


    public static void saveAllSettings(Context context) {
        //todo clean up code
        if(boolUploadplanNotification_Init != boolUploadplanNotification) {
            setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, boolUploadplanNotification);
            boolUploadplanNotification_Init = boolUploadplanNotification;
        }
        if(boolVideoNotification_Init != boolVideoNotification){
            setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, boolVideoNotification);
            boolVideoNotification_Init = boolVideoNotification;
        }
        if(boolNewsNotification_Init != boolNewsNotification) {
            setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, boolNewsNotification);
            boolNewsNotification_Init = boolNewsNotification;
        }
        if(boolPietcastNotification_Init != boolPietcastNotification) {
            setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, boolPietcastNotification);
            boolPietcastNotification_Init = boolPietcastNotification;
        }
        if(boolForceHDImages_Init != boolForceHDImages) {
            setSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_FORCE_HD_SETTING, boolForceHDImages);
            boolForceHDImages_Init = boolForceHDImages;
        }
        if(boolWifiOnlyHDImages_Init != boolWifiOnlyHDImages) {
            setSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING, boolWifiOnlyHDImages);
            boolWifiOnlyHDImages_Init = boolWifiOnlyHDImages;
        }
    }

    public static void forceSaveAllSettings(Context context) {
        setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, boolUploadplanNotification);
        setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, boolVideoNotification);
        setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, boolNewsNotification);
        setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, boolPietcastNotification);
        setSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_FORCE_HD_SETTING, boolForceHDImages);
        setSharedPreferenceBoolean(context, KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING, boolWifiOnlyHDImages);

        rebaseInitVars();
    }

    private static void rebaseInitVars() {
        boolUploadplanNotification_Init = boolUploadplanNotification;
        boolVideoNotification_Init = boolVideoNotification;
        boolNewsNotification_Init = boolNewsNotification;
        boolPietcastNotification_Init = boolPietcastNotification;
        boolForceHDImages_Init = boolForceHDImages;
        boolWifiOnlyHDImages_Init = boolWifiOnlyHDImages;
    }

    public static boolean shouldLoadHDImages(Context c) {
        if(boolForceHDImages) return true;
        if(boolWifiOnlyHDImages && NetworkUtil.isConnectedWifi(c)) return true;
        return false;
    }

}
