package com.hua.settingfragment.subfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hua.activity.R;
import com.hua.app.BaseFragment;

/**
 * ��QQ �Ļ���ɾ�� Ч��
 * @author zero
 *
 */
public class SwipeMenuFragment extends BaseFragment{

	/**
	 * һ��ʼ���ص�ȫ��view
	 */
	private View view;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(R.layout.simple__content_fragment, null); 
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}
