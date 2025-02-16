package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
    private RecyclerView tasksRecyclerView;
    private List<Task> taskList;
    private taskAdapter adapter;
    private int groupId;
    private String loggedInUser;
    private Toolbar toolbar;
    private TextView groupNameTextView;
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        dbHelper = new databaseHelper(this);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbar);
        groupNameTextView = findViewById(R.id.groupName);
        addTaskButton = findViewById(R.id.addTaskButton);

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

        loadTasks();

        navigationHelper.setupNavigationBar(this, loggedInUser);

        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(taskView.this, createTask.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivityForResult(intent, CREATE_TASK_REQUEST);
        });

        toolbar.setOnClickListener(v -> {
            Intent intent = new Intent(taskView.this, chatDetails.class);
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void loadTasks() {
        taskList = dbHelper.getTasksByGroupId(groupId);
        adapter = new taskAdapter(taskList, loggedInUser, dbHelper);
        tasksRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_TASK_REQUEST && resultCode == RESULT_OK) {
            loadTasks();
            adapter.notifyDataSetChanged();
        }
    }
}