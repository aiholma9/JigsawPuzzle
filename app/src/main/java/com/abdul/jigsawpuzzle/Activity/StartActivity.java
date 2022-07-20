package com.abdul.jigsawpuzzle.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class StartActivity extends AppCompatActivity {

    Button btn_offline, btn_online;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registrationNotification();

        btn_offline = (Button)findViewById(R.id.btn_offline);
        btn_online = (Button)findViewById(R.id.btn_online);
        btn_online.setEnabled(networkAvailable());

        btn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(StartActivity.this, OfflineHomeActivity.class);
                startActivity(Intent);
            }
        });

        btn_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(StartActivity.this, OnlineHomeActivity.class);
                startActivity(Intent);
            }
        });
    }

    //Check Internet connection
    private boolean networkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return  networkInfo != null && networkInfo.isConnected();
    }

    //
    private void registrationNotification() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Common.PUSH)){
                    String message = intent.getStringExtra("message");
                    showNotification("Holma Puzzle Game", message);
                }
            }
        };
    }

    //Display Push Notification
    private void showNotification(String s, String message) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext()).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(s)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText(message)
                .setAutoCancel(true).setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("complete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Common.PUSH));
    }

}
