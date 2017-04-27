package de.pscom.pietsmiet.service;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

import de.pscom.pietsmiet.view.MainActivity;
import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.PsLog;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String KEY_UNSUBSCRIBE = "de.pscom.pietsmiet.KEY_UNSUBSCRIBE";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PsLog.d("From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() == 0) {
            PsLog.w("No data provided");
            return;
        }
        PsLog.d("Message data payload: " + data);
        int type;
        switch (data.get("topic")) {
            case "news":
                type = NEWS;
                PsLog.v("hi");
                break;
            case "uploadplan":
                type = UPLOADPLAN;
                break;
            case "pietcast":
                type = PIETCAST;
                break;
            case "video":
                type = PS_VIDEO;
                break;
            default:
                PsLog.w("Falsche Kategorie " + data.get("topic"));
                return;
        }
        // On notification click intent
        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        clickIntent.putExtra(EXTRA_TYPE, type);
        PendingIntent clickPIntent = PendingIntent.getActivity(this, type, clickIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        // On action button click intent
        Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(data.get("link")));
        PendingIntent urlPIntent = PendingIntent.getActivity(this, type, urlIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        sendNotification(data.get("title"), data.get("message"), clickPIntent, urlPIntent, type);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, PendingIntent clickIntent, PendingIntent urlIntent, int type) {
        Intent unsubscribeIntent = new Intent();
        unsubscribeIntent.setAction(KEY_UNSUBSCRIBE);
        unsubscribeIntent.putExtra(EXTRA_TYPE, type);
        PendingIntent unsubscribePIntent = PendingIntent.getBroadcast(this, 0, unsubscribeIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher) //fixme one day: a different notif icon
                .setContentTitle(title)
                .addAction(R.drawable.ic_remove_black_24dp, getString(R.string.notification_unsubscribe), unsubscribePIntent)
                .setGroup(type + "")
                .setAutoCancel(true);
        if (type == PS_VIDEO) {
            notificationBuilder.setContentIntent(urlIntent);
        } else {
            notificationBuilder.setContentIntent(clickIntent);
            notificationBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp, getString(R.string.notification_open_url), urlIntent);
        }
        
        if (messageBody != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageBody)));
            notificationBuilder.setContentText(Html.fromHtml(messageBody));
        }

        int notificationId;
        if (type == UPLOADPLAN) {
            // Override existing notifications by providing same Id
            notificationId = type;
        } else {
            notificationId = Calendar.getInstance().get(Calendar.SECOND);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
