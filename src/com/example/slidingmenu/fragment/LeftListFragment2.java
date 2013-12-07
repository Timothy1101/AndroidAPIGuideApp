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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.timothy.android.api.adapter.LeftListAdapter2;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.android.apiguide.R;
import com.timothy.util.SPUtil;

public class LeftListFragment2 extends ListFragment {
	public static final String LOG_TAG = "LeftListFragment2";
	Context mContext;
	
	TextView title;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_list, null);
		title = (TextView) view.findViewById(R.id.indexTV);
		
		int branchIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_BRANCH_INDEX, sp);
		if(branchIndex!=-1 && branchNames!=null){
			title.setText(branchNames[branchIndex-1]);
		}
		
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TextView tv = (TextView) arg0;
				if(branchNames!=null && branchNames.length >0){
					tv.getTop();
					int y = tv.getBottom() * 3 / 2;
					int x =tv.getLeft();
//					int x = activity.getWindowManager().getDefaultDisplay().getWidth() / 4;
					showPopupWindow(x, y);					
				}
			}
		});
		return view;
	}

	SlidingActivity activity;
	SharedPreferences sp;
	
	public String branchNames[] ;
	public Map<String,String> branchNamesMap = new HashMap<String,String>();
	
	public String[] contentsArray;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		
		//get branch list
		branchNames = getResources().getStringArray(R.array.branch_array);
		
		int currentIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_INDEX, sp);
		int branchIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_BRANCH_INDEX, sp);
		
		Log.i(LOG_TAG, "branchIndex:"+branchIndex);
		Log.i(LOG_TAG, "currentIndex:"+currentIndex);
		
		contentsArray = activity.filterBranch(branchIndex);
		
		//set list
		refreshList(currentIndex,contentsArray);
	}
	

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.i(LOG_TAG, "onListItemClick()...");
		Log.i(LOG_TAG, "position:"+String.valueOf(position));
		
		String contents = contentsArray[position];
		Log.i(LOG_TAG, "contents:"+contents);
		
		String[] contentArray = contents.split(",");
		
		int contentId = Integer.valueOf(contentArray[0]);
		Log.i(LOG_TAG, "contentId:"+String.valueOf(contentId));
		
		SPUtil.save2SP(SPUtil.CURRENT_INDEX,contentId , sp);
		refreshActivity();
	}
	
	public void refreshList(int currentId,String[] contentsArray){
		if(getListAdapter()==null){
			setListAdapter(new LeftListAdapter2(mContext,contentsArray,currentId));
		}else{
			LeftListAdapter2 adapter = (LeftListAdapter2) getListAdapter();
			adapter.setFilteredContents(contentsArray);
			adapter.setCurrId(currentId);
			adapter.notifyDataSetChanged();
		}
	}
	
	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(activity, SlidingActivity.class);
        startActivity(intent);   
        activity.finish();	
	}
	
	private PopupWindow popupWindow;
	private LinearLayout layout;
	private ListView listView;

	public void showPopupWindow(int x, int y) {
		layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.popup, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(activity,R.layout.popup_item, R.id.tv_text, branchNames));
		popupWindow = new PopupWindow(activity);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(activity.getWindowManager().getDefaultDisplay().getWidth() * 3/5);
		popupWindow.setHeight(activity.getWindowManager().getDefaultDisplay().getHeight() * 4/5);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAtLocation(activity.findViewById(R.id.view_pagers), Gravity.LEFT| Gravity.TOP, x, y);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
//				Log.i(LOG_TAG,"onItemClick()...");
//				
//				TextView tv = (TextView) arg1.findViewById(R.id.tv_text);
//				String name = tv.getText().toString();
//				String url = childValueMap.get(name);
//				Log.i(LOG_TAG,"name:"+name);
//				Log.i(LOG_TAG,"url:"+url);
//				
//				//save child info
//				SPUtil.save2SP(SPUtil.CHILD_FLAG, "Yes", sp);
//				SPUtil.save2SP(SPUtil.CHILD_NAME, name, sp);
//				SPUtil.save2SP(SPUtil.CHILD_URL, url , sp);
//				
//				Log.i(LOG_TAG,"onItemClick() end.");
				
				//refreshActivity
//				refreshActivity();
				String[] contentsArray = activity.filterBranch(arg2+1); 
				refreshList(1,contentsArray);
				
				SPUtil.save2SP(SPUtil.CURRENT_BRANCH_INDEX, arg2+1, sp);
				title.setText(branchNames[arg2]);
				
				popupWindow.dismiss();
			}
		});
	}

}
