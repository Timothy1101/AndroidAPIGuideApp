package com.timothy.android.uil;

public class StringUtil {
	
	public static final String[] SPECIAL_CHARACTER_CODES = new String[]{"&quot;","&lt;","&gt;","&gt","&mdash;","&#64;"}; 
	public static final String[] SPECIAL_CHARACTER_SYMBOLS = new String[]{"\"","<",">",">","\'","@"}; 
	
	public static String rmvSpecial(String baseStr) {
		String rmvStr = baseStr;
		for (int i = 0; i < SPECIAL_CHARACTER_CODES.length; i++) {
			rmvStr = rmvStr.replaceAll(SPECIAL_CHARACTER_CODES[i],SPECIAL_CHARACTER_SYMBOLS[i]);
		}
		return rmvStr;
	}
	
	public static String rmvBlankLines(String input) {  
        return input.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1");  
	}  
	
	public static String rmvSpace(String baseStr){
		return baseStr.replace(" ", "");
	}
	
	public static String rmvEnter(String input) {  
        return input.replaceAll("\n", "");  
	}
	
	//
	public static int getBlankNumber(String s, int index){
	    if (index < s.length()) {
	        if (s.charAt(index) == ' ') {
	            return getBlankNumber(s, index + 1) + 1;
	        } else {
	            return 0;
	        }
	    } else {
	        return 0;
	    }
	}
	
	public static String mergeBlank(String s){
	    int numberBlank = 0;
	    String a1;
	    String a2;
	    for (int index = 0; index < s.length(); index++) {
	        numberBlank = getBlankNumber(s, index);
	        if (numberBlank >= 2) {
	            a1 = s.substring(0, index);
	            a2 = s.substring(index + numberBlank - 1, s.length());
	            s = a1 + a2;
	        }
	    }
	    return s;
	}
	
	public static String trim(String s){
	    if (s.charAt(0) == ' ') {
	        s = s.substring(1, s.length());
	    }
	    if (s.charAt(s.length() - 1) == ' ') {
	        s = s.substring(0, s.length() - 1);
	    }
	    return s;
	}
	
	public static boolean  isEmpty(String str){
		return (str==null || str.length()==0);
	}

}
