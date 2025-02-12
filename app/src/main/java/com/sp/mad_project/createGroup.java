package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class createGroup extends AppCompatActivity {

    private EditText groupNameInput, groupDescriptionInput, maxMembersInput;
    private Button createGroupButton;
    private String loggedInUser;
    private chatDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupNameInput = findViewById(R.id.groupNameInput);
        groupDescriptionInput = findViewById(R.id.groupDescriptionInput);
        maxMembersInput = findViewById(R.id.maxMembersInput);
        createGroupButton = findViewById(R.id.createGroupButton);

        dbHelper = new chatDatabaseHelper(this);

        // Get the logged-in user from the intent
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        createGroupButton.setOnClickListener(v -> {
            String groupName = groupNameInput.getText().toString().trim();
            String groupDescription = groupDescriptionInput.getText().toString().trim();
            String maxMembersText = maxMembersInput.getText().toString().trim();

            if (groupName.isEmpty() || maxMembersText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxMembers = Integer.parseInt(maxMembersText);

            // Create the group in the database
            long groupId = dbHelper.addGroup(groupName, groupDescription, maxMembers);

            if (groupId != -1) {
                Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to inviteMembers page
                Intent intent = new Intent(createGroup.this, inviteMembers.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("loggedInUser", loggedInUser);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to create group. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
