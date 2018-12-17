package com.example.chenkengyu.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    OnViewPagerTouchListenr onViewPagerTouchListenr;

    interface OnViewPagerTouchListenr {
        void onTouch(MotionEvent ev);
    }

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onViewPagerTouchListenr != null) {
            onViewPagerTouchListenr.onTouch(ev);
        }
        return super.onTouchEvent(ev);
    }

    public void setOnViewPagerTouchListenr(OnViewPagerTouchListenr listenr) {
        onViewPagerTouchListenr = listenr;
    }
}
