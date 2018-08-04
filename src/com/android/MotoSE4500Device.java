package com.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.auto.iscan.barcodescanner.BaseScan;
import com.zebra.adc.decoder.BarCodeReader;

public class MotoSE4500Device extends BaseScan implements
		BarCodeReader.DecodeCallback {
	private static final String TAG = "012";
	private static final boolean DEBUG = true;

	private Context mContext;
	private static BarCodeReader mBarCodeReader;

	private boolean mScanEnable = false;
	private boolean mIsDecoding = false;

	private int trigMode = BarCodeReader.ParamVal.LEVEL;
	private String decodeStatString;
	// states
	private static final int STATE_IDLE = 0;
	private static final int STATE_DECODE = 1;
	private static final int STATE_HANDSFREE = 2;
	private static final int STATE_PREVIEW = 3; // snapshot preview mode
	private static final int STATE_SNAPSHOT = 4;
	private static final int STATE_VIDEO = 5;

	private int state = STATE_IDLE;

	// 中文编码类型，有些中文二维码可能是gpk编码
	public static final int CODING_FORMAT_UNICODE = 0;
	public static final int CODING_FORMAT_GBK = 1;

	private int mCodingFormat = CODING_FORMAT_UNICODE;
	
	static SharedPreferences sharedPrefs;
	

	// 加载moto解码库
	static {
		try {
			System.loadLibrary("IAL");
			System.loadLibrary("SDL");
			System.loadLibrary("barcodereader44");
		} catch (UnsatisfiedLinkError ule) {
			Log.e(TAG, "WARNING: Could not loadLibrary");
		}
	}

	public MotoSE4500Device(Context context) {
		super(context);
		mContext = context;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	// ----------------------------------------
	private boolean isHandsFree() {
		return (trigMode == BarCodeReader.ParamVal.HANDSFREE);
	}

	// ----------------------------------------
	private boolean isAutoAim() {
		return (trigMode == BarCodeReader.ParamVal.AUTO_AIM);
	}

	public void onDecodeComplete(int symbology, int length, byte[] data,
			BarCodeReader reader) {
		if (state == STATE_DECODE)
			state = STATE_IDLE;
	//	Log.d(TAG, "onDecodeComplete length:%d \n");
		if (length > 0) {

			if (symbology == 0x69) { // signature capture
				if (mListener != null ) {
					try {

						mListener.onScanResults(data);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				mIsDecoding = false;
				decodeStatString += new String(" type: " + symbology + " len: " + length);
				
				// decodeDataString += new String(data);
			} else {
				if (symbology == 0x99) { // type 99?
					symbology = data[0];
					int n = data[1];
					int s = 2;
					int d = 0;
					int len = 0;
					byte d99[] = new byte[data.length];
					for (int i = 0; i < n; ++i) {
						s += 2;
						len = data[s++];
						System.arraycopy(data, s, d99, d, len);
						s += len;
						d += len;
					}
					d99[d] = 0;
					data = d99;
				} else {
					byte[] decodeBuff = new byte[length];
					System.arraycopy(data, 0, decodeBuff, 0, length);
					data = decodeBuff;
				}
				decodeStatString += new String(" type: " + symbology + " len: " + length);
				if (mListener != null) {
					try {
                       
						mListener.onScanResults(data);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
          
				mIsDecoding = false;

			}
			
			if (isHandsFree() == false && isAutoAim() == false) {
				mBarCodeReader.stopDecode();
			}
		} else { // no-decode
			switch (length) {
			case BarCodeReader.DECODE_STATUS_TIMEOUT:
				Log.e(TAG, "decode timed out");
				mIsDecoding = false;
				break;

			case BarCodeReader.DECODE_STATUS_CANCELED:
				Log.e(TAG, "decode cancelled");
				mIsDecoding = false;
				break;

			case BarCodeReader.DECODE_STATUS_ERROR:
				Log.e(TAG, "decode ERROR");
				mIsDecoding = false;
				break;
			default:
				break;
			}
		}
	}

	// ----------------------------------------
	// BarCodeReader.DecodeCallback override
	public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
		switch (event) {
		case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
			// ++modechgEvents;
			// dspStat("Scan Mode Changed Event (#" + modechgEvents + ")");
			break;
		case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
			// ++motionEvents;
			// dspStat("Motion Detect Event (#" + motionEvents + ")");
			break;
		default:
			// process any other events here
			break;
		}
	}

	@Override
	public boolean doOpen() {
		// TODO Auto-generated method stub

		if (mScanEnable) {
			if (DEBUG)
				Log.e(TAG, "already opened");
			return mScanEnable;
		}
		mScanEnable = true;
		try {

			mBarCodeReader = BarCodeReader.open(
					BarCodeReader.getNumberOfReaders() - 1, mContext); // Android															
			if (mBarCodeReader == null) {
				Log.e(TAG, "open failed");
				mScanEnable = false;
				return mScanEnable;
			}
			mBarCodeReader.setDecodeCallback(this);
			// 设置启动顺序为0，不然只能扫描一次。
			doSetParam(765, 0);
			doSetParam(687, 4);
			doSetParam(8610, 1); 
			
			
			
			
		
			//mBarCodeReader.setParameter(1438, 1); // DPM Mode
			//mBarCodeReader.setParameter(537, 2); // DPM Mode
		//	mBarCodeReader.setParameter(429, 4); // external illumination for DPM mode
		//	mBarCodeReader.setParameter(381, 0);
		} catch (Exception e) {
			Log.e(TAG, "open excp:" + e);
			doClose();
		}
		state = STATE_IDLE;
		return mScanEnable;

	}
	
	
	
	public static void SetScanningSettings() {
	
		String lightsintensity = sharedPrefs.getString("lightsintensityConfig",
				"5");
		int intensity = Integer.parseInt(lightsintensity);
		doSetParam(764, intensity);
		// Log.e("013","myLightsMode:"+myLightsMode+"  Securite::"+Securite+" intensity:"+intensity);
		String temp;
		int nMode = 0;
		temp = sharedPrefs.getString("decode_centering_mode", "1");
		nMode = Integer.parseInt(temp);
		doSetParam(402,nMode);
	}

	
	
	@Override
	public boolean doClose() {
		// TODO Auto-generated method stub
		//setIdle(); // suspend operation of Phone can cause state error when
					// decoding
		state = STATE_IDLE;
		mScanEnable = false;
		mIsDecoding = false;
		if (mBarCodeReader != null) {
			mBarCodeReader.release();
			mBarCodeReader = null;
		}
		return true;
	}

	@Override
	public void doStart() {
		// TODO Auto-generated method stub
		if (!mScanEnable) {
			if (DEBUG)
				Log.d(TAG, "scan not opened");
			return;
		}

		if (state != STATE_IDLE) {
			if (DEBUG)
				Log.d(TAG, "state not STATE_IDLE");
			return;
		}

		if (mIsDecoding) {
			if (DEBUG)
				Log.d(TAG, "decoding, please wait");
			return;
		}

		try {
			if (DEBUG)
				Log.d(TAG, "startDecode");
			state = STATE_DECODE;
			mIsDecoding = true;
			mBarCodeReader.startDecode();
		} catch (Exception ex) {
			Log.e(TAG, "excp:" + ex);
		}
	}

	@Override
	public void doStop() {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.d(TAG, "stopDecode");
		if (!mScanEnable) {
			if (DEBUG)
				Log.d(TAG, "scan not open ");
			return;
		}
		/*if (!mIsDecoding) {
			if (DEBUG)
				Log.d(TAG, "not Decoding");
			return;
		}*/
		setIdle();
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

	public static int doSetParam(int num, int val) {
		try {
			int ret = mBarCodeReader.setParameter(num, val);
			return ret;
		} catch (NumberFormatException nx) {
			Log.e(TAG, "set param Exception:" + nx);
		}

		return BarCodeReader.BCR_ERROR;
	}

	// ------------------------------------------
	private int setIdle() {
		int prevState = state;
		int ret = prevState; // for states taking time to chg/end

		state = STATE_IDLE;
		switch (prevState) {
		case STATE_HANDSFREE:
			// resetTrigger();
			// fall thru
		case STATE_DECODE:
			// dspStat("decode stopped");
			mBarCodeReader.stopDecode();
			mIsDecoding = false;
			break;

		case STATE_PREVIEW:
			mBarCodeReader.stopPreview(); // stop preview
			mBarCodeReader.takePicture(null); // end snapshot mode
			break;

		case STATE_VIDEO:
			mBarCodeReader.stopPreview();
			break;

		case STATE_SNAPSHOT:
			ret = STATE_IDLE;
			break;

		default:
			ret = STATE_IDLE;
		}
		return ret;
	}
	
	
	public static void SetParameter() {
		SetParameter(Se4710Symbology.DEC_CODE39_ENABLED);
		SetParameter(Se4710Symbology.DEC_CODE128_ENABLED);
		SetParameter(Se4710Symbology.DEC_EAN8_ENABLED);
		SetParameter(Se4710Symbology.DEC_EAN13_ENABLED);
		SetParameter(Se4710Symbology.DEC_UPCA_ENABLED);
		SetParameter(Se4710Symbology.DEC_UPCE_ENABLED);
		SetParameter(Se4710Symbology.DEC_MICROPDF_ENABLED);
		SetParameter(Se4710Symbology.DEC_PDF417_ENABLED);
		SetParameter(Se4710Symbology.DEC_MAXICODE_ENABLED);
		SetParameter(Se4710Symbology.DEC_QR_ENABLED);
		SetParameter(Se4710Symbology.DEC_AZTEC_ENABLED);
		SetParameter(Se4710Symbology.DEC_CODABAR_ENABLED);
		SetParameter(Se4710Symbology.DEC_CODE11_ENABLED);
		SetParameter(Se4710Symbology.DEC_CODE93_ENABLED);
		SetParameter(Se4710Symbology.DEC_I25_ENABLED);
		SetParameter(Se4710Symbology.DEC_DATAMATRIX_ENABLED);
		SetParameter(Se4710Symbology.DEC_HANXIN_ENABLED);
	     SetScanningSettings();
	}

	public static int SetParameter(int id) {
		switch (id) {
		case Se4710Symbology.DEC_CODE39_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code39_enable", true);
			
			SetCode39Properties(enable);
		}
			break;
		case Se4710Symbology.DEC_CODE128_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code128_enable", true);
			SetCode128Properties(enable);
		}
			break;

		case Se4710Symbology.DEC_EAN8_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_ean8_enable", true);
			
			SetEAN8Properties(enable);
		}
			break;

		case Se4710Symbology.DEC_EAN13_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_ean13_enable", true);
			
			SetEAN13Properties(enable);

		}
			break;

		case Se4710Symbology.DEC_UPCA_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_upca_enable", true);
			
			
			SetUPCAProperties(enable);

		}
			break;
		case Se4710Symbology.DEC_UPCE_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_upce0_enable", true);
			
			SetUPCEProperties(enable);
		}
			break;

		case Se4710Symbology.DEC_MAXICODE_ENABLED: {
			boolean enable = sharedPrefs
					.getBoolean("sym_maxicode_enable", true);
			SetMaxiCodeProperties(enable);
		}
			break;
		case Se4710Symbology.DEC_MICROPDF_ENABLED: {
			boolean enable = sharedPrefs
					.getBoolean("sym_micropdf_enable", true);
			SetMicroPDF417Properties(enable);

		}
			break;

		case Se4710Symbology.DEC_PDF417_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_pdf417_enable", true);

			SetPDF417Properties(enable);

		}
			break;

		case Se4710Symbology.DEC_QR_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_qr_enable", true);
			SetQRProperties(enable);
		}
			break;
		case Se4710Symbology.DEC_AZTEC_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_aztec_enable", true);
			SetAztecProperties(enable);
		}
			break;

		case Se4710Symbology.DEC_CODABAR_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_codabar_enable", true);
			
			//Log.d("012","enable:"+enable+"check_transmit_enable::"+check_transmit_enable+"StartStopCharactersEnabled::"+StartStopCharactersEnabled);
			SetCodabarProperties(enable);
		}
			break;
		case Se4710Symbology.DEC_CODE11_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code11_enable", true);
			
			SetCode11Properties(enable);
		}
			break;

		case Se4710Symbology.DEC_CODE93_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_code93_enable", true);
			SetCode93Properties(enable);
		}
			break;
		case Se4710Symbology.DEC_I25_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_int25_enable", true);
			SetInterleaved2of5Properties(enable);

		}
			break;

		case Se4710Symbology.DEC_HANXIN_ENABLED: {
			// enable, min, max
			boolean enable = sharedPrefs.getBoolean("sym_hanxin_enable", true);
			SetHanXinCodeProperties(enable);
		}
			break;
		case Se4710Symbology.DEC_DATAMATRIX_ENABLED: {
			boolean enable = sharedPrefs.getBoolean("sym_datamatrix_enable",
					true);
			SetDataMatrixProperties(enable);
		}
			break;

		}

		return 0;
	}
	
	
	
	
	
	
	
	
	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled: false int
	 * polarity :2
	 */
	static void SetAztecProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(574, var1);
		

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
		doSetParam(164, var1);
	}

	/*
	 * default boolean enabled: true boolean stripStartStopCharactersEnabled:
	 * false int checksumProperties :1
	 */
	static void SetCodabarProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		doSetParam(7, var1);
		

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
		doSetParam(151, var1);
	}

	/*
	 * default boolean enabled: true boolean stripChecksumDigitsEnabled: false
	 * int checksumProperties :2
	 */
	static void SetCode11Properties(boolean enabled
			) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		doSetParam(10, var1);
		

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
		doSetParam(8, var1);
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
		doSetParam(141, var1);
	}

	/*
	 * default boolean enabled: true boolean asciiModeEnabled:false boolean
	 * stripStartStopCharactersEnabled:false int checksumProperties: 1
	 */
	static void SetCode39Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		doSetParam(0, var1);
		

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
		doSetParam(149, var1);
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
		doSetParam(9, var1);
	}

	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled:false boolean
	 * extendedRectEnabled:false int polarity: 2
	 */
	static void SetDataMatrixProperties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(292, var1);
		
		
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
		doSetParam(158, var1);
	}

	/*
	 * default boolean enabled: true boolean stripCheckDigitEnabled:true boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetEAN13Properties(boolean enabled) {
		byte var2 = 1;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		doSetParam(3, var1);
	}

	/*
	 * default boolean enabled: true boolean stripCheckDigitEnabled:true boolean
	 * convertToEAN13Enabled:false boolean supplementalDecodingEnabled:false
	 */
	static void SetEAN8Properties(boolean enabled) {
		byte var2 = 1;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(4, var1);
		
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

		doSetParam(130, var1);
		if (mirrorDecodingEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
		doSetParam(222, var1);
		switch (polarity) {
		case 1:
			doSetParam(214, 1);// GridMatrixPropertiesPolarity_DarkOnLight
			break;
		case 2:
			doSetParam(214, -1);// GridMatrixPropertiesPolarity_Either
			break;
		case 3:
			doSetParam(214, 0);// GridMatrixPropertiesPolarity_LightOnDark
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

		doSetParam(1167, var1);
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

		doSetParam(152, var1);
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

		doSetParam(163, var1);
		switch (checksumProperties) {
		case 1:
			doSetParam(240, 0);
			break;
		case 2:
			doSetParam(240, 1);
			break;
		case 3:
			doSetParam(240, 2);
		}

	}

	/*
	 * default boolean enabled: true int checksumProperties:1 int quietZone:0
	 */
	static void SetInterleaved2of5Properties(boolean enabled) {
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}
	    doSetParam(6, var1);
	    doSetParam(22, 4);
	    doSetParam(23, 50);
	
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
		doSetParam(159, var1);
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

		doSetParam(161, var1);

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

		doSetParam(143, var1);
		if (stripChecksumEnabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(242, var1);
		
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

		doSetParam(618, var1);
	
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

		doSetParam(294, var1);
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
		doSetParam(227, var1);
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
		doSetParam(128, var1);
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

		doSetParam(162, var1);
		switch (checksumProperties) {
		case 1:
			doSetParam(240, 0);
			break;
		case 2:
			doSetParam(240, 1);
			break;
		case 3:
			doSetParam(240, 2);
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
		doSetParam(15, var1);
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
		doSetParam(142, var1);
	}

	/*
	 * default boolean enabled: true boolean mirrorDecodingEnabled:false boolean
	 * model1DecodingEnabled:false int polarity:2
	 */
	static void SetQRProperties(boolean enabled) {

		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(293, var1);
		
	}

	
	
	

	/*
	 * default boolean enabled: true boolean convertToEAN13Enabled:false boolean
	 * stripCheckDigitEnabled: false boolean
	 * stripUPCASystemNumberDigitEnabled:false boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetUPCAProperties(boolean enabled) {
	
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(1, var1);
		

	}

	/*
	 * default boolean enabled: true boolean expansionEnabled:false boolean
	 * stripCheckDigitEnabled: false boolean
	 * stripUPCESystemNumberDigitEnabled:false boolean
	 * supplementalDecodingEnabled:false
	 */
	static void SetUPCEProperties(boolean enabled) {

		byte var2 = 1;
		byte var1;
		if (enabled) {
			var1 = 1;
		} else {
			var1 = 0;
		}

		doSetParam(2, var1);
		doSetParam(12, var1);
		
	}
	
	
	
	

}
