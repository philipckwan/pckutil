package com.pck.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class ComicGet {
	public static final String HTML_EXTENSION = ".htm";
	public static final String URL_PATH_SEPARATOR = "/";
	//public static final String URL_JPG_KEY = "+";
	public static final String JPG_EXTENSION = ".jpg";
	
	public static final String configFile = "input.txt";
	public static final String KEYWORD_HTMLURL = "htmlUrl=";
	public static final String KEYWORD_JPGURL = "jpgUrl=";	
	public static final String KEYWORD_DIR = "dir=";
	public static final String KEYWORD_PAGES = "pages=";
	public static final String KEYWORD_URLJPGKEY = "urlJpgKey=";
	
	public static String htmlUrl = null;
	public static String jpgUrl = null;
	public static String dir = null;
	public static int pages = 0;	
	public static String urlJpgKey = "_";
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(configFile)));
		} catch (FileNotFoundException e1) {			
			e1.printStackTrace();
			System.exit(1);
		}
		
		/*
		 * Example of htmlUrl and jpgUrl
		 * htmlUrl = "http://comic2.kukudm.com/comiclist/277/15035/1.htm"
		 * jpgUrl = "http://210.51.25.193/kuku6comic6/200910/20091026/295/comic.kukudm.com_00103D.jpg"
		 */
		
		String configLine = null;
		try {
			while ((configLine = br.readLine()) != null) {
				if (configLine.indexOf(KEYWORD_HTMLURL) >= 0) htmlUrl = configLine.substring(configLine.indexOf(KEYWORD_HTMLURL) + KEYWORD_HTMLURL.length()).trim();
				if (configLine.indexOf(KEYWORD_JPGURL) >= 0) jpgUrl = configLine.substring(configLine.indexOf(KEYWORD_JPGURL) + KEYWORD_JPGURL.length()).trim();
				if (configLine.indexOf(KEYWORD_DIR) >= 0) dir = configLine.substring(configLine.indexOf(KEYWORD_DIR) + KEYWORD_DIR.length()).trim();
				if (configLine.indexOf(KEYWORD_PAGES) >= 0) pages = Integer.parseInt(configLine.substring(configLine.indexOf(KEYWORD_PAGES) + KEYWORD_PAGES.length()).trim());
				if (configLine.indexOf(KEYWORD_URLJPGKEY) >= 0) urlJpgKey = configLine.substring(configLine.indexOf(KEYWORD_URLJPGKEY) + KEYWORD_URLJPGKEY.length()).trim();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		if (htmlUrl == null || jpgUrl == null || dir == null || pages == 0) {
			System.out.println("ERROR - Please populate input.txt");
			System.exit(1);	
		}
		
		int jpgBegin = 1;
		int jpgEnd = pages;
		String oldStr = jpgBegin + HTML_EXTENSION;
		String newStr = (jpgBegin + 1) + HTML_EXTENSION;

		
		int jpgHeadIdx = jpgUrl.lastIndexOf(URL_PATH_SEPARATOR);
		int jpgTailIdx = jpgUrl.lastIndexOf(urlJpgKey);
		String comicPattern = jpgUrl.substring(jpgHeadIdx, jpgTailIdx + 1);
		jpgUrl = jpgUrl.substring(0, jpgHeadIdx);
		String htmlLine;
		
		int comicMatchHeadIdx;
		int comicMatchTailIdx;
		String comicMatch = null;
		String comicUrl;

		try {
			for (int i = jpgBegin; i <= jpgEnd; i++) {
				System.out.println("html: " + htmlUrl);
				
				BufferedReader htmlBR = new BufferedReader(new InputStreamReader(new DataInputStream(new URL(htmlUrl).openStream())));
				while ((htmlLine = htmlBR.readLine()) != null) {
					comicMatchHeadIdx = htmlLine.indexOf(comicPattern);
					if (comicMatchHeadIdx >= 0) {
						comicMatch = htmlLine.substring(comicMatchHeadIdx);
						comicMatchTailIdx = comicMatch.indexOf(JPG_EXTENSION);
						comicMatch = comicMatch.substring(0, comicMatchTailIdx);						
						break;
					}
				}
				comicUrl = jpgUrl + comicMatch + JPG_EXTENSION; 
				System.out.println("comicUrl: " + comicUrl);
				
				
				URL url = new URL(comicUrl);
				
				URLConnection urlConn = url.openConnection();
				
				urlConn.setRequestProperty("Referer", htmlUrl);
				int contentLength = urlConn.getContentLength();
				//System.out.println("contentType: " + contentType + "; contentLength: " + contentLength);
				BufferedInputStream bis = new BufferedInputStream(urlConn.getInputStream());
				byte[] data = new byte[contentLength];
				int bytesRead = 0;
				int offset = 0;
				while (offset < contentLength) {
					bytesRead = bis.read(data, offset, data.length - offset);
					if (bytesRead == -1) break;
					offset += bytesRead;
				}
				bis.close();
				
				if (offset != contentLength) {
					System.out.println("ERROR - offset != contentLength");
				}
				//String outputFileStr = ((i <= 9) ? "0" : "") + i + JPG_EXTENSION;
				String outputFileStr = "";
				
				if (i < 10) {
					if (pages > 10) outputFileStr += "0";
					if (pages > 100) outputFileStr += "0";
				} else if (i < 100) {
					if (pages > 100) outputFileStr += "0";
				} 
				outputFileStr += i + JPG_EXTENSION;
				//System.out.println("dir:" + dir + ";");
				//System.out.println("File.separator:" + File.separator + ";");
				//System.out.println("outputFileStr:" + outputFileStr + ";");
				FileOutputStream fos = new FileOutputStream(new File(dir + File.separator + outputFileStr));
				fos.write(data);
				fos.flush();
				fos.close();
				
				oldStr = i + HTML_EXTENSION;
				newStr = (i + 1) + HTML_EXTENSION;
				htmlUrl = htmlUrl.replace(oldStr, newStr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done!");
	}

}
