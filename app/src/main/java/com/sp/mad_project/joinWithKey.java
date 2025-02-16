package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class joinWithKey extends AppCompatActivity {

    private EditText groupKeyInput;
    private Button joinGroupButton;
    private databaseHelper dbHelper;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_with_key);

        groupKeyInput = findViewById(R.id.groupKeyInput);
        joinGroupButton = findViewById(R.id.joinGroupButton);

        dbHelper = new databaseHelper(this);

        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Toast.makeText(this, "No user logged in. Redirecting to login...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(joinWithKey.this, login.class);
            startActivity(intent);
            finish();
            return;
        }

        joinGroupButton.setOnClickListener(v -> {
            String groupKey = groupKeyInput.getText().toString().trim();

            if (groupKey.isEmpty()) {
                Toast.makeText(this, "Please enter a group key", Toast.LENGTH_SHORT).show();
                return;
            }

            long groupId = dbHelper.getGroupIdByKey(groupKey);
            if (groupId == -1) {
                Toast.makeText(this, "Invalid group key", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.addMemberToGroup(groupId, loggedInUser, false);
            if (success) {
                Toast.makeText(this, "Successfully joined the group!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(joinWithKey.this, chatGroups.class);
                intent.putExtra("loggedInUser", loggedInUser);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to join the group. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
