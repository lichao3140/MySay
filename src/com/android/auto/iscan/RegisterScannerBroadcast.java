package com.android.auto.iscan;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.auto.iscan.broadcast.ScannerBroadcast;
import com.android.auto.iscan.broadcast.SettingBroadcast;
import com.android.auto.iscan.utility.ConstantUtil;

public class RegisterScannerBroadcast {

	
	private Context mContext;
	private ScannerBroadcast  scannerBroadcast=null;
	
	private SettingBroadcast settingBroadcast=null;
	IntentFilter scannerfilter=null;
	IntentFilter settingfilter=null;
	

	public RegisterScannerBroadcast(ScannerServices context) {
		this.mContext = context;
		scannerBroadcast = new ScannerBroadcast(context);
		settingBroadcast=  new SettingBroadcast(context);
		scannerfilter = new IntentFilter();
		settingfilter = new IntentFilter();
		RegisterScannerBroastReceiver(context);
		RegisterSettingBroastReceiver(context);
		
	}

	public void  RegisterScannerBroastReceiver(ScannerServices listen) {
		scannerfilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		scannerfilter.addAction(ConstantUtil.KEY_RECEIVER_ACTION);
		scannerfilter.addAction(Intent.ACTION_SCREEN_OFF);
		scannerfilter.addAction(Intent.ACTION_SCREEN_ON);
		scannerfilter.addAction(ConstantUtil.KEY_CAMERACLOSE_ACTION);
		scannerfilter.addAction(ConstantUtil.KEY_CAMERAOPENE_ACTION);
		scannerfilter.addAction(Intent.ACTION_SHUTDOWN);
		scannerfilter.addAction(ConstantUtil.ISCAN_EXIT_ACTION);
		scannerfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		scannerfilter.addAction("com.android.iScan.DS7000.startpreview");
		scannerfilter.addAction("com.android.iScan.DS7000.stoppreview");
		scannerfilter.addAction("android.intent.action.USER_SWITCHED");
		mContext.registerReceiver(scannerBroadcast, scannerfilter);
	}
	
	public void RegisterSettingBroastReceiver(ScannerServices listen) {
		settingfilter.addAction(ConstantUtil.KEY_BARCODE_ENABLE_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_BARCODE_STARTSCAN_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_BARCODE_STOPSCAN_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_LOCK_SCAN_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_UNLOCK_SCAN_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_BEEP_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_VIBRATE_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_LIGHT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_CHARSET_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_TERMINATOR_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_CONTINUCESCAN_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_POWER_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_PREFIX_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SUFFIX_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_TRIMLEFT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_TRIMRIGHT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_TIMEOUT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_FILTERCHARACTER_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_INTERVALTIME_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_INTERVALOUTTIME_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_OUTPUT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SHOWNOTICEICON_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SHOWICON_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_DELELCTED_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_RESET_ACTION);
		settingfilter.addAction(ConstantUtil.SCANKEY_CONFIG_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SHOWISCANUI);
		settingfilter.addAction(ConstantUtil.KEY_UPSETTING_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_FAILUREBEEP_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_FAILUREBROADCAST_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_CHANGEPASSWORD_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_ENABLESAVEIMAGE_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_ENABLEKEYSCANSTOP_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_ENABLESYMBOLOGIES_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SETTINGACTIVITY_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_SPEECHINPUT_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_ILLUMINATION_ACTION);
		settingfilter.addAction(ConstantUtil.KEY_ILLUMINATION_AIM_ACTION);
		mContext.registerReceiver(settingBroadcast, settingfilter);
	}

	public void UnregisterReceiver(){
		mContext.unregisterReceiver(scannerBroadcast);
		mContext.unregisterReceiver(settingBroadcast);
	}
	
}
