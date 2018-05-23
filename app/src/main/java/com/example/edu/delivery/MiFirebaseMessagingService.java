package com.example.edu.delivery;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by user on 3/4/2018.
 */

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    public static  final String TAG = "NOTICIAS" ;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.d(TAG, "Mensaje recibido de: " + from);
        if (remoteMessage.getNotification() != null){
            NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify=new Notification.Builder
                    (getApplicationContext()).setContentTitle(remoteMessage.getNotification().getTitle()).setContentText(remoteMessage.getNotification().getBody()).
                    setContentTitle(remoteMessage.getNotification().getTitle()).setSmallIcon(R.drawable.logito).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
            Log.d(TAG,"Notificación: " + remoteMessage.getNotification().getBody());
        }
    }
}
