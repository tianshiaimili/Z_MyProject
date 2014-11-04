package com.hua.homefragment.subfragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hua.activity.DetailsActivity_;
import com.hua.activity.ImageDetailActivity_;
import com.hua.activity.MTNApplication;
import com.hua.activity.R;
import com.hua.adapter.HomeSubFragment1_ListViewBaseAdater;
import com.hua.adapter.ProductSlectionAdapter;
import com.hua.adapter.ViewFlowAdapter;
import com.hua.androidos.HandlerTimer;
import com.hua.app.BaseActivity2;
import com.hua.app.BaseFragment2;
import com.hua.bean.NewModle;
import com.hua.contants.Constant;
import com.hua.contants.Url;
import com.hua.network.http.json.NewListJson;
import com.hua.network.utils.HttpUtil;
import com.hua.utils.FragmentUtils;
import com.hua.utils.FragmentUtils.FragmentTabSwitcherFeed;
import com.hua.utils.FragmentUtils.FragmentTabSwitcherWithoutZorder;
import com.hua.utils.LogUtils2;
import com.hua.utils.MyImageLoader.MyLoadImageTask;
import com.hua.utils.PreferenceHelper;
import com.hua.utils.StringUtils;
import com.hua.view.CustomeListViewFooter;
import com.hua.view.ElasticScrollView;
import com.hua.view.MyGridView;
import com.hua.view.MyViewPager;
import com.hua.wedget.viewimage.Animations.DescriptionAnimation;
import com.hua.wedget.viewimage.Animations.SliderLayout;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.hua.wedget.viewimage.SliderTypes.TextSliderView;
import com.hua.weget.CircleFlowIndicator;
import com.hua.weget.LayersLayout;
import com.hua.weget.ViewFlow;
import com.listviewaddheader.view.XListViewFooter;
import com.listviewaddheader.view.XListViewHeader;

/**
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 这是homefragment中viewpager的首页
 * 
 */
public class SportFragment extends BaseFragment2 implements OnSliderClickListener{

	protected static final int START_BAR = 9;

	private static final int BANNER_NUM = 3;
	private static final int RESPONSE_OK = 200;
	/**
	 * 弹性scrollview
	 */
	private ElasticScrollView elasticScrollView;

	/**
	 * 横幅的viewpgaer
	 */
	private MyViewPager mViewPager;

	// /////
	private ImageView[] mImageViews;
	private int currentPosition = 0;
	private TextView tv_title;
	/**
	 * 分类漫画 和推荐漫画的 gridView
	 */
	private GridView gv_category;
	private MyGridView gv_recommend;
	/**
	 * 用来计算分ge图片
	 */
	int count;
	private boolean ischange;

	/**
	 * 设计广告副 走动
	 */
	private int currentItem = 0;
	private Timer timer;
	private TimerTask task;
	private boolean isCanel;

	private HandlerTimer handlerTimer;

	/**
	 * 选项其
	 */
	private FragmentTabSwitcherWithoutZorder fragmentSwitcher;
	private String ITEM_BAR = "item_bar";

	private PullToRefreshListView mPullRefreshListView;
	private LinearLayout mHeaderViewLayout;
	private ListView mListView; // 下拉刷新的listview
//	private CustomeListView mCustomeListView;
	private ViewFlow mViewFlow; // 进行图片轮播的viewFlow
	
	/**
	 * 头部的横幅滑动布局文件的另一种实现方法
	 */
    protected SliderLayout mDemoSlider;
    
	private CircleFlowIndicator mCircleFlowIndicator;
	private ViewFlowAdapter mViewFlowAdapter;
	private ProductSlectionAdapter mProductSlectionAdapter;
	// protected ProgressBar mProgressBar;
	/**
	 * 用来存储图片的url
	 */
	protected HashMap<String, String> url_maps;
	/**
	 * 已图片的url作为key 然后保存数据对象
	 */
	protected HashMap<String, NewModle> newHashMap;
	/**
	 * 读取网络数据后 设置到po对象中，形成一个list集合
	 */
	protected List<NewModle> listsModles;
	/**
	 * 自定义图层，用于对触屏事件进行重定向,整个Linearlayout
	 */
	private LayersLayout mLayersLayout;
	private View mContentView;
	private Context mContext;
	private PreferenceHelper mPreferenceHelper;
	/**
	 * 用来做加载第几页的标记
	 */
	private int index = 0;
	/**
	 * bar横幅图片的list
	 */
	private List<Integer> barImageList = new ArrayList<Integer>(
			Constant.BAR_LIST_SIZE);

