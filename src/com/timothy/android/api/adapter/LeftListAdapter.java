package com.timothy.android.api.adapter;

import java.io.File;
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

	public static final String LOG_TAG = "LeftListAdapter2";
	
	private Context mContext;
//	private List<ContentBean> sbList;
	private String[] filteredContents;
	private int currId;
	private LayoutInflater mInflater;
	SharedPreferences sp;
	
	static class ViewHolder{
		public TextView title;
		public ImageView openImg;
		public ImageView downloadFlag;
	}

	public LeftListAdapter(Context context, String[] filteredContents,int currId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context); 
		this.filteredContents = filteredContents;
		this.currId = currId;
		sp = context.getSharedPreferences("AndroidAPISP",0);
	}

	
	@Override
	public int getCount() {
		return filteredContents.length;
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

        String contents = filteredContents[position];
        String[] contentArray = contents.split(",");
        String contentId = contentArray[0];
        String contentLevel = contentArray[1];
        String contentSuperId = contentArray[2];
        String contentName = contentArray[3];
        String contentURL = contentArray[4];
        
        //set image
		if(contentLevel.equalsIgnoreCase("L1")){
			holder.openImg.setBackgroundResource(R.drawable.disclosure_up2);
		}else if(contentLevel.equalsIgnoreCase("L2")) {
			holder.openImg.setBackgroundResource(R.drawable.disclosure_down2);
		}else if(contentLevel.equalsIgnoreCase("L3")) {
			holder.openImg.setBackgroundResource(R.drawable.bullet_48);
		}
		
		//set title
		holder.title.setText(contentName);
		if(Integer.valueOf(contentId) == currId){
			convertView.setBackgroundColor(Color.parseColor("#FCFCFC"));
			holder.title.setTextColor(Color.parseColor("#307FC4"));
		}else{
			convertView.setBackgroundColor(Color.parseColor("#E8E8E8"));
			holder.title.setTextColor(Color.parseColor("#000000"));
		}
		
		String branchName = SPUtil.getFromSP(SPUtil.BRANCH_PATH_NAME, sp);
		String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
		String contentPath = appPath +  File.separator + StringUtil.rmvSpace(branchName) +  File.separator + StringUtil.rmvSpace(contentName) +".xml";
		//set download flag
		if(new File(contentPath).exists()){
			holder.downloadFlag.setBackgroundResource(R.drawable.view_content_32);
		}else{
			holder.downloadFlag.setBackgroundResource(R.drawable.downloaded_32);
		}
        return convertView; 
	}
	
	
	
	public void setFilteredContents(String[] filteredContents) {
		this.filteredContents = filteredContents;
	}


	public void setCurrId(int currId) {
		this.currId = currId;
	}


	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(mContext, SlidingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent); 
        
        Log.i(LOG_TAG, "refreshActivity()...");
	}
}
