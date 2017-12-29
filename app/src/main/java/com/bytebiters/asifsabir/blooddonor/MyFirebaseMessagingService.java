package com.bytebiters.asifsabir.blooddonor;

/**
 * Created by asifsabir on 11/9/17.
 */


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    GPSTracker gps;
    double distance;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("error", "trying");


        SharedPreferences prefs = getSharedPreferences("gpsData", MODE_PRIVATE);
        String dbLat = prefs.getString("dbLat", null);
        String dbLon = prefs.getString("dbLon", null);

        if (dbLat != null & dbLon != null) {
            Log.d("error", "location found!" + "\n" + dbLat + "\n" + dbLon);

        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("error", "paise");
            double payloadLat = Double.valueOf(remoteMessage.getData().get("latitude"));
            double payloadLon = Double.valueOf(remoteMessage.getData().get("longitude"));
            Log.d("error", payloadLat + "\n" + payloadLon);


            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(dbLat));
            loc1.setLongitude(Double.parseDouble(dbLon));

            Location loc2 = new Location("");
            loc2.setLatitude(payloadLat);
            loc2.setLongitude(payloadLon);
            distance = loc1.distanceTo(loc2);

            Log.d("error", "dist mmeasured:" + String.valueOf(distance));

            //else? if not location found then? do what?

            int notifyParameter = checkSettingsData();

            Log.d("error", "setting: " + String.valueOf(notifyParameter));

            if (notifyParameter * 1000 > (int) distance) {    //*1000 cz distance coming in meters//0 is disabled ; never logic is true here then
                Log.d("error", "login ok! showing notification");

                //do location distance check
                showNotification(remoteMessage.getData().get("name"), remoteMessage.getData().get("bloodGroup"),
                        remoteMessage.getData().get("phone"), remoteMessage.getData().get("location"),
                        remoteMessage.getData().get("latitude"), remoteMessage.getData().get("longitude")
                        , remoteMessage.getData().get("timeStamp"),remoteMessage.getData().get("reqId"));
            }
            //else do nothing ! simple

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }


    private void showNotification(String name, String bloodGroup,
                                  String phone, String location,
                                  String latitude, String longitude,
                                  String timeStamp,String reqId) {
        Intent intent = new Intent(this, ShowRequest.class);

        intent.putExtra("name", name);
        intent.putExtra("bloodGroup", bloodGroup);
        intent.putExtra("phone", phone);
        intent.putExtra("location", location);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("timeStamp", timeStamp);
        intent.putExtra("reqId", reqId);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, createID() /* Request code 0*/, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Blood wanted: " + bloodGroup)
                .setSmallIcon(R.drawable.notification_icon)
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
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    //sending the radius data
    public int checkSettingsData() {
        //viewing saved data in settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enableNotification = prefs.getBoolean("enable_notification", true);
        String radiusRange = prefs.getString("notification_range", "50");
        if (enableNotification == true) {
            return Integer.valueOf(radiusRange);
        } else {
            return 0;
        }
    }
}