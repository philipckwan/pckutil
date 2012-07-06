package com.pck.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pck.common.DownloadWorker;
import com.pck.common.Logger;
import com.pck.common.PropertiesManager;
import com.pck.common.WorkItem;

public class ComicGet2 {

	public static final String CONFIG_FILE = "input.txt";
	public static final String HTTP_HEAD = "http:";
	public static final String JPG_EXTENSION = ".jpg";
	public static final String HTML_EXTENSION = ".htm";
	
	public static final String KEYWORD_HTMLURL = "htmlUrl";
	public static final String KEYWORD_JPGURL = "jpgUrl";	
	public static final String KEYWORD_DIR = "dir";
	public static final String KEYWORD_PAGES = "pages";
	public static final String KEYWORD_JPG_PATTERN = "jpgPattern";
	public static final String KEYWORD_NUM_THREADS = "numThreads";
	
	public static final int DEFAULT_NUM_THREADS = 5;
	
	private static String htmlUrl = null;
	private static String jpgUrl = null;
	private static String dir = null;
	private static int pages = 0;
	private static String jpgPattern = null;
	private static int numThreads = 0;		
	private static List<Thread> threadList = new ArrayList<Thread>();
	
	public static void main(String[] args) {
		Logger.debug("ComicGet.main: START");
		
		PropertiesManager.initWithFile(CONFIG_FILE); 
		
		htmlUrl = PropertiesManager.getProperty(KEYWORD_HTMLURL);
		jpgUrl = PropertiesManager.getProperty(KEYWORD_JPGURL);
		dir = PropertiesManager.getProperty(KEYWORD_DIR);
		pages = PropertiesManager.getPropertyInt(KEYWORD_PAGES);
		jpgPattern = PropertiesManager.getProperty(KEYWORD_JPG_PATTERN);
		numThreads = PropertiesManager.getPropertyInt(KEYWORD_NUM_THREADS, DEFAULT_NUM_THREADS);		
		
		if (htmlUrl == null || jpgUrl == null || dir == null || pages == 0) {
			Logger.error("ComicGet.main: ERROR - Please populate configuration file:" + CONFIG_FILE + ";");
			System.exit(1);	
		}
		
		for (int i = 0; i < numThreads; i++) {
			Thread workerThread = new Thread(new DownloadWorker());
			workerThread.start();
			threadList.add(workerThread);
		}
		
		//int htmlBegin = 1;
		//int htmlLast = pages;
		String oldStr;
		String newStr;
		String htmlLine;
		int jpgPatternIdx, jpgPatternTailIdx;
		String jpgPatternTail;
		String jpgPatternHead = null;
		boolean first = true;
		
		//Logger.debug("__jpgPattern:" + jpgPattern + ";");
		
		try {
			for (int i = 1; i <= pages; i++) {
				//Logger.debug("html: " + htmlUrl);
				
				BufferedReader htmlBR = new BufferedReader(new InputStreamReader(new DataInputStream(new URL(htmlUrl).openStream())));
				while ((htmlLine = htmlBR.readLine()) != null) {
					//Logger.debug(htmlLine);
					jpgPatternIdx = htmlLine.indexOf(jpgPattern);
					if (jpgPatternIdx != -1) {
						//jpgPatternTail = htmlLine.substring(jpgPatternIdx);
						jpgPatternTailIdx = htmlLine.indexOf(JPG_EXTENSION, jpgPatternIdx) + JPG_EXTENSION.length();
						jpgPatternTail = htmlLine.substring(jpgPatternIdx, jpgPatternTailIdx);
						//Logger.debug("__jpgPatternTail:" + jpgPatternTail + ";");
						if (first) {
							jpgPatternHead = jpgUrl.substring(0, jpgUrl.indexOf(jpgPatternTail));
							first = false;
						}
						jpgUrl = jpgPatternHead + jpgPatternTail;
						//Logger.debug("__jpgUrl:" + jpgUrl + ";");
						WorkItem work = new WorkItem(new URL(jpgUrl), getFilePath(i));
						DownloadWorker.addWork(work);
					}
				}
				oldStr = i + HTML_EXTENSION;
				newStr = (i + 1) + HTML_EXTENSION;
				htmlUrl = htmlUrl.replace(oldStr, newStr);
			}
			
		} catch (IOException e) {
			Logger.error("ComicGet.main: ERROR - IOException:");
			e.printStackTrace();
		}
		
		while(!DownloadWorker.isQueueEmpty()) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		for (Thread worker : threadList) {
			worker.interrupt();
		}
		
		Logger.debug("ComicGet.main: END");
	}
	
	private static String getFilePath(int pageNum) {
		String outputFileStr = "";
		
		if (pageNum < 10) {
			if (pages > 10) outputFileStr += "0";
			if (pages > 100) outputFileStr += "0";
		} else if (pageNum < 100) {
			if (pages > 100) outputFileStr += "0";
		} 
		
		outputFileStr += pageNum + JPG_EXTENSION;
		if (dir != null) {
			outputFileStr = dir + File.separator + outputFileStr;
		}
		
		return outputFileStr;
	}
	
}
