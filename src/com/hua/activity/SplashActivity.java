package com.hua.activity;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.hua.app.BaseActivity;
import com.hua.contants.Constant;
import com.hua.model.AppData.HomeBannerData;
import com.hua.model.AppData.TempAppData;
import com.hua.util.LogUtils2;
import com.hua.util.MyImageLoader;
import com.hua.util.MyImageLoader.MyLoadImageTask;

/**
 * ����App��ʾ����ҳ
 * 
 * @author Hua
 * 
 */
public class SplashActivity extends BaseActivity {

	private int PAGER_COUNT = 3;
	private boolean isfirstInstall;
	private ViewPager viewPager;
	private SharedPreferences preferences;
	private MyLoadImageTask task;
	private MyImageLoader myImageLoader;
	private boolean isSendToHander;
	/**
	 * 读取文件解析出来的json格式数据的 list
	 */
	private List<HomeBannerData> mHomeBannerDatas = null;
	private List<TempAppData> mTempAppDatas = null;

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 5:

				viewPager.setAdapter(new MViewPagerAdapter());
				viewPager.setCurrentItem(0);
				break;
			default:
				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		preferences = getPreferences(MODE_PRIVATE);
		isfirstInstall = preferences.getBoolean("isfirstInstall", false);
		if (!isfirstInstall) {
			LogUtils2.d("***********************");
			setContentView(R.layout.first_install_page);
			//
			LogUtils2.w("111111111");
			viewPager = (ViewPager) findViewById(R.id.viewpager);
			loadBannerImages();
			LoadAppData();
			// SharedPreferences preferences =getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isfirstInstall", true);
			editor.commit();
		} else {

			LogUtils2.d("+++++++++++++");
			Intent intent = new Intent(getBaseContext(), WelcomeActivity2.class);
			// Intent intent = new Intent(getBaseContext(),
			// WelcomeActivity.class);
			startActivity(intent);
			finish();

		}
	}

	class MViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PAGER_COUNT;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
			container.removeView(((View) object));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		// //

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		// /
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			LogUtils2.w("position==" + position);
			if (position == 2) {
				LogUtils2.w("position***=" + position);
				View view = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.first_install_last_pager_item_, null);
				Button button = (Button) view.findViewById(R.id.button);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getBaseContext(),
								MainActivityPhone.class);
						startActivity(intent);
						finish();
					}
				});
				container.addView(view);
				return view;
			} else {

				ImageView imageView = new ImageView(getBaseContext());
				imageView.setImageResource(R.drawable.xianjian2);
				container.addView(imageView);
				return imageView;
			}
			// return super.instantiateItem(container, position);
		}
	}

	
	private void loadBannerImages() {
		// TODO Auto-generated method stub
		LogUtils2.e("***1111111111111*****");
		List<String> imageUrls = Arrays.asList(Constant.BANNER_URLS);
		int width = getResources().getDisplayMetrics().widthPixels;
		
		MyImageLoader myImageLoader = MyImageLoader.getInstance(getBaseContext());
		
		for (int i = 0; i < 5; i++) {
			/*MyLoadImageTask*/ 
			task = new MyLoadImageTask(null,imageUrls,width,getApplicationContext());
//			taskCollection.add(task);
			LogUtils2.e("***1111333111*****");
			task.tempTtaskCollection.add(task);
			task.execute(i);
//			handler.sendMessage(handler.obtainMessage(3));
		}
		
	}
	
	/**
	 * 
	 */
	public void LoadAppData() {
		String[] urls = { Constant.HOMEBANNER_DATA, Constant.APPDATA };
		for (String temUrl : urls) {
			LogUtils2.e("temUrl--- " + temUrl);
			new MyDownloadAsyncTask().execute(temUrl);
		}

	}

	private class MyDownloadAsyncTask extends
			AsyncTask<String, Integer, Bitmap> {

		ImageView tempImageView;
		String tempDataUrlPath;

		public MyDownloadAsyncTask(ImageView imageView) {

			tempImageView = imageView;
		}

		public MyDownloadAsyncTask() {

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];// tempUrl.substring(index+1,
									// tempUrl.length());
			LogUtils2.d("url--==" + url);

			if (url == null || url.equals("")) {
				return null;
			}

			if (url.equals(Constant.HOMEBANNER_DATA)) {

				if (myImageLoader.isDataFileExist(url)) {
					LogUtils2.d("##********************#");
					try {
						getHomeBannerData(myImageLoader, url);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils2.d("****error*****");
						return null;
					}
				}
				// 第一次下载文件....
				else {
					LogUtils2.d("###############");
					try {
						if (myImageLoader.downDataFiles(url)) {

							getHomeBannerData(myImageLoader, url);
						} else {
							return null;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils2.d("****error*****" + e.getMessage());
						return null;
					}

				}
			}
			// 下载appdata模拟数据
			else if (url.equals(Constant.APPDATA)) {

				if (myImageLoader.isDataFileExist(url)) {
					LogUtils2.d("##********************#");
					try {
						getTempAppDataList(myImageLoader, url);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils2.d("****error*****");
						return null;
					}
				}
				// 第一次下载文件....
				else {
					LogUtils2.d("###############");
					try {
						if (myImageLoader.downDataFiles(url)) {
							LogUtils2.d("@@@@@@@###############");
							getTempAppDataList(myImageLoader, url);

						} else {
							return null;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils2.d("****error*****" + e.getMessage());
						return null;
					}

				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (!isSendToHander) {
				isSendToHander = true;
				LogUtils2.e("set isSendToHander----------");
			} else if (isSendToHander) {
				LogUtils2.e("set isSendToHander*********");
				// welcomeTextView.setAlpha(0.0f);
//				 handler.obtainMessage().sendToTarget();
				 handler.sendMessage(handler.obtainMessage(5));

			}

		}

	}

	public void getTempAppDataList(MyImageLoader myImageLoader, String url)
			throws Exception {

		String jsonStr = (String) myImageLoader
				.getDataFromExistFile(myImageLoader.getDataFileNamePath(url));
		mTempAppDatas = myImageLoader.fromTempAppDataJson(jsonStr);
		LogUtils2.e("getTempAppDataList===  " + mTempAppDatas.size());

		for (TempAppData tempAppData : mTempAppDatas) {
			Constant.tempAppDataLists.add(tempAppData);
			LogUtils2.e("Constant.tempAppDataLists。size-=88= "
					+ Constant.tempAppDataLists.size());
		}

	}

	public void getHomeBannerData(MyImageLoader myImageLoader, String url)
			throws Exception {

		String jsonStr = (String) myImageLoader
				.getDataFromExistFile(myImageLoader.getDataFileNamePath(url));
		mHomeBannerDatas = myImageLoader.fromHomeBannerDataJson(jsonStr);
		for (HomeBannerData tempBannerData : mHomeBannerDatas) {
			Constant.homeBannerBitmaps.add(myImageLoader.loadImage(
					tempBannerData.getHomeBannerImageUrl(), getBaseContext()
							.getResources().getDisplayMetrics().widthPixels));
			LogUtils2.d("Constant.homeBannerBitmaps。size-== "
					+ Constant.homeBannerBitmaps.size());
		}

	}

}
