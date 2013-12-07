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
package com.example.slidingmenu.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.timothy.android.api.bean.ChildContentBean;
import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.android.apiguide.R;
import com.timothy.dao.HtmlCleanAPI2;
import com.timothy.util.NetWorkUtil;
import com.timothy.util.SPUtil;
import com.timothy.util.StringUtil;
import com.timothy.util.XMLParserUtil;
@Deprecated
public class ViewPagesFragment extends Fragment {
	public static final String LOG_TAG= "ViewPageFragment";
	
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
	
	ContentBean bean;
	int sIndex;
	int pageSize;
	boolean errorFlag = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.i(LOG_TAG,"onCreateView()...");

		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		mContext = activity.getApplicationContext();
		
		View mView = inflater.inflate(R.layout.view_pagers, null);

		showLeft = (ImageView) mView.findViewById(R.id.showLeft);
		titleTV = (TextView) mView.findViewById(R.id.titleTextView);
//		titleTV.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				TextView tv = (TextView) v;
//				if(childTitleArray!=null && childTitleArray.length >0){
//					tv.getTop();
//					int y = tv.getBottom() * 3 / 2;
//					int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 4;
//					showPopupWindow(x, y);					
//				}
//			}
//		});
		
		sIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_SCONTENT_INDEX, sp);
		boolean reBootFlag = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_REBOOT_FLAG, sp);
		
		showRight = (ImageView) mView.findViewById(R.id.showRight);
		mPager = (ViewPager) mView.findViewById(R.id.pager);
		titleTV = (TextView) mView.findViewById(R.id.titleTextView);
		progressBar = (ProgressBar) mView.findViewById(R.id.loading_spinner);
		
		if(reBootFlag || sIndex == -1){
			Log.i(LOG_TAG,"start first time...");
			
			HomeFragment homeFrag = new HomeFragment();
			pagerItemList.add(homeFrag);
			setAdapter();
			
			SPUtil.save2SP(SPUtil.SP_KEY_REBOOT_FLAG, false, sp);
		}else{
			bean = XMLParserUtil.getContentByIndex(mContext,sIndex);
			Log.i(LOG_TAG,"sIndex:"+String.valueOf(sIndex));
			List<ChildContentBean> ccbList = bean.getCcbList();
			if(ccbList!=null && ccbList.size()>0){
				int ccbId = 0;
				childTitleArray = new String[ccbList.size()];
				for(ChildContentBean ccb:ccbList){
					childTitleArray[ccbId++] = ccb.getChildContentName();	
					childValueMap.put(ccb.getChildContentName(), ccb.getChildContentURL());
					Log.i(LOG_TAG,"child name:"+ccb.getChildContentName());
					Log.i(LOG_TAG,"child url:"+ccb.getChildContentURL());
				}					
			}else{
				Log.i(LOG_TAG,"ccb List is null");
			}
			
			if(bean!=null){
				String url = bean.getUrl();
				String name = bean.getName();
				if("Yes".equalsIgnoreCase(SPUtil.getFromSP(SPUtil.CHILD_FLAG, sp))){
					url = SPUtil.getFromSP(SPUtil.CHILD_URL,sp);
					name = SPUtil.getFromSP(SPUtil.CHILD_NAME, sp);
					Log.i(LOG_TAG,"childFlag:Yes "+url);
					Log.i(LOG_TAG,"childFlag:Yes "+name);
				}else{
					Log.i(LOG_TAG,"childFlag:No url:"+url);
					Log.i(LOG_TAG,"childFlag:No name:"+name);
				}
				
				ContentBean mBean = XMLParserUtil.getContentByIndex(mContext,bean.getmIndex());
				String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
				String subPath = appPath +  File.separator + StringUtil.rmvSpace(mBean.getName()) ;
				File subPathFold = new File(subPath);
				if(!subPathFold.isDirectory()){
					subPathFold.mkdir();
				}
				if(!subPathFold.canWrite()){
					subPathFold.setWritable(true);
				}
				String contentPath = subPath +  File.separator + StringUtil.rmvSpace(name)+".xml";
				Log.i(LOG_TAG,"contentPath:"+contentPath);
				
				boolean flag3GWifi = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_SYNC_FLAG, sp);
				
				if(new File(contentPath).exists()){
					new ParseData().execute(new String[] { url, contentPath , XPATH });
				}else{
					if(NetWorkUtil.isNetworkAvailable(mContext)){
						if(NetWorkUtil.is3G(mContext) || NetWorkUtil.isWifi(mContext)){
							new GetData().execute(new String[] { url, contentPath , XPATH ,subPath});
						}else{
							if(flag3GWifi){
								errorFlag = true;
								pagerItemList.add(PageFragment.newInstance(-1,-1,2));
								setAdapter();
							}else{
								new GetData().execute(new String[] { url, contentPath , XPATH ,subPath});
							}
						}
					}else{
						errorFlag = true;
						pagerItemList.add(PageFragment.newInstance(-1,-1,1));
						setAdapter();
					}					
				}
			}
		}
		
		if(errorFlag){
			titleTV.setText("Error");
		}
		
