package com.hua.settingfragment.subfragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.adapter.SettingPagerAdapterFragmentAdapter;


public class ShowSettingPagerAdpterFragment extends Fragment implements
		View.OnClickListener {

	ViewPager viewpager;
	Button[] btnArray;
	SettingPagerAdapterFragmentAdapter adapter;
	private int currentPageIndex = 0;
	private ArrayList<Fragment> datas;
	
	SettingPagerAdapterFragment1 f1=null;
	SettingPagerAdapterFragment2 f2=null;
	SettingPagerAdapterFragment3 f3=null;
	

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
//		setContentView(R.layout.show_webpage_fragment);
		
		
//		viewpager = (ViewPager)this.findViewById(R.id.viewPager);
//		
//		adapter = new MainFragmentPagerAdapter(this.getSupportFragmentManager(), null);
//		viewpager.setAdapter(adapter);
//		
//		
//		setupView();
//		addListener();
//		setButtonColor();
//		viewpager.setCurrentItem(2);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.show_webpage_fragment, null);
		
		viewpager = (ViewPager)view.findViewById(R.id.viewPager);
		
		adapter = new SettingPagerAdapterFragmentAdapter(getActivity().getSupportFragmentManager(), null);
		viewpager.setAdapter(adapter);
		setupView(view);
		addListener();
		setButtonColor();
		viewpager.setCurrentItem(2);
		
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void setButtonColor() {
		for (int i = 0; i < btnArray.length; i++) {
			if (i == currentPageIndex) {
				btnArray[i].setTextColor(Color.parseColor("#99CC00"));
			} else {
				btnArray[i].setTextColor(0xFF000000);
			}
			
		}
	}

	
	long oldTime=0;
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		long currentTime=System.currentTimeMillis();
//		
//		if (keyCode == KeyEvent.KEYCODE_BACK && currentTime-oldTime>3*1000 ) {
//			Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
//			oldTime=System.currentTimeMillis();
//			return true;
//		}else if (keyCode == KeyEvent.KEYCODE_BACK && currentTime-oldTime<3*1000) {
//			oldTime=0;
//			finish();
//			return true;
//		}
//		
//		return super.onKeyDown(keyCode, event);
//	}
	
	
	

	private void addListener() {
		viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int index) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int pageIndex) {
				currentPageIndex = pageIndex;
				setButtonColor();
			}
		});

		for (Button btn : btnArray) {
			btn.setOnClickListener(this);
		}
	}
	
	

	private void setupView(View view) {
		datas = new ArrayList<Fragment>();
		datas.add(f1 = new SettingPagerAdapterFragment1());
		datas.add(f2 = new SettingPagerAdapterFragment2());
		datas.add(f3 = new SettingPagerAdapterFragment3());
		
		
		adapter.setDatas(datas);
		adapter.notifyDataSetChanged();

		
		btnArray=new Button[]{
				(Button)view.findViewById(R.id.btnFriendList),
				(Button)view.findViewById(R.id.btnGroupChat),
				(Button)view.findViewById(R.id.btnTopicList),
		};
	}

	
//	public void doOnClick(View v) {
//		f1.doOnClick(v);
//	}
	
	
	
	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
				case R.id.btnFriendList:
					currentPageIndex = 0;
					break;
				case R.id.btnGroupChat:
					currentPageIndex = 1;
					break;
				case R.id.btnTopicList:
					currentPageIndex = 2;
					break;
			}
			viewpager.setCurrentItem(currentPageIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}




