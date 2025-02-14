package com.sp.mad_project;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class notifications extends AppCompatActivity {

    private LinearLayout notificationContainer;
    private databaseHelper dbHelper;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationContainer = findViewById(R.id.notificationContainer);
        dbHelper = new databaseHelper(this);

        // Get the logged-in user from intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load notifications
        loadNotifications();

        navigationHelper.setupNavigationBar(this, loggedInUser);
    }

    private void loadNotifications() {
        notificationContainer.removeAllViews();

        // Fetch tasks within 7 days assigned to the logged-in user
        List<Task> tasks = dbHelper.getTasksForUser(loggedInUser);
        if (tasks.isEmpty()) {
            addMessage("No upcoming tasks found.");
        } else {
            addMessage("Upcoming Tasks:");
            for (Task task : tasks) {
                addTaskToView(task);
            }
        }

        List<Message> events = dbHelper.getEventsForUserGroups(loggedInUser);
        if (events.isEmpty()) {
            addMessage("No events found.");
        } else {
            addMessage("Events:");
            for (Message event : events) {
                addEventToView(event);
            }
        }
    }

    private void addMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(16, 16, 16, 16);
        notificationContainer.addView(textView);
    }

    private void addTaskToView(Task task) {
        TextView taskView = new TextView(this);
        taskView.setText("Task: " + task.getName() + "\nDeadline: " + task.getDeadline());
        taskView.setPadding(16, 16, 16, 16);
        notificationContainer.addView(taskView);
    }

    private void addEventToView(Message event) {
        TextView eventView = new TextView(this);
        eventView.setText("Event: " + event.getContent());
        eventView.setPadding(16, 16, 16, 16);
        notificationContainer.addView(eventView);
    }
}
