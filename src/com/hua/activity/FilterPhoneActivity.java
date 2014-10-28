package com.hua.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hua.adapter.PlayHistoryRefreshListAdapter;
import com.hua.adapter.ViewFlowAdapter;
import com.hua.contants.Constant;
import com.hua.model.AppData.TempAppData;
import com.hua.model.AppData.TrueAppData;
import com.hua.utils.LogUtils2;
import com.hua.utils.MyImageLoader;
import com.hua.weget.CircleFlowIndicator;
import com.hua.weget.LayersLayout;
import com.hua.weget.ViewFlow;

public class FilterPhoneActivity extends FragmentActivity {
	// /////////////////////////

	private ListView mListView; // 下拉刷新的listview
	private PullToRefreshListView mPullToRefreshListView;
	/**
	 * // 自定义图层，用于对触屏事件进行重定向
	 */
	private LayersLayout mLayersLayout;
	private View mContentView;
	// private PlayHistoryRefreshListAdapter mPullToRefreshListAdapter;
	private static final int REFRESH_ADAPTER_DATA = 0;
	/**
	 * 上啦情况下的刷新
	 */
	private static final int UP_REFRESH_ADAPTER_DATA = 1;
	private int AppDataCount;
	private int tempAppDataCount = 4;
	private boolean isNotData;
	/**
	 * 内部的task集合
	 */
	public static Set<GetDataTask> tempTtaskCollection;
	// private List<TrueAppData> trueAppDatas;
	private MyImageLoader myImageLoader;
	private boolean isRefreshAll;
	private CircleFlowIndicator mCircleFlowIndicator;

	/**
	 * 
	 */
	private ViewFlow mViewFlow; // 进行图片轮播的viewFlow
	private ViewFlowAdapter mViewFlowAdapter;
	private List<String> urlLists;
	/**
	 * 设置刷新Banner 分步刷新
	 */
	private static final int UP_REFRESH_BANNER = 100;

