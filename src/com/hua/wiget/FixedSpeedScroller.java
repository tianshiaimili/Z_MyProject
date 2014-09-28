package com.hua.wiget;

/**
 * 这是一个改变viewpager 滑动速度的类
 * 
 * 用法如下
 * 	try {  
            Field mField = ViewPager.class.getDeclaredField("mScroller");  
            mField.setAccessible(true);  
            mScroller = new FixedSpeedScroller(advPager.getContext(),  
                    new OvershootInterpolator());  
            //可以用setDuration的方式调整速率  
            mScroller.setmDuration(1000);  
            mField.set(advPager, mScroller);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
 * 
 * 
 */
import android.content.Context;  
import android.view.animation.Interpolator;  
import android.widget.Scroller;  
  
public class FixedSpeedScroller extends Scroller {  
  
    private int mDuration = 5000;  
  
    public FixedSpeedScroller(Context context) {  
        super(context);  
    }  
  
    public FixedSpeedScroller(Context context, Interpolator interpolator) {  
        super(context, interpolator);  
    }  
  
    @Override  
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {  
        // Ignore received duration, use fixed one instead  
        super.startScroll(startX, startY, dx, dy, mDuration);  
    }  
  
    @Override  
    public void startScroll(int startX, int startY, int dx, int dy) {  
        // Ignore received duration, use fixed one instead  
        super.startScroll(startX, startY, dx, dy, mDuration);  
    }  
  
    public void setmDuration(int time) {  
        mDuration = time;  
    }  
  
    public int getmDuration() {  
        return mDuration;  
    }  
}
