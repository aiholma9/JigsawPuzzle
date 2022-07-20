package com.abdul.jigsawpuzzle.Broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.abdul.jigsawpuzzle.Activity.MainActivity;
import com.abdul.jigsawpuzzle.R;

import static com.abdul.jigsawpuzzle.Common.Common.CHANNEL_ID;

public class DailyNotification extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        long notificationTime = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

       Intent notificationIntent = new Intent(context, MainActivity.class);
       notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Display notification on the device's system tray
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("Holma Puzzle Game").setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("Don't Forget To Play").setSound(uri).setAutoCancel(true).setWhen(notificationTime).setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000,1000,1000,1000,1000});

        //Check API level on the device is 27
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Holma Puzzle Game", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }
}
