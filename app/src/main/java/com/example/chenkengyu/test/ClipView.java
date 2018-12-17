package com.example.chenkengyu.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View {
    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint mPaint = new Paint();

    {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
    }

    Path path = new Path();
    Path sd1 = new Path();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(getWidth(), 0);
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.lineTo(0, 0);
        path.quadTo((float) getWidth() / 2f, (float) getHeight() * 3f / 4f, (float) getWidth(), 0f);
        canvas.drawPath(path, mPaint);


//        mPaint.setColor(Color.GRAY);
//        mPaint.setStrokeWidth(5);
//        mPaint.setStyle(Paint.Style.STROKE);
//        sd1.moveTo(0, 1);
//        sd1.quadTo((float) getWidth() / 2f, (float) getHeight() * 3f / 4f, (float) getWidth(), 1f);
//        canvas.drawPath(sd1, mPaint);
    }
}
