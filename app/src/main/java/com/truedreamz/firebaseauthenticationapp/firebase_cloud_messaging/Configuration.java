package com.truedreamz.firebaseauthenticationapp.firebase_cloud_messaging;

/**
 * Created by wisdom-JP on 11/25/2016.
 */

public class Configuration {
    // global topic to receive app wide push notifications
    public static final String TOPIC_AKC = "AKC";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "com.augray.AugRayAvatar.FCMregistrationComplete";
    public static final String PUSH_NOTIFICATION = "com.augray.AugRayAvatar.pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
