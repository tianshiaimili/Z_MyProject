package com.hua.fragment;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.hua.activity.R;
import com.hua.util.LayoutUtils;
import com.hua.util.LogUtils2;
import com.hua.view.ElasticScrollView;
import com.hua.view.ElasticScrollViewTuanGou;
import com.hua.view.ElasticScrollViewTuanGou.OnRefreshListener;

public class TuanGouFragment extends Fragment {
	
	private ElasticScrollViewTuanGou elasticScrollViewTuanGou;
	int count;
	private boolean ischange;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	LogUtils2.i("onCreate........................");
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils2.d("onCreateView....................");
		View historyView = inflater.inflate(R.layout.homefragment_tuangou_, null);
		
		elasticScrollViewTuanGou = (ElasticScrollViewTuanGou)historyView.findViewById(R.id.homefragment_scrollviewtuangou);
		
		
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
//						id = Integer.parseInt((R.drawable.class.getDeclaredField("xianjian"+(num2))).get(null).toString());
					LogUtils2.i("ppppppppppppppppppp");
//					id = Integer.parseInt(field.get(null).toString());
					id = Integer.valueOf(field.getInt(null));
//					 id = Integer.valueOf(field.get(null).toString());
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
        elasticScrollViewTuanGou.setonRefreshListener(new OnRefreshListener() {
			
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
		LogUtils2.i("historyView-----"+historyView);
		return historyView;
	}
	
	/**
	 * 
	 */
	protected void OnReceiveData(String str,int num) {
		if(str != null){
			
			TextView textView =  new TextView(getActivity());
			textView.setText(str.toString());
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			elasticScrollViewTuanGou.addChild(textView, 1);
			elasticScrollViewTuanGou.onRefreshComplete();
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
			elasticScrollViewTuanGou.addChild(imageView, 1);
			elasticScrollViewTuanGou.onRefreshComplete();
			
		}
		
		if(str == null && num==0){
			
			Toast.makeText(getActivity(), "Not more data", Toast.LENGTH_SHORT).show();
			elasticScrollViewTuanGou.onRefreshComplete();
		}
		
	}
	
	
}
