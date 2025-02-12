package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class taskGroups extends AppCompatActivity {

    private ListView groupListView;
    private Button addGroupButton, logoutButton;
    private ArrayAdapter<String> adapter;
    private databaseHelper dbHelper;
    private List<Group> groups; // Use a Group class to store group details (name and ID)
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        groupListView = findViewById(R.id.groupListView);
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
        loadUserGroups();

        // Handle "Add Group" button click
        addGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(taskGroups.this, createGroup.class);
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

        // Handle group selection
        groupListView.setOnItemClickListener((parent, view, position, id) -> {
            Group selectedGroup = groups.get(position);

            Intent intent = new Intent(taskGroups.this, taskView.class);
            intent.putExtra("groupId", selectedGroup.getId()); // Pass group ID instead of name
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("previousPage", "taskGroups");
            startActivity(intent);
        });
    }

    private void loadUserGroups() {
        groups = dbHelper.getGroupsWithIdsForUser(loggedInUser); // Fetch groups with IDs

        if (groups.isEmpty()) {
            Toast.makeText(this, "You are not a member of any groups", Toast.LENGTH_SHORT).show();
        }

        // Extract group names for display
        List<String> groupNames = new ArrayList<>();
        for (Group group : groups) {
            groupNames.add(group.getName());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        groupListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the groups list when the activity is resumed
        loadUserGroups();
    }
}
