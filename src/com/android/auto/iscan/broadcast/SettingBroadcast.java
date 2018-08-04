package com.android.auto.iscan.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.android.HonewellManager;
import com.android.MotoSE47XXScanner;
import com.android.auto.iscan.ScannerServices;
import com.android.auto.iscan.activity.SettingActivity;
import com.android.auto.iscan.utility.ConstantUtil;
import com.android.auto.iscan.utility.ParamNum;
import com.android.auto.iscan.utility.ScanLog;
import com.android.auto.iscan.utility.Variable;

public class SettingBroadcast extends BroadcastReceiver {

	ScannerServices mListen = null;

	public SettingBroadcast(ScannerServices Listen) {
		mListen = Listen;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (mListen.key_log)
			ScanLog.getInstance(context).LOGD(
					 "  action:"
							+ action);
		if (action.equals(ConstantUtil.KEY_BARCODE_ENABLE_ACTION)) {
			boolean m_key_barcodescan = intent.getBooleanExtra(action, true);
			mListen.setPara(ParamNum.barcode, m_key_barcodescan);
		} else if (action.equals(ConstantUtil.KEY_BARCODE_STARTSCAN_ACTION)) {
			mListen.setPara(ParamNum.startscan, 0);
		} else if (action.equals(ConstantUtil.KEY_BARCODE_STOPSCAN_ACTION)) {
			mListen.setPara(ParamNum.stopscan, 0);
		} else if (action.equals(ConstantUtil.KEY_LOCK_SCAN_ACTION)) {
			mListen.setPara(ParamNum.lockkey, 0);
		} else if (action.equals(ConstantUtil.KEY_UNLOCK_SCAN_ACTION)) {
			mListen.setPara(ParamNum.unlockkey, 0);
		} else if (action.equals(ConstantUtil.KEY_BEEP_ACTION)) {
			boolean m_key_beep = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.beep, m_key_beep);
		} else if (action.equals(ConstantUtil.KEY_VIBRATE_ACTION)) {
			boolean m_key_vibrate = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.vibrate, m_key_vibrate);
		} else if (action.equals(ConstantUtil.KEY_LIGHT_ACTION)) {
			boolean m_keylight = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.light, m_keylight);
		} else if (action.equals(ConstantUtil.KEY_CHARSET_ACTION)) {
			int key_charset = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.charset, key_charset);
		} else if (action.equals(ConstantUtil.KEY_CONTINUCESCAN_ACTION)) {
			boolean m_key_continucescan = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.continucescan, m_key_continucescan);
		} else if (action.equals(ConstantUtil.KEY_TERMINATOR_ACTION)) {
			int key_terminator = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.terminator, key_terminator);
		} else if (action.equals(ConstantUtil.KEY_POWER_ACTION)) {
			boolean m_keypower = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.power, m_keypower);
		} else if (action.equals(ConstantUtil.KEY_PREFIX_ACTION)) {
			String key_prefix = intent.getStringExtra(action);
			mListen.setPara(ParamNum.prefix, key_prefix);
		} else if (action.equals(ConstantUtil.KEY_SUFFIX_ACTION)) {
			String key_prefix = intent.getStringExtra(action);
			mListen.setPara(ParamNum.subfix, key_prefix);
		} else if (action.equals(ConstantUtil.KEY_TRIMLEFT_ACTION)) {
			int key_tirmleft = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.tirmleft, key_tirmleft);
		} else if (action.equals(ConstantUtil.KEY_TRIMRIGHT_ACTION)) {
			int key_tirmright = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.tirmright, key_tirmright);
		} else if (action.equals(ConstantUtil.KEY_TIMEOUT_ACTION)) {
			int timeout = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.timeout, timeout);
		} else if (action.equals(ConstantUtil.KEY_FILTERCHARACTER_ACTION)) {
			String key_filterchar = intent.getStringExtra(action);
			mListen.setPara(ParamNum.filterCharacter, key_filterchar);
		} else if (action.equals(ConstantUtil.KEY_INTERVALTIME_ACTION)) {
			int intervaltime = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.intervaltime, intervaltime);
		} else if (action.equals(ConstantUtil.KEY_INTERVALOUTTIME_ACTION)) {
			int intervaltime = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.IntervalOutTime, intervaltime);
		} else if (action.equals(ConstantUtil.KEY_UPSETTING_ACTION)) {
			mListen.setPara(ParamNum.updatesetting, 0);
		} else if (action.equals(ConstantUtil.KEY_OUTPUT_ACTION)) {
			int m_key_broadcast = intent.getIntExtra(action, 0);
			mListen.setPara(ParamNum.broadcast, m_key_broadcast);
		} else if (action.equals(ConstantUtil.KEY_SHOWNOTICEICON_ACTION)) {
			boolean m_key_notice = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.notice, m_key_notice);
		} else if (action.equals(ConstantUtil.KEY_DELELCTED_ACTION)) {
			boolean m_key_deleted = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.deleted, m_key_deleted);
		} else if (action.equals(ConstantUtil.KEY_RESET_ACTION)) {
			boolean m_key_reset = intent.getBooleanExtra(action, false);
			 mListen.setPara(ParamNum.reset, m_key_reset);
		} else if (action.equals(ConstantUtil.SCANKEY_CONFIG_ACTION)) {
			int key_code = intent.getIntExtra("KEYCODE", 0);
			int ScanKey_vlaue = intent.getIntExtra("value", 0);
			if (key_code == KeyEvent.KEYCODE_F9) {
				mListen.key_f9 = ScanKey_vlaue;
				Variable.getInstance(mListen).SetMiddlekeyValue(ScanKey_vlaue);
			} else if (key_code == KeyEvent.KEYCODE_F10) {
				mListen.key_f10 = ScanKey_vlaue;
				Variable.getInstance(mListen).SetLeftkeyValue(ScanKey_vlaue);
			} else if (key_code == KeyEvent.KEYCODE_F11) {
				mListen.key_f11 = ScanKey_vlaue;
				Variable.getInstance(mListen).SetRightkeyValue(ScanKey_vlaue);
			}
		} else if (action.equals(ConstantUtil.KEY_FAILUREBEEP_ACTION)) {
			boolean m_key_fail_beep = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.failurebeep, m_key_fail_beep);
		} else if (action.equals(ConstantUtil.KEY_UPSETTING_ACTION)) {
			mListen.setPara(ParamNum.updatesetting, 0);
		} else if (action.equals(ConstantUtil.KEY_SHOWISCANUI)) {
			Intent i = new Intent(context, SettingActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else if (action.equals(ConstantUtil.KEY_FAILUREBROADCAST_ACTION)) {
			boolean ErrorBroadCast = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.failurebroadcast, ErrorBroadCast);
		} else if (action.equals(ConstantUtil.KEY_CHANGEPASSWORD_ACTION)) {
			String password = intent
					.getStringExtra(ConstantUtil.KEY_CHANGEPASSWORD_ACTION);
			Variable.getInstance(mListen).SetAdvancePassword(password);
		} else if (action.equals(ConstantUtil.KEY_ENABLEPASSWORD_ACTION)) {
			boolean Enablepassword = intent.getBooleanExtra(action, true);
			Variable.getInstance(mListen).SetPasswordEnable(Enablepassword);
		} else if (action.equals(ConstantUtil.KEY_SETTINGACTIVITY_ACTION)) {
			boolean m_key_setting = intent.getBooleanExtra(action, false);
			mListen.setPara(ParamNum.settingactivity, m_key_setting);
		} else if (action.equals(ConstantUtil.KEY_SPEECHINPUT_ACTION)) {
			String barcode = intent.getStringExtra("barcode");
			mListen.setPara(ParamNum.speechinput, barcode);
		} else if (action.equals(ConstantUtil.KEY_ILLUMINATION_ACTION)) {
			if (HonewellManager.m_Decoder != null) {
				int value = intent.getIntExtra("brightness", 0);
				mListen.setPara(ParamNum.brightness, value);
			}
		} else if (action.equals(ConstantUtil.KEY_ILLUMINATION_AIM_ACTION)) {
			if (HonewellManager.m_Decoder != null
					|| MotoSE47XXScanner.mBarCodeReader != null) {
				int value = intent.getIntExtra("lightsMode", 0);
				mListen.setPara(ParamNum.lightsmode, value);
			}
		}

	}
}
