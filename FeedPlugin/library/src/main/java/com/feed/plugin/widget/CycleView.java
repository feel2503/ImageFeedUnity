package com.feed.plugin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.feed.plugin.widget.cookicrop.Circle;

public class CycleView extends View{
    private Paint mTransparentPaint;
    private Paint mSemiBlackPaint;
    private Path mPath = new Path();

    private int pathColor = 0xA6000000;

    public CycleView(Context context){
        super(context);
        init();
    }

    public CycleView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public CycleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPathColor(int color)
    {
        pathColor = color;
    }

    private void init()
    {
//        mPaint = new Paint();
//        mPaint.setColor(pathColor);
//
//        mStrokePaint = new Paint();
//        mStrokePaint.setColor(Color.YELLOW);
//        mStrokePaint.setStrokeWidth(2);
//        mStrokePaint.setStyle(Paint.Style.STROKE);


        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(Color.TRANSPARENT);
        mTransparentPaint.setStrokeWidth(10);

        mSemiBlackPaint = new Paint();
        mSemiBlackPaint.setColor(Color.TRANSPARENT);
        mSemiBlackPaint.setStrokeWidth(10);

    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //Circle circle = new Circle(canvas.getWidth() / 2, canvas.getHeight() / 2, circleRadius);
        //Circle circle = new Circle(canvas.getWidth(), canvas.getHeight(), circleRadius);

        //canvas.drawCircle(circle.getCx(), circle.getCy(), circle.getRadius(), mPaint);
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
//        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2, mPaint);


        mPath.reset();

        float radius = 0;
        if (canvas.getWidth() < canvas.getHeight()) {
            radius = canvas.getWidth() / 2;
        } else {
            radius = canvas.getHeight() / 2;
        }

        mPath.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, mTransparentPaint);

        canvas.drawPath(mPath, mSemiBlackPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(pathColor);




//       // mPaint.setStrokeWidth(strokeWidth);
//
//        mPath.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, Path.Direction.CW);
//        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
//
//        //canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, mStrokePaint);
//
//        canvas.drawPath(mPath, mPaint);


    }
}
