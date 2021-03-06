package com.hua.weget;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import com.hua.utils.LogUtils2;

public class CustomeViewFlow extends AdapterView<Adapter> {

	private static final int SNAP_VELOCITY = 200;
	private static final int INVALID_SCREEN = -1;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private LinkedList<View> mLoadedViews;
	private int mCurrentBufferIndex;
	private int mCurrentAdapterIndex;
	private int mSideBuffer = 2;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchState = TOUCH_STATE_REST;
	private float mLastMotionX;
	/**
	 * 最小的可以滑动屏幕的移动距离
	 */
	private int mTouchSlop;
	private int mMaximumVelocity;
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private boolean mFirstLayout = true;
	private CustomeViewSwitchListener mViewSwitchListener;
	private Adapter mAdapter;
	private int mLastScrollDirection;
	private MyAdapterDataSetObserver mDataSetObserver;
	private FlowIndicator mIndicator;
	private int mLastOrientation = -1;
	private long timeSpan = 3000;
	private Handler mainHandler;
	public static boolean onTouch = false;// 是否点击到了viewFlow的区域，用于自定义图层中重定向事件的传递。

	private int startY = 0;
	private int startX = 0;
	private static final int MINREFRESHY = 10;// 用来设置下来时 最小的下啦值，然后判断下啦
	private static final int MAXREFRESHX = 50;
	protected static final int VIEWFOLW_WHAT = 100;
	private Context mContext;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private Runnable mRepeatTask;
	/**
	 * 间隔的时间 默认3秒
	 */ 
	private long mInterval = 3000;
	/**
	 * 是否在运行
	 */
	private boolean mRunning;
	/**
	 * 是否退出
	 */
	private boolean mQuit;

	private Runnable mOnTick = new Runnable() {

		public void run() {
//			mRepeatTask.run();
			if (mRunning && !mQuit){
				Message message = mainHandler.obtainMessage(VIEWFOLW_WHAT);
				mainHandler.sendMessageDelayed(message, timeSpan);
//				mainHandler.postDelayed(mOnTick, mInterval);
				}
		}

		// final HandlerTimer this$0;
		//
		//
		// {
		// this$0 = HandlerTimer.this;
		// super();
		// }
	};

