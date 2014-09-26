package com.hua.homefragment.subfragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hua.activity.MTNApplication;
import com.hua.activity.R;
import com.hua.adapter.HomeSubFragment1_ListViewBaseAdater;
import com.hua.androidos.HandlerTimer;
import com.hua.contants.Constant;
import com.hua.util.FragmentUtils;
import com.hua.util.LogUtils2;
import com.hua.util.FragmentUtils.FragmentTabSwitcherFeed;
import com.hua.util.FragmentUtils.FragmentTabSwitcherWithoutZorder;
import com.hua.util.MyImageLoader.MyLoadImageTask;
import com.hua.view.ElasticScrollView;
import com.hua.view.MyGridView;
import com.hua.view.MyViewPager;

/**
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 这是homefragment中viewpager的首页
 * 
 */
public class Fragment02 extends Fragment {
	
	protected static final int START_BAR = 9;

	private static final int BANNER_NUM = 3;

	/**
	 * 弹性scrollview
	 */
	private ElasticScrollView elasticScrollView;
	
	/**
	 * 横幅的viewpgaer 
	 */
	private MyViewPager mViewPager;
	
	///////
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
	private int currentItem=0;
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
	private LinearLayout headerViewLayout;
	/**
	 * bar横幅图片的list
	 */
	private List<Integer> barImageList = new ArrayList<Integer>(Constant.BAR_LIST_SIZE);
	
	private ListView contentListView;
	private HomeSubFragment1_ListViewBaseAdater adapter;
	private List<View> barList = new ArrayList<View>();
	private List<ImageView> mImageViewList = new ArrayList<ImageView>();
//	
	private MyLoadImageTask myLoadImageTask = new MyLoadImageTask();
	
	/**
	 * 用来判断是否关闭定时器
	 */
	private SharedPreferences mPreferences;
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message message) {
			int what = message.what;
    		int numchange = what;
    		LogUtils2.i("what=="+what);
    		
    		switch (what) {
			
				case START_BAR:
					if(mViewPager !=null){
						LogUtils2.d("999999999utyuiyiyiuyui");
						mViewPager.setCurrentItem(currentItem);
						LogUtils2.d("mViewPager.getCurrentItem()=="+mViewPager.getCurrentItem());
						adapter.notifyDataSetChanged();
						
					}
				case BANNER_NUM:
					
					LogUtils2.d("wwwwww+=="+Constant.bannerImageViews.size());
					LogUtils2.d("bbbbbb+=="+Constant.bannerBitmaps.size());
//					adapter = new HomeSubFragment1_ListViewBaseAdater(getActivity(), Constant.bannerImageViews,barImageList);
					adapter = new HomeSubFragment1_ListViewBaseAdater(getActivity(), mImageViewList,barImageList,Constant.bannerBitmaps);
					contentListView.setAdapter(adapter);
					if(!mPreferences.getBoolean("isCanel", false));
					adapter.startViewPagerTimer();
					
					adapter.notifyDataSetChanged();
					break;
			default:
				break;
			}
			
		};
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	mPreferences = getActivity().getSharedPreferences(Constant.TIMER_ISCANEL, Context.MODE_PRIVATE);
    	
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_subfragment02, null);
		
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.home_sub_fragment02_listview);
		contentListView = mPullRefreshListView.getRefreshableView();
		
	     //造1个 假数据  
        for (int i = 0; i <80; i++) {  
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.icon_1_n);
            barList.add(imageView);  
        }

        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});
		
		headerViewLayout = (LinearLayout) LayoutInflater.from(getActivity()).
				inflate(R.layout.home_subfragment02_header_item, null);
//		headerViewLayout = (LinearLayout) subView;
		
		initSubView(getActivity(),headerViewLayout);
		
		   /**
		    * 设置广告横幅走动
		    */
		
		return view;
	}

//	///
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 获得服务端广告图片，这里我们就简单的直接取本地数据
		super.onActivityCreated(savedInstanceState);
//		contentListView.setAdapter(adapter);
//		if(!adapter.isCanel())
//		adapter.startViewPagerTimer();
//		mPullRefreshListView.set
		///
//		getCategoryData();
		///
