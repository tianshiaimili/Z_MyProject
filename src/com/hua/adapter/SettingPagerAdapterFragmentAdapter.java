package com.hua.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SettingPagerAdapterFragmentAdapter extends FragmentPagerAdapter {

	ArrayList<Fragment> datas;
	String [] fragmentnames ={"资讯","VK","话题"};

	public SettingPagerAdapterFragmentAdapter(FragmentManager fm,
			ArrayList<Fragment> datas) {
		super(fm);
		setDatas(datas);
	}

	public void setDatas(ArrayList<Fragment> datas) {
		if (datas == null) {
			this.datas = new ArrayList<Fragment>();
		} else {
			this.datas = datas;
		}
	}

	@Override
	public Fragment getItem(int position) {
		return datas.get(position);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentnames[position];
	}

}
