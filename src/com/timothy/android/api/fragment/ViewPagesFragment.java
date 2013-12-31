/*
 * Copyright (C) 2012 Timothy
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
import java.util.ArrayList;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.timothy.android.api.activity.R;
import com.timothy.android.api.activity.SlidingActivity;
import com.timothy.android.http.HtmlCleanAPI;
import com.timothy.android.uil.ContentUtil;
import com.timothy.android.uil.NetWorkUtil;
import com.timothy.android.uil.SPUtil;
import com.timothy.android.uil.StringUtil;

public class ViewPagesFragment extends Fragment {
	public static final String LOG_TAG= "ViewPagesFragment";
	public static final int PAGE_TEXT_LIMIT = 800;
	public static final String XPATH= "//div[@id='jd-content']";
	
	Context mContext;
	
	private ImageView showLeft;
	private TextView titleTV;
	private ImageView showRight;
	private ProgressBar progressBar;
	private MyAdapter mAdapter;
	private ViewPager mPager;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	SlidingActivity activity;
	SharedPreferences sp;
	
	private com.google.ads.AdView adView;
	private ImageView closeAD;
	
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
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(LOG_TAG,"onCreateView()...");
		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		mContext = activity.getApplicationContext();
		View mView = inflater.inflate(R.layout.view_pagers, null);
		showLeft = (ImageView) mView.findViewById(R.id.showLeft);
		titleTV = (TextView) mView.findViewById(R.id.titleTextView);
		titleTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOG_TAG,"titleTV onclicked...");
			}
		});
		
		currentIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_INDEX, sp);
		Log.i(LOG_TAG, "currentIndex:"+String.valueOf(currentIndex));
		
		showRight = (ImageView) mView.findViewById(R.id.showRight);
		mPager = (ViewPager) mView.findViewById(R.id.pager);
		progressBar = (ProgressBar) mView.findViewById(R.id.loading_spinner);
		
		String firstLoad = SPUtil.getFromSP(SPUtil.FIRST_LOAD_FLAG, sp);
//		if(currentIndex == -1){
	    if(firstLoad == null || currentIndex == -1){
			HomeFragment homeFrag = new HomeFragment();
			pagerItemList.add(homeFrag);
			setAdapter();
			
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
				Log.i(LOG_TAG, "superContents:"+superContents);
//				String[] superContentArray = superContents.split(",");
				String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
				String branchName = SPUtil.getFromSP(SPUtil.BRANCH_PATH_NAME, sp);
				Log.i(LOG_TAG, "appPath:"+appPath);
				Log.i(LOG_TAG, "branchName:"+branchName);
				
				//second path
				String branchPath = appPath +  File.separator + branchName ;
				File subPathFold = new File(branchPath);
				if(!subPathFold.isDirectory()) subPathFold.mkdir();
				if(!subPathFold.canWrite()) subPathFold.setWritable(true);
				
				String contentPath = branchPath +  File.separator + StringUtil.rmvSpace(contentName)+".xml";
				Log.i(LOG_TAG,"contentPath:"+contentPath);
				
				boolean flag3GWifi = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_SYNC_FLAG, sp);
				
				if(new File(contentPath).exists()){
					Log.i(LOG_TAG,"exists load local content");
					new ParseData().execute(new String[] { contentURL, contentPath , XPATH });
				}else{
					Log.i(LOG_TAG,"not exists,should download by network");
					if(NetWorkUtil.isNetworkAvailable(mContext)){
						if(NetWorkUtil.is3G(mContext) || NetWorkUtil.isWifi(mContext)){
							new GetData().execute(new String[] { contentURL, contentPath , XPATH ,branchPath});
						}else{
							if(flag3GWifi){
								errorFlag = true;
								pagerItemList.add(PageFragment.newInstance(-1,-1,2));
								setAdapter();
							}else{
								new GetData().execute(new String[] { contentURL, contentPath , XPATH ,branchPath});
							}
						}
					}else{
						errorFlag = true;
						pagerItemList.add(PageFragment.newInstance(-1,-1,1));
						setAdapter();
					}					
				}
			}else{
				titleTV.setText("Error,try again!");
			}
		}
		
		
		if(errorFlag){
			titleTV.setText("Error");
		}
		
		//close ad
		adView = (com.google.ads.AdView) mView.findViewById(R.id.adView);
		closeAD = (ImageView) mView.findViewById(R.id.closeAD);
		if(adView.getVisibility() == View.VISIBLE){
			closeAD.setVisibility(View.VISIBLE);
		}else{
			closeAD.setVisibility(View.GONE);
		}
		closeAD.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adView.setVisibility(View.GONE);
			}
		});
		
		return mView;
	}
	
	public void setAdapter(){
		mAdapter = new MyAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (myPageChangeListener != null)
					myPageChangeListener.onPageSelected(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.i(LOG_TAG,"onPageScrolled()...");
			}
			@Override
			public void onPageScrollStateChanged(int position) {
				Log.i(LOG_TAG,"onPageScrollStateChanged()...");
				Log.i(LOG_TAG,"position:"+String.valueOf(position));
				if(errorFlag){
					titleTV.setText("Error");
				}else if(contentName!=null){
					int pageNo = mPager.getCurrentItem()+1;
					titleTV.setText(contentName + "("+String.valueOf(pageNo) +  "/" +String.valueOf(pageSize)+")");				
				}
			}
		});
	}
	
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
	
	public boolean isFirst() {
		if (mPager.getCurrentItem() == 0)
			return true;
		else
			return false;
	}

	public boolean isEnd() {
		if (mPager.getCurrentItem() == pagerItemList.size() - 1)
			return true;
		else
			return false;
	}

	public class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public int getCount() {
			return pagerItemList.size();
		}
		@Override
		public Fragment getItem(int position) {
			Log.i(LOG_TAG,"position:"+String.valueOf(position));
			Fragment fragment = null;
			if (position < pagerItemList.size())
				fragment = pagerItemList.get(position);
			else
				fragment = pagerItemList.get(0);
			return fragment;
		}
	}

	private MyPageChangeListener myPageChangeListener;

	public void setMyPageChangeListener(MyPageChangeListener l) {
		Log.i(LOG_TAG,"setMyPageChangeListener()...");
		myPageChangeListener = l;
	}

	public interface MyPageChangeListener {
		public void onPageSelected(int position);
	}
	

	private class GetData extends AsyncTask<String, Void, String> {
		HtmlCleanAPI api2;
		String pContent = null;
		
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
				
				 Log.i(LOG_TAG,"htmlUrl:"+htmlUrl);
				 Log.i(LOG_TAG,"localPath:"+localPath);
				 Log.i(LOG_TAG,"xPath:"+xPath);
				 
				Log.i(LOG_TAG, "getNodeByNetwork()...");
				
				//Get Node by Web
				TagNode unParse = api2.getNodeByNetwork(htmlUrl);
				if (unParse != null) {
					pContent = api2.parseContent(unParse, xPath,subPath);
					if (pContent != null) {
						Log.i(LOG_TAG, "pContent:" + pContent);
						api2.saveAsFileOutputStream(localPath, pContent);
					}
				}else{
					Log.i(LOG_TAG, "unParse tagNode is null.");
				}
				
				//Parse Node by local file
				TagNode tagNode = api2.getNodeByLocal(localPath);
				if (tagNode != null) {
					pageSize = api2.getContentPageSize(tagNode);
				}
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "success";
		}
		@Override
		protected void onPostExecute(String result) {
			Log.i(LOG_TAG,"onPostExecute()...");
			progressBar.setVisibility(View.GONE);
			if(pageSize>0 && result.equalsIgnoreCase("success")){
				for(int p=1;p<=pageSize;p++){
					pagerItemList.add(PageFragment.newInstance(currentIndex,p,0));
				}
				titleTV.setText(contentName + "(1" +  "/" +String.valueOf(pageSize)+")");
			}else{
				if(pContent == null){
					pagerItemList.add(PageFragment.newInstance(-1,-1,3));//int index,int pageIndex,int erroCode
				}else{
					pagerItemList.add(PageFragment.newInstance(-1,-1,4));
				}
			}
			setAdapter();
		}
	}
	
	private class ParseData extends AsyncTask<String, Void, String> {
		HtmlCleanAPI api2;
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(1000);
				api2 = new HtmlCleanAPI();
				String localPath = params[1];
				TagNode tagNode = api2.getNodeByLocal(localPath);
				if(tagNode!=null){
					pageSize = api2.getContentPageSize(tagNode);
				}
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "success";
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.i(LOG_TAG,"onPostExecute()...");
			progressBar.setVisibility(View.GONE);
			if(pageSize>0 && result.equalsIgnoreCase("success")){
				for(int p=1;p<=pageSize;p++){
					pagerItemList.add(PageFragment.newInstance(currentIndex,p,0));
				}
				titleTV.setText(contentName + "(1" +  "/" +String.valueOf(pageSize)+")");
			}else{
				pagerItemList.add(PageFragment.newInstance(-1,-1,4));
			}
			setAdapter();
		}
	}

	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(activity, SlidingActivity.class);
        startActivity(intent);   
//        activity.finish();	
	}
}
