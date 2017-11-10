package com.example.asifsabir.blooddonor;

/**
 * Created by asifsabir on 11/9/17.
 */


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //do location distance check
            showNotification(remoteMessage.getData().get("name"), remoteMessage.getData().get("bloodGroup"),remoteMessage.getData().get("phone"),remoteMessage.getData().get("location"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }

    private void showNotification(String name, String bloodGroup,String phone, String location) {
        Intent intent = new Intent(this, ShowRequest.class);

        intent.putExtra("name",name);
        intent.putExtra("bloodGroup",bloodGroup);
        intent.putExtra("phone",phone);
        intent.putExtra("location",location);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Blood wanted: " + bloodGroup)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("By: " + name+" At: "+location)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }
}