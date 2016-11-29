package com.truedreamz.firebaseauthenticationapp.firebase_cloud_messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import static android.R.id.message;


/**
 * Created by wisdom-JP on 10/26/2016.
 */

public class AKCFCMReceiver extends BroadcastReceiver{
    private static final String TAG = "FCMAKCReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"FCMReceiver onReceive");
        // checking for type intent filter
        if (intent.getAction().equals(Configuration.REGISTRATION_COMPLETE)) {
            // fcm successfully registered
            // now subscribe to `AKC` topic to receive app wide notifications
            FirebaseMessaging.getInstance().subscribeToTopic(Configuration.TOPIC_AKC);
            Log.d(TAG,"FCMReceiver : FCM is registered.");
        } else if (intent.getAction().equals(Configuration.PUSH_NOTIFICATION)) {
            // new push notification is received
            //String message = intent.getStringExtra("message");
            String not_title=intent.getStringExtra("NotifyTitle");
            String not_message=intent.getStringExtra("NotifyMessage");

            Log.d(TAG,"FCMReceiver message :"+not_message);
            Toast.makeText(context, "Push notification: " + not_message, Toast.LENGTH_LONG).show();

            Intent pushNotification = new Intent(context,FCMActivity.class);
            pushNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //pushNotification.putExtra("message", message);
            pushNotification.putExtra("NotifyTitle",not_title);
            pushNotification.putExtra("NotifyMessage",not_message);
            context.startActivity(pushNotification);
        }
    }
}
