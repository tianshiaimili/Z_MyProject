package com.hua.fragment;

import java.lang.reflect.Field;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.util.LayoutUtils;
import com.hua.util.LogUtils2;
import com.hua.view.ElasticScrollView;
import com.hua.view.ElasticScrollView.OnRefreshListener;
import com.hua.view.InnerScrollView;

public class PlayHistoryFragment3 extends Fragment {

	private InnerScrollView innerScrollView;
	private 	ElasticScrollView elasticScrollView;
	int count;
	private boolean ischange;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtils2.d("onCreateView....................");
		View historyView = inflater.inflate(R.layout.history_fragment_main, null);
		
		elasticScrollView = (ElasticScrollView)historyView.findViewById(R.id.scrollview1);
		
		LayoutInflater inflater2 = LayoutInflater.from(getActivity());
		View historySubView = inflater2.inflate(R.layout.history_fragment_subview, null);
		
		innerScrollView = (InnerScrollView) historySubView.findViewById(R.id.scroll_2);
		final Button button = (Button) historySubView
				.findViewById(R.id.scroll_button2);
		final View content = historySubView.findViewById(R.id.scroll_content);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (content.getVisibility() == View.VISIBLE) {
					System.out.println("第2次点击");
					innerScrollView.resume();
					content.setVisibility(View.GONE);
				} else {
					System.out.println("第1次点击");
					innerScrollView.scrollTo(v);
					content.setVisibility(View.VISIBLE);
				}
			}
		});
		
		/**
		 * 
		 */
		
		elasticScrollView.addChild(historySubView, 1);
		
		/**
		 * 
		 */
		LogUtils2.d("--------------------");
	    for(int i=1;i<=5;i++){
				TextView tempTextView = new TextView(getActivity());
				tempTextView.setText("Text:" + i);
				elasticScrollView.addChild(tempTextView,1);
			}
	    LogUtils2.d("------++++++++++++-----");
		//////////////
	      final Handler handler = new Handler() {
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
							LogUtils2.i("num2==="+num2);
							//R.drawable.class.getField("xianjian"+num2+1);
							Field field = R.drawable.class.getDeclaredField("xianjian"+(num2));
//							id = Integer.parseInt((R.drawable.class.getDeclaredField("xianjian"+(num2))).get(null).toString());
						LogUtils2.i("ppppppppppppppppppp");
//						id = Integer.parseInt(field.get(null).toString());
						id = Integer.valueOf(field.getInt(null));
//						 id = Integer.valueOf(field.get(null).toString());
						LogUtils2.d("id==="+id);
						OnReceiveData(null,id);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println(e);
							OnReceiveData(null,0);
						}
						
						
						break;
					default:
						break;
					}
	        		
	        	}
	        };
		//////////////
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
	        
	        
		return historyView;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	/**
	 * 
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
			
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(num);
			LayoutUtils.setLayoutParams(imageView, null, 300, 300);
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,
//					200);
//			params.gravity = Gravity.CENTER_HORIZONTAL;
//			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			elasticScrollView.addChild(imageView, 1);
			elasticScrollView.onRefreshComplete();
			
		}
		
		if(str == null && num==0){
			
			Toast.makeText(getActivity(), "Not more data", Toast.LENGTH_SHORT).show();
			elasticScrollView.onRefreshComplete();
		}
		
	}
}
