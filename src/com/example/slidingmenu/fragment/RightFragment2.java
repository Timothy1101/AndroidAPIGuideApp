/*
 * Copyright (C) 
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.timothy.android.apiguide.R;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.util.ContentUtil;
import com.timothy.util.SPUtil;

public class RightFragment2 extends Fragment {
	SlidingActivity activity;
	SharedPreferences sp;
	Context mContext;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		activity = (SlidingActivity) getActivity();
		mContext = activity.getApplicationContext();
		sp = activity.getSharedPreferences("AndroidAPISP",0);
		
		View view = inflater.inflate(R.layout.right, null);
		
		boolean syncFlag = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_SYNC_FLAG, sp);
		//syncFlag
		ToggleButton syncFlagBtn = (ToggleButton) view.findViewById(R.id.syncFlagBtn);
		syncFlagBtn.setChecked(syncFlag);
		syncFlagBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToggleButton tb = (ToggleButton)v;
				SPUtil.save2SP(SPUtil.SP_KEY_SYNC_FLAG, tb.isChecked()  , sp);
			}
		});
		
		//lstReadTV
		TextView lstReadTV = (TextView) view.findViewById(R.id.lstReadTV);
		lstReadTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		int branchIndex = SPUtil.getIntegerFromSP(SPUtil.CURRENT_BRANCH_INDEX, sp);
		String[] contentsArray = activity.filterBranch(branchIndex);
		
		String contents = ContentUtil.getContentsById(contentsArray, SPUtil.getIntegerFromSP(SPUtil.CURRENT_INDEX, sp));
		if(contents!=null){
			String[] contentArray = contents.split(",");
			if(contentArray!=null && contentArray.length>=4){
				String contentName = contentArray[3];
				lstReadTV.setText(contentName);					
			}
		}
		
		Button gotoBtn = (Button) view.findViewById(R.id.gotoBtn);
		gotoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshActivity();
			}
		});
		
		return view;
	}
	
	public void clearSP(){
		
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(activity, SlidingActivity.class);
        startActivity(intent);   
        activity.finish();	
	}

}
