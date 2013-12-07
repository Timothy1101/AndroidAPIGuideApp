package com.timothy.android.api.custom;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.timothy.android.apiguide.R;
import com.timothy.util.NetWorkUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class CustomDialog2 extends ProgressDialog {
	
	public final static String BASE_URL = "http://developer.android.com";
	Context mContext;
	String imgURL;
	
	public CustomDialog2(Context context,String url) {
		super(context);
		mContext = context;
		imgURL = url;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog2);
		ImageView myImage = (ImageView) findViewById(R.id.myImage);
		
		if(NetWorkUtil.isNetworkAvailable(mContext)){
			Bitmap btImage = getHttpBitmap(BASE_URL + imgURL);
			myImage.setImageBitmap(btImage);
		}
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
	
	public Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
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
		return bitmap;
	}
	

}
