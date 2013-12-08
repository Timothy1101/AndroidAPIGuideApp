package com.timothy.android.uil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

//	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//	public static String getNowDate() {
//	   Date currentTime = new Date();
//	   return dateFormat.format(currentTime);
//	}
	
	public static String getNowTimeStr() {
	   Date currentTime = new Date();
	   return timeFormat.format(currentTime);
	}
	
	public static Date getNowTime() {
	   return new Date();
	}
}
