package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class addGroup extends AppCompatActivity {

    private Button createGroupButton, joinGroupButton;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        createGroupButton = findViewById(R.id.createGroupButton);
        joinGroupButton = findViewById(R.id.joinGroupButton);

        // Get the logged-in user from the intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(addGroup.this, createGroup.class);
            intent.putExtra("loggedInUser", loggedInUser); // Pass loggedInUser
            startActivity(intent);
        });

        joinGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(addGroup.this, joinWithKey.class);
            intent.putExtra("loggedInUser", loggedInUser); // Pass loggedInUser
            startActivity(intent);
        });
    }
}
