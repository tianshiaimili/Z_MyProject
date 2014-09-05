package com.hua.homefragment.subfragment;

import java.io.File;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.adapter.LettersAdapter;
import com.hua.dbhelper.ExpressDbHelper;
import com.hua.util.CopyFile;
import com.hua.util.LogUtils2;
import com.hua.view.LetterView;
import com.hua.view.LetterView.OnLetterChangeListener;

/**
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 */
public class ContactsFragment extends Fragment implements OnScrollListener,OnItemClickListener{
	
	private ListView listView;
	private LettersAdapter adapter;
	private ExpressDbHelper helper;
	
	/**分组辅助类**/
	private AlphabetIndexer indexer;
	/**字母表**/
	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**字母导航视图**/
	private LetterView letterView;
	/**自定义Toast**/
	private Toast toast;
	/**Toast视图中的TextView**/
	private TextView tvToast;
	
	/**
	 * 覆盖在ListView上面的视图
	 */
	private View viewOverlay;
	/**
	 * 覆盖在ListView上面的视图 textview显示的内容
	 */
	private TextView tvOverlay;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.letters_activity_main, null);
		
		if(isFirstRun()){
			/**
			 * 第一次进来的话 先复制db程序
			 */
			copyDb();
		}
		
		 helper = new ExpressDbHelper(getActivity());
		///
		 initToast();
		 ///
		 initViews(contentView);
		 
		return contentView;
		
	}

	/**初始化控件*/
	private void initViews(View view ) {

		viewOverlay = view.findViewById(R.id.letter_viewOverlay);
		tvOverlay = (TextView) view.findViewById(R.id.letter_tvOverlay);
		
		listView = (ListView) view.findViewById(R.id.letter_listView);
		SQLiteDatabase db = helper.getReadableDatabase();
		///
		Cursor cursor = db.query(ExpressDbHelper.TABLE_COMPANY_NAME, null, null, null, null, null, ExpressDbHelper.TABLE_COMPANY_COMPANY_INITIAL);
		//创建AlphabetIndexer对象需要cursor对象，排序的字段所在的位置，字母表
		
		indexer = new AlphabetIndexer(cursor, cursor.getColumnIndex(ExpressDbHelper.TABLE_COMPANY_COMPANY_INITIAL), alphabet);
		
//		indexer = new AlphabetIndexer(cursor, cursor.getColumnIndex(ExpressDbHelper.TABLE_COMPANY_COMPANY_INITIAL), alphabet );
				adapter = new LettersAdapter(cursor, getActivity(), indexer);
				listView.setAdapter(adapter);
	
				letterView = (LetterView) view.findViewById(R.id.letterView);
				letterView.setOnLetterChangeListener(letterChangeListener);
				listView.setOnScrollListener(this);
				listView.setOnItemClickListener(this);
	}

	/**
	 * 初始化显示的ToasT
	 */
	private void initToast() {
		toast = new Toast(getActivity());
		View view  = LayoutInflater.from(getActivity()).inflate(R.layout.letter_toast, null);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		tvToast = (TextView) view.findViewById(R.id.tvToast);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		//这两行代表作用是 让ListView滚动的时候控制LetterView里选中字母位置
		/**
		 * int section = indexer.getSectionForPosition(firstVisibleItem);
		 * 获取该item对应的字母在字母表中的位置
		 * 因为一个字母组可能对应多个item的 。例如A字母组，就对应了5家公司
		 */
		int section = indexer.getSectionForPosition(firstVisibleItem);
		letterView.setSelectedIndex(section);
		
		/**
		 * 	int pos = indexer.getPositionForSection(nextSection);
		 * 获取该字母分组中第一条数据的位置，例如 获取B字母组中B公司开头的第一个公司的位置
		 */
		//设置挤压效果  
		int nextSection = section+1;
		int pos = indexer.getPositionForSection(nextSection);
		
		if(pos == firstVisibleItem + 1 ){
			
			View child = view.getChildAt(0);
			if(child == null){
				return;
			}
			
			int distance = child.getBottom() - tvOverlay.getHeight();
			if(distance <= 0 ){
				
				viewOverlay.setPadding(0, distance, 0, 0);
				tvOverlay.setText(alphabet.charAt(section)+"");
//				listView.postInvalidate();
			}else {
				viewOverlay.setPadding(0, 0, 0, 0);
				tvOverlay.setText(alphabet.charAt(section)+"");
			}
			
		}else {
			
			//正常显示
			viewOverlay.setPadding(0, 0, 0, 0);
			tvOverlay.setText(alphabet.charAt(section)+"");
			
		}
		
		
	}
	
    /**为LetterView设置的监听器**/
    private OnLetterChangeListener letterChangeListener = new OnLetterChangeListener() {
		
		@Override
		public void onLetterChange(int selectedIndex) {
			
			/**
			 * 	int pos = indexer.getPositionForSection(nextSection);
			 * 获取该字母分组中第一条数据的位置，例如 获取B字母组中B公司开头的第一个公司的位置
			 */
			int position = indexer.getPositionForSection(selectedIndex);
			listView.setSelection(position);
			tvToast.setText(alphabet.charAt(selectedIndex)+"");
			toast.show();
			
			//这种Toast显示方式 不适合文字快速变化的情况
//			Toast.makeText(getApplicationContext(), alphabet.charAt(selectedIndex)+"", 0).show();
		}
	};
	
	
	/**
	 * 复制DB到APP中
	 */
	private void copyDb() {
		
		File file = getActivity().getDatabasePath("express.db");
		
		CopyFile.copyFileFromResToPhone(file.getParent(), file.getName(), 
				getActivity().getResources().openRawResource(R.raw.express));
		
	}

	private boolean isFirstRun() {

		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("share", getActivity().MODE_PRIVATE);
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		Editor editor = sharedPreferences.edit();
		if(isFirstRun){
			editor.putBoolean("isFirstRun", false);
			editor.commit();
		}
		return isFirstRun;
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		
		TextView textView = (TextView) view.findViewById(R.id.tvCompanyName_item);
		
		LogUtils2.e("item上的值iew是：="+view+"   | 值是："+textView.getText());
		LogUtils2.e("parent上的值iew是：="+parent+"   | 可看见的item的值是："+parent.getChildCount());
		LogUtils2.i("总的itemCount=="+parent.getCount());
//		LogUtils2.i(""+parent.);
//		Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
		
		
	}
	
}
