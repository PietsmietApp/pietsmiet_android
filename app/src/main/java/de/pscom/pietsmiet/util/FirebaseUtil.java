package de.pscom.pietsmiet.util;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.R;

@SuppressWarnings("WeakerAccess")
public abstract class FirebaseUtil {

    public static final String TOPIC_VIDEO = "video";
    public static final String TOPIC_UPLOADPLAN = "uploadplan";
    public static final String TOPIC_NEWS = "news";
    public static final String TOPIC_PIETCAST = "pietcast";
    public static final String TOPIC_TEST = "test";

    public static final String EVENT_NEXT_COMPLETED = "next_loading_completed";
    public static final String EVENT_NEW_COMPLETED = "new_loading_completed";
    public static final String EVENT_FRESH_COMPLETED = "fresh_loading_completed";

    public static final String PARAM_START_POSITION = "start_position";
    public static final String PARAM_ITEM_COUNT = "item_count";


    public static void loadRemoteConfig() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFirebaseRemoteConfig.activateFetched();
            }
        });
    }

    public static void disableCollectionOnDebug(Context context) {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
        FirebasePerformance.getInstance().setPerformanceCollectionEnabled(!BuildConfig.DEBUG);
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }

    public static void setupTopicSubscriptions() {
        setFirebaseTopicSubscription(TOPIC_TEST, BuildConfig.DEBUG);
        setFirebaseTopicSubscription(TOPIC_UPLOADPLAN, SettingsHelper.boolUploadplanNotification);
        setFirebaseTopicSubscription(TOPIC_NEWS, SettingsHelper.boolNewsNotification);
        setFirebaseTopicSubscription(TOPIC_VIDEO, SettingsHelper.boolVideoNotification);
        setFirebaseTopicSubscription(TOPIC_PIETCAST, SettingsHelper.boolPietcastNotification);
    }

    public static void setFirebaseTopicSubscription(String topic, boolean subscribe) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        }
    }

    public static void reportError(String message, Throwable tr) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(tr);
    }
}
