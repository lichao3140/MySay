package com.android.auto.iscan;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.BarcodeControll;
import com.android.auto.iscan.activity.AboutActivity;
import com.android.auto.iscan.utility.ParamNum;

public class ScannnerHandler extends Handler {

	private ScannerServices activity = null;

	ScannnerHandler(ScannerServices activity) {

		this.activity = activity;
	}

	private String keychar[] = { "", "GB2312", "GBK", "GB18030", "UTF-8",
			"ISO-8859-1", "BIG5", "SJIS", "EUC-JP"

	};

	public void SendBraodcast() {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.SCANRESULT");
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.putExtra("value", "");
		localIntent.putExtra("length", 0);
		activity.sendBroadcast(localIntent);
	}

	public void handleMessage(Message message) {

		switch (message.what) {
		case BarcodeControll.BARCODE_READ: {
			String result = "";
			String result1 = "";

			if (message.obj instanceof String) {
				result = activity.mLabelPrefix + message.obj
						+ activity.mLabelSuffix;

			} else {
				if (activity.m_key_charset == 0) {
					result = activity.mLabelPrefix
							+ ChineseHandle((byte[]) message.obj)
							+ activity.mLabelSuffix;
				} else {

					try {
						result = activity.mLabelPrefix
								+ new String((byte[]) message.obj,
										keychar[activity.m_key_charset])
								+ activity.mLabelSuffix;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			int len = result.length();
			if (len - activity.mLabelright > activity.mLabelleft) {
				result1 = result.substring(activity.mLabelleft, len
						- activity.mLabelright);
				result = String.format("%s", result1);
			}
			int decodetime = message.arg1;
			activity.HandleBacodeResult(result, decodetime);
			activity.mHandler.sendEmptyMessageDelayed(ParamNum.LedClose, 300);
			break;
		}
		case BarcodeControll.BARCODE_DECODE_FAILURE: {
			if (activity.m_key_broadcast == 1 && activity.mErrorBroadCast) {
				SendBraodcast();
			}
			activity.scanFeedback.handleFailureScan();
			break;
		}

		case ParamNum.LedClose:
			activity.scanFeedback.SetLed_ScanClose(activity.isCharging);
			break;
		case ParamNum.screenon:
			activity.scannerManager.setBarcodeScanEnabled(true);
			if (activity.m_key_continuscan)
				activity.scannerManager.setContinuousScanEnabled(true,
						activity.mIntervalTime);
			break;
		case ParamNum.screenoff:
			activity.scannerManager.setBarcodeScanEnabled(false);
			if (activity.m_key_continuscan)
				activity.scannerManager.setContinuousScanEnabled(false,
						activity.mIntervalTime);
			activity.scanFeedback.SetLed_ScanClose(activity.isCharging);
			break;

		case ParamNum.showui:
			Intent intent = new Intent();
			intent.setClass(activity, AboutActivity.class);
			activity.startActivity(intent);
			break;

		}
	}

	private static String ChineseHandle(byte[] arraydata) {
		String str01 = "";
		if (isUTF8(arraydata)) {
			str01 = Uft8toString(arraydata);
			;

		} else {
			str01 = DecodetoString(arraydata);
		}
		return str01;
	}

	/**
	 * function:DecodetoString
	 * 
	 * @param value
	 *            [] id:=0,reservd,=1,Chinese ,=2,Janpaese
	 * @return
	 */
	private static String DecodetoString(byte[] value) {

		String str = null;
		try {
			str = new String(value, "gb2312");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static boolean isUTF8(byte[] rawtext) {
		int score = 0;
		int i, rawtextlen = 0;
		int goodbytes = 0, asciibytes = 0;
		// Maybe also use UTF8 Byte Order Mark: EF BB BF
		// Check to see if characters fit into acceptable ranges
		rawtextlen = rawtext.length;
		for (i = 0; i < rawtextlen; i++) {
			if ((rawtext[i] & (byte) 0x7F) == rawtext[i]) {
				// 最高位是0的ASCII字符
				asciibytes++;
				// Ignore ASCII, can throw off count
			} else if (-64 <= rawtext[i]
					&& rawtext[i] <= -33
					// -0x40~-0x21
					&& // Two bytes
					i + 1 < rawtextlen && -128 <= rawtext[i + 1]
					&& rawtext[i + 1] <= -65) {
				goodbytes += 2;
				i++;
			} else if (-32 <= rawtext[i]
					&& rawtext[i] <= -17
					&& // Three bytes
					i + 2 < rawtextlen && -128 <= rawtext[i + 1]
					&& rawtext[i + 1] <= -65 && -128 <= rawtext[i + 2]
					&& rawtext[i + 2] <= -65) {
				goodbytes += 3;
				i += 2;
			}
		}
		if (asciibytes == rawtextlen) {
			return false;
		}
		score = 100 * goodbytes / (rawtextlen - asciibytes);
		// If not above 98, reduce to zero to prevent coincidental matches
		// Allows for some (few) bad formed sequences
		if (score > 98) {
			return true;
		} else if (score > 95 && goodbytes > 30) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * function:UTF8 to String
	 * 
	 * @param value
	 * @return
	 */
	private static String Uft8toString(byte[] value) {

		String str = null;
		try {
			str = new String(value, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
