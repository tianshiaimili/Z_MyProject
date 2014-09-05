package com.hua.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hua.app.BaseActivity;
import com.hua.util.LogUtils2;

/**
 * 刚打开时显示的页面，后台在加载数据    
 * @author Hua
 *
 */
public class WelcomeActivity extends BaseActivity implements AnimationListener{


	private LinearLayout welcomeLayout;
	private TextView welcomeTextView;
	private TextView text1,text2,text3,text4,text5;
	private boolean isOver;
	Animation animation1,animation2,animation3,animation4,animation5;
	int count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_activity);
		
		///
		welcomeLayout = (LinearLayout) findViewById(R.id.welcome_sub_laytou);
		welcomeTextView = (TextView) findViewById(R.id.welcome_textview);
		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);
		text5 = (TextView) findViewById(R.id.text5);
		///
		Animation animationw = AnimationUtils.loadAnimation(getBaseContext(), R.animator.welcome_animation);
		animationw.setFillAfter(true);
		animationw.setInterpolator(new LinearInterpolator());
		animationw.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				welcomeTextView.setVisibility(View.VISIBLE);
			}
		});
		
//		TranslateAnimation translateAnimation = 
		///
		
		animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
//		animation2.setFillAfter(true);
		animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
		animation2.setAnimationListener(this);
//		text1.startAnimation(animation2);
		animation3 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
		animation4 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
		animation5 = AnimationUtils.loadAnimation(getBaseContext(), R.animator.shake);
		new Thread(){
			@Override
		
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				
				while(count < 5){
					switch (count++) {
					case 0:
						text1.startAnimation(animation1);
						break;
					case 1:
						text2.startAnimation(animation2);
						break;
					case 2:
						text3.startAnimation(animation3);
						break;
					case 3:
						text4.startAnimation(animation4);
						break;
					case 4:
						text5.startAnimation(animation5);
						break;
					default:
						break;
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}.start();;;
//		text2.startAnimation(animation2);
//		text3.startAnimation(animation2);
//		text4.startAnimation(animation2);
//		text5.startAnimation(animation2);
//		text1.setVisibility(View.GONE);
		
		
//		welcomeLayout.setAnimation(animation);
//		welcomeLayout.startAnimation(animation);
		
	
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
//		animation.
		
//		if(!isOver){
//			
//			text2.startAnimation(animation2);
//			
//		}
		LogUtils2.i("===================");
//		switch (count++) {
//		case 0:
//			text2.startAnimation(animation2);
//			break;
//		case 1:
//			text3.startAnimation(animation2);
//			break;
//		case 2:
//			text4.startAnimation(animation2);
//			break;
//		case 3:
//			text5.startAnimation(animation2);
//			break;
//		default:
//			break;
//		}
		count ++ ;
		isOver = true;
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
	}
	
}
