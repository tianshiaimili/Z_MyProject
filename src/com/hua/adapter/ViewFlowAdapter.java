package com.hua.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.hua.activity.R;

/**
 * 这是作为首页 banner 的adapter
 * @author zero
 *
 */
public class ViewFlowAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private static final int[] ids = { R.drawable.test1, R.drawable.test2,
			R.drawable.test3,R.drawable.test1 ,R.drawable.test2,R.drawable.xianjian1};
	private String[] urls = {"url1","url2","url3","url4"};

	private Context mContext;
	
	public ViewFlowAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.viewflow_image_item, null);
		}
		((ImageView) convertView.findViewById(R.id.imgView))
				.setImageResource(ids[position % ids.length]);
		convertView.setOnClickListener(new MyOnClickListener(urls[position % urls.length]));
		return convertView;
	}

	
	class MyOnClickListener implements OnClickListener{

		String murl;
		
		public MyOnClickListener(String url){
			this.murl = url;
		}
		
		@Override
		public void onClick(View v) {

			Toast.makeText(mContext, ""+murl, 300).show();
			
		}
		
	}
	
}
