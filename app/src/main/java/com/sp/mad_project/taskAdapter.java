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

    public taskAdapter(List<Task> taskList) {
        this.taskList = taskList;
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

        // View Details Button
        holder.viewDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), taskDetails.class);
            intent.putExtra("taskId", task.getId()); // Pass the task ID to the details activity
            intent.putExtra("groupId", task.getGroupId());
            v.getContext().startActivity(intent);
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
