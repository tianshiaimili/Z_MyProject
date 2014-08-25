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
 * ʵ�������� �ص���ListView
 * @author Hua
 *
 */
public class ElasticListView extends ListView implements OnScrollListener{
	
	
	private static final String TAG = "ElasticScrollView";
		///////////////////�ⲿ�� ����β����
	private static final int PULL_DOWN_BACK_ACTION = 0x01;
	private static final int PULL_UP_BACK_ACTION = 0x02;
	private static final float PULL_FACTOR = 0.5F;// ��������,ʵ������ʱ���ӳ�Ч��
	private static final int PULL_BACK_REDUCE_STEP = 1;// �ص�ʱÿ�μ��ٵĸ߶�
	private static final int PULL_BACK_TASK_PERIOD = 500000;// �ص�ʱ�ݼ�HeadView�߶ȵ�Ƶ��, ע��������Ϊ��λ
	private boolean isRecordPullDown;
	private boolean isRecordPullUp;
	private View mTailView;// ����ʵ����������Ч����TailView
	private int currentScrollState;
	private ScheduledExecutorService schedulor;// ʵ�ֻص�Ч���ĵ�����
//	private int firstItemIndex;// ��һ���ɼ���Ŀ������
	private int startPullUpY;// ��¼�տ�ʼ����ʱ�Ĵ���λ�õ�Y����
	private int lastItemIndex;// ���һ���ɼ���Ŀ������
	///////////////////////
	
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
	private final static int RATIO = 3;

	private int headContentWidth;
	private int headContentHeight;

	/**
	 * ����Listview�е�LinearLayout����
	 */
	private LinearLayout innerLayout;
	/**
	 * һ��ʼ�����ص�HeaderView
	 */
	private LinearLayout headView;
	/**
	 * ��ͷͼƬ
	 */
	private ImageView arrowImageView;//��ͷͼƬ
	/**
	 * ������
	 */
	private ProgressBar progressBar;
	/**
	 * ��ʾ���� �ɿ� ˢ�µ�����
	 */
	private TextView tipsTextview;// ��ʾ���� �ɿ� ˢ�µ�����
	/**
	 * ��ʾ�ϴθ��µ�ʱ��
	 */
	private TextView lastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	private int state;
	/**
	 * �������������������������ʱ ��arrowImageViewͼƬ�����ж����ı䶯��
	 */
	private boolean isBack;

	private RotateAnimation animation; //��ͷ�Ķ���
	private RotateAnimation reverseAnimation;

	private boolean canReturn; //������ʾ�ɿ��ֺ�Ϳ��Իص���
	private boolean isRecored;
	/**
	 * ������ʾһ��ʼ��ָ����ʱ�� Y �����
	 */
	private int startY;
	/**
	 * 
	 */
	private int firstItemIndex ; 

	
	
	/**
	 * ʵ�ֻص�Ч����handler,���ڵݼ�HeadView�ĸ߶Ȳ������ػ�
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
//				// �ݼ��߶�
//				headerParams.height -= PULL_BACK_REDUCE_STEP;
//				mHeadView.setLayoutParams(headerParams);
//				// �ػ�
//				mHeadView.invalidate();
//				// ֹͣ�ص�ʱ�ݼ�headView�߶ȵ�����
//				if (headerParams.height <= 0) {
//					schedulor.shutdownNow();
//				}
//				
//				break;
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
		 * ����ʱ ��ʾ�Ĳ���
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
		 * ��ȡ����headerview�Ĵ�С�Ϳ�Ȼ������paddingֵ��Ȼ����һ��ʼ
		 * ����ʾ��screen
		 */
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.i("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		//��headerView��ӵ��ڲ���linearlayout��
//		innerLayout.addView(headView);
		setOnScrollListener(this);
		/**
		 * ��Linearlayout��ӵ���ǰ��Listview
		 * ��
		 */
//		addView(innerLayout);
		addHeaderView(headView);
//		addView(innerLayout, 1);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		///���ü�����
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
	 * ��ʼ��footview
	 */
	private void initFootView() {

		// ��������״̬
		setOnScrollListener(this);
		// ����PullListView��HeadView
		mTailView = new View(this.getContext());
		// Ĭ�ϰ�ɫ����,���Ըı���ɫ, Ҳ�������ñ���ͼƬ
		mTailView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// Ĭ�ϸ߶�Ϊ0
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
	 * ����
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
		LogUtils2.w("��������������............");
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
						// ʲô������
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.i(TAG, "������ˢ��״̬����done״̬");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						LogUtils2.i("�������..onRefresh");
						onRefresh();
						Log.i(TAG, "���ɿ�ˢ��״̬����done״̬");
					}
				}
				
