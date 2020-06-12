package com.alvaro.vgym;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alvaro.vgym.model.Workout;
import com.alvaro.vgym.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

import static com.alvaro.vgym.BaseApp.CHANNEL_ID;

public class AlertReceiver extends BroadcastReceiver
{
    private FirebaseService firebaseService;
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        firebaseService = FirebaseService.getInstance();

        if (firebaseService.getCurrentUser() != null)
        {
            String uid = firebaseService.getUid();

            DatabaseReference dbRef = firebaseService.getReference("selected/" + uid);

            dbRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    String selectedKey = dataSnapshot.getValue(String.class);

                    if (selectedKey != null)
                    {
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                        int d = calendar.get(Calendar.DAY_OF_WEEK) - 2;

                        if (d == (-1)) { d = 6; }

                        String reference = "routines/" + uid + "/" + selectedKey + "/workouts/" + d;

                        DatabaseReference dbWorkoutRef = firebaseService.getReference(reference);

                        dbWorkoutRef.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                Workout workout = dataSnapshot.getValue(Workout.class);

                                if (!workout.isRestDay())
                                {
                                    String title = context.getString(R.string.reminder);
                                    String contentText = context.getString(R.string.reminder_msg);
                                    contentText += " " + workout.getName() + "!";

                                    createNotification(context, title, contentText);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                    else { cancelAlarm(context, intent); }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
        else { cancelAlarm(context, intent); }
    }

    private void createNotification(Context context, String title, String contentText)
    {
        notificationManager = NotificationManagerCompat.from(context);

        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            0
        );

        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_routine_black_18dp)
                .setContentTitle(title)
                .setContentText(contentText)
                .setColor(context.getColor(R.color.primaryL))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        }
        else
        {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_routine_black_18dp)
                .setContentTitle(title)
                .setContentText(contentText)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        }

        notificationManager.notify(1, notification);
    }

    private void cancelAlarm(Context context, Intent intent)
    {
        AlarmManager alarmManager = (AlarmManager) context
            .getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmManager.cancel(pendingIntent);
    }
}
