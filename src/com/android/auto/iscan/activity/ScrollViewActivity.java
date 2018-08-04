package com.android.auto.iscan.activity;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.auto.iscan.R;
import com.android.auto.iscan.utility.AScanLog;
import com.android.auto.iscan.utility.ScanLog;
import com.android.auto.iscan.utility.ScanSettingUtil;

public class ScrollViewActivity extends Activity {

	// private EditText siteCodeEdit;

	private TextView total_num;
	private TextView scan_len;
	private Button btStart;

	private int intentdata;

	private TextView decode_time;

	private IntentFilter mFilter;

	private BroadcastReceiver mReceiver;

	boolean start = false;

	long totaltime = 0;

	int total = 0;
	


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:

				decode_time.setText(String.valueOf(msg.arg1));

				// siteCodeEdit.setText(msg.obj + "");
				scan_len.setText(String.valueOf(msg.arg2));
				decode_time.invalidate();
				total_num.setText(String.valueOf(totaltime));
				break;

			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_test);

		btStart = (Button) findViewById(R.id.start_btn);
		btStart.setOnClickListener(btStart_listener);
		iniHolder();

		decode_time = (TextView) findViewById(R.id.decode_time);
		scan_len = (TextView) findViewById(R.id.decode_len);
		total_num = (TextView) findViewById(R.id.decode_total);

		mFilter = new IntentFilter("android.intent.action.SCANRESULT");
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 此处获取扫描结果信息
				final String scanResult = intent.getStringExtra("value");
				int decodetime = intent.getIntExtra("decodetime", 0);
				AScanLog.getInstance(context).LOGD(scanResult);
				
				if (scanResult.startsWith("%")) {
					String result = scanResult.toString().trim();
					enterSetting(result);
				}
				
				if (scanResult.length() > 0) {
					Message msg = Message.obtain();
					msg.what = 100;
					msg.obj = "";
					msg.arg1 = decodetime;
					msg.arg2 = scanResult.length();
					handler.sendMessage(msg);
					totaltime++;
				}
			}
		};

	}
	
	private void enterSetting(String code) {
		isTime();
		if (code.equals(ScanSettingUtil.SETTING_ENTER)) { // 进入设置
			
			Toast.makeText(ScrollViewActivity.this, code, Toast.LENGTH_LONG).show();
		} else if (code.equals(ScanSettingUtil.SETTING_EXIT)) { // 退出设置
			
			Toast.makeText(ScrollViewActivity.this, code, Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean isTime() {
		long previous = 0L;
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		if(now - previous <= 1000 * 10) {
			previous = now ;
			return true;
		}
		return false;
	}


	private OnClickListener btStart_listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			decode_time.setText("");
			scan_len.setText("");
			total_num.setText("");
			Holder.tv_null.setText("");
			totaltime = 0;
		}
	};

	protected void onResume() {
		super.onResume();
		handler.post(ScrollRunnable);
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
		AScanLog.getInstance(this).disInstance();
		handler.removeCallbacks(ScrollRunnable);
		super.onDestroy();
	}

	private Runnable ScrollRunnable = new Runnable() {
		@Override
		public void run() {
			
			int   line=getCurrentCursorLine(Holder.tv_null);
			
		//	Log.d("012","line:::"+line);
			
			if(line>50){
				int index = Holder.tv_null.getSelectionStart();  
				Editable editable = Holder.tv_null.getText();  
				editable.delete(0, index/2);
			}
			 handler.postDelayed(this, 10);
		}
	};
	
	private int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1&&layout!=null) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }

	private void iniHolder() {
		//Holder.scroll = (ScrollView) findViewById(R.id.sv_scroll);
		Holder.mlayout = (LinearLayout) findViewById(R.id.l_test);
		Holder.tv_null = (EditText) findViewById(R.id.scanresult);
		//Holder.tv_null.setHeight(getWindowManager().getDefaultDisplay()
		//		.getHeight()/2
		//);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		try {
			Class<EditText> cls = EditText.class;
			Method setSoftInputShownOnFocus;
			setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus",
					boolean.class);
			setSoftInputShownOnFocus.setAccessible(true);
			setSoftInputShownOnFocus.invoke(Holder.tv_null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class Holder {
		//static ScrollView scroll;
		static LinearLayout mlayout;
		static EditText tv_null;
	}
	

	
}
