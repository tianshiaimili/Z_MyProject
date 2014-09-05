package com.hua.view;

import java.util.Date;
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
import com.hua.util.LogUtils2;


/**
 * 实现上下拉 回弹的ListView
 * @author Hua
 *
 */
public class ElasticListView extends ListView implements OnScrollListener{
	
	
	private static final String TAG = "ElasticScrollView";
		///////////////////这部分 设置尾处的
	private static final int PULL_DOWN_BACK_ACTION = 0x01;
	private static final int PULL_UP_BACK_ACTION = 0x02;
	private static final float PULL_FACTOR = 0.5F;// 下拉因子,实现下拉时的延迟效果
	private static final int PULL_BACK_REDUCE_STEP = 1;// 回弹时每次减少的高度
	private static final int PULL_BACK_TASK_PERIOD = 500000;// 回弹时递减HeadView高度的频率, 注意以纳秒为单位
	private boolean isRecordPullDown;
	private boolean isRecordPullUp;
	private View mTailView;// 用于实现上拉弹性效果的TailView
	private int currentScrollState;
	private ScheduledExecutorService schedulor;// 实现回弹效果的调度器
//	private int firstItemIndex;// 第一个可见条目的索引
	private int startPullUpY;// 记录刚开始上拉时的触摸位置的Y坐标
	private int lastItemIndex;// 最后一个可见条目的索引
	///////////////////////
	
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private int headContentWidth;
	private int headContentHeight;

	/**
	 * 整个Listview中的LinearLayout布局
	 */
	private LinearLayout innerLayout;
	/**
	 * 一开始被隐藏的HeaderView
	 */
	private LinearLayout headView;
	/**
	 * 箭头图片
	 */
	private ImageView arrowImageView;//箭头图片
	/**
	 * 进度条
	 */
	private ProgressBar progressBar;
	/**
	 * 提示下拉 松开 刷新的文字
	 */
	private TextView tipsTextview;// 提示下拉 松开 刷新的文字
	/**
	 * 显示上次更新的时间
	 */
	private TextView lastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	private int state;
	/**
	 * 这个是用来做当下拉后，又上啦时 对arrowImageView图片做的判断来改变动画
	 */
	private boolean isBack;

	private RotateAnimation animation; //箭头的动画
	private RotateAnimation reverseAnimation;

	private boolean canReturn; //用来表示松开手后就可以回弹了
	private boolean isRecored;
	/**
	 * 用来表示一开始手指按下时的 Y 坐标点
	 */
	private int startY;
	/**
	 * 
	 */
	private int firstItemIndex ; 

	
	
	/**
	 * 实现回弹效果的handler,用于递减HeadView的高度并请求重绘
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
//			case PULL_DOWN_BACK_ACTION:
//				AbsListView.LayoutParams headerParams = (LayoutParams) mHeadView
//						.getLayoutParams();
//				// 递减高度
//				headerParams.height -= PULL_BACK_REDUCE_STEP;
//				mHeadView.setLayoutParams(headerParams);
//				// 重绘
//				mHeadView.invalidate();
//				// 停止回弹时递减headView高度的任务
//				if (headerParams.height <= 0) {
//					schedulor.shutdownNow();
//				}
//				
//				break;
			case PULL_UP_BACK_ACTION:
				AbsListView.LayoutParams footerParams = (LayoutParams) mTailView
				.getLayoutParams();
				// 递减高度
				footerParams.height -= PULL_BACK_REDUCE_STEP;
				mTailView.setLayoutParams(footerParams);
				// 重绘
				mTailView.invalidate();
				// 停止回弹时递减headView高度的任务
				if (footerParams.height <= 0) {
					schedulor.shutdownNow();
				}
				
				break;
			}
		}
	};
	
	public ElasticListView(Context context) {
		super(context);
		init(context);
	}

	public ElasticListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		innerLayout = new LinearLayout(context);
		innerLayout.setLayoutParams(new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.VERTICAL);
		
		/**
		 * 下拉时 显示的部分
		 */
		headView = (LinearLayout) inflater.inflate(R.layout.myscrollview_head,
				null);
		
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		
	
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		/**
		 * 
		 */
		measureView(headView);

