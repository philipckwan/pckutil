package com.pck.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.pck.common.DownloadWorker;
import com.pck.common.WorkItem;

public class MultiWGet {
	
	
	public static final String configFile = "input2.txt";
	//public static final String dataFile = "data.txt";
	
	public static final String KEYWORD_FILE_URL = "fileUrl=";
	public static final String KEYWORD_DIR = "dir=";
	public static final String KEYWORD_NUM_THREAD = "numThread=";
	public static final String KEYWORD_NUM_PAGE = "numPage=";
	public static final String KEYWORD_SEGMENT = "segment=";
	public static final String KEYWORD_CODE = "code=";
	
	public static String fileUrl = null;
	public static String dir = null;
	public static int numThread = 0;
	public static int numPage = 0;
	public static String fileSegment = null;
	public static String code = null;
	
	public static String fileUrlHead = null;
	public static String fileUrlTail = null;
	
	private static List<Thread> threadList = new ArrayList<Thread>();
	
	public static final String OUTPUT_FILE_EXTENSION = ".jpg";

	public static void main(String[] args) {
		
		try {
			//int count = 1;
			BufferedReader br = null;
			// First, read the config file
			br = new BufferedReader(new FileReader(new File(configFile)));
			String configLine = null;
			while ((configLine = br.readLine()) != null) {
				if (configLine.indexOf(KEYWORD_FILE_URL) >= 0) fileUrl = configLine.substring(configLine.indexOf(KEYWORD_FILE_URL) + KEYWORD_FILE_URL.length()).trim();				
				if (configLine.indexOf(KEYWORD_DIR) >= 0) dir = configLine.substring(configLine.indexOf(KEYWORD_DIR) + KEYWORD_DIR.length()).trim();
				if (configLine.indexOf(KEYWORD_NUM_THREAD) >= 0) numThread = Integer.parseInt(configLine.substring(configLine.indexOf(KEYWORD_NUM_THREAD) + KEYWORD_NUM_THREAD.length()).trim());
				if (configLine.indexOf(KEYWORD_NUM_PAGE) >= 0) numPage = Integer.parseInt(configLine.substring(configLine.indexOf(KEYWORD_NUM_PAGE) + KEYWORD_NUM_PAGE.length()).trim());
				if (configLine.indexOf(KEYWORD_SEGMENT) >= 0) fileSegment = configLine.substring(configLine.indexOf(KEYWORD_SEGMENT) + KEYWORD_SEGMENT.length()).trim();				
				if (configLine.indexOf(KEYWORD_CODE) >= 0) code = configLine.substring(configLine.indexOf(KEYWORD_CODE) + KEYWORD_CODE.length()).trim();
			}
			br.close();

			if (fileUrl == null || dir == null) {
				System.out.println("ERROR - Please populate input.txt");
				System.exit(1);	
			}

			// Second, read the data file to get list of files to be downloaded
			//br = new BufferedReader(new FileReader(new File(dataFile)));

			// Get the first file to see where the file name alters
			//String fileSegment = configLine = br.readLine();
			fileUrlHead = fileUrl.substring(0, fileUrl.indexOf(fileSegment));
			System.out.println("fileUrlHead:" + fileUrlHead + ";");
			
			fileUrlTail = fileUrl.substring(fileUrl.indexOf(fileSegment) + fileSegment.length());
			System.out.println("fileUrlTail:" + fileUrlTail + ";");

			// Initializing download workers
			for (int i = 0; i < numThread; i++) {
				Thread workerThread = new Thread(new DownloadWorker());
				workerThread.start();
				threadList.add(workerThread);
			}
			
			// Add work to the workerQueue
			for (int i = 1; i < numPage + 1; i++) {
				fileUrl = fileUrlHead + getFileName_2Comic(i) + fileUrlTail;
				String filePath = dir + File.separator + getFileName(i);
				
				DownloadWorker.addWork(new WorkItem(new URL(fileUrl), filePath));
			} 
			//while ((fileSegment = br.readLine()) != null);
			 

		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
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
		
		System.out.println("MultiWGet.main: ends");		
	}
	
	public static String getFileName(int count) {
		String filename = null;
		if (count < 10) {
			filename = "00" + count + OUTPUT_FILE_EXTENSION;
		} else if (count < 100) {
			filename = "0" + count + OUTPUT_FILE_EXTENSION;
		}  else {
			filename = count + OUTPUT_FILE_EXTENSION;
		}
		return filename;
	}
	
	public static String getFileName_2Comic(int p){
		String filename = null;		
		int m = (((p-1)/10)%10)+(((p-1)%10)*3);
		
		if (p < 10) {
			filename = "00" + p + "_" + code.substring(m, m+3);
		} else if (p < 100) {
			filename = "0" + p + "_" + code.substring(m, m+3);
		} else {
			filename = p + "_" + code.substring(m, m+3);
		}
		return filename;
	}
}

class MultiWGetThread {
	
}
