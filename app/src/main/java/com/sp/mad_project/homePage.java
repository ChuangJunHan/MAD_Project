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

        loggedInUser = getIntent().getStringExtra("loggedInUser");

        TextView userDisplay = findViewById(R.id.userDisplayTextView);
        userDisplay.setText("Welcome, " + loggedInUser + "!");

        navigationHelper.setupNavigationBar(this, loggedInUser);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(homePage.this, login.class);
            startActivity(intent);
            finish();
        });
    }
}
