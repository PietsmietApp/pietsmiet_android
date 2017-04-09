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
import android.media.RingtoneManager;
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

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    @PostType.AllTypes
    private int postType;
    private int notificationId;

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

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            switch (data.get("topic")) {
                case "news":
                    type = UPLOADPLAN;
                    notificationId = 10;
                    break;
                case "uploadplan":
                    type = UPLOADPLAN;
                    notificationId = 11;
                    break;
                case "pietcast":
                    type = PIETCAST;
                    notificationId = 13;
                    break;
                case "video":
                    type = VIDEO;
                    notificationId = 14;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get("link")));
                    break;
                default:
                    PsLog.w("Falsche Kategorie "  + data.get("topic"));
                    return;
            }
            intent.putExtra(EXTRA_TYPE, type);


            sendNotification(data.get("title"), data.get("message"), intent);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (messageBody != null){
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageBody)));
            notificationBuilder.setContentText(Html.fromHtml(messageBody));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
