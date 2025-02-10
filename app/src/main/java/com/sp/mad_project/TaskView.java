package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class TaskView extends AppCompatActivity {
    groupdb db;
    LinearLayout tasksContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        db = new groupdb(this);
        tasksContainer = findViewById(R.id.tasksContainer);

        String groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName != null) {
            loadTasks(groupName); // Load tasks if group name is valid
        } else {
            Toast.makeText(this, "Invalid group name", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if group name is null
        }

    }

    private void loadTasks(String groupName) {
        tasksContainer.removeAllViews();
        List<String[]> tasks = db.getTasksByGroup(groupName);

        for (String[] task : tasks) {
            TextView taskView = new TextView(this);
            taskView.setText(task[0] + " | " + task[1] + " | " + task[2]);
            tasksContainer.addView(taskView);
        }

    }
}