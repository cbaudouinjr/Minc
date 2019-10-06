package com.abotimable.minc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    public static final String CHANNEL_ID = "minc";
    private static final String CHANNEL_NAME = "Minc";
    private static final String CHANNEL_DESC = "Minc Notifications";
    String token;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DESC);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        token = sessionData.getString("token", null);
        super.onCreate(savedInstanceState);

        if( token == null ){
            Intent i = new Intent(getApplicationContext(), Authentication.class);
            this.startActivity(i);
        }else{
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()){
                                SharedPreferences.Editor editor = sessionData.edit();
                                editor.putString("fcmToken", task.getResult().getToken());
                                editor.commit();
                            }else {
                                System.out.println("failure");
                            }
                        }
                    });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