	private ListView contentListView;
	private HomeSubFragment1_ListViewBaseAdater adapter;
	private List<View> barList = new ArrayList<View>();
	private List<ImageView> mImageViewList = new ArrayList<ImageView>();
	//
	private MyLoadImageTask myLoadImageTask = new MyLoadImageTask();

//	private FixedSpeedScroller mScroller;
	private boolean isRefresh = false;
	private MyOnitemClickListener mOnitemClickListener;
	
//	private CustomeListViewFooter mCustomeListViewFooter;

	
	//********************
	private float mLastY = -1; // save event y
	private Scroller mScroller2; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private CustomeListViewListener2 mListViewListener;

	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private CustomeListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	
	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 600; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 120; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
	
	private boolean isInbottom;
	/**
	 * 第一次进来的时候 不要执行加载更多
	 */
	private boolean isShowFirstIn;
	
	private boolean isFootViewVisible;
	private static final int ShowFootView = 300;
	/**
	 * listview中的item的总个数
	 */
	private int TOTALITEMCOUNT;
	
	/**
	 * 用来判断是否关闭定时器
	 */
	private SharedPreferences mPreferences;

	Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			int what = message.what;
			int numchange = what;
			LogUtils2.i("what==" + what);
			switch (what) {

			case START_BAR:
				if (mViewPager != null) {
					LogUtils2.d("999999999utyuiyiyiuyui");
					mViewPager.setCurrentItem(currentItem);
					LogUtils2.d("mViewPager.getCurrentItem()=="
							+ mViewPager.getCurrentItem());
					adapter.notifyDataSetChanged();

				}

			case RESPONSE_OK:
				String result = (String) message.obj;
				getResult(result);
				mHandler.obtainMessage(ShowFootView).sendToTarget();
				break;

			case ShowFootView:
				if(mFooterView.getVisibility() == View.GONE){
					mFooterView.setVisibility(View.VISIBLE);
				}
				isShowFirstIn = true;
				/**
				 * 停止加载更多 恢复原样
				 */
				stopLoadMore();
				
			break;
			default:
				break;
			}

		};
	};

	/**
	 * 获取数据后刷新页面 重新布局页面数据
	 * 
	 * @param result
	 */
	public void getResult(String result) {
		getMyActivity().setCacheStr("TiYuFragment" + currentPagte, result);
		if (isRefresh) {
			isRefresh = false;
			mProductSlectionAdapter.clear();
			listsModles.clear();
		}
		List<NewModle> list = NewListJson.instance(getActivity())
				.readJsonNewModles(result, Url.TiYuId);
		if (index == 0) {
			initDatasCollections(list);
		} else {
			mProductSlectionAdapter.appendList(list,index,false);
			
		}
		listsModles.addAll(list);
		// mListView.onBottomComplete();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPreferences = getActivity().getSharedPreferences(
				Constant.TIMER_ISCANEL, Context.MODE_PRIVATE);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils2.e("******onCreateView********");
		LogUtils2.e("******index******** == "+index);
		
		init();
		mContentView = inflater.inflate(
				R.layout.product_selection_layout_fragment, null);
		initComponents(mContext, mContentView);
		return mContentView;
	}

	private void initComponents(Context context, View mView) {
		mLayersLayout = (LayersLayout) mView
				.findViewById(R.id.pro_selection_layerslayout);
		mPullRefreshListView = (PullToRefreshListView) mContentView
				.findViewById(R.id.selection_pulltorefreshlistview);
//		mPullRefreshListView.set
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		/**
		 * 设置刷新时 显示的文字
		 */
//		mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel("lastUpdateLabel"); 
//		mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("PULLLABLE");
//		mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("refreshingLabel");
//		mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel("releaseLabel");
//		mListView.addFooterView(new CustomeListViewFooter(getActivity()));
//		mCustomeListView.add
		mListView = mPullRefreshListView.getRefreshableView();
//		mCustomeListView =  (CustomeListView) mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
				
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {

						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						LogUtils2.i("onPullDownToRefresh------");
						isShowFirstIn = false;
						currentPagte = 1;
						index = 0;
						mPreferenceHelper.setPreference("isUpdate", true);
						loadData(getCommonUrl(index + "", Url.TiYuId));
						mDemoSlider.removeAllSliders();
						url_maps.clear();
						mProductSlectionAdapter.clear();
						listsModles.clear();
						if(mFooterView.getVisibility() == View.VISIBLE){
							mFooterView.setVisibility(View.GONE);
						}
					}
				}, 2000);
				
			}
		});

