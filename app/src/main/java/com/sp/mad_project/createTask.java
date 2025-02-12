package com.sp.mad_project;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.Manifest;



import java.util.ArrayList;

import java.util.List;

public class createTask extends AppCompatActivity {
    private EditText taskNameInput, deadlineInput, commentInput, todoInput;
    private Spinner memberDropdown;
    private TextView progressView;
    private SeekBar progressSeekBar;
    private LinearLayout todoContainer, commentContainer;
    private int groupId;
    private databaseHelper dbHelper;

    private List<CheckBox> todoCheckBoxes = new ArrayList<>();
    private List<String> commentList = new ArrayList<>();
    private List<String> projectMembers = new ArrayList<>(); // Holds project members
    private NotificationCompat.Builder notificationBuilder;
    private ActivityResultLauncher<String> activityResultLauncher;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Initialize inputs
        taskNameInput = findViewById(R.id.taskNameInput);
        deadlineInput = findViewById(R.id.deadlineInput);
        memberDropdown = findViewById(R.id.memberDropdown);
        progressView = findViewById(R.id.progressText);
        progressSeekBar = findViewById(R.id.progressSeekBar);
        todoContainer = findViewById(R.id.todoContainer);
        todoInput = findViewById(R.id.todoInput);
        commentContainer = findViewById(R.id.commentsContainer);
        commentInput = findViewById(R.id.commentInput);

        // Database setup
        dbHelper = new databaseHelper(this);
        groupId = getIntent().getIntExtra("groupId", -1);

        // Load project members and populate dropdown
        loadProjectMembers();

        ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o) {
                    Toast.makeText(createTask.this, "Post notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(createTask.this, "Post notification permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up deadline picker
        deadlineInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                        deadlineInput.setText(selectedDate);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Add To-Do button click listener
        Button addTodoButton = findViewById(R.id.addTodoButton);
        addTodoButton.setOnClickListener(v -> addTodoItem());

        // Add Comment button click listener
        Button addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(v -> addComment());

        // Save Task button click listener
        Button saveTaskButton = findViewById(R.id.saveTaskButton);
        saveTaskButton.setOnClickListener(v -> saveTask());

        Button testNotificationButton = findViewById(R.id.testNotificationButton);
        testNotificationButton.setOnClickListener(v -> {
            sendTestNotification();
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        createNotificationChannel();
    }




    private void loadProjectMembers() {
        // Fetch project members from the database
        projectMembers = dbHelper.getMembersByGroupId(groupId);

        if (projectMembers.isEmpty()) {
            Toast.makeText(this, "No members found for this project.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Populate the Spinner with project members
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectMembers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberDropdown.setAdapter(adapter);
    }

    private void addTodoItem() {
        String todoText = todoInput.getText().toString().trim();
        if (!todoText.isEmpty()) {
            CheckBox todoCheckBox = new CheckBox(this);
            todoCheckBox.setText(todoText);

            // Sync progress bar with checkbox state
            todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateProgressBar());

            todoContainer.addView(todoCheckBox);
            todoCheckBoxes.add(todoCheckBox);
            todoInput.setText(""); // Clear input
            updateProgressBar(); // Update progress when a new To-Do is added
        }
    }

    private void addComment() {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            TextView commentView = new TextView(this);
            commentView.setText(commentText);
            commentContainer.addView(commentView);
            commentList.add(commentText); // Add to list for saving
            commentInput.setText(""); // Clear input
        }
    }

    private void updateProgressBar() {
        int totalTodos = todoCheckBoxes.size();
        int completedTodos = 0;

        for (CheckBox checkBox : todoCheckBoxes) {
            if (checkBox.isChecked()) {
                completedTodos++;
            }
        }

        int progress = (totalTodos == 0) ? 0 : (completedTodos * 100) / totalTodos;
        progressSeekBar.setProgress(progress);
        progressView.setText("Progress: " + progress + "%");
    }

    private void saveTask() {
        String taskName = taskNameInput.getText().toString().trim();
        String deadline = deadlineInput.getText().toString().trim();
        String assignedMember = (String) memberDropdown.getSelectedItem(); // Get selected member
        int progress = progressSeekBar.getProgress(); // Get progress value

        if (taskName.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save task in the database
        long taskId = dbHelper.addTaskToProject(groupId, taskName, deadline, assignedMember, progress);
        if (taskId != -1) {
            // Save todos
            for (CheckBox checkBox : todoCheckBoxes) {
                String todoText = checkBox.getText().toString();
                boolean completed = checkBox.isChecked();
                dbHelper.addTodoToTask(taskId, todoText, completed);
            }

            // Save comments
            for (String comment : commentList) {
                dbHelper.addCommentToTask(taskId, comment);
            }

            Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Notify calling activity of success
            scheduleTaskNotification(taskId, deadline);
            finish(); // Close the activity
        } else {
            Toast.makeText(this, "Failed to create task.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TaskNotifications";
            String description = "Notifications for upcoming tasks";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TASK_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void scheduleTaskNotification(long taskId, String deadline) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(deadline));

            // Schedule both notifications
            scheduleNotificationAt(calendar, taskId, 7); // 7 days before
            scheduleNotificationAt(calendar, taskId, 1); // 1 day before

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotificationAt(Calendar calendar, long taskId, int daysBefore) {
        Calendar notificationTime = (Calendar) calendar.clone();
        notificationTime.add(Calendar.DAY_OF_MONTH, -daysBefore);



        long triggerTime = notificationTime.getTimeInMillis();
        if (triggerTime > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, TaskNotificationReceiver.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("taskName", taskNameInput.getText().toString());
            intent.putExtra("daysBefore", daysBefore); // Pass how many days before the deadline

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) (taskId * 10 + daysBefore), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

            Toast.makeText(this, "Notification scheduled in " + daysBefore + " days!", Toast.LENGTH_LONG).show();
        }
    }

    private void sendTestNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TASK_CHANNEL")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentTitle("Test Notification")
                .setContentText("This is a test notification!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(999, builder.build());
    }

}
