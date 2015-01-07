package com.hua.homefragment.subfragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.hua.activity.DetailsActivity_;
import com.hua.activity.ImageDetailActivity_;
import com.hua.activity.R;
import com.hua.adapter.CardsAnimationAdapter;
import com.hua.adapter.NewAdapter2;
import com.hua.app.BaseActivity2;
import com.hua.app.BaseFragment2;
import com.hua.bean.NewModle;
import com.hua.contants.Url;
import com.hua.initview.InitView;
import com.hua.network.http.json.NewListJson;
import com.hua.network.utils.HttpUtil;
import com.hua.utils.LogUtils2;
import com.hua.utils.StringUtils;
import com.hua.wedget.swiptlistview.SwipeListView;
import com.hua.wedget.viewimage.Animations.DescriptionAnimation;
import com.hua.wedget.viewimage.Animations.SliderLayout;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.hua.wedget.viewimage.SliderTypes.TextSliderView;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

public class DianYingFragment extends BaseFragment2 implements
		SwipeRefreshLayout.OnRefreshListener, OnSliderClickListener {

	private View mContentView;
	private Context mContext;
	/**
	 * 头部的 banner
	 */
	protected SliderLayout mDemoSlider;
	/**
	 * 新的组件，可以下拉滑动刷新
	 */
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * 滑动的listview
	 */
	protected SwipeListView mListView;
	/**
	 * the progressBar on the bottom when the listview have been scroll to the
	 * bottom
	 */
	protected ProgressBar mProgressBar;
	protected HashMap<String, String> url_maps;

	protected HashMap<String, NewModle> newHashMap;

	// protected NewAdapter newAdapter;
	protected NewAdapter2 newAdapter2;
	/**
	 * 封装多个json数据对象的list
	 */
	protected List<NewModle> listsModles;
	private int index = 0;
	private boolean isRefresh = false;
	private static final int RESPONSE_OK = 2000;
	protected static final int START_BAR = 900;
	private static final int BANNER_NUM = 300;

	Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			int what = message.what;
			int numchange = what;
			LogUtils2.i("what==" + what);
			switch (what) {

			// case START_BAR:
			// if (mViewPager != null) {
			// LogUtils2.d("999999999utyuiyiyiuyui");
			// mViewPager.setCurrentItem(currentItem);
			// LogUtils2.d("mViewPager.getCurrentItem()=="
			// + mViewPager.getCurrentItem());
			// adapter.notifyDataSetChanged();
			//
			// }
			//
			case RESPONSE_OK:
				String result = (String) message.obj;
				getResult(result);
				// mHandler.obtainMessage(ShowFootView).sendToTarget();
				break;
			//
			// case ShowFootView:
			// if(mFooterView.getVisibility() == View.GONE){
			// mFooterView.setVisibility(View.VISIBLE);
			// }
			// isShowFirstIn = true;
			// /**
			// * 停止加载更多 恢复原样
			// */
			// stopLoadMore();
			//
			// break;
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView =  inflater.inflate(R.layout.activity_main,
				null);
		initComponents(mContentView);

		return mContentView;//super.onCreateView(inflater, container, savedInstanceState);
	}

	public void initComponents(View contentView) {

		mSwipeRefreshLayout = (SwipeRefreshLayout) contentView
				.findViewById(R.id.swipe_container);
		mListView = (SwipeListView) contentView.findViewById(R.id.listview);
		mProgressBar = (ProgressBar) contentView.findViewById(R.id.progressBar);
		// /
		listsModles = new ArrayList<NewModle>();
		url_maps = new HashMap<String, String>();
		newHashMap = new HashMap<String, NewModle>();
		// ////////
		mSwipeRefreshLayout.setOnRefreshListener(this);
		InitView.instance().initSwipeRefreshLayout(mSwipeRefreshLayout);
		InitView.instance().initListView(mListView, getActivity());
		// //
		View headView = LayoutInflater.from(getActivity()).inflate(
				R.layout.head_item, null);
		mDemoSlider = (SliderLayout) headView.findViewById(R.id.slider);
		mListView.addHeaderView(headView);
		newAdapter2 = new NewAdapter2(mContext);
		AnimationAdapter animationAdapter = new CardsAnimationAdapter(
				newAdapter2);
		animationAdapter.setAbsListView(mListView);
		mListView.setAdapter(animationAdapter);
		loadData(getCommonUrl(index + "", Url.DianYingId));

		mListView.setOnBottomListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentPagte++;
				index = index + 20;
				loadData(getCommonUrl(index + "", Url.DianYingId));
			}
		});

		mListView.setOnItemClickListener(new MyOnitemClickListener());

	}

	/**
	 * 根据url加载数据
	 * 
	 * @param commonUrl
	 */
	private void loadData(String commonUrl) {

		if (getMyActivity().hasNetWork()) {
			LogUtils2.d("**********index+++== " + index);
			loadNewList(commonUrl);
		} else {
			// mPullRefreshListView.onRefreshComplete();
			// mProgressBar.setVisibility(View.GONE);
			// getMyActivity().showShortToast(getString(R.string.not_network));
			// String result = getMyActivity().getCacheStr(
			// "TiYuFragment" + currentPagte);
			// if (!StringUtils.isEmpty(result)) {
			// Message msg = new Message();
			// msg.what = RESPONSE_OK;
			// msg.obj = result;
			// mHandler.sendMessage(msg);
			// // getResult(result);
			// }

			mListView.onBottomComplete();
			mProgressBar.setVisibility(View.GONE);
			getMyActivity().showShortToast(getString(R.string.not_network));
			String result = getMyActivity().getCacheStr(
					"DianYingFragment" + currentPagte);
			if (!StringUtils.isEmpty(result)) {
				getResult(result);
			}

		}

	}

	/**
	 * 
	 * @param url
	 */
	// TODO
	private void loadNewList(String url) {
		String result;
		try {
			new GetDataTask().execute(url);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils2.e("error -----");
		}
	}

	/**
	 * 获取数据后刷新页面 重新布局页面数据
	 * 
	 * @param result
	 */
	public void getResult(String result) {
		getMyActivity().setCacheStr("DianYingFragment" + currentPagte, result);
		if (isRefresh) {
			isRefresh = false;
			newAdapter2.clear();
			listsModles.clear();
		}
		mProgressBar.setVisibility(View.GONE);
		mSwipeRefreshLayout.setRefreshing(false);

		List<NewModle> list = NewListJson.instance(getActivity())
				.readJsonNewModles(result, Url.DianYingId);
		if (index == 0) {
			initDatasCollections(list);
		} else {
			newAdapter2.appendList(list);

		}
		listsModles.addAll(list);
		mListView.onBottomComplete();
	}

	public void enterDetailActivity(NewModle newModle) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("newModle", newModle);
		Class<?> class1;
		if (newModle.getImagesModle() != null
				&& newModle.getImagesModle().getImgList().size() > 1) {
			// class1 = ImageDetailActivity_.class;
			class1 = ImageDetailActivity_.class;
		} else {
			class1 = DetailsActivity_.class;
		}
		((BaseActivity2) getActivity()).openActivity(class1, bundle, 0);
	}

	/**
	 * 第一次进来时 把banner部分初始化处理
	 * 
	 * @param newModles
	 */
	private void initDatasCollections(List<NewModle> newModles) {

		if (!isNullString(newModles.get(0).getImgsrc()))
			newHashMap.put(newModles.get(0).getImgsrc(), newModles.get(0));
		if (!isNullString(newModles.get(1).getImgsrc()))
			newHashMap.put(newModles.get(1).getImgsrc(), newModles.get(1));
		if (!isNullString(newModles.get(2).getImgsrc()))
			newHashMap.put(newModles.get(2).getImgsrc(), newModles.get(2));
		if (!isNullString(newModles.get(3).getImgsrc()))
			newHashMap.put(newModles.get(3).getImgsrc(), newModles.get(3));

		if (!isNullString(newModles.get(0).getImgsrc()))
			url_maps.put(newModles.get(0).getTitle(), newModles.get(0)
					.getImgsrc());
		if (!isNullString(newModles.get(1).getImgsrc()))
			url_maps.put(newModles.get(1).getTitle(), newModles.get(1)
					.getImgsrc());
		if (!isNullString(newModles.get(2).getImgsrc()))
			url_maps.put(newModles.get(2).getTitle(), newModles.get(2)
					.getImgsrc());
		if (!isNullString(newModles.get(3).getImgsrc()))
			url_maps.put(newModles.get(3).getTitle(), newModles.get(3)
					.getImgsrc());

		for (String name : url_maps.keySet()) {
			TextSliderView textSliderView = new TextSliderView(getActivity());
			textSliderView.setOnSliderClickListener(this);
			textSliderView.description(name).image(url_maps.get(name));

			textSliderView.getBundle().putString("extra", name);
			mDemoSlider.addSlider(textSliderView);
		}

		mDemoSlider.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
		mDemoSlider
				.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
		mDemoSlider.setCustomAnimation(new DescriptionAnimation());
		LogUtils2.i("*****mViewFlowAdapter.setAdapterData********");
		// mViewFlowAdapter.setAdapterData(newHashMap, url_maps);
		LogUtils2.e("");
		newAdapter2.appendList(newModles);
	}

	private class GetDataTask extends AsyncTask<String, Void, String> {
		// 后台处理部分
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				result = HttpUtil.getByHttpClient(mContext, params[0]);

			} catch (Exception e) {
				e.printStackTrace();
				LogUtils2.e("GetDataTask get Data error ----");
			}
			LogUtils2.i("get data from network result == " + result);
			return result;
		}

		// 这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		// 根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
		@Override
		protected void onPostExecute(String result) {
			// 在头部增加新添内容
			// Toast.makeText(getActivity(), "lal", 300).show();
			super.onPostExecute(result);// 这句是必有的，AsyncTask规定的格式
			// if (mPullRefreshListView != null) {
			Message msg = new Message();
			msg.obj = result;
			msg.what = RESPONSE_OK;
			mHandler.sendMessage(msg);
			// mPullRefreshListView.onRefreshComplete();
			// }

		}
	}

	// /////////////////////////////////////////////////////////////
	@Override
	public void onSliderClick(BaseSliderView slider) {
		NewModle newModle = newHashMap.get(slider.getUrl());
		enterDetailActivity(newModle);
	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				currentPagte = 1;
				isRefresh = true;
				loadData(getCommonUrl(0 + "", Url.DianYingId));
				url_maps.clear();
				mDemoSlider.removeAllSliders();
			}
		}, 2000);
	}

	/**
	 * 
	 * @author zero
	 * 
	 */
	class MyOnitemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NewModle newModle = listsModles.get(position - 1);
			enterDetailActivity(newModle);
		}

	}

}