//		mPullRefreshListView.setMode(Mode.BOTH);
//		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
//
//			@Override
//			public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//
//						String label = DateUtils.formatDateTime(getActivity(),
//								System.currentTimeMillis(),
//								DateUtils.FORMAT_SHOW_TIME
//										| DateUtils.FORMAT_SHOW_DATE
//										| DateUtils.FORMAT_ABBREV_ALL);
//						refreshView.getLoadingLayoutProxy()
//								.setLastUpdatedLabel(label);
//						LogUtils2.i("onPullDownToRefresh------");
//						currentPagte = 1;
//						index = 0;
//						mPreferenceHelper.setPreference("isUpdate", true);
//						loadData(getCommonUrl(index + "", Url.TiYuId));
//						mDemoSlider.removeAllSliders();
//						url_maps.clear();
//						mProductSlectionAdapter.clear();
//						listsModles.clear();
//					}
//				}, 2000);
//			}
//
//			@Override
//			public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
//				
////				mFooterView.setVisibility(View.GONE);if
//				if(refreshView.getState().PULL_TO_REFRESH == State.PULL_TO_REFRESH){
//					
//					mListView.removeFooterView(mFooterView);
//				}else if(refreshView.getState().RESET == State.RESET){
//					mListView.addFooterView(mFooterView);
//					
//				}
//				
//				
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//
//						String label = DateUtils.formatDateTime(getActivity(),
//								System.currentTimeMillis(),
//								DateUtils.FORMAT_SHOW_TIME
//										| DateUtils.FORMAT_SHOW_DATE
//										| DateUtils.FORMAT_ABBREV_ALL);
//						refreshView.getLoadingLayoutProxy()
//								.setLastUpdatedLabel(label);
//						mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("上啦查看更多");
//						mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("玩命加载中.....");
//						mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel("放开刷新");
//						LogUtils2.i("onPullDownToRefresh------");
////						currentPagte = 1;
//						index ++;
//						mPreferenceHelper.setPreference("isUpdate", true);
//						loadData(getCommonUrl(index + "", Url.TiYuId));
////						mDemoSlider.removeAllSliders();
////						url_maps.clear();
////						mProductSlectionAdapter.clear();
////						listsModles.clear();
//					}
//				}, 2000);
//			}
//		});
		
		
		LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		/**
		 * 用另一种banner横幅
		 */
		 View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_item, null);
	     mDemoSlider = (SliderLayout) headView.findViewById(R.id.slider);
	     
	     /**
	      * 添加头尾
	      */
	     mListView.addHeaderView(headView);
	     mListView.addFooterView(mFooterView);
	     mFooterView.setVisibility(View.GONE);
	     mListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				
				if (mLastY == -1) {
					mLastY = ev.getRawY();
				}

				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					
					
					mLastY = ev.getRawY();
//					LogUtils2.d("YYYYYYYYYYYYYYYY==mLastY= "+mLastY);
					break;
				case MotionEvent.ACTION_MOVE:
					final float deltaY = ev.getRawY() - mLastY;
					mLastY = ev.getRawY();
					 if (isInbottom
							&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
						// last item, already pulled up or want to pull up.
						updateFooterHeight(-deltaY / OFFSET_RADIO);
					}
					break;
				default:
					mLastY = -1; // reset
					LogUtils2.i("************isInbottom==***== "+isInbottom);
					LogUtils2.i("************mListView.getLastVisiblePosition()==***== "+mListView.getLastVisiblePosition());
					LogUtils2.e("************TOTALITEMCOUNT==***== "+TOTALITEMCOUNT);
				if (isShowFirstIn) {
					
						// invoke load more.
						if (mEnablePullLoad
						    && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
						    && !mPullLoading) {
							
							LogUtils2.e(" mFooterView.getBottomMargin()== "+ mFooterView.getBottomMargin());
							
							startLoadMore();
						}
						LogUtils2.i("*********resetFooterHeight********");
						resetFooterHeight();
					}
					break;
				}
				
				return false;
			}
		});
	     
		
		mProductSlectionAdapter = ProductSlectionAdapter.instanceAdapter(mContext,index);
		
		mListView.setAdapter(mProductSlectionAdapter);
