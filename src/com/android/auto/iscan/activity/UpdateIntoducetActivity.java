package com.android.auto.iscan.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.auto.iscan.R;

public class UpdateIntoducetActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.uodate_introduce);
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.title);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.iscan);
		actionBar.show();

		TextView tvTitle = (TextView) findViewById(R.id.Titletext);
		tvTitle.setText(this.getString(R.string.iscan_update_introduce));
		Button btnBack = (Button) findViewById(R.id.TitleBackBtn);
		btnBack.setText(getString(R.string.iscan_back));
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();

			}
		});
	}

	

}
