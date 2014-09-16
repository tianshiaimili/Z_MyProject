package com.hua.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.MTNApplication;
import com.hua.activity.R;
import com.hua.app.BaseFragment;
import com.hua.homefragment.subfragment.Fragment01;
import com.hua.homefragment.subfragment.ContactsFragment;
import com.hua.homefragment.subfragment.FoundFragment;
import com.hua.homefragment.subfragment.FourFragment;
import com.hua.homefragment.subfragment.Fragment02;
import com.hua.util.LogUtils2;
import com.hua.view.MyScrollView;
import com.hua.wiget.PagerSlidingTabStrip;
import com.hua.wiget.TopIndicator2;
import com.hua.wiget.TopIndicator2.OnClickTopIndicatorListener;

public class HomeFragment extends BaseFragment implements
		/* OnTopIndicatorListener, */OnClickTopIndicatorListener ,OnPageChangeListener{

	public static final String TAG = "HomeFragment";
	private static final int PagerCount = 4;
	private Activity mActivity;
	private TextView mTitleTv;
	private ViewPager mViewPager;
	 private TabPagerAdapter mPagerAdapter;
	// private TopIndicator mTopIndicator;
	private TopIndicator2 mTopIndicator2;
	private PagerSlidingTabStrip tabs;
	private int[] images = { R.drawable.xianjian1, R.drawable.xianjian2,
			R.drawable.xianjian1, R.drawable.xianjian2 };

	private Fragment currentFragment;

	public Fragment getCurFragment() {
		return currentFragment;
	}

	public void setCurrentFragment(Fragment fragment) {
		this.currentFragment = fragment;
	}

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	/**
	 * 聊天界面的Fragment
	 */
//	private Fragment01 chatFragment;
	private Fragment02 chatFragment;

	/**
	 * 发现界面的Fragment
	 */
	private FoundFragment foundFragment;

	/**
	 * 通讯录界面的Fragment
	 */
	private ContactsFragment contactsFragment;

	/**
	 * 第四个fragment
	 */
	private FourFragment fourFragment;

	/**
	 * 标题组
	 */
	private final String[] titles = { "精选", "发现", "榜单", "团购" };

	private MyPagerAdapter myPagerAdapter;
	/**
	 * 当前viewpager的item
	 */
	int currentViewPItem;

	private static HomeFragment newInstance() {
		HomeFragment homeFragment = new HomeFragment();
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
		LogUtils2.d("*****onCreate********");
		dm = getResources().getDisplayMetrics();
		setCurrentFragment(this);
	}

	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtils2.d("*****onCreateView********");
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		return view;// super.onCreateView(inflater, container,
					// savedInstanceState);
	}

	//
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		LogUtils2.d("*****onViewCreated********");
		initViews(view);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		myPagerAdapter.notifyDataSetChanged();
	}

	private void initViews(View view) {
		// TODO Auto-generated method stub

		LogUtils2.i("initViews......");
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		 mPagerAdapter = new TabPagerAdapter(getFragmentManager());

		myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(myPagerAdapter);

//		mViewPager.setCurrentItem(3);
		// mTopIndicator = (TopIndicator) view.findViewById(R.id.top_indicator);
		// mTopIndicator2 = (TopIndicator2)
		// view.findViewById(R.id.top_indicator);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.top_indicator);
		tabs.setViewPager(mViewPager);

		setTabsValue();

		/**
		 * 注册点击标题变化的监听器
		 */
		// mTopIndicator.setOnTopIndicatorListener(this);

		// mTopIndicator2.setOnClickTopIndicatorListener(this);
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		// tabs.setDividerColor(Color.TRANSPARENT);
		tabs.setDividerColor(Color.WHITE);
		/**
		 * 设置Tab底部线的高度 这个方法是转变为标准尺寸的一个函数，例如 　　 int size =
		 * (int)TypedValue.applyDimension(TypedValue. 　　 COMPLEX_UNIT_SP, 20, 　　
		 * context.getResources().getDisplayMetrics()); 　
		 * 　这里COMPLEX_UNIT_SP是单位，20是数值，也就是20sp。
		 */
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#45c01a"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	/**
	 * 
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// initDisplay();

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		LogUtils2.i("******onStart******");

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils2.i("******onStop******");

	}

	private void initDisplay() {
		// TODO Auto-generated method stub

		// mViewPager.setAdapter(mPagerAdapter);
		// mViewPager.invalidate();
		// mPagerAdapter.notifyDataSetChanged();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"mUnderLineFromX", getActivity().MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("mUnderLineFromX", 0);
		editor.commit();

		super.onDestroy();
	}

	/**
	 * 
	 */
	class TabPagerAdapter extends PagerAdapter{

		
		
		public TabPagerAdapter(FragmentManager fragmentManager) {
			// TODO Auto-generated constructor stub
//			mViewPager.setOnPageChangeListener(this);
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
	}
	
	 

	class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			
			LogUtils2.i("******instantiateItem**********");
			
			
			return super.instantiateItem(container, position);
		}
		
		
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub

			LogUtils2.e("position==" + position);
			switch (position) {

			case 0:
				LogUtils2.i("******chatFragment******");
				if (chatFragment == null) {
					LogUtils2.i("******chatFragment******");
//					chatFragment = new Fragment01();
					chatFragment = new Fragment02();
					// MTNApplication.startFragment(getCurFragment(),
					// chatFragment);
				}
				currentViewPItem = position;
				return chatFragment;
			case 1:
				LogUtils2.i("******foundFragment******");
				if (foundFragment == null) {
					LogUtils2.i("******foundFragment******");
					foundFragment = new FoundFragment();
					// MTNApplication.startFragment(getCurFragment(),
					// foundFragment);
				}
				currentViewPItem = position;
				return foundFragment;
			case 2:
				LogUtils2.i("******contactsFragment******");
				if (contactsFragment == null) {
					LogUtils2.i("******contactsFragment******");
					contactsFragment = new ContactsFragment();
				}
				currentViewPItem = position;
				return contactsFragment;

			case 3:
				LogUtils2.i("******fourFragment******");
				if (fourFragment == null) {
					LogUtils2.i("******fourFragment******");
					fourFragment = new FourFragment();
				}
				currentViewPItem = position;
				return fourFragment;

			default:
				return null;
			}

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			super.destroyItem(container, position, object);
			LogUtils2.w("******destroyItem********");
