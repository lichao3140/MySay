package com.android.auto.iscan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.auto.iscan.R;
import com.android.auto.iscan.ScannerServices;
import com.android.auto.iscan.utility.Variable;

public class ScankeyConfigActivity extends Activity {

	private Context context = null;
	RadioGroup radioGroup_m = null;
	RadioGroup radioGroup_l = null;
	RadioGroup radioGroup_r = null;

	RadioGroup radioGroup_up_l = null;
	RadioGroup radioGroup_up_r = null;

	private RadioButton check_barcode_m, check_rfid_m, check_user_m;
	private RadioButton check_barcode_r, check_rfid_r, check_user_r;
	private RadioButton check_barcode_l, check_rfid_l, check_user_l;

	private RadioButton check_barcode_up_l, check_rfid_up_l, check_user_up_l;
	private RadioButton check_barcode_up_r, check_rfid_up_r, check_user_up_r;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		context = this;
		
		setContentView(R.layout.scankey_dialog);
		

		radioGroup_m = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioGroup_m.setOnCheckedChangeListener(listen_m);
		radioGroup_r = (RadioGroup) this.findViewById(R.id.radioGroup_r);
		radioGroup_r.setOnCheckedChangeListener(listen_r);

		radioGroup_l = (RadioGroup) this.findViewById(R.id.radioGroup_l);
		radioGroup_l.setOnCheckedChangeListener(listen_l);

		check_barcode_m = (RadioButton) findViewById(R.id.check_barcode_m);
		check_rfid_m = (RadioButton) findViewById(R.id.check_rfid_m);
		check_user_m = (RadioButton) findViewById(R.id.check_user_m);

		check_barcode_r = (RadioButton) findViewById(R.id.check_barcode_r);
		check_rfid_r = (RadioButton) findViewById(R.id.check_rfid_r);
		check_user_r = (RadioButton) findViewById(R.id.check_user_r);

		check_barcode_l = (RadioButton) findViewById(R.id.check_barcode_l);
		check_rfid_l = (RadioButton) findViewById(R.id.check_rfid_l);
		check_user_l = (RadioButton) findViewById(R.id.check_user_l);

		int m_value = Variable.getInstance(context).GetMiddlekeyValue();
		switch (m_value) {
		case 0:
			check_barcode_m.setChecked(true);
			break;
		case 1:
			check_rfid_m.setChecked(true);
			break;
		case 2:
			check_user_m.setChecked(true);
			break;

		}

		int l_value = Variable.getInstance(context).GetLeftkeyValue();
		switch (l_value) {
		case 0:
			check_barcode_l.setChecked(true);
			break;
		case 1:
			check_rfid_l.setChecked(true);
			break;
		case 2:
			check_user_l.setChecked(true);
			break;

		}

		int r_value = Variable.getInstance(context).GetRightkeyValue();
		switch (r_value) {
		case 0:
			check_barcode_r.setChecked(true);
			break;
		case 1:
			check_rfid_r.setChecked(true);
			break;
		case 2:
			check_user_r.setChecked(true);
			break;

		}

	}

	private OnCheckedChangeListener listen_m = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (group.getCheckedRadioButtonId()) {
			case R.id.check_barcode_m:
				Variable.getInstance(context).SetMiddlekeyValue(0);
				break;
			case R.id.check_user_m:
				Variable.getInstance(context).SetMiddlekeyValue(2);

				break;
			case R.id.check_rfid_m:
				Variable.getInstance(context).SetMiddlekeyValue(1);

				break;
			}

		}
	};

	

	
	private OnCheckedChangeListener listen_r = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (group.getCheckedRadioButtonId()) {
			case R.id.check_barcode_r:
				Variable.getInstance(context).SetRightkeyValue(0);
				break;
			case R.id.check_user_r:
				Variable.getInstance(context).SetRightkeyValue(2);

				break;
			case R.id.check_rfid_r:
				Variable.getInstance(context).SetRightkeyValue(1);

				break;
			}

		}
	};

	private OnCheckedChangeListener listen_l = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (group.getCheckedRadioButtonId()) {
			case R.id.check_barcode_l:
				Variable.getInstance(context).SetLeftkeyValue(0);

				break;
			case R.id.check_user_l:
				Variable.getInstance(context).SetLeftkeyValue(2);

				break;
			case R.id.check_rfid_l:
				Variable.getInstance(context).SetLeftkeyValue(1);

				break;
			}

		}
	};

	
	
	
	void StartService() {
		Intent i = new Intent(this, ScannerServices.class);
		startService(i);
	}

	public void exitbutton0(View v) {
		StartService();
		this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}