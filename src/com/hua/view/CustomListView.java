package com.hua.view;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hua.activity.R;
  
public class CustomListView extends ListView implements OnScrollListener {  
  
    // 松开刷新标志   
    private final static int RELEASE_To_REFRESH = 0;  
    // 下拉刷新标志   
    private final static int PULL_To_REFRESH = 1;  
    // 正在刷新标志   
    private final static int REFRESHING = 2;  
    // 刷新完成标志   
    private final static int DONE = 3;  
  
    private LayoutInflater inflater;  
  
    private LinearLayout headView;  
    private TextView tipsTextview;  
    private TextView lastUpdatedTextView;  
    private ImageView arrowImageView;  
    private ProgressBar progressBar;  
    // 用来设置箭头图标动画效果   
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;  
  
    // 用于保证startY的值在一个完整的touch事件中只被记录一次   
    private boolean isRecored;  
  
    private int headContentWidth;  
    private int headContentHeight;  
  
    private int startY;  
    private int firstItemIndex;  
  
    private int state;  
  
    private boolean isBack;  
  
    public OnRefreshListener refreshListener;  
  
    private final static String TAG = "Monitor Current Infomation";  
  
    public CustomListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
  
    private void init(Context context) {  
  
        inflater = LayoutInflater.from(context);  
        headView = (LinearLayout) inflater.inflate(R.layout.myscrollview_head, null);  
  
        arrowImageView = (ImageView) headView  
                .findViewById(R.id.head_arrowImageView);  
        arrowImageView.setMinimumWidth(50);  
        arrowImageView.setMinimumHeight(50);  
        progressBar = (ProgressBar) headView  
                .findViewById(R.id.head_progressBar);  
        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);  
        lastUpdatedTextView = (TextView) headView  
                .findViewById(R.id.head_lastUpdatedTextView);  
  
        measureView(headView);  
  
        headContentHeight = headView.getMeasuredHeight();  
        headContentWidth = headView.getMeasuredWidth();  
  
        headView.setPadding(0, -1 * headContentHeight, 0, 0);  
        headView.invalidate();  
  
        Log.v("size", "width:" + headContentWidth + " height:"  
                + headContentHeight);  
  
        addHeaderView(headView);  
        setOnScrollListener(this);  
  
        animation = new RotateAnimation(0, -180,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        animation.setInterpolator(new LinearInterpolator());  
        animation.setDuration(500);  
        animation.setFillAfter(true);  
  
        reverseAnimation = new RotateAnimation(-180, 0,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        reverseAnimation.setInterpolator(new LinearInterpolator());  
        reverseAnimation.setDuration(500);  
        reverseAnimation.setFillAfter(true);  
    }  
  
    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,  
            int arg3) {  
        firstItemIndex = firstVisiableItem;  
    }  
  
    public void onScrollStateChanged(AbsListView arg0, int arg1) {  
    }  
  
    public boolean onTouchEvent(MotionEvent event) {  
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            if (firstItemIndex == 0 && !isRecored) {  
                startY = (int) event.getY();  
                isRecored = true;  
  
                Log.v(TAG, "记录按下时的位置");  
            }  
            break;  
  
        case MotionEvent.ACTION_UP:  
  
            if (state != REFRESHING) {  
                if (state == DONE) {  
                    Log.v(TAG, "什么都不做");  
                }  
                if (state == PULL_To_REFRESH) {  
                    state = DONE;  
                    changeHeaderViewByState();  
  
                    Log.v(TAG, "由下拉刷新状态到刷新完成状态");  
                }  
                if (state == RELEASE_To_REFRESH) {  
                    state = REFRESHING;  
                    changeHeaderViewByState();  
                    onRefresh();  
  
                    Log.v(TAG, "由松开刷新状态，到刷新完成状态");  
                }  
            }  
  
            isRecored = false;  
            isBack = false;  
  
            break;  
  
        case MotionEvent.ACTION_MOVE:  
            int tempY = (int) event.getY();  
            if (!isRecored && firstItemIndex == 0) {  
                Log.v(TAG, "记录拖拽时的位置");  
                isRecored = true;  
                startY = tempY;  
            }  
            if (state != REFRESHING && isRecored) {  
                // 可以松开刷新了   
                if (state == RELEASE_To_REFRESH) {  
                    // 往上推，推到屏幕足够掩盖head的程度，但还没有全部掩盖   
                    if ((tempY - startY < headContentHeight)  
                            && (tempY - startY) > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");  
                    }  
                    // 一下子推到顶   
                    else if (tempY - startY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "由松开刷新状态转变到done状态");  
                    }  
                    // 往下拉，或者还没有上推到屏幕顶部掩盖head   
                    else {  
                        // 不用进行特别的操作，只用更新paddingTop的值就行了   
                    }  
                }  
                // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态   
                if (state == PULL_To_REFRESH) {  
                    // 下拉到可以进入RELEASE_TO_REFRESH的状态   
                    if (tempY - startY >= headContentHeight) {  
                        state = RELEASE_To_REFRESH;  
                        isBack = true;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");  
                    }  
                    // 上推到顶了   
                    else if (tempY - startY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "由Done或者下拉刷新状态转变到done状态");  
                    }  
                }  
  
