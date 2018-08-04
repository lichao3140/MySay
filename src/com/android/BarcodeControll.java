package com.android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.auto.iscan.barcodescanner.BarcodeScanner;
import com.android.auto.iscan.barcodescanner.OnScanListener;
import com.android.auto.iscan.barcodescanner.ScanCallBack;

public class BarcodeControll implements ScanCallBack {

	public boolean bScanning = false;

	OnScanListener mScan = null;
	public static Handler mHandler = null;

	public static Handler mScanHandler = null;

	public Context mcontext = null;

	private Boolean mcontinuousScanEnabled = false;
	private int ScannerTimeout = 10 * 1000;

	private static final int START_SCAN_MSG = 100;
	private static final int RESULT_SCAN_MSG = 300;
	private static final int TIMEOUT_MSG = 500;

	static public final int BARCODE_READ = 0xad;
	static public final int BARCODE_DECODE_FAILURE = 0xaf;

	private BarcodeScanner mScanner = null;

	int mIntervalTime = 1000;
	private static boolean m_scanSuccess = false;

	boolean SE47XX = false;

	private long decodeStartTime = 0;
	private long decodeEndTime = 0;

	public BarcodeControll(Context context) {
		mScanner = new BarcodeScanner(context);
		mScan = mScanner.getListener();
		mScan.setListener(this);
		mScanHandler = new ScanHandler();
		mcontext = context;
	}

	public class ScanHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIMEOUT_MSG:
				Barcode_StopScan();
				break;
			case START_SCAN_MSG:
				Barcode_StartScan();
				break;
			case RESULT_SCAN_MSG:
				mScanHandler.removeMessages(START_SCAN_MSG);
				Barcode_StopScan();
				break;
			default:
				break;
			}
		}
	};

	public boolean Barcode_open() {

		return mScan.doOpen();

	}

	public  void Barcode_Close() {

		if (mcontinuousScanEnabled) {
			setContinuousScanEnabled(false);
			mcontinuousScanEnabled = false;
		}
		Barcode_StopScan();
		mScan.doClose();
	}

	public boolean IsSoftScanner() {
		return mScanner.GetSoftScannerFlag();
	}

	public void Barcode_StartScan() {
		if (!bScanning) {
			bScanning = true;
			mScan.doStart();
			decodeStartTime = System.currentTimeMillis();
			m_scanSuccess = false;
			mScanHandler.removeMessages(TIMEOUT_MSG);
			mScanHandler.sendEmptyMessageDelayed(TIMEOUT_MSG, ScannerTimeout);
		}
	}

	public void Barcode_StopScan() {
		if (bScanning) {
			bScanning = false;
			mScan.doStop();
			mScanHandler.removeMessages(START_SCAN_MSG);
			mScanHandler.removeMessages(TIMEOUT_MSG);
			mScanHandler.removeMessages(RESULT_SCAN_MSG);
			if (mcontinuousScanEnabled) {
				if (m_scanSuccess)
					mScanHandler.sendEmptyMessageDelayed(START_SCAN_MSG,
							mIntervalTime);
				else
					mScanHandler.sendEmptyMessageDelayed(START_SCAN_MSG, 50);
			}
			if (!m_scanSuccess) {
				mHandler.sendEmptyMessage(BARCODE_DECODE_FAILURE);
			}
		}
	}

	public void setContinuousScanEnabled(boolean enable) {
		this.mcontinuousScanEnabled = enable;
		if (mcontinuousScanEnabled)
			mScanHandler.sendEmptyMessage(START_SCAN_MSG);
		else {
			mScanHandler.removeMessages(START_SCAN_MSG);
			mScanHandler.removeMessages(TIMEOUT_MSG);
			mScanHandler.removeMessages(RESULT_SCAN_MSG);
			Barcode_StopScan();
		}
	}

	public void sendMessage(int id) {
		if (mScanHandler != null) {
			Message message = Message.obtain(mScanHandler, id);
			mScanHandler.sendMessage(message);
		}
	}

	public int setParams(int num, int value) {
		mScan.setParams(num, value);
		return 0;
	}

	public boolean isContinuousScanEnabled() {
		return this.mcontinuousScanEnabled;
	}

	public byte[] GetLastImage() {
		return mScan.GetLastImage();
	}

	public void setIntervalTime(int intervalTime) {
		mIntervalTime = intervalTime;
	}

	public void setScannerTimeout(int timeout) {

		this.ScannerTimeout = timeout * 1000;
	}

	@Override
	public void onScanResults(byte[] data) {
		// TODO Auto-generated method stub
		if (mHandler != null) {
			m_scanSuccess = true;
			decodeEndTime = System.currentTimeMillis();
			sendMessage(RESULT_SCAN_MSG);
			Message msg = Message.obtain();
			msg.what = BARCODE_READ;
			msg.obj = data;
			msg.arg1 = (int) (decodeEndTime - decodeStartTime);
			// ScanLog.LOGD("decodeEndTime=" + msg.arg1);
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public int onFail(int code) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static String toHexString(byte[] byteArray, int size) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException(
					"this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder(2 * size);
		for (int i = 0; i < size; i++) {
			if ((byteArray[i] & 0xff) < 0x10)//
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
			if (i != (byteArray.length - 1))
				hexString.append(" ");
		}
		return hexString.toString().toUpperCase();
	}

	@Override
	public void onScanResults(String data) {
		// TODO Auto-generated method stub
		if (mHandler != null) {
			m_scanSuccess = true;
			decodeEndTime = System.currentTimeMillis();
			sendMessage(RESULT_SCAN_MSG);
			Message msg = Message.obtain();
			msg.what = BARCODE_READ;
			msg.obj = data;
			;
			msg.arg1 = (int) (decodeEndTime - decodeStartTime);
			mHandler.sendMessage(msg);
		}
	}

}
