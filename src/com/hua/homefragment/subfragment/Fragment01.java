package com.hua.homefragment.subfragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.adapter.HF01_RecommendAdapter;
import com.hua.adapter.HomeSubFragment1_GridViewAdater;
import com.hua.adapter.HomeSubViewPagerAdater;
import com.hua.androidos.HandlerTimer;
import com.hua.model.CategoryInfo;
import com.hua.model.ShopAppApplication;
import com.hua.util.LogUtils2;
import com.hua.view.ElasticScrollView;
import com.hua.view.ElasticScrollView.OnRefreshListener;
import com.hua.view.MyGridView;

/**
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 */
public class Fragment01 extends Fragment {
	
	/**
	 * 弹性scrollview
	 */
	private ElasticScrollView elasticScrollView;
	
	/**
	 * 横幅的viewpgaer 
	 */
	private ViewPager viewPager;
	
	///////
	private ImageView[] mImageViews;
	private int currentPosition = 0;
	private TextView tv_title;
	/**
	 * 分类漫画 和推荐漫画的 gridView
	 */
	private GridView gv_category;
	private MyGridView gv_recommend;
	int[] recommend_icon = new int[] { R.drawable.huoying_bg,
			R.drawable.haizie_bg, R.drawable.heizi, R.drawable.sishen_bg,
			R.drawable.huoying,R.drawable.caomao,R.drawable.diguang,R.drawable.huoying,
			R.drawable.huoying,R.drawable.caomao,R.drawable.diguang,R.drawable.huoying};
	String[] recommend_msg = new String[] { "火影忍者", "海贼王", "黑子的篮球", "死神",
			"火影忍者", "海贼王", "黑子的篮球", "死神","死神","德玛西亚","黑子的篮球", "死神"
			};
	String[] ad_text = new String[] { "火影忍者", "海贼王", "黑子的篮球", "死神","德玛西亚" };
	
	/**
	 * 用来计算分ge图片
	 */
	int count;
	private boolean ischange;
	
	/**
	 * 设计广告副 走动
	 */
	private int currentItem=0;
	private Timer timer;
	private TimerTask task;
	private boolean isCanel;
	
	private HandlerTimer handlerTimer;
	
	Handler handler = new Handler(){
		public void handleMessage(Message message) {
			int what = message.what;
    		int numchange = what;
    		LogUtils2.i("what=="+what);
    		
    		switch (what) {
			case 0:
				String str = (String)message.obj;
        		OnReceiveData(str,0);
				break;
			case 1:
				int num = message.arg1;
				ischange = !ischange;
				int num2 = ischange == true ? 1: 2;
				int id = 0;
				try {
					Field field = R.drawable.class.getDeclaredField("xianjian"+(num2));
				id = Integer.valueOf(field.getInt(null));
				OnReceiveData(null,id);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e);
					OnReceiveData(null,0);
				}
				break;
				case 6:
					if(viewPager !=null){
						
						viewPager.setCurrentItem(currentItem);
						
					}
					
			default:
				break;
			}
			
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_subfragment01, null);
		
		elasticScrollView = (ElasticScrollView) view.findViewById(R.id.home_sub_fragment01_sv);
		
		View subView = LayoutInflater.from(getActivity()).
				inflate(R.layout.home_subfragment01_item, null);
		
		initSubView(getActivity(),subView);
		
		elasticScrollView.addChild(subView, 1);
		///
		///////////////
		   elasticScrollView.setonRefreshListener(new OnRefreshListener() {
				
				@Override
				public void onRefresh() {
					LogUtils2.i("onRefresh**************");
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Message message = handler.obtainMessage(0, "new Text"+(count++));
							message.what = count % 2;
							message.arg1 = count;
							LogUtils2.i("message.what ==="+(message.what ));
							handler.sendMessage(message);
						}
					});
					thread.start();
				}
			});
		
		   /**
		    * 设置广告横幅走动
		    */
			//自动跳转广告
			task = new TimerTask() {

				@Override
				public void run() {

					synchronized (viewPager) {
						if(viewPager != null){
//							currentItem = (currentItem + 1) % ad_text.length;
							currentItem = viewPager.getCurrentItem() + 1;
						}
						
						Message message = handler.obtainMessage(6);
//						handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
						handler.sendMessage(message);
					}

				}
			};
			// /
			timer = new Timer();
			timer.schedule(task, 2000, 3000);
