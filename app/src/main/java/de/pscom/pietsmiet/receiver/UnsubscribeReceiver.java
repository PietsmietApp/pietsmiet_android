package de.pscom.pietsmiet.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.Serializable;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.FirebaseUtil;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_NOTIF_ID;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_TYPE;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class UnsubscribeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PsLog.v("Received unsubscribe intent from " + intent.getAction() +
                " with notif id " + intent.getIntExtra(EXTRA_NOTIF_ID, -1));

        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Delete notification
        if (notifManager != null) {
            notifManager.cancel(intent.getIntExtra(EXTRA_NOTIF_ID, -1));
        }

        if (intent.getAction() == KEY_UNSUBSCRIBE) {
            Serializable ser = intent.getSerializableExtra(EXTRA_TYPE);
            Post.PostType type = (ser instanceof Post.PostType ? (Post.PostType) ser : null );

            //Unsubscribe from category & cancel the notification
            if (type != null) {
                switch (type) {
                    case PIETCAST:
                        SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_PIETCAST, false);
                        break;
                    case UPLOADPLAN:
                        SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, false);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_UPLOADPLAN, false);
                        break;
                    case NEWS:
                        SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_NEWS, false);
                        break;
                    case PS_VIDEO:
                        SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_VIDEO, false);
                        break;
                    default:
                        PsLog.w("Wrong or empty topic");
                        break;
                }
                Toast.makeText(context, context.getString(R.string.info_disabled_notif, type.name), Toast.LENGTH_LONG).show();
            } else {
                PsLog.e("Could not unsubsribe, type not valid!");
                Toast.makeText(context, "Could not unsubsribe, type not valid!", Toast.LENGTH_LONG).show();
            }


        } else {
            PsLog.i("Wrong intent received");
        }
    }
}
