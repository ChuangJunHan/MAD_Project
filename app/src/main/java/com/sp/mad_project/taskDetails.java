package com.sp.mad_project;

import android.app.DatePickerDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class taskDetails extends AppCompatActivity {
    private EditText taskNameInput, deadlineInput, commentInput, todoInput;
    private Spinner memberDropdown;
    private TextView progressView;
    private SeekBar progressSeekBar;
    private LinearLayout todoContainer, commentContainer;
    private int taskId, groupId;
    private databaseHelper dbHelper;
    private String loggedInUser;

    private List<CheckBox> todoCheckBoxes = new ArrayList<>();
    private List<String> commentList = new ArrayList<>();
    private List<String> projectMembers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskNameInput = findViewById(R.id.taskNameInput);
        deadlineInput = findViewById(R.id.deadlineInput);
        memberDropdown = findViewById(R.id.memberDropdown);
        progressView = findViewById(R.id.progressText);
        progressSeekBar = findViewById(R.id.progressSeekBar);
        todoContainer = findViewById(R.id.todoContainer);
        todoInput = findViewById(R.id.todoInput);
        commentContainer = findViewById(R.id.commentsContainer);
        commentInput = findViewById(R.id.commentInput);

        dbHelper = new databaseHelper(this);
        taskId = getIntent().getIntExtra("taskId", -1);
        groupId = getIntent().getIntExtra("groupId", -1);
        loggedInUser = getIntent().getStringExtra("loggedInUser");

        loadProjectMembers();

        loadTaskDetails();

        deadlineInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                        deadlineInput.setText(selectedDate);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Button addTodoButton = findViewById(R.id.addTodoButton);
        addTodoButton.setOnClickListener(v -> addTodoItem());

        Button addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(v -> addComment());

        Button updateTaskButton = findViewById(R.id.saveTaskButton);
        updateTaskButton.setText("Update Task");
        updateTaskButton.setOnClickListener(v -> updateTask());
    }

    private void loadProjectMembers() {
        projectMembers = dbHelper.getMembersByGroupId(groupId);

        if (projectMembers.isEmpty()) {
            Toast.makeText(this, "No members found for this project.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectMembers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberDropdown.setAdapter(adapter);
    }

    private void loadTaskDetails() {
        Task task = dbHelper.getTaskById(taskId);
        if (task != null) {
            taskNameInput.setText(task.getName());
            deadlineInput.setText(task.getDeadline());
            progressSeekBar.setProgress(task.getProgress());
            progressView.setText("Progress: " + task.getProgress() + "%");

            if (projectMembers.contains(task.getAssignedMember())) {
                memberDropdown.setSelection(projectMembers.indexOf(task.getAssignedMember()));
            }

            List<String> todos = dbHelper.getTodosByTask(taskId);
            for (String todo : todos) {
                CheckBox todoCheckBox = new CheckBox(this);
                todoCheckBox.setText(todo);

                todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateProgressBar());

                todoContainer.addView(todoCheckBox);
                todoCheckBoxes.add(todoCheckBox);
            }

            List<String> comments = dbHelper.getCommentsByTask(taskId);
            for (String comment : comments) {
                TextView commentView = new TextView(this);
                commentView.setText(comment);
                commentContainer.addView(commentView);
                commentList.add(comment);
            }
        } else {
            Toast.makeText(this, "Error loading task details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addTodoItem() {
        String todoText = todoInput.getText().toString().trim();
        if (!todoText.isEmpty()) {
            CheckBox todoCheckBox = new CheckBox(this);
            todoCheckBox.setText(todoText);

            todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateProgressBar());

            todoContainer.addView(todoCheckBox);
            todoCheckBoxes.add(todoCheckBox);
            todoInput.setText("");
            updateProgressBar();
        }
    }

    private void addComment() {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            TextView commentView = new TextView(this);
            commentView.setText(commentText);
            commentContainer.addView(commentView);
            commentList.add(commentText);
            commentInput.setText("");
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

    private void updateTask() {
        String taskName = taskNameInput.getText().toString().trim();
        String deadline = deadlineInput.getText().toString().trim();
        String assignedMember = (String) memberDropdown.getSelectedItem();
        int progress = progressSeekBar.getProgress();

        if (taskName.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = dbHelper.updateTask(taskId, taskName, deadline, assignedMember, progress);
        if (isUpdated) {
            dbHelper.deleteTodosByTask(taskId);
            for (CheckBox checkBox : todoCheckBoxes) {
                String todoText = checkBox.getText().toString();
                boolean completed = checkBox.isChecked();
                dbHelper.addTodoToTask(taskId, todoText, completed);
            }

            dbHelper.deleteCommentsByTask(taskId);
            for (String comment : commentList) {
                dbHelper.addCommentToTask(taskId, comment);
            }

            Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task.", Toast.LENGTH_SHORT).show();
        }
    }
}