package com.truedreamz.firebaseauthenticationapp.firebase_cloud_messaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.truedreamz.firebaseauthenticationapp.R;

public class FCMActivity extends AppCompatActivity {

    private TextView txtNotificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        txtNotificationMessage=(TextView)findViewById(R.id.txtNotificationMessage);

        Bundle extras = getIntent().getExtras();

        if(extras!=null){
            //String title=extras.getString("NotifyTitle");
            String message=extras.getString("NotifyMessage");
            txtNotificationMessage.setText("Message:"+message);
        }
    }
}
