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
		 * 获取该item对应的字母在字母表中的位置
		 * 因为一个字母组可能对应多个item的 。例如A字母组，就对应了5家公司
		 */
		int section = indexer.getSectionForPosition(position);
		/**
		 * 获取该字母分组中第一条数据的位置，例如 获取B字母组中B公司开头的第一个公司的位置
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
		 * Listview中 分隔的字母栏
		 */
		TextView tvLetter;
		TextView tvCompanyName;
	}
}
