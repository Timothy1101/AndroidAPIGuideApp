package com.timothy.android.uil;

import java.io.File;

public class FileUtil {

public final static String LOG_TAG = "FileUtil";
	
	public static void deleteFolder(File dir) {
		if(dir.exists()){
			File filelist[] = dir.listFiles();
			if(filelist!=null){
				int listlen = filelist.length;
				for (int i = 0; i < listlen; i++) {
					if (filelist[i].isDirectory()) {
						deleteFolder(filelist[i]);
					} else {
						filelist[i].delete();
					}
				}
			}
			dir.delete();			
		}
	}
}
