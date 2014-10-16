package com.hua.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hua.activity.R;
import com.hua.app.BaseFragment;
import com.hua.utils.CommonTools;
import com.hua.wiget.AutoClearEditText;

public class SearchFragment extends BaseFragment {


	private AutoClearEditText mEditText = null;
	private ImageButton mImageButton = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_result_fragment, null);

		mEditText = (AutoClearEditText) view.findViewById(R.id.search_edit);
		mImageButton = (ImageButton) view.findViewById(R.id.search_button);
		
		return view;// super.onCreateView(inflater, container,
					// savedInstanceState);
	}

	protected void initView() {
		// TODO Auto-generated method stub
		mEditText.requestFocus();
		mImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommonTools.showShortToast(getActivity(), "亲，该功能暂未开放");
			}
		});
	}
	
	public void setHostActivity(Context mainActivity) {
		// TODO Auto-generated method stub
		
	}

}
