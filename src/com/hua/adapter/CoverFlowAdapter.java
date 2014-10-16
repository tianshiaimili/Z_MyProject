package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hua.activity.R;
import com.hua.fragment.VODFragment;
import com.hua.model.Banner.BannerData;
import com.hua.utils.ImageUtil;
import com.hua.utils.LogUtils2;
import com.hua.weget.CoverFlow;

public class CoverFlowAdapter extends BaseAdapter {
	private Context mContext;
	private List<BannerData> bannerDataList = new ArrayList<BannerData>();
	private Bitmap watermark_banner;
	private int viewPagerWidth;
//	private SingleImageLoader viewPagerSingleLoader;
	private int imageWidth,imageHeight;
	private Bitmap bitmapWithReflection;
	
	private int[] images = {R.drawable.xianjian1,R.drawable.xianjian2,
			R.drawable.xianjian1,R.drawable.xianjian2
			,R.drawable.title_home_n};
	
	public CoverFlowAdapter(Context mContext,/*SingleImageLoader viewPagerSingleLoader,*/int imageWidth,int imageHeight) {
		super();
		this.mContext = mContext;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
//		this.viewPagerSingleLoader = viewPagerSingleLoader;
		watermark_banner = ImageUtil.getLoadingImageBitmap(mContext,R.drawable.watermark_banner);
	}

	public void setData(List<BannerData> bannerDataList) {
		this.bannerDataList = bannerDataList;
		this.notifyDataSetChanged();
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
		 ImageView bannerImage = (ImageView) convertView;
		 if(bannerImage == null){
			 bannerImage = new ImageView(mContext);
			 bannerImage = createReflectedImages(mContext,R.drawable.watermark_banner);
			 bannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
		 }

		 if(bannerDataList.size() != 0){
			 LogUtils2.e("bannerDataList=="+bannerDataList.size());
			 int imagePosition = position % bannerDataList.size();
			 LogUtils2.e("imagePosition=="+imagePosition);
			 bannerImage.setImageResource(images[imagePosition]);
//			 viewPagerSingleLoader.setRemoteImage(bannerImage,
//						ImageUtil.getImageUrl(bannerDataList.get(imagePosition).getImage1(), imageWidth, VODFragment.BANNER_WIDTH_TABLET, VODFragment.BANNER_HEIGHT_TABLET),
//						watermark_banner, viewPagerWidth);
		 }
	     // 设置的抗锯齿

//	     BitmapDrawable drawable = (BitmapDrawable) bannerImage.getDrawable();
//	     drawable.setAntiAlias(true);
	     return bannerImage;
	}

	
	public float getScale(boolean focused, int offset) {
	     return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	 }

	  

	 public ImageView createReflectedImages(Context mContext,int imageId) {



	   /*  Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
	     final int reflectionGap = 4;
	     int width = originalImage.getWidth();
	     int height = originalImage.getHeight();
	     Log.v("coverFlow width", width + " ");
	     Matrix matrix = new Matrix();
	     matrix.preScale(1, -1);

//	     Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
//	             height / 2, width, height / 2, matrix, false);

//	     Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
//	             (height + height / 2), Config.ARGB_8888);
	     Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
	             (height), Config.ARGB_8888);

	     Canvas canvas = new Canvas(bitmapWithReflection);
	     // 抗锯齿效果
	     canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG));
	     canvas.drawBitmap(originalImage, 0, 0, null);

//	     Paint deafaultPaint = new Paint();
//	     canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
//	     canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

	     Paint paint = new Paint();
	     
	     LinearGradient shader = new LinearGradient(0, originalImage
	             .getHeight(), 0, bitmapWithReflection.getHeight()
	             + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
	     paint.setShader(shader);
	     paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	     canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
	             + reflectionGap, paint);
*/


		 if(bitmapWithReflection == null){
			 bitmapWithReflection =  getImageBitmap(mContext,imageId);
		 }
		 
	     ImageView imageView = new ImageView(mContext);
	     imageView.setLayoutParams(new CoverFlow.LayoutParams(imageWidth, imageHeight));
	     imageView.setImageBitmap(bitmapWithReflection);

	     return imageView;

	 }
	 
	 public Bitmap getImageBitmap(Context mContext,int imageId){
		 Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
		return originalImage;


	 }
	
}
