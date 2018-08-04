package com.android.auto.iscan;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.android.BarcodeControll;
import com.android.BarcodeJNI;
import com.android.HonewellManager;
import com.android.MotoSE47XXScanner;
import com.android.ScannerInerface;
import com.android.aidl.ScannerAPI;
import com.android.auto.iscan.utility.Gather;
import com.android.auto.iscan.utility.NoticeManager;
import com.android.auto.iscan.utility.ParamNum;
import com.android.auto.iscan.utility.ScanLog;
import com.android.auto.iscan.utility.Variable;
import com.barcode.sdk.CameraListener;
import com.barcode.sdk.CameraMonkey;
import com.hsm.barcode.DecoderException;

public class ScannerServices extends Service {

	public boolean m_key_barcodescan = true;
	public boolean m_key_rfidscan = false;
	public int m_key_broadcast = 0;
	public int m_key_charset = 0;
	public boolean m_key_beep = false;
	public boolean m_key_beep_faliure = false;
	public boolean m_key_vibrate = false;
	public boolean m_key_enter = false;
	public boolean m_key_power = false;
	public boolean m_key_lr = false;
	public boolean m_key_tab = false;
	public boolean m_key_light = false;
	public boolean m_key_notice = true;
	public boolean m_key_icon = false;
	public boolean m_key_continuscan = false;
	public boolean m_key_deletect = false;
	public boolean m_key_lock = false;
	public boolean CameraOpen = false;
	public boolean SetingActivty = false;
	public int key_terminator = 0;
	public String mLabelPrefix = "";
	public String mLabelSuffix = "";
	public String f_character = "";
	public boolean mErrorBroadCast = false;
	public int mLabelleft = 0;
	public int mLabelright = 0;
	int mIntervalTime = 1000;
	int mIntervalOutTime = 30;
	public int key_f9 = 0;
	public int key_f10 = 0;
	public int key_f11 = 0;
	public int key_f8 = 0;
	public int key_f1 = 0;
	public int key_f2 = 0;
	public int timeout = 3000;
	boolean isFirstOn = false;
	public String en_mode = "UTF-8";
	public NoticeManager scannnerNoticeManager = null;
	public ScannnerManager scannerManager = null;
	public RegisterScannerBroadcast scannerBroadcastManager = null;
	public ScanFeedback scanFeedback = null;
	public ScannnerHandler mHandler = null;
	public boolean ScreenON = false;
	public boolean ScreenOFF = false;
	boolean isCharging = false;
	public boolean Key_scan_stop = false;

	public boolean key_log = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public void onCreate() {

		scannnerNoticeManager = new NoticeManager(this);
		scannerManager = new ScannnerManager(this);
		scannerBroadcastManager = new RegisterScannerBroadcast(this);
		scanFeedback = new ScanFeedback(this);
		mHandler = new ScannnerHandler(this);
		BarcodeControll.mHandler = this.mHandler;
		if (BarcodeJNI.GetPlatform() == 10) {
			new MyBinder(this);
		}
	}

	public void onStart(Intent intent, int startId) {
		initSetting();
		scannnerNoticeManager.setNotificationEnabled(m_key_notice);
		scannerManager.setBarcodeScanEnabled(m_key_barcodescan);
		scannerManager.setBarcodeTimeOut(timeout);
		scannerManager.setContinuousScanEnabled(m_key_continuscan,
				mIntervalTime);
		scannerManager.setParams(0x11008, mIntervalOutTime);
		scanFeedback.setLightEnabled(m_key_light);
		scanFeedback.setSuccessfulBeepEnabled(m_key_beep);
		scanFeedback.setFailureBeepEnabled(m_key_beep_faliure);
		scanFeedback.setVibrateEnabled(m_key_vibrate);
		super.onStart(intent, startId);
	}

	public void onDestroy() {
		scannerManager.setBarcodeScanEnabled(false);
		scannerBroadcastManager.UnregisterReceiver();
		scannnerNoticeManager.CancerNotification();
		super.onDestroy();
	}

