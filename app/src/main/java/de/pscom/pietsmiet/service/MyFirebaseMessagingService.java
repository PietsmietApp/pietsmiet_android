package de.pscom.pietsmiet.service;

/*
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
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.view.MainActivity;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static de.pscom.pietsmiet.generic.Post.PostType.NEWS;
import static de.pscom.pietsmiet.generic.Post.PostType.PIETCAST;
import static de.pscom.pietsmiet.generic.Post.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.generic.Post.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_NEWS;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_PIETCAST;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_VIDEO;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String KEY_UNSUBSCRIBE = "de.pscom.pietsmiet.KEY_UNSUBSCRIBE";
    public static final String EXTRA_NOTIF_ID = "EXTRA_NOTIF_ID";
    private static final String DATA_TOPIC = "topic";
    private static final String DATA_MESSAGE = "message";
    private static final String DATA_TITLE = "title";
    private static final String DATA_LINK = "link";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PsLog.d("Firebase notification received for: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data.size() == 0) {
            PsLog.w("No data provided");
            return;
        }
        PsLog.d("Message data payload: " + data);
        Post.PostType type;
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
                break;
            default:
                PsLog.w("Wrong type! Is " + data.get(DATA_TOPIC));
                return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_NAME, type.ordinal());
        FirebaseAnalytics.getInstance(this).logEvent("notification_received", bundle);

        String title = data.get(DATA_TITLE);
        String message = data.get(DATA_MESSAGE);
        String link = data.get(DATA_LINK);

        int notificationId;
        if (type == UPLOADPLAN) {
            // Override existing notifications by providing same Id
            notificationId = type.ordinal();
        } else {
            // Create a unique notification id for each video,
            // this is a workaround for duplicate notifications
            notificationId = title.hashCode() + message.hashCode() + link.hashCode() + type.ordinal();
        }

        sendNotification(title, message, link, type, notificationId);
        sendStackNotification(type, message);
    }

    /** Sends a summary notification if the api is higher or equal to 23.
     *  @param notif_type type / category of the notification.
     */
    private void sendStackNotification(Post.PostType notif_type, String new_notif_text) {
        if (Build.VERSION.SDK_INT >= 23) {
            int stack_notif_id = 1000 + notif_type.ordinal();

            ArrayList<StatusBarNotification> groupedNotifications = new ArrayList<>();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            boolean sum_notif_spawned = false;
            int notif_counter = 0;

            for (StatusBarNotification sbn : notificationManager.getActiveNotifications()) {
                if( sbn.getNotification().getGroup().equals(notif_type.name) ) {
                    groupedNotifications.add(sbn);
                    if(sbn.getId() == stack_notif_id) sum_notif_spawned = true;
                }
            }

            if (groupedNotifications.size() > 1 && !sum_notif_spawned || groupedNotifications.size() > 0 && sum_notif_spawned ) {
                NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
                if( sum_notif_spawned ) {
                    inbox.addLine(new_notif_text);
                    notif_counter++;
                }
                for (StatusBarNotification activeSbn : groupedNotifications) {
                    if ( !sum_notif_spawned ) {
                        CharSequence stackNotificationLine = activeSbn.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
                        if (stackNotificationLine != null) {
                            inbox.addLine(stackNotificationLine);
                            notif_counter++;
                        }
                    } else if ( activeSbn.getId() == stack_notif_id ) {
                        CharSequence[] stackNotificationLineArray = activeSbn.getNotification().extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
                        if (stackNotificationLineArray != null) {
                            for(CharSequence line : stackNotificationLineArray) {
                                if(new_notif_text != null && !line.toString().equals(new_notif_text)) {
                                    inbox.addLine(line);
                                    notif_counter++;
                                }
                            }
                        }
                    }

                }

                // On notification click intent
                Intent clickIntent = new Intent(this, MainActivity.class);
                clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                clickIntent.putExtra(EXTRA_TYPE, notif_type);
                PendingIntent clickPIntent = PendingIntent.getActivity(this, notif_type.ordinal(), clickIntent,
                        FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder nBuilderSum = new NotificationCompat.Builder(this, "Default")
                        .setSmallIcon(R.drawable.ic_ps_app_controller_notext_white)
                        .setContentIntent(clickPIntent)
                        .setGroup(notif_type.name)
                        .setGroupSummary(true)
                        .setAutoCancel(true);

                switch (notif_type) { // TODO: put in string file + translation?
                    case PS_VIDEO:
                        nBuilderSum.setContentTitle("Neue Videos online")
                                   .setContentText(String.format("%d neue Videos", notif_counter));
                        inbox.setSummaryText(String.format("%d neue Videos", notif_counter));
                        break;
                    case UPLOADPLAN:
                        nBuilderSum.setContentTitle("Neue Uploadpläne online")
                                   .setContentText(String.format("%d neue Uploadpläne", notif_counter));
                        inbox.setSummaryText(String.format("%d neue Uploadpläne", notif_counter));
                        break;
                    case NEWS:
                        nBuilderSum.setContentTitle("Neue News online")
                                   .setContentText(String.format("%d neue News", notif_counter));
                        inbox.setSummaryText(String.format("%d neue News", notif_counter));
                        break;
                    case PIETCAST:
                        nBuilderSum.setContentTitle("Neue Pietcasts online")
                                   .setContentText(String.format("%d neue Pietcasts", notif_counter));
                        inbox.setSummaryText(String.format("%d neue Pietcasts", notif_counter));
                        break;
                    default:
                        nBuilderSum.setContentTitle("Neue Benachrichtigungen")
                                   .setContentText(String.format("%d neue", notif_counter));
                        inbox.setSummaryText(String.format("%d neue Benachrichtigungen", notif_counter));
                }
                nBuilderSum.setStyle(inbox);
                notificationManager.notify(stack_notif_id, nBuilderSum.build());
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String link, Post.PostType type, int notificationId) {
        // On notification click intent
        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        clickIntent.putExtra(EXTRA_TYPE, type);
        PendingIntent clickPIntent = PendingIntent.getActivity(this, type.ordinal(), clickIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        // On action button click intent
        Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(link));
        PendingIntent urlPIntent = PendingIntent.getActivity(this, type.ordinal(), urlIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        Intent unsubscribeIntent = new Intent();
        unsubscribeIntent.setAction(KEY_UNSUBSCRIBE);
        unsubscribeIntent.putExtra(EXTRA_TYPE, type);
        unsubscribeIntent.putExtra(EXTRA_NOTIF_ID, notificationId);
        // TODO requestCode = 0 just works because there are no notifs for YT videos coming from the Firebase
        PendingIntent unsubscribePIntent = PendingIntent.getBroadcast(this, 0, unsubscribeIntent,
                FLAG_ONE_SHOT | FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Default")
                .setSmallIcon(R.drawable.ic_ps_app_controller_notext_white)
                .setContentTitle(title)
                .setGroup(type.name)
                .addAction(R.drawable.ic_remove_black_24dp, getString(R.string.notification_unsubscribe), unsubscribePIntent)
                .setOnlyAlertOnce( true )
                .setAutoCancel(true);

        if (type == PS_VIDEO) {
            notificationBuilder.setContentIntent(urlPIntent);
        } else {
            notificationBuilder.setContentIntent(clickPIntent);
            notificationBuilder.addAction(R.drawable.ic_open_in_browser_black_24dp, getString(R.string.notification_open_url), urlPIntent);
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
