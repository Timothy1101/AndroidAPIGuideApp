package com.timothy.android.api.activity;

import java.io.File;
import com.timothy.android.api.activity.R;
import com.timothy.android.uil.FileUtil;
import com.timothy.android.uil.SPUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.WindowManager;

public class LoadActivity extends Activity {
    
	//time for picture display
    private static final int LOAD_DISPLAY_TIME = 1500;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.load);
        
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File appHome = new File(sdPath+ File.separator + "AndroidApiGuide"); 
		
		SharedPreferences sp = this.getSharedPreferences("AndroidAPISP",0);
		
		//remove folder first for new version
		int versionCode = getVersion(getApplicationContext());
		if(versionCode == 3 && (SPUtil.getFromSP(SPUtil.FOLDE_DELETE_FLAG, sp) == null)){
			FileUtil.deleteFolder(appHome);
			SPUtil.save2SP(SPUtil.FOLDE_DELETE_FLAG, "YES", sp);
		}
		
		if(!appHome.isDirectory()){
			appHome.mkdir();
		}
		if(!appHome.canWrite()){
			appHome.setWritable(true);
		}
		
		
		String appHomePath = appHome.getAbsolutePath();
		
		SPUtil.save2SP(SPUtil.APP_HOME_PATH, appHomePath, sp);
		
		String branch = SPUtil.getFromSP(SPUtil.BRANCH_PATH_NAME, sp);
		if(branch==null) SPUtil.save2SP(SPUtil.BRANCH_PATH_NAME,"Introduction", sp);
		
		int currentIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_INDEX, sp);
		if(currentIndex==-1) SPUtil.save2SP(SPUtil.CURRENT_INDEX, 1, sp);
		
		int branchIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_BRANCH_INDEX, sp);
		if(branchIndex==-1) SPUtil.save2SP(SPUtil.CURRENT_BRANCH_INDEX, 1, sp);
		
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //Go to main activity, and finish load activity
                Intent mainIntent = new Intent(LoadActivity.this, SlidingActivity.class);
                LoadActivity.this.startActivity(mainIntent);
                LoadActivity.this.finish();
            }
        }, LOAD_DISPLAY_TIME); 
    }
    
    private int getVersion(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return manager.versionCode;
			
		} catch (NameNotFoundException e) {
			return -1;
		}
	}
}