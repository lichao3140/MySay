package com.android;

import android.content.Context;

import com.android.auto.iscan.barcodescanner.BaseScan;

public class BarcodeJNI extends BaseScan {

	public BarcodeJNI(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean doOpen() {
		// TODO Auto-generated method stub
		setScannerOn();
		return true;
	}
	@Override
	public boolean doClose() {

		setScannerOff();
		return true;
	}

	@Override
	public void doStart() {
		SetScannerStart();
	}

	@Override
	public void doStop() {

		SetScannerStop();
	}

	@Override
	public int setParams(int num, int value) {
		// TODO Auto-generated method stub
		return setScannerParameter(num, value);
	}

	@Override
	public int getParams(int num) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void ScanResults(byte[] data) {
		if (mListener != null) {
			mListener.onScanResults(data);
		}
	}

	public native static int GetPlatform();

	public native static void SetGpioState(int gpio);

	public native int setScannerParameter(int para, int value);

	private native void setScannerOn();

	private native void setScannerOff();

	private native void SetScannerStart();

	private native void SetScannerStop();

	static {
		System.loadLibrary("iscan-barcode");
	}

	@Override
	public byte[] GetLastImage() {
		// TODO Auto-generated method stub
		return null;
	}
}
