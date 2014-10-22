package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hua.bean.NewModle;
import com.hua.network.utils.LogUtils2;
import com.hua.view.NewItemView;
import com.hua.view.NewItemView_;

public class ProductSlectionAdapter extends BaseAdapter {
	
	private Context context;
	private List<NewModle> mDataList = new ArrayList<NewModle>();
	private LayoutInflater mInflater;
	private String currentItem;
	private static ProductSlectionAdapter mProductSlectionAdapter;
	private int mIndex = 0;
	private int oldIndex = -1;
	public ProductSlectionAdapter(Context context,List<NewModle > list) {
		this.context = context;
		this.mDataList = list;
		this.mInflater = LayoutInflater.from(context);
	}

	public ProductSlectionAdapter(Context context,int index){
		this.context = context;
		this.mIndex = index;
		if(mDataList == null){
			mDataList = new ArrayList<NewModle>();
			
		LogUtils2.i("");
		}else {
			mDataList = getAdapterDataLists();
			
		}
	}
	
	
	public static ProductSlectionAdapter instanceAdapter(Context context,int index){
		
		if(mProductSlectionAdapter == null){
			mProductSlectionAdapter = new ProductSlectionAdapter(context,index);
		}
		return mProductSlectionAdapter;
		
	}
	
	 public void appendList(List<NewModle> list,int index,boolean isUpdate) {
		 LogUtils2.d("list---"+list.size());
//	        if (!mDataList.contains(list.get(0)) && list != null && list.size() > 0) {
//	        	mDataList.addAll(list);
//	        	LogUtils2.d("mDataList---"+mDataList.size());
//	        }
		   if (isUpdate || (index != oldIndex && list != null && list.size() > 0)) {
	        	mDataList.addAll(list);
	        	LogUtils2.d("mDataList---"+mDataList.size());
	        }
	        oldIndex = index;
	        notifyDataSetChanged();
	    }
	
	/**
	 * 设置adapter的数据
	 * @param dataList
	 */
	public void setAdapterData(List<NewModle> dataList){
		if(dataList != null){
			mDataList = dataList;
			  notifyDataSetChanged();
		}else {
			LogUtils2.e("not data ------");
		}
	}
	
	/**
	 * 设置adapter中的数据集合 以便保存着状态 ，不用每次都新建
	 * @return
	 */
	public List<NewModle> getAdapterDataLists(){
		return mDataList;
	}
	
	public void setAdapterDataLists(List<NewModle> dataList){
		mDataList = dataList;
	}
	
	/**
	 * 清除adapter中的数据
	 */
	 public void clear() {
	        mDataList.clear();
	        notifyDataSetChanged();
	    }
	

	    public void currentItem(String item) {
	        this.currentItem = item;
	    }

	    @Override
	    public int getCount() {
	        return mDataList.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return mDataList.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {

	        NewItemView newItemView;

	        if (convertView == null) {
	            newItemView = NewItemView_.build(context);
	        } else {
	            newItemView = (NewItemView) convertView;
	        }

	        NewModle newModle = mDataList.get(position);
	        if (newModle.getImagesModle() == null) {
	            newItemView.setTexts(newModle.getTitle(), newModle.getDigest(),
	                    newModle.getImgsrc(), currentItem);
	        } else {
	            newItemView.setImages(newModle);
	        }

	        return newItemView;
	    }
	
	
}
