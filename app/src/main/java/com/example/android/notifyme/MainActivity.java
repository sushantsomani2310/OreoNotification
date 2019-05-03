package com.example.android.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button notifyButton,updateButton,cancelButton;
    private static final String PRIMARY_NOTIFICATION_CHANNEL_ID = "primary_notification_channel";
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifyButton = findViewById(R.id.notify_button);
        updateButton = findViewById(R.id.update_button);
        cancelButton = findViewById(R.id.cancel_button);

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the notification
                updateNotification();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel the notification
                cancelNotification();
            }
        });
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver,new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        createNotificationChannel();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    /**
     * creates notification when button is pressed
     */
    public void sendNotification(){
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = getNotificationBuilder();
        notificationBuilder.addAction(R.drawable.ic_update,"Update Notification",updatePendingIntent);
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }

    /**
     * creates notification channel here for devices have Oreo or later
     */
    public void createNotificationChannel(){
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //create notification channel here
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_NOTIFICATION_CHANNEL_ID,
                    "Notification First",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        //below intent helps to launch respective activity when user taps notification
        Intent mainIntent = new Intent(this,MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,mainIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,PRIMARY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Notification title")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("This is text of the notification")
                .setContentIntent(mainActivityPendingIntent)
                .setAutoCancel(true);

        return notificationBuilder;
    }

    /**
     * updates the existing notification
     */
    public void updateNotification(){
        Bitmap notificationImage = BitmapFactory.decodeResource(getResources(),R.drawable.ic_notification);
        NotificationCompat.Builder notifBuilder = getNotificationBuilder();
        notifBuilder.setStyle(new NotificationCompat.BigPictureStyle()
        .bigPicture(notificationImage)
        .setBigContentTitle("Updated Notification"));
        notificationManager.notify(NOTIFICATION_ID,notifBuilder.build());
    }

    /**
     * cancels the existing notification
     */
    public void cancelNotification(){
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //update the notification
            updateNotification();
        }
    }
}
