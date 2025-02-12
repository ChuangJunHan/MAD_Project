package com.sp.mad_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class drawingAdapter extends RecyclerView.Adapter<drawingAdapter.ViewHolder> {

    private Context context;
    private List<Drawing> drawings;

    public drawingAdapter(Context context, List<Drawing> drawings) {
        this.context = context;
        this.drawings = drawings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drawing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawing drawing = drawings.get(position);

        // Load drawing as bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(drawing.getPath());
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return drawings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.drawingImageView);
        }
    }
}