		/**
		 * 获取得了headerview的大小和宽，然后设置padding值，然他们一开始
		 * 不显示在screen
		 */
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.i("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		//把headerView添加到内部的linearlayout中
//		innerLayout.addView(headView);
		setOnScrollListener(this);
		/**
		 * 把Linearlayout添加到当前的Listview
		 * 中
		 */
//		addView(innerLayout);
		addHeaderView(headView);
//		addView(innerLayout, 1);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		///设置加速器
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		initFootView();
		
		state = DONE;
		isRefreshable = true;
		canReturn = false;
	}

	
	/**
	 * 初始化footview
	 */
	private void initFootView() {

		// 监听滚动状态
		setOnScrollListener(this);
		// 创建PullListView的HeadView
		mTailView = new View(this.getContext());
		// 默认白色背景,可以改变颜色, 也可以设置背景图片
		mTailView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// 默认高度为0
		mTailView.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, 0));
		this.addFooterView(mTailView);
		
	}

	public boolean isPullDownState() {
		return isRecordPullDown;
	}
	
	public boolean isPullUpState() {
		return isRecordPullUp;
	}
	
	private synchronized void setPullUpState(boolean state) {
		isRecordPullUp = state;
	}
	
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
	
	/**
	 * 监听
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		currentScrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		firstItemIndex  = firstVisibleItem;
		this.lastItemIndex = firstVisibleItem + visibleItemCount;
		LogUtils2.w("啦啦啦德玛西亚............");
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			
			int ACTION_CODE = event.getAction();
			
			switch (ACTION_CODE) {
			
			case MotionEvent.ACTION_DOWN:
				
			     if (firstItemIndex == 0 && !isRecored) {  
		                startY = (int) event.getY();  
		                isRecored = true;  
		            }
			     
		            break;  

			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.i(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						LogUtils2.i("这里调用..onRefresh");
						onRefresh();
						Log.i(TAG, "由松开刷新状态，到done状态");
					}
				}
				
				/**
				 * 针对上啦的情况
				 */
				if(isPullUpState()) {
					Log.d(TAG, "isRecordPullUp=" + isRecordPullUp);
					// 以一定的频率递减HeadView的高度,实现平滑回弹
					schedulor = Executors.newScheduledThreadPool(1);
					schedulor.scheduleAtFixedRate(new Runnable() {
		
						@Override
						public void run() {
							mHandler.sendEmptyMessage(PULL_UP_BACK_ACTION);
		
						}
					}, 0, PULL_BACK_TASK_PERIOD, TimeUnit.NANOSECONDS);
		
					setPullUpState(!isRecordPullUp);
				}
				
				isRecored = false;
				isBack = false;

				break;
		            
			case MotionEvent.ACTION_MOVE:
				/**
				 * 临时的坐标点 表示停止滑动后的坐标点
				 */
				int tempY = (int) event.getY();
				
			    if (!isRecored && firstItemIndex == 0) {  
	                Log.v(TAG, "记录拖拽时的位置");  
	                isRecored = true;  
	                startY = tempY;  
	            }  
				
				LogUtils2.d("tempY=="+tempY+"   startY=="+startY);
				
				/**
				 * 这里是设置当手指拖拉脱去刷新的时候
				 */
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						LogUtils2.i("*******************");
						canReturn = true;
					     // 往上推，推到屏幕足够掩盖head的程度，但还没有全部掩盖   
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							LogUtils2.i("由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							LogUtils2.i("由松开刷新状态转变到done状态");
						} else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
						headView.invalidate();
					}

					// 更新headView的paddingTop
					
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
						headView.invalidate();
					}
					
					
					if (canReturn) {
						canReturn = false;
						return true;
					}
				}
				
				
				/**
				 *  done状态下  一开始的状态
				 */
				if (state == DONE) {
					if (tempY - startY > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				/**
				 *  还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				 */
				if (state == PULL_To_REFRESH) {
					canReturn = true;

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - startY) / RATIO >= headContentHeight) {
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();
						LogUtils2.i("由done或者下拉刷新状态转变到松开刷新");
					}else if (tempY - startY <= 0) {
						// 上推到顶了
						state = DONE;
						changeHeaderViewByState();
						LogUtils2.i("由DOne或者下拉刷新状态转变到done状态");
					}
				}
				
				/**
				 * 下面这里设置尾部回弹的
				 */
				if (!isRecordPullUp && lastItemIndex == getCount()) {
					Log.d(TAG, "lastItemIndex == getCount()" + " set isRecordPullUp=true");
					startPullUpY = (int) event.getY();
					setPullType(PULL_UP_BACK_ACTION);
				}
				
				if(isRecordPullUp) {
					int tempY2 = (int) event.getY();
					int moveY = startPullUpY - tempY2;
					if (moveY < 0) {
						setPullUpState(false);
						break;
					}
		
					Log.d(TAG, "tempY2=" + tempY2);
					Log.d(TAG, "startPullUpY=" + startPullUpY);
					Log.d(TAG, "moveY=" + moveY);
					mTailView.setLayoutParams(new AbsListView.LayoutParams(
							LayoutParams.MATCH_PARENT, (int) (moveY * PULL_FACTOR)));
					mTailView.invalidate();
				}
				
				break;
				
			}
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

			tipsTextview.setText("松开刷新");

			LogUtils2.i("当前状态，松开刷新*******");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			Log.i(TAG, "当前状态，下拉刷新");
			LogUtils2.d("当前状态，下拉刷新...");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.i(TAG, "当前状态,正在刷新...");
			LogUtils2.d("当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.goicon);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			LogUtils2.i("当前状态，done......");
			break;
		}
	}

	/**
	 * MeasureSpec封装从parent传递给child的layout要求。每个MeasureSpec表示对width/height的要求。
	 * MeasureSpec由size和mode组成。可用的mode有3种：
	1. UNSPECIFIED表示parent没有强加给child任何constraint。
	2. EXACTLY表示parent已经确定child的精确size。
	3. AT_MOST表示child可以设定为specified size之内的任何值。
	 * @param child
	 */
	private void measureView(View child) {
		
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		
		/**
		 * 这里应该是测量获取子View的大小把，然后在父view中给出合适的大小显示
		 */
		LogUtils2.d("p.width=="+p.width+"   p.height=="+p.height);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		/**
		 * 这里获取的lpHeight 一般是根据layout布局文件里面的Layout_height的值获取的 
		 * 如果是WRAP_CONTENT 则值 是-2 
		 * MATCH_PARENT 是-1
		 * 如果大于0  说明可能是给出明确的取值了 例如300dp 或者100dp 这样的大小
		 */
		int lpHeight = p.height;
		int childHeightSpec;
		/**
		 * 
		 */
		if (lpHeight > 0) {
			//大于0  说明设置了确定的值，那么模式就是MeasureSpec.EXACTLY 确切的类型
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		
		//
		LogUtils2.i("childWidthSpec=="+childWidthSpec+"   childHeightSpec="+childHeightSpec);
		child.measure(childWidthSpec, childHeightSpec);
	}

	
	
	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
		invalidate();
		scrollTo(0, 0);
	}

	private void onRefresh() {
		if (refreshListener != null) {
			LogUtils2.i("开始...........onRefresh");
			refreshListener.onRefresh();
		}
	}

	public void addChild(View child) {
		innerLayout.addView(child);
	}

	public void addChild(View child, int position) {
		innerLayout.addView(child, position);
	}


}
