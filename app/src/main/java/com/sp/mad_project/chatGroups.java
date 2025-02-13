package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class chatGroups extends AppCompatActivity {

    private RecyclerView groupsRecyclerView;
    private Button addGroupButton, logoutButton;
    private databaseHelper dbHelper;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        groupsRecyclerView = findViewById(R.id.groupsRecyclerView);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroupButton = findViewById(R.id.addGroupButton);
        logoutButton = findViewById(R.id.logoutButton);

        dbHelper = new databaseHelper(this);

        // Get the logged-in user from the intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Toast.makeText(this, "No user logged in. Redirecting to login...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(chatGroups.this, login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Fetch and display groups for the logged-in user
        loadChatGroups();

        // Handle "Add Group" button click
        addGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatGroups.this, addGroup.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        // Handle logout
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatGroups.this, login.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadChatGroups() {
        List<Group> chatGroups = dbHelper.getGroupsWithEventCounts(loggedInUser);

        if (chatGroups.isEmpty()) {
            Toast.makeText(this, "No chat groups found.", Toast.LENGTH_SHORT).show();
        }

        groupAdapter adapter = new groupAdapter(this, chatGroups, true);
        groupsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatGroups();
    }
}