/*		AdView adView = (AdView) mView.findViewById(R.id.adView);
        adView.loadAd(new AdRequest());*/
        
		return mView;
	}
	

/*	private ProgressBar createProgressBar(Activity activity, Drawable customIndeterminateDrawable) {
	    FrameLayout rootContainer = (FrameLayout) activity.findViewById(android.R.id.content);
	    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
	            ViewGroup.LayoutParams.WRAP_CONTENT,
	            ViewGroup.LayoutParams.WRAP_CONTENT);
	    lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
	    ProgressBar progressBar = new ProgressBar(activity);
	    progressBar.setVisibility(View.VISIBLE);
	    progressBar.setLayoutParams(lp);
	    progressBar.bringToFront();
	    if (customIndeterminateDrawable != null) {
	        progressBar.setIndeterminateDrawable(customIndeterminateDrawable);
	    }
	    rootContainer.addView(progressBar);
	    return progressBar;
	}*/
	
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
//				progressBar.setVisibility(View.VISIBLE);
			}
			@Override
			public void onPageScrollStateChanged(int position) {
				Log.i(LOG_TAG,"onPageScrollStateChanged()...");
				Log.i(LOG_TAG,"position:"+String.valueOf(position));
				
//				progressBar.setVisibility(View.GONE);
				if(errorFlag){
					titleTV.setText("Error");
				}else if(bean!=null){
					int pageNo = mPager.getCurrentItem()+1;
					titleTV.setText(bean.getName() + "("+String.valueOf(pageNo) +  "/" +String.valueOf(pageSize)+")");				
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
		HtmlCleanAPI2 api2;
		String pContent = null;
		
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(2000);
				api2 = new HtmlCleanAPI2();
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
					pagerItemList.add(PageFragment.newInstance(sIndex,p,0));
				}
				titleTV.setText(bean.getName() + "(1" +  "/" +String.valueOf(pageSize)+")");
//				urlAddress.setText(bean.getUrl());
			}else{
				if(pContent == null){
					pagerItemList.add(PageFragment.newInstance(-1,-1,3));//int index,int pageIndex,int erroCode
				}else{
					pagerItemList.add(PageFragment.newInstance(-1,-1,4));
				}
			}
			setAdapter();
			if("Yes".equalsIgnoreCase(SPUtil.getFromSP("childFlag", sp))){
				SPUtil.save2SP("childFlag", "No", sp);
				SPUtil.save2SP("childName", null, sp);
				SPUtil.save2SP("childURL", null, sp);		
			}

		}
	}
	
	private class ParseData extends AsyncTask<String, Void, String> {
		HtmlCleanAPI2 api2;
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(1000);
				api2 = new HtmlCleanAPI2();
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
					pagerItemList.add(PageFragment.newInstance(sIndex,p,0));
				}
				titleTV.setText(bean.getName() + "(1" +  "/" +String.valueOf(pageSize)+")");
			}else{
				pagerItemList.add(PageFragment.newInstance(-1,-1,4));
			}
			setAdapter();
		}
	}

	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;
	public String childTitleArray[] ;
	public Map<String,String> childValueMap = new HashMap<String,String>();
	
	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.popup, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(activity,R.layout.popup_item, R.id.tv_text, childTitleArray));
		popupWindow = new PopupWindow(activity);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(activity.getWindowManager().getDefaultDisplay().getWidth() * 2 / 3);
		popupWindow.setHeight(300);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAtLocation(activity.findViewById(R.id.view_pagers), Gravity.LEFT| Gravity.TOP, x, y);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Log.i(LOG_TAG,"onItemClick()...");
				
				TextView tv = (TextView) arg1.findViewById(R.id.tv_text);
				String name = tv.getText().toString();
				String url = childValueMap.get(name);
				Log.i(LOG_TAG,"name:"+name);
				Log.i(LOG_TAG,"url:"+url);
				
				//save child info
				SPUtil.save2SP(SPUtil.CHILD_FLAG, "Yes", sp);
				SPUtil.save2SP(SPUtil.CHILD_NAME, name, sp);
				SPUtil.save2SP(SPUtil.CHILD_URL, url , sp);
				
				Log.i(LOG_TAG,"onItemClick() end.");
				//refreshActivity
				refreshActivity();
			}
		});
	}
	
	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(activity, SlidingActivity.class);
        startActivity(intent);   
//        activity.finish();	
	}
}