	public void SendBroadcast(String value) {
		Intent _intent = new Intent(Gather.REQUEST);
		_intent.putExtra("Timestamp", new Date().getTime());
		_intent.putExtra("Cmd", 0x0024);
		_intent.putExtra("InputString", value);
		sendBroadcast(_intent);
	}

	public void SendKeyEvent(String result) {
		if (m_key_lr || m_key_enter) {
			result += "\n";
		}
		if (m_key_tab) {
			result += "\t";
		}
		if (BarcodeJNI.GetPlatform() == 10) {
			SendBroadcast(result);
		} else {
			Intent intent = new Intent(
					ScannerInerface.SET_SIMULATION_KEYBOARD_STRING);
			intent.putExtra(ScannerInerface.SET_SIMULATION_KEYBOARD_STRING,
					result);
			sendBroadcast(intent);
		}
	}

	public void HandleBacodeResult(String result, int decodetime) {
		if (m_key_lr) {
			result += "\n";
		}
		String res = result.replace(f_character, "");
		if (m_key_broadcast == 1) {
			SendBraodcast(res, decodetime);
		} else if (m_key_broadcast == 0) {
			SendScanText(res);
			SendBraodcast(res, decodetime);
		} else if (m_key_broadcast == 2) {
			SendKeyEvent(res);
			SendBraodcast(res, decodetime);
		}
		scanFeedback.handleSuccessfulScan();
		scanFeedback.SetLed_ResultScan(isCharging);
	}

	public void SendBraodcast(String result, int decodetime) {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.SCANRESULT");
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.putExtra("value", result);
		localIntent.putExtra("length", result.length());
		localIntent.putExtra("decodetime", decodetime);
		getApplicationContext().sendBroadcast(localIntent);
	}

	public void SendScanText(String info) {
		Intent localIntent = new Intent();
		localIntent.setAction("com.iData.Scancontext");
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.putExtra("Scan_context", info);
		if (m_key_deletect) {
			localIntent.putExtra("DeleteSurroundingText", true);
		}
		if (m_key_enter && !SetingActivty) {
			localIntent.putExtra("iData_SimulateKeyboard", true);
			localIntent.putExtra("iData_SimulateKeyboard_Keycode",
					KeyEvent.KEYCODE_ENTER);
		}
		if (m_key_tab) {
			localIntent.putExtra("iData_SimulateKeyboard", true);
			localIntent.putExtra("iData_SimulateKeyboard_Keycode",
					KeyEvent.KEYCODE_TAB);
		}
		getApplicationContext().sendBroadcast(localIntent);
	}

