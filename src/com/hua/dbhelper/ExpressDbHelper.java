package com.hua.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpressDbHelper extends SQLiteOpenHelper{
	public static final String DB_NAME = "express.db";
	public static final int VERSION = 1;
	public static final String TABLE_COMPANY_NAME = "company";
	public static final String TABLE_COMPANY_ID = "_id";
	/**��ݹ�˾����**/
	public static final String TABLE_COMPANY_COMPANY_NAME = "company_name";
	/**��ݹ�˾��Ӧcode**/
	public static final String TABLE_COMPANY_COMPANY_CODE = "company_code";
	/**��˾���ֶ�Ӧ������ĸ**/
	public static final String TABLE_COMPANY_COMPANY_INITIAL = "initial";
	/**����**/
	public static final String TABLE_COMPANY_COMMON = "common";
	public ExpressDbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
