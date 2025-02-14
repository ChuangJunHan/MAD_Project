package com.sp.mad_project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private String loggedInUser;
    private databaseHelper dbHelper;

    public taskAdapter(List<Task> taskList, String loggedInUser, databaseHelper dbHelper) {
        this.taskList = taskList;
        this.loggedInUser = loggedInUser; // Initialize
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set task details
        holder.taskName.setText(task.getName());
        holder.taskDeadline.setText("Deadline: " + task.getDeadline());
        holder.taskProgress.setText("Progress: " + task.getProgress() + "%");
        holder.taskMember.setText("Assigned to: " + task.getAssignedMember());

        // Check if the current user is the assigned member or the group creator
        boolean isGroupCreator = dbHelper.isGroupCreator(task.getGroupId(), loggedInUser);
        boolean isAssignedUser = loggedInUser.equals(task.getAssignedMember());

        // Enable or disable the button based on the condition
        if (isGroupCreator || isAssignedUser) {
            holder.viewDetailsButton.setEnabled(true);
            holder.viewDetailsButton.setAlpha(1.0f); // Fully visible when enabled
        } else {
            holder.viewDetailsButton.setEnabled(false);
            holder.viewDetailsButton.setAlpha(0.5f); // Dimmed to indicate it's disabled
        }

        // View Details Button Click Listener
        holder.viewDetailsButton.setOnClickListener(v -> {
            if (isGroupCreator || isAssignedUser) { // Only proceed if the button is enabled
                Intent intent = new Intent(v.getContext(), taskDetails.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("groupId", task.getGroupId());
                intent.putExtra("loggedInUser", loggedInUser);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDeadline, taskProgress, taskMember;
        Button viewDetailsButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDeadline = itemView.findViewById(R.id.taskDeadline);
            taskProgress = itemView.findViewById(R.id.taskProgress);
            taskMember = itemView.findViewById(R.id.taskMember);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }
    }
}
