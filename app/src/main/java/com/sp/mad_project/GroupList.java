package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.LinearLayout;

import java.util.List;

public class GroupList extends AppCompatActivity {
    groupdb db;
    FloatingActionButton addNewGroup;
    LinearLayout groupContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        db = new groupdb(this);
        groupContainer = findViewById(R.id.groupContainer);
        addNewGroup = findViewById(R.id.addNewGroup);

        loadGroups();
        addNewGroup.setOnClickListener(v -> {
            db.insertGroup("New Group " + (System.currentTimeMillis() % 1000));
            loadGroups(); // Refresh
        });

    }

    private void loadGroups() {
        groupContainer.removeAllViews();
        List<String> groups = db.getAllGroups();
        for (String groupName : groups) {
            Button groupButton = new Button(this);
            groupButton.setText(groupName); // Set the group name on the button

            // Set onClick to open TaskView and pass the group name
            groupButton.setOnClickListener(v -> {
                Intent intent = new Intent(GroupList.this, TaskView.class);
                intent.putExtra("GROUP_NAME", groupName); // Pass the selected group name
                 startActivity(intent);
            });

            // Add the button to the container
            groupContainer.addView(groupButton);
        }
    }
}