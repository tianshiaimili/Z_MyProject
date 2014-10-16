package com.hua.settingfragment.subfragment;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.hua.activity.R;
import com.hua.app.BaseFragment;
import com.hua.utils.LogUtils2;

/**
 * 仿QQ 的滑动删除 效果
 * @author zero
 *
 */
public class CopyOfSwipeMenuFragment extends BaseFragment{

	/**
	 * 一开始加载的全局view
	 */
	private View view;
	private List<ApplicationInfo> mAppList;
	private Context context;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		context = activity;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		context = getActivity();
		view = inflater.inflate(R.layout.swipemenu_fragment, null); 
		mAppList = context.getPackageManager().getInstalledApplications(0);
		LogUtils2.d("mAppList=="+mAppList.size());
		
		SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.listView);
		
		AppAdapter adapter = new AppAdapter();
		
		listView.setAdapter(adapter);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		
		// set creator
				listView.setMenuCreator(creator);

				// step 2. listener item click event
				listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu, int index) {
						ApplicationInfo item = mAppList.get(position);
						switch (index) {
						case 0:
							// open
							open(item);
							break;
						case 1:
							// delete
							delete(item);
							break;
						}
					}
				});

				// other setting
//				listView.setCloseInterpolator(new BounceInterpolator());

				// test item long click
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view,
							int position, long id) {
						Toast.makeText(getApplicationContext(), position + " long click", 0).show();
						return false;
					}
				});
		
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	/**
	 * 卸载
	 * @param item
	 */
	private void delete(ApplicationInfo item) {
		// delete app
		try {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.fromParts("package", item.packageName, null));
			startActivity(intent);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 打开APP
	 * @param item
	 */
	private void open(ApplicationInfo item) {
		// open app
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(item.packageName);
		List<ResolveInfo> resolveInfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			LogUtils2.d("activityPackageName=="+activityPackageName+"    className=="+className);
			ComponentName componentName = new ComponentName(
					activityPackageName, className);

			intent.setComponent(componentName);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activityanim_in, R.anim.activityanim_out);
		}
	}
	
	/**
	 * 
	 * @author zero
	 *
	 */
	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public ApplicationInfo getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.swipemenu_fragment_item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ApplicationInfo item = getItem(position);
			holder.iv_icon.setImageDrawable(item.loadIcon(context.getPackageManager()));
			holder.tv_name.setText(item.loadLabel(context.getPackageManager()));
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}
	}
	
	/**
	 * 
	 */
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
