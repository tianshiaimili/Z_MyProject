package com.hua.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hua.activity.R;
import com.hua.app.BaseFragment;

public class SearchAutoCompleteFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, null);

		return view;// super.onCreateView(inflater, container,
					// savedInstanceState);
	}

	public void setHostActivity(Context mainActivity) {
		// TODO Auto-generated method stub
		
	}

}
