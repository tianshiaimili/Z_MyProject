package com.hua.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hua.activity.R;
import com.hua.fragment.VODFragment;

public class ImageUtil {
	/**
	 * get loading Image's Bitmap
	 * @return Bitmap
	 */
	public static Bitmap getLoadingImageBitmap(Context context){
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermark_tv_detail_thumbnail);
		return bmp;
	}
	
	/**
	 * get loading Image's Bitmap
	 * @return Bitmap
	 */
	public static Bitmap getLoadingImageBitmap(Context context,int loaclResource){
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), loaclResource);
		return bmp;
	}
	
	/**
	 * add "_imageWidth_imageHeight" at the oldImageUrl
	 * @param oldImageUrl
	 * @param newImageWidth
	 * @param isMovie
	 * @return
	 */
	public static String getImageUrl(String oldImageUrl,int newImageWidth,Boolean isMovie){
		int newImageHeight;
		String newImageUrl;
		if(isMovie){
//			newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, 348, 516);
			newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, 174, 278);
		}else {
//			newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, 238, 134);
			newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, 211, 119);
		}
		newImageUrl = oldImageUrl+"_" + newImageWidth + "_" + newImageHeight;
		return newImageUrl;
	}
	
	/**
	 * add "_imageWidth_imageHeight" at the oldImageUrl
	 * @param oldImageUrl
	 * @param newImageWidth
	 * @param oldImageWidth
	 * @param oldImageHeight
	 * @return
	 */
	public static String getImageUrl(String oldImageUrl,int newImageWidth,int oldImageWidth,int oldImageHeight){
		int newImageHeight;
		String newImageUrl;
		newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, oldImageWidth, oldImageHeight);
		newImageUrl = oldImageUrl+"_" + newImageWidth + "_" + newImageHeight;
		return newImageUrl;
	}
	
	public static String getLandingPageVODListCellImageUrl(String oldImageUrl,int newImageWidth,Boolean isTablet,Boolean isMovie){
		int newImageHeight;
		String newImageUrl;
		if(isTablet){
			if(isMovie){
				newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, VODFragment.VOD_LIST_CELL_MOVIE_WIDTH_TABLET, VODFragment.VOD_LIST_CELL_MOVIE_HEIGHT_TABLET);
			}else{
				newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, VODFragment.VOD_LIST_CELL_TV_WIDTH_TABLET, VODFragment.VOD_LIST_CELL_TV_HEIGHT_TABLET);
			}
		}else{
			if(isMovie){
				newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, VODFragment.VOD_LIST_CELL_MOVIE_WIDTH_PHONE, VODFragment.VOD_LIST_CELL_MOVIE_HEIGHT_PHONE);
			}else{
				newImageHeight = FlexibleImageView.getFitHeight(newImageWidth, VODFragment.VOD_LIST_CELL_TV_WIDTH_PHONE, VODFragment.VOD_LIST_CELL_TV_HEIGHT_PHONE);
			}
		}
		newImageUrl = oldImageUrl+"_" + newImageWidth + "_" + newImageHeight;
		return newImageUrl;
		
	}
}
