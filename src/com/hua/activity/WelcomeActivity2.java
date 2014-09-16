package com.hua.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.hua.app.BaseActivity;
import com.hua.contants.Constant;
import com.hua.util.LogUtils2;
import com.hua.util.MyImageLoader;
import com.hua.util.MyImageLoader.MyLoadImageTask;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 刚打开时显示的页面，后台在加载数据    啦啦啦德玛西亚   啦啦啦啦啦啦的玛西亚
 * 提交ok？lalalallalas大三大四的
 * @author Hua
 *
 */
public class WelcomeActivity2 extends BaseActivity {


	private TextView welcomeTextView;
	private boolean isOver;
	private Animation animation1;
	int count;
	private MyLoadImageTask task;
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			
			switch (msg.what) {
			case 5:
				try {
//					Thread.sleep(2000);
//					welcomeTextView.startAnimation(animation1);
					welcomeTextView.setVisibility(View.VISIBLE);
					   AnimatorSet set = new AnimatorSet();
				        //包含平移、缩放和透明度动画
				        set.playTogether(
//				                ObjectAnimator.ofFloat(welcomeTextView, "translationX", 0, 80),
				                ObjectAnimator.ofFloat(welcomeTextView, "translationY", 0, 80),
				                ObjectAnimator.ofFloat(welcomeTextView, "scaleX", 0f, 1f),
				                ObjectAnimator.ofFloat(welcomeTextView, "scaleY", 0f, 1f),
				                ObjectAnimator.ofFloat(welcomeTextView, "alpha", 0f, 1));
				        //动画周期为500ms
							set.setInterpolator(OvershootInterpolator.class.newInstance());
				        set.setDuration(1000).start();
				        set.addListener(new AnimatorListener() {
							
							@Override
							public void onAnimationStart(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animator arg0) {
								// TODO Auto-generated method stub
								LogUtils2.d("+++++++++++++");
								Intent intent = new Intent(getBaseContext(), MainActivityPhone.class);
//								Intent intent = new Intent(getBaseContext(), WelcomeActivity.class);
								startActivity(intent);
								finish();
							}
							
							@Override
							public void onAnimationCancel(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				
				
				
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
		setContentView(R.layout.welcome_activity2);
		welcomeTextView = (TextView) findViewById(R.id.welcome_text);
//		animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
		
		loadBannerImages();
		
		new Thread(){
			
			public void run() {
				try {
					Thread.sleep(3000);
					
//					welcomeTextView.setAlpha(0.0f);
//					handler.obtainMessage().sendToTarget();
					handler.sendMessage(handler.obtainMessage(5));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			};
			
		}.start();
	}

	private void loadBannerImages() {
		// TODO Auto-generated method stub
		LogUtils2.e("***1111111111111*****");
		List<String> imageUrls = Arrays.asList(Constant.BANNER_URLS);
		int width = getResources().getDisplayMetrics().widthPixels;
		
		MyImageLoader myImageLoader = MyImageLoader.getInstance(getBaseContext());
		
		for (int i = 0; i < 5; i++) {
			/*MyLoadImageTask*/ task = new MyLoadImageTask(null,imageUrls,width,getApplicationContext());
//			taskCollection.add(task);
			LogUtils2.e("***1111333111*****");
			task.tempTtaskCollection.add(task);
			task.execute(i);
//			handler.sendMessage(handler.obtainMessage(3));
		}
		
	}


	
}
