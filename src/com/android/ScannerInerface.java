package com.android;

import android.content.Context;
import android.content.Intent;

public class ScannerInerface {
/********************************************ɨ��ӿڶ��峣��******************************/
	public static final String KEY_BARCODE_ENABLESCANNER_ACTION = "android.intent.action.BARCODESCAN";
	
	public static final String KEY_BARCODE_STARTSCAN_ACTION = "android.intent.action.BARCODESTARTSCAN";
	
	public static final String KEY_BARCODE_STOPSCAN_ACTION = "android.intent.action.BARCODESTOPSCAN";
	
	public static final String KEY_LOCK_SCAN_ACTION = "android.intent.action.BARCODELOCKSCANKEY";
	
	public static final String KEY_UNLOCK_SCAN_ACTION = "android.intent.action.BARCODEUNLOCKSCANKEY";
	
	public static final String KEY_BEEP_ACTION = "android.intent.action.BEEP";
	
	public static final String KEY_VIBRATE_ACTION = "android.intent.action.VIBRATE";
	
	public static final String KEY_OUTPUT_ACTION = "android.intent.action.BARCODEOUTPUT";
	
    public static final String KEY_POWER_ACTION = "android.intent.action.POWER";
    
    public static final String KEY_ENTER_ACTION = "android.intent.action.ADDKEY";
    public static final String KEY_SHOW_APP_NOTICEICON = "android.intent.action.SHOWNOTICEICON";
	public static final String KEY_SHOW_APP_ICON = "android.intent.action.SHOWAPPICON";
	public static final String KEY_SHOWISCANUI = "com.android.auto.iscan.show_setting_ui";
	public static final String KEY_FAILUREBROADCAST_ACTION = "android.intent.action.FAILUREBROADCAST";
	
/****************************************************************************************************/
	
/********************************************ϵͳ�ӿڶ��峣��******************************/
	static final String  SET_STATUSBAR_EXPAND = "com.android.set.statusbar_expand";
	static final String  SET_USB_DEBUG = "com.android.set.usb_debug";
	static final String  SET_INSTALL_PACKAGE = "com.android.set.install.package";
	static final String  SET_SCREEN_LOCK = "com.android.set.screen_lock";
	static final String  SET_CFG_WAKEUP_ANYKEY = "com.android.set.cfg.wakeup.anykey";
	static final String  SET_UNINSTALL_PACKAGE= "com.android.set.uninstall.package";          
	static final String  SET_SYSTEM_TIME="com.android.set.system.time"; 
	static final String  SET_KEYBOARD_CHANGE = "com.android.disable.keyboard.change";
	static final String SET_INSTALL_PACKAGE_WITH_SILENCE = "com.android.set.install.packege.with.silence";
	static final String SET_INSTALL_PACKAGE_EXTRA_APK_PATH = "com.android.set.install.packege.extra.apk.path";
	static final String SET_INSTALL_PACKAGE_EXTRA_TIPS_FORMAT = "com.android.set.install.packege.extra.tips.format";
	public static final String SET_SIMULATION_KEYBOARD = "com.android.simulation.keyboard";
	public static final String SET_SIMULATION_KEYBOARD_STRING = "com.android.simulation.keyboard.string";
	
	public static final String KEY_CONTINUCESCAN_ACTION = "android.intent.action.CONTINUCESCAN";
/****************************************************************************************************/
	
	private Context mContext;
	private static ScannerInerface androidjni;
	
	public ScannerInerface(Context context) {
		mContext = context;

	}

 //	1.��ɨ��ͷ��Դ
	public void open(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, true);
			mContext.sendBroadcast(intent);
		}
	}
	
//2.�ر�ɨ��ͷ��Դ
	public void  close(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, false);
			mContext.sendBroadcast(intent);
		}
		
	}
	
// 3. ����ɨ��ͷ��ɨ��ͷ����
/*
 * �˺����� scan_stop ���ʹ�ÿ����ڳ������������ɨ��ͷ����ɨ��ͷ���ڿ���״
 ̬ʱ,���� scan_start ���Դ���ɨ��ͷ����ɨ�衣ɨ����ϻ�ʱ��,�������
scan_start �ָ�ɨ��ͷ״̬��
 * 
 * */
 
	public void  scan_start(){
		
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STARTSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
		
	}
	
 //4.ֹͣɨ��ͷ���룬ɨ��ͷ���
	
