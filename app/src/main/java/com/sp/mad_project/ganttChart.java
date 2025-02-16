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
    private int weekCount = 0;
    private int taskCount = 0;
    private int groupId;
    private databaseHelper dbHelper;

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

        dbHelper = new databaseHelper(this);

        groupId = getIntent().getIntExtra("groupId", -1);

        if (groupId == -1) {
            Toast.makeText(this, "Invalid group ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadGanttChart();

        btnAddWeek.setOnClickListener(v -> addWeek());
        btnAddTask.setOnClickListener(v -> addTaskRow("Task " + (taskCount + 1)));
        btnSaveGantt.setOnClickListener(v -> saveGanttChart());
        btnDeleteTask.setOnClickListener(v -> deleteSelectedRow());
        btnDeleteWeek.setOnClickListener(v -> deleteLastColumn());
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);

        TextView taskHeader = new TextView(this);
        taskHeader.setText("Task");
        taskHeader.setPadding(16, 16, 16, 16);
        taskHeader.setBackgroundResource(R.drawable.cell_border);
        headerRow.addView(taskHeader);

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

        TextView taskCell = new TextView(this);
        taskCell.setText(taskName);
        taskCell.setPadding(16, 16, 16, 16);
        taskCell.setBackgroundResource(R.drawable.cell_border);
        taskRow.addView(taskCell);

        for (int i = 0; i < weekCount; i++) {
            taskRow.addView(createClickableCell());
        }

        ganttTable.addView(taskRow);
    }

    private TextView createClickableCell() {
        TextView blankCell = new TextView(this);
        blankCell.setPadding(16, 16, 16, 16);
        blankCell.setBackgroundResource(R.drawable.cell_border);

        blankCell.setOnClickListener(v -> {
            if (blankCell.isSelected()) {
                blankCell.setSelected(false);
                blankCell.setBackgroundResource(R.drawable.cell_border);
            } else {
                blankCell.setSelected(true);
                blankCell.setBackgroundColor(Color.GREEN);
            }
        });

        return blankCell;
    }

    private void deleteSelectedRow() {
        if (ganttTable.getChildCount() > 1) {
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

        TableRow headerRow = (TableRow) ganttTable.getChildAt(0);
        headerRow.removeViewAt(headerRow.getChildCount() - 1);

        for (int i = 1; i < ganttTable.getChildCount(); i++) {
            TableRow taskRow = (TableRow) ganttTable.getChildAt(i);
            taskRow.removeViewAt(taskRow.getChildCount() - 1);
        }

        weekCount--;
        Toast.makeText(this, "Last column deleted.", Toast.LENGTH_SHORT).show();
    }

    private void saveGanttChart() {
        String chartData = serializeGanttChart();
        Log.d("SerializedData", "Saving chart: " + chartData);

        boolean success = dbHelper.saveGanttChartData(groupId, chartData);

        if (success) {
            Toast.makeText(this, "Gantt chart saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save Gantt chart.", Toast.LENGTH_SHORT).show();
        }
    }

    private String serializeGanttChart() {
        JSONArray chartArray = new JSONArray();

        for (int i = 1; i < ganttTable.getChildCount(); i++) {
            TableRow row = (TableRow) ganttTable.getChildAt(i);
            JSONObject rowData = new JSONObject();

            try {
                TextView taskCell = (TextView) row.getChildAt(0);
                rowData.put("task", taskCell.getText().toString());

                JSONArray weeks = new JSONArray();
                for (int j = 1; j < row.getChildCount(); j++) {
                    TextView weekCell = (TextView) row.getChildAt(j);
                    weeks.put(weekCell.isSelected());
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
            addHeaderRow();
        }
    }

    private void deserializeGanttChart(String chartData) {
        try {
            Log.d("DeserializedData", "Loading chart: " + chartData);

            JSONArray chartArray = new JSONArray(chartData);

            if (chartArray.length() > 0) {
                JSONObject firstRow = chartArray.getJSONObject(0);
                JSONArray weeks = firstRow.getJSONArray("weeks");
                weekCount = weeks.length();
                addHeaderRow();
            }

            for (int i = 0; i < chartArray.length(); i++) {
                JSONObject rowData = chartArray.getJSONObject(i);
                String taskName = rowData.getString("task");
                JSONArray weeks = rowData.getJSONArray("weeks");

                TableRow taskRow = new TableRow(this);

                TextView taskCell = new TextView(this);
                taskCell.setText(taskName);
                taskCell.setPadding(16, 16, 16, 16);
                taskCell.setBackgroundResource(R.drawable.cell_border);
                taskRow.addView(taskCell);

                for (int j = 0; j < weeks.length(); j++) {
                    TextView weekCell = createClickableCell();
                    if (weeks.getBoolean(j)) {
                        weekCell.setSelected(true);
                        weekCell.setBackgroundColor(Color.GREEN);
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
