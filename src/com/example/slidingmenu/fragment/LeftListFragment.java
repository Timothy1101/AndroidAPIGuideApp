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
import java.util.List;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.timothy.android.api.adapter.LeftListAdapter;
import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.android.apiguide.R;
import com.timothy.util.SPUtil;
import com.timothy.util.XMLParserUtil;
@Deprecated
public class LeftListFragment extends ListFragment {
	public static final String LOG_TAG = "LeftListFragment";
//	TextView indexTV;
	Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_list, null);
		return view;
	}

	SlidingActivity activity;
	SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		activity = (SlidingActivity) getActivity();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		int mIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_MCONTENT_INDEX, sp);
		int sIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_SCONTENT_INDEX, sp);
		refreshList(mIndex,sIndex);
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		Log.i(LOG_TAG,"position:"+position);
		
		ContentBean clickBean = sbList.get(position);
		if(clickBean!=null){
			Log.i(LOG_TAG,"mIndex:"+clickBean.getmIndex());
			Log.i(LOG_TAG,"index:"+clickBean.getIndex());
			boolean openFlag = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_OPEN_FLAG, sp);
			if(openFlag){//sub content is open
				int mIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_MCONTENT_INDEX, sp);
				if(clickBean.getType().equalsIgnoreCase("M")){
					if(mIndex == clickBean.getmIndex()){
						refreshList(-1, -1);//close sub contents
						SPUtil.save2SP(SPUtil.SP_KEY_OPEN_FLAG, false, sp);
					} else {
						refreshList(clickBean.getmIndex(), clickBean.getIndex());//open clicked content
					}
				}else{
					refreshActivity();
				}
			}else{//sub content is close
				refreshList(clickBean.getmIndex(), clickBean.getIndex());//open clicked content
				SPUtil.save2SP(SPUtil.SP_KEY_OPEN_FLAG, true, sp);
			}
//			if(clickBean.getType().equalsIgnoreCase("S")){
				SPUtil.save2SP(SPUtil.SP_KEY_MCONTENT_INDEX, clickBean.getmIndex(), sp);
				SPUtil.save2SP(SPUtil.SP_KEY_SCONTENT_INDEX, clickBean.getIndex(), sp);	
//			}
		}
	}
	
	List<ContentBean> sbList;
	public void refreshList(int mIndex,int currentId){
		sbList = XMLParserUtil.getContentsByMIndex(mContext,mIndex);
		if(sbList!=null){
			Log.i(LOG_TAG,"-------------sbList is not empty start-------------");
			for(ContentBean bean:sbList){
				Log.i(LOG_TAG,bean.toString());
			}
			Log.i(LOG_TAG,"-------------sbList is not empty end-------------");
			setListAdapter(new LeftListAdapter(mContext,sbList,currentId));
		}
	}
	
	public void refreshActivity(){
		
		SPUtil.save2SP(SPUtil.CHILD_FLAG, "No", sp);
		
        Intent intent = new Intent();
        intent.setClass(activity, SlidingActivity.class);
        startActivity(intent);   
        activity.finish();	
	}
	

}
