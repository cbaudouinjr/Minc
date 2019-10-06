package com.abotimable.minc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class AndroidNotificationListenerService extends NotificationListenerService {

    private final String endpoint = "https://minc.app/api/v1/hook";
    private JSONObject notificationPayload;
    private JSONObject notificationExtras;
    private Thread networkOperations;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        final SharedPreferences sessionData = getSharedPreferences(getString(R.string.preferences_location_name), Context.MODE_PRIVATE);
        String fcmToken = sessionData.getString("fcmToken", null);
        notificationPayload = new JSONObject();
        notificationExtras = new JSONObject();

        Bundle extras = sbn.getNotification().extras;
        Set<String> extrasKeys = sbn.getNotification().extras.keySet();

        try{
            if(fcmToken != null){
                System.out.println("Token valid");
                System.out.println(fcmToken);
                notificationPayload.put("fcmToken", fcmToken);
            }else{
                System.out.println("No token");
                notificationPayload.put("fcmToken", "None");
            }
            notificationPayload.put("id", sbn.getId());
            notificationPayload.put("packageName", sbn.getPackageName());
            notificationPayload.put("OpPackage", sbn.getOpPkg());
            notificationPayload.put("postTime", sbn.getPostTime());

            for( String key : extrasKeys ){
                notificationExtras.put(key, JSONObject.wrap(extras.get(key)));
            }

            notificationPayload.put("extras", notificationExtras);
        }catch (JSONException e){
            System.out.println(e);
        }

        networkOperations = new Thread(new Runnable() {
            @Override
            public void run() {
                sendPayload();
            }
        });

        networkOperations.start();

        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    private void sendPayload(){
        try{
            URL hookURL = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) hookURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(notificationPayload.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            System.out.println(connection.getResponseCode());

        }catch (IOException i){
            System.out.println(i);

        }
    }
}
