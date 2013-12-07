package com.timothy.android.api.bean;

import java.util.ArrayList;
import java.util.List;


public class ContentBean extends BaseBean {

	
	@Override
	public String toString() {
		return "ContentBean [index=" + index + ", name=" + name + ", url="
				+ url + ", type=" + type + ", mIndex=" + mIndex
				+ ", localAddr=" + localAddr + ", ccbList=" + ccbList + "]";
	}

	private int index;
	private String name;
	private String url;
	private String type;
	private int mIndex;
	private String localAddr;
	
	public ContentBean() {
		super();
	}
	
	public ContentBean(int index, String name, String url, String type,
			int mIndex,String localAddr) {
		super();
		this.index = index;
		this.name = name;
		this.url = url;
		this.type = type;
		this.mIndex = mIndex;
		this.localAddr = localAddr;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getmIndex() {
		return mIndex;
	}
	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}
	
	//add child content Bean
	private List<ChildContentBean> ccbList = new ArrayList<ChildContentBean>();
	
	public List<ChildContentBean> getCcbList() {
		return ccbList;
	}

	public void setCcbList(List<ChildContentBean> cbList) {
		this.ccbList = cbList;
	}


	
}
