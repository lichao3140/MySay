package com.android.auto.iscan.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.android.MotoSE47XXScanner;
import com.android.auto.iscan.R;
import com.android.auto.iscan.ScanFeedback;
import com.android.auto.iscan.ScannnerManager;

public class PreviewActivty extends Activity implements Callback {
	private CheckBox mBeep = null;
	
	private SurfaceHolder surfaceHolder = null;
	public ScannnerManager scannerManager = null;
	public ScanFeedback scanFeedback = null;
	private SurfaceView surfaceView = null;
	boolean beepMode = true;

	private static final String KEY_CAMERAOPENE_ACTION = "com.android.iScan.DS7000.startpreview";
	private static final String KEY_CAMERCLOSE_ACTION = "com.android.iScan.DS7000.stoppreview";
	// NvRAMAgent bb;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.previewetest);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sendBroadcast(KEY_CAMERAOPENE_ACTION);
		surfaceView = (SurfaceView) this.findViewById(R.id.camerapreview);
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(3);
		
	}

	
	private void sendBroadcast(String action){
		Intent localIntent = new Intent();
		localIntent.setAction(action );
	    sendBroadcast(localIntent);
	}
	
	
	protected void onDestroy() {
		surfaceHolder=null;
		
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub  

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		try {
			MotoSE47XXScanner.mBarCodeReader.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		
	}

	
	public boolean dispatchKeyEvent(KeyEvent event) {
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
            	sendBroadcast(KEY_CAMERCLOSE_ACTION);
            	System.exit(0);  
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
	

}
