package com.timothy.android.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.timothy.android.apiguide.ActivityManage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE = new CrashHandler();
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private CrashHandler() {
	}
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
			
			ActivityManage.finishProgram();
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "Sorry,please restart the application!", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		
		collectDeviceInfo(mContext);
		
		String crashInfoString =  getCrashInfo(ex);
		
		//print error message
		Log.e(TAG, crashInfoString);
		
		String fileName = saveCrashInfo2File(crashInfoString);
		if(fileName!=null) Log.e(TAG, "Log file name : "+fileName);
		
		return true;
	}
	
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	private String getCrashInfo(Throwable ex){
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		
		return sb.toString();
	}
	
	private String saveCrashInfo2File(String crashInfo) {

		try {
			 long timestamp = System.currentTimeMillis();  
	            String time = formatter.format(new Date());  
	            String fileName = "crash-" + time + "-" + timestamp + ".log";  
	            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
	                String path = Environment.getExternalStorageDirectory().getPath()+File.separator +"AndroidAPIGuide"+File.separator +"crash";  
	                File dir = new File(path);  
	                if (!dir.exists()) {  
	                    dir.mkdirs();  
	                }  
	                FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);  
	                fos.write(crashInfo.getBytes());  
	                fos.close();  
	            }  
	            return fileName;  
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
	
	public void doSendLogFile(String fileName) {
	    String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
	    String myDir = externalStorageDirectory + "/crash/";  // the file will be in myDir
	    Uri uri = Uri.parse("file://" + myDir + fileName);
	    Intent i = new Intent(Intent.ACTION_SEND);
	    i.setType("message/rfc822"); 
	    i.putExtra(Intent.EXTRA_EMAIL, new String[] { "tangqi1101@gmail.com" });
	    i.putExtra(Intent.EXTRA_SUBJECT, "the subject text");
	    i.putExtra(Intent.EXTRA_TEXT, "extra text body");
	    i.putExtra(Intent.EXTRA_STREAM, uri);
	    try {
	    	mContext.startActivity(Intent.createChooser(i, "Send mail..."));
	    } catch (android.content.ActivityNotFoundException ex) {
	        Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT) .show();
	    }
	}
	
}