	private List<String> contentStrings = new ArrayList<String>();
	private MyAdapter myAdapter;
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int whatCode = msg.what;
			switch (whatCode) {
			case REFRESH_ADAPTER_DATA:
				mListView.requestLayout();
				// mViewFlowAdapter =
				// ViewFlowAdapter.getInstance(getActivity());
				// mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片

				// mPullToRefreshListAdapter = new
				// PlayHistoryRefreshListAdapter(
				// getActivity(), trueAppDatas);
				// mViewFlowAdapter.notifyDataSetInvalidated();
				// mViewFlowAdapter.notifyDataSetChanged();
				// ((PlayHistoryRefreshListAdapter)((HeaderViewListAdapter)mListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();;
				// mPullToRefreshListAdapter.notifyDataSetInvalidated();
				// mPullToRefreshListAdapter.setAdapterData(trueAppDatas);
				// mPullToRefreshListAdapter.notifyDataSetChanged();
				// mHandler.obtainMessage(UP_REFRESH_BANNER).sendToTarget();
				// mPullToRefreshListAdapter.notifyDataSetChanged();
				break;

			case UP_REFRESH_ADAPTER_DATA:
				// mViewFlowAdapter = new ViewFlowAdapter(getActivity());
				// mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
				mListView.requestLayout();
				// mViewFlowAdapter.notifyDataSetInvalidated();
				// mViewFlowAdapter.notifyDataSetChanged();
				// mPullToRefreshListAdapter = new
				// PlayHistoryRefreshListAdapter(
				// getActivity(), trueAppDatas);
				// mListView.setAdapter(mPullToRefreshListAdapter); // 绑定数据
				// mPullToRefreshListAdapter.notifyDataSetInvalidated();
				// mPullToRefreshListAdapter.setAdapterData(trueAppDatas);
				// mPullToRefreshListAdapter.notifyDataSetChanged();
				mHandler.obtainMessage(UP_REFRESH_BANNER).sendToTarget();
				if (isNotData) {
					Toast.makeText(getBaseContext(), "Not data", 300).show();
				}
				break;

			case UP_REFRESH_BANNER:
				mListView.requestLayout();
				urlLists = Arrays.asList(Constant.SANGUO_URLS);
//				mViewFlowAdapter = new ViewFlowAdapter(getBaseContext(), urlLists);
				mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
				mViewFlowAdapter.setUrlList(urlLists);
				// mViewFlowAdapter.notifyDataSetChanged();
				mViewFlowAdapter.notifyDataSetInvalidated();
				mHandler.obtainMessage(10).sendToTarget();;
//				Message message = new Message();
//				message.what = 10;
//				mHandler.sendMessageDelayed(message);
				
				LogUtils2.e("********000000000");
				break;

			case 200:
				mPullToRefreshListView.onRefreshComplete();
				LogUtils2.e("********000000000");
				break;
				

			case 10:
				myAdapter.setAdapter(contentStrings);
				Message message = new Message();
				message.what = 200;
				mHandler.sendMessageDelayed(message,2000);
				break;
				
			default:
				break;

			}

		};
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.simple__content_fragment);

		// mContentView =
		// LayoutInflater.from(getBaseContext()).inflate(R.layout.simple__content_fragment,
		// null);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.filter_pulltorefreshlistview);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mListView = mPullToRefreshListView.getRefreshableView();

		tempTtaskCollection = new HashSet<GetDataTask>();
		// trueAppDatas = new ArrayList<TrueAppData>();
		myImageLoader = new MyImageLoader(getBaseContext());
		mLayersLayout = (LayersLayout) findViewById(R.id.filter_layerslayout);

		View headerView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.viewflow, null);

		// mListView.addHeaderView(headerView);

		mViewFlow = (ViewFlow) headerView.findViewById(R.id.viewflow);// 获得viewFlow对象

		// mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
		if (Constant.homeBannerBitmaps != null) {

			mViewFlow.setmSideBuffer(Constant.homeBannerBitmaps.subList(0, 5)
					.size());
		} else {
			mViewFlow.setmSideBuffer(8);
		}

		mCircleFlowIndicator = (CircleFlowIndicator) headerView
				.findViewById(R.id.viewflowindic);

		mViewFlow.setFlowIndicator(mCircleFlowIndicator);
		mViewFlow.setTimeSpan(5500);
		mViewFlow.setSelection(3 * 1000); // 设置初始位置
		// mViewFlow.startAutoFlowTimer(); // 启动自动播放
		mViewFlow.scheduleRepeatExecution(3000, 3000);
		 mViewFlow.stopAutoFlowTimer();
		mViewFlowAdapter = new ViewFlowAdapter(getBaseContext(), urlLists);
		mViewFlow.setAdapter(mViewFlowAdapter);
		mLayersLayout.setView(mViewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向
		mListView.addHeaderView(headerView);
		
//		contentStrings = Arrays.asList(Constant.SANGUO_URLS);
		for(int i=0;i<20;i++){
			contentStrings.add("德玛西亚--"+ i);
		}
		myAdapter = new MyAdapter(contentStrings);
		mListView.setAdapter(myAdapter);

		// mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(
		// getBaseContext(), trueAppDatas);
		// mListView.setAdapter(mPullToRefreshListAdapter);

		/**
		 * 获取数据
		 */
		// getData(true);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mListView.getFirstVisiblePosition() != 1) {
					// LogUtils2.d("111111111111111111  "
					// + mListView.getFirstVisiblePosition());
					// if (mViewFlow != null) {
					// // mViewFlow.stopAutoFlowTimer();
					// }
					// } else {
					// if (mViewFlow != null) {
					// // mViewFlow.startAutoFlowTimer();
					// }
				}
			}
		});

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {

				String label = DateUtils.formatDateTime(getBaseContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				// trueAppDatas.clear();
				// getData(true);

//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
						try {
							contentStrings.add("123456");
//							urlLists.clear();
//								urlLists.add("https://raw.githubusercontent.com/tianshiaimili/MyResource/master/res/drawable-hdpi/other14.jpg");
//								urlLists.add((Constant.SANGUO_URLS)[2]);
//								urlLists.add((Constant.SANGUO_URLS)[1]);
//								urlLists.add((Constant.SANGUO_URLS)[0]);
							mHandler.obtainMessage(UP_REFRESH_BANNER)
									.sendToTarget();
//							Thread.sleep(2000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

//					}
//				}).start();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				String label = DateUtils.formatDateTime(getBaseContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				// trueAppDatas.clear();
				// getData(false);
//				mHandler.obtainMessage(UP_REFRESH_BANNER).sendToTarget();

				contentStrings.add("123456");
				mHandler.obtainMessage(10)
				.sendToTarget();
//		Thread.sleep(2000);
				
			}
		});

	}

	public void getData(boolean refresh) {
		isRefreshAll = refresh;
		if (Constant.tempAppDataLists != null) {
			AppDataCount = Constant.tempAppDataLists.size();
			if (tempAppDataCount >= AppDataCount) {
				tempAppDataCount = AppDataCount - 4;
				isNotData = true;
			}

			int tempLenght = tempAppDataCount + 4;
			if (tempLenght >= AppDataCount) {
				tempLenght = AppDataCount;
			}
			LogUtils2.e("tempAppDataCount---== " + tempAppDataCount);
			LogUtils2.e("AppDataCount---== " + AppDataCount);
			// Collections.shuffle(Constant.tempAppDataLists);
			if (!isRefreshAll) {
				List<TempAppData> tempAppDatas = Constant.tempAppDataLists
						.subList(0, 5);
				for (int i = 0; i < tempAppDatas.size(); i++) {
					// new GetDataTask().execute();
					GetDataTask task = new GetDataTask();
					task.execute(tempAppDatas.get(i));
					tempTtaskCollection.add(task);
					tempAppDataCount++;
				}
			} else {

				List<TempAppData> tempAppDatas = Constant.tempAppDataLists
						.subList(0, 5);
				for (int i = 0; i < tempAppDatas.size(); i++) {
					// new GetDataTask().execute();
					GetDataTask task = new GetDataTask();
					task.execute(tempAppDatas.get(i));
					tempTtaskCollection.add(task);
					// tempAppDataCount++;
				}

			}

			LogUtils2.e("tempAppDataCount--******== " + tempAppDataCount);
		}
	}

	/**
	 * 异步获取数据
	 * 
	 * @author zero
	 * 
	 */
	private class GetDataTask extends AsyncTask<TempAppData, Void, Bitmap> {
		// 后台处理部分
		@Override
		protected Bitmap doInBackground(TempAppData... params) {
			// Simulates a background job.
			if (params[0] != null) {

				try {
					// Thread.sleep(1000);
					TempAppData tempAppData = params[0];
					TrueAppData trueAppData = new TrueAppData();
					Bitmap temBitmap = myImageLoader.loadImage(
							tempAppData.getAppIco(), 50);
					if (temBitmap != null) {
						trueAppData.setAppIco(temBitmap);
					} else {
						trueAppData.setAppIco(new BitmapFactory()
								.decodeResource(getResources(),
										R.drawable.baihe));
					}
					trueAppData.setAppName(tempAppData.getAppName());
					trueAppData.setAppScore(tempAppData.getAppScore());
					trueAppData.setAppDownLoadNum(tempAppData
							.getAppDownLoadNum());
					trueAppData.setAppSize(tempAppData.getAppSize());
					trueAppData.setAppComment(tempAppData.getAppComment());
					trueAppData.setAppDowmUrl(tempAppData.getAppDowmUrl());
					// trueAppDatas.add(trueAppData);

				} catch (Exception e) {
				}

			}

			return null;
		}

		// 这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		// 根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
		@Override
		protected void onPostExecute(Bitmap result) {
			// 在头部增加新添内容
			// mListItems.addFirst(result);

			// 通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
			// mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			// Toast.makeText(getActivity(), "lal=="+result, 300).show();
			// contentStr = Arrays.asList(array)
			// adapterList.add(result);
			tempTtaskCollection.remove(this);
			// mPullToRefreshListAdapter.notifyDataSetInvalidated();
			if (tempTtaskCollection.size() <= 0) {
				if (isRefreshAll) {

					// if (mPullToRefreshListAdapter != null) {
					//
					// // mPullToRefreshListAdapter.notifyDataSetInvalidated();
					// }
					mHandler.obtainMessage(UP_REFRESH_ADAPTER_DATA)
							.sendToTarget();
					mPullToRefreshListView.onRefreshComplete();
				} else {
					LogUtils2.e("UP_REFRESH_ADAPTER_DATA===========");
					mHandler.obtainMessage(REFRESH_ADAPTER_DATA).sendToTarget();
					mPullToRefreshListView.onRefreshComplete();
					LogUtils2.e("over-----------");
				}
			}

			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
		}
	}

	class MyAdapter extends BaseAdapter {

		List<String> tempList;
		
		public MyAdapter(List<String> list){
			tempList = list;
		}
		
		
		public void setAdapter(List<String> list){
			tempList = list;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tempList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView eTextView = new TextView(getApplicationContext());
			eTextView.setText(tempList.get(position % tempList.size()));
			eTextView.setTextSize(24f);
			return eTextView;
		}

	}

}
