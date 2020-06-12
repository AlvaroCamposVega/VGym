package com.alvaro.vgym;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BaseApp extends Application
{
    public static final String CHANNEL_ID = "notificationChannel";

    @Override
    public void onCreate()
    {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelName = getString(R.string.reminder);
            String channelDescription = getString(R.string.reminder_desc);

            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW
            );

            channel.setDescription(channelDescription);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
