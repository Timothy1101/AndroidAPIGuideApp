package com.timothy.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class FileUtil {

public final static String LOG_TAG = "FileUtil";
	
	private String SDPATH;
	private int FILESIZE = 4 * 1024; 
	
	public String getSDPATH(){
		return SDPATH;
	}
	
	public FileUtil(){
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	public void deleteFolder(File dir) {
		File filelist[] = dir.listFiles();
		int listlen = filelist.length;
		for (int i = 0; i < listlen; i++) {
			if (filelist[i].isDirectory()) {
				deleteFolder(filelist[i]);
			} else {
				filelist[i].delete();
			}
		}
		dir.delete();// É¾³ýµ±Ç°Ä¿Â¼
	}

	/**
	 * ï¿½ï¿½SDï¿½ï¿½ï¿½Ï´ï¿½ï¿½ï¿½ï¿½Ä¼ï¿½
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException{
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName){
		File dir = new File(SDPATH + dirName);
//		if(!dir.isDirectory()){
			dir.mkdir();
//		}
		return dir;
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	/**
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try {
//			if(!new File(path).isDirectory()){
//				
//			}
			
			createSDDir(path);
			
			file = createSDFile(path + File.separator + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while((input.read(buffer)) != -1){
				output.write(buffer);
			}
			output.flush();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public File saveFile(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try {
			file = createSDFile(path + File.separator + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while((input.read(buffer)) != -1){
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public static FileInputStream getFileInputStream(String filePath,String fileName){
		Log.i(LOG_TAG, "filePath:"+filePath);
		Log.i(LOG_TAG, "fileName:"+fileName);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(filePath+ File.separator+fileName));
		} catch (FileNotFoundException e) {
			Log.i(LOG_TAG,e.getMessage());
		}
		Log.i(LOG_TAG, "getExamStream() end.");
		return inputStream;
	}
	
	public static FileInputStream getFileInputStream(String filePath){
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			Log.i(LOG_TAG,e.getMessage());
		}
		Log.i(LOG_TAG, "getExamStream() end.");
		return inputStream;
	}
	
	public static String inputStream2String(InputStream is) throws IOException {
		Log.i(LOG_TAG,"inputStream2String...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
}
