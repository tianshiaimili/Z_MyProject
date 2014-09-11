package com.hua.settingfragment.subfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hua.activity.MTNApplication;
import com.hua.activity.R;
import com.hua.app.BaseFragment;

public class SettingCustomerLoginFragment extends BaseFragment {
	private boolean isTablet;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isTablet = MTNApplication.isTablet(getActivity());
		View view;
		if(isTablet)
		{
			 view=inflater.inflate(R.layout.setting_tablet_login_page, container,false);
		}else{
			 view=inflater.inflate(R.layout.setting_login_page, container,false);
		}
		
		  
		  return view;
	}
	
}
