package com.sp.mad_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class


drawingView extends View {

    private Paint paint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private ArrayList<Path> paths = new ArrayList<>();

    public drawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                paths.add(new Path(path));
                path.reset();
                invalidate();
                return true;
            default:
                return false;
        }
    }

    public void clearCanvas() {
        paths.clear();
        path.reset();
        invalidate();
    }

    public Bitmap getBitmap() {
        Bitmap resultBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(resultBitmap);
        draw(tempCanvas);
        return resultBitmap;
    }

    public void loadBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            // Create a new canvas using the bitmap
            this.bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(this.bitmap);
            this.canvas.drawBitmap(bitmap, 0, 0, null);
            invalidate(); // Redraw the view
        } else {
            throw new IllegalArgumentException("Bitmap is null. Cannot load drawing.");
        }
    }

}
