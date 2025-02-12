package com.sp.mad_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class whiteboard extends AppCompatActivity {

    private com.sp.mad_project.drawingView drawingView;
    private Button saveButton, clearButton, doneButton;
    private String groupName;
    private databaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);

        drawingView = findViewById(R.id.drawingView);
        saveButton = findViewById(R.id.btnSave);
        clearButton = findViewById(R.id.btnClear);
        doneButton = findViewById(R.id.btnDone);

        dbHelper = new databaseHelper(this);

        // Get the group name from the intent
        groupName = getIntent().getStringExtra("groupName");
        if (groupName == null) {
            Toast.makeText(this, "Group not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        clearButton.setOnClickListener(v -> drawingView.clearCanvas());

        saveButton.setOnClickListener(v -> {
            try {
                String filePath = saveDrawing(groupName);
                Toast.makeText(this, "Drawing saved to " + filePath, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error saving drawing", Toast.LENGTH_SHORT).show();
            }
        });

        doneButton.setOnClickListener(v -> {
            try {
                String filePath = saveDrawing(groupName);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("drawingPath", filePath);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (IOException e) {
                Toast.makeText(this, "Error saving drawing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String saveDrawing(String groupName) throws IOException {
        // Convert the drawing to a bitmap
        Bitmap bitmap = drawingView.getBitmap();

        // Create a directory for the group if it doesn't exist
        File groupDir = new File(getExternalFilesDir(null), groupName);
        if (!groupDir.exists()) groupDir.mkdirs();

        // Save the bitmap to a file
        File file = new File(groupDir, "drawing_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }

        // Save the drawing details in the database
        dbHelper.addDrawingToGroup(groupName, file.getAbsolutePath());
        return file.getAbsolutePath();
    }
}
