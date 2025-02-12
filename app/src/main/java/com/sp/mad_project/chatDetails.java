package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class chatDetails extends AppCompatActivity {

    private TextView groupNameText, groupDescriptionText, groupKeyText;
    private RecyclerView membersRecyclerView;
    private Button inviteMembersButton, backButton;
    private databaseHelper dbHelper;
    private int groupId;
    private String loggedInUser;
    private membersAdapter adapter;
    private List<String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        groupNameText = findViewById(R.id.groupNameText);
        groupDescriptionText = findViewById(R.id.groupDescriptionText);
        groupKeyText = findViewById(R.id.groupKeyText);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        inviteMembersButton = findViewById(R.id.inviteMembersButton);
        backButton = findViewById(R.id.backButton);

        dbHelper = new databaseHelper(this);

        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get group details and navigation info from the intent
        groupId = getIntent().getIntExtra("groupId", -1);
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (groupId == -1 || loggedInUser == null) {
            Toast.makeText(this, "Invalid group or user information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrieve group details
        String groupName = dbHelper.getGroupNameById(groupId);
        groupNameText.setText(groupName);
        groupDescriptionText.setText(dbHelper.getGroupDescriptionById(groupId));

        // Retrieve and display the group key
        String groupKey = dbHelper.getGroupKeyById(groupId);
        groupKeyText.setText("Group Key: " + groupKey);

        // Retrieve and display group members
        loadGroupMembers();

        // Invite Members Button
        inviteMembersButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatDetails.this, inviteMembers.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatDetails.this, homePage.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the group members when returning from inviteMembers activity
        loadGroupMembers();
    }

    private void loadGroupMembers() {
        // Retrieve and display group members
        members = dbHelper.getMembersByGroupId(groupId);

        if (members.isEmpty()) {
            Toast.makeText(this, "No members found in this group.", Toast.LENGTH_SHORT).show();
        }

        // Set up adapter and assign it to the RecyclerView
        if (adapter == null) {
            adapter = new membersAdapter(this, members, new ArrayList<>());
            membersRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
