package com.android;

public class Test {

	public native void OpenSession();

	public native void CloseSession();

	public native void StartScanning();

	public native void StopScanning();
	
	
	
	static {
		System.loadLibrary("test");
	}

	
	
	
	
	
	
	
	
}
