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
  
    // �ɿ�ˢ�±�־   
    private final static int RELEASE_To_REFRESH = 0;  
    // ����ˢ�±�־   
    private final static int PULL_To_REFRESH = 1;  
    // ����ˢ�±�־   
    private final static int REFRESHING = 2;  
    // ˢ����ɱ�־   
    private final static int DONE = 3;  
  
    private LayoutInflater inflater;  
  
    private LinearLayout headView;  
    private TextView tipsTextview;  
    private TextView lastUpdatedTextView;  
    private ImageView arrowImageView;  
    private ProgressBar progressBar;  
    // �������ü�ͷͼ�궯��Ч��   
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;  
  
    // ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��   
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
  
                Log.v(TAG, "��¼����ʱ��λ��");  
            }  
            break;  
  
        case MotionEvent.ACTION_UP:  
  
            if (state != REFRESHING) {  
                if (state == DONE) {  
                    Log.v(TAG, "ʲô������");  
                }  
                if (state == PULL_To_REFRESH) {  
                    state = DONE;  
                    changeHeaderViewByState();  
  
                    Log.v(TAG, "������ˢ��״̬��ˢ�����״̬");  
                }  
                if (state == RELEASE_To_REFRESH) {  
                    state = REFRESHING;  
                    changeHeaderViewByState();  
                    onRefresh();  
  
                    Log.v(TAG, "���ɿ�ˢ��״̬����ˢ�����״̬");  
                }  
            }  
  
            isRecored = false;  
            isBack = false;  
  
            break;  
  
        case MotionEvent.ACTION_MOVE:  
            int tempY = (int) event.getY();  
            if (!isRecored && firstItemIndex == 0) {  
                Log.v(TAG, "��¼��קʱ��λ��");  
                isRecored = true;  
                startY = tempY;  
            }  
            if (state != REFRESHING && isRecored) {  
                // �����ɿ�ˢ����   
                if (state == RELEASE_To_REFRESH) {  
                    // �����ƣ��Ƶ���Ļ�㹻�ڸ�head�ĳ̶ȣ�����û��ȫ���ڸ�   
                    if ((tempY - startY < headContentHeight)  
                            && (tempY - startY) > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");  
                    }  
                    // һ�����Ƶ���   
                    else if (tempY - startY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");  
                    }  
                    // �����������߻�û�����Ƶ���Ļ�����ڸ�head   
                    else {  
                        // ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������   
                    }  
                }  
                // ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬   
                if (state == PULL_To_REFRESH) {  
                    // ���������Խ���RELEASE_TO_REFRESH��״̬   
                    if (tempY - startY >= headContentHeight) {  
                        state = RELEASE_To_REFRESH;  
                        isBack = true;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");  
                    }  
                    // ���Ƶ�����   
                    else if (tempY - startY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();  
  
                        Log.v(TAG, "��Done��������ˢ��״̬ת�䵽done״̬");  
                    }  
                }  
  
                // done״̬��   
                if (state == DONE) {  
                    if (tempY - startY > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();  
                    }  
                }  
  
                // ����headView��size   
                if (state == PULL_To_REFRESH) {  
                    headView.setPadding(0, -1 * headContentHeight  
                            + (tempY - startY), 0, 0);  
                    headView.invalidate();  
                }  
  
                // ����headView��paddingTop   
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
  
    // ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���   
    private void changeHeaderViewByState() {  
        switch (state) {  
        case RELEASE_To_REFRESH:  
  
            arrowImageView.setVisibility(View.VISIBLE);  
            progressBar.setVisibility(View.GONE);  
            tipsTextview.setVisibility(View.VISIBLE);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            arrowImageView.clearAnimation();  
            arrowImageView.startAnimation(animation);  
  
            tipsTextview.setText("�ɿ�����ˢ��");  
  
            Log.v(TAG, "��ǰ״̬���ɿ�ˢ��");  
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
  
                tipsTextview.setText("��������ˢ��");  
            } else {  
                tipsTextview.setText("��������ˢ��");  
            }  
            Log.v(TAG, "��ǰ״̬������ˢ��");  
            break;  
  
        case REFRESHING:  
  
            headView.setPadding(0, 0, 0, 0);  
            headView.invalidate();  
  
            progressBar.setVisibility(View.VISIBLE);  
            arrowImageView.clearAnimation();  
            arrowImageView.setVisibility(View.GONE);  
            tipsTextview.setText("����ˢ�£����Ժ�...");  
            lastUpdatedTextView.setVisibility(View.GONE);  
  
            Log.v(TAG, "��ǰ״̬,����ˢ��...");  
            break;  
        case DONE:  
            headView.setPadding(0, -1 * headContentHeight, 0, 0);  
            headView.invalidate();  
  
            progressBar.setVisibility(View.GONE);  
            arrowImageView.clearAnimation();  
            // �˴�����ͼ��   
            arrowImageView.setImageResource(R.drawable.goicon);  
  
            tipsTextview.setText("��������ˢ��");  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            Log.v(TAG, "��ǰ״̬��done");  
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
        lastUpdatedTextView.setText("�������:" + new Date().toLocaleString());  
        changeHeaderViewByState();  
    }  
  
    private void onRefresh() {  
        if (refreshListener != null) {  
            refreshListener.onRefresh();  
        }  
    }  
  
    // �˴��ǡ����ơ�headView��width�Լ�height   
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

