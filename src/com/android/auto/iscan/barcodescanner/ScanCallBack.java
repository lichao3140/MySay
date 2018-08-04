package com.android.auto.iscan.barcodescanner;

public interface ScanCallBack {
	void onScanResults(byte[] data);
	
	void onScanResults(String data);
	int onFail(int code) ;
	

}
