package com.android.auto.iscan.internet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.android.auto.iscan.utility.ScanLog;

/**
 * Get Data by web, support GZIP, not support encryption and decryption
 * 
 * @author chaizsz
 * 
 */
public class GetWebData {


	private static HashMap<String, String> mHashMap;

	

	public static String GetAppKey() {

		InputStream inStream = ParseXmlService.class.getClassLoader()
				.getResourceAsStream("version.xml");
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != mHashMap) {
			return mHashMap.get("appkey");
		}
		return null;
	}

	
	public static String GetServerUrl() {
		InputStream inStream = ParseXmlService.class.getClassLoader()
				.getResourceAsStream("version.xml");
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != mHashMap) {
			return mHashMap.get("server-url");
		}
		return null;
	}

	

	
	
	public static String getInfobyget(final URL url) throws Exception {

		String result = "";
		
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		
		httpURLConnection.setDoInput(true);
		httpURLConnection.setConnectTimeout(60 * 1000);
		httpURLConnection.setReadTimeout(60 * 1000);
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.connect();
		int responsecode = httpURLConnection.getResponseCode();
		
		if (200 == responsecode) {
			InputStream inputStream = httpURLConnection.getInputStream();
			result = readData(inputStream, "utf-8");
			
		} else {

		}
		httpURLConnection.disconnect();
		
		return result;
	}

	/**
	 * get string from stream
	 * 
	 * @param inStream
	 * @param charsetName
	 * @return
	 * @throws Exception
	 */
	private static String readData(InputStream inStream, String charsetName)
			throws Exception {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		byte[] data = outputStream.toByteArray();

		outputStream.close();
		inStream.close();
		return new String(data, charsetName);
	}

}
