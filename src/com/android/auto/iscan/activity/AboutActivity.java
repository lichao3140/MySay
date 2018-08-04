package com.android.auto.iscan.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.auto.iscan.R;
import com.android.auto.iscan.internet.UpdateManager;
import com.android.auto.iscan.utility.ScanLog;

public class AboutActivity extends Activity implements OnItemClickListener {

	private TextView tvVision;

	private ListView list = null;
	private AboutAdapter adapter;
	String[] absText = new String[3];

	private UpdateManager manager = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.title);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.iscan);
		actionBar.show();

		TextView tvTitle = (TextView) findViewById(R.id.Titletext);
		tvTitle.setText(this.getString(R.string.about_logo_text));
		Button btnBack = (Button) findViewById(R.id.TitleBackBtn);
		btnBack.setText(getString(R.string.iscan_back));
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();

			}
		});

		tvVision = (TextView) findViewById(R.id.vision);
		tvVision.setTextColor(Color.BLACK);

		absText[0] = this.getString(R.string.iscan_function_introduce);
		absText[1] = this.getString(R.string.iscan_update);
	//	absText[2] = this.getString(R.string.iscan_update_introduce);

		list = (ListView) findViewById(R.id.list);
		adapter = new AboutAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	class AboutAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		viewHolder holder;

		public AboutAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
			return 2;
		}

		public Object getItem(int pos) {
			return pos;
		}

		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.list_item, parent,
						false);
				holder = new viewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.groupItem);
				convertView.setTag(holder);
				holder.title.setText(absText[position]);

			}
			return convertView;
		}

		class viewHolder {
			public TextView title;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
	
		switch (position) {
		case 0:
			Toast.makeText(AboutActivity.this,
					AboutActivity.this.getString(R.string.function_settings),
					Toast.LENGTH_SHORT).show();
			break;
		case 1:
			ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cwjManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				manager = new UpdateManager(this);
				manager.Checkupdate();
			} else {
				Toast.makeText(AboutActivity.this,
						AboutActivity.this.getString(R.string.iscan_no_wifi),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			Intent intent = new Intent();
			intent.setClass(AboutActivity.this, UpdateIntoducetActivity.class);
			startActivity(intent);
			break;

		}

	}

}
