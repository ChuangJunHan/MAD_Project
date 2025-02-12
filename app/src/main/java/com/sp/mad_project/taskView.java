package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class taskView extends AppCompatActivity {
    private static final int CREATE_TASK_REQUEST = 1;
    private databaseHelper dbHelper;
    private List<Task> taskList;
    private taskAdapter adapter;
    private int groupId;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        dbHelper = new databaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView groupNameTextView = findViewById(R.id.groupName);

        // Get group ID
        groupId = getIntent().getIntExtra("groupId", -1);
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        if (groupId != -1) {
            String groupName = dbHelper.getGroupNameById(groupId);
            groupNameTextView.setText(groupName);
        } else {
            Toast.makeText(this, "Invalid group information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new databaseHelper(this);

        // Load tasks for the selected group
        loadTasks();

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new taskAdapter(taskList);
        recyclerView.setAdapter(adapter);

        // Add Task button click listener
        findViewById(R.id.addTaskButton).setOnClickListener(v -> {
            Intent intent = new Intent(taskView.this, createTask.class);
            intent.putExtra("groupId", groupId);
            startActivityForResult(intent, CREATE_TASK_REQUEST);
        });

        toolbar.setOnClickListener(v -> {
            Intent intent = new Intent(taskView.this, chatDetails.class);
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    // Load tasks from the database
    private void loadTasks() {
        taskList = dbHelper.getTasksByGroupId(groupId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_TASK_REQUEST && resultCode == RESULT_OK) {
            // Reload the task list when returning from createTask activity
            loadTasks();
            adapter.notifyDataSetChanged();
        }
    }
}
