package com.hua.fragment;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.ListView;

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
	private String[] contentStr = new String[]{};
//		{ /*"ListView1", "ListView2", "ListView3",*/
//			/*"ListView4", "ListView5", "ListView6", "ListView7", "ListView8",
//			"ListView9", "ListView10", "ListView11", "ListView12",*/ };
	private PlayHistoryRefreshListAdapter mPullToRefreshListAdapter;
	private static final int REFRESH_ADAPTER_DATA = 0;
	/**
	 * 内部的task集合
	 */
	public static Set<GetDataTask> tempTtaskCollection;
	private List<TrueAppData> trueAppDatas;
	private MyImageLoader myImageLoader;
	
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int whatCode = msg.what;
			switch (whatCode) {
			case 0:
				mPullToRefreshListAdapter = new PlayHistoryRefreshListAdapter(getActivity(),trueAppDatas);
				mListView.setAdapter(mPullToRefreshListAdapter); // 绑定数据
				mPullToRefreshListAdapter.notifyDataSetInvalidated();
				break;

			default:
				break;
			}
			
		};
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.payhistory_layout_fragment, null);
		mPullToRefreshListView =  (PullToRefreshListView) mContentView.findViewById(R.id.playhistory_pulltorefreshlistview);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		tempTtaskCollection = new HashSet<GetDataTask>();
		trueAppDatas = new ArrayList<TrueAppData>();
		myImageLoader = new MyImageLoader(getActivity());
		mLayersLayout = (LayersLayout) mContentView.findViewById(R.id.layerslayout);

		LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
		View headerView = mLayoutInflater.inflate(R.layout.viewflow, null);
		/**
		 * 获取数据
		 */
		getData();
		
		mListView.addHeaderView(headerView);
		
		mViewFlow = (ViewFlow) headerView.findViewById(R.id.viewflow);// 获得viewFlow对象
		mViewFlow.setAdapter(new ViewFlowAdapter(getActivity())); // 对viewFlow添加图片
		if(Constant.homeBannerBitmaps != null){
			
			mViewFlow.setmSideBuffer(Constant.homeBannerBitmaps.size());
		}else {
			mViewFlow.setmSideBuffer(8);			
		}
		
		mCircleFlowIndicator = (CircleFlowIndicator) mContentView.findViewById(R.id.viewflowindic);
		
		
		mViewFlow.setFlowIndicator(mCircleFlowIndicator);
		mViewFlow.setTimeSpan(5500);
		mViewFlow.setSelection(3 * 1000); // 设置初始位置
		mViewFlow.startAutoFlowTimer(); // 启动自动播放
		
		
		mLayersLayout.setView(mViewFlow); // 将viewFlow对象传递给自定义图层，用于对事件进行重定向
		
		
//		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
//			@Override
//			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				
//				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
//						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//				// Update the LastUpdatedLabel
//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//
//				// Do work to refresh the list here.
//						getData();
//					}
//				});
		
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				trueAppDatas.clear();
						getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				trueAppDatas.clear();
						getData();
				
			}
		});
		
		
		return mContentView;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void getData(){
		
		if(Constant.tempAppDataLists != null){
			Collections.shuffle(Constant.tempAppDataLists);
			for(int i=0;i<Constant.tempAppDataLists.size();i++){
//				new GetDataTask().execute();
				GetDataTask task = new GetDataTask();
				task.execute(Constant.tempAppDataLists.get(i));
				tempTtaskCollection.add(task);
			}
		}
	}
	
	
	 /**
	  * 异步获取数据
	  * @author zero
	  *
	  */
	 private class GetDataTask extends AsyncTask<TempAppData, Void, Bitmap> {
			// 后台处理部分
			@Override
			protected Bitmap doInBackground(TempAppData... params) {
				// Simulates a background job.
				if(params[0] != null){
					
					try {
//						Thread.sleep(1000);
						TempAppData tempAppData = params[0];
						TrueAppData trueAppData = new TrueAppData();
						Bitmap temBitmap = myImageLoader.loadImage(tempAppData.getAppIco(), 50);
						if(temBitmap != null){
							trueAppData.setAppIco(temBitmap);
						}else {
							trueAppData.setAppIco(new BitmapFactory().decodeResource(getResources(), R.drawable.baihe));
						}
						trueAppData.setAppName(tempAppData.getAppName());
						trueAppData.setAppScore(tempAppData.getAppScore());
						trueAppData.setAppDownLoadNum(tempAppData.getAppDownLoadNum());
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
//				mListItems.addFirst(result);

				// 通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
//				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
//				Toast.makeText(getActivity(), "lal=="+result, 300).show();
//				contentStr = Arrays.asList(array)
//				adapterList.add(result);
				tempTtaskCollection.remove(this);
				mHandler.obtainMessage(REFRESH_ADAPTER_DATA).sendToTarget();
				if(tempTtaskCollection.size() <=0){
					mPullToRefreshListView.onRefreshComplete();
				}
				
				
				
				super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
			}
		}
	 
	
}
