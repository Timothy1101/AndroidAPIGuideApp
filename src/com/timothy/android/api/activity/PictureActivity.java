package com.timothy.android.api.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.timothy.android.api.activity.R;
import com.timothy.android.uil.NetWorkUtil;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class PictureActivity extends Activity {
	public final static String LOG_TAG = "PictureActivity";
	//time for picture display
	public final static String BASE_URL = "http://developer.android.com";
	Context mContext;
	String imgURL;
	
	ImageView apiImage;
	ImageView closeIV;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);
        mContext = getApplicationContext();
        
        apiImage = (ImageView) findViewById(R.id.apiImage);
        
		Bundle bundle = this.getIntent().getExtras();
		String path  = bundle.getString("path");
		String baseFolder  = bundle.getString("baseFolder");
		
		Log.i(LOG_TAG, "path:" + path);
		Log.i(LOG_TAG, "baseFolder:" + baseFolder);
		
		Bitmap localBt = getLoacalBitmap(baseFolder + File.separator +  path);
        if(localBt!=null){
        	apiImage.setImageBitmap(localBt);
		}else{
			if(NetWorkUtil.isNetworkAvailable(mContext)){
				new LoadPicture().execute(new String[]{BASE_URL+ path});
			}			
		}
        
		closeIV = (ImageView) findViewById(R.id.closeIV);
		closeIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }
    
	public Bitmap getLoacalBitmap(String localUrl) {
	try {
		FileInputStream fis = new FileInputStream(localUrl);
		return BitmapFactory.decodeStream(fis);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		return null;
	}
}
	
	private class LoadPicture extends AsyncTask<String, Void, String> {
		Bitmap bitmap;
		@Override
		protected String doInBackground(String... params) {
			URL myFileUrl = null;
			try {
				Log.i("Picture",params[0]);
				myFileUrl = new URL(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setConnectTimeout(0);
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "success";
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result.equalsIgnoreCase("success")){
				apiImage.setImageBitmap(bitmap);
			}else{
				
			}
		}
	}
	

}