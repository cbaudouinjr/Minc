package com.abotimable.minc;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body){
        // WARNING: You need to include a small icon, or some Android version will throw errors.
        // Furthermore, you should use an image located in the project heirarchy, not a system
        // image. Using system images seems to cause errors after a period of time.
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context );
        notificationManagerCompat.notify(1, nBuilder.build());
    }
}
