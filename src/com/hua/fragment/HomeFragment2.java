package com.hua.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.app.BaseFragment;
import com.hua.utils.LogUtils2;
import com.hua.view.ElasticScrollView;
import com.hua.view.MyScrollView;
import com.hua.wiget.TopIndicator;
import com.hua.wiget.TopIndicator.OnTopIndicatorListener;
import com.hua.wiget.TopIndicator2;
import com.hua.wiget.TopIndicator2.OnClickTopIndicatorListener;

public class HomeFragment2 extends BaseFragment implements /*OnTopIndicatorListener,*/OnClickTopIndicatorListener{

	public static final String TAG = "HomeFragment";
	private static final int PagerCount = 4;
	private Activity mActivity;
	private TextView mTitleTv;
	private ViewPager mViewPager;
	private TabPagerAdapter mPagerAdapter;
//	private TopIndicator mTopIndicator;
	private TopIndicator2 mTopIndicator2;
	private int [] images ={R.drawable.xianjian1,R.drawable.xianjian2,R.drawable.xianjian1,R.drawable.xianjian2};
	private static HomeFragment2 newInstance(){
		HomeFragment2 homeFragment = new HomeFragment2();
		return homeFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	//
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
	
	}
	
	private void initViews(View view) {
		// TODO Auto-generated method stub
		
		LogUtils2.i("initViews......");
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		mPagerAdapter = new TabPagerAdapter(getFragmentManager());
		
//		mTopIndicator = (TopIndicator) view.findViewById(R.id.top_indicator);
		mTopIndicator2 = (TopIndicator2) view.findViewById(R.id.top_indicator);
		
		/**
		 * 注册点击标题变化的监听器
		 */
//		mTopIndicator.setOnTopIndicatorListener(this);
		mTopIndicator2.setOnClickTopIndicatorListener(this);
	}

	/**
	 * 
	 */
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		initDisplay();
	
	}
	
	private void initDisplay() {
		// TODO Auto-generated method stub
		
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.invalidate();
		mPagerAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getActivity().getSharedPreferences("mUnderLineFromX", getActivity().MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("mUnderLineFromX", 0);
		editor.commit();
		
		super.onDestroy();
	}
	
	
	/**
	 * 
	 */
	class TabPagerAdapter extends PagerAdapter implements OnPageChangeListener{

		
		
		public TabPagerAdapter(FragmentManager fragmentManager) {
			// TODO Auto-generated constructor stub
			mViewPager.setOnPageChangeListener(this);
		}

		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PagerCount;
//			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			LogUtils2.i("instantiateItem..........position="+position);
			if(position == 3){
				LogUtils2.i("tuangou ---------------------------");
				
//				LinearLayout tuangouLayout = (LinearLayout) LayoutInflater.from(getActivity()).
//						inflate(R.layout.homefragment_tuangou_, null);
//				ElasticScrollView tempScrollview = (ElasticScrollView) tuangouLayout.findViewById(R.id.homefragment_scrollview4);
				 TuanGouFragment tuanGouFragment = new TuanGouFragment();
				 View historyView = LayoutInflater.from(getActivity()).inflate(R.layout.homefragment_tuangou_, null);
				 MyScrollView myScrollView = (MyScrollView) LayoutInflater.from(getActivity()).
						inflate(R.layout.fragment_tuangou, null);
				LogUtils2.e("myScrollView===="+myScrollView);
				LogUtils2.e("tuanGouFragment===="+tuanGouFragment.getView());
				
//				tempScrollview.addChild(myScrollView, 1);
				
				container.addView(myScrollView);
				return myScrollView;
			}
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(images[position]);
			container.addView(imageView);
			
//			container.addView(imageView);
			
			return imageView;//super.instantiateItem(container, position);
		}
		
		//
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
//			super.destroyItem(container, position, object);
			LogUtils2.d("position=="+position);
			container.removeView((View)object);
			
		}


		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}


		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}


		@Override
		public void onPageSelected(int arg0) {
//			mTopIndicator.setTabsDisplay(getActivity(), arg0);
			LogUtils2.d("change indicator by pageChange********");
			mTopIndicator2.setTabsDisplay(getActivity(), arg0);
			LogUtils2.d("arg0==="+arg0);
			
		}
		
	}

/*	@Override
	public void onIndicatorSelected(int index) {
		mViewPager.setCurrentItem(index);
		
	}*/

	@Override
	public void onClickIndicatorSelected(int index) {
		// TODO Auto-generated method stub
		LogUtils2.d("index===="+index);
		mViewPager.setCurrentItem(index);
	}
}
