package com.hua.homefragment.subfragment;

import com.hua.activity.R;
import com.hua.fragment.TuanGouFragment;
import com.hua.util.LogUtils2;
import com.hua.view.MyScrollView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

/**
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 */
public class FourFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		LogUtils2.i("*********onCreateView*************");
		
		 TuanGouFragment tuanGouFragment = new TuanGouFragment();
//		 View historyView = LayoutInflater.from(getActivity()).inflate(R.layout.homefragment_tuangou_, null);
		 MyScrollView myScrollView = (MyScrollView) LayoutInflater.from(getActivity()).
				inflate(R.layout.fragment_tuangou, null);
		LogUtils2.e("myScrollView===="+myScrollView);
		LogUtils2.e("tuanGouFragment===="+tuanGouFragment.getView());
		
//		tempScrollview.addChild(myScrollView, 1);
		
		return myScrollView;
		
	}
}
