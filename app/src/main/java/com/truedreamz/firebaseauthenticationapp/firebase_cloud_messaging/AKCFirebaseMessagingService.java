package com.truedreamz.firebaseauthenticationapp.firebase_cloud_messaging;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.id.message;

/**
 * Created by wisdom-JP on 11/25/2016.
 */

public class AKCFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = AKCFirebaseMessagingService.class.getSimpleName();

    private NotificationUtilities notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String title,String message) {
        if (!NotificationUtilities.isAppIsInBackground(getApplicationContext())) {
            Log.d(TAG, "handleNotification : app is in foreground, broadcast toast.");
            // app is in foreground, broadcast the push message
            broadcastPushNotification(title,message);
        }else{
            // If the app is in background, firebase itself handles the notification
            Log.d(TAG, "handleNotification : app is in background.");
        }
    }

    private void broadcastPushNotification(String title,String message){
        try {
            Intent pushNotification = new Intent();
            //pushNotification.putExtra("message", message);
            pushNotification.putExtra("NotifyTitle",title);
            pushNotification.putExtra("NotifyMessage",message);
            pushNotification.setAction(Configuration.PUSH_NOTIFICATION);
            sendBroadcast(pushNotification);
        }catch (Exception ex){
            Log.e(TAG,"Pushnotify Exception:"+ex.getLocalizedMessage());
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtilities.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                broadcastPushNotification(title,message);
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), FCMActivity.class);
                //resultIntent.putExtra("message", message);
                resultIntent.putExtra("NotifyTitle",title);
                resultIntent.putExtra("NotifyMessage",message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtilities(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtilities(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}