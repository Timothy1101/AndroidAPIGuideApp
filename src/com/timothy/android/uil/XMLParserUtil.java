package com.timothy.android.uil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.timothy.android.api.bean.ChildContentBean;
import com.timothy.android.api.bean.ContentBean;
import com.timothy.android.api.activity.R;

public class XMLParserUtil {
	
	public static final String LOG_TAG = "XMLParser";

	/*public static List<ContentBean> getAllContents(Context context) {
		XmlResourceParser xrp = context.getResources().getXml(R.xml.contents);
		List<ContentBean> cbList = null;
		ContentBean bean = null;
		try {
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagName = xrp.getName();
					
					if (tagName.equals("contents")) {
						cbList = new ArrayList<ContentBean>();
					} else if (tagName.equals("content")) {
						bean = new ContentBean();
					} else if (tagName.equals("index") && bean != null) {
						bean.setIndex(Integer.valueOf(xrp.nextText()));
					} else if (tagName.equals("name") && bean != null) {
						bean.setName(xrp.nextText());
					} else if (tagName.equals("url") && bean != null) {
						bean.setUrl(xrp.nextText());
					} else if (tagName.equals("type") && bean != null) {
						bean.setType(xrp.nextText());
					} else if (tagName.equals("mIndex") && bean != null) {
						bean.setmIndex(Integer.valueOf(xrp.nextText()));
					} 
				}
				if (xrp.getEventType() == XmlResourceParser.END_TAG) {
					String tagName = xrp.getName();
					if (tagName.equals("content") && cbList != null && bean != null) {
						cbList.add(bean);
					} else if (tagName.equals("contents")) {
						break;
					}
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cbList;
	}*/
	
	public static List<ContentBean> getContentsByMIndex(Context context,int mIndex) {
		XmlResourceParser xrp = context.getResources().getXml(R.xml.contents);
		List<ContentBean> cbList = null;
		ContentBean bean = null;
//		List<ChildContentBean> ccbList = null; 
//		ChildContentBean cBean = null;
		try {
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagName = xrp.getName();
					
					if (tagName.equals("contents")) {
						cbList = new ArrayList<ContentBean>();
					} else if (tagName.equals("content")) {
						bean = new ContentBean();
					} else if (tagName.equals("index") && bean != null) {
						bean.setIndex(Integer.valueOf(xrp.nextText()));
					} else if (tagName.equals("name") && bean != null) {
						bean.setName(xrp.nextText());
					} else if (tagName.equals("url") && bean != null) {
						bean.setUrl(xrp.nextText());
					} else if (tagName.equals("type") && bean != null) {
						bean.setType(xrp.nextText());
					} else if (tagName.equals("mIndex") && bean != null) {
						bean.setmIndex(Integer.valueOf(xrp.nextText()));
					} 
					
//					else if (tagName.equals("child_contents") && bean != null) {
//						ccbList = new ArrayList<ChildContentBean>();
//					} else if (tagName.equals("child_content") && bean != null) {
//						cBean = new ChildContentBean();
//					} else if (tagName.equals("child_name") && cBean != null) {
//						cBean.setChildContentName(xrp.nextText());
//					} else if (tagName.equals("child_url") && cBean != null) {
//						cBean.setChildContentURL(xrp.nextText());
//					}
				}
				if (xrp.getEventType() == XmlResourceParser.END_TAG) {
					String tagName = xrp.getName();
//					if (tagName.equals("child_content") && cBean != null && ccbList != null) {
//						ccbList.add(cBean);
//						cBean = null;
//					}else if (tagName.equals("child_contents") && bean != null ) {
//						bean.setCcbList(ccbList);
//						ccbList = null;
//					}else 
						
				    if (tagName.equals("content") && cbList != null && bean != null) {
						if(bean.getType().equalsIgnoreCase("M") || bean.getmIndex() == mIndex){
							cbList.add(bean);
						}
					} else if (tagName.equals("contents")) {
						break;
					}
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cbList;
	}
	
	public static ContentBean getContentByIndex(Context context,int index) {
		if(index == 3){
			Log.i(LOG_TAG, "------------------getContentByIndex...-----------------  "+ String.valueOf(index));
		}else{
//			Log.i(LOG_TAG, "------------------getContentByIndex...-----------------");
		}
		
		
		XmlResourceParser xrp = context.getResources().getXml(R.xml.contents);
		ContentBean bean = null;
		List<ChildContentBean> ccbList = null; 
		ChildContentBean cBean = null;
		try {
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagName = xrp.getName();
					
					if (tagName.equals("content")) {
						bean = new ContentBean();
					} else if (tagName.equals("index") && bean != null) {
						bean.setIndex(Integer.valueOf(xrp.nextText()));
					} else if (tagName.equals("name") && bean != null) {
						bean.setName(xrp.nextText());
					} else if (tagName.equals("url") && bean != null) {
						bean.setUrl(xrp.nextText());
					} else if (tagName.equals("type") && bean != null) {
						bean.setType(xrp.nextText());
					} else if (tagName.equals("mIndex") && bean != null) {
						bean.setmIndex(Integer.valueOf(xrp.nextText()));
					} else if (tagName.equals("child_contents") && bean != null) {
//						Log.i(LOG_TAG,"find child_contents.");
						ccbList = new ArrayList<ChildContentBean>();
					} else if (tagName.equals("child_content") && bean != null) {
//						Log.i(LOG_TAG,"find child_content.");
						cBean = new ChildContentBean();
					} else if (tagName.equals("child_name") && cBean != null) {
//						Log.i(LOG_TAG,"find child_name.");
						cBean.setChildContentName(xrp.nextText());
					} else if (tagName.equals("child_url") && cBean != null) {
//						Log.i(LOG_TAG,"find child_url.");
						cBean.setChildContentURL(xrp.nextText());
					}
				}
				if (xrp.getEventType() == XmlResourceParser.END_TAG) {
					String tagName = xrp.getName();
					if (tagName.equals("child_content") && cBean != null && ccbList != null) {
						ccbList.add(cBean);
//						Log.i(LOG_TAG,"add cBean:"+cBean.toString());
						cBean = null;
					}else if (tagName.equals("child_contents") && ccbList != null && bean != null ) {
						if( ccbList !=null){
							bean.setCcbList(ccbList);
						}
						ccbList = null;
					}else if (tagName.equals("content") && bean != null) {
						if(index == bean.getIndex()){
//							if(ccbList !=null && ccbList.size()>0){
								if(index == 3){
									Log.i(LOG_TAG,bean.toString());
								}
								
//							}
							break;
						}
					} 
				}
				
//				if(bean!=null){
//					Log.i(LOG_TAG,bean.toString());
//				}
				
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(index == 3){
			Log.i(LOG_TAG, "------------------getContentByIndex End-----------------"+ String.valueOf(index));
		}else{
//			Log.i(LOG_TAG, "------------------getContentByIndex End-----------------");
		}
		
		
		return bean;
		
	}
}
