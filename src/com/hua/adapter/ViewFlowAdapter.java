package com.hua.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.hua.activity.DetailsActivity_;
import com.hua.activity.ImageDetailActivity_;
import com.hua.activity.R;
import com.hua.app.BaseActivity2;
import com.hua.bean.NewModle;
import com.hua.contants.Constant;
import com.hua.contants.Url;
import com.hua.utils.LogUtils2;
import com.hua.utils.Options;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
	protected HashMap<String, NewModle> newHashMap;
//	protected List<NewModle> listsModles;
	 protected HashMap<String, String> url_maps;
	private ImageLoader mImageLoader;
	private static ViewFlowAdapter mViewFlowAdapter;
	
	/**
	 * 获取实例
	 * @param context
	 * @return
	 */
	public static  ViewFlowAdapter getInstance(Context context){
		if(mViewFlowAdapter == null){
			mViewFlowAdapter = new ViewFlowAdapter(context);
		}
		return mViewFlowAdapter;
	}
	
	public ViewFlowAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mImageLoader = ImageLoader.getInstance();
		newHashMap = new HashMap<String, NewModle>();
//		listsModles = new ArrayList<NewModle>();
		url_maps = new HashMap<String, String>();
		if(Constant.homeBannerBitmaps != null){
			
			mBitmaps = Constant.homeBannerBitmaps.subList(0, 5);
			
		}
		
	}

	public ViewFlowAdapter(Context context,HashMap<String, NewModle> newHashMap,List<NewModle> listsModles){
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mImageLoader = ImageLoader.getInstance();
		if(Constant.homeBannerBitmaps != null){
			
			mBitmaps = Constant.homeBannerBitmaps.subList(0, 5);
			
		}
		
	}
	
	
	public void setAdapterData(List<Bitmap> bitmaps){
		if(bitmaps != null){
			mBitmaps = bitmaps;
			notifyDataSetChanged();
		}else {
			LogUtils2.e("error ----- not data");
		}
		
	}
	
	public void setAdapterData(final HashMap<String, NewModle> tempNewHashMap,final HashMap<String, String> tempUrl_maps){
	
//		new Handler(Looper.getMainLooper()){
//			
//		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(newHashMap != null){
					newHashMap = tempNewHashMap;
					LogUtils2.e("tempNewHashMap.size== "+tempNewHashMap.size());
				}else {
					newHashMap = new HashMap<String, NewModle>();
					LogUtils2.e("error ----- not data");
				}
				
				if(url_maps != null){
					url_maps = tempUrl_maps;
					LogUtils2.e("tempUrl_maps.size== "+tempUrl_maps.size()+"   oo=="+tempUrl_maps.get("0"));
				}
//				notifyDataSetChanged();
				notifyDataSetInvalidated();
			}
		}, 1000);
		
		
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
		
		if(url_maps.size() > 0 && newHashMap.size() > 0){
			LogUtils2.e("urlPath=="+url_maps.get((position % url_maps.size())+""));
//			((ImageView) convertView.findViewById(R.id.imgView))
//			.setImageBitmap(mImageLoader.loadImageSync(url_maps.get((position % url_maps.size())+""), Options.getListOptions()));
			((ImageView) convertView.findViewById(R.id.imgView))
			.setImageBitmap(mBitmaps.get(position % mBitmaps.size()));
		}else if(mBitmaps != null){
			((ImageView) convertView.findViewById(R.id.imgView))
			.setImageBitmap(mBitmaps.get(position % mBitmaps.size()));
		}else {
			((ImageView) convertView.findViewById(R.id.imgView))
			.setImageResource(ids[position % ids.length]);
		}
		
//		if(mBitmaps != null){
//			((ImageView) convertView.findViewById(R.id.imgView))
//			.setImageBitmap(mBitmaps.get(position % mBitmaps.size()));
//		}else {
//			
//			((ImageView) convertView.findViewById(R.id.imgView))
//			.setImageResource(ids[position % ids.length]);
//		}
		
//		convertView.setOnClickListener(new MyOnClickListener(urls[position % urls.length]));
		if(url_maps.size() > 0 && newHashMap.size() > 0){
			
			convertView.setOnClickListener(new MyOnClickListener(newHashMap.get(url_maps.get((position % url_maps.size())+""))));
		}else {
//			convertView.setOnClickListener(new MyOnClickListener(urls[position % urls.length]));
		}
		return convertView;
	}

	
	class MyOnClickListener implements OnClickListener{

		String murl;
		NewModle mNewModle;
		
		public MyOnClickListener(NewModle tempNewModle){
			this.mNewModle = tempNewModle;
		}
		
		@Override
		public void onClick(View v) {

//			Toast.makeText(mContext, ""+murl, 300).show();
			 enterDetailActivity(mNewModle);
		}
		

//	    @Override
//	    public void onSliderClick(BaseSliderView slider) {
//	        NewModle newModle = newHashMap.get(slider.getUrl());
//	        enterDetailActivity(newModle);
//	    }
		
	}
	
	 /**
     * 点解item后跳转到详细页面
     * @param newModle
     */
    public void enterDetailActivity(NewModle newModle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("newModle", newModle);
        Class<?> class1;
//        Class<?> class2;
       if (newModle.getImagesModle() != null && newModle.getImagesModle().getImgList().size() > 1) {
    	   LogUtils2.d("ImageDetailActivity_----------------");
            class1 = ImageDetailActivity_.class;
        } else {
            class1 = DetailsActivity_.class;
        }
        ((BaseActivity2)mContext ).openActivity(class1,
                bundle, 0);
        // Intent intent = new Intent(getActivity(), class1);
        // intent.putExtras(bundle);
        // IntentUtils.startPreviewActivity(getActivity(), intent);
    }
	
}
