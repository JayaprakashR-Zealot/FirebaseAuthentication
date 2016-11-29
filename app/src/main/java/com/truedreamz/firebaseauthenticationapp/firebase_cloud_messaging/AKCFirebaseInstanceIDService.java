package com.truedreamz.firebaseauthenticationapp.firebase_cloud_messaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by wisdom-JP on 11/25/2016.
 */

public class AKCFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = AKCFirebaseInstanceIDService.class.getSimpleName();
    //PreferenceStore preferenceStore;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //preferenceStore=new PreferenceStore(this.getApplicationContext());

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        /*Intent registrationComplete = new Intent(FCMConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/

        try {
            Intent registrationComplete = new Intent();
            registrationComplete.putExtra("token", refreshedToken);
            registrationComplete.setAction(Configuration.REGISTRATION_COMPLETE);
            sendBroadcast(registrationComplete);
        }catch (Exception ex){
            Log.e(TAG,"FCM Exception:"+ex.getLocalizedMessage());
        }

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.d(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Configuration.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("FCMRegToken", token);
        editor.commit();
    }
}