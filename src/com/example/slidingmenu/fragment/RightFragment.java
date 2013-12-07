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
import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.apiguide.R;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.util.SPUtil;
import com.timothy.util.XMLParserUtil;

public class RightFragment extends Fragment {
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
		int sIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_SCONTENT_INDEX, sp);
//		int pageNo = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_PAGE_INDEX, sp);
		String readTime = SPUtil.getFromSP(SPUtil.SP_KEY_READ_TIME, sp);
		if(sIndex>0 ){
			ContentBean bean = XMLParserUtil.getContentByIndex(mContext,sIndex);
			if(bean!=null){
				ContentBean mBean = XMLParserUtil.getContentByIndex(mContext,bean.getmIndex());
				StringBuilder hintText = new StringBuilder();
				if(mBean.getName() !=null && bean.getName() !=null){
					hintText.append(mBean.getName() + "->" + bean.getName());
				}
				if(readTime!=null){
					hintText.append("\n" + "On " + readTime);
				}
				if(hintText.length()>0){
					lstReadTV.setText(hintText.toString());
				}
			}
		}
		
		Button gotoBtn = (Button) view.findViewById(R.id.gotoBtn);
		gotoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshActivity();
			}
		});
		
//		Button clrBtn = (Button) view.findViewById(R.id.clearBtn);
//		clrBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				new RmvData().execute();
//			}
//		});
		
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
	
/*	private class RmvData extends AsyncTask<String, Void, String> {
		boolean success = false;
		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(2000);
				String xmlBase = Environment.getExternalStorageDirectory() + File.separator + "androidApiGuide" ;
				FileUtil util = new FileUtil();
				util.deleteFolder(new File(xmlBase));
				success = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(success){
				SPUtil.clearSP(sp);
				Toast.makeText(mContext, "All Text are cleared!", Toast.LENGTH_SHORT).show();
			}
		}
	}*/

}