/**
	 * �˺����� scan_stop ���ʹ�ÿ����ڳ������������ɨ��ͷ����Ӧ�ó������
scan_start ����ɨ��ͷ����ɨ���, ������� scan_stop �ָ�ɨ��ͷ״̬��
	
 */
	public void scan_stop(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STOPSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
		
	}
	
	
	
 // 5. �����豸��ɨ�谴��
	
	/*
	�����豸��ɨ�谴������ɨ�谴���������Ժ�,�û��޷�ͨ��ɨ�谴������ɨ��ͷ��
   ������ɨ�衣 
	 */
	public void  lockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_LOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}
	
  //6.�����ɨ�谴��������
	/******
	 * �����ɨ�谴��������,�û������Լ�����ɨ�谴����ϵͳ����ɨ�蹦�ܹر�
	 * 
	 */
	public void unlockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_UNLOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}
	

  //7.ɨ��ͷ�����ģʽ
	
	/*mode 0:ɨ����ֱ�ӷ��͵�����༭����     mode 1:ɨ�����Թ㲥ģʽ���ͣ�Ӧ�ó�����Ҫע��actionΪ��android.intent.action.SCANRESULT���Ĺ㲥���������ڹ㲥���� onReceive(Context context, Intent arg1) ������,ͨ���������
	String  barocode=arg1.getStringExtra("value");
	int barocodelen=arg1.getIntExtra("length",0);
	�ֱ��� ����ֵ,���볤��,��������,���Բο� demo
	*/
	
	public void setOutputMode(int mode){
		if(mContext != null){
			Intent intent = new Intent(KEY_OUTPUT_ACTION);
			intent.putExtra(KEY_OUTPUT_ACTION, mode);
			mContext.sendBroadcast(intent);
		}
	}
  //8 �Ƿ񲥷�����
	public void enablePlayBeep(boolean enable){
		if(mContext != null){
			Intent intent = new Intent(KEY_BEEP_ACTION);
			intent.putExtra(KEY_BEEP_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}
	
	
  //9 �Ƿ���
	public void enablePlayVibrate(boolean enable){
	   if(mContext != null){
			Intent intent = new Intent(KEY_VIBRATE_ACTION);
			intent.putExtra(KEY_VIBRATE_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}
	
  // 10 ��ӻ��з�
   //value 0:�����   3����ӻ��з�
	public void  enableAddKeyValue(int value){
	  if(mContext != null){
			Intent intent = new Intent(KEY_ENTER_ACTION);
			intent.putExtra(KEY_ENTER_ACTION, value);
			mContext.sendBroadcast(intent);
		}
	}
	  // 11 ����֪ͨ��ͼ��  
	   //enable ��true  ����   false: 
		public void  enablShowNoticeIcon(boolean  enable){
		  if(mContext != null){
				Intent intent = new Intent(KEY_SHOW_APP_NOTICEICON);
				intent.putExtra(KEY_SHOW_APP_NOTICEICON, enable);
				mContext.sendBroadcast(intent);
			}
		}

		 // 12  ���� app ����ͼ�� 
		// enable ��true  ����   false: 
		public void  enablShowAPPIcon(boolean  enable){
			  if(mContext != null){
					Intent intent = new Intent(KEY_SHOW_APP_ICON);
					intent.putExtra(KEY_SHOW_APP_ICON, enable);
					mContext.sendBroadcast(intent);
				}
			}
		
		public void  enableContinuousScan(boolean  enable){
			  if(mContext != null){
					Intent intent = new Intent(KEY_CONTINUCESCAN_ACTION);
					intent.putExtra(KEY_CONTINUCESCAN_ACTION, enable);
					mContext.sendBroadcast(intent);
				}
			}
/************************************************************/
		public void ShowUI(){	
			if(mContext != null){
				Intent intent = new Intent(KEY_SHOWISCANUI);
				mContext.sendBroadcast(intent);
			}
		}
	
		public void SetErrorBroadCast(boolean enable ){	
			if(mContext != null){
				Intent intent = new Intent(KEY_FAILUREBROADCAST_ACTION);
				intent.putExtra(KEY_FAILUREBROADCAST_ACTION, enable);
				mContext.sendBroadcast(intent);
			}
		}
	

}
