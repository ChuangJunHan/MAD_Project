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
    private Button addGroupButton;
    private List<Group> groupList;
    private databaseHelper dbHelper;
    private String loggedInUser;
    private groupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        dbHelper = new databaseHelper(this);

        groupsRecyclerView = findViewById(R.id.groupsRecyclerView);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroupButton = findViewById(R.id.addGroupButton);

        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Toast.makeText(this, "No user logged in. Redirecting to login...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(taskGroups.this, login.class);
            startActivity(intent);
            finish();
            return;
        }

        loadTaskGroups();

        navigationHelper.setupNavigationBar(this, loggedInUser);

        addGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(taskGroups.this, addGroup.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });
    }

    private void loadTaskGroups() {
        groupList = dbHelper.getGroupsWithTaskCounts(loggedInUser);
        adapter = new groupAdapter(this, groupList, false, loggedInUser, "taskGroups");
        groupsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskGroups();
    }
}
