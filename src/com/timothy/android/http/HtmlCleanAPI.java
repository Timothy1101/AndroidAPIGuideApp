package com.timothy.android.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;  
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;  
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.htmlcleaner.HtmlCleaner;  
import org.htmlcleaner.TagNode;  
import org.htmlcleaner.XPatherException;  

import android.util.Log;

import com.timothy.android.uil.StringUtil;
  
/** 
 * 
 */  
public class HtmlCleanAPI {
	
	public final static String LOG_TAG = "HtmlCleanAPI";
	public final static int TEXT_LIMIT_PERPAGE = 1000;
	public final static String BASE_URL = "http://developer.android.com";
	
	public class PageContent{
		public int pageNo;
		List<HtmlContent> contentList = new ArrayList<HtmlContent>();
		public PageContent() {
			super();
		}
		public PageContent(int pageNo, List<HtmlContent> contentList) {
			super();
			this.pageNo = pageNo;
			this.contentList = contentList;
		}
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		public List<HtmlContent> getContentList() {
			return contentList;
		}
		public void setContentList(List<HtmlContent> contentList) {
			this.contentList = contentList;
		}
		@Override
		public String toString() {
			return "PageContent [pageNo=" + pageNo + ", contentList="
					+ contentList + "]";
		}
	}
	
