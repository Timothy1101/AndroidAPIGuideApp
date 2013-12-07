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
package com.timothy.android.apiguide;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;
import com.example.slidingmenu.fragment.LeftListFragment2;
import com.example.slidingmenu.fragment.PageFragment2;
import com.example.slidingmenu.fragment.RightFragment2;
import com.example.slidingmenu.fragment.ViewPagesFragment2;
import com.example.slidingmenu.fragment.ViewPagesFragment2.MyPageChangeListener;
import com.example.slidingmenu.view.SlidingMenu;

public class SlidingActivity extends FragmentActivity {
	SlidingMenu mSlidingMenu;
	
	LeftListFragment2 leftListFragment2;
	RightFragment2 rightFragment2;
	
	ViewPagesFragment2 viewPageFragment2;
	PageFragment2 textFragment2;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ActivityManage.add(this);
		setContentView(R.layout.main);
		init();
		initListener();
	}
	

	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		
		leftListFragment2 = new LeftListFragment2();
		t.replace(R.id.left_frame, leftListFragment2);

		rightFragment2 = new RightFragment2();
		t.replace(R.id.right_frame, rightFragment2);

		viewPageFragment2 = new ViewPagesFragment2();
		t.replace(R.id.center_frame, viewPageFragment2);
		
		t.commit();
		
	}
	
	private void initListener() {
		viewPageFragment2.setMyPageChangeListener(new MyPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(viewPageFragment2.isFirst()){
					mSlidingMenu.setCanSliding(true,false);
				}else if(viewPageFragment2.isEnd()){
					mSlidingMenu.setCanSliding(false,true);
				}else{
					mSlidingMenu.setCanSliding(false,false);
				}
			}
		});
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	    	if((System.currentTimeMillis()-exitTime) > 2000){
	    		Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.msg_exit_hint), Toast.LENGTH_SHORT).show();                                
	    		exitTime = System.currentTimeMillis();
	    	}else{
			    finish();
			    ActivityManage.finishProgram();
			    System.exit(0);
		    }
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//filter branch by name
	public String[] filterBranch(int branchIndex){
		String[] contentsArray = null;
		switch(branchIndex){
	 		case 1:contentsArray = getResources().getStringArray(R.array.array1);break;
	 		case 2:contentsArray = getResources().getStringArray(R.array.array2);break;
	 		case 3:contentsArray = getResources().getStringArray(R.array.array3);break;
	 		case 4:contentsArray = getResources().getStringArray(R.array.array4);break;
	 		case 5:contentsArray = getResources().getStringArray(R.array.array5);break;
	 		case 6:contentsArray = getResources().getStringArray(R.array.array6);break;
	 		case 7:contentsArray = getResources().getStringArray(R.array.array7);break;
	 		case 8:contentsArray = getResources().getStringArray(R.array.array8);break;
	 		case 9:contentsArray = getResources().getStringArray(R.array.array9);break;
	 		case 10:contentsArray = getResources().getStringArray(R.array.array10);break;
	 		case 11:contentsArray = getResources().getStringArray(R.array.array11);break;
	 		case 12:contentsArray = getResources().getStringArray(R.array.array12);break;
	 		case 13:contentsArray = getResources().getStringArray(R.array.array13);break;
		}
		return contentsArray;
	}

}
