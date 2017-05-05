package de.pscom.pietsmiet.util;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import de.pscom.pietsmiet.BuildConfig;

@SuppressWarnings("WeakerAccess")
public abstract class FirebaseUtil {
    private static final String PARAM_FIREBASE_DB_URL = "FIREBASE_DB_URL";

    private static final String TOPIC_VIDEO = "video";
    private static final String TOPIC_UPLOADPLAN = "uploadplan";
    private static final String TOPIC_NEWS = "news";
    private static final String TOPIC_PIETCAST = "pietcast";
    private static final String TOPIC_TEST = "test2";


    public static void loadRemoteConfig(Activity context) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        mFirebaseRemoteConfig.activateFetched();

                        String firebaseDbUrl = mFirebaseRemoteConfig.getString(PARAM_FIREBASE_DB_URL);
                        if (firebaseDbUrl != null && firebaseDbUrl != "") {
                            SharedPreferenceHelper.setSharedPreferenceString(
                                    context, SharedPreferenceHelper.KEY_FIREBASE_DB_URL, firebaseDbUrl);
                            SettingsHelper.loadAllSettings(context);
                        }
                    }
                });
    }

    public static void setupTopicSubscriptions(Context context) {
        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TEST);
            FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(false);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_TEST);
        }
        if (SettingsHelper.boolUploadplanNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_UPLOADPLAN);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_UPLOADPLAN);
        }
        if (SettingsHelper.boolVideoNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_VIDEO);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_VIDEO);
        }
        if (SettingsHelper.boolNewsNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NEWS);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_NEWS);
        }
        if (SettingsHelper.boolPietcastNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_PIETCAST);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_PIETCAST);
        }
    }

    public static void reportError(String message, Throwable tr) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(tr);
    }
}
