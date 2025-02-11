package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class chatGroups extends AppCompatActivity {

    private ListView groupListView;
    private Button addGroupButton, logoutButton;
    private ArrayAdapter<String> adapter;
    private chatDatabaseHelper dbHelper;
    private List<String> groups;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        groupListView = findViewById(R.id.groupListView);
        addGroupButton = findViewById(R.id.addGroupButton);
        logoutButton = findViewById(R.id.logoutButton);

        dbHelper = new chatDatabaseHelper(this);

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
        loadUserGroups();

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

        // Handle group selection
        groupListView.setOnItemClickListener((parent, view, position, id) -> {
            String groupName = groups.get(position);

            Intent intent = new Intent(chatGroups.this, chatMessages.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });
    }

    private void loadUserGroups() {
        groups = dbHelper.getGroupsForUser(loggedInUser);

        if (groups.isEmpty()) {
            Toast.makeText(this, "You are not a member of any groups", Toast.LENGTH_SHORT).show();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groups);
        groupListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the groups list when the activity is resumed
        loadUserGroups();
    }
}
