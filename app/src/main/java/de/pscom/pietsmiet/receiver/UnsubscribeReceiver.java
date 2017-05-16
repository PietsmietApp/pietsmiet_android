package de.pscom.pietsmiet.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.FilterUtil;
import de.pscom.pietsmiet.util.FirebaseUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_GAME;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_NOTIF_ID;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_TYPE;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE_CATEGORY;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE_GAME;
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
        PsLog.v("Received unsubscribe intent from " + intent.getAction() +
                " with notif id " + intent.getIntExtra(EXTRA_NOTIF_ID, -1));

        // Delete notification
        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(intent.getIntExtra(EXTRA_NOTIF_ID, -1));

        if (intent.getAction() == KEY_UNSUBSCRIBE_CATEGORY) {
            int type = intent.getIntExtra(EXTRA_TYPE, -1);
            //Unsubscribe from category & cancel the notification
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

            Toast.makeText(context, context.getString(R.string.info_disabled_notif, PostType.getName(type, context)), Toast.LENGTH_LONG).show();

        } else if (intent.getAction() == KEY_UNSUBSCRIBE_GAME) {
            String game = intent.getStringExtra(EXTRA_GAME);
            if (game == null || game == "") {
                PsLog.w("No game or empty game provided");
                return;
            }
            FilterUtil.addStringToBlacklist(game, context);
            Toast.makeText(context, context.getString(R.string.info_disabled_notif, game), Toast.LENGTH_LONG).show();
        } else {
            PsLog.i("Wrong intent received");
        }
    }
}
