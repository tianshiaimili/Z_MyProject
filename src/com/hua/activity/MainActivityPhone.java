package com.hua.activity;

import java.util.LinkedHashSet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.app.BaseFragmentActivity;
import com.hua.fragment.DownloadFragment;
import com.hua.fragment.HomeFragment;
import com.hua.fragment.PlayHistoryFragment;
import com.hua.fragment.SearchAutoCompleteCrewDetailFragment;
import com.hua.fragment.SearchAutoCompleteFragment;
import com.hua.fragment.SettingsFragment;
import com.hua.fragment.VODFragment;
import com.hua.util.FragmentUtils;
import com.hua.util.FragmentUtils.FragmentTabBarController;
import com.hua.util.FragmentUtils.FragmentTabSwitcher;
import com.hua.util.FragmentUtils.FragmentTabSwitcherFeed;
import com.hua.util.KeyboardUtils;
import com.hua.util.LayoutUtils;
import com.hua.util.LogUtils2;

public class MainActivityPhone extends BaseFragmentActivity {

	private FragmentTabSwitcher mFragmentTabSwitcher;
	private static final String TAB_TV = "tv";
	private static final String TAB_MOVIE = "movie";
	private static final String TAB_DOWNLOAD = "download";
	private static final String TAB_PLAY_HISTORY = "play_history";
	private static final String TAB_SETTING = "setting";
	//
	private View currentSelectedIcon;
	private FragmentTabBarController mFragmentTabBarController;
	private ViewGroup backLayout;
	private ImageView backButton;
	private ImageView backLogo;
	private RelativeLayout navigationBar;
	//search
	private ImageView searchButton;
	private boolean isCrew;//Ӧ�����ж�user�Ƿ�������ϸ��Ϣ���߽����µ�page
	private boolean isOnclickDetail;//Ӧ�����ж�user�Ƿ�������ϸ��Ϣ���߽����µ�page
	
	private ImageView filterButton;
	private long lastClickBackTime = 0;
	
	//
	private static final String CURRENT_FRAGMENT_TAG = "currentFragmentTag";
	private SearchAutoCompleteFragment searchAutoCompleteFragment;
	private SearchAutoCompleteCrewDetailFragment searchAutoCompleteCrewDetailFragment;
	

