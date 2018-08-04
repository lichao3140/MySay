package com.android.auto.iscan;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.android.BarcodeJNI;
import com.android.LedControll;

public class ScanFeedback {

	private boolean mSBeepEnabled = true;
	private boolean mFBeepEnabled = false;
	private boolean mVibrateEnabled = false;
	private Vibrator mVibrator = null;
	private long mVibrateTime = 300L;
	private boolean mLightEnable = true;
	LedControll led = null;
	int dev_num = 0;
	private Context mContext;

	private SoundPool soundpool;
	private HashMap<Integer, Integer> spMap;

	private boolean enbaleLED = false;

	public ScanFeedback(Context context) {
		led = new LedControll(context);
		dev_num = BarcodeJNI.GetPlatform();

		if (dev_num == 3 || dev_num == 2 || dev_num == 6 || dev_num == 7
				|| dev_num == 21 || dev_num == 17) {
			enbaleLED = true;
		} else {
			enbaleLED = false;
		}

		mVibrator = (Vibrator) context.getSystemService("vibrator");
		mContext = context;
		initSoundPool();
	}

	private void initSoundPool() {
		soundpool = new SoundPool(2, AudioManager.STREAM_RING, 0);
		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, soundpool.load(mContext, R.raw.scan, 1));
		spMap.put(2, soundpool.load(mContext, R.raw.scan_abnormal, 1));
	}

	public void setSuccessfulBeepEnabled(boolean enable) {
		this.mSBeepEnabled = enable;
	}

	public void setFailureBeepEnabled(boolean enable) {
		this.mFBeepEnabled = enable;
	}

	public void setVibrateEnabled(boolean enable) {
		this.mVibrateEnabled = enable;
	}

	public void setLightEnabled(boolean enable) {
		this.mLightEnable = enable;
	}

	public void handleSuccessfulScan() {
		if (this.mVibrateEnabled) {
			this.vibrate();
		}
		if (this.mSBeepEnabled) {
			this.Sbeep();
		}
	}

	public void handleFailureScan() {
		if (this.mFBeepEnabled) {
			this.Fbeep();
		}
	}

	private void Fbeep() {
		if (this.soundpool != null) {
			this.soundpool.play(spMap.get(2), 1.0F, 1.0F, 1, 0, 1.0F);
		}
	}

	private void Sbeep() {
		if (this.soundpool != null) {
			this.soundpool.play(spMap.get(1), 1.0F, 1.0F, 1, 0, 1.0F);
		}
	}

	private void vibrate() {
		if (this.mVibrator != null) {
			this.mVibrator.vibrate(this.mVibrateTime);
		}

	}

	public void SetLed_SatrtScan(boolean isCharge) {
		if (mLightEnable) {
			if (enbaleLED && isCharge) {
				led.SetBlueLed(false);
			} else {
				led.SetBlueLed(false);
				led.SetRedLed(true);
			}

		}
	}

	public void SetLed_SatopScan(boolean isCharge) {
		if (mLightEnable) {
			if (enbaleLED && isCharge) {
			} else {
				led.SetRedLed(false);
			}
		}

	}

	public void SetLed_ResultScan(boolean isCharge) {
		if (mLightEnable) {
			if (enbaleLED && isCharge) {
				led.SetBlueLed(true);
			} else {
				led.SetBlueLed(true);
				led.SetRedLed(false);
			}

		}
	}

	public void SetLed_ScanClose(boolean isCharge) {
		if (mLightEnable) {
			if (enbaleLED && isCharge) {
				led.SetBlueLed(false);
			} else {
				led.SetRedLed(false);
				led.SetBlueLed(false);
			}

		}
	}

}
