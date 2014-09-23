package com.hua.settingfragment.subfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hua.activity.R;

public class SettingPagerAdapterFragment2 extends Fragment {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.f2, null);
		TextView textView = new TextView(getActivity());
		textView.setText("内容即将呈现.....2.");
		return textView;
	}

	
}


