package com.sp.mad_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class notifications extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private notificationAdapter adapter;
    private List<Object> notificationList; // Holds tasks and events
    private databaseHelper dbHelper;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new databaseHelper(this);
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        notificationList = new ArrayList<>();

        loadNotifications();

        navigationHelper.setupNavigationBar(this, loggedInUser);

        adapter = new notificationAdapter(this, notificationList);
        notificationsRecyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        List<Task> tasks = dbHelper.getTasksForUser(loggedInUser);
        if (tasks.isEmpty()) {
            Toast.makeText(this, "No tasks found.", Toast.LENGTH_SHORT).show();
        } else {
            notificationList.addAll(tasks);
        }

        List<Message> events = dbHelper.getEventsForUserGroups(loggedInUser);
        if (events.isEmpty()) {
            Toast.makeText(this, "No events found.", Toast.LENGTH_SHORT).show();
        } else {
            notificationList.addAll(events);
        }
    }
}
