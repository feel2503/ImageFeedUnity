package com.feed.plugin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class FilterValueView extends View{
    private final int paintColor = Color.BLACK;
    private Paint drawPaint;

    public FilterValueView(Context context){
        super(context);
        setupPaint();
    }

    public FilterValueView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        setupPaint();
    }

    public FilterValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setupPaint();
    }

    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas){
        //super.onDraw(canvas);
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        int gap = width/4;
        int startX = width - gap;
        int startY = height - height/2;

        //canvas.drawLine(startX, startY, startX+(gap*2), startY, drawPaint);
        canvas.drawLine(0, startY, width, startY, drawPaint);
    }


}
