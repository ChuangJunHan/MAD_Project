package com.sp.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class groupAdapter extends RecyclerView.Adapter<groupAdapter.GroupViewHolder> {
    private Context context;
    private List<Group> groupList;
    private boolean isChatGroup; // Flag to differentiate between chat and task groups
    private String loggedInUser, contextType;

    public groupAdapter(Context context, List<Group> groupList, boolean isChatGroup, String loggedInUser, String contextType) {
        this.context = context;
        this.groupList = groupList;
        this.loggedInUser = loggedInUser;
        this.contextType = contextType;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        holder.groupName.setText(group.getName());

        // Update infoText dynamically for ganttGroups
        if ("ganttGroups".equals(contextType)) {
            if (group.getTaskCount() > 0) { // Assuming taskCount represents ganttCount for ganttGroups
                holder.groupInfo.setText("Gantt Chart Present: Yes");
                holder.viewDetailsButton.setEnabled(true);
                holder.viewDetailsButton.setAlpha(1.0f); // Fully visible when enabled
            } else {
                holder.groupInfo.setText("Gantt Chart Present: No");
                holder.viewDetailsButton.setEnabled(false);
                holder.viewDetailsButton.setAlpha(0.5f); // Dim button to indicate it's disabled
            }
        } else {
            // For chatGroups or taskGroups
            String infoText = isChatGroup ?
                    "Number of Events: " + group.getEventCount() :
                    "Number of Tasks: " + group.getTaskCount();
            holder.groupInfo.setText(infoText);
        }

        // Set button text based on context type
        if ("chatGroups".equals(contextType)) {
            holder.viewDetailsButton.setText("View Messages");
        } else if ("taskGroups".equals(contextType)) {
            holder.viewDetailsButton.setText("View Tasks");
        } else if ("ganttGroups".equals(contextType)) {
            holder.viewDetailsButton.setText("View Chart");
        }

        // View Details Button Click Listener
        holder.viewDetailsButton.setOnClickListener(v -> {
            if ("chatGroups".equals(contextType)) {
                Intent intent = new Intent(v.getContext(), chatMessages.class);
                intent.putExtra("groupId", group.getId());
                intent.putExtra("loggedInUser", loggedInUser);
                v.getContext().startActivity(intent);
            } else if ("taskGroups".equals(contextType)) {
                Intent intent = new Intent(v.getContext(), taskView.class);
                intent.putExtra("groupId", group.getId());
                intent.putExtra("loggedInUser", loggedInUser);
                v.getContext().startActivity(intent);
            } else if ("ganttGroups".equals(contextType)) {
                if (group.getTaskCount() > 0) { // Only start activity if a Gantt chart exists
                    Intent intent = new Intent(v.getContext(), ganttChart.class);
                    intent.putExtra("groupId", group.getId());
                    intent.putExtra("loggedInUser", loggedInUser);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupInfo;
        Button viewDetailsButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            groupInfo = itemView.findViewById(R.id.groupInfo);
            viewDetailsButton = itemView.findViewById(R.id.viewGroupDetailsButton);
        }
    }
}