	//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutUtils.setActivityNoTitleBar(this);
		setContentView(R.layout.main_activity_phone);
		initFragmentTabSwitcher();
		initUI();
//		downloadHandler = new DownloadHandler(this);
//		MTNApplication.getDownloadService().setUiHandler(downloadHandler);
//		KeyboardUtils.setSoftInputAdjustNothing(this, true);
		///
		
	}

	public FragmentTabSwitcher getmFragmentTabSwitcher() {
		return mFragmentTabSwitcher;
	}

	public void setmFragmentTabSwitcher(FragmentTabSwitcher mFragmentTabSwitcher) {
		this.mFragmentTabSwitcher = mFragmentTabSwitcher;
	}

	@Override
	protected void onResume() {
		super.onResume();
		setBackButtonVisible(false);
		navigationBar = (RelativeLayout) findViewById(R.id.main_activity_navigation_bar);
		searchButton = (ImageView) findViewById(R.id.main_activity_navigation_bar_right_search);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchAutoCompleteFragment = new SearchAutoCompleteFragment();
				startFragment(searchAutoCompleteFragment);
				searchAutoCompleteFragment.setHostActivity(MainActivityPhone.this);
				navigationBar.setVisibility(View.GONE);
			}
		});
	}
	
	public void startFragment(Fragment fragment) {
		mFragmentTabSwitcher.pushFragment(fragment);
	}
	
	private void initUI() {
		// TODO Auto-generated method stub
		//���صĲ���
		backLayout = (ViewGroup) findViewById(R.id.main_activity_navigation_bar_back_layout);
		backButton = (ImageView) findViewById(R.id.main_activity_navigation_bar_back_img);
		backLogo = (ImageView) findViewById(R.id.main_activity_navigation_bar_back_logo);
		backLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(backButton.getVisibility() == View.VISIBLE){
					mFragmentTabSwitcher.popFragment();
					if(isCrew||isOnclickDetail){
						navigationBar.setVisibility(View.GONE);
						isCrew = false;
						isOnclickDetail = false;
					} else {
						navigationBar.setVisibility(View.VISIBLE);
					}
				}
				
			}
		});
		
		/**
		 * �����ǹ��˵�����
		 */
		filterButton = (ImageView) findViewById(R.id.main_activity_navigation_bar_right_filter);
		filterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivityPhone.this,FilterPhoneActivity.class);
				intent.putExtra(CURRENT_FRAGMENT_TAG, mFragmentTabSwitcher.getCurrentTabId());
				startActivityForResult(intent, 200);
			}
		});
		
	}

	private void initFragmentTabSwitcher() {
		// TODO Auto-generated method stub
		FragmentTabSwitcherFeed rootFragmentFeed = new FragmentTabSwitcherFeed() {
			
			@Override
			public Fragment newRootFragment(String tag) {
				if(TAB_TV.equalsIgnoreCase(tag)) {
					HomeFragment homeFragment = new HomeFragment();
					return homeFragment;
				} else if(TAB_MOVIE.equalsIgnoreCase(tag)) {
					VODFragment vodFragment = new VODFragment();
					return vodFragment;
				} else if(TAB_DOWNLOAD.equalsIgnoreCase(tag)) {
					return new DownloadFragment();
				} else if(TAB_PLAY_HISTORY.equalsIgnoreCase(tag)) {
					return new PlayHistoryFragment();
				} else if(TAB_SETTING.equalsIgnoreCase(tag)) {
					return new SettingsFragment();
				} 
				return null;
			}
			
			@Override
			public LinkedHashSet getRootFragmentTags() {
				/**
				 * linkHashset������ó�����ʱ��˳��
				 */
				LogUtils2.e("getRootFragmentTags********");
				return FragmentUtils.makeRootFragmentTags(new String[]{TAB_TV, TAB_MOVIE, TAB_PLAY_HISTORY, TAB_DOWNLOAD, TAB_SETTING});
			}
			//
		};
		mFragmentTabSwitcher = new FragmentTabSwitcher(this, R.id.main_activity_fragment_container, rootFragmentFeed);
		mFragmentTabBarController = new FragmentTabBarController(this, (LinearLayout)findViewById(R.id.tab_bar_container), true) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup viewgroup,
					LayoutInflater layoutinflater) {
				// TODO Auto-generated method stub
				LogUtils2.e("mFragmentTabBarController.getView=="+position);
				ImageView icon = null;
				TextView lable = null;
				////
				if(convertView == null){
					convertView = layoutinflater.inflate(R.layout.tab_bar_item, null);
					icon = (ImageView) convertView.findViewById(R.id.tab_bar_img);
					lable = (TextView) convertView.findViewById(R.id.tab_bar_text);
				}
				
				///
				switch (position) {
				case 0:
					
					icon.setImageResource(R.drawable.tab_tv_selector);
					lable.setText(R.string.tab_bar_tv_series);
					convertView.setTag(TAB_TV);
					
					break;
				case 1:
					icon.setImageResource(R.drawable.tab_movie_selector);
					lable.setText(R.string.tab_bar_movies);
					convertView.setTag(TAB_MOVIE);
					
					break;
					
				case 2:
					icon.setImageResource(R.drawable.tab_history_selector);
					lable.setText(R.string.tab_bar_play_history);
					convertView.setTag(TAB_PLAY_HISTORY);
					break;
				case 3:
					icon.setImageResource(R.drawable.tab_download_selector);
					lable.setText(R.string.tab_bar_download);
					convertView.setTag(TAB_DOWNLOAD);
					break;
				case 4:
					icon.setImageResource(R.drawable.tab_setting_selector);
					lable.setText(R.string.tab_bar_settings);
					convertView.setTag(TAB_SETTING);
					break;
				}
				
				///
				convertView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String tag = (String)v.getTag();
						if(TAB_PLAY_HISTORY.equals(tag) || TAB_DOWNLOAD.equals(tag)){
							mFragmentTabSwitcher.switchTab(tag, false);
						}else{
							mFragmentTabSwitcher.switchTab(tag);
						}
						
						////
						setCurrentSelected(v);
						//
						navigationBar.setVisibility(View.VISIBLE);
						KeyboardUtils.hideKeyboard(navigationBar);
						if(mFragmentTabSwitcher.isRootFragment()) {
							setBackButtonVisible(false);
						}
					}
				});
				return convertView;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mFragmentTabSwitcher.getTabCount();
			}
			///
		};
		LogUtils2.d("createTabBar------------");
		mFragmentTabBarController.createTabBar();
		LogUtils2.d("createTabBar**********");
		//
		mFragmentTabSwitcher.setSwitcherListener(new FragmentUtils.FragmentTabSwitcherListener() {
			
			@Override
			public void onTabSelected(String rootFragmentTag, int tabIndex, String previousRootFragmentTag, int previousTabIndex) {
				setCurrentSelected(mFragmentTabBarController.getItem(tabIndex));
			}
		});
		
		///���Ȱ�TVfragmentTab���� 
		mFragmentTabSwitcher.switchTab(TAB_TV);
		
	}

	/**
	 * 
	 */
	@Override
	public void onBackPressed() {
		LogUtils2.w("onBackPressed*************");
		mFragmentTabSwitcher.onFragmentActivityBackPressed();
		if(isCrew||isOnclickDetail){
			navigationBar.setVisibility(View.GONE);
			if (isOnclickDetail == false) {
				isCrew = false;
			}
			isOnclickDetail = false;
		}else {
			navigationBar.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)  
         {  
			 //click back button twice to exit system when in rootfragment page
			 if(mFragmentTabSwitcher.isRootFragment()){
                 if((System.currentTimeMillis() - lastClickBackTime) > 2000)    
                 {  
                  Toast.makeText(getApplicationContext(), "System exit when click one more time" ,Toast.LENGTH_SHORT).show();                                  
                  lastClickBackTime = System.currentTimeMillis();
                 }  
                 else  
                 {  
//                	 clearFilterSharedPreferences();
                     finish();  
                     System.exit(0);  
                 }  
                 return true;
			 }
         }  
         return super.onKeyDown(keyCode, event); 
	}
	
	/**
	 * ��������ǵ�ѡ��ĳ��tabʱ���ð����ڴ�tab�е�ȫ��view��������ʾ������ѡ��״̬��
	 * @param selected
	 */
	private void setCurrentSelected(View selected) {
		// dim previous selected
		if(currentSelectedIcon != null) {
			currentSelectedIcon.setSelected(false);
			if(currentSelectedIcon instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) currentSelectedIcon;
				int childCount = vg.getChildCount();
				for(int i=0; i<childCount; i++) {
					vg.getChildAt(i).setSelected(false);
				}
			} 
		}
		// highlight current selected
		currentSelectedIcon = selected;
		currentSelectedIcon.setSelected(true);
		if(currentSelectedIcon instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) currentSelectedIcon;
			int childCount = vg.getChildCount();
			for(int i=0; i<childCount; i++) {
				vg.getChildAt(i).setSelected(true);
			}
		} 
	}
	
	///
	public void setBackButtonVisible(boolean visible) {
		backButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}
	

}