	private OnGlobalLayoutListener orientationChangeListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			getViewTreeObserver().removeGlobalOnLayoutListener(
					orientationChangeListener);
			setSelection(mCurrentAdapterIndex);
		}
	};

	/**
	 * Receives call backs when a new {@link View} has been scrolled to.
	 */
	public static interface CustomeViewSwitchListener {

		/**
		 * This method is called when a new View has been scrolled to.
		 * 
		 * @param view
		 *            the {@link View} currently in focus.
		 * @param position
		 *            The position in the adapter of the {@link View} currently
		 *            in focus.
		 */
		void onSwitched(View view, int position);

	}

	public CustomeViewFlow(Context context) {
		super(context);
		mSideBuffer = 5;
		mContext = context;
		init();
	}

	public CustomeViewFlow(Context context, int sideBuffer) {
		super(context);
		mSideBuffer = sideBuffer;
		mContext = context;
		init();
	}

	/**
	 * 初始化=====
	 */
	public void init() {

		mLoadedViews = new LinkedList<View>();
		mScroller = new Scroller(mContext);
		final ViewConfiguration mConfiguration = ViewConfiguration
				.get(mContext);
		// 意思应该是触发移动事件的最短距离，如果小于这个距离就不触发移动控件
		// configuration.getScaledTouchSlop() //获得能够进行手势滑动的距离
		// configuration.getScaledMinimumFlingVelocity()//获得允许执行一个fling手势动作的最小速度值
		// configuration.getScaledMaximumFlingVelocity()//获得允许执行一个fling手势动作的最大速度值
		mTouchSlop = mConfiguration.getScaledTouchSlop();
		mMaximumVelocity = mConfiguration.getScaledMaximumFlingVelocity();

	}

	/**
	 * 开启自动跳转
	 */
	public void startAutoFlowTimer(boolean isAutoPlay) {

		if (isAutoPlay) {
			mainHandler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {

					if (msg.what == VIEWFOLW_WHAT) {
						LogUtils2.e("startAutoFlowTimer-******************");
						snapToScreen((mCurrentScreen + 1) % getChildCount());
						Message message = mainHandler
								.obtainMessage(VIEWFOLW_WHAT);
						sendMessageDelayed(message, timeSpan);
					}
				}
			};

			LogUtils2.e("startAutoFlowTimer------");
//			Message message = mainHandler.obtainMessage(VIEWFOLW_WHAT);
//			mainHandler.sendMessageDelayed(message, timeSpan);

		}
		//
		// handler = new Handler(Looper.getMainLooper()) {
		// @Override
		// public void handleMessage(Message msg) {
		// LogUtils2.e("startAutoFlowTimer-******************");
		// snapToScreen((mCurrentScreen + 1) % getChildCount());
		// Message message = handler.obtainMessage(0);
		// sendMessageDelayed(message, timeSpan);
		// }
		// };
		//
		// LogUtils2.e("startAutoFlowTimer------");
		// Message message = handler.obtainMessage(0);
		// handler.sendMessageDelayed(message, timeSpan);
	}

	private void checkTimer(Runnable task, long delay, long interval) {
		if (mQuit)
			throw new IllegalStateException(
					"Timer has been quit. Can not run task in a quit timer.");
		if (task == null)
			throw new IllegalArgumentException("Timer task can not be null.");
		if (delay < 0L)
			throw new IllegalArgumentException("delay must be >= 0.");
		if (interval <= 0L)
			throw new IllegalArgumentException("interval must be > 0.");
		else
			return;
	}

	public synchronized void scheduleRepeatExecution( long delay,
			long interval) throws IllegalStateException,
			IllegalArgumentException {
		// Log.d(TAG, (new
		// StringBuilder("scheduleRepeatExecution(), delay = ")).append(delay).append(", interval = ").append(interval).append("ms").toString());
		checkTimer(null, delay, interval);
		if (mRunning)
			throw new IllegalStateException(
					"Timer is running a repeating execution. You should stop it before schedule a new one.");
		mRepeatTask = null;
		mInterval = interval;
		mRunning = true;
		if (delay == 0L)
			mainHandler.post(mOnTick);
		else
			mainHandler.postDelayed(mOnTick, delay);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation != mLastOrientation) {
			mLastOrientation = newConfig.orientation;
			getViewTreeObserver().addOnGlobalLayoutListener(
					orientationChangeListener);
		}
	}

	
	public int getViewsCount() {
		return mSideBuffer;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY && !isInEditMode()) {
			throw new IllegalStateException(
					"ViewFlow can only be used in EXACTLY mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY && !isInEditMode()) {
			throw new IllegalStateException(
					"ViewFlow can only be used in EXACTLY mode.");
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			mScroller.startScroll(0, 0, mCurrentScreen * width, 0, 0);
			mFirstLayout = false;
		}
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			getParent().requestDisallowInterceptTouchEvent(true);
			startX = (int) ev.getX();
			startY = (int) ev.getY();
			// Remember where the motion event started
			mLastMotionX = x;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			if (mainHandler != null)
				mainHandler.removeMessages(VIEWFOLW_WHAT);
			break;

		case MotionEvent.ACTION_MOVE:
			
			int statY = (int) ev.getY();
			int statX = (int) ev.getX();
			
			 int tempY = Math.abs(statY - startY);
			 int tempX = Math.abs(statX - startX);
			 
			if((tempY > MINREFRESHY) &&(tempX < MAXREFRESHX)){
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			
			final int xDiff = (int) Math.abs(x - mLastMotionX);

			boolean xMoved = xDiff > mTouchSlop;

			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_SCROLLING;
			}

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				final int scrollX = getScrollX();
				if (deltaX < 0) {
					if (scrollX > 0) {
						scrollBy(Math.max(-scrollX, deltaX), 0);
					}
				} else if (deltaX > 0) {
					final int availableToScroll = getChildAt(
							getChildCount() - 1).getRight()
							- scrollX - getWidth();
					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// Fling hard enough to move left
					snapToScreen(mCurrentScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount() - 1) {
					// Fling hard enough to move right
					snapToScreen(mCurrentScreen + 1);
				} else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}

			mTouchState = TOUCH_STATE_REST;
			if (mainHandler != null) {
				Message message = mainHandler.obtainMessage(VIEWFOLW_WHAT);
//				mainHandler.sendMessageDelayed(message, timeSpan);
				mainHandler.postDelayed(mOnTick, timeSpan);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return false;

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			onTouch = true; // 点击到viewflow的区域中
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = x;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			if (mainHandler != null)
				mainHandler.removeMessages(VIEWFOLW_WHAT);
			break;

		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);

			boolean xMoved = xDiff > mTouchSlop;

			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_SCROLLING;
			}

			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				final int scrollX = getScrollX();
				if (deltaX < 0) {
					if (scrollX > 0) {
						scrollBy(Math.max(-scrollX, deltaX), 0);
					}
				} else if (deltaX > 0) {
					final int availableToScroll = getChildAt(
							getChildCount() - 1).getRight()
							- scrollX - getWidth();
					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			onTouch = false;// 手指抬起，将数值进行重置
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
					// Fling hard enough to move left
					snapToScreen(mCurrentScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen < getChildCount() - 1) {
					// Fling hard enough to move right
					snapToScreen(mCurrentScreen + 1);
				}
				// else if (velocityX < -SNAP_VELOCITY
				// && mCurrentScreen == getChildCount() - 1) {
				// snapToScreen(0);
				// }
				// else if (velocityX > SNAP_VELOCITY
				// && mCurrentScreen == 0) {
				// snapToScreen(getChildCount() - 1);
				// }
				else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}

			mTouchState = TOUCH_STATE_REST;

			if (mainHandler != null) {
				Message message = mainHandler.obtainMessage(VIEWFOLW_WHAT);
//				mainHandler.sendMessageDelayed(message, timeSpan);
				mainHandler.postDelayed(mOnTick, timeSpan);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			snapToDestination();
			mTouchState = TOUCH_STATE_REST;
		}
		return true;
	}
	
	private void snapToDestination() {
		final int screenWidth = getWidth();
		final int whichScreen = (getScrollX() + (screenWidth / 2))
				/ screenWidth;

		snapToScreen(whichScreen);
	}
	
	@Override
	protected void onScrollChanged(int h, int v, int oldh, int oldv) {
		super.onScrollChanged(h, v, oldh, oldv);
		if (mIndicator != null) {
			/*
			 * The actual horizontal scroll origin does typically not match the
			 * perceived one. Therefore, we need to calculate the perceived
			 * horizontal scroll origin here, since we use a view buffer.
			 */
			int hPerceived = h + (mCurrentAdapterIndex - mCurrentBufferIndex)
					* getWidth();
			mIndicator.onScrolled(hPerceived, v, oldh, oldv);
		}
	}
	
	
	public void snapToScreen(int whichScreen) {

		mLastScrollDirection = whichScreen - mCurrentScreen;
		if (!mScroller.isFinished())
			return;

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		mNextScreen = whichScreen;

		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
		
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0,
					Math.min(mNextScreen, getChildCount() - 1));
			mNextScreen = INVALID_SCREEN;
			postViewSwitched(mLastScrollDirection);
		}
	}
	
	
	public void postViewSwitched(int direction) {
		if (direction == 0)
			return;

		if (direction > 0) { // to the right
			mCurrentAdapterIndex++;
			mCurrentBufferIndex++;

			// if(direction > 1) {
			// mCurrentAdapterIndex += mAdapter.getCount() - 2;
			// mCurrentBufferIndex += mAdapter.getCount() - 2;
			// }

			View recycleView = null;

			// Remove view outside buffer range
			if (mCurrentAdapterIndex > mSideBuffer) {
				recycleView = mLoadedViews.removeFirst();
				detachViewFromParent(recycleView);
				// removeView(recycleView);
				mCurrentBufferIndex--;
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex + mSideBuffer;
			if (newBufferIndex < mAdapter.getCount())
				mLoadedViews.addLast(makeAndAddView(newBufferIndex, true,
						recycleView));

		} else { // to the left
			mCurrentAdapterIndex--;
			mCurrentBufferIndex--;

			// if(direction < -1) {
			// mCurrentAdapterIndex -= mAdapter.getCount() - 2;
			// mCurrentBufferIndex -= mAdapter.getCount() - 2;
			// }

			View recycleView = null;

			// Remove view outside buffer range
			if (mAdapter.getCount() - 1 - mCurrentAdapterIndex > mSideBuffer) {
				recycleView = mLoadedViews.removeLast();
				detachViewFromParent(recycleView);
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex - mSideBuffer;
			if (newBufferIndex > -1) {
				mLoadedViews.addFirst(makeAndAddView(newBufferIndex, false,
						recycleView));
				mCurrentBufferIndex++;
			}

		}

		requestLayout();
		setVisibleView(mCurrentBufferIndex, true);
		if (mIndicator != null) {
			mIndicator.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
					mCurrentAdapterIndex);
		}
		if (mViewSwitchListener != null) {
			mViewSwitchListener
					.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
							mCurrentAdapterIndex);
		}
	}

	private View makeAndAddView(int position, boolean addToEnd, View convertView) {
		View view = mAdapter.getView(position, convertView, this);
		return setupChild(view, addToEnd, convertView != null);
	}
	
	private View setupChild(View child, boolean addToEnd, boolean recycle) {
		ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) child
				.getLayoutParams();
		if (p == null) {
			p = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		}
		if (recycle)
			attachViewToParent(child, (addToEnd ? -1 : 0), p);
		else
			addViewInLayout(child, (addToEnd ? -1 : 0), p, true);
		return child;
	}
	
	
	/**
	 * Scroll to the {@link View} in the view buffer specified by the index.
	 * 
	 * @param indexInBuffer
	 *            Index of the view in the view buffer.
	 */
	private void setVisibleView(int indexInBuffer, boolean uiThread) {
		mCurrentScreen = Math.max(0,
				Math.min(indexInBuffer, getChildCount() - 1));
		int dx = (mCurrentScreen * getWidth()) - mScroller.getCurrX();
		mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx,
				0, 0);
		if (dx == 0)
			onScrollChanged(mScroller.getCurrX() + dx, mScroller.getCurrY(),
					mScroller.getCurrX() + dx, mScroller.getCurrY());
		if (uiThread)
			invalidate();
		else
			postInvalidate();
	}
	
	
	/**
	 * Set the listener that will receive notifications every time the {code
	 * ViewFlow} scrolls.
	 * 
	 * @param l
	 *            the scroll listener
	 */
	public void setOnViewSwitchListener(CustomeViewSwitchListener l) {
		mViewSwitchListener = l;
	}
	
	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		setAdapter(adapter, 2);
	}

	
	public void setAdapter(Adapter adapter, int initialPosition) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}

		mAdapter = adapter;

		if (mAdapter != null) {
			mDataSetObserver = new MyAdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

		}
		if (mAdapter == null || mAdapter.getCount() == 0)
			return;

		setSelection(initialPosition);
	}
	
	
	@Override
	public View getSelectedView() {
		return (mCurrentBufferIndex < mLoadedViews.size() ? mLoadedViews
				.get(mCurrentBufferIndex) : null);
	}

	@Override
	public int getSelectedItemPosition() {
		return mCurrentAdapterIndex;
	}

	@Override
	public void setSelection(int position) {
		mNextScreen = INVALID_SCREEN;
		mScroller.forceFinished(true);
		if (mAdapter == null)
			return;

		position = Math.max(position, 0);
		position = Math.min(position, mAdapter.getCount() - 1);

		ArrayList<View> recycleViews = new ArrayList<View>();
		View recycleView;
		while (!mLoadedViews.isEmpty()) {
			recycleViews.add(recycleView = mLoadedViews.remove());
			detachViewFromParent(recycleView);
		}

		View currentView = makeAndAddView(position, true,
				(recycleViews.isEmpty() ? null : recycleViews.remove(0)));
		mLoadedViews.addLast(currentView);

		for (int offset = 1; mSideBuffer - offset >= 0; offset++) {
			int leftIndex = position - offset;
			int rightIndex = position + offset;
			if (leftIndex >= 0)
				mLoadedViews
						.addFirst(makeAndAddView(
								leftIndex,
								false,
								(recycleViews.isEmpty() ? null : recycleViews
										.remove(0))));
			if (rightIndex < mAdapter.getCount())
				mLoadedViews
						.addLast(makeAndAddView(rightIndex, true, (recycleViews
								.isEmpty() ? null : recycleViews.remove(0))));
		}

		mCurrentBufferIndex = mLoadedViews.indexOf(currentView);
		mCurrentAdapterIndex = position;

		for (View view : recycleViews) {
			removeDetachedView(view, false);
		}
		requestLayout();
		setVisibleView(mCurrentBufferIndex, false);
		if (mIndicator != null) {
			mIndicator.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
					mCurrentAdapterIndex);
		}
		if (mViewSwitchListener != null) {
			mViewSwitchListener
					.onSwitched(mLoadedViews.get(mCurrentBufferIndex),
							mCurrentAdapterIndex);
		}
	}
	
	class MyAdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			View v = getChildAt(mCurrentBufferIndex);
			if (v != null) {
				for (int index = 0; index < mAdapter.getCount(); index++) {
					if (v.equals(mAdapter.getItem(index))) {
						mCurrentAdapterIndex = index;
						break;
					}
				}
			}
			resetFocus();
		}

		@Override
		public void onInvalidated() {
			// Not yet implemented!
		}

	}
	
	private void resetFocus() {
		mLoadedViews.clear();
		removeAllViewsInLayout();

		for (int i = Math.max(0, mCurrentAdapterIndex - mSideBuffer); i < Math
				.min(mAdapter.getCount(), mCurrentAdapterIndex + mSideBuffer
						+ 1); i++) {
			mLoadedViews.addLast(makeAndAddView(i, true, null));
			if (i == mCurrentAdapterIndex)
				mCurrentBufferIndex = mLoadedViews.size() - 1;
		}
		requestLayout();
	}

	
	
	
}
