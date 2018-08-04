package com.android.auto.iscan.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.android.auto.iscan.ScannerServices;
import com.android.auto.iscan.utility.ConstantUtil;
import com.android.auto.iscan.utility.ParamNum;

public class ScannerBroadcast extends BroadcastReceiver {

	ScannerServices mListen = null;

	boolean Lkeydown = false, Rkeydown = false, Mkeydown = false;

	public ScannerBroadcast(ScannerServices context) {
		mListen = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// Log.e("013","action::"+action);
		if (action.equals(ConstantUtil.KEY_RECEIVER_ACTION)) {
			if (!mListen.m_key_lock || mListen.m_key_continuscan)
				return;
			String key_action = intent.getStringExtra("action");
			String key_code = intent.getStringExtra("code");
			if (key_code == null) {
				key_code = Integer.toString(intent.getIntExtra("code", 0));
			}
			if (key_code.compareTo("139") == 0
					|| key_code.compareTo("KEYCODE_F9") == 0
					|| key_code.compareTo("KEYCODE_F8") == 0
					|| key_code.compareTo("138") == 0
					|| key_code.compareTo("188") == 0) {
				mListen.Handle_YellowKey(key_action, mListen.key_f9);

				if (key_action.equals("down")) {
					Mkeydown = true;
					if (Rkeydown && Lkeydown) {
						mListen.setPara(ParamNum.reset, 0);
					} else if (Rkeydown || Lkeydown) {
						mListen.setPara(ParamNum.reset_barcode, 0);
					}

				} else if (key_action.equals("up")) {
					Mkeydown = false;
				}
			}
			if (key_code.compareTo("140") == 0
					|| key_code.compareTo("KEYCODE_F10") == 0
					|| key_code.compareTo("189") == 0) {
				mListen.Handle_YellowKey(key_action, mListen.key_f10);
				if (key_action.equals("down")) {
					Lkeydown = true;
					if (Rkeydown && Mkeydown) {
						mListen.setPara(ParamNum.reset, 0);
					} else if (Rkeydown || Mkeydown) {
						mListen.setPara(ParamNum.reset_barcode, 0);
					}

				} else if (key_action.equals("up")) {
					Lkeydown = false;
				}
			}
			if (key_code.compareTo("141") == 0
					|| key_code.compareTo("KEYCODE_F11") == 0
					|| key_code.compareTo("190") == 0) {
				mListen.Handle_YellowKey(key_action, mListen.key_f11);
				if (key_action.equals("down")) {
					Rkeydown = true;
					if (Lkeydown && Mkeydown) {
						mListen.setPara(ParamNum.reset, 0);
					} else if (Lkeydown || Mkeydown) {
						mListen.setPara(ParamNum.reset_barcode, 0);
					}

				} else if (key_action.equals("up")) {
					Rkeydown = false;
				}
			}
		} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
			mListen.setPara(ParamNum.screenon, 0);
		} else if (action.equals("com.android.iScan.d")) {
			mListen.scannerManager.startScanning();
		} else if (action.equals("com.android.iScan.DS7000test")) {
			mListen.scannerManager.startScanning();
		}

		else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			mListen.setPara(ParamNum.screenoff, 0);
		} else if (action.equals(ConstantUtil.KEY_CAMERAOPENE_ACTION)) {
			mListen.setPara(ParamNum.cameraon, 0);
		} else if (action.equals(ConstantUtil.KEY_CAMERACLOSE_ACTION)) {
			mListen.setPara(ParamNum.cameraoff, 0);
		} else if (action.equals(Intent.ACTION_SHUTDOWN)) {
			mListen.setPara(ParamNum.poweroff, 0);
		} else if (action.equals(ConstantUtil.ISCAN_EXIT_ACTION)) {
			mListen.setPara(ParamNum.exit, 0);
		} else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
			int status = intent.getIntExtra("status",
					BatteryManager.BATTERY_STATUS_UNKNOWN);
			Log.e("013", "status::" + status);

			if (status == BatteryManager.BATTERY_STATUS_CHARGING
					|| status == BatteryManager.BATTERY_STATUS_FULL) {
				mListen.setPara(ParamNum.charge, true);
			} else {
				mListen.setPara(ParamNum.charge, false);
			}
		}
	}

}
