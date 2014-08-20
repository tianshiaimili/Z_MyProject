package com.hua.activity;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hua.app.BaseApplication;
import com.hua.util.LayoutUtils;

public class MTNApplication extends BaseApplication {

	private static final String TAG = MTNApplication.class.getSimpleName();
	private static MTNApplication app;
//	private AppServiceAccessAide mAppServiceAccessAide;
	
	public static MTNApplication getMTNApplication() {
		return app;
	}
	
	@Override
	public void onCreate() {
		app = this;
		super.onCreate();
//		mAppServiceAccessAide = new AppServiceAccessAide();
//		startService(new Intent(this, DownloadService.class));
	}
	
//	public AppServiceAccessAide getAppServiceAccessAide() {
//		return mAppServiceAccessAide;
//	}
	
	public static void addService(Service service) {
//		getMTNApplication().getAppServiceAccessAide().addService(service);
	}
	
	public static void removeService(Service service) {
//		getMTNApplication().getAppServiceAccessAide().removeService(service);
	}
	
//	public static DownloadService getDownloadService() {
//		return (DownloadService)getMTNApplication().getAppServiceAccessAide().getService(DownloadService.class);
//	}
	
	public static void startFragment(Fragment oldFragment, Fragment newFragment) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "startFragment() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).startFragment(newFragment);
		} else if(act instanceof MainActivityTablet) {
			((MainActivityTablet)act).startFragment(newFragment);
		} else {
			Log.d(TAG, "startFragment() failed : host activity not valid : " + act);
		}
	}
	
	public static void setBackButtonVisibility(Fragment oldFragment, boolean visible) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "setBackButtonVisibility() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).setBackButtonVisible(visible);
		} else if(act instanceof MainActivityTablet) {
			// never show back button for tablet app.
			// ((MainActivityTablet)act).setBackButtonVisibility(visibility);
		} else {
			Log.d(TAG, "setBackButtonVisibility() failed : host activity not valid : " + act);
		}
	}
	
	public static void setFilterButtonVisibility(Fragment oldFragment, int visibility) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "setBackButtonVisibility() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).setFilterButtonVisibility(visibility);
		} else if(act instanceof MainActivityTablet) {
			((MainActivityTablet)act).setFilterButtonVisibility(visibility);
		} else {
			Log.d(TAG, "setFilterButtonVisibility() failed : host activity not valid : " + act);
		}
	}
	
	public static boolean isTablet(Context context) {
		boolean isTablet = LayoutUtils.isTablet(context);
		//isTablet = true;
		return isTablet;
	}
	
}
