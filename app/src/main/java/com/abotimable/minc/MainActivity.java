package com.abotimable.minc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    public static final String CHANNEL_ID = "minc";
    private static final String CHANNEL_NAME = "Minc";
    private static final String CHANNEL_DESC = "Minc Notifications";
    private SharedPreferences sessionData;
    String token;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DESC);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        token = sessionData.getString("token", null);
        super.onCreate(savedInstanceState);

        if( token == null ){
            displayAuthentication();
        }else {
            displayHome();
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

    private void displayAuthentication(){
        Intent i = new Intent(getApplicationContext(), Authentication.class);
        this.startActivity(i);
    }

    private void displayHome(){
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Button logout = new Button(this);
            logout.setText(R.string.button_logout);
            Toolbar.LayoutParams l3=new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            l3.gravity= Gravity.END;
            l3.rightMargin = 20;
            logout.setTextColor(Color.WHITE);
            logout.setLayoutParams(l3);
            logout.setClickable(true);
            logout.setBackgroundColor(Color.TRANSPARENT);
            toolbar.addView(logout);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeSessionData();
                    displayAuthentication();
                }
            });

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

    private void removeSessionData(){
        SharedPreferences sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionData.edit();

        editor.clear();
        editor.apply();
    }
}

