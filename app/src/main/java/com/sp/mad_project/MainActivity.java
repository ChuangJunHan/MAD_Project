package com.sp.mad_project;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TableLayout ganttTable;
    private int weekCount = 0; // Number of weeks
    private int taskCount = 0; // Number of tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ganttTable = findViewById(R.id.ganttTable);
        Button btnAddWeek = findViewById(R.id.btnAddWeek);
        Button btnAddTask = findViewById(R.id.btnAddTask);


        // Add header row initially
        addHeaderRow();

        // Add a sample task row
        addTaskRow("Task 1");

        // Button to add weeks dynamically
        btnAddWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeek();
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskRow("Task " + (taskCount + 1)); // Add a new task with an incremented task number
            }
        });
    }

    // Adds the header row (Task | Week 1 | Week 2 | ...)
    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);

        // "Task" column
        TextView taskHeader = new TextView(this);
        taskHeader.setText("Task");
        taskHeader.setPadding(16, 16, 16, 16);
        headerRow.addView(taskHeader);

        ganttTable.addView(headerRow);
    }

    // Adds a new column for a week
    private void addWeek() {
        weekCount++;

        // Update header row with a new week column
        TableRow headerRow = (TableRow) ganttTable.getChildAt(0);
        TextView weekHeader = new TextView(this);
        weekHeader.setText("Week " + weekCount);
        weekHeader.setPadding(16, 16, 16, 16);
        headerRow.addView(weekHeader);

        // Add a blank cell to all task rows
        for (int i = 1; i < ganttTable.getChildCount(); i++) {
            TableRow taskRow = (TableRow) ganttTable.getChildAt(i);
            taskRow.addView(createClickableCell());
        }
    }

    // Adds a new task row
    private void addTaskRow(String taskName) {
        taskCount++;

        TableRow taskRow = new TableRow(this);

        // Task Name
        TextView taskCell = new TextView(this);
        taskCell.setText(taskName);
        taskCell.setPadding(16, 16, 16, 16);
        taskRow.addView(taskCell);

        // Add blank cells for the existing weeks
        for (int i = 0; i < weekCount; i++) {
            taskRow.addView(createClickableCell());
        }

        ganttTable.addView(taskRow);
    }

    // Creates a blank clickable cell
    private TextView createClickableCell() {
        TextView blankCell = new TextView(this);


        blankCell.setPadding(16, 16, 16, 16);
        blankCell.setBackgroundResource(android.R.drawable.editbox_background);

        // Add click listener to change color on click
        blankCell.setOnClickListener(new View.OnClickListener() {
            private boolean isColored = false; // Track cell state

            @Override
            public void onClick(View v) {
                if (isColored) {
                    // Reset to default color
                    blankCell.setBackgroundColor(Color.TRANSPARENT);
                    blankCell.setBackgroundResource(R.drawable.cell_border); // Retain outline

                } else {
                    // Change to colored state
                    blankCell.setBackgroundColor(Color.GREEN);
                }
                isColored = !isColored;
            }
        });

        return blankCell;
    }
}
