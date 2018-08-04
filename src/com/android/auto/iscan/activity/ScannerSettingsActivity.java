package com.android.auto.iscan.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.MotoSE47XXScanner;
import com.android.auto.iscan.R;
import com.android.auto.iscan.barcodescanner.BarcodeScanner;
import com.hsm.barcode.DecoderException;

public class ScannerSettingsActivity extends PreferenceActivity {
	private static final String TAG = "BarcodeConfigSettingsActivity";
	PreferenceScreen root;
private ListPreference  m_light_intensity;
	
	private ListPreference  m_light_config; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(R.style.AppBaTheme);
		addPreferencesFromResource(R.xml.scanner_configuration_settings);
		root = getPreferenceScreen();
		m_light_intensity = ((ListPreference) findPreference("lightsConfig"));
		m_light_config = ((ListPreference) findPreference("lightsintensityConfig"));
		
		if (BarcodeScanner.GetSoftScannerType() == 4) {
			 ((PreferenceGroup)findPreference("scanning_setting")).removePreference(m_light_intensity);
			 
			 ((PreferenceGroup)findPreference("scanning_setting")).removePreference(m_light_config);
		}
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);

		if (preference != null)
			if (preference instanceof PreferenceScreen)
				if (((PreferenceScreen) preference).getDialog() != null)
					((PreferenceScreen) preference)
							.getDialog()
							.getWindow()
							.getDecorView()
							.setBackgroundDrawable(
									this.getWindow().getDecorView()
											.getBackground().getConstantState()
											.newDrawable());
		return false;
	}

	void HandleDecoderException(DecoderException e) {
		Log.d(TAG, "Config Error: " + e.getMessage());
	}

	public void onDestroy() {
		MotoSE47XXScanner.SetParameter();
		super.onDestroy();
	}

	
	public boolean onPreferenceChange(Preference key, Object arg1) {
		// TODO Auto-generated method stub
		Log.d("012", "onPreferenceChange::" + key.getKey());
		
		return  true;
	}
}