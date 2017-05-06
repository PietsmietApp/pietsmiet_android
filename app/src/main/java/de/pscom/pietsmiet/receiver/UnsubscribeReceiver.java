package de.pscom.pietsmiet.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.FirebaseUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_TYPE;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class UnsubscribeReceiver extends BroadcastReceiver {
    @SuppressWarnings("StringEquality")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == KEY_UNSUBSCRIBE) {
            int type = intent.getIntExtra(EXTRA_TYPE, -1);
            NotificationManager notifManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //Unsubscribe from category & cancel the notification
            switch (type) {
                case PIETCAST:
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
                    FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_PIETCAST, false);
                    notifManager.cancel(PIETCAST);
                    break;
                case UPLOADPLAN:
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, false);
                    FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_UPLOADPLAN, false);
                    notifManager.cancel(UPLOADPLAN);
                    break;
                case NEWS:
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
                    FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_NEWS, false);
                    notifManager.cancel(NEWS);
                    break;
                case PS_VIDEO:
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
                    FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_VIDEO, false);
                    notifManager.cancel(PS_VIDEO);
                    break;
                default:
                    PsLog.w("Wrong or empty topic");
                    break;
            }

            Toast.makeText(context, context.getString(R.string.info_disabled_notif, PostType.getName(type)), Toast.LENGTH_LONG).show();
        } else {
            PsLog.i("Wrong intent received");
        }
    }
}
