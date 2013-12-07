package com.timothy.android.api.bean;

public class ChildContentBean{
	String childContentName;
	String childContentURL;
	
	public ChildContentBean() {
		super();
	}
	public ChildContentBean(String childContentName, String childContentURL) {
		super();
		this.childContentName = childContentName;
		this.childContentURL = childContentURL;
	}
	public String getChildContentName() {
		return childContentName;
	}
	
	public void setChildContentName(String childContentName) {
		this.childContentName = childContentName;
	}
	public String getChildContentURL() {
		return childContentURL;
	}
	public void setChildContentURL(String childContentURL) {
		this.childContentURL = childContentURL;
	}
	
	@Override
	public String toString() {
		return "ChildContentBean [childContentName=" + childContentName
				+ ", childContentURL=" + childContentURL + "]";
	}
}