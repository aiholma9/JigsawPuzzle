package com.abdul.jigsawpuzzle.Broadcast;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.abdul.jigsawpuzzle.Common.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService{

    //Receive message
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        handleNotification(remoteMessage.getNotification().getBody());
    }

    private void handleNotification(String body){
        Intent pushNotification = new Intent(Common.PUSH);
        pushNotification.putExtra("message", body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}
