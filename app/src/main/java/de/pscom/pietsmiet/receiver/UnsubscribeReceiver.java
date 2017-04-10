package de.pscom.pietsmiet.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_TYPE;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class UnsubscribeReceiver extends BroadcastReceiver {
    @SuppressWarnings("StringEquality")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == KEY_UNSUBSCRIBE) {
            String topic = intent.getExtras().getString(EXTRA_TYPE, "");
            NotificationManager notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //Unsubscribe from category & cancel the notification
            switch (topic) {
                case "pietcast":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
                    notifManager.cancel(PIETCAST);
                    break;
                case "uploadplan":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, false);
                    notifManager.cancel(UPLOADPLAN);
                    break;
                case "news":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
                    notifManager.cancel(NEWS);
                    break;
                case "video":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
                    notifManager.cancel(VIDEO);
                    break;
                default:
                    PsLog.w("Wrong or empty topic");
                    break;
            }
        } else {
            PsLog.i("Wrong intent received");
        }
    }
}
