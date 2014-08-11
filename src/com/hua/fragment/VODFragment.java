package com.hua.fragment;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.MainActivityPhone;
import com.hua.activity.MainActivityTablet;
import com.hua.activity.R;
import com.hua.adapter.CoverFlowAdapter;
import com.hua.app.BaseFragment;
import com.hua.app.WeakHandler;
import com.hua.model.Banner;
import com.hua.model.Banner.BannerData;
import com.hua.model.VODNodeData;
import com.hua.util.DisplayUtils;
import com.hua.util.FlexibleImageView;
import com.hua.util.HandlerTimer;
import com.hua.util.ImageUtil;
import com.hua.util.LayoutUtils;
import com.hua.util.LogUtils2;
import com.hua.view.PlaceholderView;
import com.hua.wiget.CoverFlow;
import com.hua.wiget.ObservableListView;
import com.hua.wiget.PageIndexer;

public class VODFragment extends BaseFragment{

	private PageIndexer pageIndexer;
//	private SingleImageLoader viewPagerSingleLoader;
//
	private ObservableListView listView;
//	private GroupImageLoader imageLoader;
	private VODListAdapter listAdapter;

	public int currentItem = 0; // current show image num

	private static final int DRAW_PAGEINDEXER = 0;
	private static final int SHOW_FILTER_RESULT = 1;

	private HandlerTimer timer;

	private boolean isMovie = false;
	private boolean isTablet;

	private int everyRowNum = 4;
	private String tagName;

	public int viewPagerWidth;
	public int viewPagerHeight;

	public int listItemWidth, listItemHeight;

	public static String BANNER_TYPE_SERIES = "series";
	public static String BANNER_TYPE_PRODUCT = "product";
	public static String BANNER_TYPE_URL = "url";
	public static String BANNER_TYPE_OTHER = "other";
	public final static String VOD_NODE_TAG_ALL = "All";
	public final static String VOD_NODE_TAG_FILTER1 = "Filter1";
	public final static String VOD_NODE_TAG_FILTER2 = "Filter2";
	private static final String TAB_TV = "tv";
	public static final String TAB_MOVIE = "movie";
	public final static String GENRES_NODE_NAME_DEFAULT = "All";
	public List<BannerData> bannerDataList;
	public View listViewHeader,listViewFooter;
	public String nodeName; // the genres's param
	public int listViewHeaderY;

	public static int BANNER_WIDTH_TABLET = 948;
	public static int BANNER_HEIGHT_TABLET = 375;
	public static int BANNER_WIDTH_PHONE = 320;
	public static int BANNER_HEIGHT_PHONE = 127;

	public static int VOD_LIST_CELL_TV_WIDTH_TABLET = 211;
	public static int VOD_LIST_CELL_TV_HEIGHT_TABLET = 119;
	public static int VOD_LIST_CELL_MOVIE_WIDTH_TABLET = 174;
	public static int VOD_LIST_CELL_MOVIE_HEIGHT_TABLET = 278;

	public static int VOD_LIST_CELL_TV_WIDTH_PHONE = 100;
	public static int VOD_LIST_CELL_TV_HEIGHT_PHONE = 57;
	public static int VOD_LIST_CELL_MOVIE_WIDTH_PHONE = 100;
	public static int VOD_LIST_CELL_MOVIE_HEIGHT_PHONE = 149;
	
	//
	public Bitmap watermark_banner;
	private CoverFlow coverFlow;
	private CoverFlowAdapter coverFlowAdapter;
	private int appTopBarHeight;

	public Fragment getCurFragment() {
		return this;
	}
	
	@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			
			String tabId =null;
			if(getActivity() instanceof MainActivityPhone){
				
				tabId =( (MainActivityPhone)getActivity()).getmFragmentTabSwitcher().getCurrentTabId();
				
			}else if (getActivity() instanceof MainActivityTablet) {
//				tabId = ((MainActivityTablet) getActivity()).getmFragmentTabSwitcher().getCurrentTabId();
			}
	
