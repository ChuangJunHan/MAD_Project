package com.sp.mad_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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

        if (holder.messageText != null) holder.messageText.setVisibility(View.GONE);
        if (holder.senderName != null) holder.senderName.setVisibility(View.GONE);
        if (holder.photoView != null) holder.photoView.setVisibility(View.GONE);
        if (holder.messageDate != null) holder.messageDate.setVisibility(View.GONE);

        if (message == null) {
            Log.w("MessageAdapter", "Null message object at position: " + position);
            return;
        }

        String type = message.getType() != null ? message.getType() : "unknown";
        String sender = message.getSender() != null ? message.getSender() : "Unknown Sender";
        String content = message.getContent() != null ? message.getContent() : "No Content";
        String date = message.getDate() != null ? message.getDate() : "No Date";

        Log.d("MessageAdapter", "Binding message: Type = " + type + ", Sender = " + sender);

        if (type.equals("photo")) {
            if (holder.photoView != null) {
                holder.photoView.setVisibility(View.VISIBLE);
                Bitmap photo = BitmapFactory.decodeFile(content);
                if (photo != null) {
                    holder.photoView.setImageBitmap(photo);
                }
            }
        } else if (type.equals("event")) {
            if (holder.messageText != null) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(content);
            }
            if (holder.messageDate != null) {
                holder.messageDate.setVisibility(View.VISIBLE);
                holder.messageDate.setText(date);
            }
        } else {
            if (holder.senderName != null) {
                holder.senderName.setVisibility(View.VISIBLE);
                holder.senderName.setText(sender);
            }
            if (holder.messageText != null) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(content);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, senderName, messageDate;
        ImageView photoView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageContent);
            messageDate = itemView.findViewById(R.id.messageDate);
            senderName = itemView.findViewById(R.id.senderName);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages == null || messages.isEmpty()) {
            Log.w("MessageAdapter", "Empty or null message list.");
            return -1;
        }

        Message message = messages.get(position);
        if (message == null) {
            return -1;
        }

        String type = message.getType();
        if ("event".equals(type)) {
            return 2;
        } else if ("photo".equals(type)) {
            return 3;
        } else {
            return loggedInUser.equals(message.getSender()) ? 1 : 0;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 2) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_event, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_outgoing, parent, false);
        } else if (viewType == 3) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_photo, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }
}