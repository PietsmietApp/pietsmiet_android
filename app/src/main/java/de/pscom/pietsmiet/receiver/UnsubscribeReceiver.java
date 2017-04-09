package de.pscom.pietsmiet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.EXTRA_TYPE;
import static de.pscom.pietsmiet.service.MyFirebaseMessagingService.KEY_UNSUBSCRIBE;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class UnsubscribeReceiver extends BroadcastReceiver{
    @SuppressWarnings("StringEquality")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == KEY_UNSUBSCRIBE){
            String topic = intent.getExtras().getString(EXTRA_TYPE);
            if (topic == null){
                PsLog.w("topic is null");
                return;
            }
            switch(topic) {
                case "pietcast":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_PIETCAST_SETTING, false);
                    break;
                case "uploadplan":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_UPLOADPLAN_SETTING, false);
                    break;
                case "news":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_NEWS_SETTING, false);
                    break;
                case "video":
                    SharedPreferenceHelper.setSharedPreferenceBoolean(context, KEY_NOTIFY_VIDEO_SETTING, false);
                    break;
                default:
                    PsLog.w("Wrong topic");
                    break;
            }
        } else {
            PsLog.i("Wrong intent received");
        }
    }
}