				/**
				 * ������������
				 */
				if(isPullUpState()) {
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
				
				isRecored = false;
				isBack = false;

				break;
		            
			case MotionEvent.ACTION_MOVE:
				/**
				 * ��ʱ������� ��ʾֹͣ������������
				 */
				int tempY = (int) event.getY();
				
			    if (!isRecored && firstItemIndex == 0) {  
	                Log.v(TAG, "��¼��קʱ��λ��");  
	                isRecored = true;  
	                startY = tempY;  
	            }  
				
				LogUtils2.d("tempY=="+tempY+"   startY=="+startY);
				
				/**
				 * ���������õ���ָ������ȥˢ�µ�ʱ��
				 */
				if (state != REFRESHING && isRecored && state != LOADING) {
					// ��������ȥˢ����
					if (state == RELEASE_To_REFRESH) {
						LogUtils2.i("*******************");
						canReturn = true;
					     // �����ƣ��Ƶ���Ļ�㹻�ڸ�head�ĳ̶ȣ�����û��ȫ���ڸ�   
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							LogUtils2.i("���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
						}
						// һ�����Ƶ�����
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							LogUtils2.i("���ɿ�ˢ��״̬ת�䵽done״̬");
						} else {
							// ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
						}
					}

					// ����headView��size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
						headView.invalidate();
					}

					// ����headView��paddingTop
					
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
				 *  done״̬��  һ��ʼ��״̬
				 */
				if (state == DONE) {
					if (tempY - startY > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				/**
				 *  ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
				 */
				if (state == PULL_To_REFRESH) {
					canReturn = true;

					// ���������Խ���RELEASE_TO_REFRESH��״̬
					if ((tempY - startY) / RATIO >= headContentHeight) {
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();
						LogUtils2.i("��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
					}else if (tempY - startY <= 0) {
						// ���Ƶ�����
						state = DONE;
						changeHeaderViewByState();
						LogUtils2.i("��DOne��������ˢ��״̬ת�䵽done״̬");
					}
				}
				
				/**
				 * ������������β���ص���
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

			tipsTextview.setText("�ɿ�ˢ��");

			LogUtils2.i("��ǰ״̬���ɿ�ˢ��*******");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("����ˢ��");
			} else {
				tipsTextview.setText("����ˢ��");
			}
			Log.i(TAG, "��ǰ״̬������ˢ��");
			LogUtils2.d("��ǰ״̬������ˢ��...");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("����ˢ��...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.i(TAG, "��ǰ״̬,����ˢ��...");
			LogUtils2.d("��ǰ״̬,����ˢ��...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.goicon);
			tipsTextview.setText("����ˢ��");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			LogUtils2.i("��ǰ״̬��done......");
			break;
		}
	}

	/**
	 * MeasureSpec��װ��parent���ݸ�child��layoutҪ��ÿ��MeasureSpec��ʾ��width/height��Ҫ��
	 * MeasureSpec��size��mode��ɡ����õ�mode��3�֣�
	1. UNSPECIFIED��ʾparentû��ǿ�Ӹ�child�κ�constraint��
	2. EXACTLY��ʾparent�Ѿ�ȷ��child�ľ�ȷsize��
	3. AT_MOST��ʾchild�����趨Ϊspecified size֮�ڵ��κ�ֵ��
	 * @param child
	 */
	private void measureView(View child) {
		
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		
		/**
		 * ����Ӧ���ǲ�����ȡ��View�Ĵ�С�ѣ�Ȼ���ڸ�view�и������ʵĴ�С��ʾ
		 */
		LogUtils2.d("p.width=="+p.width+"   p.height=="+p.height);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		/**
		 * �����ȡ��lpHeight һ���Ǹ���layout�����ļ������Layout_height��ֵ��ȡ�� 
		 * �����WRAP_CONTENT ��ֵ ��-2 
		 * MATCH_PARENT ��-1
		 * �������0  ˵�������Ǹ�����ȷ��ȡֵ�� ����300dp ����100dp �����Ĵ�С
		 */
		int lpHeight = p.height;
		int childHeightSpec;
		/**
		 * 
		 */
		if (lpHeight > 0) {
			//����0  ˵��������ȷ����ֵ����ôģʽ����MeasureSpec.EXACTLY ȷ�е�����
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
		lastUpdatedTextView.setText("�������:" + new Date().toLocaleString());
		changeHeaderViewByState();
		invalidate();
		scrollTo(0, 0);
	}

	private void onRefresh() {
		if (refreshListener != null) {
			LogUtils2.i("��ʼ...........onRefresh");
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
