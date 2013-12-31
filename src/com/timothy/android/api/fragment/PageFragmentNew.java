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
package com.timothy.android.api.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import com.timothy.android.api.activity.R;
import com.timothy.android.api.activity.SlidingActivity;
import com.timothy.android.api.custom.CustomDialog;
import com.timothy.android.api.fragment.ViewPagesFragment.MyAdapter;
import com.timothy.android.http.HtmlCleanAPI;
import com.timothy.android.http.HtmlCleanAPI.HtmlContent;
import com.timothy.android.uil.ContentUtil;
import com.timothy.android.uil.NetWorkUtil;
import com.timothy.android.uil.SPUtil;
import com.timothy.android.uil.StringUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class PageFragmentNew extends Fragment {
	
	public static final String LOG_TAG= "PageFragmentNew";
	public static final String XPATH= "//div[@id='jd-content']";
	
	Context mContext;
	
	private ImageView showLeft;
	private TextView titleTV;
	private ImageView showRight;
	private ProgressBar progressBar;
	private MyAdapter mAdapter;
	private ViewPager mPager;
	
	SlidingActivity activity;
	SharedPreferences sp;
	int currentIndex;
	int pageSize;
	boolean errorFlag = false;
	String[] contentsArray ;
	String contents ;
	String[] contentArray ;
    String contentId;
    String contentLevel ;
    String contentSuperId ;
    String contentName ;
    String contentURL ;  
    LinearLayout lineLayout;
//	TextView testTV = null;
	
    private String baseFolder;
    
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(LOG_TAG,"onActivityCreated()...");
		showLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((SlidingActivity) getActivity()).showLeft();
			}
		});
		showRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((SlidingActivity) getActivity()).showRight();
			}
		});
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//get activity and SharedPreferences
		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		mContext = activity.getApplicationContext();
		
		View mView = inflater.inflate(R.layout.view_pager_text_new, null);
		lineLayout = (LinearLayout) mView.findViewById(R.id.lineLayout);
		
		currentIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_INDEX, sp);
		Log.i(LOG_TAG, "currentIndex:"+String.valueOf(currentIndex));
		
		showLeft = (ImageView) mView.findViewById(R.id.showLeft);
		showRight = (ImageView) mView.findViewById(R.id.showRight);
//		mPager = (ViewPager) mView.findViewById(R.id.pager);
		progressBar = (ProgressBar) mView.findViewById(R.id.loading_spinner);
		
//		String firstLoad = SPUtil.getFromSP(SPUtil.FIRST_LOAD_FLAG, sp);
//		if(currentIndex == -1){
	    if(currentIndex == -1){
//			HomeFragment homeFrag = new HomeFragment();
//			pagerItemList.add(homeFrag);
//			setAdapter();
			
			SPUtil.save2SP(SPUtil.FIRST_LOAD_FLAG, "No", sp);
			
		}else{
			
			int branchIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_BRANCH_INDEX, sp);
			Log.i(LOG_TAG, "branchIndex:"+String.valueOf(branchIndex));
			
			contentsArray = activity.filterBranch(branchIndex);
			contents = ContentUtil.getContentsById(contentsArray, currentIndex);
			
			if(contents!=null){
				Log.i(LOG_TAG, "contents:"+contents);
				contentArray = contents.split(",");
		        contentId = contentArray[0];
		        contentLevel = contentArray[1];
		        contentSuperId = contentArray[2];
		        contentName = contentArray[3];
		        contentURL = contentArray[4];
		        
				//super content
				String superContents = ContentUtil.getSuperContentsById(contentsArray, currentIndex);
				String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
				String branchName = SPUtil.getFromSP(SPUtil.BRANCH_PATH_NAME, sp);
				Log.i(LOG_TAG, "superContents:"+superContents);
				Log.i(LOG_TAG, "appPath:"+appPath);
				Log.i(LOG_TAG, "branchName:"+branchName);
				
				baseFolder = appPath + File.separator + branchName;
				
				//second path
				String branchPath = appPath +  File.separator + branchName ;
				File subPathFold = new File(branchPath);
				if(!subPathFold.isDirectory()) subPathFold.mkdir();
				if(!subPathFold.canWrite()) subPathFold.setWritable(true);
				
				String contentPath = branchPath +  File.separator + StringUtil.rmvSpace(contentName)+".xml";
				Log.i(LOG_TAG,"contentPath:"+contentPath);
				
				boolean flag3GWifi = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_SYNC_FLAG, sp);
				
//				if(new File(contentPath).exists()){
////					Log.i(LOG_TAG,"exists load local content");
////					new ParseData().execute(new String[] { contentURL, contentPath , XPATH });
//				}else{
//					Log.i(LOG_TAG,"not exists,should download by network");
//				}
				
//				if(NetWorkUtil.isNetworkAvailable(mContext)){
//					if(NetWorkUtil.is3G(mContext) || NetWorkUtil.isWifi(mContext)){
						new GetData().execute(new String[] { contentURL, contentPath , XPATH ,branchPath});
