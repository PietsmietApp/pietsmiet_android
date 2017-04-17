package de.pscom.pietsmiet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Source: https://github.com/nickescobedo/Android-Shared-Preferences-Helper/blob/master/SharedPreferenceHelper.java
 */
@SuppressWarnings("SameParameterValue")
public class SharedPreferenceHelper {
    public static final String KEY_NOTIFY_UPLOADPLAN_SETTING = "KEY_NOTIFY_UPLOADPLAN_SETTING";
    public static final String KEY_NOTIFY_VIDEO_SETTING = "KEY_NOTIFY_VIDEO_SETTING";
    public static final String KEY_NOTIFY_NEWS_SETTING = "KEY_NOTIFY_NEWS_SETTING";
    public static final String KEY_NOTIFY_PIETCAST_SETTING = "KEY_NOTIFY_PIETCAST_SETTING";
    public static final String KEY_QUALITY_IMAGE_FORCE_HD_SETTING = "KEY_QUALITY_IMAGE_FORCE_HD_SETTING";
    public static final String KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING = "KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING";
    private final static String PREF_FILE = "PREF";

    /**
     * Set a string shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set a long shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Set a Boolean shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a string shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static String getSharedPreferenceString(Context context, String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getString(key, defValue);
    }

    /**
     * Get a long shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static long getSharedPreferenceLong(Context context, String key, long defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getLong(key, defValue);
    }

    /**
     * Get a boolean shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getBoolean(key, defValue);
    }
}

