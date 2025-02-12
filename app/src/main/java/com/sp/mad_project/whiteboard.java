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
    private Button clearButton, doneButton;
    private int groupId;
    private databaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);

        drawingView = findViewById(R.id.drawingView);
        clearButton = findViewById(R.id.btnClear);
        doneButton = findViewById(R.id.btnDone);

        dbHelper = new databaseHelper(this);

        // Get the groupId from the intent
        groupId = getIntent().getIntExtra("groupId", -1);
        if (groupId == -1) {
            Toast.makeText(this, "Group not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        clearButton.setOnClickListener(v -> drawingView.clearCanvas());

        doneButton.setOnClickListener(v -> {
            try {
                String filePath = saveDrawing(groupId);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("drawingPath", filePath);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (IOException e) {
                Toast.makeText(this, "Error saving drawing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String saveDrawing(int groupId) throws IOException {
        // Convert the drawing to a bitmap
        Bitmap bitmap = drawingView.getBitmap();

        // Create a directory for the group if it doesn't exist
        File groupDir = new File(getExternalFilesDir(null), "group_" + groupId);
        if (!groupDir.exists()) groupDir.mkdirs();

        // Save the bitmap to a file
        File file = new File(groupDir, "drawing_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }

        // Save the drawing details in the database
        dbHelper.addDrawingToGroup(groupId, file.getAbsolutePath());
        return file.getAbsolutePath();
    }
}
