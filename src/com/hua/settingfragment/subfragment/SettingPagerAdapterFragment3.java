package com.hua.settingfragment.subfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hua.activity.R;


public class SettingPagerAdapterFragment3 extends Fragment {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText("内容即将呈现.....3.");
		return textView;
	}

	private void addListener() {
	}

	private void setupView(View view) {
	}

}
