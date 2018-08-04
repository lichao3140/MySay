package com.android.auto.iscan.barcodescanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

import com.android.BarcodeJNI;
import com.android.HonewellManager;
import com.android.MotoSE4500Device;
import com.android.MotoSE47XXScanner;

public class BarcodeScanner {

	int softType = 0;
	private Context m_context;

	private OnScanListener listener;

	int platform_type = 0;

	public static int id = 0;
	boolean SoftScannerFlage = false;

	public OnScanListener getListener() {
		return listener;
	}

	public BarcodeScanner(Context context) {
		m_context = context;
		softType = GetSoftScannerType();
		id = 0;
		platform_type = BarcodeJNI.GetPlatform();
		listener = Listener();
	}

	public OnScanListener Listener() {
		if (platform_type == 3 || platform_type == 1 || platform_type == 4
				|| softType > 10) {
			SoftScannerFlage = false;
			id = 0x0;
			return new BarcodeJNI(m_context);
		} else {
			if (softType == 3) {
				SoftScannerFlage = true;
				id = 0x6603;
				return new HonewellManager(m_context);
			} else if (softType == 4 || softType == 5) {
				SoftScannerFlage = true;
				id = 0x6000;
				return new MotoSE47XXScanner(m_context);
			} else if (softType == 1 || softType == 2) {
				SoftScannerFlage = true;
				id = 0x4710;
				return new MotoSE4500Device(m_context);
			} else {
				SoftScannerFlage = false;
				id = 0;
			}
		}
		return new BarcodeJNI(m_context);
	}

	public boolean GetSoftScannerFlag() {

		return SoftScannerFlage;
	}

	public static int GetSoftScannerType() {
		int index = 0;
		try {
			String f = "/proc/driver/camtype";
			FileInputStream in = new FileInputStream(f);
			InputStreamReader inReader = new InputStreamReader(in);
			index = inReader.read() - 48;
			inReader.close();
			Log.d("012", "index::::" + index);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;
	}

}
