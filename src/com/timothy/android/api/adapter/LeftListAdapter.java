package com.timothy.android.api.adapter;

import java.io.File;
import java.util.List;

import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.apiguide.R;
import com.timothy.android.apiguide.SlidingActivity;
import com.timothy.util.SPUtil;
import com.timothy.util.StringUtil;
import com.timothy.util.XMLParserUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@Deprecated
public class LeftListAdapter extends BaseAdapter{

	public static final String LOG_TAG = "LeftListAdapter";
	
	private Context mContext;
	private List<ContentBean> sbList;
	private int index;
	private LayoutInflater mInflater;
//	private String appPath;

	SharedPreferences sp;
	
	static class ViewHolder{
		public TextView title;
		public ImageView openImg;
		public ImageView downloadFlag;
	}

	public LeftListAdapter(Context context, List<ContentBean> sbList,int index) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context); 
		this.sbList = sbList;
		this.index = index;
		sp = context.getSharedPreferences("AndroidAPISP",0);
	}

	
	@Override
	public int getCount() {
		return sbList.size();
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

        final ContentBean bean = sbList.get(position);
        ContentBean mBean = XMLParserUtil.getContentByIndex(mContext,bean.getmIndex());
        
        Log.i(LOG_TAG,bean.toString());
		holder.title.setText(bean.getName());
		
		boolean openFlag = SPUtil.getBooleanFromSP(SPUtil.SP_KEY_OPEN_FLAG, sp);
		int mIndex = SPUtil.getIntegerFromSP(SPUtil.SP_KEY_MCONTENT_INDEX, sp);
		
		Log.i(LOG_TAG, "openFlag:" + openFlag);
		Log.i(LOG_TAG, "mIndex:" + mIndex);
		
		if(bean.getType().equalsIgnoreCase("M")){
			holder.title.setTypeface(null,Typeface.BOLD);
			Log.i(LOG_TAG, "bean.getmIndex():" + bean.getmIndex());
			if(openFlag && mIndex == bean.getmIndex()){
				Log.i(LOG_TAG, "-------set open--------");
//				holder.openImg.setBackgroundResource(R.drawable.open_32);
				holder.openImg.setBackgroundResource(R.drawable.disclosure_up2);

			}else{
				Log.i(LOG_TAG, "-------set close--------");
//				holder.openImg.setBackgroundResource(R.drawable.close_32);
				holder.openImg.setBackgroundResource(R.drawable.disclosure_down2);

			}
			holder.openImg.setVisibility(View.VISIBLE);
			
		}else{
			holder.title.setTypeface(null,Typeface.NORMAL);
			holder.openImg.setBackgroundResource(R.drawable.bullet_48);
//			holder.openImg.setVisibility(View.INVISIBLE);
		}
		
		
		if(bean.getIndex() == index){
			convertView.setBackgroundColor(Color.parseColor("#FCFCFC"));
			holder.title.setTextColor(Color.parseColor("#307FC4"));
		}else{
			convertView.setBackgroundColor(Color.parseColor("#E8E8E8"));
			holder.title.setTextColor(Color.parseColor("#000000"));
		}
		
		String appPath = SPUtil.getFromSP(SPUtil.APP_HOME_PATH, sp);
		String contentPath = appPath +  File.separator + StringUtil.rmvSpace(mBean.getName()) +  File.separator + StringUtil.rmvSpace(bean.getName()) +".xml";
//		Log.i(LOG_TAG,"contentPath:"+contentPath);
		
		if(new File(contentPath).exists()){
			holder.downloadFlag.setBackgroundResource(R.drawable.view_content_32);
		}else{
			holder.downloadFlag.setBackgroundResource(R.drawable.downloaded_32);
		}
		
		if(openFlag){
			holder.downloadFlag.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SPUtil.save2SP(SPUtil.SP_KEY_MCONTENT_INDEX, bean.getmIndex(), sp);
					SPUtil.save2SP(SPUtil.SP_KEY_SCONTENT_INDEX, bean.getIndex(), sp);
					refreshActivity();
				}
			});			
		}
		
//		if(openFlag && bean.getType().equalsIgnoreCase("M")){
//			holder.downloadFlag.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					SPUtil.save2SP(SPUtil.SP_KEY_MCONTENT_INDEX, bean.getmIndex(), sp);
//					SPUtil.save2SP(SPUtil.SP_KEY_SCONTENT_INDEX, bean.getIndex(), sp);
//					refreshActivity();
//				}
//			});			
//		}
        return convertView; 
        
	}
	
	public void getList(){
		
	}
	
	public void refreshList(int mIndex,boolean openFlag){
		if(openFlag){
			sbList = XMLParserUtil.getContentsByMIndex(mContext,mIndex);
		}else{
			sbList = XMLParserUtil.getContentsByMIndex(mContext,-1);
		}
		this.notifyDataSetChanged();
	}
	
	public void refreshActivity(){
        Intent intent = new Intent();
        intent.setClass(mContext, SlidingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent); 
        
        Log.i(LOG_TAG, "refreshActivity()...");
	}
}
