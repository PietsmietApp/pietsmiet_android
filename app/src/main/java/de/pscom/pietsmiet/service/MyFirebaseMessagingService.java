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


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_NOTIFICATION_ID = "de.pscom.pietsmiet.EXTRA_NOTIFICATION_ID";
    @PostType.AllTypes
    private int postType;
    private int notificationId;
    public static final String KEY_UNSUBSCRIBE = "de.pscom.pietsmiet.KEY_UNSUBSCRIBE";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        PsLog.d("From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            PsLog.d("Message data payload: " + data);
            int type;
            switch (data.get("topic")) {
                case "news":
                    type = NEWS;
                    notificationId = NEWS;
                    PsLog.v("hi");
                    break;
                case "uploadplan":
                    type = UPLOADPLAN;
                    notificationId = UPLOADPLAN;
                    break;
                case "pietcast":
                    type = PIETCAST;
                    notificationId = PIETCAST;
                    break;
                case "video":
                    type = VIDEO;
                    notificationId = VIDEO;
                    break;
                default:
                    PsLog.w("Falsche Kategorie " + data.get("topic"));
                    return;
            }
            // On notification click intent
            Intent clickIntent = new Intent(this, MainActivity.class);
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            clickIntent.putExtra(EXTRA_TYPE, type);
            PendingIntent clickPIntent = PendingIntent.getActivity(this, notificationId, clickIntent,
                    FLAG_ONE_SHOT|FLAG_UPDATE_CURRENT);

            // On action button click intent
            Intent urlIntent = new Intent(Intent.ACTION_VIEW);
            urlIntent.setData(Uri.parse(data.get("link")));
            PendingIntent urlPIntent = PendingIntent.getActivity(this, notificationId, urlIntent,
                    FLAG_ONE_SHOT|FLAG_UPDATE_CURRENT);

            sendNotification(data.get("title"), data.get("message"), clickPIntent, urlPIntent, type);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
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
                FLAG_ONE_SHOT|FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .addAction(R.drawable.ic_remove_black_24dp, "Abbestellen", unsubscribePIntent)
                .setAutoCancel(true)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_MAX);
        if (type == VIDEO) {
            notificationBuilder.setContentIntent(urlIntent);
        } else {
            notificationBuilder.setContentIntent(clickIntent);
            notificationBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp, "Link öffnen", urlIntent);
        }
        if (messageBody != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageBody)));
            notificationBuilder.setContentText(Html.fromHtml(messageBody));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
