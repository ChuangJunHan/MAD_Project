package com.sp.mad_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

public class chatMessages extends AppCompatActivity {

    private static final int WHITEBOARD_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton, optionsButton;
    private String loggedInUser;
    private int groupId;
    private databaseHelper dbHelper;
    private List<Message> messages;
    private messageAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView groupNameTextView = findViewById(R.id.groupName);

        loggedInUser = getIntent().getStringExtra("loggedInUser");
        groupId = getIntent().getIntExtra("groupId", -1);

        dbHelper = new databaseHelper(this);

        if (groupId != -1) {
            String groupName = dbHelper.getGroupNameById(groupId);
            groupNameTextView.setText(groupName);
        } else {
            Toast.makeText(this, "Invalid group information.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        optionsButton = findViewById(R.id.optionsButton);

        messages = dbHelper.getMessagesForGroupById(groupId);
        adapter = new messageAdapter(this, messages, loggedInUser);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (messageText.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addMessageToGroupById(groupId, loggedInUser, messageText, "message");
            messages.add(new Message(loggedInUser, messageText, "message"));
            adapter.notifyDataSetChanged();
            messagesRecyclerView.scrollToPosition(messages.size() - 1);
            messageInput.setText("");
        });

        optionsButton.setOnClickListener(v -> showOptionsDialog());

        navigationHelper.setupNavigationBar(this, loggedInUser);

        // Add click listener to the toolbar
        toolbar.setOnClickListener(v -> {
            Intent intent = new Intent(chatMessages.this, chatDetails.class);
            intent.putExtra("loggedInUser", loggedInUser);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void showOptionsDialog() {
        String[] options = {"Send Event", "Send File", "Open Whiteboard", "Take Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showEventNameDialog();
            } else if (which == 1) {
                sendFile();
            } else if (which == 2) {
                openWhiteboard();
            } else if (which == 3) {
                takePhoto();
            }
        });
        builder.show();
    }

    private void showEventNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Event Name");

        EditText eventNameInput = new EditText(this);
        eventNameInput.setHint("Enter event name");
        builder.setView(eventNameInput);

        builder.setPositiveButton("Next", (dialog, which) -> {
            String eventName = eventNameInput.getText().toString().trim();
            if (eventName.isEmpty()) {
                Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            showDatePickerDialog(eventName);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDatePickerDialog(String eventName) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            saveEvent(eventName, selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveEvent(String eventName, String eventDate) {
        String eventDetails = "Event: " + eventName + "\nDate: " + eventDate;

        dbHelper.addMessageToGroupById(groupId, "System", eventDetails, "event");
        messages.add(new Message("System", eventDetails, "event"));
        adapter.notifyDataSetChanged();
        messagesRecyclerView.scrollToPosition(messages.size() - 1);
    }

    private void sendFile() {
        Toast.makeText(this, "File sending feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void openWhiteboard() {
        Intent intent = new Intent(this, whiteboardGallery.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("previousPage", "chatMessages");
        startActivity(intent);
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WHITEBOARD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String drawingPath = data.getStringExtra("drawingPath");
            if (drawingPath != null) {
                dbHelper.addMessageToGroupById(groupId, loggedInUser, drawingPath, "drawing");
                messages.add(new Message(loggedInUser, drawingPath, "drawing"));
                adapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messages.size() - 1);
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Save the photo locally and send the photo message
            String photoPath = savePhoto(photo);
            if (photoPath != null) {
                dbHelper.addMessageToGroupById(groupId, loggedInUser, photoPath, "photo");
                messages.add(new Message(loggedInUser, photoPath, "photo"));
                adapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messages.size() - 1);
            } else {
                Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String savePhoto(Bitmap photo) {
        try {
            File photoFile = new File(getExternalFilesDir(null), "photo_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(photoFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return photoFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
