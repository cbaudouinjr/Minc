package com.abotimable.minc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Authentication extends AppCompatActivity {

    private final String loginEndpoint = "https://minc.app/api/v1/register";
    private Button login;
    private EditText emailInput;
    private EditText passwordInput;
    private String email;
    private String password;
    private String token;
    private Thread networkOperations;
    private boolean loginFlowSuccessful;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_login);
        login = findViewById(R.id.button_login);
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginFlow();
            }
        });

        super.onCreate(savedInstanceState);

    }

    private void handleLoginFlow(){
        final JSONObject loginPayload = new JSONObject();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        // Alert user email/password can't be blank
        if( email.equals("") || password.equals("")){
            sendAlert(getString(R.string.login_alert_title), getString(R.string.login_alert_message));
            return;
        }

        try{
            loginPayload.put("email", email);
            loginPayload.put("password", password);

            networkOperations = new Thread(new Runnable() {
                @Override
                public void run() {
                    loginFlowSuccessful = sendLoginInfo(loginPayload);
                }
            });

            networkOperations.start();
            login.setText(R.string.button_login_loading);
            networkOperations.join();
            if(loginFlowSuccessful){
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                this.startActivity(i);
            }
        }catch (JSONException | InterruptedException e){
            System.out.println(e);
        }
    }

    private boolean sendLoginInfo(JSONObject loginPayload){
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            StringEntity requestEntity = new StringEntity(loginPayload.toString(), ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(loginEndpoint);
            httpPost.setEntity(requestEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();
            String jsonData = EntityUtils.toString(responseEntity);

            JSONObject jsonResponse = new JSONObject(jsonData);

            token = jsonResponse.get("token").toString();

            return handleToken();

        }catch (IOException | ParseException | JSONException e){
            System.out.println(e);
            return false;
        }
    }

    private boolean handleToken(){
        if(token != null){
            SharedPreferences sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sessionData.edit();
            editor.putString("token", token);
            editor.commit();

            return true;
        }else{
            return false;
        }
    }

    private void sendAlert(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title);
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton(R.string.login_alert_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