			//

			if (isMovie) {
				tagName = "Movie";
			} else {
				tagName = "TV";
			}
			if (!isTablet) {
				everyRowNum = 3;
			}
			if (isMovie && isTablet) {
				everyRowNum = 5;
			}
			
			watermark_banner = ImageUtil.getLoadingImageBitmap(getActivity(),R.drawable.watermark_banner);
			
	}
	
	//
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

//		View view = inflater.inflate(R.layout.vod_landing_page, null);
		View view = inflater.inflate(R.layout.vod_landing_page, container, false);
		//
		pageIndexer = (PageIndexer) view.findViewById(R.id.page_indexer);
		
		if (isTablet) {
//			viewPagerWidth = DisplayUtils.getScreenWidth(getActivity()) - (int) getResources().getDimension(R.dimen.tablet_main_tab_switcher_width);
//			viewPagerHeight = FlexibleImageView.getFitHeight(viewPagerWidth, BANNER_WIDTH_TABLET, BANNER_HEIGHT_TABLET);
		} else {
			viewPagerWidth = DisplayUtils.getScreenWidth(getActivity());
			viewPagerHeight = FlexibleImageView.getFitHeight(viewPagerWidth, BANNER_WIDTH_PHONE, BANNER_HEIGHT_PHONE);
		}
		//
		coverFlow = (CoverFlow) view.findViewById(R.id.coverFlow);
		//
		LayoutUtils.setLayoutParams(coverFlow, null, viewPagerWidth, viewPagerHeight);
		///
		int imagaMargin = (int) getResources().getDimension(R.dimen.vod_landing_listView_cell_margin_left);
		//
		listView = (ObservableListView) view.findViewById(R.id.vodListView);
		///
		coverFlow.setBackgroundColor(getResources().getColor(R.color.vod_landing_bg_color));
		coverFlow.setFocusable(true);
		coverFlow.setClickable(true);
		int coverFlowImageWidth = (int) (viewPagerWidth * 0.8);
		int coverFlowImageHeight = (int) (viewPagerHeight * 0.8);
		coverFlowAdapter = new CoverFlowAdapter(getActivity(),/*viewPagerSingleLoader,*/coverFlowImageWidth,coverFlowImageHeight);
        coverFlow.setAdapter(coverFlowAdapter);
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	

	/**
	 * start viewPager's timer
	 */
	public void startViewPagerTimer() {
		if (timer == null) {
			timer = new HandlerTimer(true);
			timer.scheduleRepeatExecution(new Runnable() {
				@Override
				public void run() {
					LogUtils2.d("currentItem=="+currentItem);
					LogUtils2.e("是否可点"+coverFlow.isClickable());
					coverFlow.setSelection((currentItem + 1) % bannerDataList.size() /*+ 20 * bannerDataList.size()*/);
				}
			}, 60000, 60000);
		}
	}

	/**
	 * stop viewPager's timer
	 */
	public void stopViewPagerTimer() {
		if (timer != null) {
			timer.cancelRepeatExecution();
			timer.quit();
			timer = null;
		}

	}
	
	/**
	 * 
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		super.onActivityCreated(savedInstanceState);
		
		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				LogUtils2.d("*****这里*************");
				int targetPosition = position % bannerDataList.size();
				pageIndexer.updateSelected(targetPosition);
				currentItem = targetPosition;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		/////
		
		coverFlow.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				
				LogUtils2.d("onTouch________________");
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					LogUtils2.d("zheli down.....");
						Toast.makeText(getActivity(), "12", 3000).show();
						break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					/**
					 * true表示自己消化事件
					 */
					listView.setTouchInterested(true);
					break;
				}
				return false;
			}
		});
		
		////
		coverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LogUtils2.i("onitemclick=======");
				Toast.makeText(getActivity(), "546546", 3000).show();
			}
		});

		/**
		 * 
		 */
		listViewHeader = new PlaceholderView(getActivity());
