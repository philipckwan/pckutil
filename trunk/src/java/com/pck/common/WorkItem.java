package com.pck.common;

import java.net.URL;

public class WorkItem {

	private URL url;
	private String filePath;
	
	public WorkItem(URL url, String filePath) {
		this.url = url;
		this.filePath = filePath;
	}
	
	public URL getURL() { return url; }
	
	public String getFilePath() { return filePath; }
	
}
