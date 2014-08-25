package com.hua.view;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * ������ʱ���е��Ե�ListView�������ķ���
 * 
 * @version 1.0
 * @time 2013-12-24 10:17:29
 */
public class PullDownListView extends ListView implements OnScrollListener {
	private static final String TAG = PullDownListView.class.getSimpleName();
	private static final int PULL_DOWN_BACK_ACTION = 0x01;
	private static final int PULL_UP_BACK_ACTION = 0x02;

	private static final float PULL_FACTOR = 0.5F;// ��������,ʵ������ʱ���ӳ�Ч��
	private static final int PULL_BACK_REDUCE_STEP = 1;// �ص�ʱÿ�μ��ٵĸ߶�
	private static final int PULL_BACK_TASK_PERIOD = 500000;// �ص�ʱ�ݼ�HeadView�߶ȵ�Ƶ��, ע��������Ϊ��λ
	private boolean isRecordPullDown;
	private boolean isRecordPullUp;
	private int startPullDownY;// ��¼�տ�ʼ����ʱ�Ĵ���λ�õ�Y����
	private int startPullUpY;// ��¼�տ�ʼ����ʱ�Ĵ���λ�õ�Y����
	private int firstItemIndex;// ��һ���ɼ���Ŀ������
	private int lastItemIndex;// ���һ���ɼ���Ŀ������
	private View mHeadView;// ����ʵ����������Ч����HeadView
	private View mTailView;// ����ʵ����������Ч����TailView
	private int currentScrollState;
	private ScheduledExecutorService schedulor;// ʵ�ֻص�Ч���ĵ�����
//	private ScheduledExecutorService schedulor_pull_up;// ʵ�����»ص�Ч���ĵ�����

	/**
	 * ʵ�ֻص�Ч����handler,���ڵݼ�HeadView�ĸ߶Ȳ������ػ�
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case PULL_DOWN_BACK_ACTION:
				AbsListView.LayoutParams headerParams = (LayoutParams) mHeadView
						.getLayoutParams();
				// �ݼ��߶�
				headerParams.height -= PULL_BACK_REDUCE_STEP;
				mHeadView.setLayoutParams(headerParams);
				// �ػ�
				mHeadView.invalidate();
				// ֹͣ�ص�ʱ�ݼ�headView�߶ȵ�����
				if (headerParams.height <= 0) {
					schedulor.shutdownNow();
				}
				
				break;
			case PULL_UP_BACK_ACTION:
				AbsListView.LayoutParams footerParams = (LayoutParams) mTailView
				.getLayoutParams();
				// �ݼ��߶�
				footerParams.height -= PULL_BACK_REDUCE_STEP;
				mTailView.setLayoutParams(footerParams);
				// �ػ�
				mTailView.invalidate();
				// ֹͣ�ص�ʱ�ݼ�headView�߶ȵ�����
				if (footerParams.height <= 0) {
					schedulor.shutdownNow();
				}
				
				break;
			}
		}
	};

	/**
	 * ���캯��
	 * 
	 * @param context
	 */
	public PullDownListView(Context context, boolean isHeadViewNeed, boolean isTailViewNeed) {
		super(context);
		init(isHeadViewNeed, isTailViewNeed);
	}

	/**
	 * ���캯��
	 * 
	 * @param context
	 * @param attr
	 */
	public PullDownListView(Context context, AttributeSet attr) {
		super(context, attr);
		init(true, true);
	}

