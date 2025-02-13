package com.sp.mad_project;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ganttChart extends AppCompatActivity {

    private TableLayout ganttTable;
    private Button btnAddWeek, btnAddTask, btnSaveGantt, btnDeleteTask, btnDeleteWeek;
    private int weekCount = 0; // Number of weeks
    private int taskCount = 0; // Number of tasks
    private int groupId; // Group ID for this Gantt chart
    private databaseHelper dbHelper; // Use your databaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gantt_chart);

        ganttTable = findViewById(R.id.ganttTable);
        btnAddWeek = findViewById(R.id.btnAddWeek);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnSaveGantt = findViewById(R.id.btnSaveGantt);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);
        btnDeleteWeek = findViewById(R.id.btnDeleteWeek);

        // Initialize database helper
        dbHelper = new databaseHelper(this);

        // Get the group ID passed from the previous activity
        groupId = getIntent().getIntExtra("groupId", -1);

        if (groupId == -1) {
            Toast.makeText(this, "Invalid group ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing Gantt chart for this group
        loadGanttChart();

        // Add listeners for buttons
        btnAddWeek.setOnClickListener(v -> addWeek());
        btnAddTask.setOnClickListener(v -> addTaskRow("Task " + (taskCount + 1)));
        btnSaveGantt.setOnClickListener(v -> saveGanttChart());
        btnDeleteTask.setOnClickListener(v -> deleteSelectedRow());
        btnDeleteWeek.setOnClickListener(v -> deleteLastColumn());
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);

        // "Task" column
        TextView taskHeader = new TextView(this);
        taskHeader.setText("Task");
        taskHeader.setPadding(16, 16, 16, 16);
        taskHeader.setBackgroundResource(R.drawable.cell_border);
        headerRow.addView(taskHeader);

        // Add existing week columns to header
        for (int i = 1; i <= weekCount; i++) {
            TextView weekHeader = new TextView(this);
            weekHeader.setText("Week " + i);
            weekHeader.setPadding(16, 16, 16, 16);
            weekHeader.setBackgroundResource(R.drawable.cell_border);
            headerRow.addView(weekHeader);
        }

        ganttTable.addView(headerRow);
    }

    private void addWeek() {
        weekCount++;

        // Update header row with a new week column
        TableRow headerRow = (TableRow) ganttTable.getChildAt(0);
        TextView weekHeader = new TextView(this);
        weekHeader.setText("Week " + weekCount);
        weekHeader.setPadding(16, 16, 16, 16);
        weekHeader.setBackgroundResource(R.drawable.cell_border);
        headerRow.addView(weekHeader);

        // Add a blank cell to all task rows
        for (int i = 1; i < ganttTable.getChildCount(); i++) {
            TableRow taskRow = (TableRow) ganttTable.getChildAt(i);
            taskRow.addView(createClickableCell());
        }
    }

    private void addTaskRow(String taskName) {
        taskCount++;

        TableRow taskRow = new TableRow(this);

        // Task Name
        TextView taskCell = new TextView(this);
        taskCell.setText(taskName);
        taskCell.setPadding(16, 16, 16, 16);
        taskCell.setBackgroundResource(R.drawable.cell_border);
        taskRow.addView(taskCell);

        // Add blank cells for the existing weeks
        for (int i = 0; i < weekCount; i++) {
            taskRow.addView(createClickableCell());
        }

        ganttTable.addView(taskRow);
    }

    private TextView createClickableCell() {
        TextView blankCell = new TextView(this);
        blankCell.setPadding(16, 16, 16, 16);
        blankCell.setBackgroundResource(R.drawable.cell_border);

        // Add click listener to toggle selection
        blankCell.setOnClickListener(v -> {
            if (blankCell.isSelected()) {
                blankCell.setSelected(false);
                blankCell.setBackgroundResource(R.drawable.cell_border); // Reset to default
            } else {
                blankCell.setSelected(true);
                blankCell.setBackgroundColor(Color.GREEN); // Highlight
            }
        });

        return blankCell;
    }

    private void deleteSelectedRow() {
        if (ganttTable.getChildCount() > 1) { // Ensure at least the header exists
            ganttTable.removeViewAt(ganttTable.getChildCount() - 1);
            taskCount--;
            Toast.makeText(this, "Last row deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No task rows to delete.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLastColumn() {
        if (weekCount == 0) {
            Toast.makeText(this, "No columns to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove the last column from the header row
        TableRow headerRow = (TableRow) ganttTable.getChildAt(0);
        headerRow.removeViewAt(headerRow.getChildCount() - 1);

        // Remove the last column from all task rows
        for (int i = 1; i < ganttTable.getChildCount(); i++) {
            TableRow taskRow = (TableRow) ganttTable.getChildAt(i);
            taskRow.removeViewAt(taskRow.getChildCount() - 1);
        }

        weekCount--;
        Toast.makeText(this, "Last column deleted.", Toast.LENGTH_SHORT).show();
    }

    private void saveGanttChart() {
        String chartData = serializeGanttChart();
        Log.d("SerializedData", "Saving chart: " + chartData); // Debug log

        boolean success = dbHelper.saveGanttChartData(groupId, chartData);

        if (success) {
            Toast.makeText(this, "Gantt chart saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save Gantt chart.", Toast.LENGTH_SHORT).show();
        }
    }

    private String serializeGanttChart() {
        JSONArray chartArray = new JSONArray();

        for (int i = 1; i < ganttTable.getChildCount(); i++) { // Skip header row
            TableRow row = (TableRow) ganttTable.getChildAt(i);
            JSONObject rowData = new JSONObject();

            try {
                TextView taskCell = (TextView) row.getChildAt(0);
                rowData.put("task", taskCell.getText().toString()); // Save task name

                JSONArray weeks = new JSONArray();
                for (int j = 1; j < row.getChildCount(); j++) { // Start from 1 to skip task name column
                    TextView weekCell = (TextView) row.getChildAt(j);
                    weeks.put(weekCell.isSelected()); // Save selection state
                }
                rowData.put("weeks", weeks);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            chartArray.put(rowData);
        }

        return chartArray.toString();
    }

    private void loadGanttChart() {
        String savedChartData = dbHelper.getGanttChartData(groupId);

        if (savedChartData != null) {
            deserializeGanttChart(savedChartData);
        } else {
            addHeaderRow(); // Add a default header row if no data is saved
        }
    }

    private void deserializeGanttChart(String chartData) {
        try {
            Log.d("DeserializedData", "Loading chart: " + chartData); // Debug log

            JSONArray chartArray = new JSONArray(chartData);

            // Add header row
            if (chartArray.length() > 0) {
                JSONObject firstRow = chartArray.getJSONObject(0);
                JSONArray weeks = firstRow.getJSONArray("weeks");
                weekCount = weeks.length(); // Restore week count
                addHeaderRow();
            }

            // Add each task row
            for (int i = 0; i < chartArray.length(); i++) {
                JSONObject rowData = chartArray.getJSONObject(i);
                String taskName = rowData.getString("task");
                JSONArray weeks = rowData.getJSONArray("weeks");

                TableRow taskRow = new TableRow(this);

                // Add task name
                TextView taskCell = new TextView(this);
                taskCell.setText(taskName);
                taskCell.setPadding(16, 16, 16, 16);
                taskCell.setBackgroundResource(R.drawable.cell_border);
                taskRow.addView(taskCell);

                // Add week cells
                for (int j = 0; j < weeks.length(); j++) {
                    TextView weekCell = createClickableCell();
                    if (weeks.getBoolean(j)) { // Restore selection state
                        weekCell.setSelected(true);
                        weekCell.setBackgroundColor(Color.GREEN); // Restore highlight
                    }
                    taskRow.addView(weekCell);
                }

                ganttTable.addView(taskRow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
