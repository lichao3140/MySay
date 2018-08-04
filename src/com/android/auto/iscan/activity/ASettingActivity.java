package com.android.auto.iscan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.auto.iscan.R;
import com.android.auto.iscan.utility.Variable;

public class ASettingActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if(Variable.getInstance(
				ASettingActivity.this)
				.GetAdvancePassword().equals("")){
			
			Intent intent = new Intent();
			intent.setClass(ASettingActivity.this,
					SettingActivity.class);
			startActivity(intent);
			ASettingActivity.this.finish();
		}
		else
		showPassswordDialog(this.getString(R.string.iscan_enter_password));

	}

	public void showPassswordDialog(final String key_title) {

		LinearLayout inputLayout;
		inputLayout = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.input2, null);
		final EditText mCurrentFormat = (EditText) inputLayout
				.findViewById(R.id.xET);
		new AlertDialog.Builder(this)
				.setTitle(key_title)
				.setView(inputLayout)
				.setCancelable(false)

				.setPositiveButton(getString(R.string.iscan_password_ok),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								if (mCurrentFormat
										.getText()
										.toString()
										.equals(Variable.getInstance(
												ASettingActivity.this)
												.GetAdvancePassword())) {
									Intent intent = new Intent();
									intent.setClass(ASettingActivity.this,
											SettingActivity.class);
									startActivity(intent);

								} else {
									Toast toast = Toast
											.makeText(
													ASettingActivity.this,
													ASettingActivity.this
															.getString(R.string.iscan_password_error),
													Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();

								}
								ASettingActivity.this.finish();
							}
						})

				.setNegativeButton(getString(R.string.iscan_password_cancel),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) { //
								ASettingActivity.this.finish();
							}
						}).show();
	}

}
