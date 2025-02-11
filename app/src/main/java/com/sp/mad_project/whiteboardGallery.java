package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class whiteboardGallery extends AppCompatActivity {

    private static final int WHITEBOARD_REQUEST_CODE = 200;

    private RecyclerView drawingsRecyclerView;
    private Button addDrawingButton;
    private String groupName;
    private chatDatabaseHelper dbHelper;
    private List<Drawing> drawings;
    private drawingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard_gallery);

        drawingsRecyclerView = findViewById(R.id.drawingsRecyclerView);
        addDrawingButton = findViewById(R.id.addDrawingButton);

        dbHelper = new chatDatabaseHelper(this);

        groupName = getIntent().getStringExtra("groupName");
        if (groupName == null) {
            Toast.makeText(this, "Group not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDrawings();

        addDrawingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, whiteboard.class);
            intent.putExtra("groupName", groupName); // Pass group name for saving the drawing
            startActivityForResult(intent, WHITEBOARD_REQUEST_CODE);
        });
    }

    private void loadDrawings() {
        // Load drawings for the specific group
        drawings = dbHelper.getDrawingsForGroup(groupName);

        // Set up the RecyclerView
        adapter = new drawingAdapter(this, drawings);
        drawingsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Display as a grid
        drawingsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WHITEBOARD_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload drawings after adding a new one
            loadDrawings();
        }
    }
}
