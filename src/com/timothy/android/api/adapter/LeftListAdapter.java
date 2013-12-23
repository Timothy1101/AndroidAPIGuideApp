package com.timothy.android.api.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.timothy.android.api.activity.R;
import com.timothy.android.api.activity.SlidingActivity;
import com.timothy.android.uil.SPUtil;
import com.timothy.android.uil.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeftListAdapter extends BaseAdapter{

	public static final String LOG_TAG = "LeftListAdapter";
	
	private Context mContext;
	private String[] initialContentsArray;
	private String[] contentsArrayExL3;
	private int currId;
	private LayoutInflater mInflater;
	SharedPreferences sp;
	
	private boolean expandFlag = false;
	
	private boolean ifHasChildItems(String index){
		boolean has = false;
		for(String content: initialContentsArray){
	        String[] contentArray = content.split(",");
	        String contentSuperId = contentArray[2];
	        if(contentSuperId.equalsIgnoreCase(index)){
	        	has = true;
	        	break;
	        }
		}
		return has;
	}
	
	static class ViewHolder{
		public TextView title;
		public ImageView openImg;
		public ImageView downloadFlag;
	}

	private String[] hiddenL3Items(String[] filteredContents){
		List<String> exL3List = new ArrayList<String>();
		for(String fContent: filteredContents){
	        String[] contentArray = fContent.split(",");
			if(contentArray[1].equalsIgnoreCase("L3")){
				continue;
			}
			exL3List.add(fContent);
		}
		String[] exL3Array = exL3List.toArray(new String[exL3List.size()]);
		return exL3Array;
	}
	
	public LeftListAdapter(Context context, String[] filteredContents,int currId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context); 
		this.initialContentsArray = filteredContents;
		this.contentsArrayExL3 = hiddenL3Items(filteredContents);
		this.currId = currId;
		sp = context.getSharedPreferences("AndroidAPISP",0);
	}

	
	@Override
	public int getCount() {
		return contentsArrayExL3.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;  
        if (convertView == null){  
            holder = new ViewHolder();  
            convertView = mInflater.inflate(R.layout.left_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.titleItem);
            holder.openImg = (ImageView) convertView.findViewById(R.id.openImg);
            holder.downloadFlag = (ImageView) convertView.findViewById(R.id.downloadFlag);
            
            convertView.setTag(holder);  
        }else {  
            holder = (ViewHolder) convertView.getTag();  
        }

        String contents = contentsArrayExL3[position];
        String[] contentArray = contents.split(",");
        final String contentId = contentArray[0];
        String contentLevel = contentArray[1];
//        String contentSuperId = contentArray[2];
        String contentName = contentArray[3];
//        String contentURL = contentArray[4];
        
        //set title
  		holder.title.setText(contentName);
  		if(Integer.valueOf(contentId) == currId){
  			convertView.setBackgroundColor(Color.parseColor("#FCFCFC"));
  			holder.title.setTextColor(Color.parseColor("#307FC4"));
  		}else{
  			convertView.setBackgroundColor(Color.parseColor("#E8E8E8"));
  			holder.title.setTextColor(Color.parseColor("#000000"));
  		}
      		
        //set image
		if(contentLevel.equalsIgnoreCase("L1")){
			holder.openImg.setBackgroundResource(R.drawable.ic_home);
			holder.openImg.setVisibility(View.VISIBLE);
			holder.title.setTextSize(15.0f);
		}else if(contentLevel.equalsIgnoreCase("L2")) {
			if(ifHasChildItems(contentId)){
				holder.openImg.setBackgroundResource(R.drawable.ic_action_expand);
				holder.openImg.setVisibility(View.VISIBLE);
				holder.openImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						changeList(contentId);
//						if(expandFlag){
//							v.setBackgroundResource(R.drawable.ic_action_collapse);
//						}else{
//							v.setBackgroundResource(R.drawable.ic_action_expand);
//						}
					}
				});
			}else{
				holder.openImg.setBackgroundResource(R.drawable.ic_action_expand);
				holder.openImg.setVisibility(View.INVISIBLE);
			}
			holder.title.setTextSize(15.0f);
		}else if(contentLevel.equalsIgnoreCase("L3")) {
			holder.openImg.setBackgroundResource(R.drawable.bullet_48);
			holder.openImg.setVisibility(View.VISIBLE);
			holder.title.setTextSize(12.5f);
		}
		
		//set download flag
		String branchName = SPUtil.getFromSP(SPUtil.BRANCH_PATH_NAME, sp);
		String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
		String contentPath = appPath +  File.separator + branchName +  File.separator + StringUtil.rmvSpace(contentName) +".xml";
		if(new File(contentPath).exists()){
			holder.downloadFlag.setVisibility(View.INVISIBLE);
		}else{
			holder.downloadFlag.setVisibility(View.VISIBLE);
		}
        return convertView; 
	}
	
	public void reSetFilteredContents(String[] filteredContents) {
		this.initialContentsArray = filteredContents;
		this.contentsArrayExL3 = hiddenL3Items(filteredContents);
	}

	public void setCurrId(int currId) {
		this.currId = currId;
	}
	
	private void changeList(String contentId){
		if(expandFlag){
			addL3Content(contentId);
		}else{
			recoverExL3Content();
		}
		expandFlag = !expandFlag;
	}
	
	private void addL3Content(String contentId){
		List<String> addL3List = new ArrayList<String>();
		for(String fContent: initialContentsArray){
	        String[] contentArray = fContent.split(",");
			if(contentArray[1].equalsIgnoreCase("L3") ){
				if(!contentArray[2].endsWith(contentId)){
					continue;
				}
			}
			addL3List.add(fContent);
		}
		contentsArrayExL3 = addL3List.toArray(new String[addL3List.size()]);
		notifyDataSetChanged();
	}
	
	private void recoverExL3Content(){
		this.contentsArrayExL3 = hiddenL3Items(initialContentsArray);
		notifyDataSetChanged();
	}

//	private void refreshActivity(){
//        Intent intent = new Intent();
//        intent.setClass(mContext, SlidingActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent); 
//        Log.i(LOG_TAG, "refreshActivity()...");
//	}
}
