package com.android.auto.iscan.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.ScannerInerface;
import com.android.auto.iscan.R;
import com.android.auto.iscan.utility.ScanLog;
import com.android.auto.iscan.utility.Variable;

public class BarcodeTestActivty extends Activity {

	private EditText siteCodeEdit;

	private EditText total_time;

	private int intentdata;

	private Button btStart;

	private long totalTime = 0;
	private int scanTotal = 0, errorCount = 0;
	long cur_time = 0;

	int hour1;
	long hour;
	private static Timer mTimer = null;
	int count = 0;
	private TextView decode_time;
	private TextView sucessful_total;
	private TextView error_total;

	int broadcast = 0;
	boolean mErrorbroadcast = false;

	boolean continusScan = false;

	ScannerInerface inerface = null;

	private IntentFilter mFilter;

	private BroadcastReceiver mReceiver;

	boolean start = false;
	
	long  totaltime=0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				scanTotal++;
			
				totaltime+=msg.arg1;
				decode_time.setText("TTR:"+String.valueOf(msg.arg1)+" AVE:"+totaltime/scanTotal);
			
				siteCodeEdit.setText(msg.obj + "");
				sucessful_total.setText(scanTotal + "");
				decode_time.invalidate();

				break;
			case 200:
				
				errorCount++;
				error_total.setText(errorCount + "");
				decode_time.setText(msg.arg1+"");
				error_total.invalidate();
				break;
			case 300:
				cur_time++;
			
				if (cur_time >= totalTime) {
					stopTimer();
					cur_time = 0;
					inerface.enableContinuousScan(false);
					start = false;
					btStart.setText(BarcodeTestActivty.this
							.getString(R.string.start));
				}

				break;

			}
		}
	};

	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(300);
			}
		}, 0, 1000);

	}

	private void stopTimer() {

		if (mTimer != null) {
			mTimer.cancel();
			handler.removeMessages(300);
			mTimer = null;

		}
	}
	String firstdecode="";
	
	boolean  first=false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.barcodetest);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		inerface = new ScannerInerface(this);

		siteCodeEdit = (EditText) findViewById(R.id.site_code);
		siteCodeEdit.setInputType(InputType.TYPE_NULL);

		decode_time = (TextView) findViewById(R.id.decode_time);
		total_time = (EditText) findViewById(R.id.total_time);
		total_time.setText("0.1");
		
		sucessful_total = (TextView) findViewById(R.id.sucessful_total);
		error_total = (TextView) findViewById(R.id.error_total);
		btStart = (Button) findViewById(R.id.start_btn);
		btStart.setOnClickListener(btStart_listener);
		mFilter = new IntentFilter("android.intent.action.SCANRESULT");
		broadcast = Variable.getInstance(this).GetBroadcastState("0");
		mErrorbroadcast = Variable.getInstance(this).GetBroadCastFailure();
		if (!mErrorbroadcast) {

			inerface.SetErrorBroadCast(true);
		}

		if (broadcast!=1) {
			inerface.setOutputMode(1);
		}
		
		first=false;
		
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 此处获取扫描结果信息
				final String scanResult = intent.getStringExtra("value");
				int decodetime = intent.getIntExtra("decodetime", 0);
				if(!first){
				firstdecode=scanResult;
				first=true;
				}
			//	ScanLog.LOGD("decodetime:" + decodetime);
				if (scanResult.length() > 0) {
					Message msg = Message.obtain();
					
					if(!firstdecode.equals(scanResult)){
						msg.what = 200;
						
					}else{
						msg.what = 100;
					}
					msg.obj = scanResult;
					msg.arg1 = decodetime;
					handler.sendMessage(msg);
				} 
			}
		};
		continusScan = Variable.getInstance(this).GetContinusScanState();
	}

	private OnClickListener btStart_listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (!start) {
				startTimer();
				start = true;
				cur_time = 0;
				scanTotal = 0;
				errorCount = 0;
				totaltime=0;
				btStart.setText(BarcodeTestActivty.this
						.getString(R.string.stop));
				totalTime = (int) (Float.parseFloat(total_time.getText()
						.toString()) * 3600);
				if (!continusScan)
					inerface.enableContinuousScan(true);

			} else {
				start = false;
				btStart.setText(BarcodeTestActivty.this
						.getString(R.string.start));
				inerface.enableContinuousScan(false);
				stopTimer();
			}
		}
	};

	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	protected void onPause() {
		// 注销获取扫描结果的广播
		this.unregisterReceiver(mReceiver);
		super.onPause();
	}

	protected void onDestroy() {
		mReceiver = null;
		mFilter = null;
		if (broadcast!=1) {
			inerface.setOutputMode(broadcast);
		}
		if (!continusScan)
			inerface.enableContinuousScan(continusScan);
		if (!mErrorbroadcast) {

			inerface.SetErrorBroadCast(false);
		}
		super.onDestroy();
	}

}
