package com.android.auto.iscan;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.android.BarcodeControll;

public class ScannnerManager {

	private boolean m_baropen = false;

	private WakeLock wakeLock;
	private BarcodeControll barcodecontroll = null;
	private Context mContext = null;

	private boolean ScanEnable = true;

	public ScannnerManager(Context context) {

		barcodecontroll = new BarcodeControll(context);

		mContext = context;
	}

	public void setBarcodeScanEnabled(boolean Enable) {
		ScanEnable = Enable;
		if (ScanEnable) {
			OpenBarcode();
		} else {
			CloseBarcode();
		}
	}

	public void setBarcodeTimeOut(int timeout) {
		barcodecontroll.setScannerTimeout(timeout);
	}

	public void startScanning() {
		if (ScanEnable) {
			barcodecontroll.Barcode_StartScan();
		}
	}

	public void stopScanning() {
		if (ScanEnable) {
			barcodecontroll.Barcode_StopScan();
		}
	}

	public void setParams(int num, int value) {
		if (ScanEnable) {
			barcodecontroll.setParams(num, value);
		}
	}

	public boolean IsSoftScannner() {

		return barcodecontroll.IsSoftScanner();

	}

	public void setContinuousScanEnabled(boolean Enable, int mIntervalTime) {
		if (ScanEnable) {
			barcodecontroll.setIntervalTime(mIntervalTime);
			barcodecontroll.setContinuousScanEnabled(Enable);
			if (Enable) {
				acquireWakeLock();
			} else {
				releaseWakeLock();
			}
		}
	}

	private void OpenBarcode() {
		if (!m_baropen) {
			m_baropen = true;
			barcodecontroll.Barcode_open();
		}
	}

	private void CloseBarcode() {
		if (m_baropen) {
			m_baropen = false;
			barcodecontroll.Barcode_Close();
		}
	}

	public void acquireWakeLock() {
		if (wakeLock == null) {

			PowerManager pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}

	}

	public void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}

	}

}
