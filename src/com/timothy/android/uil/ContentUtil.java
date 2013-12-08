package com.timothy.android.uil;

public class ContentUtil {

	public static String getContentsById(String[] contentsArray,int id){
		for(String contents:contentsArray){
			String[] contentArray = contents.split(",");
			String contentId = contentArray[0];
			if(Integer.valueOf(contentId) == id){
				return contents;
			}
		}
		return null;
	}
	
	public static String getSuperContentsById(String[] contentsArray,int id){
		String contents = getContentsById(contentsArray,id);
		
		String[] contentArray = contents.split(",");
		int superId = Integer.valueOf(contentArray[2]);
		
		String superContents = getContentsById(contentsArray,superId);
		
		return superContents;
	}
	
}
