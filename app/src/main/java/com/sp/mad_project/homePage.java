package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class homePage extends AppCompatActivity {

    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get the loggedInUser from the intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        // Display the loggedInUser
        TextView userDisplay = findViewById(R.id.userDisplayTextView);
        userDisplay.setText("Welcome, " + loggedInUser + "!");

        // Navigation to Chat Groups
        Button chatGroupsButton = findViewById(R.id.chatGroupsButton);
        chatGroupsButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, chatGroups.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        // Navigation to Task Groups
        Button taskGroupsButton = findViewById(R.id.taskGroupsButton);
        taskGroupsButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, taskGroups.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        // Navigation to Home (Refresh the Current Activity)
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, homePage.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish();
        });

        // In your dashboard or main activity
        Button notificationsButton = findViewById(R.id.notificationsButton);
        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, notifications.class);
            intent.putExtra("loggedInUser", loggedInUser); // Pass the logged-in user
            startActivity(intent);
        });
    }
}
