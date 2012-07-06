package com.pck.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {

	private static final String DEFAULT_PROPERTY_FILE = "properties.txt";
	private static File propertyFile = null;
	private static Properties runningProperties = null;
	private static FileOutputStream propertyFileOutput = null;	
	private static FileInputStream propertyFileInput = null;
	
	private static void init() {
		initWithFile(DEFAULT_PROPERTY_FILE);
	}
	
	public static void initWithFile(String filepath) {
		runningProperties = new Properties();
		
		propertyFile = new File(filepath);									
	}
	
	public static String getProperty(String key) {
		if (runningProperties == null) {
			init();
		}
		
		try {
			propertyFileInput = new FileInputStream(propertyFile);
			runningProperties.load(propertyFileInput);
		} catch (FileNotFoundException e2) {
			System.out.println("PropertiesManager.initWithFile: ERROR - FileNotFoundException: file:" + propertyFile.getAbsolutePath() + ";");
			e2.printStackTrace();
		} catch (IOException e) {
			System.out.println("PropertiesManager.initWithFile: ERROR - IOException 1;");
			e.printStackTrace();
		} finally {
			try {
				if (propertyFileInput != null) {
					propertyFileInput.close();
				}
			} catch (IOException e) {
				System.out.println("PropertiesManager.initWithFile: ERROR - IOException 2;");
				e.printStackTrace();
			}
		}
		return runningProperties.getProperty(key);
	}
	
	public static int getPropertyInt(String key, int defaultProperty) {
		int value = -1;
		String valueStr = null;
		try {
			valueStr = runningProperties.getProperty(key, Integer.toString(defaultProperty));
			value = Integer.parseInt(valueStr);
		} catch (NumberFormatException ex) {
			System.out.println("PropertiesManager.getPropertyInt: ERROR - NumberFormatException, key:" + key + "; returning -1;");
			ex.printStackTrace();
		}
		return value;
	}
	
	public static int getPropertyInt(String key) {
		return getPropertyInt(key, -1);
	}
	
	public static boolean setProperty(String key, String newValue) {
		boolean success = false;
		
		String originalValue = getProperty(key);
		runningProperties.setProperty(key, newValue);
		
		try {
			propertyFileOutput = new FileOutputStream(propertyFile);			
			runningProperties.store(propertyFileOutput, null);
			//runningProperties.storeToXML(propertyFileOutput, "v0.3");
		} catch (FileNotFoundException e) {
			System.out.println("PropertiesManager.setProperty: ERROR - FileNotFoundException;");
			e.printStackTrace();
		} catch (IOException e2) {
			System.out.println("PropertiesManager.setProperty: ERROR - IOException 1;");
			e2.printStackTrace();
		} finally {			
			try {
				if (propertyFileOutput != null) {
					propertyFileOutput.close();
				}
			} catch (IOException e) {
				System.out.println("PropertiesManager.setProperty: ERROR - IOException 2;");
				e.printStackTrace();
			}			
		}
		
		System.out.println("PropertiesManager.setProperty: Setting property [" + key + "] with value [" + newValue + "], old value [" + originalValue + "];");
		
		return success;
	}
	
	public static String getDebugString() {
		System.out.println("PropertiesManager.getDebugString: START");
		if (runningProperties == null) {
			init();
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append("PropertiesManager properties:<<<");
		
		for (String propertyName : runningProperties.stringPropertyNames()) {
			sb.append("[" + propertyName + ":" + runningProperties.getProperty(propertyName) + "];");
		}
		
		sb.append(">>>");
		
		System.out.println("PropertiesManager.getDebugString: END");
		
		return sb.toString();
	}
	
	
	
	public static void main(String[] args) {
		System.out.println("PropertiesManager.main2: START");	
		
		final String KEY_ID = "id";
		final String KEY_USERNAME = "username";
		final String KEY_PASSWORD = "password";
		final String KEY_COUNT = "count";
		
		String id = null;
		String username = null;
		String password = null;
		int count = -1;
		
		//initWithFile("properties2.txt");
		System.out.println("--DEBUG--:" + getDebugString() + ";");
		
		id = getProperty(KEY_ID);
		username = getProperty(KEY_USERNAME);
		password = getProperty(KEY_PASSWORD);
		count = getPropertyInt(KEY_COUNT);
		
		System.out.println("_1_id:" + id + ";username:" + username + ";password:" + password + ";count:" + count + ";");
		
		setProperty(KEY_ID, "superman");
		setProperty(KEY_COUNT, Integer.toString(count + 2));		
		
		id = getProperty(KEY_ID);
		username = getProperty(KEY_USERNAME);
		password = getProperty(KEY_PASSWORD);
		count = getPropertyInt(KEY_COUNT);
		
		System.out.println("_2_id:" + id + ";username:" + username + ";password:" + password + ";count:" + count + ";");				
		
		System.out.println("PropertiesManager.main2: END");
	}

}
