package com.sp.mad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messages;
    private String loggedInUser;

    public messageAdapter(Context context, List<Message> messages, String loggedInUser) {
        this.context = context;
        this.messages = messages;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getType().equals("event")) {
            holder.messageText.setText(message.getContent());
            // No need to set senderName for events
        } else {
            holder.senderName.setText(message.getSender());
            holder.messageText.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, senderName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            senderName = itemView.findViewById(R.id.senderName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getType().equals("event")) {
            return 2; // Event message layout
        } else {
            return message.getSender().equals(loggedInUser) ? 1 : 0; // Regular messages
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 2) { // Event layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_event, parent, false);
        } else if (viewType == 1) { // Outgoing message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_outgoing, parent, false);
        } else { // Incoming message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }

}
