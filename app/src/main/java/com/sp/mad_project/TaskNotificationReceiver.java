package com.sp.mad_project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("taskName");
        int taskId = intent.getIntExtra("taskId", 0);
        int daysBefore = intent.getIntExtra("daysBefore", 7); // Default to 7 days

        String message;
        if (daysBefore == 7) {
            message = "Your task '" + taskName + "' is due in **7 days**. Plan accordingly!";
        } else if (daysBefore == 1) {
            message = "🚨 Reminder: Your task '" + taskName + "' is due **tomorrow**!";
        } else {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_CHANNEL")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("Task Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(taskId + daysBefore, builder.build());
    }
}
