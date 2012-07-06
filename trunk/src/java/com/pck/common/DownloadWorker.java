package com.pck.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DownloadWorker implements Runnable {

	private static BlockingQueue<WorkItem> aQueue = new LinkedBlockingQueue<WorkItem>();
	private static int idList = 1;
	private int id;
	private int numDownloaded = 0;

	@Override
	public void run() {
		int contentLength;
		byte[] data;
		int bytesRead;
		int offset;
		id = idList++;
		System.out.println("DownloadWorker.run: Thread[" + id + "] starts");
		
		try {
			while (true) {
				WorkItem work = takeWork();
				System.out.println("DownloadWorker.run: START Thread[" + id + "] downloads [" + work.getURL() + "] to path [" + work.getFilePath() + "]");
				
				URL url = work.getURL();
				String filePath = work.getFilePath();

				URLConnection urlConn = url.openConnection();

				contentLength = urlConn.getContentLength();
				//System.out.println("contentType: " + contentType + "; contentLength: " + contentLength);
				
				BufferedInputStream bis = new BufferedInputStream(urlConn.getInputStream());
				data = new byte[contentLength];
				bytesRead = 0;
				offset = 0;
				while (offset < contentLength) {
					bytesRead = bis.read(data, offset, data.length - offset);
					if (bytesRead == -1) break;
					offset += bytesRead;
				}
				bis.close();
				
				FileOutputStream fos = new FileOutputStream(new File(filePath));
				fos.write(data);
				fos.flush();
				fos.close();
				
				System.out.println("DownloadWorker.run: END Thread[" + id + "] downloads [" + work.getURL() + "] to path [" + work.getFilePath() + "]");				
				numDownloaded++;
			}
		} catch (InterruptedException e) {
			System.out.println("DownloadWorker.run: Thread[" + id + "] interrupted");
		} catch (IOException e) {
			System.out.println("DownloadWorker.run: ERROR - Thread[" + id + "]: IOException:");
			e.printStackTrace();
		}
		System.out.println("DownloadWorker.run: Thread[" + id + "] ends; total downloads:" + numDownloaded + ";");		
	}
		
	public static boolean addWork(WorkItem work) {
		boolean success = true;
		try {
			aQueue.put(work);
		} catch (InterruptedException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	private WorkItem takeWork() throws InterruptedException {
		WorkItem work = null;
		work = aQueue.take();
		return work;
	}
	
	public static boolean isQueueEmpty() {
		return aQueue.isEmpty();
	}
	
}
