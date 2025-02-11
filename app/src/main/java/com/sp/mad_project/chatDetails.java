package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class chatDetails extends AppCompatActivity {

    private TextView groupNameText, groupDescriptionText, groupKeyText;
    private ListView membersListView;
    private Button inviteMembersButton, backToChatButton;
    private chatDatabaseHelper dbHelper;
    private String groupName, loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        groupNameText = findViewById(R.id.groupNameText);
        groupDescriptionText = findViewById(R.id.groupDescriptionText);
        groupKeyText = findViewById(R.id.groupKeyText);
        membersListView = findViewById(R.id.membersListView);
        inviteMembersButton = findViewById(R.id.inviteMembersButton);
        backToChatButton = findViewById(R.id.backToChatButton);

        dbHelper = new chatDatabaseHelper(this);

        // Get group details from the intent
        groupName = getIntent().getStringExtra("groupName");
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        groupNameText.setText(groupName);
        groupDescriptionText.setText(dbHelper.getGroupDescription(groupName));

        // Retrieve and display the group key
        String groupKey = dbHelper.getGroupKey(groupName);
        groupKeyText.setText("Group Key: " + groupKey);

        // Retrieve and display group members
        List<String> members = dbHelper.getGroupMembers(groupName);
        membersAdapter adapter = new membersAdapter(this, members, new ArrayList<>());
        membersListView.setAdapter(adapter);

        // Invite Members Button
        inviteMembersButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatDetails.this, inviteMembers.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        // Back to Chat Button
        backToChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(chatDetails.this, chatMessages.class);
            intent.putExtra("groupName", groupName);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish();
        });
    }
}