//			transaction.detach((Fragment)(object));
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return titles.length;
		}

		
		//
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return titles[position];
		}

	}

	/*class TabPagerAdapter extends PagerAdapter {

		public TabPagerAdapter(FragmentManager fragmentManager) {
			// TODO Auto-generated constructor stub
			// mViewPager.setOnPageChangeListener(this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PagerCount;
			// return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			LogUtils2.i("instantiateItem..........position=" + position);

			switch (position) {

			case 0:
				LogUtils2.i("******chatFragment******");
				if (chatFragment == null) {
					LogUtils2.i("******chatFragment******");
					chatFragment = new ChatFragment();
					// MTNApplication.startFragment(getCurFragment(),
					// chatFragment);
				}
				currentViewPItem = position;
				container.addView(chatFragment.getView());
				return chatFragment.getView();
			case 1:
				LogUtils2.i("******foundFragment******");
				if (foundFragment == null) {
					LogUtils2.i("******foundFragment******");
					foundFragment = new FoundFragment();
					// MTNApplication.startFragment(getCurFragment(),
					// foundFragment);
				}
				currentViewPItem = position;
				container.addView(foundFragment.getView());
				return foundFragment.getView();
			case 2:
				LogUtils2.i("******contactsFragment******");
				if (contactsFragment == null) {
					LogUtils2.i("******contactsFragment******");
					contactsFragment = new ContactsFragment();
				}
				currentViewPItem = position;
				container.addView(contactsFragment.getView());
				return contactsFragment.getView();

			case 3:
				LogUtils2.i("******fourFragment******");
				if (fourFragment == null) {
					LogUtils2.i("******fourFragment******");
					fourFragment = new FourFragment();
					LogUtils2.e("******fourFragment******"+fourFragment);
					
				}
				currentViewPItem = position;
				container.addView(fourFragment.getView());
				return fourFragment.getView();

			default:
				return null;
			}
			
			// container.addView(imageView);

//			return imageView;// super.instantiateItem(container, position);
		}

		//
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
			LogUtils2.d("position==" + position);
			container.removeView((View) object);

		}
	}*/

	
	@Override
	public void onClickIndicatorSelected(int index) {
		// TODO Auto-generated method stub
		LogUtils2.d("index====" + index);
		mViewPager.setCurrentItem(index);
		currentViewPItem = index;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
