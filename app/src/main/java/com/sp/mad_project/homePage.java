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

        // Handle navigation bar
        navigationHelper.setupNavigationBar(this, loggedInUser);

        // Handle logout
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, login.class);
            startActivity(intent);
            finish();
        });
    }
}