//		getRecommendData();
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		LogUtils2.e("777777777777777777777777777777");
		
		if(adapter != null){
			if(!mPreferences.getBoolean("isCanel", false)){
				LogUtils2.e("oooooooooooooooo");
				adapter.startViewPagerTimer();
			}
		}
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(adapter != null){
			LogUtils2.e("pause--------------");
			if(mPreferences.getBoolean("isCanel", false)){
				adapter.stopViewPagerTimer();
			}
			
			LogUtils2.d("setviewpager--------------");
//			HomeSubFragment1_ListViewBaseAdater.setViewPager();
//			LogUtils2.e("pause=====mViewPager="+mViewPager.getCurrentItem());
			
		}
		
		
		
	}
	
	/**
	 * 初始化 子view中的content
	 * @param context
	 */
	private void initSubView(Context context,View view) {
		// TODO Auto-generated method stub
//		viewPager = (ViewPager) view.findViewById(R.id.vp_ad2);
//		tv_title = (TextView) view.findViewById(R.id.tv_title2);
		getAdData();
//		createPoint(view);
//		adapter = new HomeSubFragment1_ListViewBaseAdater(getActivity(), barList,barImageList);
		mViewPager = (MyViewPager) view.findViewById(R.id.vp_ad2);
//		contentListView.setAdapter(adapter);
//		gv_c
	}
	
//	
	/**
	 * 获得广告数据
	 */
	private void getAdData() {
		
		if(Constant.bannerImageViews.size() >= 0){
//			barImageList = 
			List<Integer> list = new ArrayList<Integer>();
//			List<Integer> barImageList = new ArrayList<Integer>(Constant.BAR_LIST_SIZE);
			list.add(R.drawable.huoying);
			list.add(R.drawable.caomao);
			list.add(R.drawable.yinhun);
			list.add(R.drawable.diguang);
			list.add(R.drawable.jianxin);
			
			barImageList = list;
			mImageViewList = Constant.bannerImageViews;
			handler.sendMessage(handler.obtainMessage(BANNER_NUM));
			
		}
	}
//	
//	
	/**
	 * 获得分类 gridView分类数据
	 */
	private void getCategoryData() {
		
//		gv_category.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		LogUtils2.d(" getCategoryData()......");
//		gv_category.setAdapter(new HomeSubFragment1_GridViewAdater(getActivity(), new ShopAppApplication().mDatas));
		
		
	}
//	
	/**
	 * 获取gridView推荐漫画的数据
	 */
	private void getRecommendData() {
//		final List<CategoryInfo> list2 = new ArrayList<CategoryInfo>();
//		for (int i = 0; i < Constant.recommend_icon.length; i++) {
//			CategoryInfo categoryInfo = new CategoryInfo();
//			categoryInfo.setIcon(Constant.recommend_icon[i]);
//			categoryInfo.setMsg(Constant.recommend_msg[i]);
//			list2.add(categoryInfo);
//		}
//		gv_recommend.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		gv_recommend.setAdapter(new HF01_RecommendAdapter(getActivity(), list2));
//		gv_recommend.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "您选中："+list2.get(position).getMsg(), 300).show();
//				changeFragment(ITEM_BAR);
//			}
//		});
	}
//	
	
	
//	
//	/**
//	 * 初始化 选想起
//	 */
	  public void initFragmentSwitcher(){
			FragmentTabSwitcherFeed feed = new  FragmentTabSwitcherFeed() {
				@Override
				public Fragment newRootFragment(String tag) {
					if(ITEM_BAR.equalsIgnoreCase(tag)){
						return new HotFragment();
					}
					
					return null;
				}
				@Override
				public LinkedHashSet<String> getRootFragmentTags() {
					return FragmentUtils.makeRootFragmentTags(ITEM_BAR);
				}
				
			};
			fragmentSwitcher = new FragmentTabSwitcherWithoutZorder(getActivity(), R.id.rightLayout, feed);
		}
//	  
	   /**
	     * 改变tag 显示不同的fargment
	     * @param tag
	     */
		public void changeFragment(String tag) {
//			if(CUSTOMER_LOGIN_TAG.equalsIgnoreCase(tag)){
//				image1 = (ImageView)getView().findViewById(R.id.arrow1);
//			}else if(TERMS_CONDITIONS_TAG.equalsIgnoreCase(tag)){
//				image1 = (ImageView)getView().findViewById(R.id.arrow4);
//			}
//			image1.setSelected(true);
			if (false) {
				fragmentSwitcher.switchTab(tag);
			} else {
				if (ITEM_BAR.equalsIgnoreCase(tag)) {
					MTNApplication.startFragment(getCurFragment(), new HotFragment());
				}
			}
		}
	  
		public Fragment getCurFragment(){
			return this;
		}
//	
	
	private class GetDataTask extends AsyncTask<Void, Void, String> {
		// 后台处理部分
		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			String str = "Added after refresh..." + (count++) + " add";
			return str;
		}

		// 这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		// 根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
		@Override
		protected void onPostExecute(String result) {
			// 在头部增加新添内容
//			mListItems.addFirst(result);

			// 通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
//			mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			Toast.makeText(getActivity(), "lal", 300).show();
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
		}
	}
	
	
}
