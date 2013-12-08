package com.timothy.android.api.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public class ActivityManage {
private static final String LOG_TAG = "ActivityStackControlUtil";
	
	private static List<Activity> activityList = new ArrayList<Activity>();
	
	public static void remove(Activity activity){
		activityList.remove(activity);
	}
	
	public static void add(Activity activity){
		activityList.add(activity);
	}
	
	public static void finishProgram() {
		Log.i(LOG_TAG, "finishProgram()...");
		int i = 1;
		for (Activity activity : activityList) {
			Log.i(LOG_TAG,(i++)+ ":"+activity.getClass().getName());
			activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		
		Log.i(LOG_TAG, "finishProgram() end");
	}
}