//		mCustomeListView.setAdapter(mProductSlectionAdapter);
		mListView.setOnItemClickListener(mOnitemClickListener);
//		mCustomeListView.setOnItemClickListener(mOnitemClickListener);
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				TOTALITEMCOUNT = totalItemCount;
				if(mListView.getFirstVisiblePosition() == 0){
//					this.onScrollStateChanged(view, 2);
					/**
					 * 停止滑动
					 */
					mListView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
				}
//				
				if(view.getLastVisiblePosition() == totalItemCount - 1 && isShowFirstIn){
					LogUtils2.e("view.getLastVisiblePosition()=="+view.getLastVisiblePosition());
					LogUtils2.e("totalItemCount = "+totalItemCount);
					isInbottom = true;
				}else {
//					isInbottom = false;
				}
				
				
			}
		});
		loadData(getCommonUrl(index + "", Url.TiYuId));
		
		
		
		
		
	}

	/**
	 * 根据url加载数据
	 * 
	 * @param commonUrl
	 */
	private void loadData(String commonUrl) {

		if (getMyActivity().hasNetWork()) {
			LogUtils2.d("**********index+++== "+index);
			loadNewList(commonUrl);
		} else {
			mPullRefreshListView.onRefreshComplete();
			// mProgressBar.setVisibility(View.GONE);
			getMyActivity().showShortToast(getString(R.string.not_network));
			String result = getMyActivity().getCacheStr(
					"TiYuFragment" + currentPagte);
			if (!StringUtils.isEmpty(result)) {
				Message msg = new Message();
				msg.what = RESPONSE_OK;
				msg.obj = result;
				mHandler.sendMessage(msg);
				// getResult(result);
			}
		}

	}

	/**
	 * 
	 * @param url
	 */
	// TODO
	private void loadNewList(String url) {
		String result;
		try {
			new GetDataTask().execute(url);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils2.e("error -----");
		}
	}

	/**
	 * 初始化 变量
	 */
	protected void init() {
		listsModles = new ArrayList<NewModle>();
		url_maps = new HashMap<String, String>();
		newHashMap = new HashMap<String, NewModle>();
		mOnitemClickListener = new MyOnitemClickListener();
		mPreferenceHelper = PreferenceHelper.getInstance(mContext);
		mFooterView = new CustomeListViewFooter(mContext);
		mScroller2 = new Scroller(mContext, new DecelerateInterpolator());
		setPullLoadEnable(true);
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(CustomeListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LogUtils2.i("******setPullLoadEnable***********");
					startLoadMore();
				}
			});
		}
	}
	
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(CustomeListViewFooter.STATE_LOADING);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				LogUtils2.i("onPullDownToRefresh------");
				currentPagte ++;
				index = index + 20;
				mPreferenceHelper.setPreference("isUpdate", true);
				loadData(getCommonUrl(index + "", Url.TiYuId));
				isInbottom = false;
//				stopLoadMore();
				
			}
		}, 2000);
		
//		if (mListViewListener != null) {
//			LogUtils2.i("******startLoadMore************");
//			mListViewListener.onLoadMore();
//		}
	}
	

	/**
	 * 更改状态
	 * @param delta
	 */
	private void updateFooterHeight(float delta) {
		LogUtils2.i("can updateFooterHeight************");
		int height = mFooterView.getBottomMargin() + (int) (delta*0.85);
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(CustomeListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(CustomeListViewFooter.STATE_NORMAL);
			}
		}
		LogUtils2.i("can updateFooterHeight***********height=== "+height);
			
			mFooterView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		LogUtils2.i("can resetFooterHeight************bottomMargin= "+bottomMargin);
		if (bottomMargin > 0) {
			LogUtils2.e("can resetFooterHeight************bottomMargin= "+bottomMargin);
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller2.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			mFooterView.setBottomMargin(mScroller2.getCurrY());
			mContentView.postInvalidate();
			
		}
	}
	
