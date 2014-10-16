package com.hua.model;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.hua.contants.Constant;
import com.hua.utils.LogUtils2;

public class ShopAppApplication extends Application {
	public static List<CategoryInfo> mDatas;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils2.i("---------------------onCreate start-------------");
		/*mDatas = new ArrayList<CategoryInfo>();
		for (int i = 0; i < Constant.category_msg.length; i++) {
			CategoryInfo categoryInfo2 = new CategoryInfo();
			categoryInfo2.setIcon(Constant.category_icon[i]);
			categoryInfo2.setMsg(Constant.category_msg[i]);
			mDatas.add(categoryInfo2);
		}*/
		
	}

	public ShopAppApplication() {
		LogUtils2.i("---------------------ShopAppApplication start-------------");
		
		mDatas = new ArrayList<CategoryInfo>();
		for (int i = 0; i < Constant.category_msg.length; i++) {
			CategoryInfo categoryInfo2 = new CategoryInfo();
			categoryInfo2.setIcon(Constant.category_icon[i]);
			categoryInfo2.setMsg(Constant.category_msg[i]);
			mDatas.add(categoryInfo2);
		}
		
	}

	
}
