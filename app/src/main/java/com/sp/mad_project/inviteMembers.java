package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class inviteMembers extends AppCompatActivity {

    private RecyclerView membersRecyclerView;
    private Button inviteButton;
    private membersAdapter adapter;
    private List<String> selectedMembers;
    private List<String> availableUsers;
    private databaseHelper dbHelper;
    private int groupId;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_members);

        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        inviteButton = findViewById(R.id.inviteButton);

        dbHelper = new databaseHelper(this);

        // Get group details from the intent
        groupId = getIntent().getIntExtra("groupId", -1);
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (groupId == -1 || loggedInUser == null) {
            Toast.makeText(this, "Invalid group or user information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch available users (excluding already added members)
        availableUsers = dbHelper.getAllUsersNotInGroup(groupId);
        Log.d("InviteMembers", "Group ID: " + groupId);
        Log.d("InviteMembers", "Available Users: " + availableUsers);

        if (availableUsers.isEmpty()) {
            Toast.makeText(this, "No users available to invite.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedMembers = new ArrayList<>();
        adapter = new membersAdapter(this, availableUsers, selectedMembers);

        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(adapter);

        inviteButton.setOnClickListener(v -> {
            if (selectedMembers.isEmpty()) {
                Toast.makeText(this, "Please select at least one member.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add selected members to the group
            dbHelper.addMembersToGroup(groupId, selectedMembers);
            Toast.makeText(this, "Members invited successfully!", Toast.LENGTH_SHORT).show();

            // Redirect back to chatDetails
            Intent intent = new Intent(inviteMembers.this, chatDetails.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish();
        });
    }
}
