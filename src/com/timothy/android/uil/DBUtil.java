package com.timothy.android.uil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtil {
	private static final String TAG = "DatabaseUtil";

	//Database Name
	private static final String DATABASE_NAME = "db_nav";
	//Database Version
	private static final int DATABASE_VERSION = 1;
	//Table Name
	private static final String DATABASE_TABLE = "nav_tbl";

	/**
	 * Table columns
	 */
	public static final String NAV_ID = "nav_id";
	public static final String NAV_NAME = "nav_name";
	public static final String SUP_NAV_ID = "sup_nav_id";
	public static final String NAV_URL = "url";
	public static final String NAV_IS_DISPLAY = "is_display";

	/**
	 * Database creation sql statement
	 */
	private static final String CREATE_ANSWER_TABLE = "create table if not exists "
			+ DATABASE_TABLE + "(" 
			+ NAV_ID + " integer not null,"
			+ NAV_NAME + " text,"  
			+ SUP_NAV_ID + " integer not null," 
			+ NAV_URL + " text," 
			+ NAV_IS_DISPLAY + " text," 
			+ "primary key("+ NAV_ID + ")" 
			+ ");";

	/**
	 * Context
	 */
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Inner private class. Database Helper class for creating and updating database.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		/**
		 * onCreate method is called for the 1st time when database doesn't exists.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating DataBase: " + CREATE_ANSWER_TABLE);
			db.execSQL(CREATE_ANSWER_TABLE);
		}
		/**
		 * onUpgrade method is called when database version changes.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "+ newVersion);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx the Context within which to work
	 */
	public DBUtil(Context ctx) {
		this.mCtx = ctx;
	}
	/**
	 * This method is used for creating/opening connection
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public DBUtil open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	/**
	 * This method is used for closing the connection.
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * This method is used to create/insert new record.
	 * @param name
	 * @param value
	 * @return long
	 */
	public long createAnswer(int navId, String navName,int supNavId,String navURL,String isDisplay) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(NAV_ID, navId);
		initialValues.put(NAV_NAME, navName);
		initialValues.put(SUP_NAV_ID, supNavId);
		initialValues.put(NAV_URL, navURL);
		initialValues.put(NAV_IS_DISPLAY, isDisplay);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	/**
	 * This method will delete record.
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteAnswer(int navId) {
		return mDb.delete(DATABASE_TABLE, NAV_ID + "=" + navId, null) > 0;
	}
	
	public boolean deleteAllAnswers() {
		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	/**
	 * This method will return Cursor holding all  records.
	 * @return Cursor
	 */
	public Cursor fetchAllAnswers() {
		return mDb.query(DATABASE_TABLE, new String[] {NAV_ID,NAV_NAME,SUP_NAV_ID,NAV_URL,NAV_IS_DISPLAY}, null, null, null, null, null);
	}
	
	public int fetchAllAnswersCount() {
		int sum=0;
		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] {NAV_ID,NAV_NAME,SUP_NAV_ID,NAV_URL,NAV_IS_DISPLAY}, null, null, null, null, null);
		while(cursor.moveToNext()){
			sum++;
		}
		return sum;
	}

	public Cursor fetchNav(int navId) throws SQLException {
		Log.i(TAG, "fetchAnswer()... ");
		String con = NAV_ID + "=" + navId ;
		Log.i(TAG, "con: " + con);
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {NAV_ID,NAV_NAME,SUP_NAV_ID,NAV_URL,NAV_IS_DISPLAY}, con, null,null, null, null, null);
		return mCursor;
	}
	
	public Cursor fetchSubNav(int supNavId) throws SQLException {
		Log.i(TAG, "fetchAnswer()... ");
		String con = SUP_NAV_ID + "=" + supNavId;
		Log.i(TAG, "con: " + con);
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {NAV_ID,NAV_NAME,SUP_NAV_ID,NAV_URL,NAV_IS_DISPLAY}, con, null,null, null, null, null);
		/*if (mCursor != null) {
			mCursor.moveToFirst();
		}*/
		return mCursor;
	}

	/**
	 * This method will update record.
	 * @param id
	 * @param name
	 * @param value
	 * @return boolean
	 */
	public boolean updateAnswer(int navId, String navName,int supNavId,String navURL,String isDisplay) {
		ContentValues args = new ContentValues();
		args.put(NAV_ID, navId);
		args.put(NAV_NAME, navName);
		args.put(SUP_NAV_ID, supNavId);
		args.put(NAV_URL, navURL);
		args.put(NAV_IS_DISPLAY, isDisplay);
		return mDb.update(DATABASE_TABLE,args,NAV_ID + "=" + navId, null) > 0;
	}
	
	public void printAll(){
    	Cursor cursor = fetchAllAnswers() ;
    	while(cursor.moveToNext()){
    		int navId = cursor.getInt(0);
    		String navName = cursor.getString(1);
    		int supNavId = cursor.getInt(2);
			String navUrl = cursor.getString(3);
			String isDisplay = cursor.getString(4);
			Log.i(TAG, "navId="+String.valueOf(navId)
					+" navName="+navName
					+" supNavId="+ String.valueOf(supNavId)
					+" navUrl=" + navUrl
					+" isDisplay=" + isDisplay );
		}
    	cursor.close();
	}
}
