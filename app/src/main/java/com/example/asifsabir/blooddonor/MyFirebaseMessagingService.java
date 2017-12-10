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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //do location distance check
            showNotification(remoteMessage.getData().get("name"), remoteMessage.getData().get("bloodGroup"),
                    remoteMessage.getData().get("phone"), remoteMessage.getData().get("location"),
                    remoteMessage.getData().get("latitude"), remoteMessage.getData().get("longitude")
                    , remoteMessage.getData().get("timeStamp"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }

    private void showNotification(String name, String bloodGroup,
                                  String phone, String location,
                                  String latitude, String longitude,
                                  String timeStamp) {
        Intent intent = new Intent(this, ShowRequest.class);

        intent.putExtra("name", name);
        intent.putExtra("bloodGroup", bloodGroup);
        intent.putExtra("phone", phone);
        intent.putExtra("location", location);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("timeStamp", timeStamp);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, createID() /* Request code 0*/, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Blood wanted: " + bloodGroup)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("By: " + name + " At: " + location)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(createID() /* ID of notification 0*/, notificationBuilder.build());


    }

    //creating ID using date stamp
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmssSS", Locale.US).format(now));
        return id;
    }
}