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
	
	/**���鸨����**/
	private AlphabetIndexer indexer;
	/**��ĸ��**/
	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**��ĸ������ͼ**/
	private LetterView letterView;
	/**�Զ���Toast**/
	private Toast toast;
	/**Toast��ͼ�е�TextView**/
	private TextView tvToast;
	
	/**
	 * ������ListView�������ͼ
	 */
	private View viewOverlay;
	/**
	 * ������ListView�������ͼ textview��ʾ������
	 */
	private TextView tvOverlay;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.letters_activity_main, null);
		
		if(isFirstRun()){
			/**
			 * ��һ�ν����Ļ� �ȸ���db����
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

	/**��ʼ���ؼ�*/
	private void initViews(View view ) {

		viewOverlay = view.findViewById(R.id.letter_viewOverlay);
		tvOverlay = (TextView) view.findViewById(R.id.letter_tvOverlay);
		
		listView = (ListView) view.findViewById(R.id.letter_listView);
		SQLiteDatabase db = helper.getReadableDatabase();
		///
		Cursor cursor = db.query(ExpressDbHelper.TABLE_COMPANY_NAME, null, null, null, null, null, ExpressDbHelper.TABLE_COMPANY_COMPANY_INITIAL);
		//����AlphabetIndexer������Ҫcursor����������ֶ����ڵ�λ�ã���ĸ��
		
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
	 * ��ʼ����ʾ��ToasT
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
		
		//�����д��������� ��ListView������ʱ�����LetterView��ѡ����ĸλ��
		/**
		 * int section = indexer.getSectionForPosition(firstVisibleItem);
		 * ��ȡ��item��Ӧ����ĸ����ĸ���е�λ��
		 * ��Ϊһ����ĸ����ܶ�Ӧ���item�� ������A��ĸ�飬�Ͷ�Ӧ��5�ҹ�˾
		 */
		int section = indexer.getSectionForPosition(firstVisibleItem);
		letterView.setSelectedIndex(section);
		
		/**
		 * 	int pos = indexer.getPositionForSection(nextSection);
		 * ��ȡ����ĸ�����е�һ�����ݵ�λ�ã����� ��ȡB��ĸ����B��˾��ͷ�ĵ�һ����˾��λ��
		 */
		//���ü�ѹЧ��  
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
			
			//������ʾ
			viewOverlay.setPadding(0, 0, 0, 0);
			tvOverlay.setText(alphabet.charAt(section)+"");
			
		}
		
		
	}
	
    /**ΪLetterView���õļ�����**/
    private OnLetterChangeListener letterChangeListener = new OnLetterChangeListener() {
		
		@Override
		public void onLetterChange(int selectedIndex) {
			
			/**
			 * 	int pos = indexer.getPositionForSection(nextSection);
			 * ��ȡ����ĸ�����е�һ�����ݵ�λ�ã����� ��ȡB��ĸ����B��˾��ͷ�ĵ�һ����˾��λ��
			 */
			int position = indexer.getPositionForSection(selectedIndex);
			listView.setSelection(position);
			tvToast.setText(alphabet.charAt(selectedIndex)+"");
			toast.show();
			
			//����Toast��ʾ��ʽ ���ʺ����ֿ��ٱ仯�����
//			Toast.makeText(getApplicationContext(), alphabet.charAt(selectedIndex)+"", 0).show();
		}
	};
	
	
	/**
	 * ����DB��APP��
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
		
		LogUtils2.e("item�ϵ�ֵiew�ǣ�="+view+"   | ֵ�ǣ�"+textView.getText());
		LogUtils2.e("parent�ϵ�ֵiew�ǣ�="+parent+"   | �ɿ�����item��ֵ�ǣ�"+parent.getChildCount());
		LogUtils2.i("�ܵ�itemCount=="+parent.getCount());
//		LogUtils2.i(""+parent.);
//		Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
		
		
	}
	
}
