/*
 * Copyright (C) 2012 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.slidingmenu.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.api.custom.CustomDialog;
import com.timothy.android.api.custom.CustomDialog2;
import com.timothy.android.apiguide.PictureActivity;
import com.timothy.android.apiguide.R;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.dao.HtmlCleanAPI2;
import com.timothy.dao.HtmlCleanAPI2.PageContent;
import com.timothy.util.SPUtil;
import com.timothy.util.StringUtil;
import com.timothy.util.TimeUtil;
import com.timothy.util.XMLParserUtil;
@Deprecated
public class PageFragment extends Fragment {
	
	public static final String LOG_TAG = "PageFragment";
	
	public static final String ERROR_MESSGE_ONE = "Your network is not available !";
	public static final String ERROR_MESSGE_TWO = "Your network is not 3G or WIFI !";
	public static final String ERROR_MESSGE_THREE = "Can not get content from ";
	public static final String ERROR_MESSGE_FOUR = "Can not parse content at ";
	public static final String ERROR_MESSGE_DEFAULT = "Unknow Error, you can report it to App Author!";

//	public static String xPath = "//div[@id='jd-content']";
	
	Context mContext;
	private TextView msgTV = null;
	private LinearLayout lineLayout = null;
//	private ProgressBar progressBar;
	
	private String baseFolder;

	public static PageFragment newInstance(int index,int pageIndex,int erroCode) {
		PageFragment f = new PageFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		args.putInt("pageIndex", pageIndex);
		args.putInt("erroCode", erroCode);
		f.setArguments(args);
		return f;
	}
    public int getIndex() {
        return getArguments().getInt("index", 0);
    }
    
    public int getPageIndex() {
        return getArguments().getInt("pageIndex", 0);
    }
    
    public int getErrorCode() {
        return getArguments().getInt("erroCode", 0);
    }
    
    SlidingActivity activity;
    SharedPreferences sp;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity().getApplicationContext();
		
		//get activity and SharedPreferences
		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		mContext = activity.getApplicationContext();
		
		View mView = inflater.inflate(R.layout.view_pager_text, null);
		lineLayout = (LinearLayout) mView.findViewById(R.id.lineLayout);
		
//		msgTV = (TextView) mView.findViewById(R.id.contentTV);
//		msgTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;  
		
		ContentBean bean = XMLParserUtil.getContentByIndex(mContext, getIndex());
		ContentBean mBean = XMLParserUtil.getContentByIndex(mContext, bean.getmIndex());
		String name = null;
		String pageNo = null;
		if (getIndex() > 0 && getPageIndex() > 0 ) {
			name = bean.getName();
//			if(bean.getType().equalsIgnoreCase("M")){
////				msgTV.setText(name);
//			}else{
				pageNo = String.valueOf(getPageIndex());
				baseFolder = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp) + File.separator;
				String xmlurl = baseFolder +  StringUtil.rmvSpace(mBean.getName()) + File.separator +  StringUtil.rmvSpace(name) + ".xml";
				Log.i(LOG_TAG,"xmlurl:"+xmlurl);
				Log.i(LOG_TAG,"pageNo:"+pageNo);
				if(new File(xmlurl).exists()){
					new GetData().execute(new String[] {xmlurl, pageNo});
				}else{
					Toast.makeText(mContext, "No Content!", Toast.LENGTH_SHORT).show();
				}
//			}
		}else{
			
			TextView failTV = new TextView(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//			lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			lp.gravity = Gravity.CENTER;
			lp.setMargins(2, 2, 2, 2); 
			failTV.setLayoutParams(lp);
//			failTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
			failTV.setPadding(3, 3, 3, 3);
			int errorCode = getErrorCode();
			switch(errorCode){
				case 1:failTV.setText(ERROR_MESSGE_ONE );break;
				case 2:failTV.setText(ERROR_MESSGE_TWO);break;
				case 3:failTV.setText(ERROR_MESSGE_THREE + ":" + bean.getUrl());break;
				case 4:failTV.setText(ERROR_MESSGE_FOUR + ":" + bean.getLocalAddr());break;
				default:failTV.setText(ERROR_MESSGE_DEFAULT);break;
			}
			failTV.setTextColor(Color.RED);
			failTV.setTextSize(18.0f);
			lineLayout.addView(failTV);
		}
		
		//Save current page
		SPUtil.save2SP(SPUtil.SP_KEY_PAGE_INDEX, getPageIndex(), sp);
		SPUtil.save2SP(SPUtil.SP_KEY_READ_TIME, TimeUtil.getNowTimeStr(), sp);
		
		return mView;
	}

	String contentStr = null;
	PageContent pc;
	
	private class GetData extends AsyncTask<String, Void, String> {
		HtmlCleanAPI2 api2;
//		String pageContentStr = null;
//		HtmlCleanAPI2.PageContent pageContent;
		
		@Override
		protected void onPreExecute() {
			// progressBar1.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i(LOG_TAG, "doInBackground()...");
			try {
				api2 = new HtmlCleanAPI2();
				String localPath = params[0];
				int pageNo = Integer.valueOf(params[1]);
				Log.i(LOG_TAG, "pageNo:"+pageNo);
				TagNode tagNode = api2.getNodeByLocal(localPath);
				if (tagNode != null ) {
					Log.i(LOG_TAG, "tagNode:"+tagNode.getText());
					pc = api2.getPage(tagNode, pageNo);
				}
			} catch (XPatherException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(LOG_TAG, "onPostExecute()...");
			if(pc!=null){
				List<HtmlCleanAPI2.HtmlContent> hcList = pc.getContentList();
				setAllComponent(hcList);
			}else{
				Log.i(LOG_TAG, "PageContent is null!");
			}
		}
	}
	
/*	private class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
		}
		@Override
		protected String doInBackground(String... params) {
			return null;
		}
		@Override
		protected void onPostExecute(String result) {

		}
	}*/
	
	public void setAllComponent(List<HtmlCleanAPI2.HtmlContent> hcList){
		Log.i(LOG_TAG, "setAllComponent()...");
		for(HtmlCleanAPI2.HtmlContent hc: hcList){
			String tag = hc.getTag();
			String content =  hc.getContent();
			Log.i(LOG_TAG, "tag:" + tag);
			Log.i(LOG_TAG, "content:" + content);
			
			if(tag.equalsIgnoreCase("P")){
				final TextView tagPTV = new TextView(mContext);
//				aaa.setBackgroundColor(Color.WHITE);
//				aaa.setBackgroundDrawable(R.drawable.tag_p_drawable);
				tagPTV.setBackgroundResource(R.drawable.tag_p_drawable);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagPTV.setLayoutParams(lp);
				tagPTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagPTV.setTextColor(Color.BLACK);
				tagPTV.setPadding(3, 3, 3, 3);
				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
				String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
				tagPTV.setText(contentRSpecial);
				tagPTV.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						openDialog(tagPTV.getText().toString());
						return false;
					}
				});
				lineLayout.addView(tagPTV);
				
			}else if(tag.equalsIgnoreCase("pre")){
				final TextView tagPre = new TextView(mContext);
				tagPre.setBackgroundColor(Color.parseColor("#D2EADE"));
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagPre.setLayoutParams(lp);
				tagPre.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagPre.setTextColor(Color.BLACK);
//				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
				String contentRSpecial = StringUtil.rmvSpecial(content);
				tagPre.setText(contentRSpecial);
				tagPre.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						openDialog(tagPre.getText().toString());
						return false;
					}
				});				
				lineLayout.addView(tagPre);
			}else if(tag.equalsIgnoreCase("dt") || tag.equalsIgnoreCase("H1") || tag.equalsIgnoreCase("H2") || tag.equalsIgnoreCase("H3")){//title
				TextView tagDtHTV = new TextView(mContext);
				tagDtHTV.setBackgroundColor(Color.DKGRAY);
				tagDtHTV.setTypeface(null,Typeface.BOLD);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 0, 2, 0); 
				tagDtHTV.setLayoutParams(lp);
				tagDtHTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagDtHTV.setTextColor(Color.WHITE);
				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
				String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
				tagDtHTV.setText(contentRSpecial);
				lineLayout.addView(tagDtHTV);
			}else if(tag.equalsIgnoreCase("dd")){
				final TextView tagDD = new TextView(mContext);
				tagDD.setBackgroundResource(R.drawable.tag_p_drawable);
//				aaa.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagDD.setLayoutParams(lp);
				tagDD.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagDD.setTextColor(Color.BLACK);
				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
				String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
				tagDD.setText(contentRSpecial);
				tagDD.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						openDialog(tagDD.getText().toString());
						return false;
					}
				});					
				lineLayout.addView(tagDD);
			}else if(tag.equalsIgnoreCase("li")){
				final TextView tagLigTV = new TextView(mContext);
				tagLigTV.setBackgroundResource(R.drawable.tag_p_drawable);
//				aaa.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagLigTV.setLayoutParams(lp);
				tagLigTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagLigTV.setTextColor(Color.BLACK);
				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
				String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
				tagLigTV.setText(contentRSpecial);
				tagLigTV.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						openDialog(tagLigTV.getText().toString());
						return false;
					}
				});					
				lineLayout.addView(tagLigTV);
			}else if(tag.equalsIgnoreCase("img")){
				final TextView imgTV = new TextView(mContext);
				imgTV.setBackgroundResource(R.drawable.tag_p_drawable);
//				aaa.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				imgTV.setLayoutParams(lp);
				imgTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				imgTV.setTextColor(Color.BLACK);
//				String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(StringUtil.mergeBlank(content)));
//				String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
				imgTV.setText(content);
				imgTV.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv = (TextView)v;
						String path = tv.getText().toString();
						if(path!=null && path.trim().length()>0){
							openDialog2(path);
						}
					}
				});					
				lineLayout.addView(imgTV);
			}
		}
	}
	
	public void openDialog(String content){
		Log.i(LOG_TAG, "openDialog()...");
		CustomDialog cusDialog = new CustomDialog(activity,R.style.custom_dialog_style,content);
		//set alpha
		Window wd = cusDialog.getWindow();
		WindowManager.LayoutParams lp = wd.getAttributes();
		lp.alpha = 1.0f;
		wd.setAttributes(lp);
		cusDialog.show();
	}
	
	public void openDialog2(String path){
//		Log.i(LOG_TAG, "openDialog()...");
//		CustomDialog2 cusDialog2 = new CustomDialog2(mContext,url);
//		cusDialog2.show();
		
        Intent intent = new Intent();
        intent.setClass(activity, PictureActivity.class);
        intent.putExtra("baseFolder", baseFolder);
        intent.putExtra("path", path);
        startActivity(intent); 
	}
	


}
