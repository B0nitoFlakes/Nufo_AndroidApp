package com.example.nufo.Activities.FoodRecognition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.nufo.R;

import java.util.List;
import java.util.LinkedList;

public class OverlayView extends View {

    private List<BoundingBox> results;
    private Paint boxPaint;
    private Paint textBackgroundPaint;
    private Paint textPaint;
    private Rect bounds;

    private static final int BOUNDING_RECT_TEXT_PADDING = 8;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        results = new LinkedList<>();
        initPaints();
    }

    public void clear() {
        textPaint.reset();
        textBackgroundPaint.reset();
        boxPaint.reset();
        invalidate();
        initPaints();
    }

    private void initPaints() {
        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(Color.BLACK);
        textBackgroundPaint.setStyle(Paint.Style.FILL);
        textBackgroundPaint.setTextSize(50f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50f);

        boxPaint = new Paint();
        boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.bounding_box_color));
        boxPaint.setStrokeWidth(8F);
        boxPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (BoundingBox box : results) {
            float left = box.getX1() * getWidth();
            float top = box.getY1() * getHeight();
            float right = box.getX2() * getWidth();
            float bottom = box.getY2() * getHeight();

            canvas.drawRect(left, top, right, bottom, boxPaint);
            String drawableText = box.getClsName();

            bounds = new Rect();
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length(), bounds);
            int textWidth = bounds.width();
            int textHeight = bounds.height();
            canvas.drawRect(left, top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint);
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint);
        }
    }

    public void setResults(List<BoundingBox> boundingBoxes) {
        results = boundingBoxes;
        invalidate();
    }

}