//		listViewHeader.setBackgroundColor(Color.parseColor("#00000000"));
		int indicatorBarHeight = pageIndexer.getHeight();
		appTopBarHeight = (int) getResources().getDimension(R.dimen.page_padding_top);
		LayoutUtils.setLayoutParams(listViewHeader, listView, 
				ViewGroup.LayoutParams.MATCH_PARENT, 
				viewPagerHeight + indicatorBarHeight + appTopBarHeight);
		LayoutUtils.addListHeaderView(listView, listViewHeader);
		int appBottomBarHeight = (int) getResources().getDimension(R.dimen.page_padding_bottom);
		listViewFooter = new PlaceholderView(getActivity());
		///
		listViewFooter.setBackgroundColor(Color.parseColor("#7f700000"));
		LayoutUtils.setLayoutParams(listViewFooter, listView, ViewGroup.LayoutParams.MATCH_PARENT, appBottomBarHeight);
		LayoutUtils.addListFooterView(listView, listViewFooter);
		
		///
		listViewHeader.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					
					LogUtils2.d("listViewHeader.listViewHeaderY=="+listViewHeaderY);
					if (listViewHeaderY < 0) {
						LogUtils2.d("listViewHeader.setOnTouchListener.down");
						LogUtils2.d("listView.setTouchInterested(true);");
						listView.setTouchInterested(true);
					}else {
						LogUtils2.d("listView.setTouchInterested(false);");
						listView.setTouchInterested(false);
					}
					
					break;
				}
				return false;
			}
		});
		
		///
		listAdapter = new VODListAdapter(getActivity());
		listView.setAdapter(listAdapter);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				listViewHeaderY = (int) listViewHeader.getY();
				LogUtils2.d("listViewHeaderY=="+listViewHeaderY);
				if (listViewHeaderY < 0) {
					stopViewPagerTimer();
				} else {
					startViewPagerTimer();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem != 0 || view.getChildCount() == 0) {
					return;
				}
				View firstChildView = view.getChildAt(0);
				int height = firstChildView.getHeight();
				int top = firstChildView.getTop();
				float alpha = (float) Math.abs(top) / (float) height;
				coverFlow.setAlpha(1 - alpha < 0.2 ? 0.2f : 1 - alpha);
			}
		});
	}
	
	///
	@Override
	public void onResume() {
		LogUtils2.e("onResume---------");
		startViewPagerTimer();
		SharedPreferences genres = getActivity().getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
//		if (isMovie) {
//			nodeName = genres.getString(FilterFragment.MOVIE_SELECTEDFILTER, "All");
//		} else {
//			nodeName = genres.getString(FilterFragment.TV_SELECTEDFILTER, "All");
//		}
//		MTNApplication.setBackButtonVisibility(this, false);
//		MTNApplication.setFilterButtonVisibility(this, View.VISIBLE);
		mHandler.sendEmptyMessage(DRAW_PAGEINDEXER);
		super.onResume();
	}
	
	/**
	 * 
	 */
	private final InnerStaticHandler mHandler = new InnerStaticHandler(this);
	private static class InnerStaticHandler extends WeakHandler<VODFragment> {

		public InnerStaticHandler(VODFragment contextObject) {
			super(contextObject);
		}

		@Override
		public void handleWeakHandlerMessage(VODFragment contextObject, Message msg) {
			LogUtils2.d("msg.what=="+msg.what);
			switch (msg.what) {
			case DRAW_PAGEINDEXER:
//				List<VODNodeData> list = contextObject.getDataWithTag(contextObject.tagName, contextObject.nodeName);
				
				List<VODNodeData> list  =  new ArrayList<VODNodeData>();
				for(int i=0;i<8;i++){
					VODNodeData vodNodeData = new VODNodeData("Tilte"+i+"","subTilte"+i);
					list.add(vodNodeData);
				}
				contextObject.listAdapter.setData(list);
				List<BannerData> listBannerDatas = new ArrayList<Banner.BannerData>();
				for(int i=0;i<5;i++){
					
					BannerData bannerData = new BannerData(i+"", "title"+i);
					listBannerDatas.add(bannerData);
					
				}
				//
				
				contextObject.bannerDataList = listBannerDatas;//contextObject.getBannerDataList(contextObject.tagName);
				contextObject.coverFlowAdapter.setData(contextObject.bannerDataList);
				contextObject.coverFlow.setSelection(contextObject.bannerDataList.size() /2);

				int pageIndexerPadding = (int) contextObject.getResources().getDimension(R.dimen.vod_landing_indicator_margin_right);
				contextObject.pageIndexer.generateViews(contextObject.bannerDataList.size(), R.drawable.index_round, pageIndexerPadding, pageIndexerPadding,
						pageIndexerPadding, pageIndexerPadding);
				break;
		/*	case SHOW_FILTER_RESULT:
				List<VODNodeData> filterResultList = contextObject.getDataWithTag(contextObject.tagName, contextObject.nodeName);
				contextObject.listAdapter.setData(filterResultList);
				contextObject.startViewPagerTimer();
				break;*/
			default:
				break;
			}

		}

	};
	
	/**
	 * tagName= TV or movie
	 */
	public List<BannerData> getBannerDataList(String tagName) {
		List<BannerData> list = new ArrayList<BannerData>();
		List<BannerData> bannerDatalist = Banner.getInstance().getBannerList();
		LogUtils2.w("bannerDatalist=="+bannerDatalist);
		for (BannerData bannerData : bannerDatalist) {
			if (bannerData.getTagList() != null && bannerData.getTagList().size() > 0) {
				for (String tag : bannerData.getTagList()) {
					if (tag.equalsIgnoreCase(tagName)) {
						list.add(bannerData);
					}
				}
			}
		}
		return list;

	}
	
	
	/**
	 * 
	 */
	private class VODListAdapter extends BaseAdapter {
		
		private int [] images={R.drawable.xianjian1,R.drawable.xianjian2,
				R.drawable.icon_2_n,R.drawable.xianjian1,R.drawable.icon_3_n,
				R.drawable.xianjian2,
				R.drawable.icon_2_n,R.drawable.icon_2_s,R.drawable.icon_3_n,
				R.drawable.tab_bg,R.drawable.tab_download_normal,R.drawable.tab_live_active,
				R.drawable.tab_vod_active,R.drawable.xianjian1,R.drawable.xianjian2};
		
		private static final int TYPE_TITLE = 0;
		private static final int TYPE_ITEM = 1;

		private Context context;

		private View landingListItemSubLayout1;
		private View landingListItemSubLayout2;
		private View landingListItemSubLayout3;
		private View landingListItemSubLayout4;
		private View landingListItemSubLayout5;

		private List<VODNodeData> nodeDataList;
		int rowCount = 0;
		List<Integer> titleRowNumList = new ArrayList<Integer>();

		public VODListAdapter(Context context) {
			super();
			this.context = context;
		}

		public void setData(List<VODNodeData> list) {
			rowCount = 0;
			titleRowNumList.clear();
			this.nodeDataList = list;
			int[] nodeRowCountArray = new int[nodeDataList.size()];// every
																	// node's
																	// count
																	// array,sucn
																	// as must
																	// see's row
																	// count
			for (int i = 0; i < nodeDataList.size(); i++) {
				int nodeRowCount;
				// the displayown only show one row ,so nodeRowCount is 1;
				if (i != nodeDataList.size() - 1) {
					nodeRowCount = 1;
				} else {
					nodeRowCount = (int) Math.ceil(((float) nodeDataList.get(i).getNodeNum()) / everyRowNum);
				}
				nodeRowCountArray[i] = nodeRowCount;
			}
			titleRowNumList.add(0); // title row's num list
			for (int nodeRowCount : nodeRowCountArray) {
				rowCount += nodeRowCount + 1;
				titleRowNumList.add(rowCount);
			}
			LogUtils2.d("rowCount=="+rowCount);
			this.notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			if (titleRowNumList.contains(position)) {
				return TYPE_TITLE;
			} else {
				return TYPE_ITEM;
			}

		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			return rowCount;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ImageView imageView = new ImageView(getActivity());
			LogUtils2.d("position=="+position);
			imageView.setImageResource(images[position%rowCount]);
			return imageView;
			/*View view = convertView;
			ListViewHolder holder;
			int type = getItemViewType(position);
			String title;
			if (view == null) {
				holder = new ListViewHolder();
				switch (type) {
				case TYPE_TITLE:
					view = LayoutInflater.from(context).inflate(R.layout.vod_listview_title, parent, false);
					holder.Title = (TextView) view.findViewById(R.id.title);
					holder.SubTitle = (TextView) view.findViewById(R.id.sub_title);
					holder.separatorView = view.findViewById(R.id.separator);
					break;
				case TYPE_ITEM:
					view = LayoutInflater.from(context).inflate(R.layout.vod_landing_list_item, parent, false);
					landingListItemSubLayout1 = view.findViewById(R.id.landingListItem1);
					landingListItemSubLayout2 = view.findViewById(R.id.landingListItem2);
					landingListItemSubLayout3 = view.findViewById(R.id.landingListItem3);
					landingListItemSubLayout4 = view.findViewById(R.id.landingListItem4);
					landingListItemSubLayout5 = view.findViewById(R.id.landingListItem5);

					holder.ItemImage1 = (ImageView) landingListItemSubLayout1.findViewById(R.id.imageView);
					holder.ItemText1 = (TextView) landingListItemSubLayout1.findViewById(R.id.textView);
					holder.ItemImage2 = (ImageView) landingListItemSubLayout2.findViewById(R.id.imageView);
					holder.ItemText2 = (TextView) landingListItemSubLayout2.findViewById(R.id.textView);
					holder.ItemImage3 = (ImageView) landingListItemSubLayout3.findViewById(R.id.imageView);
					holder.ItemText3 = (TextView) landingListItemSubLayout3.findViewById(R.id.textView);
					holder.ItemImage4 = (ImageView) landingListItemSubLayout4.findViewById(R.id.imageView);
					holder.ItemText4 = (TextView) landingListItemSubLayout4.findViewById(R.id.textView);
					holder.ItemImage5 = (ImageView) landingListItemSubLayout5.findViewById(R.id.imageView);
					holder.ItemText5 = (TextView) landingListItemSubLayout5.findViewById(R.id.textView);

					LayoutUtils.setLayoutParams(holder.ItemImage1, null, listItemWidth, listItemHeight);
					LayoutUtils.setLayoutParams(holder.ItemImage2, null, listItemWidth, listItemHeight);
					LayoutUtils.setLayoutParams(holder.ItemImage3, null, listItemWidth, listItemHeight);
					LayoutUtils.setLayoutParams(holder.ItemImage4, null, listItemWidth, listItemHeight);
					LayoutUtils.setLayoutParams(holder.ItemImage5, null, listItemWidth, listItemHeight);

					break;
				}
				view.setTag(holder);
			} else {
				holder = (ListViewHolder) view.getTag();
			}

			if (TYPE_TITLE == type) {
				for (int i = 0; i < titleRowNumList.size() - 1; i++) {
					if (position == titleRowNumList.get(i)) {
						holder.Title.setText(nodeDataList.get(i).getTilte());
						if (position == 0) {
							holder.separatorView.setVisibility(View.GONE);
						} else {
							holder.separatorView.setVisibility(View.VISIBLE);
						}
						if (nodeDataList.get(i).getTilte() == null || nodeDataList.get(i).getTilte() == "") {
							holder.Title.setVisibility(View.GONE);
						} else {
							holder.Title.setVisibility(View.VISIBLE);
						}
						if(nodeDataList.get(i).getSubTilte() != null && nodeDataList.get(i).getSubTilte() != ""){
							holder.SubTitle.setText(nodeDataList.get(i).getSubTilte());
							holder.SubTitle.setVisibility(View.VISIBLE);
							LayoutParams params = (RelativeLayout.LayoutParams)holder.SubTitle.getLayoutParams();
							if(isTablet){
								params.addRule(RelativeLayout.RIGHT_OF, R.id.title);
							}else {
								params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
							}
							holder.SubTitle.setLayoutParams(params); 
						}else{
							holder.SubTitle.setVisibility(View.GONE);
						}

						break;
					}
				}

			} else if (TYPE_ITEM == type) {
				int curResNum, nodeStartRow = 0;
				VODNodeData nodeData = null;
				for (int i = 0; i < titleRowNumList.size(); i++) {
					if (position < titleRowNumList.get(i)) {
						nodeStartRow = titleRowNumList.get(i - 1) + 1;
						nodeData = nodeDataList.get(i - 1);
						break;
					}
				}
				if (nodeData != null) {
					curResNum = (position - nodeStartRow) * everyRowNum;
					if (curResNum < nodeData.getNodeNum()) {
						holder.ItemImage1.setVisibility(View.VISIBLE);// visiable
						holder.ItemText1.setVisibility(View.VISIBLE);
						if (curResNum < nodeData.getSerierNodeList().size()) {
							holder.ItemImage1.setOnClickListener(new VODOnClickListener(nodeData.getSerierNodeList().get(curResNum).getNodeId(), null));
							if (isMovie) {
								imageLoader.setRemoteImage(
										holder.ItemImage1,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg1Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(
										holder.ItemImage1,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg2Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getSerierNodeList().get(curResNum).getBreakdownSeriesName() + " S"
									+ nodeData.getSerierNodeList().get(curResNum).getSeasonNumber();
							holder.ItemText1.setText(title);
						} else {
							holder.ItemImage1.setOnClickListener(new VODOnClickListener(null, nodeData.getProductNodeList()
									.get(curResNum - nodeData.getSerierNodeList().size()).getEpisodeId()));
							if (isMovie) {
								imageLoader.setRemoteImage(holder.ItemImage1, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg1Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(holder.ItemImage1, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg2Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getBreakdownProductName();
							holder.ItemText1.setText(title);
						}
					} else {
						holder.ItemImage1.setVisibility(View.GONE);// GONE
						holder.ItemText1.setVisibility(View.GONE);
					}

					curResNum = (position - nodeStartRow) * everyRowNum + 1;
					if (curResNum < nodeData.getNodeNum()) {
						holder.ItemImage2.setVisibility(View.VISIBLE);// visiable
						holder.ItemText2.setVisibility(View.VISIBLE);
						if (curResNum < nodeData.getSerierNodeList().size()) {
							holder.ItemImage2.setOnClickListener(new VODOnClickListener(nodeData.getSerierNodeList().get(curResNum).getNodeId(), null));
							if (isMovie) {
								imageLoader.setRemoteImage(
										holder.ItemImage2,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg1Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(
										holder.ItemImage2,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg2Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getSerierNodeList().get(curResNum).getBreakdownSeriesName() + " S"
									+ nodeData.getSerierNodeList().get(curResNum).getSeasonNumber();
							holder.ItemText2.setText(title);
						} else {
							holder.ItemImage2.setOnClickListener(new VODOnClickListener(null, nodeData.getProductNodeList()
									.get(curResNum - nodeData.getSerierNodeList().size()).getEpisodeId()));
							if (isMovie) {
								imageLoader.setRemoteImage(holder.ItemImage2, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg1Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(holder.ItemImage2, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg2Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getBreakdownProductName();
							holder.ItemText2.setText(title);
						}
					} else {
						holder.ItemImage2.setVisibility(View.GONE);// GONE
						holder.ItemText2.setVisibility(View.GONE);
					}

					curResNum = (position - nodeStartRow) * everyRowNum + 2;
					if (curResNum < nodeData.getNodeNum()) {
						holder.ItemImage3.setVisibility(View.VISIBLE);// visiable
						holder.ItemText3.setVisibility(View.VISIBLE);
						if (curResNum < nodeData.getSerierNodeList().size()) {
							holder.ItemImage3.setOnClickListener(new VODOnClickListener(nodeData.getSerierNodeList().get(curResNum).getNodeId(), null));
							if (isMovie) {
								imageLoader.setRemoteImage(
										holder.ItemImage3,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg1Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(
										holder.ItemImage3,
										ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getSerierNodeList().get(curResNum).getHdImg2Path(),
												imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getSerierNodeList().get(curResNum).getBreakdownSeriesName() + " S"
									+ nodeData.getSerierNodeList().get(curResNum).getSeasonNumber();
							holder.ItemText3.setText(title);
						} else {
							holder.ItemImage3.setOnClickListener(new VODOnClickListener(null, nodeData.getProductNodeList()
									.get(curResNum - nodeData.getSerierNodeList().size()).getEpisodeId()));
							if (isMovie) {
								imageLoader.setRemoteImage(holder.ItemImage3, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg1Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							} else {
								imageLoader.setRemoteImage(holder.ItemImage3, ImageUtil.getLandingPageVODListCellImageUrl(
										nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getWebImg2Path(),
										imageLoader.getRequestMinWidth(), isTablet, isMovie));
							}
							title = nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getBreakdownProductName();
							holder.ItemText3.setText(title);
						}
					} else {
						holder.ItemImage3.setVisibility(View.GONE);// GONE
						holder.ItemText3.setVisibility(View.GONE);
					}
					if (isTablet) {
						curResNum = (position - nodeStartRow) * everyRowNum + 3;
						if (curResNum < nodeData.getNodeNum()) {
							holder.ItemImage4.setVisibility(View.VISIBLE);// visiable
							holder.ItemText4.setVisibility(View.VISIBLE);
							if (curResNum < nodeData.getSerierNodeList().size()) {
								holder.ItemImage4.setOnClickListener(new VODOnClickListener(nodeData.getSerierNodeList().get(curResNum).getNodeId(), null));
								if (isMovie) {
									imageLoader.setRemoteImage(holder.ItemImage4, ImageUtil.getLandingPageVODListCellImageUrl(
											nodeData.getSerierNodeList().get(curResNum).getHdImg1Path(), imageLoader.getRequestMinWidth(), isTablet, isMovie));
								} else {
									imageLoader.setRemoteImage(holder.ItemImage4, ImageUtil.getLandingPageVODListCellImageUrl(
											nodeData.getSerierNodeList().get(curResNum).getHdImg2Path(), imageLoader.getRequestMinWidth(), isTablet, isMovie));
								}
								title = nodeData.getSerierNodeList().get(curResNum).getBreakdownSeriesName() + " S"
										+ nodeData.getSerierNodeList().get(curResNum).getSeasonNumber();
								holder.ItemText4.setText(title);
							} else {
								holder.ItemImage4.setOnClickListener(new VODOnClickListener(null, nodeData.getProductNodeList()
										.get(curResNum - nodeData.getSerierNodeList().size()).getEpisodeId()));
								if (isMovie) {
									imageLoader.setRemoteImage(holder.ItemImage4, ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getProductNodeList()
											.get(curResNum - nodeData.getSerierNodeList().size()).getWebImg1Path(), imageLoader.getRequestMinWidth(), isTablet,
											isMovie));
								} else {
									imageLoader.setRemoteImage(holder.ItemImage4, ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getProductNodeList()
											.get(curResNum - nodeData.getSerierNodeList().size()).getWebImg2Path(), imageLoader.getRequestMinWidth(), isTablet,
											isMovie));
								}

								title = nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getBreakdownProductName();
								holder.ItemText4.setText(title);
							}
						} else {
							holder.ItemImage4.setVisibility(View.GONE);// GONE
							holder.ItemText4.setVisibility(View.GONE);
						}
					} else {
						landingListItemSubLayout4.setVisibility(View.GONE);// GONE
					}

					if (isTablet && isMovie) {
						curResNum = (position - nodeStartRow) * everyRowNum + 4;
						if (curResNum < nodeData.getNodeNum()) {
							holder.ItemImage5.setVisibility(View.VISIBLE);// visiable
							holder.ItemText5.setVisibility(View.VISIBLE);
							if (curResNum < nodeData.getSerierNodeList().size()) {
								holder.ItemImage5.setOnClickListener(new VODOnClickListener(nodeData.getSerierNodeList().get(curResNum).getNodeId(), null));
								if (isMovie) {
									imageLoader.setRemoteImage(holder.ItemImage5, ImageUtil.getLandingPageVODListCellImageUrl(
											nodeData.getSerierNodeList().get(curResNum).getHdImg1Path(), imageLoader.getRequestMinWidth(), isTablet, isMovie));
								} else {
									imageLoader.setRemoteImage(holder.ItemImage5, ImageUtil.getLandingPageVODListCellImageUrl(
											nodeData.getSerierNodeList().get(curResNum).getHdImg2Path(), imageLoader.getRequestMinWidth(), isTablet, isMovie));
								}
								title = nodeData.getSerierNodeList().get(curResNum).getBreakdownSeriesName() + " S"
										+ nodeData.getSerierNodeList().get(curResNum).getSeasonNumber();
								holder.ItemText5.setText(title);
							} else {
								holder.ItemImage5.setOnClickListener(new VODOnClickListener(null, nodeData.getProductNodeList()
										.get(curResNum - nodeData.getSerierNodeList().size()).getEpisodeId()));
								if (isMovie) {
									imageLoader.setRemoteImage(holder.ItemImage5, ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getProductNodeList()
											.get(curResNum - nodeData.getSerierNodeList().size()).getWebImg1Path(), imageLoader.getRequestMinWidth(), isTablet,
											isMovie));
								} else {
									imageLoader.setRemoteImage(holder.ItemImage5, ImageUtil.getLandingPageVODListCellImageUrl(nodeData.getProductNodeList()
											.get(curResNum - nodeData.getSerierNodeList().size()).getWebImg2Path(), imageLoader.getRequestMinWidth(), isTablet,
											isMovie));
								}
								title = nodeData.getProductNodeList().get(curResNum - nodeData.getSerierNodeList().size()).getBreakdownProductName();
								holder.ItemText5.setText(title);
							}
						} else {
							holder.ItemImage5.setVisibility(View.GONE);// GONE
							holder.ItemText5.setVisibility(View.GONE);
						}
					} else {
						landingListItemSubLayout5.setVisibility(View.GONE);// GONE
					}
				}
			}*/
//			return null;
		}

	}
	
	static class ListViewHolder {
		ImageView ItemImage1;
		TextView ItemText1;
		ImageView ItemImage2;
		TextView ItemText2;
		ImageView ItemImage3;
		TextView ItemText3;
		ImageView ItemImage4;
		TextView ItemText4;
		ImageView ItemImage5;
		TextView ItemText5;

		TextView Title;
		TextView SubTitle;
		View separatorView;
	}

	/**
	 * 
	 */
	public class VODOnClickListener implements OnClickListener {
		private String category_node_id, product_node_id;

		public VODOnClickListener(String category_node_id, String product_node_id) {
			super();
			this.category_node_id = category_node_id;
			this.product_node_id = product_node_id;
		}

		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString("category_node_id", category_node_id);
			bundle.putString("product_node_id", product_node_id);
		/*	if (isTablet) {
				VODDetailFragment vodDetailFragment = new VODDetailFragment();
				vodDetailFragment.setArguments(bundle);
				MTNApplication.startFragment(getCurFragment(), vodDetailFragment);
			} else {
				VODDetailPhoneFragment vodDetailPhoneFragment = new VODDetailPhoneFragment();
				vodDetailPhoneFragment.setArguments(bundle);
				MTNApplication.startFragment(getCurFragment(), vodDetailPhoneFragment);
			}*/
		}

	}
}