//			startViewPagerTimer();
			isCanel = false;
		   
		
		return view;
	}

	///
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 获得服务端广告图片，这里我们就简单的直接取本地数据
		getAdData();
		///
		getCategoryData();
		///
		getRecommendData();
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
		isCanel = true;
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		timer.cancel();
		isCanel = true;
		
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		if(timer.){
//			
//		}
//		timer.schedule(task, 2000, 3000);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isCanel){
			   /**
			    * 设置广告横幅走动
			    */
				//自动跳转广告
				task = new TimerTask() {

					@Override
					public void run() {

						LogUtils2.i("timer.schedule(task, 2000, 3000);--------------------------------");
						synchronized (viewPager) {
							if(viewPager != null){
//								currentItem = (currentItem + 1) % ad_text.length;
								currentItem = viewPager.getCurrentItem() + 1;
							}
							
							Message message = handler.obtainMessage(6);
//							handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
							handler.sendMessage(message);
						}

					}
				};
				// /
				timer = new Timer();
				timer.schedule(task, 2000, 3000);
		}
		
	}
	
	/**
	 * 初始化 子view中的content
	 * @param context
	 */
	private void initSubView(Context context,View view) {
		// TODO Auto-generated method stub
		viewPager = (ViewPager) view.findViewById(R.id.vp_ad);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		gv_category = (GridView) view.findViewById(R.id.gv_category);
		gv_recommend = (MyGridView) view.findViewById(R.id.gv_recommend);
		gv_recommend.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				LogUtils2.e("77779999977777777777");
				switch (scrollState) {
				// 当不滚动时

//				case OnScrollListener.SCROLL_STATE_IDLE:
//					// 判断滚动到底部
//					if (gv_recommend.getLastVisiblePosition() == (gv_recommend.getCount() - 1)) {
//						
////						gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//						Toast.makeText(getActivity(), "到di", 300).show();
//						gv_recommend.setfouce();
//					} else {
////						gv_recommend.getParent().requestDisallowInterceptTouchEvent(true);
//					}
//					// 判断滚动到顶部
//
//					if (gv_recommend.getFirstVisiblePosition() == 0) {
////						gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//						Toast.makeText(getActivity(), "到头", 300).show();
//						gv_recommend.setfouce();
//					} else {
//
////						gv_recommend.getParent().requestDisallowInterceptTouchEvent(true);
//					}
//
//					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					
					// 判断滚动到底部
					if (gv_recommend.getLastVisiblePosition() == (gv_recommend.getCount() - 1)) {
						
//						gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//						Toast.makeText(getActivity(), "道di", 300).show();
						gv_recommend.setfouce();
					} else {
//						gv_recommend.getParent().requestDisallowInterceptTouchEvent(true);
					}
					// 判断滚动到顶部

					if (gv_recommend.getFirstVisiblePosition() == 0) {
//						gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//						Toast.makeText(getActivity(), "道头", 300).show();
						gv_recommend.setfouce();
					} else {

//						gv_recommend.getParent().requestDisallowInterceptTouchEvent(true);
					}
					
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
//				  if(firstVisibleItem==0){  
//					  //顶部
//					  LogUtils2.i("66666666666666666");
//					  gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//		          }       
//		          if(visibleItemCount+firstVisibleItem==totalItemCount){  
//		            //底部
//		        	  gv_recommend.getParent().requestDisallowInterceptTouchEvent(false);
//		          }
				
			}
		});
		//
		createPoint(view);
	}
	
	/**
	 * 创建指示点
	 * @param view
	 */
	private void createPoint(View view) {
		// six index round point
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.llayout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 12, 0);
		mImageViews = new ImageView[5];
		for (int i = 0; i < mImageViews.length; i++) {
			mImageViews[i] = new ImageView(getActivity());
			mImageViews[i].setImageResource(R.drawable.guide_round);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setLayoutParams(lp);
			ll.addView(mImageViews[i]);
		}
		mImageViews[currentPosition].setEnabled(false);
	}
	
	/**
	 * 获得广告数据
	 */
	private void getAdData() {
		List<Integer> list = new ArrayList<Integer>();

		list.add(R.drawable.huoying);
		list.add(R.drawable.caomao);
		list.add(R.drawable.yinhun);
		list.add(R.drawable.diguang);
		list.add(R.drawable.jianxin);

		viewPager.setAdapter(new HomeSubViewPagerAdater(getActivity(), list));
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				setCurPoint(position % 5);
			}
		});

	}
	

	/**
	 * 改变显示的文字
	 * @param position
	 */
	private void setCurPoint(int position) {
		// TODO Auto-generated method stub
		if(position < 0 || position > mImageViews.length || position == currentPosition){
			return;
		}
		
		mImageViews[currentPosition].setEnabled(true);
		mImageViews[position].setEnabled(false);
		////
		
		switch (position) {
		case 0:
			tv_title.setText(ad_text[position]);
			break;
		case 1:
			tv_title.setText(ad_text[position]);
			break;
		case 2:
			tv_title.setText(ad_text[position]);
			break;
		case 3:
			tv_title.setText(ad_text[position]);
			break;
		case 4:
			tv_title.setText(ad_text[position]);
			break;
		}
		
		currentPosition = position;
	}
	
	/**
	 * 获得分类 gridView分类数据
	 */
	private void getCategoryData() {
		
		gv_category.setSelector(new ColorDrawable(Color.TRANSPARENT));
		LogUtils2.d(" getCategoryData()......");
		gv_category.setAdapter(new HomeSubFragment1_GridViewAdater(getActivity(), new ShopAppApplication().mDatas));
		
		
	}
	
	/**
	 * 获取gridView推荐漫画的数据
	 */
	private void getRecommendData() {
		final List<CategoryInfo> list2 = new ArrayList<CategoryInfo>();
		for (int i = 0; i < recommend_icon.length; i++) {
			CategoryInfo categoryInfo = new CategoryInfo();
			categoryInfo.setIcon(recommend_icon[i]);
			categoryInfo.setMsg(recommend_msg[i]);
			list2.add(categoryInfo);
		}
		gv_recommend.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_recommend.setAdapter(new HF01_RecommendAdapter(getActivity(), list2));
		gv_recommend.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "您选中："+list2.get(position).getMsg(), 300).show();
			}
		});
	}
	
	/**
	 * 模拟刷新添加图片
	 */
	protected void OnReceiveData(String str,int num) {
		if(str != null){
			
			TextView textView =  new TextView(getActivity());
			textView.setText(str.toString());
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			elasticScrollView.addChild(textView, 1);
			elasticScrollView.onRefreshComplete();
		}
		if(num !=0 ) {
			
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setLayoutParams(
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
			layout.setBackgroundColor(Color.parseColor("#CCCCCC"));
//			layout.setGravity(Gravity.CENTER_HORIZONTAL);
//			layout.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK);
			layout.setHorizontalGravity(Gravity.CENTER);
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(num);
//			LayoutUtils.setLayoutParams(imageView, null, 300, 300);
			LinearLayout.LayoutParams imaLayoutParams = 
					new LinearLayout.LayoutParams(300,300);
			imaLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
			imageView.setLayoutParams(imaLayoutParams);
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,
//					200);
//			params.gravity = Gravity.CENTER_HORIZONTAL;
//			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			layout.addView(imageView);
			elasticScrollView.addChild(layout, 1);
			elasticScrollView.onRefreshComplete();
			
		}
		
		if(str == null && num==0){
			
			Toast.makeText(getActivity(), "Not more data", Toast.LENGTH_SHORT).show();
			elasticScrollView.onRefreshComplete();
		}
		
	}
	
	/**
	 * start viewPager's timer
	 */
	public void startViewPagerTimer() {
		if (handlerTimer == null) {
			handlerTimer = new HandlerTimer(true);
			handlerTimer.scheduleRepeatExecution(new Runnable() {
				@Override
				public void run() {
//					coverFlow.setSelection((currentItem + 1) % bannerDataList.size() + 20 * bannerDataList.size());
					synchronized (viewPager) {
						if(viewPager != null){
//							currentItem = (currentItem + 1) % ad_text.length;
							currentItem = viewPager.getCurrentItem() + 1;
						}
					}
				
				}
			}, 3000, 3000);
		}
	}

	/**
	 * stop viewPager's timer
	 */
	public void stopViewPagerTimer() {
		if (handlerTimer != null) {
			handlerTimer.cancelRepeatExecution();
			handlerTimer.quit();
			handlerTimer = null;
		}

	}
	
	
}