	public void initSetting() {
		m_key_icon = Variable.getInstance(this).GetAppIconState();
		m_key_notice = Variable.getInstance(this).GetNoticeIconState();
		m_key_barcodescan = Variable.getInstance(this).GetBarcodeState();

		m_key_deletect = Variable.getInstance(this).GetDeletectState();
		m_key_beep = Variable.getInstance(this).GetBeepState();
		m_key_vibrate = Variable.getInstance(this).GetVibrateState();
		m_key_broadcast = Variable.getInstance(this).GetBroadcastState("0");
		m_key_charset = Variable.getInstance(this).GetCharSet("0");
		m_key_light = Variable.getInstance(this).GetLightState();
		m_key_power = Variable.getInstance(this).GetPowerState();
		m_key_continuscan = Variable.getInstance(this).GetContinusScanState();
		f_character = Variable.getInstance(this).GetFilterCharacter();
		key_f9 = Variable.getInstance(this).GetMiddlekeyValue();
		key_f10 = Variable.getInstance(this).GetLeftkeyValue();
		key_f11 = Variable.getInstance(this).GetRightkeyValue();
		key_f1 = Variable.getInstance(this).GetUpLeftkeyValue();
		key_f2 = Variable.getInstance(this).GetUpRightkeyValue();
		key_f8 = Variable.getInstance(this).GetMiddlekeyValue();
		m_key_lock = Variable.getInstance(this).GetKeyScanEnable();
		mLabelPrefix = Variable.getInstance(this).GetPrefix();
		mLabelSuffix = Variable.getInstance(this).GetSuffix();
		mErrorBroadCast = Variable.getInstance(this).GetBroadCastFailure();
		key_terminator = Variable.getInstance(this).GetTerminator("1");
		key_log = Variable.getInstance(this).getOutputLog();
		Handl_Key_State(key_terminator);
		mIntervalTime = Integer.parseInt(Variable.getInstance(this)
				.GetIntervalTime().equals("") ? "0" : Variable
				.getInstance(this).GetIntervalTime());
		mIntervalOutTime = Integer.parseInt(Variable.getInstance(this)
				.GetIntervalOutTime().equals("") ? "0" : Variable.getInstance(
				this).GetIntervalOutTime());
		mLabelleft = Integer.parseInt(Variable.getInstance(this).GetTrimleft()
				.equals("") ? "0" : Variable.getInstance(this).GetTrimleft());
		mLabelright = Integer.parseInt(Variable.getInstance(this)
				.GetTrimright().equals("") ? "0" : Variable.getInstance(this)
				.GetTrimright());
		m_key_beep_faliure = Variable.getInstance(this).GetBeepFailueState();
		timeout = Integer.parseInt(Variable.getInstance(this).GetOutTime()
				.equals("") ? "0" : Variable.getInstance(this).GetOutTime());
		Key_scan_stop = Variable.getInstance(this).GetKeyScanStop();

	}

	public void Handl_Key_State(int state) {
		switch (state) {
		case 0:
			m_key_enter = false;
			m_key_lr = false;
			m_key_tab = false;
			break;
		case 1:
			m_key_enter = true;
			m_key_lr = false;
			m_key_tab = false;
			break;
		case 2:
			m_key_enter = false;
			m_key_lr = false;
			m_key_tab = true;
			break;
		case 3:
			m_key_enter = false;
			m_key_lr = true;
			m_key_tab = false;
			break;
		}
	}

	public void BarcodeScanner(String key_action) {
		if (key_action.equals("down")) {
			scannerManager.startScanning();
			scanFeedback.SetLed_SatrtScan(isCharging);

		} else if (key_action.equals("up")) {
			if (!Key_scan_stop) {
				scannerManager.stopScanning();
				scanFeedback.SetLed_SatopScan(isCharging);
			}
		}

	}

	public void BarcodeContinusScanner(String key_action) {
		if (key_action.equals("down")) {
			m_key_continuscan = !m_key_continuscan;
			scannerManager.setContinuousScanEnabled(m_key_continuscan,
					mIntervalTime);
		} else if (key_action.equals("up")) {

		}
	}

	public void Handle_YellowKey(String key_action, int value) {
		if (value == 0) {// 扫描
			BarcodeScanner(key_action);
		} else if (value == 2) {
			BarcodeContinusScanner(key_action);
		}
	}

