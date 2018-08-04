package com.android.auto.iscan.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.HonewellManager;
import com.android.auto.iscan.R;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderException;
 
public class ConfigurationSettingsActivity extends PreferenceActivity {
	private static final String TAG = "BarcodeConfigSettingsActivity";
	private Decoder m_decDecoder = null;
 
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTheme(R.style.AppBaTheme);

        addPreferencesFromResource(R.xml.configuration_settings);
       
    }  
    
    /* Flawless work around for submenu issue with 2.3 */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
    	super.onPreferenceTreeClick(preferenceScreen, preference);
    	
    	if (preference!=null)
	    	if (preference instanceof PreferenceScreen)
	        	if (((PreferenceScreen)preference).getDialog()!=null)
	        		((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
    	return false;
    }
    
    void HandleDecoderException(DecoderException e)
    {
    	Log.d(TAG, "Config Error: " + e.getMessage()); 
    }
 
    
    public void onDestroy() {
    	HonewellManager.ScanningSettings();
		super.onDestroy();
	}
    
    
    
}