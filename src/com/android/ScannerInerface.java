package com.android;

import android.content.Context;
import android.content.Intent;

public class ScannerInerface {
/********************************************扫描接口定义常量******************************/
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
	
/********************************************系统接口定义常量******************************/
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

 //	1.打开扫描头电源
	public void open(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, true);
			mContext.sendBroadcast(intent);
		}
	}
	
//2.关闭扫描头电源
	public void  close(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, false);
			mContext.sendBroadcast(intent);
		}
		
	}
	
// 3. 触发扫描头，扫描头出光
/*
 * 此函数和 scan_stop 配合使用可以在程序中软件触发扫描头。当扫描头处于空闲状
 态时,调用 scan_start 可以触发扫描头出光扫描。扫描完毕或超时后,必须调用
scan_start 恢复扫描头状态。
 * 
 * */
 
	public void  scan_start(){
		
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STARTSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
		
	}
	
 //4.停止扫描头解码，扫描头灭光
	
/**
	 * 此函数和 scan_stop 配合使用可以在程序中软件触发扫描头。当应用程序调用
scan_start 触发扫描头出光扫描后, 必须调用 scan_stop 恢复扫描头状态。
	
 */
	public void scan_stop(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STOPSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
		
	}
	
	
	
 // 5. 锁定设备的扫描按键
	
	/*
	锁定设备的扫描按键。在扫描按键被锁定以后,用户无法通过扫描按键触发扫描头进
   行条码扫描。 
	 */
	public void  lockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_LOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}
	
  //6.解除对扫描按键的锁定
	/******
	 * 解除对扫描按键的锁定,用户可以自己控制扫描按键，系统按键扫描功能关闭
	 * 
	 */
	public void unlockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_UNLOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}
	

  //7.扫描头的输出模式
	
	/*mode 0:扫描结果直接发送到焦点编辑框内     mode 1:扫描结果以广播模式发送，应用程序需要注册action为“android.intent.action.SCANRESULT”的广播接收器，在广播机的 onReceive(Context context, Intent arg1) 方法中,通过如下语句
	String  barocode=arg1.getStringExtra("value");
	int barocodelen=arg1.getIntExtra("length",0);
	分别获得 条码值,条码长度,条码类型,可以参考 demo
	*/
	
	public void setOutputMode(int mode){
		if(mContext != null){
			Intent intent = new Intent(KEY_OUTPUT_ACTION);
			intent.putExtra(KEY_OUTPUT_ACTION, mode);
			mContext.sendBroadcast(intent);
		}
	}
  //8 是否播放声音
	public void enablePlayBeep(boolean enable){
		if(mContext != null){
			Intent intent = new Intent(KEY_BEEP_ACTION);
			intent.putExtra(KEY_BEEP_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}
	
	
  //9 是否震动
	public void enablePlayVibrate(boolean enable){
	   if(mContext != null){
			Intent intent = new Intent(KEY_VIBRATE_ACTION);
			intent.putExtra(KEY_VIBRATE_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}
	
  // 10 添加换行符
   //value 0:不添加   3；添加换行符
	public void  enableAddKeyValue(int value){
	  if(mContext != null){
			Intent intent = new Intent(KEY_ENTER_ACTION);
			intent.putExtra(KEY_ENTER_ACTION, value);
			mContext.sendBroadcast(intent);
		}
	}
	  // 11 隐藏通知栏图标  
	   //enable ：true  隐藏   false: 
		public void  enablShowNoticeIcon(boolean  enable){
		  if(mContext != null){
				Intent intent = new Intent(KEY_SHOW_APP_NOTICEICON);
				intent.putExtra(KEY_SHOW_APP_NOTICEICON, enable);
				mContext.sendBroadcast(intent);
			}
		}

		 // 12  隐藏 app 桌面图标 
		// enable ：true  隐藏   false: 
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
