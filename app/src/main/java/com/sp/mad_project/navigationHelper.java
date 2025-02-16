package com.sp.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;

public class navigationHelper {

    public static void setupNavigationBar(Activity activity, String loggedInUser) {
        // Navigation to Chat Groups
        ImageButton chatsButton = activity.findViewById(R.id.chatsButton);
        if (chatsButton != null) {
            chatsButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, chatGroups.class);
                intent.putExtra("loggedInUser", loggedInUser);
                activity.startActivity(intent);
            });
        }

        // Navigation to Task Groups
        ImageButton tasksButton = activity.findViewById(R.id.tasksButton);
        if (tasksButton != null) {
            tasksButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, taskGroups.class);
                intent.putExtra("loggedInUser", loggedInUser);
                activity.startActivity(intent);
            });
        }

        ImageButton homeButton = activity.findViewById(R.id.homeButton);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, homePage.class);
                intent.putExtra("loggedInUser", loggedInUser);
                activity.startActivity(intent);
            });
        }

        // Navigation to Gantt Groups
        ImageButton ganttButton = activity.findViewById(R.id.ganttButton);
        if (ganttButton != null) {
            ganttButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ganttGroups.class);
                intent.putExtra("loggedInUser", loggedInUser);
                activity.startActivity(intent);
            });
        }

        // Navigation to Notifications
        ImageButton notificationsButton = activity.findViewById(R.id.notificationsButton);
        if (notificationsButton != null) {
            notificationsButton.setOnClickListener(v -> {
                Intent intent = new Intent(activity, notifications.class);
                intent.putExtra("loggedInUser", loggedInUser);
                activity.startActivity(intent);
            });
        }
    }
}
