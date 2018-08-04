package com.android;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.auto.iscan.barcodescanner.BarcodeScanner;
import com.android.auto.iscan.barcodescanner.BaseScan;
import com.zebra.adc.decoder.BarCodeReader1;

public class MotoSE47XXScanner extends BaseScan implements
		BarCodeReader1.PreviewCallback {
	public static BarCodeReader1 mBarCodeReader;
	private static final String TAG = "013";
	private static final int STATE_IDLE = 0;
	private static final int STATE_DECODE = 1;
	private int state = STATE_IDLE;
	private Context mContext;
	static SharedPreferences sharedPrefs;

	BarCodeReader1.Parameters parameters;

	static boolean Securite = false;
	private static String g_strFilePath = Environment
			.getExternalStorageDirectory() + "/DCIM";

	public MotoSE47XXScanner(Context context) {
		super(context);
		mContext = context;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public static void SetParameter() {
		SetParameter(CodeSymbology.DEC_CODE39_ENABLED);
		SetParameter(CodeSymbology.DEC_CODE128_ENABLED);
		SetParameter(CodeSymbology.DEC_EAN8_ENABLED);
		SetParameter(CodeSymbology.DEC_EAN13_ENABLED);
		SetParameter(CodeSymbology.DEC_UPCA_ENABLED);
		SetParameter(CodeSymbology.DEC_UPCE_ENABLED);
		SetParameter(CodeSymbology.DEC_MICROPDF_ENABLED);
		SetParameter(CodeSymbology.DEC_PDF417_ENABLED);
		SetParameter(CodeSymbology.DEC_MAXICODE_ENABLED);
		SetParameter(CodeSymbology.DEC_QR_ENABLED);
		SetParameter(CodeSymbology.DEC_AZTEC_ENABLED);
		SetParameter(CodeSymbology.DEC_CODABAR_ENABLED);
		SetParameter(CodeSymbology.DEC_CODE11_ENABLED);
		SetParameter(CodeSymbology.DEC_CODE93_ENABLED);
		SetParameter(CodeSymbology.DEC_I25_ENABLED);
		SetParameter(CodeSymbology.DEC_DATAMATRIX_ENABLED);
		SetParameter(CodeSymbology.DEC_HANXIN_ENABLED);
		mBarCodeReader.setProperty(147, 1);
		mBarCodeReader.setProperty(128, 1);
		SetScanningSettings();
	}

	public static int SetParameter(int id) {
		switch (id) {
		case CodeSymbology.DEC_CODE39_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code39_enable", true);
			boolean check_enable = sharedPrefs.getBoolean(
					"sym_code39_check_enable", false);
			boolean StartStopCharactersEnabled = sharedPrefs.getBoolean(
					"sym_code39_start_stop_transmit_enable", false);
			boolean asciiModeEnabled = sharedPrefs.getBoolean(
					"sym_code39_fullascii_enable", false);
			SetCode39Properties(enable, asciiModeEnabled,
					StartStopCharactersEnabled, check_enable);
		}
			break;
		case CodeSymbology.DEC_CODE128_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code128_enable", true);
			SetCode128Properties(enable);
		}
			break;

		case CodeSymbology.DEC_EAN8_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_ean8_enable", true);
			boolean check_transmit_enable = sharedPrefs.getBoolean(
					"sym_ean8_check_transmit_enable", true);
			boolean supplementalDecodingEnabled = sharedPrefs.getBoolean(
					"sym_ean8_2_5_digit_addenda_enable", false);
			boolean convertToEAN13Enabled = sharedPrefs.getBoolean(
					"sym_translate_ean8_to_ean13_enable", false);

			SetEAN8Properties(enable, check_transmit_enable,
					convertToEAN13Enabled, supplementalDecodingEnabled);
		}
			break;

		case CodeSymbology.DEC_EAN13_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_ean13_enable", true);
			boolean check_transmit_enable = sharedPrefs.getBoolean(
					"sym_ean13_check_transmit_enable", true);
			boolean supplementalDecodingEnabled = sharedPrefs.getBoolean(
					"sym_ean13_2_5_digit_addenda_enable", false);
			SetEAN13Properties(enable, check_transmit_enable,
					supplementalDecodingEnabled);

		}
			break;

		case CodeSymbology.DEC_UPCA_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_upca_enable", true);

			boolean check_transmit_enable = sharedPrefs.getBoolean(
					"sym_upca_check_transmit_enable", true);

			boolean UPCASystemNumberDigitEnabled = sharedPrefs.getBoolean(
					"sym_upca_sys_num_transmit_enable", true);

			boolean supplementalDecodingEnabled = sharedPrefs.getBoolean(
					"sym_upca_2_5_digit_addenda_enable", false);
			boolean convertToEAN13Enabled = sharedPrefs.getBoolean(
					"sym_translate_upca_to_ean13_enable", false);
			SetUPCAProperties(enable, convertToEAN13Enabled,
					UPCASystemNumberDigitEnabled, check_transmit_enable,
					supplementalDecodingEnabled);

		}
			break;
		case CodeSymbology.DEC_UPCE_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_upce0_enable", true);
			boolean expansionEnabled = sharedPrefs.getBoolean(
					"sym_upce0_upce_expanded_enable", false);
			boolean check_transmit_enable = sharedPrefs.getBoolean(
					"sym_upce0_check_transmit_enable", true);
			boolean UPCESystemNumberDigitEnabled = sharedPrefs.getBoolean(
					"sym_upce0_sys_num_transmit_enable", true);
			boolean supplementalDecodingEnabled = sharedPrefs.getBoolean(
					"sym_upce0_2_5_digit_addenda_enable", false);
			SetUPCEProperties(enable, expansionEnabled, check_transmit_enable,
					UPCESystemNumberDigitEnabled, supplementalDecodingEnabled);
		}
			break;

		case CodeSymbology.DEC_MAXICODE_ENABLED: {
			boolean enable = sharedPrefs
					.getBoolean("sym_maxicode_enable", true);
			SetMaxiCodeProperties(enable);
		}
			break;
		case CodeSymbology.DEC_MICROPDF_ENABLED: {
			boolean enable = sharedPrefs
					.getBoolean("sym_micropdf_enable", true);
			SetMicroPDF417Properties(enable);

		}
			break;

		case CodeSymbology.DEC_PDF417_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_pdf417_enable", true);

			SetPDF417Properties(enable);

		}
			break;

		case CodeSymbology.DEC_QR_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_qr_enable", true);
			SetQRProperties(enable, true, true);
		}
			break;
		case CodeSymbology.DEC_AZTEC_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_aztec_enable", true);
			SetAztecProperties(enable, true);
		}
			break;

		case CodeSymbology.DEC_CODABAR_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_codabar_enable", true);
			boolean check_transmit_enable = sharedPrefs.getBoolean(
					"sym_codabar_check_transmit_enable", false);
			boolean StartStopCharactersEnabled = sharedPrefs.getBoolean(
					"sym_codabar_start_stop_transmit_enable", false);
			SetCodabarProperties(enable, StartStopCharactersEnabled,
					check_transmit_enable);
		}
			break;
		case CodeSymbology.DEC_CODE11_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code11_enable", true);
			boolean check_enable = sharedPrefs.getBoolean(
					"sym_code11_check_enable", false);
			SetCode11Properties(enable, check_enable);
		}
			break;

		case CodeSymbology.DEC_CODE93_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code93_enable", true);
			SetCode93Properties(enable);
		}
			break;
		case CodeSymbology.DEC_I25_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_int25_enable", true);
			boolean check_enable = sharedPrefs.getBoolean(
					"sym_int25_check_enable", false);

			SetInterleaved2of5Properties(enable, check_enable, 0);

		}
			break;

		case CodeSymbology.DEC_HANXIN_ENABLED: {
			// enable, min, max
			boolean enable = sharedPrefs.getBoolean("sym_hanxin_enable", true);
			SetHanXinCodeProperties(enable);
		}
			break;
		case CodeSymbology.DEC_DATAMATRIX_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_datamatrix_enable",
					true);
			SetDataMatrixProperties(enable, true, true);
		}
			break;

		}

		return 0;
	}

	public static void SetScanningSettings() {
		/* Lights Mode */
		String lightsModeString = sharedPrefs.getString("lightsConfig", "0");

		int myLightsMode = Integer.parseInt(lightsModeString);

		mBarCodeReader.setLightsMode(myLightsMode);

		String SecuriteLeve = sharedPrefs.getString("securiteConfig", "0");
		Securite = Integer.parseInt(SecuriteLeve) == 0 ? false : true;
		String lightsintensity = sharedPrefs.getString("lightsintensityConfig",
				"5");
		int intensity = Integer.parseInt(lightsintensity);
		mBarCodeReader.setbrightness(intensity);
		// Log.e("013","myLightsMode:"+myLightsMode+"  Securite::"+Securite+" intensity:"+intensity);
		String temp;
		int nMode = 0;
		temp = sharedPrefs.getString("decode_centering_mode", "0");
		nMode = Integer.parseInt(temp);
		mBarCodeReader.setDecodeWindowMode(nMode);
	}

	@Override
	public boolean doOpen() {
		// TODO Auto-generated method stub

		try {

			mBarCodeReader = BarCodeReader1.open(
					BarCodeReader1.getNumberOfReaders() - 1, mContext);
			if (mBarCodeReader == null) {
				Log.d(TAG, "open failed");
				return false;
			}

			parameters = mBarCodeReader.getParameters();
			if (BarcodeScanner.GetSoftScannerType() == 5) {
				parameters.setPreviewSize(1280, 800);
			} else {
				parameters.setPreviewSize(640, 480);
			}
			mBarCodeReader.setParameters(parameters);
			SetParameter();

			this.state = STATE_IDLE;

		} catch (Exception e) {
			Log.e(TAG, "open excp:" + e);
			mBarCodeReader = null;
			return false;

		}
		// Log.e("013", "open ----");
		return true;
	}

	public static Point GetCurrentPreviewSize() {
		if (mBarCodeReader != null) {
			BarCodeReader1.Size var1 = mBarCodeReader.getParameters()
					.getPreviewSize();
			return new Point(var1.width, var1.height);
		} else {
			return null;
		}
	}

	@Override
	public boolean doClose() {
		// TODO Auto-generated method stub
		if (mBarCodeReader != null) {
			try {
				doStop();
				mBarCodeReader.setOneShotPreviewCallback(null);

				mBarCodeReader.release();
			} catch (Exception var2_1) {
				mBarCodeReader = null;
			}
			mBarCodeReader = null;
		}
		this.state = STATE_IDLE;
		return true;
	}

	@Override
	public void doStart() {
		if (this.state != STATE_IDLE || mBarCodeReader == null)
			return;
		this.state = STATE_DECODE;
		try {

			// Log.e(TAG, "excp:" + 0);
			mBarCodeReader.setOneShotPreviewCallback(this);
			mBarCodeReader.startDecode(true);
			mBarCodeReader.enableDecoding(true);
			return;
		} catch (Exception ex) {
			Log.e(TAG, "excp:" + ex);
			return;
		}
	}

	@Override
	public void doStop() {
		// Log.e(TAG, "doStop" );
		if (mBarCodeReader != null) {
			if (this.state == STATE_DECODE) {
				try {
					mBarCodeReader.stopDecode(true);
					mBarCodeReader.enableDecoding(false);
					mBarCodeReader
							.setOneShotPreviewCallback((BarCodeReader1.PreviewCallback) null);

				} catch (Exception ex) {

					// Log.e(TAG, "excp:" + ex);
				}
			}
			this.state = STATE_IDLE;
		}
	}

	byte[] lastInfo = null;

	@Override
	public void onPreviewFrame(byte[] var1, BarCodeReader1 var2) {
		// TODO Auto-generated method stub
		BarCodeReader1.Parameters parameters = var2.getParameters();
		BarCodeReader1.Size var3 = parameters.getPreviewSize();

		// Log.e("013","var3.height::"+var3.height);
		byte[] var61 = mBarCodeReader.scanner_decode(var1, var3.width,
				var3.height);
		if (var61 != null) {
			if (Securite) {
				if (lastInfo != null) {
					if (Arrays.equals(lastInfo, var61)) {
						mListener.onScanResults(var61);
						mBarCodeReader.stopDecode(true);
						mBarCodeReader.enableDecoding(false);
						state = STATE_IDLE;
						lastInfo = null;

					} else {
						lastInfo = null;
						mBarCodeReader.setOneShotPreviewCallback(this);

					}
				} else {
					lastInfo = var61;
					mBarCodeReader.setOneShotPreviewCallback(this);
				}
			} else {
				if (mListener != null) {
					mListener.onScanResults(var61);
					mBarCodeReader.stopDecode(true);
					mBarCodeReader.enableDecoding(false);
					this.state = STATE_IDLE;
					lastInfo = null;
				}
			}
		} else {
			mBarCodeReader.setOneShotPreviewCallback(this);
		}

	}

	@Override
	public int setParams(int num, int value) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public int getParams(int num) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] GetLastImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled: false int
	 * polarity :2
	 */
	static void SetAztecProperties(boolean enabled,
			boolean mirrorDecodingEnabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(103, var1);
		if (mirrorDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(219, var1);

	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetCanadaPostProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(164, var1);
	}

	/*
	 * default boolean enabled: true boolean stripStartStopCharactersEnabled:
	 * false int checksumProperties :1
	 */
	static void SetCodabarProperties(boolean enabled,
			boolean StartStopCharactersEnabled, boolean checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(114, var1);
		if (StartStopCharactersEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}
		mBarCodeReader.setProperty(260, var1);

		if (checksumProperties) {
			var1 = 2;

		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(202, var1);

	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetCodablockFProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(151, var1);
	}

	/*
	 * default boolean enabled: true boolean stripChecksumDigitsEnabled: false
	 * int checksumProperties :2
	 */
	static void SetCode11Properties(boolean enabled,
			boolean ChecksumDigitsEnabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(140, var1);
		if (ChecksumDigitsEnabled) {
			var1 = 0;
			mBarCodeReader.setProperty(239, 2);
		} else {
			var1 = 0;
			mBarCodeReader.setProperty(239, 0);
		}
		mBarCodeReader.setProperty(249, var1);

	}

	/*
	 * default boolean enabled: true
	 */
	static void SetCode128Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(110, var1);
	}

	/*
	 * default boolean enabled: false
	 */
	void SetCode32Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(141, var1);
	}

	/*
	 * default boolean enabled: true boolean asciiModeEnabled:false boolean
	 * stripStartStopCharactersEnabled:false int checksumProperties: 1
	 */
	static void SetCode39Properties(boolean enabled, boolean asciiModeEnabled,
			boolean StartStopCharactersEnabled, boolean checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(111, var1);
		if (asciiModeEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(203, var1);
		if (StartStopCharactersEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(261, var1);

		if (checksumProperties) {
			var1 = 2;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(200, var1);

	}

	/*
	 * default boolean enabled: false ,can read
	 */
	void SetCode49Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(149, var1);
	}

	/*
	 * default boolean enabled: true
	 */
	static void SetCode93Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(112, var1);
	}

	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled:false boolean
	 * extendedRectEnabled:false int polarity: 2
	 */
	static void SetDataMatrixProperties(boolean enabled,
			boolean mirrorDecodingEnabled, boolean extendedRectEnabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(101, var1);
		if (mirrorDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(217, var1);
		if (extendedRectEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(225, var1);

	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetDutchPostProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(158, var1);
	}

	/*
	 * default boolean enabled: true boolean stripCheckDigitEnabled:true boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetEAN13Properties(boolean enabled, boolean CheckDigitEnabled,
			boolean supplementalDecodingEnabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(117, var1);
		if (supplementalDecodingEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}
		mBarCodeReader.setProperty(206, var1);
		if (CheckDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}
		mBarCodeReader.setProperty(258, var1);
	}

	/*
	 * default boolean enabled: true boolean stripCheckDigitEnabled:true boolean
	 * convertToEAN13Enabled:false boolean supplementalDecodingEnabled:false
	 */
	static void SetEAN8Properties(boolean enabled, boolean CheckDigitEnabled,
			boolean convertToEAN13Enabled, boolean supplementalDecodingEnabled) {
		byte var2 = 1;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(118, var1);
		if (supplementalDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(206, var1);
		if (CheckDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}

		mBarCodeReader.setProperty(259, var1);
		if (convertToEAN13Enabled) {
			var1 = var2;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(251, var1);
	}

	/*
	 * default boolean enabled: false boolean mirrorDecodingEnabled:true int
	 * polarity:2
	 */
	void SetGridMatrixProperties(boolean enabled,
			boolean mirrorDecodingEnabled, int polarity) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(130, var1);
		if (mirrorDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(222, var1);
		switch (polarity) {
		case 1:
			mBarCodeReader.setProperty(214, 1);// GridMatrixPropertiesPolarity_DarkOnLight
			break;
		case 2:
			mBarCodeReader.setProperty(214, -1);// GridMatrixPropertiesPolarity_Either
			break;
		case 3:
			mBarCodeReader.setProperty(214, 0);// GridMatrixPropertiesPolarity_LightOnDark
		}
	}

	/*
	 * default boolean enabled: true
	 */
	static void SetHanXinCodeProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(127, var1);
	}

	/*
	 * default boolean enabled: false
	 */
	void SetHongKong2of5Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(152, var1);
	}

	/*
	 * default boolean enabled: false int checksumProperties:1
	 */
	void SetIATA2of5Properties(boolean enabled, int checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(163, var1);
		switch (checksumProperties) {
		case 1:
			mBarCodeReader.setProperty(240, 0);
			break;
		case 2:
			mBarCodeReader.setProperty(240, 1);
			break;
		case 3:
			mBarCodeReader.setProperty(240, 2);
		}

	}

	/*
	 * default boolean enabled: true int checksumProperties:1 int quietZone:0
	 */
	static void SetInterleaved2of5Properties(boolean enabled,
			boolean checksumProperties, int quietZone) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(113, var1);
		mBarCodeReader.setProperty(204, quietZone);

		if (checksumProperties) {

			var1 = 2;
		} else {

			var1 = 0;
		}
		mBarCodeReader.setProperty(201, var1);

		/*
		 * switch (checksumProperties) { case 1: mBarCodeReader.setProperty(201,
		 * 0); break; case 2: mBarCodeReader.setProperty(201, 1); break; case 3:
		 * mBarCodeReader.setProperty(201, 2); }
		 */
	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetJapanPostProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(159, var1);
	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetKoreaPostProperties(boolean enabled) {

		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(161, var1);

	}

	/*
	 * default boolean enabled: false can use boolean stripChecksumEnabled:false
	 * int checksumProperties:1
	 */
	void SetMSIPlesseyProperties(boolean enabled, boolean stripChecksumEnabled,
			int checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(143, var1);
		if (stripChecksumEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(242, var1);
		switch (checksumProperties) {
		case 1:
			mBarCodeReader.setProperty(241, 0);// MSIPlesseyPropertiesChecksum_Disabled
			break;
		case 2:
			mBarCodeReader.setProperty(241, 1);// MSIPlesseyPropertiesChecksum_EnabledMod10
			break;
		case 3:
			mBarCodeReader.setProperty(241, 2);// MSIPlesseyPropertiesChecksum_EnabledMod10_10
			break;
		case 4:
			mBarCodeReader.setProperty(241, 2);// MSIPlesseyPropertiesChecksum_EnabledMod11_10
		}
	}

	/*
	 * default boolean enabled: false int checksumProperties:1
	 */
	void SetMatrix2of5Properties(boolean enabled, int checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(147, var1);
		switch (checksumProperties) {
		case 1:
			mBarCodeReader.setProperty(240, 0);
			break;
		case 2:
			mBarCodeReader.setProperty(240, 1);
			break;
		case 3:
			mBarCodeReader.setProperty(240, 2);
		}
	}

	/*
	 * default boolean enabled: false
	 */
	static void SetMaxiCodeProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(104, var1);
	}

	/*
	 * default boolean enabled: true
	 */
	static void SetMicroPDF417Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(106, var1);

	}

	/*
	 * default can use boolean enabled: true
	 */
	void SetMicroQRProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(128, var1);
	}

	/*
	 * default can use boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetNEC2of5Properties(boolean enabled, int checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(162, var1);
		switch (checksumProperties) {
		case 1:
			mBarCodeReader.setProperty(240, 0);
			break;
		case 2:
			mBarCodeReader.setProperty(240, 1);
			break;
		case 3:
			mBarCodeReader.setProperty(240, 2);
		}
	}

	/*
	 * default boolean enabled: true
	 */
	static void SetPDF417Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(105, var1);
	}

	/*
	 * default boolean enabled: false 注：代码不需设置这个条码
	 */
	void SetPlesseyProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(142, var1);
	}

	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled:false boolean
	 * model1DecodingEnabled:false int polarity:2
	 */
	static void SetQRProperties(boolean enabled, boolean mirrorDecodingEnabled,
			boolean model1DecodingEnabled) {

		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		// mBarCodeReader.setProperty(128, var1);

		mBarCodeReader.setProperty(102, var1);
		if (model1DecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(129, var1);
		if (mirrorDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(218, var1);
		/*
		 * switch(polarity) { case 1: mBarCodeReader.setProperty(210, 1); break;
		 * case 2: mBarCodeReader.setProperty(210, -1); break; case 3:
		 * mBarCodeReader.setProperty(210, 0); }
		 */
	}

	/*
	 * default boolean enabled: false int checksumProperties:1
	 */
	void SetStraight2of5Properties(boolean enabled, int checksumProperties) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(148, var1);
		switch (checksumProperties) {
		case 1:
			mBarCodeReader.setProperty(240, 0);// Checksum_Disabled
			break;
		case 2:
			mBarCodeReader.setProperty(240, 1);// Checksum_Enabled
			break;
		case 3:
			mBarCodeReader.setProperty(240, 2);// Checksum_EnabledStripCheckCharacter
		}
	}

	/*
	 * default boolean enabled: false int checksumProperties:1
	 */
	void SetTelepenProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(144, var1);
	}

	/*
	 * default boolean enabled: false boolean
	 * stripStartStopCharactersEnabled:false 注：代码不需设置这个条码
	 */
	void SetTriopticProperties(boolean enabled,
			boolean stripStartStopCharactersEnabled) {
		byte var2 = 0;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		mBarCodeReader.setProperty(145, var1);
		if (stripStartStopCharactersEnabled) {
			var1 = var2;
		} else {
			var1 = 1;
		}
		mBarCodeReader.setProperty(262, var1);
	}

	/*
	 * default boolean enabled: true boolean convertToEAN13Enabled:false boolean
	 * stripCheckDigitEnabled: false boolean
	 * stripUPCASystemNumberDigitEnabled:false boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetUPCAProperties(boolean enabled,
			boolean convertToEAN13Enabled, boolean CheckDigitEnabled,
			boolean UPCASystemNumberDigitEnabled,
			boolean supplementalDecodingEnabled) {

		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(115, var1);
		if (supplementalDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(206, var1);
		if (convertToEAN13Enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(250, var1);
		if (UPCASystemNumberDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}

		mBarCodeReader.setProperty(254, var1);
		if (CheckDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}

		mBarCodeReader.setProperty(256, var1);

	}

	/*
	 * default boolean enabled: true boolean expansionEnabled:false boolean
	 * stripCheckDigitEnabled: false boolean
	 * stripUPCESystemNumberDigitEnabled:false boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetUPCEProperties(boolean enabled, boolean expansionEnabled,
			boolean CheckDigitEnabled, boolean UPCESystemNumberDigitEnabled,
			boolean supplementalDecodingEnabled) {

		byte var2 = 1;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(116, var1);
		if (expansionEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(207, var1);
		if (supplementalDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		mBarCodeReader.setProperty(206, var1);
		if (UPCESystemNumberDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}

		mBarCodeReader.setProperty(255, var1);
		if (CheckDigitEnabled) {
			var1 = 0;
		} else {
			var1 = 1;
		}
		mBarCodeReader.setProperty(257, var1);

	}

}