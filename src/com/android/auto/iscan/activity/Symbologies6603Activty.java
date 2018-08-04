package com.android.auto.iscan.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.HonewellManager;
import com.android.auto.iscan.R;
import com.android.auto.iscan.barcodescanner.BarcodeScanner;
import com.android.auto.iscan.utility.Variable;
import com.zebra.adc.decoder.BarCodeReader1;

public class Symbologies6603Activty extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.n6603_settings);

		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.title);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.iscan);
		actionBar.show();

		TextView tvTitle = (TextView) findViewById(R.id.Titletext);
		tvTitle.setText(this.getString(R.string.iscan_enable_code));
		Button btnBack = (Button) findViewById(R.id.TitleBackBtn);
		btnBack.setText(getString(R.string.iscan_back));
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();

			}
		});
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference != null) {
			String prefere = preference.getKey();

			if (prefere.equals("key_special_setting")) {

				showPassswordDialog(Symbologies6603Activty.this
						.getString(R.string.iscan_enter_password));
			} else {
				HonewellManager.setSymbologySettings();

			}
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void onDestroy() {
		SendBraodcast();
		super.onDestroy();
	}

	public void SendBraodcast() {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.updatesetting");
		sendBroadcast(localIntent);
	}

	public void showPassswordDialog(final String key_title) {
		final EditText mCurrentFormat;
		LinearLayout inputLayout;
		inputLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.input2, null);
		mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);
		new AlertDialog.Builder(this)
				.setTitle(key_title)
				.setView(inputLayout)

				.setPositiveButton(getString(R.string.iscan_password_ok),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								if (mCurrentFormat
										.getText()
										.toString()
										.equals(Variable.getInstance(
												Symbologies6603Activty.this)
												.GetAdvancePassword())) {

									Intent intent = new Intent();
									intent.setClass(
											Symbologies6603Activty.this,
											ConfigurationSettingsActivity.class);
									startActivity(intent);
								} else {
									Toast toast = Toast
											.makeText(
													Symbologies6603Activty.this,
													Symbologies6603Activty.this
															.getString(R.string.iscan_password_error),
													Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
						})

				.setNegativeButton(getString(R.string.iscan_password_cancel),
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) { //

							}
						}).show();
	}

}