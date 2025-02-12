package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class inviteMembers extends AppCompatActivity {

    private ListView membersListView;
    private Button inviteButton;
    private membersAdapter adapter;
    private List<String> selectedMembers;
    private List<String> availableUsers;
    private chatDatabaseHelper dbHelper;
    private String groupName, loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_members);

        membersListView = findViewById(R.id.membersListView);
        inviteButton = findViewById(R.id.inviteButton);

        dbHelper = new chatDatabaseHelper(this);

        // Get group details from the intent
        groupName = getIntent().getStringExtra("groupName");
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        // Dummy users for testing
        availableUsers = new ArrayList<>();
        availableUsers.add("User1");
        availableUsers.add("User2");
        availableUsers.add("User3");

        selectedMembers = new ArrayList<>();
        adapter = new membersAdapter(this, availableUsers, selectedMembers);
        membersListView.setAdapter(adapter);

        inviteButton.setOnClickListener(v -> {
            if (selectedMembers.isEmpty()) {
                Toast.makeText(this, "Please select at least one member", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add selected members to the group
            long groupId = dbHelper.getGroupIdByName(groupName);
            dbHelper.addMembersToGroup(groupId, selectedMembers);

            Toast.makeText(this, "Members invited successfully!", Toast.LENGTH_SHORT).show();

            // Redirect back to chatDetails
            Intent intent = new Intent(inviteMembers.this, chatDetails.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish();
        });
    }
}