//					}
//				}
			}else{
				titleTV.setText("Error,try again!");
			}
		}
		

		

		return mView;
	}
	
	private class GetData extends AsyncTask<String, Void, String> {
		HtmlCleanAPI api2;
		TagNode unParse;
		String pContent = null;
		TagNode localTagNode;
		
		@Override
		protected void onPreExecute() {
			 progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(2000);
				api2 = new HtmlCleanAPI();
				String htmlUrl = params[0];
				String localPath = params[1];
				String xPath = params[2];
				String subPath = params[3];

				Log.i(LOG_TAG, "htmlUrl:" + htmlUrl);
				Log.i(LOG_TAG, "localPath:" + localPath);
				Log.i(LOG_TAG, "xPath:" + xPath);
				Log.i(LOG_TAG, "getNodeByNetwork()...");

				// Get Node by Web
				if(NetWorkUtil.isNetworkAvailable(mContext)){
					unParse = api2.getNodeByNetwork(htmlUrl);
				}
				
				if (unParse != null) {
					pContent = api2.parseContent(unParse, xPath, subPath);
					if (pContent != null) {
						Log.i(LOG_TAG, "pContent:" + pContent);
						api2.saveAsFileOutputStream(localPath, pContent);
					}
				} else {
					Log.i(LOG_TAG, "unParse tagNode is null.");
				}

				// Parse Node by local file
				localTagNode = api2.getNodeByLocal(localPath);
				// if (tagNode != null) {
				// pageSize = api2.getContentPageSize(tagNode);
				// }
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(LOG_TAG, "onPostExecute()...");
			progressBar.setVisibility(View.INVISIBLE);
			if(localTagNode!=null){
				List<HtmlContent> hcList = api2.getHtmlList(localTagNode);
				setAllComponent(hcList);
			}else{
				Toast.makeText(mContext, "No content", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public static final int TEXT_SIZE = 17;
	public void setAllComponent(List<HtmlCleanAPI.HtmlContent> hcList){
		Log.i(LOG_TAG, "setAllComponent()...");
		for(HtmlCleanAPI.HtmlContent hc: hcList){
			String tag = hc.getTag();
			String content =  hc.getContent();
			if(StringUtil.isEmpty(content)) continue;
			
			String contentMBlank = StringUtil.mergeBlank(content);
			if(StringUtil.isEmpty(contentMBlank)) continue;
			
			String contentRBlank = StringUtil.rmvEnter(StringUtil.trim(contentMBlank));
			if(StringUtil.isEmpty(contentRBlank)) continue;
			
			String contentRSpecial = StringUtil.rmvSpecial(contentRBlank);
			if(StringUtil.isEmpty(contentRSpecial)) continue;
			
			Log.i(LOG_TAG, "tag:" + tag);
			Log.i(LOG_TAG, "content:" + contentRSpecial);
			
			if(tag.equalsIgnoreCase("P")){ 
				final TextView tagPTV = new TextView(mContext);
				tagPTV.setBackgroundResource(R.drawable.tag_p_drawable);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 3, 2, 3); 
				tagPTV.setLayoutParams(lp);
				tagPTV.setPadding(3, 3, 3, 3);
				tagPTV.setPaddingRelative(3, 3, 3, 3);
				tagPTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagPTV.setTextColor(Color.BLACK);
				tagPTV.setText(contentRSpecial);
				tagPTV.setTextSize(TEXT_SIZE);
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
				tagPre.setPadding(2, 2, 2, 2);
				tagPre.setPaddingRelative(2, 2, 2, 2);
				tagPre.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagPre.setTextColor(Color.BLACK);
//				String contentRSpecial = StringUtil.rmvSpecial(content);
				tagPre.setText(StringUtil.rmvSpecial(content));
				tagPre.setTextSize(TEXT_SIZE);
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
				tagDtHTV.setPadding(2, 2, 2, 2);
				tagDtHTV.setPaddingRelative(2, 2, 2, 2);
				tagDtHTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagDtHTV.setTextColor(Color.WHITE);
				tagDtHTV.setText(contentRSpecial);
				tagDtHTV.setTextSize(TEXT_SIZE);
				lineLayout.addView(tagDtHTV);
			}else if(tag.equalsIgnoreCase("dd")){
				final TextView tagDD = new TextView(mContext);
				tagDD.setBackgroundResource(R.drawable.tag_p_drawable);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagDD.setLayoutParams(lp);
				tagDD.setPadding(3, 3, 3, 3);
				tagDD.setPaddingRelative(3, 3, 3, 3);
				tagDD.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagDD.setTextColor(Color.BLACK);
				tagDD.setText(contentRSpecial);
				tagDD.setTextSize(TEXT_SIZE);
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
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				tagLigTV.setLayoutParams(lp);
				tagLigTV.setPaddingRelative(2, 2, 2, 2);
				tagLigTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				tagLigTV.setTextColor(Color.BLACK);
				tagLigTV.setText(contentRSpecial);
				tagLigTV.setTextSize(TEXT_SIZE);
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
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.setMargins(2, 2, 2, 2); 
				imgTV.setLayoutParams(lp);
				imgTV.setPaddingRelative(2, 2, 2, 2);
				imgTV.setMovementMethod(ScrollingMovementMethod.getInstance()) ;
				imgTV.setTextColor(Color.BLACK);
				imgTV.setText(content);
				imgTV.setTextSize(TEXT_SIZE);
				lineLayout.addView(imgTV);
				
				ImageView imageView = new ImageView(mContext);
				LinearLayout.LayoutParams imgLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(imgLP);
				Bitmap localBt = getLoacalBitmap(baseFolder + File.separator +  content);
				imageView.setImageBitmap(localBt);
				lineLayout.addView(imageView);
			}
		}
	}
	
	public void openDialog(String content){
		CustomDialog cusDialog = new CustomDialog(activity,R.style.custom_dialog_style,content);
		Window wd = cusDialog.getWindow();
		WindowManager.LayoutParams lp = wd.getAttributes();
		lp.alpha = 1.0f;
		wd.setAttributes(lp);
		cusDialog.show();
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
}
