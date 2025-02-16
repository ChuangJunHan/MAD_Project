package com.sp.mad_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // Clear all views by default
        if (holder.messageText != null) holder.messageText.setVisibility(View.GONE);
        if (holder.senderName != null) holder.senderName.setVisibility(View.GONE);
        if (holder.photoView != null) holder.photoView.setVisibility(View.GONE);

        if (message.getType().equals("photo")) {
            if (holder.photoView != null) {
                holder.photoView.setVisibility(View.VISIBLE);
                Bitmap photo = BitmapFactory.decodeFile(message.getContent());
                if (photo != null) {
                    holder.photoView.setImageBitmap(photo);
                }
            }
        } else if (message.getType().equals("event")) {
            if (holder.messageText != null) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(message.getContent());
            }
        } else { // Regular text message
            if (holder.senderName != null) {
                holder.senderName.setVisibility(View.VISIBLE);
                holder.senderName.setText(message.getSender());
            }
            if (holder.messageText != null) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(message.getContent());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, senderName;
        ImageView photoView; // Add ImageView for photo messages

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            senderName = itemView.findViewById(R.id.senderName);
            photoView = itemView.findViewById(R.id.photoView); // Bind ImageView
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getType().equals("event")) {
            return 2; // Event message layout
        } else if (message.getType().equals("photo")) {
            return 3; // Photo message layout
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
        } else if (viewType == 3) { // Photo message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_photo, parent, false);
        } else { // Incoming message layout
            view = LayoutInflater.from(context).inflate(R.layout.item_message_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }
}
