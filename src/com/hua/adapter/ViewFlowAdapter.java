package com.hua.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.contants.Constant;
import com.hua.utils.LogUtils2;

/**
 * 这是作为首页 banner 的adapter
 * @author zero
 *
 */
public class ViewFlowAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private static final int[] ids = { R.drawable.test1, R.drawable.test2,
			R.drawable.test3,R.drawable.test1 ,R.drawable.test2,R.drawable.xianjian1,R.drawable.test3,R.drawable.test1};
	private String[] urls = {"url1","url2","url3","url4"};

	private Context mContext;
	private static List<Bitmap> mBitmaps;
	
	public ViewFlowAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		
		if(Constant.homeBannerBitmaps != null){
			
			mBitmaps = Constant.homeBannerBitmaps.subList(0, 5);
			
		}
		
	}

	
	public void setAdapterData(List<Bitmap> bitmaps){
		if(bitmaps != null){
			mBitmaps = bitmaps;
		}else {
			LogUtils2.e("error ----- not data");
		}
		
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
		if(mBitmaps != null){
			((ImageView) convertView.findViewById(R.id.imgView))
			.setImageBitmap(mBitmaps.get(position % mBitmaps.size()));
		}else {
			
			((ImageView) convertView.findViewById(R.id.imgView))
			.setImageResource(ids[position % ids.length]);
		}
		
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