	public class HtmlContent{
		public String tag;
		public String content;
		public HtmlContent(String tag,String content){
			this.tag = tag;
			this.content = content;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
  
	public void saveAsFileOutputStream(String path,String content) {
		OutputStream foutput = null;
		try {
			File file = new File(path);
//			if(!file.exists()){
//				file.createNewFile();
//			}
			foutput = new FileOutputStream(file);
			foutput.write(content.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
//				foutput.flush();
				foutput.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
//		System.out.println("Save Successfully!" + path);
	}
	
	public void saveAsFileOutputStream(String path,String name,String content) {
//		OutputStream foutput = null;
		BufferedWriter out = null;
		try {
			File file = new File(path,name);
			if (file.canWrite()){
                FileWriter filewriter = new FileWriter(file,true);
                out = new BufferedWriter(filewriter);
                out.write(content);
            }
//			foutput = new FileOutputStream(file);
//			foutput.write(content.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				out.close();
//				foutput.flush();
//				foutput.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
//		System.out.println("Save Successfully!" + path);
	}
	
	public static void download(String src,String subPath) throws Exception {
		Log.i(LOG_TAG, "subPath:"+subPath);
		Log.i(LOG_TAG, "src:"+src);
		
		String imgURL = BASE_URL+src;
		
		String[] folds = src.split("/");
		String imageName;
		String picPath = null;
		if(folds.length>0){
			imageName = folds[folds.length-1 ];
			picPath = src.replace(imageName, "");
			Log.i(LOG_TAG, "picName:"+imageName);
			Log.i(LOG_TAG, "picPath:"+picPath);
		}
		
		Log.i(LOG_TAG, "make folder if not created for:\n" + subPath + "/" + picPath);
		File imageFolder = new File(subPath + File.separator + picPath);
		if (!imageFolder.exists()) {  
			imageFolder.mkdirs();  
        }
		
	    URL url = new URL(imgURL);
	    URLConnection con = url.openConnection();
	    InputStream is = con.getInputStream();
	    byte[] bs = new byte[1024];
	    int len;
	    Log.i(LOG_TAG, "save image to :\n" + subPath + "/" + src);
	    OutputStream os = new FileOutputStream(subPath + File.separator + src);
	    while ((len = is.read(bs)) != -1) {
	      os.write(bs, 0, len);
	    }
	    os.close();
	    is.close();
	}   

	public TagNode getNodeByNetwork(String url)
			throws XPatherException {
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = null;
		try {
			node = cleaner.clean(new URL(url), "utf-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return node;
	}

	public TagNode getNodeByLocal(String xmlurl)
			throws XPatherException {
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = null;
		try {
			// node = cleaner.clean(new
			// URL("http://developer.android.com/guide/topics/providers/content-providers.html"),"utf-8");
			node = cleaner.clean(new File(xmlurl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return node;
	}

	public String parseContent(TagNode node, String xpath,String subPath) {
		StringBuilder contentSB = new StringBuilder();
		List<HtmlContent> contentList = new ArrayList<HtmlContent>();
		Object[] ns = node.getElementsByName("title", true);
		if (ns!=null && ns.length > 0) {
//			contentSB.append(((TagNode) ns[0]).getText());
		}
		//----- //div[@id='jd-content']------
//		contentSB.append("-------------------------------start div 'jd-content' ....-------------------------------");
		try {
			ns = node.evaluateXPath(xpath);
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		
		TagNode qvTag = null;
		for (Object on : ns) {
			TagNode n = (TagNode) on;
			TagNode[] subTNs = n.getAllElements(true);
			for (TagNode sbn : subTNs) {
				String sbnName = sbn.getName();
				String sbnId = sbn.getAttributeByName("id");
				String sbnText = sbn.getText().toString();
				
				if(sbnId!=null && sbnId.equalsIgnoreCase("qv")){
					qvTag = sbn;
				}else {
					if (sbnName.equalsIgnoreCase("H1") || sbn.getName().equalsIgnoreCase("H2") || 
							sbn.getName().equalsIgnoreCase("H3") || sbn.getName().equalsIgnoreCase("H4") ||
							sbn.getName().equalsIgnoreCase("H5") || sbn.getName().equalsIgnoreCase("H6") ||
							sbn.getName().equalsIgnoreCase("H7")) {
//						TagNode pTN = sbn.getParent() ;
//						String pId = pTN.getAttributeByName("id");
						if(sbn.getParent() != qvTag){
							contentList.add(new HtmlContent(sbnName,sbnText));
						}
					}else if (sbnName.equalsIgnoreCase("P")) {
						if(sbnText.trim().length()>0){
							contentList.add(new HtmlContent("P",sbnText));
						}
						
					}else if (sbnName.equalsIgnoreCase("dl")) {
						TagNode[] dls = sbn.getAllElements(true);
						for (TagNode dl : dls) {
							String dlName = dl.getName();
							String dlText = dl.getText().toString();
							if (dlName.equalsIgnoreCase("dt")) {
								if(dlText.trim().length()>0){
									contentList.add(new HtmlContent("dt",dlText));
								}
							}else if (dlName.equalsIgnoreCase("dd")) {
								if(dlText.trim().length()>0){
									contentList.add(new HtmlContent("dd",dlText));
								}
							}
						}
						
					}else if (sbnName.equalsIgnoreCase("ul")) {
						TagNode[] uls = sbn.getAllElements(true);
						for (TagNode ul : uls) {
							String ulName = ul.getName();
							String ulText = ul.getText().toString();
							if (ulName.equalsIgnoreCase("li")) {
								if(ulText.trim().length()>0){
									contentList.add(new HtmlContent("li",ulText));
								}
							}
						}
					}else if (sbnName.equalsIgnoreCase("pre")) {
						if(sbnText.trim().length()>0){
							contentList.add(new HtmlContent("pre",sbnText));
						}
					}else if (sbnName.equalsIgnoreCase("img")) {
						String src = sbn.getAttributeByName("src");
						
						if(src!=null){
							Log.i(LOG_TAG,"src:"+src);
//							String imgURL = BASE_URL+src;
//							String name = src.replace("/images/", "");
							try {
								download(src,subPath);
								contentList.add(new HtmlContent("img",src));
							} catch (Exception e) {
								e.printStackTrace();
							}
							
//							try {
//								Thread thread = new Thread(){
//								   public void run(){
//									   try {
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								   }
//								};
//								thread.start();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
							
						}
					}
				}

			}
		}
//		contentSB.append("-------------------------------end div 'jd-content' .-------------------------------");
		
		for(HtmlContent con: contentList){
			contentSB.append("\n");
			if(con.getTag().equalsIgnoreCase("img")){
				contentSB.append("<img alt=\"\" src=\"");
				contentSB.append( con.getContent().trim() );
				contentSB.append("\">");
			}else{
				contentSB.append("<" + con.getTag() + ">");
				contentSB.append("\n");
				contentSB.append( con.getContent().trim() );
				contentSB.append("\n");
				contentSB.append("</" + con.getTag() + ">");
			}
			
			contentSB.append("\n");	

		}
		String tranString = StringUtil.rmvBlankLines(contentSB.toString());
		return tranString;
	}
	
	public List<PageContent> getPageList(TagNode node) {
		List<PageContent> pageList = new ArrayList<PageContent>();
		List<HtmlContent> contentList = new ArrayList<HtmlContent>();
		TagNode[] tns = node.getAllElements(true);
		int total = 0;
		int pageNo = 1;
		for (TagNode tn : tns) {
			if (tn.getName().equalsIgnoreCase("body")|| tn.getName().equalsIgnoreCase("head")) {
				continue;
			}
			String tnText =  tn.getText().toString();
			if (tn.getName().equalsIgnoreCase("img")){
				tnText = tn.getAttributeByName("src");
			}
			int textLength = 0;
			if(tnText!=null){
				textLength = tnText.length();
			}
			total += textLength;
			contentList.add(new HtmlContent(tn.getName(), tnText));
			if (total > TEXT_LIMIT_PERPAGE) {
				pageList.add(new PageContent(pageNo,contentList));
				total = 0;
				contentList = new ArrayList<HtmlContent>();//new contentList
				pageNo++;
			} 				
		}
		if(contentList.size()>0){
			pageList.add(new PageContent(pageNo,contentList));
		}
		return pageList;
	}
	
	public PageContent getPage(TagNode node,int findPageNo) {
		List<PageContent> pageList = getPageList(node);
		for(PageContent pc:pageList){
			if(pc.getPageNo() == findPageNo){
				return pc;
			}
		}
		return null;
	}
	
	//get page size by parsed content
	public int getContentPageSize(TagNode node) {
		TagNode[] tns = node.getAllElements(true);
		int pages = 1;
		int total = 0;
		for (TagNode tn : tns) {
			if (tn.getName().equalsIgnoreCase("body") || tn.getName().equalsIgnoreCase("head")) {
				continue;
			}
			total += tn.getText().toString().length();
			if(total > TEXT_LIMIT_PERPAGE){
				pages++;
				total=0;
			}
		}
		return pages;
	}

	
//	public static void main(String args[]){
//		String url = "http://developer.android.com/guide/topics/providers/content-providers.html";
//		String xpath = "//div[@id='jd-content']";
//		String localPath = "E:/test.xml";
//		HtmlCleanAPI2 api = new HtmlCleanAPI2();
//		TagNode tagNode;
//		try {
//			tagNode = api.getNodeByNetwork(url);
//			String content = api.parseContent(tagNode, xpath);
//			System.out.println("Save content of:\n"+content);
//			api.saveAsFileOutputStream(localPath, content);
//			TagNode tagNode2 = api.getNodeByLocal(localPath);
//			List<PageContent> pageList = api.getPageList(tagNode2);
//			System.out.println("---Get All PageContent---");
//			for(PageContent hc : pageList){
//				System.out.println("---------------"+hc.getPageNo()+"-----------------");
//				List<HtmlContent> contentList = hc.getContentList();
//				for(HtmlContent pc:contentList){
//					System.out.println(pc.getTag()+" " + pc.getContent());
//				}
//			}
//		} catch (XPatherException e) {
//			e.printStackTrace();
//		}
//	}
}