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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.timothy.android.api.activity.R;

public class HomeFragment extends Fragment {

	Context mContext;
	TextView homeTV;
	TextView updateTV;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = getActivity().getApplicationContext();
		
		View view = inflater.inflate(R.layout.home, null);
		homeTV = (TextView) view.findViewById(R.id.homeTV);
//		tv.setText(R.string.content_home);
		homeTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save index
//				SlidingActivity activity = (SlidingActivity) getActivity();
//				SharedPreferences.Editor edit = activity.getSharedPreferences().edit();
//				edit.putInt("index", 1);
//				edit.commit();
//				
//		        Intent intent = new Intent();
//		        intent.setClass(activity, SlidingActivity.class);
//		        startActivity(intent);   
//		        
//		        activity.finish();
				
			}
		});
		
//		updateTV = (TextView) view.findViewById(R.id.updateTV);
		
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}


}

