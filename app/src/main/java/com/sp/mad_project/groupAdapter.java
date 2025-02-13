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

    public groupAdapter(Context context, List<Group> groupList, boolean isChatGroup) {
        this.context = context;
        this.groupList = groupList;
        this.isChatGroup = isChatGroup;
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
        String infoText = isChatGroup ?
                "Number of Events: " + group.getEventCount() :
                "Number of Tasks: " + group.getTaskCount();
        holder.groupInfo.setText(infoText);

        holder.viewDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, isChatGroup ? chatDetails.class : taskView.class);
            intent.putExtra("groupId", group.getId());
            context.startActivity(intent);
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
