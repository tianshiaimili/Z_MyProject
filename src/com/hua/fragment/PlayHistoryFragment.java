package com.hua.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hua.activity.R;
import com.hua.adapter.PlayHistoryRefreshListAdapter;
import com.hua.adapter.ViewFlowAdapter;
import com.hua.contants.Constant;
import com.hua.model.AppData.TempAppData;
import com.hua.model.AppData.TrueAppData;
import com.hua.util.LogUtils2;
import com.hua.util.MyImageLoader;
import com.hua.wiget.CircleFlowIndicator;
import com.hua.wiget.LayersLayout;
import com.hua.wiget.ViewFlow;

public class PlayHistoryFragment extends Fragment {

	private ListView mListView; // 下拉刷新的listview
	private ViewFlow mViewFlow; // 进行图片轮播的viewFlow
	private LayersLayout mLayersLayout; // 自定义图层，用于对触屏事件进行重定向
	private View mContentView;
	private PullToRefreshListView mPullToRefreshListView;
	private CircleFlowIndicator mCircleFlowIndicator;
	private int count;
	private List<String> adapterList = new ArrayList<String>();
	private String[] contentStr = new String[] {};
	// { /*"ListView1", "ListView2", "ListView3",*/
	// /*"ListView4", "ListView5", "ListView6", "ListView7", "ListView8",
	// "ListView9", "ListView10", "ListView11", "ListView12",*/ };
	private PlayHistoryRefreshListAdapter mPullToRefreshListAdapter;
	private ViewFlowAdapter mViewFlowAdapter;
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
	private List<TrueAppData> trueAppDatas;
	private MyImageLoader myImageLoader;
	private boolean isRefreshAll;
	private HeaderViewListAdapter mHeaderViewListAdapter;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int whatCode = msg.what;
			switch (whatCode) {
			case REFRESH_ADAPTER_DATA:
				mListView.requestLayout();
				mViewFlowAdapter = new ViewFlowAdapter(getActivity());
				mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
				
				mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(
						getActivity(), trueAppDatas);
				mListView.setAdapter(mPullToRefreshListAdapter); // 绑定数据
				mListView.requestLayout();
				mViewFlowAdapter.notifyDataSetInvalidated();
//				mViewFlowAdapter.notifyDataSetChanged();
//				((PlayHistoryRefreshListAdapter)((HeaderViewListAdapter)mListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();;
				mPullToRefreshListAdapter.notifyDataSetInvalidated();
				break;

			case UP_REFRESH_ADAPTER_DATA:
//				mViewFlowAdapter = new ViewFlowAdapter(getActivity());
//				mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
				mListView.requestLayout();
				mViewFlowAdapter.notifyDataSetInvalidated();
//				mViewFlowAdapter.notifyDataSetChanged();
//				mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(
//						getActivity(), trueAppDatas);
//				mListView.setAdapter(mPullToRefreshListAdapter); // 绑定数据
				mListView.requestLayout();
				mPullToRefreshListAdapter.notifyDataSetInvalidated();
//				mPullToRefreshListAdapter.notifyDataSetChanged();
				if (isNotData) {
					Toast.makeText(getActivity(), "Not data", 300).show();
				}
				break;

			default:
				break;
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.payhistory_layout_fragment,
				null);
		mPullToRefreshListView = (PullToRefreshListView) mContentView
				.findViewById(R.id.playhistory_pulltorefreshlistview);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
	
		tempTtaskCollection = new HashSet<GetDataTask>();
		trueAppDatas = new ArrayList<TrueAppData>();
		myImageLoader = new MyImageLoader(getActivity());
		mLayersLayout = (LayersLayout) mContentView
				.findViewById(R.id.layerslayout);

		LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
		View headerView = mLayoutInflater.inflate(R.layout.viewflow, null);
		/**
		 * 获取数据
		 */
		getData(true);

		mListView.addHeaderView(headerView);

		mViewFlow = (ViewFlow) headerView.findViewById(R.id.viewflow);// 获得viewFlow对象

		// mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
		if (Constant.homeBannerBitmaps != null) {

			mViewFlow.setmSideBuffer(Constant.homeBannerBitmaps.subList(0, 5).size());
		} else {
			mViewFlow.setmSideBuffer(8);
		}

		mCircleFlowIndicator = (CircleFlowIndicator) mContentView
				.findViewById(R.id.viewflowindic);

		mViewFlow.setFlowIndicator(mCircleFlowIndicator);
		mViewFlow.setTimeSpan(5500);
		mViewFlow.setSelection(3 * 1000); // 设置初始位置
//		 mViewFlow.startAutoFlowTimer(); // 启动自动播放
		 mViewFlow.scheduleRepeatExecution(3000,3000);
//		mViewFlow.stopAutoFlowTimer();
		mViewFlowAdapter = new ViewFlowAdapter(getActivity());
		mViewFlow.setAdapter(mViewFlowAdapter);
		mLayersLayout.setView(mViewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向

		mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(
				getActivity(), trueAppDatas);
		mListView.setAdapter(mPullToRefreshListAdapter);
		
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// LogUtils2.i("999999999");
//				LogUtils2.d("view.getScaleY()==" + view.getScaleY());
				if (mListView.getFirstVisiblePosition() != 1) {
					// LogUtils2.d("111111111111111111  "
					// + mListView.getFirstVisiblePosition());
					if (mViewFlow != null) {
						// mViewFlow.stopAutoFlowTimer();
					}
				} else {
					if (mViewFlow != null) {
						// mViewFlow.startAutoFlowTimer();
					}
				}
			}
		});

		// mPullToRefreshListView.setOnRefreshListener(new
		// OnRefreshListener<ListView>() {
		// @Override
		// public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		//
		// String label = DateUtils.formatDateTime(getActivity(),
		// System.currentTimeMillis(),
		// DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
		// DateUtils.FORMAT_ABBREV_ALL);
		//
		// // Update the LastUpdatedLabel
		// refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		//
		// // Do work to refresh the list here.
		// getData();
		// }
		// });

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {

				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				trueAppDatas.clear();
				getData(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				// trueAppDatas.clear();
				getData(false);

			}
		});

		return mContentView;// super.onCreateView(inflater, container,
							// savedInstanceState);
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
					trueAppDatas.add(trueAppData);

				} catch (Exception e) {
				}

			}

			String str = "Added after refresh..." + (count++) + " add";
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

					if (mPullToRefreshListAdapter != null) {

//						mPullToRefreshListAdapter.notifyDataSetInvalidated();
					}
					mHandler.obtainMessage(REFRESH_ADAPTER_DATA).sendToTarget();
					mPullToRefreshListView.onRefreshComplete();
				} else {
					LogUtils2.e("UP_REFRESH_ADAPTER_DATA===========");
					if (mViewFlowAdapter != null) {

//						mViewFlowAdapter.notifyDataSetInvalidated();
					}
					mHandler.obtainMessage(UP_REFRESH_ADAPTER_DATA)
							.sendToTarget();
					mPullToRefreshListView.onRefreshComplete();
					LogUtils2.e("over-----------");
				}
			}

			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
		}
	}

}
