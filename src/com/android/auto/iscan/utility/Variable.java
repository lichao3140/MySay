package com.android.auto.iscan.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Variable {

	private static Variable instance;
	public static Context gContext = null;

	private static SharedPreferences sharedPref = null;
	private static SharedPreferences.Editor sharedPrefEditor = null;

	public static Variable getInstance(Context context) {
		if (instance == null) {
			instance = new Variable(context);
			sharedPref = gContext.getSharedPreferences(
					"com.android.auto.iscan_preferences", Context.MODE_PRIVATE);
			sharedPrefEditor = sharedPref.edit();
		}
		return instance;
	}

	private Variable(Context context) {
		gContext = context;
	}

	public void setCookieVariables(String name, Object obj) {
		if ((obj instanceof Integer))
			sharedPrefEditor.putInt(name, ((Integer) obj).intValue());
		else if ((obj instanceof String))
			sharedPrefEditor.putString(name, (String) obj);
		else if ((obj instanceof Boolean))
			sharedPrefEditor.putBoolean(name, ((Boolean) obj).booleanValue());
		sharedPrefEditor.commit();
	}

	private boolean getBooleanVariables(String name, Object obj) {
		return sharedPref.getBoolean(name, ((Boolean) obj).booleanValue());

	}

	private int getIntegerVariables(String name, int value) {

		return sharedPref.getInt(name, value);

	}

	private String getStringVariables(String name, String value) {
		return sharedPref.getString(name, value);

	}

	public boolean GetNoticeIconState() {
		return getBooleanVariables(ConstantUtil.key_notice, true);

	}
	
	
	public int GetCharSet(String value) {
		return Integer.parseInt(getStringVariables(ConstantUtil.key_charset,
				value));

	}

	public void SetCharSet(String value) {

		setCookieVariables(ConstantUtil.key_charset, value);
	}

	public void SetNoticeIconState(boolean state) {

		setCookieVariables(ConstantUtil.key_notice, state);
	}

	public boolean GetAppIconState() {
		return getBooleanVariables(ConstantUtil.key_icon, true);

	}

	public void SetAppIconState(boolean state) {

		setCookieVariables(ConstantUtil.key_icon, state);
	}

	public boolean GetAotuStartState() {
		return getBooleanVariables(ConstantUtil.key_auto, true);

	}

	public void SetAotuStartState(boolean state) {

		setCookieVariables(ConstantUtil.key_auto, state);
	}

	public boolean GetLightState() {
		return getBooleanVariables(ConstantUtil.key_light, true);

	}

	public void SetLightState(boolean state) {

		setCookieVariables(ConstantUtil.key_light, state);
	}

	public boolean GetBarcodeState() {
		return getBooleanVariables(ConstantUtil.key_barcode, true);

	}

	public void SetBarcodeState(boolean state) {
		setCookieVariables(ConstantUtil.key_barcode, state);
	}

	public boolean GetBeepState() {

		return getBooleanVariables(ConstantUtil.key_beep, true);
	}

	public void SetBeepState(boolean state) {

		setCookieVariables(ConstantUtil.key_beep, state);
	}

	public boolean GetBeepFailueState() {

		return getBooleanVariables(ConstantUtil.key_beep_bad, false);
	}

	public void SetBeepFailueState(boolean state) {

		setCookieVariables(ConstantUtil.key_beep_bad, state);
	}

	public boolean GetVibrateState() {

		return getBooleanVariables(ConstantUtil.key_vibrate, false);
	}

	public void SetVibrateState(boolean state) {

		setCookieVariables(ConstantUtil.key_vibrate, state);
	}

	public int GetBroadcastState(String value) {

		return Integer.parseInt(getStringVariables(ConstantUtil.key_broadcast,
				value));
	}


	public void SetDeletectState(boolean state) {

		setCookieVariables(ConstantUtil.key_deletect, state);
	}

	public boolean GetDeletectState() {
		return getBooleanVariables(ConstantUtil.key_deletect, false);
	}


	public void SetBroadcastState(String  state) {

		setCookieVariables(ConstantUtil.key_broadcast, state);
	}

	public boolean GetPowerState() {

		return getBooleanVariables(ConstantUtil.key_power, true);
	}

	public void SetPowerState(boolean state) {

		setCookieVariables(ConstantUtil.key_power, state);
	}

	public boolean GetContinusScanState() {

		return getBooleanVariables(ConstantUtil.key_continus_scan, false);
	}

	public void SetContinusScan(boolean state) {

		setCookieVariables(ConstantUtil.key_continus_scan, state);
	}

	public String GetIntervalTime() {

		return getStringVariables(ConstantUtil.key_interval_time, "1000");
	}

	public void SetIntervalTime(String value) {

		setCookieVariables(ConstantUtil.key_interval_time, value);
	}

	public String GetIntervalOutTime() {

		return getStringVariables(ConstantUtil.key_interval_outtime, "30");
	}

	
	public void SetIntervalOutTime(String value) {

		setCookieVariables(ConstantUtil.key_interval_outtime, value);
	}
	
	
	public String GetFilterCharacter() {

		return getStringVariables(ConstantUtil.key_filter_character, "");
	}

	public void SetFilterCharacter(String value) {

		setCookieVariables(ConstantUtil.key_filter_character, value);
	}

	
	public void SetKeyScanEnable(boolean value) {

		setCookieVariables(ConstantUtil.key_enable_scan, value);

	}
	
	public void SettBroadCastFailure(boolean value) {

		setCookieVariables(ConstantUtil.KEY_FAILUREBROADCAST_ACTION, value);

	}
	
	public boolean GetBroadCastFailure() {
		return getBooleanVariables(ConstantUtil.KEY_FAILUREBROADCAST_ACTION, false);
	}
	
	
	public void SetKeyScanStop(boolean value) {

		setCookieVariables(ConstantUtil.key_scan_stop, value);

	}
	
	public boolean GetKeyScanStop() {
		return getBooleanVariables(ConstantUtil.key_scan_stop, false);
	}
	

	public void SetOutputLog(boolean value) {

		setCookieVariables(ConstantUtil.key_log, value);

	}
	
	public boolean getOutputLog() {
		return getBooleanVariables(ConstantUtil.key_log, false);
	}
	
	
	public boolean GetPasswordEnable() {

		return getBooleanVariables(ConstantUtil.key_enable_password, false);
	}

	public void SetPasswordEnable(boolean value) {
		setCookieVariables(ConstantUtil.key_enable_password, value);
	}
	
	public boolean GetKeyScanEnable() {
		return getBooleanVariables(ConstantUtil.key_enable_scan, true);
	}


	public int GetUpLeftkeyValue() {

		return this.getIntegerVariables(ConstantUtil.key_up_l_config, 2);
	}

	public void SetUpLeftkeyValue(int value) {

		setCookieVariables(ConstantUtil.key_up_l_config, value);
	}

	public int GetUpRightkeyValue() {

		return this.getIntegerVariables(ConstantUtil.key_up_r_config, 2);
	}

	public void SetUpRightkeyValue(int value) {

		setCookieVariables(ConstantUtil.key_up_r_config, value);
	}

	public int GetMiddlekeyValue() {

		return this.getIntegerVariables(ConstantUtil.key_m_config, 0);
	}

	public void SetMiddlekeyValue(int value) {

		setCookieVariables(ConstantUtil.key_m_config, value);
	}

	public int GetLeftkeyValue() {

		return this.getIntegerVariables(ConstantUtil.key_l_config, 0);
	}

	public void SetLeftkeyValue(int value) {

		setCookieVariables(ConstantUtil.key_l_config, value);
	}

	public int GetRightkeyValue() {

		return this.getIntegerVariables(ConstantUtil.key_r_config, 0);
	}

	public void SetRightkeyValue(int value) {
		setCookieVariables(ConstantUtil.key_r_config, value);
	}

	public int GetTerminator(String value) {

		return Integer.parseInt(getStringVariables(ConstantUtil.key_terminator,
				value));
	}

	public void SetTerminator(String value) {

		setCookieVariables(ConstantUtil.key_terminator, value);
	}

	

	public String GetOutTime() {
		return getStringVariables(ConstantUtil.key_outtime, "10");
	}

	public void SetOutTime(int value) {

		setCookieVariables(ConstantUtil.key_outtime, Integer.toString(value));
	}

	public void setPrefix(String value) {
		setCookieVariables(ConstantUtil.key_prefix, value);

	}

	public void setSuffix(String value) {
		setCookieVariables(ConstantUtil.key_suffix, value);

	}

	public void setTrimleft(String value) {
		setCookieVariables(ConstantUtil.key_trimleft, value);
	}

	public void setTrimright(String value) {
		setCookieVariables(ConstantUtil.key_trimright, value);
	}

	public String GetPrefix() {
		return getStringVariables(ConstantUtil.key_prefix, "");
	}

	public String GetSuffix() {

		return getStringVariables(ConstantUtil.key_suffix, "");
	}
	public String GetAdvancePassword() {
		return getStringVariables(ConstantUtil.key_advance_password, "654321");
	}
	
	public void SetAdvancePassword(String value) {
		setCookieVariables(ConstantUtil.key_advance_password, value);
	}
	
	public String GetTrimleft() {
		return getStringVariables(ConstantUtil.key_trimleft, "");
	}

	public String GetTrimright() {

		return getStringVariables(ConstantUtil.key_trimright, "");
	}
	
	public void SetDecodeCenter(boolean value) {
		setCookieVariables("pref_key_center", value);
	}
	
	public boolean GetDecodeCenter() {

		return getBooleanVariables("pref_key_center", true);
	}

	public String GetBrghtnessValue() {
		return getStringVariables("exposure_fixed_exposure", "80");
	}

	public void SetBrghtnessValue(String state) {

		setCookieVariables("exposure_fixed_exposure", state);
	}
	
	public void SetLightsMode(String state) {

		setCookieVariables("lightsConfig", state);
	}
	
	
	public void ResetValue() {
		try {
			if (sharedPrefEditor != null) {
				sharedPrefEditor.clear();
				sharedPrefEditor.commit();
			}
		} catch (Exception e) {

		}
	}

}
