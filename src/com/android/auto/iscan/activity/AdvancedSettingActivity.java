package com.android.auto.iscan.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.auto.iscan.R;
import com.android.auto.iscan.ScannerServices;
import com.android.auto.iscan.barcodescanner.BarcodeScanner;
import com.android.auto.iscan.utility.ConstantUtil;
import com.android.auto.iscan.utility.ScanLog;
import com.android.auto.iscan.utility.Variable;

public class AdvancedSettingActivity extends BaseActivty {
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new AdvancedSettingFragement())
				.commit();
	}

	public static class AdvancedSettingFragement extends PreferenceFragment
			implements Preference.OnPreferenceChangeListener {

		private PreferenceScreen key_prefix;
		private PreferenceScreen key_suffix;

		private PreferenceScreen key_trimleft;
		private PreferenceScreen key_trimright;

		private PreferenceScreen key_advance_password;
		private PreferenceScreen key_interval_time;

		private PreferenceScreen key_filter_character;
		private PreferenceScreen key_enable_code;
		private PreferenceScreen key_reset;

		private ListPreference m_key_broadcast;
		private CheckBoxPreference m_key_icon;

		private CheckBoxPreference m_key_notice;

		private CheckBoxPreference key_scan_stop;

		private CheckBoxPreference m_key_failure;

		private CheckBoxPreference m_key_deletect;

		private PreferenceScreen key_scan_test;

		private PreferenceScreen key_interval_outtime;

		private ListPreference key_char;
		private CheckBoxPreference key_log;

		private CheckBoxPreference key_continus_scan;
		public static AdvancedSettingFragement instance = null;
		private TextView tvTitle;
		private Button btnBack;
		private PreferenceScreen key_outtime;
		private EditText mCurrentFormat;
		PreferenceScreen root;

		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.advancesetting);
			root = getPreferenceScreen();

			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setCustomView(R.layout.title);
			actionBar.setIcon(R.drawable.iscan);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.show();
			instance = this;

			// getActivity().setTheme(android.R.style.Theme_Light);
			getActivity().setTheme(R.style.AppBaTheme);

			key_prefix = ((PreferenceScreen) findPreference(ConstantUtil.key_prefix));
			key_prefix.setOnPreferenceChangeListener(this);

			key_suffix = ((PreferenceScreen) findPreference(ConstantUtil.key_suffix));
			key_suffix.setOnPreferenceChangeListener(this);

			key_interval_time = ((PreferenceScreen) findPreference(ConstantUtil.key_interval_time));
			key_interval_time.setOnPreferenceChangeListener(this);

			key_filter_character = ((PreferenceScreen) findPreference(ConstantUtil.key_filter_character));
			key_filter_character.setOnPreferenceChangeListener(this);

			key_enable_code = ((PreferenceScreen) findPreference(ConstantUtil.key_enable_code));
			key_enable_code.setOnPreferenceChangeListener(this);

			key_advance_password = ((PreferenceScreen) findPreference(ConstantUtil.key_advance_password));
			key_advance_password.setOnPreferenceChangeListener(this);

			key_interval_outtime = ((PreferenceScreen) findPreference(ConstantUtil.key_interval_outtime));
			key_interval_outtime.setOnPreferenceChangeListener(this);

			if (BarcodeScanner.id != 0x6603 && BarcodeScanner.id != 0x6000 && BarcodeScanner.id != 0x4710) {
				root.removePreference(key_enable_code);
			} else {
				root.removePreference(key_interval_outtime);
			}

		
			key_reset = ((PreferenceScreen) findPreference(ConstantUtil.key_reset));
			key_reset.setOnPreferenceChangeListener(this);

			m_key_notice = ((CheckBoxPreference) findPreference(ConstantUtil.key_notice));
			m_key_notice.setOnPreferenceChangeListener(this);

			m_key_broadcast = ((ListPreference) findPreference(ConstantUtil.key_broadcast));
			m_key_broadcast.setOnPreferenceChangeListener(this);

			m_key_failure = ((CheckBoxPreference) findPreference(ConstantUtil.key_beep_bad));
			m_key_failure.setOnPreferenceChangeListener(this);

			m_key_deletect = ((CheckBoxPreference) findPreference(ConstantUtil.key_deletect));
			m_key_deletect.setOnPreferenceChangeListener(this);

			key_trimleft = ((PreferenceScreen) findPreference(ConstantUtil.key_trimleft));
			key_trimleft.setOnPreferenceChangeListener(this);

			key_scan_test = ((PreferenceScreen) findPreference(ConstantUtil.key_scan_test));
			key_scan_test.setOnPreferenceChangeListener(this);

			m_key_icon = ((CheckBoxPreference) findPreference(ConstantUtil.key_icon));
			m_key_icon.setOnPreferenceChangeListener(this);
			root.removePreference(m_key_icon);

			key_scan_stop = ((CheckBoxPreference) findPreference(ConstantUtil.key_scan_stop));
			key_scan_stop.setOnPreferenceChangeListener(this);

			key_log = ((CheckBoxPreference) findPreference(ConstantUtil.key_log));
			key_log.setOnPreferenceChangeListener(this);

			key_char = ((ListPreference) findPreference(ConstantUtil.key_charset));
			key_char.setOnPreferenceChangeListener(this);

			key_trimright = ((PreferenceScreen) findPreference(ConstantUtil.key_trimright));
			key_trimright.setOnPreferenceChangeListener(this);
			tvTitle = (TextView) getActivity().findViewById(R.id.Titletext);
			tvTitle.setText("iScan");

			key_outtime = ((PreferenceScreen) findPreference("key_outtime"));
			key_outtime.setOnPreferenceChangeListener(this);

			key_continus_scan = ((CheckBoxPreference) findPreference(ConstantUtil.key_continus_scan));
			key_continus_scan.setOnPreferenceChangeListener(this);

			btnBack = (Button) getActivity().findViewById(R.id.TitleBackBtn);
			btnBack.setText(getString(R.string.iscan_back));
			btnBack.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					getActivity().finish();

				}
			});
			updateState();
		}

		private void updateState() {
			key_prefix.setSummary(Variable.getInstance(getActivity())
					.GetPrefix());
			key_suffix.setSummary(Variable.getInstance(getActivity())
					.GetSuffix());
			key_trimleft.setSummary(Variable.getInstance(getActivity())
					.GetTrimleft());
			key_trimright.setSummary(Variable.getInstance(getActivity())
					.GetTrimright());
			key_interval_time.setSummary(Variable.getInstance(getActivity())
					.GetIntervalTime());
			key_filter_character.setSummary(Variable.getInstance(getActivity())
					.GetFilterCharacter());
			key_outtime.setSummary(Variable.getInstance(getActivity())
					.GetOutTime());
			m_key_broadcast.setSummary(m_key_broadcast.getEntry());
			key_char.setSummary(key_char.getEntry());
			key_interval_outtime.setSummary(Variable.getInstance(getActivity())
					.GetIntervalOutTime());
		}

		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			final String key = preference.getKey();
			final String key_title = preference.getTitle().toString();
			if (key.equals(ConstantUtil.key_notice)
					|| key.equals(ConstantUtil.key_broadcast)
					|| key.equals(ConstantUtil.key_charset)
					|| key.equals(ConstantUtil.key_deletect)
					|| key.equals(ConstantUtil.key_beep_bad)
					|| key.equals(ConstantUtil.key_log)
					|| key.equals(ConstantUtil.key_icon)
					|| key.equals(ConstantUtil.key_scan_stop)
					|| key.equals(ConstantUtil.key_continus_scan)) {
				return false;
			} else if (key.equals(ConstantUtil.key_enable_code)) {
				Intent intent = new Intent();

				if (BarcodeScanner.id == 0x6603) {
					intent.setClass(getActivity(), Symbologies6603Activty.class);
					// showPassswordDialog1(AdvancedSettingFragement.this.getString(R.string.iscan_enter_password));

				} else if (BarcodeScanner.id == 0x6000) {
					intent.setClass(getActivity(), Symbologies47XXActivty.class);
				}
				else if (BarcodeScanner.id == 0x4710) {
					intent.setClass(getActivity(), Symbologies4710Activty.class);
				}

				startActivity(intent);
			} else if (key.equals(ConstantUtil.key_scan_test)) {
				
				showPassswordDialog1(AdvancedSettingFragement.this.getString(R.string.iscan_enter_password));
				
				

			} else if (key.equals(ConstantUtil.key_advance_password)) {
				showEditDialog(
						preference,
						getActivity().getString(
								R.string.iscan_advance_oldpassword), key, "");
			}

			else if (key.equals(ConstantUtil.key_reset)) {
				ScanLog.getInstance(getActivity()).LOGD(
						" manual:Reset default");
				Variable.getInstance(getActivity()).ResetValue();
				StartService();
				AdvancedSettingFragement.instance.getActivity().finish();
				SettingActivity.SettingFragement.instance.getActivity()
						.finish();
			} else if (key.equals(ConstantUtil.key_scan_config)) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ScankeyConfigActivity.class);
				startActivity(intent);
			} else {
				showEditDialog(preference, key_title, key, preference
						.getSummary().toString());
			}

			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub

			final String key = preference.getKey();
			if (key.equals(ConstantUtil.key_broadcast)
					|| key.equals("key_charset")) {
				ListPreference listPreference = (ListPreference) preference;
				CharSequence[] entries = listPreference.getEntries();
				int index = listPreference.findIndexOfValue((String) newValue);
				listPreference.setSummary("" + entries[index]);
				ScanLog.getInstance(getActivity()).LOGD("output:"+
						entries[index]);

			}
			if (key.equals(ConstantUtil.key_continus_scan)) {
				if (!key_continus_scan.isChecked()) {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:Open continus scan");
				} else {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:Close continus scan");
				}
			}
			if (key.equals(ConstantUtil.key_scan_stop)) {
				if (!key_scan_stop.isChecked()) {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:Open  saving model");
				} else {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:Close saving scan");
				}
			}
			
			if (key.equals(ConstantUtil.key_deletect)) {
				if (!m_key_deletect.isChecked()) {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:delet char");
				} else {
					ScanLog.getInstance(getActivity()).LOGD(
							" manual:no delet char");
				}
			}
			
			StartService();
			return true;
		}

		public void StartService() {
			Intent i = new Intent(getActivity(), ScannerServices.class);
			getActivity().startService(i);
		}

		public void showEditDialog(final Preference preference,
				final String key_title, final String key, final String key_text) {

			LinearLayout inputLayout;
			if (key.equals("key_prefix") || key.equals("key_suffix")
					|| key.equals("key_filter_character")) {
				inputLayout = (LinearLayout) getActivity().getLayoutInflater()
						.inflate(R.layout.input1, null);
			} else if (key.equals(ConstantUtil.key_advance_password)) {

				inputLayout = (LinearLayout) getActivity().getLayoutInflater()
						.inflate(R.layout.input2, null);
			} else {
				inputLayout = (LinearLayout) getActivity().getLayoutInflater()
						.inflate(R.layout.input, null);
			}
			mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);//
			if (!key_text.equals("")) {
				this.mCurrentFormat.setText(key_text);
			}
			new AlertDialog.Builder(getActivity())
					.setTitle(key_title)
					.setView(inputLayout)

					.setPositiveButton(
							getString(R.string.iscan_password_ok),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (key.equals("key_prefix")
											|| key.equals("key_suffix")
											|| key.equals("key_filter_character")) {
										Variable.getInstance(getActivity())
												.setCookieVariables(
														key,
														mCurrentFormat
																.getText()
																.toString());
										preference.setSummary(mCurrentFormat
												.getText().toString());
										StartService();
									} else if (key
											.equals(ConstantUtil.key_advance_password)) {

										if (mCurrentFormat
												.getText()
												.toString()
												.equals(Variable.getInstance(
														getActivity())
														.GetAdvancePassword())) {
											showPassswordDialog(getActivity()
													.getString(
															R.string.iscan_advance_newpassword));

										} else {
											Toast toast = Toast
													.makeText(
															getActivity(),
															getActivity()
																	.getString(
																			R.string.iscan_password_error),
															Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.CENTER, 0,
													0);
											toast.show();
										}

									}

									else {
										Variable.getInstance(getActivity())
												.setCookieVariables(
														key,
														mCurrentFormat
																.getText()
																.toString());
										preference.setSummary(mCurrentFormat
												.getText().toString());
										StartService();
									}

								}

							})

					.setNegativeButton(
							getString(R.string.iscan_password_cancel),
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) { //

								}
							}).show();
		}

		public void showPassswordDialog(final String key_title) {

			LinearLayout inputLayout;
			inputLayout = (LinearLayout) getActivity().getLayoutInflater()
					.inflate(R.layout.input2, null);
			mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);
			new AlertDialog.Builder(getActivity())
					.setTitle(key_title)
					.setView(inputLayout)

					.setPositiveButton(
							getString(R.string.iscan_password_ok),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Variable.getInstance(getActivity())
											.SetAdvancePassword(
													mCurrentFormat.getText()
															.toString());
									StartService();
								}

							})

					.setNegativeButton(
							getString(R.string.iscan_password_cancel),
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) { //

								}
							}).show();
		}

		public void showPassswordDialog1(final String key_title) {
			final EditText mCurrentFormat;
			LinearLayout inputLayout;
			inputLayout = (LinearLayout) getActivity().getLayoutInflater()
					.inflate(R.layout.input2, null);
			mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);
			new AlertDialog.Builder(getActivity())
					.setTitle(key_title)
					.setView(inputLayout)

					.setPositiveButton(
							getString(R.string.iscan_password_ok),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (mCurrentFormat
											.getText()
											.toString()
											.equals(Variable.getInstance(
													getActivity())
													.GetAdvancePassword())) {

										Intent intent = new Intent();
										intent.setClass(getActivity(), BarcodeTestActivty.class);
										startActivity(intent);
										
									} else {
										Toast toast = Toast
												.makeText(
														getActivity(),
														getActivity()
																.getString(
																		R.string.iscan_password_error),
														Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
								}
							})

					.setNegativeButton(
							getString(R.string.iscan_password_cancel),
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) { //

								}
							}).show();
		}

		
		public void showPassswordDialog2(final String key_title) {
			final EditText mCurrentFormat;
			LinearLayout inputLayout;
			inputLayout = (LinearLayout) getActivity().getLayoutInflater()
					.inflate(R.layout.input2, null);
			mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);
			new AlertDialog.Builder(getActivity())
					.setTitle(key_title)
					.setView(inputLayout)

					.setPositiveButton(
							getString(R.string.iscan_password_ok),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (mCurrentFormat
											.getText()
											.toString()
											.equals(Variable.getInstance(
													getActivity())
													.GetAdvancePassword())) {

										Intent intent = new Intent();
										intent.setClass(
												getActivity(),
												ConfigurationSettingsActivity.class);
										startActivity(intent);
									} else {
										Toast toast = Toast
												.makeText(
														getActivity(),
														getActivity()
																.getString(
																		R.string.iscan_password_error),
														Toast.LENGTH_SHORT);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
								}
							})

					.setNegativeButton(
							getString(R.string.iscan_password_cancel),
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) { //

								}
							}).show();
		}

		public void onDestroy() {

			super.onDestroy();
		}

	}

}
