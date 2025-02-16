package com.sp.mad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class membersAdapter extends RecyclerView.Adapter<membersAdapter.ViewHolder> {

    private final Context context;
    private final List<String> availableUsers;
    private final List<String> selectedMembers;

    public membersAdapter(Context context, List<String> availableUsers, List<String> selectedMembers) {
        this.context = context;
        this.availableUsers = availableUsers;
        this.selectedMembers = selectedMembers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String user = availableUsers.get(position);
        holder.memberName.setText(user);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedMembers.contains(user));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedMembers.add(user);
            } else {
                selectedMembers.remove(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return availableUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            checkBox = itemView.findViewById(R.id.memberCheckBox);
        }
    }
}
