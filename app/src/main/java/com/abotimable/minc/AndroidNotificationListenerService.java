package com.abotimable.minc;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class AndroidNotificationListenerService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
