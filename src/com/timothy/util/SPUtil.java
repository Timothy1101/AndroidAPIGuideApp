package com.timothy.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.content.SharedPreferences;
import android.util.Log;

public class SPUtil {

	public final static String LOG_TAG = "SPUtil";

	// login infor key
	public final static String SP_KEY_MCONTENT_INDEX = "sp_key_mcontent_index";
	public final static String SP_KEY_SCONTENT_INDEX = "sp_key_scontent_index";
	public final static String SP_KEY_PAGE_INDEX = "sp_key_page_index";
	public final static String SP_KEY_READ_TIME = "sp_key_read_time";
	
	public final static String SP_KEY_OPEN_FLAG = "sp_key_open_flag";
	public final static String SP_KEY_REBOOT_FLAG = "sp_key_reboot_flag";
	
	//setting key
	public final static String SP_KEY_SYNC_FLAG = "sp_key_sync_flag";
	public final static String SP_KEY_LAST_READ_TIME = "sp_key_last_read_time";
	
	public final static String APP_HOME_PATH = "APP_HOME_PATH";
	public final static String BRANCH_PATH_NAME = "BRANCH_PATH_NAME";
	
	public final static String CHILD_FLAG = "child_flag";
	public final static String CHILD_NAME = "child_name";
	public final static String CHILD_URL = "child_url";
	
	//
	public final static String CURRENT_INDEX = "CURRENT_INDEX";
	public final static String CURRENT_BRANCH_INDEX = "CURRENT_BRANCH_INDEX";
	
	// save String to SP
	public static void save2SP(String key, String value, SharedPreferences sp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	// save boolean to SP
	public static void save2SP(String key, boolean value, SharedPreferences sp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	// save float to SP
	public static void save2SP(String key, float value, SharedPreferences sp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putFloat(key, value);
		edit.commit();
	}

	// save int to SP
	public static void save2SP(String key, int value, SharedPreferences sp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	// save long to SP
	public static void save2SP(String key, long value, SharedPreferences sp) {
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong(key, value);
		edit.commit();
	}

	// save String to SP
	public static void append2SP(String key, String value, SharedPreferences sp) {

		String oldValue = SPUtil.getFromSP(key, sp);
		String newValue;
		if (oldValue == null) {
			newValue = value;
		} else {
			newValue = oldValue + "," + value;
		}
		SharedPreferences.Editor edit = sp.edit();
		edit.putString(key, newValue);
		edit.commit();
	}

	// get by string
	public static String getFromSP(String key, SharedPreferences sp) {
		return sp.getString(key, null);
	}

	public static int getIntegerFromSP(String key, SharedPreferences sp) {
		return sp.getInt(key, -1);
	}

	public static long getLongFromSP(String key, SharedPreferences sp) {
		return sp.getLong(key, -1);
	}
	
	public static boolean getBooleanFromSP(String key, SharedPreferences sp) {
		return sp.getBoolean(key, false);
	}


	// clear data in SP
	public static void clearSP(SharedPreferences sp) {
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	public static String getAllSPData(SharedPreferences sp) {
		Log.i(LOG_TAG, "----------------getAllSPData-----------------");
		StringBuffer sb = new StringBuffer();
		Map<String, ?> dataInSP = sp.getAll();
		Iterator it = dataInSP.entrySet().iterator();
		int i = 1;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			sb.append(String.valueOf(i++) + ". " + key.toString() + ": "
					+ value.toString() + "\n");
		}
		return sb.toString();
	}

	public static Map<String, String> getAllSPDataMap(SharedPreferences sp) {
		Log.i(LOG_TAG, "----------------getAllSPData-----------------");
		Map<String, String> dataMap = new HashMap<String, String>();
		Map<String, ?> dataInSP = sp.getAll();
		Iterator it = dataInSP.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			dataMap.put(key.toString(), value.toString());
		}
		return dataMap;
	}
}
