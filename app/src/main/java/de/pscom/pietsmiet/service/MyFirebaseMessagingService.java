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

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.FilterUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.view.MainActivity;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_NEWS;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_PIETCAST;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_VIDEO;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_GAME = "EXTRA_GAME";
    public static final String EXTRA_NOTIF_ID = "EXTRA_NOTIF_ID";
    public static final String KEY_UNSUBSCRIBE_CATEGORY = "de.pscom.pietsmiet.KEY_UNSUBSCRIBE_CATEGORY";
    public static final String KEY_UNSUBSCRIBE_GAME = "de.pscom.pietsmiet.KEY_UNSUBSCRIBE_GAME";

    private final String DATA_TOPIC = "topic";
    private final String DATA_LINK = "link";
    private final String DATA_TITLE = "title";
    private final String DATA_MESSAGE = "message";
    private final String DATA_GAME = "game";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PsLog.d("Message received from: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() == 0) {
            PsLog.w("No data provided");
            return;
        }
        PsLog.d("Message data payload: " + data);
        int type;
        switch (data.get(DATA_TOPIC)) {
            case TOPIC_NEWS:
                type = NEWS;
                break;
            case TOPIC_UPLOADPLAN:
                type = UPLOADPLAN;
                break;
            case TOPIC_PIETCAST:
                type = PIETCAST;
                break;
            case TOPIC_VIDEO:
                type = PS_VIDEO;
                if (!FilterUtil.isGameAllowed(data.get(DATA_GAME), this)) {
                    PsLog.v("Game is not allowed, not showing");
                    return;
                }
                break;
            default:
                PsLog.w("Wrong type " + data.get(DATA_TOPIC));
                return;
        }
        sendNotification(data.get(DATA_TITLE), data.get(DATA_MESSAGE), type, data.get(DATA_LINK), data.get(DATA_GAME));
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, int type, String link, String game) {
        int notificationId;
        if (type == UPLOADPLAN) {
            // Override existing notifications by providing same id
            notificationId = type;
        } else {
            // Use a non-existing id
            notificationId = Calendar.getInstance().get(Calendar.SECOND);
        }

        Intent openInAppIntent = new Intent(this, MainActivity.class);
        openInAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openInAppIntent.putExtra(EXTRA_TYPE, type);
        PendingIntent openInAppPIntent = PendingIntent.getActivity(this, type,
                openInAppIntent, FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        Intent openUrlExternallyIntent = new Intent(Intent.ACTION_VIEW);
        openUrlExternallyIntent.setData(Uri.parse(link));
        PendingIntent openUrlExternallyPIntent = PendingIntent.getActivity(this, type,
                openUrlExternallyIntent, FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        Intent unsubscribeCategoryIntent = new Intent();
        unsubscribeCategoryIntent.setAction(KEY_UNSUBSCRIBE_CATEGORY);
        unsubscribeCategoryIntent.putExtra(EXTRA_TYPE, type);
        unsubscribeCategoryIntent.putExtra(EXTRA_NOTIF_ID, notificationId);
        PendingIntent unsubscribeCategoryPIntent = PendingIntent.getBroadcast(this, (int) (Math.random() * 5000),
                unsubscribeCategoryIntent, 0);

        Intent unsubscribeGameIntent = new Intent();
        unsubscribeGameIntent.setAction(KEY_UNSUBSCRIBE_GAME);
        unsubscribeGameIntent.putExtra(EXTRA_GAME, game);
        unsubscribeGameIntent.putExtra(EXTRA_NOTIF_ID, notificationId);
        PendingIntent unsubscribeGamePIntent = PendingIntent.getBroadcast(this, (int) (Math.random() * 5000),
                unsubscribeGameIntent, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_ps_app_controller_notext_white)
                .setContentTitle(title)
                .addAction(R.drawable.ic_remove_black_24dp,
                        getString(R.string.notification_unsubscribe_category, PostType.getName(type, this)),
                        unsubscribeCategoryPIntent)
                .setAutoCancel(true);

        if (type == PS_VIDEO) {
            notificationBuilder.setContentIntent(openUrlExternallyPIntent);
            if (game != null) {
                notificationBuilder.addAction(R.drawable.ic_remove_black_24dp,
                        getString(R.string.notification_unsubscribe_game),
                        unsubscribeGamePIntent);
            }
        } else {
            notificationBuilder.setContentIntent(openInAppPIntent);
            notificationBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp,
                    getString(R.string.notification_open_url), openUrlExternallyPIntent);
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
