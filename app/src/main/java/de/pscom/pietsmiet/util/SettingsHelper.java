package de.pscom.pietsmiet.util;

import android.content.Context;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;

/**
 * Created by Denny on 16.11.2016.
 */

public class SettingsHelper {
    public static boolean uploadPlanNotification;
    public static boolean andere_setting;

    public static void loadAllSettings(Context context){
        uploadPlanNotification = getSharedPreferenceBoolean(context, KEY_NEWS_SETTING,true);
    }

}
