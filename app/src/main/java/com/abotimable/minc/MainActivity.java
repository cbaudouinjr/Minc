package com.abotimable.minc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String token;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);

        token = sessionData.getString("token", null);
        super.onCreate(savedInstanceState);

        if( token == null ){
            Intent i = new Intent(getApplicationContext(), Authentication.class);
            this.startActivity(i);
        }else{
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
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

