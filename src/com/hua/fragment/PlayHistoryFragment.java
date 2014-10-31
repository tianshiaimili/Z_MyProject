package com.hua.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.hua.utils.LogUtils2;
import com.hua.utils.MyImageLoader;
import com.hua.view.PullDownListView;
import com.hua.weget.CircleFlowIndicator;
import com.hua.weget.LayersLayout;
import com.hua.weget.ViewFlow;
import com.listviewaddheader.model.Information;
import com.listviewaddheader.view.PullDownRefreshListView;
import com.listviewaddheader.view.PullDownRefreshListView.IXListViewListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PlayHistoryFragment extends Fragment implements OnItemClickListener,
IXListViewListener{

//	private ListView mListView; // 下拉刷新的listview
	private ViewFlow mViewFlow; // 进行图片轮播的viewFlow
	private LayersLayout mLayersLayout; // 自定义图层，用于对触屏事件进行重定向
	private View mContentView;
	/**
	 * 这是另外一种下啦刷新
	 */
	private PullDownRefreshListView mPullDownListView;
	private CircleFlowIndicator mCircleFlowIndicator;
	private int count;
	private List<String> adapterList = new ArrayList<String>();
	private String[] contentStr = new String[] {};
	private PlayHistoryRefreshListAdapter mPullToRefreshListAdapter;
	private ViewFlowAdapter mViewFlowAdapter;
	private static final int REFRESH_ADAPTER_DATA = 0;
	/**
	 * 上啦情况下的刷新
	 */
	private static final int UP_REFRESH_ADAPTER_DATA = 1;
	
	/**
	 * 设置刷新Banner 分步刷新
	 */
	private static final int UP_REFRESH_BANNER = 100;
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
	private ImageLoader mImageLoader;
	private List<String> bannerUrlLists = new ArrayList<String>();
	private int start = 0;
	private static int refreshCnt = 0;
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int whatCode = msg.what;
			switch (whatCode) {
			case REFRESH_ADAPTER_DATA:
//				mListView.requestLayout();
				mPullToRefreshListAdapter.setAdapterData(trueAppDatas);
//				mHandler.obtainMessage(UP_REFRESH_BANNER).sendToTarget();
				mPullToRefreshListAdapter.notifyDataSetChanged();
				mHandler.obtainMessage(UP_REFRESH_BANNER).sendToTarget();
				break;

			case UP_REFRESH_ADAPTER_DATA:
				mPullToRefreshListAdapter.setAdapterData(trueAppDatas);
				mPullToRefreshListAdapter.notifyDataSetChanged();
				if (isNotData) {
					Toast.makeText(getActivity(), "Not data", 300).show();
				}
				break;
				
			case UP_REFRESH_BANNER:
				if(bannerUrlLists != null){
					bannerUrlLists.clear();
				}
				for(String url:Constant.SANGUO_URLS){
					
					bannerUrlLists.add(url);
				}
				mViewFlowAdapter.setUrlList(bannerUrlLists);
				mViewFlowAdapter.notifyDataSetInvalidated();
				mViewFlow.setTimeSpan(7000);
				mViewFlow.startAutoFlowTimer();
				mViewFlow.setAdapter(mViewFlowAdapter); // 对viewFlow添加图片
				onLoad();
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
		init(mContentView);
		setListener();
		return mContentView;// super.onCreateView(inflater, container,
							// savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mViewFlow != null) {
			 mViewFlow.stopAutoFlowTimer();
		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mViewFlow != null) {
			 mViewFlow.startAutoFlowTimer();
		}
	
	}
	
	
	/**
	 * 初始化
	 * @param refresh
	 * TODO
	 */
	public void init(View contenView){
		
		mPullDownListView = (PullDownRefreshListView) contenView.findViewById(R.id.playhistory_pulltorefreshlistview);
		
		tempTtaskCollection = new HashSet<GetDataTask>();
		trueAppDatas = new ArrayList<TrueAppData>();
		myImageLoader = new MyImageLoader(getActivity());
		mLayersLayout = (LayersLayout) contenView
				.findViewById(R.id.layerslayout);

		LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
		View headerView = mLayoutInflater.inflate(R.layout.viewflow, null);

//		mListView.addHeaderView(headerView);
		mPullDownListView.addHeaderView(headerView);
		mPullDownListView.setPullLoadEnable(true);
		mViewFlow = (ViewFlow) headerView.findViewById(R.id.viewflow);// 获得viewFlow对象

		mCircleFlowIndicator = (CircleFlowIndicator) contenView
				.findViewById(R.id.viewflowindic);

		mViewFlow.setFlowIndicator(mCircleFlowIndicator);
		mViewFlow.setmSideBuffer(6);
		mViewFlow.setTimeSpan(8000);
		mViewFlow.setSelection(3 * 1000); // 设置初始位置
		 mViewFlow.startAutoFlowTimer(); // 启动自动播放
//		 mViewFlow.scheduleRepeatExecution(8000,8000);
//		mViewFlow.stopAutoFlowTimer();
		mViewFlowAdapter = new ViewFlowAdapter(getActivity(),bannerUrlLists);
		mViewFlow.setAdapter(mViewFlowAdapter);
		mLayersLayout.setView(mViewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向

		mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(
				getActivity(), trueAppDatas);
//		mListView.setAdapter(mPullToRefreshListAdapter);
		mPullDownListView.setAdapter(mPullToRefreshListAdapter);
		/**
		 * 获取数据
		 */
		getData(true);
		/**
		 * 
		 */
		mPullDownListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				LogUtils2.i("mListView.getFirstVisiblePosition()=="+mPullDownListView.getFirstVisiblePosition());
				if (mPullDownListView.getFirstVisiblePosition() -2 >= 0) {
					 LogUtils2.d("111111111111111111  ");
					// + mListView.getFirstVisiblePosition());
					if (mViewFlow != null) {
						 mViewFlow.stopAutoFlowTimer();
					}
				} 
				if(mPullDownListView.getFirstVisiblePosition() <= 1){
					 LogUtils2.d("11222222211  ");
					if (mViewFlow != null) {
						 mViewFlow.startAutoFlowTimer();
					}
				}
			}
		});
		
		
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		mPullDownListView.setOnItemClickListener(this);
		mPullDownListView.setXListViewListener(this);
		
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
			tempTtaskCollection.remove(this);
			// mPullToRefreshListAdapter.notifyDataSetInvalidated();
			if (tempTtaskCollection.size() <= 0) {
				if (isRefreshAll) {

					if (mPullToRefreshListAdapter != null) {

//						mPullToRefreshListAdapter.notifyDataSetInvalidated();
					}
					mHandler.obtainMessage(REFRESH_ADAPTER_DATA).sendToTarget();
//					mPullToRefreshListView.onRefreshComplete();
				} else {
					LogUtils2.e("UP_REFRESH_ADAPTER_DATA===========");
					if (mViewFlowAdapter != null) {

//						mViewFlowAdapter.notifyDataSetInvalidated();
					}
					mHandler.obtainMessage(UP_REFRESH_ADAPTER_DATA)
							.sendToTarget();
//					mPullToRefreshListView.onRefreshComplete();
					LogUtils2.e("over-----------");
				}
			}

			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
		}
	}

	////////////下面是IXListViewListener的接口///////////////
	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				start = ++refreshCnt;
				getData(true);
//				onLoad();
			}
		}, 2000);
	}


	@Override
	public void onLoadMore() {

		LogUtils2.d("*****onLoadMore*********");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData(true);
			}
		}, 2000);
		
		
	}
	////////////////////////////////
	
	private void onLoad() {
		mPullDownListView.stopRefresh();
		mPullDownListView.stopLoadMore();
		Date mDate = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		String date = format.format(mDate);
		mPullDownListView.setRefreshTime(date);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
	
}