                // done状态下   
                if (state == DONE) {  
                    if (tempY - startY > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();  
                    }  
                }  
  
                // 更新headView的size   
                if (state == PULL_To_REFRESH) {  
                    headView.setPadding(0, -1 * headContentHeight  
                            + (tempY - startY), 0, 0);  
                    headView.invalidate();  
                }  
  
                // 更新headView的paddingTop   
                if (state == RELEASE_To_REFRESH) {  
                    headView.setPadding(0, tempY - startY - headContentHeight,  
                            0, 0);  
                    headView.invalidate();  
                }  
            }  
            break;  
        }  
        return super.onTouchEvent(event);  
    }  
  
    // 当状态改变时候，调用该方法，以更新界面   
    private void changeHeaderViewByState() {  
        switch (state) {  
        case RELEASE_To_REFRESH:  
  
            arrowImageView.setVisibility(View.VISIBLE);  
            progressBar.setVisibility(View.GONE);  
            tipsTextview.setVisibility(View.VISIBLE);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            arrowImageView.clearAnimation();  
            arrowImageView.startAnimation(animation);  
  
            tipsTextview.setText("松开可以刷新");  
  
            Log.v(TAG, "当前状态，松开刷新");  
            break;  
        case PULL_To_REFRESH:  
  
            progressBar.setVisibility(View.GONE);  
            tipsTextview.setVisibility(View.VISIBLE);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
            arrowImageView.clearAnimation();  
            arrowImageView.setVisibility(View.VISIBLE);  
            if (isBack) {  
                isBack = false;  
                arrowImageView.clearAnimation();  
                arrowImageView.startAnimation(reverseAnimation);  
  
                tipsTextview.setText("下拉可以刷新");  
            } else {  
                tipsTextview.setText("下拉可以刷新");  
            }  
            Log.v(TAG, "当前状态，下拉刷新");  
            break;  
  
        case REFRESHING:  
  
            headView.setPadding(0, 0, 0, 0);  
            headView.invalidate();  
  
            progressBar.setVisibility(View.VISIBLE);  
            arrowImageView.clearAnimation();  
            arrowImageView.setVisibility(View.GONE);  
            tipsTextview.setText("正在刷新，请稍后...");  
            lastUpdatedTextView.setVisibility(View.GONE);  
  
            Log.v(TAG, "当前状态,正在刷新...");  
            break;  
        case DONE:  
            headView.setPadding(0, -1 * headContentHeight, 0, 0);  
            headView.invalidate();  
  
            progressBar.setVisibility(View.GONE);  
            arrowImageView.clearAnimation();  
            // 此处更换图标   
            arrowImageView.setImageResource(R.drawable.goicon);  
  
            tipsTextview.setText("下拉可以刷新");  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            Log.v(TAG, "当前状态，done");  
            break;  
        }  
    }  
  
    public void setonRefreshListener(OnRefreshListener refreshListener) {  
        this.refreshListener = refreshListener;  
    }  
  
    public interface OnRefreshListener {  
        public void onRefresh();  
    }  
  
    public void onRefreshComplete() {  
        state = DONE;  
        lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());  
        changeHeaderViewByState();  
    }  
  
    private void onRefresh() {  
        if (refreshListener != null) {  
            refreshListener.onRefresh();  
        }  
    }  
  
    // 此处是“估计”headView的width以及height   
    private void measureView(View child) {  
        ViewGroup.LayoutParams p = child.getLayoutParams();  
        if (p == null) {  
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,  
                    ViewGroup.LayoutParams.WRAP_CONTENT);  
        }  
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
        int lpHeight = p.height;  
        int childHeightSpec;  
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,  
                    MeasureSpec.EXACTLY);  
        } else {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,  
                    MeasureSpec.UNSPECIFIED);  
        }  
        child.measure(childWidthSpec, childHeightSpec);  
    }  
  
}  