	/**
	 * ��ʼ��
	 */
	@SuppressWarnings("deprecation")
	private void init(boolean isHeadViewNeed, boolean isTailViewNeed) {
		Log.d(TAG, "isHeadViewNeed=" + isHeadViewNeed);
		Log.d(TAG, "isTailViewNeed=" + isTailViewNeed);
		if(isHeadViewNeed) {
			// ��������״̬
			setOnScrollListener(this);
			// ����PullListView��HeadView
			mHeadView = new View(this.getContext());
			// Ĭ�ϰ�ɫ����,���Ըı���ɫ, Ҳ�������ñ���ͼƬ
			mHeadView.setBackgroundColor(Color.parseColor("#4F9D9D"));
			// Ĭ�ϸ߶�Ϊ0
			mHeadView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 0));
			this.addHeaderView(mHeadView);
		} 
		
		if(isTailViewNeed) {
			// ��������״̬
			setOnScrollListener(this);
			// ����PullListView��HeadView
			mTailView = new View(this.getContext());
			// Ĭ�ϰ�ɫ����,���Ըı���ɫ, Ҳ�������ñ���ͼƬ
			mTailView.setBackgroundColor(Color.parseColor("#4F9D9D"));
			// Ĭ�ϸ߶�Ϊ0
			mTailView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, 0));
			this.addFooterView(mTailView);
		}
	}

	/**
	 * ����onTouchEvent����,ʵ�������ص�Ч��
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (!isRecordPullDown && !isRecordPullUp) {
				// it's not in pull down state or pull up state, break
				Log.d(TAG, "ACTION_UP it's not in pull down state or pull up state, break");
				break;
			}
			if(isPullDownState()) {
				Log.d(TAG, "isRecordPullDown=" + isRecordPullDown);
				// ��һ����Ƶ�ʵݼ�HeadView�ĸ߶�,ʵ��ƽ���ص�
				schedulor = Executors.newScheduledThreadPool(1);
				schedulor.scheduleAtFixedRate(new Runnable() {
	
					@Override
					public void run() {
						mHandler.sendEmptyMessage(PULL_DOWN_BACK_ACTION);
	
					}
				}, 0, PULL_BACK_TASK_PERIOD, TimeUnit.NANOSECONDS);
	
				setPullDownState(!isRecordPullDown);
			} else if(isPullUpState()) {
				Log.d(TAG, "isRecordPullUp=" + isRecordPullUp);
				// ��һ����Ƶ�ʵݼ�HeadView�ĸ߶�,ʵ��ƽ���ص�
				schedulor = Executors.newScheduledThreadPool(1);
				schedulor.scheduleAtFixedRate(new Runnable() {
	
					@Override
					public void run() {
						mHandler.sendEmptyMessage(PULL_UP_BACK_ACTION);
	
					}
				}, 0, PULL_BACK_TASK_PERIOD, TimeUnit.NANOSECONDS);
	
				setPullUpState(!isRecordPullUp);
			}

			break;

		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "firstItemIndex=" + firstItemIndex);
			if (!isRecordPullDown && firstItemIndex == 0) {
				Log.d(TAG, "firstItemIndex=" + firstItemIndex + " set isRecordPullDown=true");
				startPullDownY = (int) event.getY();
				setPullType(PULL_DOWN_BACK_ACTION);
			}else if (!isRecordPullUp && lastItemIndex == getCount()) {
				Log.d(TAG, "lastItemIndex == getCount()" + " set isRecordPullUp=true");
				startPullUpY = (int) event.getY();
				setPullType(PULL_UP_BACK_ACTION);
			}

			if (!isRecordPullDown && !isRecordPullUp) {
				// it's not in pull down state or pull up state, break
				Log.d(TAG, "ACTION_MOVE it's not in pull down state or pull up state, break");
				break;
			}

			if(isRecordPullDown) {
				int tempY = (int) event.getY();
				int moveY = tempY - startPullDownY;
				if (moveY < 0) {
					setPullDownState(false);
					break;
				}
	
				Log.d(TAG, "tempY=" + tempY);
				Log.d(TAG, "startPullDownY=" + startPullDownY);
				Log.d(TAG, "moveY=" + moveY);
				mHeadView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.MATCH_PARENT, (int) (moveY * PULL_FACTOR)));
				mHeadView.invalidate();
			} else if(isRecordPullUp) {
				int tempY = (int) event.getY();
				int moveY = startPullUpY - tempY;
				if (moveY < 0) {
					setPullUpState(false);
					break;
				}
	
				Log.d(TAG, "tempY=" + tempY);
				Log.d(TAG, "startPullUpY=" + startPullUpY);
				Log.d(TAG, "moveY=" + moveY);
				mTailView.setLayoutParams(new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT, (int) (moveY * PULL_FACTOR)));
				mTailView.invalidate();
			}

			break;
		}
		return super.onTouchEvent(event);
	}
	
	private synchronized void setPullDownState(boolean state) {
		isRecordPullDown = state;
	}
	
	public boolean isPullDownState() {
		return isRecordPullDown;
	}
	
	private synchronized void setPullUpState(boolean state) {
		isRecordPullUp = state;
	}
	
	public boolean isPullUpState() {
		return isRecordPullUp;
	}
	
	/**
	 * set pull type
	 * 
	 * @param action 
	 * 				pull-down:PULL_DOWN_BACK_ACTION
	 * 				pull-up:PULL_UP_BACK_ACTION
	 */
	private synchronized void setPullType(final int action) {
		switch(action) {
		case PULL_DOWN_BACK_ACTION:
			isRecordPullDown = true;
			isRecordPullUp = false;
			break;
		case PULL_UP_BACK_ACTION:
			isRecordPullDown = false;
			isRecordPullUp = true;
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		this.firstItemIndex = firstVisiableItem;
		this.lastItemIndex = firstVisiableItem + visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		currentScrollState = scrollState;
		Log.d(TAG, "scrollState: " + getScrollStateString(currentScrollState));
	}

	private String getScrollStateString(int flag) {
		String str = "";
		switch(flag) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			str = "SCROLL_STATE_IDLE";
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			str = "SCROLL_STATE_TOUCH_SCROLL";
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			str = "SCROLL_STATE_FLING";
			break;
		default:
			str = "wrong state";
		}
		
		return str;
	}
}