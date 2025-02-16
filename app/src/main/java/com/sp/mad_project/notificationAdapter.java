package com.sp.mad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Object> notifications; // This list will contain both Task and Message objects

    public notificationAdapter(Context context, List<Object> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Object notification = notifications.get(position);

        if (notification instanceof Task) {
            Task task = (Task) notification;
            holder.notificationTitle.setText("Task: " + task.getName());
            holder.notificationDate.setText("Deadline: " + task.getDeadline());
        } else if (notification instanceof Message) {
            Message message = (Message) notification;
            holder.notificationTitle.setText("Event: " + message.getContent());
            holder.notificationDate.setText("Date: N/A"); // Update this if Message has a date field
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, notificationDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationDate = itemView.findViewById(R.id.notificationDate);
        }
    }
}