//	/**
//	 * stop refresh, reset header view.
//	 */
//	public void stopRefresh() {
//		if (mPullRefreshing == true) {
//			mPullRefreshing = false;
//			resetHeaderHeight();
//		}
//	}
	
	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}
	
	
	private void initDatasCollections(List<NewModle> newModles) {

		if (!isNullString(newModles.get(0).getImgsrc()))
			newHashMap.put(newModles.get(0).getImgsrc(), newModles.get(0));
		if (!isNullString(newModles.get(1).getImgsrc()))
			newHashMap.put(newModles.get(1).getImgsrc(), newModles.get(1));
		if (!isNullString(newModles.get(2).getImgsrc()))
			newHashMap.put(newModles.get(2).getImgsrc(), newModles.get(2));
		if (!isNullString(newModles.get(3).getImgsrc()))
			newHashMap.put(newModles.get(3).getImgsrc(), newModles.get(3));

		if (!isNullString(newModles.get(0).getImgsrc()))
			url_maps.put(newModles.get(0).getTitle(), newModles.get(0)
					.getImgsrc());
		if (!isNullString(newModles.get(1).getImgsrc()))
			url_maps.put(newModles.get(1).getTitle(), newModles.get(1)
					.getImgsrc());
		if (!isNullString(newModles.get(2).getImgsrc()))
			url_maps.put(newModles.get(2).getTitle(), newModles.get(2)
					.getImgsrc());
		if (!isNullString(newModles.get(3).getImgsrc()))
			url_maps.put(newModles.get(3).getTitle(), newModles.get(3)
					.getImgsrc());

		for (String name : url_maps.keySet()) {
			 TextSliderView textSliderView = new
			 TextSliderView(getActivity());
			 textSliderView.setOnSliderClickListener(this);
			 textSliderView
			 .description(name)
			 .image(url_maps.get(name));
			
			 textSliderView.getBundle()
			 .putString("extra", name);
			 mDemoSlider.addSlider(textSliderView);
		}

		 mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
		 mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
		 mDemoSlider.setCustomAnimation(new DescriptionAnimation());
		LogUtils2.i("*****mViewFlowAdapter.setAdapterData********");
//		mViewFlowAdapter.setAdapterData(newHashMap, url_maps);
		LogUtils2.e("");
		mProductSlectionAdapter.appendList(newModles,index,true);
	}

	// ///
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 获得服务端广告图片，这里我们就简单的直接取本地数据
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils2.e("******onResume********");
		LogUtils2.e("777777777777777777777777777777");

		if (adapter != null) {
			if (!mPreferences.getBoolean("isCanel", false)) {
				LogUtils2.e("oooooooooooooooo");
				adapter.startViewPagerTimer2();
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (adapter != null) {
			LogUtils2.e("pause--------------");
			if (mPreferences.getBoolean("isCanel", false)) {
				adapter.stopViewPagerTimer2();
			}

			LogUtils2.d("setviewpager--------------");

		}

	}

	/**
	 * 初始化 子view中的content
	 * 
	 * @param context
	 */
	private void initSubView(Context context, View view) {
		// viewPager = (ViewPager) view.findViewById(R.id.vp_ad2);
		// tv_title = (TextView) view.findViewById(R.id.tv_title2);
		getAdData();
		// createPoint(view);
		// adapter = new HomeSubFragment1_ListViewBaseAdater(getActivity(),
		// barList,barImageList);
		mViewPager = (MyViewPager) view.findViewById(R.id.mBannerViewPager);

		// contentListView.setAdapter(adapter);
		// gv_c
	}

	//
	/**
	 * 获得广告数据
	 */
	private void getAdData() {

		if (Constant.bannerImageViews.size() >= 0) {
			// barImageList =
			List<Integer> list = new ArrayList<Integer>();
			// List<Integer> barImageList = new
			// ArrayList<Integer>(Constant.BAR_LIST_SIZE);
			list.add(R.drawable.huoying);
			list.add(R.drawable.caomao);
			list.add(R.drawable.yinhun);
			list.add(R.drawable.diguang);
			list.add(R.drawable.jianxin);

			barImageList = list;
			mImageViewList = Constant.bannerImageViews;
			mHandler.sendMessage(mHandler.obtainMessage(BANNER_NUM));

		}
	}
	//
	/**
	 * 获得分类 gridView分类数据
	 */
	private void getCategoryData() {

	}

	//
	/**
	 * 获取gridView推荐漫画的数据
	 */
	private void getRecommendData() {
	}

	// /**
	// * 初始化 选想起
	// */
	public void initFragmentSwitcher() {
		FragmentTabSwitcherFeed feed = new FragmentTabSwitcherFeed() {
			@Override
			public Fragment newRootFragment(String tag) {
				if (ITEM_BAR.equalsIgnoreCase(tag)) {
					return new HotFragment();
				}

				return null;
			}

			@Override
			public LinkedHashSet<String> getRootFragmentTags() {
				return FragmentUtils.makeRootFragmentTags(ITEM_BAR);
			}

		};
		fragmentSwitcher = new FragmentTabSwitcherWithoutZorder(getActivity(),
				R.id.rightLayout, feed);
	}

	//
	/**
	 * 改变tag 显示不同的fargment
	 * 
	 * @param tag
	 */
	public void changeFragment(String tag) {
		// if(CUSTOMER_LOGIN_TAG.equalsIgnoreCase(tag)){
		// image1 = (ImageView)getView().findViewById(R.id.arrow1);
		// }else if(TERMS_CONDITIONS_TAG.equalsIgnoreCase(tag)){
		// image1 = (ImageView)getView().findViewById(R.id.arrow4);
		// }
		// image1.setSelected(true);
		if (false) {
			fragmentSwitcher.switchTab(tag);
		} else {
			if (ITEM_BAR.equalsIgnoreCase(tag)) {
				MTNApplication.startFragment(getCurFragment(),
						new HotFragment());
			}
		}
	}

	public Fragment getCurFragment() {
		return this;
	}

	//

	private class GetDataTask extends AsyncTask<String, Void, String> {
		// 后台处理部分
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				result = HttpUtil.getByHttpClient(mContext, params[0]);

			} catch (Exception e) {
				e.printStackTrace();
				LogUtils2.e("GetDataTask get Data error ----");
			}
			LogUtils2.i("get data from network result == " + result);
			return result;
		}

		// 这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		// 根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
		@Override
		protected void onPostExecute(String result) {
			// 在头部增加新添内容
			// Toast.makeText(getActivity(), "lal", 300).show();
			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
			if (mPullRefreshListView != null) {
				Message msg = new Message();
				msg.obj = result;
				msg.what = RESPONSE_OK;
				mHandler.sendMessage(msg);
				mPullRefreshListView.onRefreshComplete();
			}
			
		}
	}

	/**
	 * 
	 * @author zero
	 * 
	 */
	class MyOnitemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NewModle newModle = listsModles.get(position-2);
			enterDetailActivity(newModle);
		}

	}

	// protected void onItemClick(int position) {
	// NewModle newModle = listsModles.get(position - 1);
	// enterDetailActivity(newModle);
	// }

	public void enterDetailActivity(NewModle newModle) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("newModle", newModle);
		Class<?> class1;
		if (newModle.getImagesModle() != null
				&& newModle.getImagesModle().getImgList().size() > 1) {
			// class1 = ImageDetailActivity_.class;
			class1 = ImageDetailActivity_.class;
		} else {
			class1 = DetailsActivity_.class;
		}
		((BaseActivity2) getActivity()).openActivity(class1, bundle, 0);
	}

	public int getIndex(){
		LogUtils2.d("********index==***== "+index);
		return index;
	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		  NewModle newModle = newHashMap.get(slider.getUrl());
	      enterDetailActivity(newModle);		
	}
	
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mPreferenceHelper.setPreference("isUpdate", false);
		LogUtils2.e("******onDestroy************");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPreferenceHelper.setPreference("isUpdate", false);
		LogUtils2.e("******onDestroy************");
	}
	
	
	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface CustomeListViewListener2 {
		public void onRefresh();

		public void onLoadMore();
	}
	
	//第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动    
    //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）    
    //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动  
	
	
	
	
}
