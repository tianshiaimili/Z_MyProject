package com.hua.homefragment.subfragment;

import com.hua.activity.R;
import com.hua.view.PullDownListView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

/**实现listview上下回弹的效果
 * 第二个 viewpager的页面
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 */
public class FoundFragment extends Fragment {
	
	private PullDownListView listView ;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.home_found_subfragment, null);
		listView = (PullDownListView) view.findViewById(R.id.home_found_lsitview);
		listView.setAdapter(new MyAdater());
		
		return view;
	}
	
	
	class MyAdater extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 20;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(R.drawable.ic_launcher);
			
			return imageView;
		}
		
	}
	
}
