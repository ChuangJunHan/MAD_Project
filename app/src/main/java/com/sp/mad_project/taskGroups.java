package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class taskGroups extends AppCompatActivity {

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
            Intent intent = new Intent(taskGroups.this, login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Fetch and display groups for the logged-in user
        loadTaskGroups();

        // Handle "Add Group" button click
        addGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(taskGroups.this, addGroup.class);
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("previousPage", "taskGroups");
            startActivity(intent);
        });

        // Handle logout
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(taskGroups.this, login.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadTaskGroups() {
        List<Group> taskGroups = dbHelper.getGroupsWithTaskCounts(loggedInUser);

        if (taskGroups.isEmpty()) {
            Toast.makeText(this, "No task groups found.", Toast.LENGTH_SHORT).show();
        }

        groupAdapter adapter = new groupAdapter(this, taskGroups, false);
        groupsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the groups list when the activity is resumed
        loadTaskGroups();
    }
}
