package com.hua.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.app.BaseFragment;
import com.hua.utils.LogUtils2;
import com.hua.view.KeywordsView;

public class SearchFragment2 extends BaseFragment implements View.OnClickListener {
	private String[] totalKeys = null;
	/**
	 * 显示的关键搜索词
	 */
	private  String[] key_words=new String[15];
//	= { "QQ", "单机", "联网", "游戏", "美女",
//			"冒险", "uc", "手机", "app", "谷歌" };
	protected static final String TAG = "SearchPage";
	/**
	 * 内容显示的interface的KeywordsView（搜索关键字）
	 */
	private KeywordsView showKeywords = null;
	/**
	 * 内容的整体的布局
	 */
	private LinearLayout searchLayout = null;
	
	/**
	 * 手势监听器
	 */
	private GestureDetector mGestureDetector;
	/**
	 * 判断是在外页面还是内页面
	 */
	private boolean isOutter;
	
	private View mContenView;
	
//	Handler mHandler = new Handler(){
//		public void handleMessage(Message msg) {
//			
//		};
//	};
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.searchpage);
//		searchLayout = (LinearLayout) this.findViewById(R.id.searchContent);
//		
//		showKeywords = (KeywordsView) this.findViewById(R.id.word);
//		showKeywords.setDuration(2000l);
//		showKeywords.setOnClickListener(this);
//		this.mGestureDetector =new GestureDetector(new Mygdlinseter());
////		mGestureDetector = new GestureDetector(getApplicationContext(), new Mygdlinseter(), mHandler);
//		showKeywords.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//			    return mGestureDetector.onTouchEvent(event); //注册点击事件
//			}
//		});
//		isOutter = true;
//		
//		handler.sendEmptyMessage(Msg_Start_Load);
//
//		
//		
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		this.setContentView(R.layout.searchpage);
		mContenView = LayoutInflater.from(getActivity()).inflate(R.layout.searchpage, null);
		searchLayout = (LinearLayout) mContenView.findViewById(R.id.searchContent);
		
		showKeywords = (KeywordsView) mContenView.findViewById(R.id.word);
		showKeywords.setDuration(2000l);
		showKeywords.setOnClickListener(this);
		this.mGestureDetector =new GestureDetector(new Mygdlinseter());
//		mGestureDetector = new GestureDetector(getApplicationContext(), new Mygdlinseter(), mHandler);
		showKeywords.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    return mGestureDetector.onTouchEvent(event); //注册点击事件
			}
		});
		isOutter = true;
		
		handler.sendEmptyMessage(Msg_Start_Load);
		return mContenView;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	/**
	 * 获取随机的关键字集合
	 * @return
	 */
	private String[]getRandomArray(){
		if (totalKeys != null && totalKeys.length > 0) {
			LogUtils2.e("************");
			String[] keys = new String[15];
			List<String> ks = new ArrayList<String>();
			for (int i = 0; i < totalKeys.length; i++) {
				ks.add(totalKeys[i]);
			}
			for (int i = 0; i < keys.length; i++) {
				int k = (int) (ks.size() * Math.random());
				keys[i] = ks.remove(k);
				if(keys[i] == null)
					System.out.println("nulnulnulnulnul");	
			}
			System.out.println("result's length = "+keys.length);
			return keys;
		}else {
			
//			totalKeys = {"QQ", "单机", "联网", "游戏", "美女","冒险", "uc", "安卓", "app", "谷歌","多多米","财迷","快播","YY","MSN" };
			
		}
		
		
		
		return 	new String[]{ "QQ", "单机", "联网", "游戏", "美女",
				"冒险", "uc", "安卓", "app", "谷歌","多多米","财迷","快播","YY","MSN" };
	}

	private static final int Msg_Start_Load = 0x0102;
	private static final int Msg_Load_End = 0x0203;
	private static final int Msg_Loop = 1000;
	private static final int delayMillis = 1000;
	
	private LoadKeywordsTask task = null;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Msg_Start_Load:
					task = new LoadKeywordsTask();
					new Thread(task).start();
				
				break;
			case Msg_Load_End:
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_OUT);
//				handler.sendEmptyMessage(100);
				handler.sendEmptyMessageDelayed(Msg_Loop, delayMillis);
				break;
				
			case Msg_Loop:
//				key_words = new String[15]; 
//				key_words = getRandomArray();
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				LogUtils2.e("key_words.lenght=="+key_words.length);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_OUT);
//				handler.sendEmptyMessage(Msg_Loop);
				handler.sendEmptyMessageDelayed(Msg_Loop, delayMillis);
				break;
				
			}
			
		}
	};
	private class LoadKeywordsTask implements Runnable{
		@Override
		public void run() {
			try {
				
				key_words = getRandomArray();
				if(key_words.length>0)
					handler.sendEmptyMessage(Msg_Load_End);
			} catch (Exception e) {
			}
		}
	}
	private void feedKeywordsFlow(KeywordsView keyworldFlow, String[] arr) {
		for (int i = 0; i < KeywordsView.MAX; i++) {
			String tmp = arr[i];
			keyworldFlow.feedKeyword(tmp);
		}
	}

	

	class Mygdlinseter implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		@Override
		public void onShowPress(MotionEvent e) {
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		@Override
		public void onLongPress(MotionEvent e) {
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e2.getX() - e1.getX() > 100) { //右滑
				key_words = getRandomArray();
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_OUT);
				return true;
			}
			if (e2.getX() - e1.getX() < -100) {//左滑
				key_words = getRandomArray();
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_IN);
				return true;
			}
			if (e2.getY() - e1.getY() < -100) {//上滑
				key_words = getRandomArray();
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_IN);
				return true;
			}
			if (e2.getY() - e1.getY() > 100) {//下滑
				key_words = getRandomArray();
				showKeywords.rubKeywords();
				feedKeywordsFlow(showKeywords, key_words);
				showKeywords.go2Shwo(KeywordsView.ANIMATION_OUT);
				return true;
			}
			return false;
		}
	}
 

	@Override
	public void onClick(View v) {
		System.out.println("V"+v);
		// TODO Auto-generated method stub
		if(isOutter){
			isOutter = false;
		
			String kw = ((TextView) v).getText().toString();
			Log.i(TAG, "keywords = "+kw);
			if (!kw.trim().equals("")) {			
				searchLayout.removeAllViews();

			}
			handler.removeMessages(Msg_Loop);
			Toast.makeText(getActivity(), "选中的内容是：" + ((TextView) v).getText().toString(), 1)
			.show();
		}
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handler.removeMessages(Msg_Loop);
	}
	
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		handler.removeMessages(Msg_Loop);
//		
//	}
//	
//	
	@Override
	public void onStop() {
		super.onStop();
		handler.removeMessages(Msg_Loop);
	
	}
//	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	if(!handler.hasMessages(Msg_Loop)){
		handler.sendEmptyMessageDelayed(Msg_Loop, delayMillis);
	}
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(handler.hasMessages(Msg_Loop)){
			handler.removeMessages(Msg_Loop);
		}
	}
	
	/**
	 * 处理返回按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			
			if(!isOutter){
				isOutter = true;
				searchLayout.removeAllViews();
				searchLayout.addView(showKeywords);
				handler.removeMessages(Msg_Loop);
				handler.sendEmptyMessageDelayed(Msg_Loop, delayMillis);
				/**
				 * 将自身设为不可动作
				 */
			
				/**
				 * 将搜索栏清空
				 */
				
			}else{
				SearchFragment2.this.finish();
			/**
			 * 执行返回按键操作
			 */
			
			}
			
			return true;
		}
		
		return true;
	}
	
	
	

}