	public void setPara(int num, Object obj) {
		switch (num) {
		case ParamNum.barcode: {
			boolean key_barcodescan = ((Boolean) obj).booleanValue();
			if (m_key_barcodescan != key_barcodescan) {
				m_key_barcodescan = key_barcodescan;
				Variable.getInstance(this).SetBarcodeState(m_key_barcodescan);
				scannerManager.setBarcodeScanEnabled(m_key_barcodescan);
				if (m_key_barcodescan)
					ScanLog.getInstance(this).LOGD("broadcast:open  scannner ");
				else {

					ScanLog.getInstance(this).LOGD(
							"broadcast: close  scannner ");
				}
			}
		}
			break;

		case ParamNum.reset_barcode: {
			m_key_barcodescan = true;
			scannerManager.setBarcodeScanEnabled(m_key_barcodescan);
			Variable.getInstance(this).SetBarcodeState(m_key_barcodescan);
			m_key_lock = true;
			Variable.getInstance(this).SetKeyScanEnable(m_key_lock);
			ScanLog.getInstance(this).LOGD("broadcast:reset  scannner ");
		}
			break;

		case ParamNum.broadcast: {
			m_key_broadcast = ((Integer) obj).intValue();
			Variable.getInstance(this).SetBroadcastState(
					String.valueOf(m_key_broadcast));

			ScanLog.getInstance(this).LOGD(
					"broadcast:add context-- " + m_key_broadcast);
		}
			break;
		case ParamNum.beep: {
			m_key_beep = ((Boolean) obj).booleanValue();
			scanFeedback.setSuccessfulBeepEnabled(m_key_beep);
			Variable.getInstance(this).SetBeepState(m_key_beep);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_beep-- " + m_key_beep);
		}
			break;

		case ParamNum.failurebeep: {
			m_key_beep_faliure = ((Boolean) obj).booleanValue();
			scanFeedback.setFailureBeepEnabled(m_key_beep_faliure);
			Variable.getInstance(this).SetBeepFailueState(m_key_beep_faliure);
			ScanLog.getInstance(this).LOGD(
					"broadcast:failurebeep-- " + m_key_beep_faliure);

		}
			break;

		case ParamNum.vibrate: {
			m_key_vibrate = ((Boolean) obj).booleanValue();
			scanFeedback.setVibrateEnabled(m_key_vibrate);
			Variable.getInstance(this).SetVibrateState(m_key_vibrate);
			ScanLog.getInstance(this).LOGD(
					"broadcast:vibrate-- " + m_key_vibrate);
		}
			break;
		case ParamNum.terminator: {
			key_terminator = ((Integer) obj).intValue();
			Variable.getInstance(this).SetTerminator(
					String.valueOf(key_terminator));
			Handl_Key_State(key_terminator);

			ScanLog.getInstance(this).LOGD(
					"broadcast:terminator-- " + key_terminator);
		}
			break;
		case ParamNum.power: {
			m_key_power = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetPowerState(m_key_power);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_power-- " + m_key_power);
		}
			break;

		case ParamNum.filterCharacter: {
			f_character = (String) obj;
			Variable.getInstance(this).SetFilterCharacter(f_character);
		}
			break;

		case ParamNum.light: {
			m_key_light = ((Boolean) obj).booleanValue();
			scanFeedback.setLightEnabled(m_key_light);
			Variable.getInstance(this).SetLightState(m_key_light);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_light-- " + m_key_light);
		}
			break;
		case ParamNum.notice: {
			m_key_notice = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetNoticeIconState(m_key_notice);
			scannnerNoticeManager.setNotificationEnabled(m_key_notice);

			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_notice-- " + m_key_notice);
		}
			break;

		case ParamNum.icon: {
			m_key_icon = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetAppIconState(m_key_icon);
		}
			break;

		case ParamNum.startscan:
			scannerManager.startScanning();

			break;
		case ParamNum.stopscan:
			scannerManager.stopScanning();
			break;

		case ParamNum.screenon: {
			if (m_key_power && m_key_barcodescan && !CameraOpen) {
				mHandler.sendEmptyMessage(ParamNum.screenon);
			}
		}
			break;

		case ParamNum.screenoff: {
			if (m_key_power && m_key_barcodescan && !CameraOpen) {
				mHandler.sendEmptyMessage(ParamNum.screenoff);

			}
		}
			break;

		case ParamNum.cameraon: {
			if (!scannerManager.IsSoftScannner() || !m_key_barcodescan) {

				return;
			}
			scannerManager.setBarcodeScanEnabled(false);
			CameraOpen = true;
			m_key_barcodescan = false;

		}
			break;
		case ParamNum.cameraoff: {
			if (!scannerManager.IsSoftScannner()
					|| !Variable.getInstance(this).GetBarcodeState()) {
				return;
			}
			scannerManager.setBarcodeScanEnabled(true);
			CameraOpen = false;
			m_key_barcodescan = true;
		}
			break;

		case ParamNum.poweroff:
			scannerManager.setBarcodeScanEnabled(false);
			break;

		case ParamNum.lockkey: {
			m_key_lock = true;
			Variable.getInstance(this).SetKeyScanEnable(m_key_lock);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_lock-- " + m_key_lock);
		}
			break;

		case ParamNum.unlockkey: {
			m_key_lock = false;
			Variable.getInstance(this).SetKeyScanEnable(m_key_lock);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_lock-- " + m_key_lock);
		}
			break;

		case ParamNum.continucescan: {
			m_key_continuscan = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetContinusScan(m_key_continuscan);
			scannerManager.setContinuousScanEnabled(m_key_continuscan,
					mIntervalTime);

			ScanLog.getInstance(this).LOGD(
					"broadcastm_key_continuscan-- " + m_key_continuscan);
		}
			break;
		case ParamNum.prefix: {
			mLabelPrefix = (String) obj;
			Variable.getInstance(this).setPrefix(mLabelPrefix);
		}
			break;

		case ParamNum.subfix: {
			mLabelSuffix = (String) obj;
			Variable.getInstance(this).setSuffix(mLabelSuffix);
		}
			break;
		case ParamNum.tirmleft: {
			mLabelleft = ((Integer) obj).intValue();
			Variable.getInstance(this).setTrimleft(String.valueOf(mLabelleft));
		}
			break;
		case ParamNum.tirmright: {
			mLabelright = ((Integer) obj).intValue();
			Variable.getInstance(this)
					.setTrimright(String.valueOf(mLabelright));
		}
			break;

		case ParamNum.timeout: {
			timeout = ((Integer) obj).intValue();
			Variable.getInstance(this).SetOutTime(timeout);
			scannerManager.setBarcodeTimeOut(timeout);
		}
			break;
		case ParamNum.intervaltime: {
			mIntervalTime = ((Integer) obj).intValue();
			Variable.getInstance(this).SetIntervalTime(
					String.valueOf(mIntervalTime));
			scannerManager.setContinuousScanEnabled(m_key_continuscan,
					mIntervalTime);
		}
			break;

		case ParamNum.IntervalOutTime: {
			mIntervalOutTime = ((Integer) obj).intValue();
			Variable.getInstance(this).SetIntervalOutTime(
					String.valueOf(mIntervalOutTime));
			scannerManager.setParams(0x11008, mIntervalOutTime);
		}
			break;

		case ParamNum.deleted: {
			m_key_deletect = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetDeletectState(m_key_deletect);
			ScanLog.getInstance(this).LOGD(
					"broadcast:m_key_deletect-- " + m_key_deletect);
		}
			break;

		case ParamNum.reset: {
			Variable.getInstance(this).ResetValue();
			StartService();
			ScanLog.getInstance(this).LOGD("broadcast:reset ");
		}
			break;

		case ParamNum.updatesetting:
			scannerManager.setParams(0, 0);
			break;
		case ParamNum.showui:
			mHandler.sendEmptyMessageDelayed(ParamNum.showui, 500);
			break;

		case ParamNum.failurebroadcast: {
			mErrorBroadCast = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SettBroadCastFailure(mErrorBroadCast);
		}
			break;
		case ParamNum.exit:
			stopService();
			ScanLog.getInstance(this).LOGD("broadcast:exit ");
			break;
		case ParamNum.reboot: {
			Variable.getInstance(this).SetBarcodeState(true);
			scannerManager.setBarcodeScanEnabled(true);
		}
			break;

		case ParamNum.scanstop: {
			Key_scan_stop = ((Boolean) obj).booleanValue();
			Variable.getInstance(this).SetKeyScanStop(Key_scan_stop);
		}

			break;

		case ParamNum.charset: {
			m_key_charset = ((Integer) obj).intValue();
			Variable.getInstance(this)
					.SetCharSet(String.valueOf(m_key_charset));
		}
			// /
			// ScanLog.getInstance(this).LOGD("broadcast:m_key_charset-- "+m_key_charset
			// );
			break;

		case ParamNum.charge: {
			isCharging = ((Boolean) obj).booleanValue();
			if (BarcodeJNI.GetPlatform() == 21)
				isCharging = false;
		}
			break;
		case ParamNum.settingactivity: {
			SetingActivty = ((Boolean) obj).booleanValue();
		}
			break;
		case ParamNum.speechinput: {
			String barcode = ((String) obj);
			HandleBacodeResult(barcode, 0);
		}

			break;
		case ParamNum.brightness: {
			int value = ((Integer) obj).intValue();
			Variable.getInstance(this).SetBrghtnessValue(String.valueOf(value));
			try {
				if (HonewellManager.m_Decoder != null)
					HonewellManager.SetScanningSettings();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
			break;
		case ParamNum.lightsmode: {
			int value = ((Integer) obj).intValue();
			Variable.getInstance(this).SetLightsMode(String.valueOf(value));
			try {
				if (HonewellManager.m_Decoder != null)
					HonewellManager.SetScanningSettings();
				else if (MotoSE47XXScanner.mBarCodeReader != null)
					MotoSE47XXScanner.SetScanningSettings();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

			break;
		}

	}

	public void StartService() {
		Intent i = new Intent(this, ScannerServices.class);
		startService(i);
	}

	public void stopService() {

		Intent i = new Intent(this, ScannerServices.class);
		stopService(i);

	}

	public class MyBinder implements CameraListener {

		private static final int CAMERA_CLOSE = 0;// 相机打开
		private static final int CAMERA_OPNEN = 1;// 相机关闭
		CameraMonkey mCameraMonkey;

		public MyBinder(ScannerServices context) {
			mCameraMonkey = new CameraMonkey(context, mHandler);
			mCameraMonkey.addListener(this);
			if (mCameraMonkey != null) {
				if (!mCameraMonkey.getListenState()) {
					mCameraMonkey.startListen();
				}
			}
		}

		@Override
		public void onCameraStateChange(int state) {
			// TODO Auto-generated method stub
			switch (state) {
			case CAMERA_OPNEN:
				setPara(ParamNum.cameraon, 0);
				break;
			case CAMERA_CLOSE:
				setPara(ParamNum.cameraoff, 0);
				break;
			default:
				break;
			}
		}
	}

	private final ScannerAPI.Stub mBinder = new ScannerAPI.Stub() {
		@Override
		public void openScanner() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.barcode, true);
		}

		@Override
		public void closeScanner() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.barcode, false);
		}

		@Override
		public boolean getScannerState() throws RemoteException {
			// TODO Auto-generated method stub
			return m_key_barcodescan;
		}

		@Override
		public boolean getTriggerLockState() throws RemoteException {
			// TODO Auto-generated method stub
			return m_key_lock;
		}

		@Override
		public boolean lockTrigger() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.lockkey, 0);
			return false;
		}

		@Override
		public boolean unlockTrigger() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.unlockkey, 0);
			return true;
		}

		@Override
		public int getOutputMode() throws RemoteException {
			// TODO Auto-generated method stub
			return m_key_broadcast;
		}

		@Override
		public boolean switchOutputMode(int mode) throws RemoteException {
			// TODO Auto-generated method stub
			switch (mode) {
			case 0:
			case 1:
			case 2:
				setPara(ParamNum.broadcast, mode);
				break;
			case 3:
				setPara(ParamNum.beep, true);
				break;
			case 4:
				setPara(ParamNum.beep, false);
				break;
			case 5:
				setPara(ParamNum.vibrate, true);
				break;
			case 6:
				setPara(ParamNum.vibrate, false);
				break;

			}
			return true;
		}

		@Override
		public boolean startDecode() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.startscan, 0);
			return true;
		}

		@Override
		public boolean stopDecode() throws RemoteException {
			// TODO Auto-generated method stub
			setPara(ParamNum.stopscan, 0);
			return true;
		}

	};

}
