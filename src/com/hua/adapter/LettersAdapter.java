package com.hua.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.dbhelper.ExpressDbHelper;

public class LettersAdapter extends BaseAdapter{
	private Cursor cursor;
	private Context context;
	private AlphabetIndexer indexer;
	
	
	/**
	 * @param cursor
	 * @param context
	 * @param indexer
	 */
	public LettersAdapter(Cursor cursor, Context context, AlphabetIndexer indexer) {
		super();
		this.cursor = cursor;
		this.context = context;
		this.indexer = indexer;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Cursor getItem(int position) {
		cursor.moveToPosition(position);
		return cursor;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.letter_content_item, null);
			holder = new ViewHolder();
			holder.tvLetter = (TextView) convertView.findViewById(R.id.tvLetter);
			holder.tvCompanyName = (TextView) convertView.findViewById(R.id.tvCompanyName_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		cursor.moveToPosition(position);
		holder.tvCompanyName.setText(cursor.getString(cursor.getColumnIndex(ExpressDbHelper.TABLE_COMPANY_COMPANY_NAME)));
		/**
		 * ��ȡ��item��Ӧ����ĸ����ĸ���е�λ��
		 * ��Ϊһ����ĸ����ܶ�Ӧ���item�� ������A��ĸ�飬�Ͷ�Ӧ��5�ҹ�˾
		 */
		int section = indexer.getSectionForPosition(position);
		/**
		 * ��ȡ����ĸ�����е�һ�����ݵ�λ�ã����� ��ȡB��ĸ����B��˾��ͷ�ĵ�һ����˾��λ��
		 */
		int pos = indexer.getPositionForSection(section);
		if(pos == position){
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(cursor.getString(cursor.getColumnIndex(ExpressDbHelper.TABLE_COMPANY_COMPANY_INITIAL)));
		}else{
			holder.tvLetter.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder{
		/**
		 * Listview�� �ָ�����ĸ��
		 */
		TextView tvLetter;
		TextView tvCompanyName;
	}
}
