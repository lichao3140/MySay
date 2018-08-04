package com.android;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.auto.iscan.barcodescanner.BaseScan;
import com.android.auto.iscan.utility.Variable;
import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.DecodeWindowing.DecodeWindowMode;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues.LightsMode;
import com.hsm.barcode.DecoderConfigValues.SymbologyFlags;
import com.hsm.barcode.DecoderConfigValues.SymbologyID;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.DecoderException.ResultID;
import com.hsm.barcode.DecoderListener;
import com.hsm.barcode.ExposureValues.ExposureMode;
import com.hsm.barcode.ExposureValues.ExposureSettings;
import com.hsm.barcode.ImageAttributes;
import com.hsm.barcode.SymbologyConfig;


public class HonewellManager extends BaseScan implements DecoderListener {

	private DecodeResult m_decResult = null; // Result object
	private static boolean bOkToScan = false; // Flag to start scanning
	private static boolean bDecoding = false; // Flag to start decoding
	private static boolean bRunThread = false; // Flag to run thread
	private static boolean bThreadDone = true; // Flag to signal thread done

	public boolean g_bKeepGoing = true; // for trigger callback
	public boolean bTriggerReleased = true;
	public static Decoder m_Decoder = null; // Decoder object
	public int g_nImageWidth = 0; // Global image width
	public int g_nImageHeight = 0; // Global image height
	private static int g_nDecodeTimeout = 10 * 1000; // Decode timeout 10
														// seconds
	private static boolean bSaveImage = false;
	private static String g_strFilePath = Environment
			.getExternalStorageDirectory() + "/DCIM";
	static SharedPreferences sharedPrefs;
	int keep = 0;
	static String TAG = "012";
	
	public HonewellManager(Context context) {
		super(context);
		mContext = context;
		m_decResult = new DecodeResult();

	}

	@Override
	public boolean onKeepGoingCallback() {
		// TODO Auto-generated method stub
		return (g_bKeepGoing);
	}

	@Override
	public boolean onMultiReadCallback() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doOpen() {
		
		
		m_Decoder = Decoder.open(Decoder.getNumberOfCameras() - 1, mContext);
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);	
		try {
			m_Decoder.connectDecoderLibrary();
			g_nImageWidth = m_Decoder.getImageWidth();
			g_nImageHeight = m_Decoder.getImageHeight();
			new Thread(new Task()).start();
			bRunThread = true;
		//	disableAllSymbologies();
		  //  m_Decoder.disableSymbology(34);
		 //   m_Decoder.disableSymbology(SymbologyID.SYM_ISBT);
			//m_Decoder.enableSymbology(100);

			setSymbologySettings();
			SetDecodingSettings();
			SetScanningSettings();
			m_Decoder.setDecoderListeners(this);

		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static byte[] ConvertOCRTemplateStringToByteArray(String var1) {
		String[] var2 = var1.split(",");
		byte[] var3 = new byte[var2.length];

		for (int var4 = 0; var4 < var2.length; ++var4) {
			var3[var4] = (byte) Integer.parseInt(var2[var4]);
		}

		return var3;
	}

	@Override
	public boolean doClose() {
		// TODO Auto-generated method stub
		//writeFileSdcardFile("/data/wificfg/N6603.conf","1");
		doStop();
		if (bDecoding)
			while (bDecoding)
				;
		if (!bDecoding)
			Log.d(TAG, "...done waiting for scan stop");
		try {
			
			m_Decoder.disconnectDecoderLibrary();
			//writeFileSdcardFile("/data/wificfg/N6603.conf","0");
		} catch (DecoderException e) {

		}
		bThreadDone = true;
		bRunThread = false;
		while (!bThreadDone)
			Log.d(TAG, "waiting for thread to stop...");
		m_Decoder = null;
		return false;
	}

	public void doStart() {
		// TODO Auto-generated method stub
		Log.d(TAG, "doStart");
		if (m_Decoder == null) {
			return;
		}
		if (bOkToScan == false) {
			bOkToScan = true;
			g_bKeepGoing = true;
		} else
			Log.d(TAG, "unable to start scanning");

	}

	@Override
	public void doStop() {
		// TODO Auto-generated method stub
		Log.d(TAG, "doStop");
		if (m_Decoder == null) {
			return;
		}
		bOkToScan = false;
		g_bKeepGoing = false; // KeepGoing is false for trigger callback

	}

	public void HandleDecoderException(final DecoderException e) {
		new Thread() {
			public void run() {
				switch (e.getErrorCode()) {

				case ResultID.RESULT_ERR_NOTCONNECTED:

					break;
				case ResultID.RESULT_ERR_NOIMAGE:
					try {
						//m_Decoder.disconnectDecoderLibrary();
						//m_Decoder.connectDecoderLibrary();
					} catch (Exception e) {
						//HandleDecoderException(e);
					}
					break;
				default:

				}
			}

		}.start();

	}

	/**
	 * Decode Thread
	 * 
	 */

	class Task implements Runnable {
		@Override
		public void run() {

			bThreadDone = false;

			//Log.d(TAG, "***** DECODE THREAD IS RUNNING *****");

			while (bRunThread) { // forever?

				try {

					Thread.sleep(10); // TODO: sleep for 50 ms before doing
										// again?

					if (bOkToScan) {
						// Log. d(TAG, "OK to scan...");
						bOkToScan = false; // don't scan again until told to

						if (!bDecoding) {
							bDecoding = true;

							try {
								m_Decoder.waitForDecodeTwo(g_nDecodeTimeout,
										m_decResult); // wait for decode with
														// results arg

							} catch (DecoderException e) {
								HandleDecoderException(e);
							}

							//Log.d(TAG, "waitForDecodeTwo returned");

							DisplayDecodeResults();

							bDecoding = false;
						}

					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			bThreadDone = true;

			Log.d(TAG, "!!!!! DECODE THREAD HAS STOPPED RUNNING !!!!!");
		}

	}
	public byte[] GetLastImage(){
		ImageAttributes attr = new ImageAttributes();
		byte[] image = null;
		try {
			image = m_Decoder.getLastImage(attr);
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
		}
		return image;
	}

	public void SaveImage(String filename) {
		ImageAttributes attr = new ImageAttributes();
		byte[] image = null;
		try {
			image = m_Decoder.getLastImage(attr);
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
		}
		if (image != null)
			StoreByteImage(image, filename, 100, g_strFilePath);
	}

	private void DisplayDecodeResults() {
		if (mListener != null && m_decResult.length > 0) {
			try {
				
				//Log.d("013", "MAX barcodeData----!!" + m_decResult.barcodeData.length());
				if(m_decResult.barcodeData.length()!=0)
				{
        			mListener.onScanResults(m_decResult.barcodeData);
				}
				else
				{
					mListener.onScanResults(m_Decoder.getBarcodeByteData());
				}

				
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	

	void enableAllSymbologies() {
		try {
			m_Decoder.enableSymbology(SymbologyID.SYM_ALL);
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void disableAllSymbologies() {
		try {
			m_Decoder.disableSymbology(SymbologyID.SYM_ALL);
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int setParams(int num, int value) {
		// TODO Auto-generated method stub
		
		ScanningSettings();
		
		return 0;
	}

	@Override
	public int getParams(int num) {
		// TODO Auto-generated method stub
		return 0;
	}

	

	
	public static void setImagePath(String path, boolean enable) {

		bSaveImage = enable;
		g_strFilePath = path;
	}

	public boolean StoreByteImage(byte[] imageData, String imagename,
			int quality, String path) {
		File imageFilePath = new File(path);
		if (!imageFilePath.exists()) {
			imageFilePath.mkdirs();
		}
		FileOutputStream fileOutputStream1 = null;
		BufferedOutputStream bos = null;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			int width = g_nImageWidth;
			int height = g_nImageHeight;
			Bitmap myBitMap = Bitmap.createBitmap(width, height,
					Bitmap.Config.RGB_565);
			byte[] buffer = new byte[m_Decoder.getImageHeight()
					* m_Decoder.getImageWidth()];
			ImageAttributes attr = new ImageAttributes();
			buffer = m_Decoder.getLastImage(attr);
			// FIXME:
			int[] array = new int[width * height * 2];
			int cnt = 0;
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					array[cnt] = buffer[width * h + w] * 0x00010101;
					cnt += 1;
				}
			}
			myBitMap.setPixels(array, 0, width, 0, 0, width, height);

			String myNameFile = imageFilePath.toString() + "/" + imagename
					+ ".jpg";
			fileOutputStream1 = new FileOutputStream(myNameFile);
			bos = new BufferedOutputStream(fileOutputStream1);
			myBitMap.compress(Bitmap.CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (DecoderException e) {

			return false;
		}
		return true;
	}

	public static void SetOcrSettings() {

		int ocr_mode = 0;
		int ocr_template = 0;
		byte[] ocr_user_defined_template;
		String temp;
		if (sharedPrefs.getBoolean("sym_ocr_enable", false)) {

			// mode (enable)
			temp = sharedPrefs.getString("sym_ocr_mode_config", "1");
			ocr_mode = Integer.parseInt(temp);
			// ocr template
			temp = sharedPrefs.getString("sym_ocr_template_config", "1");
			ocr_template = Integer.parseInt(temp);
			// user defined template
			temp = sharedPrefs.getString("sym_ocr_user_template",
					"1,2,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,8,0");
			
			//temp = sharedPrefs.getString("sym_ocr_user_template",
		//			"1,3,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,2,3,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,3,3,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,0");
			ocr_user_defined_template = ConvertOCRTemplateStringToByteArray(temp);// temp.getBytes();
			try {
				m_Decoder.setOCRMode(ocr_mode);
				m_Decoder.setOCRTemplates(ocr_template);
				m_Decoder.setOCRUserTemplate(ocr_user_defined_template);
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				m_Decoder.setOCRMode(0);
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	int g_nExposureSettings[] = {

	ExposureSettings.DEC_ES_TARGET_VALUE, 0,
			ExposureSettings.DEC_ES_TARGET_PERCENTILE, 0,
			ExposureSettings.DEC_ES_TARGET_ACCEPT_GAP, 0,
			ExposureSettings.DEC_ES_MAX_EXP, 0,
			ExposureSettings.DEC_ES_MAX_GAIN, 0,

			ExposureSettings.DEC_ES_CONFORM_IMAGE, 0,
			ExposureSettings.DEC_ES_CONFORM_TRIES, 0,

			ExposureSettings.DEC_ES_FIXED_EXP, 0,
			ExposureSettings.DEC_ES_FIXED_GAIN, 0,
			ExposureSettings.DEC_ES_FIXED_FRAME_RATE, 0, };

	/**
	 * Sets the Decoder settings based on user preferences
	 * 
	 * @throws DecoderException
	 * 
	 */
	static void SetDecodingSettings() throws DecoderException {
		Log.d(TAG, "SetDecodingSettings++");

		String temp;

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	
		int nMode = DecodeWindowMode.DECODE_WINDOW_MODE_DISABLED;
		
		temp = sharedPrefs.getString("decode_centering_mode", "1");
		nMode = Integer.parseInt(temp);
		m_Decoder.setDecodeWindowMode(nMode);

	}

	/**
	 * Sets the Scanning settings based on user preferences
	 * 
	 * @throws DecoderException
	 * @throws NumberFormatException
	 * 
	 */
	public static void SetScanningSettings() throws NumberFormatException,
			DecoderException {
		Log.d(TAG, "SetScanningSettings++");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		String temp = sharedPrefs.getString("prefExposureMode",
				Integer.toString(ExposureMode.HHP));
		m_Decoder.setExposureMode(Integer.parseInt(temp));

	//	int g_nExposureSettings = Integer.parseInt(sharedPrefs.getString(
	//			"exposure_fixed_exposure", "80"));
		int g_nExposureSettings = Integer.parseInt(Variable.getInstance(mContext).GetBrghtnessValue()
        );
		Log.d("012", "g_nExposureSettings++::::" + g_nExposureSettings
				+ "   (temp):::" + Integer.parseInt(temp));

		if (temp.equals(Integer.toString(ExposureMode.HHP))) {
			int expSettingsArray[] = { ExposureSettings.DEC_ES_MAX_EXP,
					g_nExposureSettings };
			m_Decoder.setExposureSettings(expSettingsArray);
		} else {
			int expSettingsArray[] = { ExposureSettings.DEC_ES_FIXED_EXP,
					g_nExposureSettings };
			m_Decoder.setExposureSettings(expSettingsArray);

		}
		int myLightsMode = LightsMode.ILLUM_AIM_ON;
		String lightsModeString = prefs.getString("lightsConfig", "3");
		myLightsMode = Integer.parseInt(lightsModeString);
		m_Decoder.setLightsMode(myLightsMode);
		
		
		boolean fastmod = sharedPrefs.getBoolean("fast_scan_enable", true);
		
	
		    m_Decoder.SetFastMode(fastmod);
		 
		Log.d(TAG, "SetScanningSettings--");
	}

	public static void ScanningSettings() {
		try {
			SetDecodingSettings();
			SetScanningSettings();
			setSymbologySettings();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void setSymbologySettings() //throws DecoderException
	{
		Log. e(TAG, "SetSymbologySettings++");
		
		int flags = 0;											// flags config
		int min = 0;											// minimum length config
		int max = 0;											// maximum length config
		int postal_config = 0;									// postal config
		String temp;											// temp string for converting string to int
		SymbologyConfig symConfig = new SymbologyConfig(0);		// symbology config
		int min_default, max_default;
		String strMinDefault = null;
		String strMaxDefault = null;
		boolean bNotSupported = false;
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		for(int i = 0; i < SymbologyID.SYM_ALL; i++)
		{
			
			symConfig.symID = i;								// symID
			//if( i != SymbologyID.SYM_OCR &&
			//	i != SymbologyID.SYM_POSTALS )
			//m_Decoder.getSymbologyConfig(symConfig,false); // gets the current symConfig
			flags = 0;											// reset the flags
					
			// Set appropriate sym config mask...
			switch(i)
			{
				// Flag & Range:
				case SymbologyID.SYM_AZTEC:
		        case SymbologyID.SYM_CODABAR:
		        case SymbologyID.SYM_CODE11:
		        case SymbologyID.SYM_CODE128:
				case SymbologyID.SYM_GS1_128:
		        case SymbologyID.SYM_CODE39:
		        case SymbologyID.SYM_CODE93:
		        case SymbologyID.SYM_COMPOSITE:
		        case SymbologyID.SYM_DATAMATRIX:
		        case SymbologyID.SYM_INT25:
		        case SymbologyID.SYM_MAXICODE:
		        case SymbologyID.SYM_MICROPDF:
		        case SymbologyID.SYM_PDF417:
		        case SymbologyID.SYM_QR:
		        case SymbologyID.SYM_RSS:
		        case SymbologyID.SYM_IATA25:
		        case SymbologyID.SYM_CODABLOCK:
		        case SymbologyID.SYM_MSI:
		        case SymbologyID.SYM_MATRIX25:
		        case SymbologyID.SYM_KOREAPOST:
		        case SymbologyID.SYM_STRT25:
		        //case SymbologyID.SYM_PLESSEY: 	// not supported
		        case SymbologyID.SYM_CHINAPOST:
		        case SymbologyID.SYM_TELEPEN:
		        //case SymbologyID.SYM_CODE16K: 	// not supported
		        //case SymbologyID.SYM_POSICODE:	// not supported
				case SymbologyID.SYM_HANXIN:
				//case SymbologyID.SYM_GRIDMATRIX:	// not supported
					try
					{
						m_Decoder.getSymbologyConfig(symConfig); // gets the current symConfig
						min_default = m_Decoder.getSymbologyMinRange(i); strMinDefault = Integer.toString(min_default);
						max_default = m_Decoder.getSymbologyMaxRange(i); strMaxDefault = Integer.toString(max_default);
						
						//Log.d("012","min_default:"+strMinDefault+"  max_default:::"+max_default+"   symID::"+symConfig.symID);
					}
					catch(DecoderException e)
					{
						
					}
					symConfig.Mask = SymbologyFlags.SYM_MASK_FLAGS| SymbologyFlags.SYM_MASK_MIN_LEN | SymbologyFlags.SYM_MASK_MAX_LEN;
					break;
				// Flags Only:
				case SymbologyID.SYM_EAN8:
		        case SymbologyID.SYM_EAN13:
		        case SymbologyID.SYM_POSTNET:
		        case SymbologyID.SYM_UPCA:
		        case SymbologyID.SYM_UPCE0:
		        case SymbologyID.SYM_UPCE1:
		      //  case SymbologyID.SYM_ISBT:
		        case SymbologyID.SYM_BPO:
		        case SymbologyID.SYM_CANPOST:
		        case SymbologyID.SYM_AUSPOST:
		        case SymbologyID.SYM_JAPOST:
		        case SymbologyID.SYM_PLANET:
		        case SymbologyID.SYM_DUTCHPOST:
		        case SymbologyID.SYM_TLCODE39:
		        case SymbologyID.SYM_TRIOPTIC:
		        case SymbologyID.SYM_CODE32:
		        case SymbologyID.SYM_COUPONCODE:
				case SymbologyID.SYM_USPS4CB:
				case SymbologyID.SYM_IDTAG:
				//case SymbologyID.SYM_LABEL:		// not supported
				case SymbologyID.SYM_US_POSTALS1:
					try
					{
						m_Decoder.getSymbologyConfig(symConfig); // gets the current symConfig
					}
					catch(DecoderException e)
					{
						
					}
					symConfig.Mask = SymbologyFlags.SYM_MASK_FLAGS;
					break;
				// default:
				default:
					// invalid / not supported
					bNotSupported = true;
					break;
			}
			
			// Set symbology config...
			switch(i)
			{
				case SymbologyID.SYM_AZTEC:
					// enable
					flags |= sharedPrefs.getBoolean("sym_aztec_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					// min, max
					temp = sharedPrefs.getString("sym_aztec_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_aztec_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CODABAR:
		        	// enable, check char, start/stop transmit, codabar concatenate
		        	flags |= sharedPrefs.getBoolean("sym_codabar_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_codabar_check_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_ENABLE : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_codabar_check_transmit_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_codabar_start_stop_transmit_enable", false) ? SymbologyFlags.SYMBOLOGY_START_STOP_XMIT : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_codabar_concatenate_enable", false) ? SymbologyFlags.SYMBOLOGY_CODABAR_CONCATENATE : 0;	
		        	// min, max
		        	temp = sharedPrefs.getString("sym_codabar_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_codabar_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CODE11:
					// enable, check char
		        	flags |= sharedPrefs.getBoolean("sym_code11_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_code11_check_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_code11_min", strMinDefault); min = Integer.parseInt(temp); 
		        	temp = sharedPrefs.getString("sym_code11_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CODE128:
		        	// enable
					flags |= sharedPrefs.getBoolean("sym_code128_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					// min, max
					temp = sharedPrefs.getString("sym_code128_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_code128_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
				case SymbologyID.SYM_GS1_128:
		        	// enable
					flags |= sharedPrefs.getBoolean("sym_gs1_128_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					// min, max
					temp = sharedPrefs.getString("sym_gs1_128_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_gs1_128_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CODE39:
		        	// enable, check char, start/stop transmit, append, full ascii
		        	flags |= sharedPrefs.getBoolean("sym_code39_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_code39_check_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_ENABLE : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_code39_check_transmit_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_code39_start_stop_transmit_enable", false) ? SymbologyFlags.SYMBOLOGY_START_STOP_XMIT : 0;
		        	//flags |= sharedPrefs.getBoolean("sym_code39_append_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE_APPEND_MODE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_code39_fullascii_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE_FULLASCII : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_code39_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_code39_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		      
		        case SymbologyID.SYM_CODE93:
		        	// enable
					flags |= sharedPrefs.getBoolean("sym_code93_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					// min, max
					temp = sharedPrefs.getString("sym_code93_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_code93_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_COMPOSITE:
		        	// enable, composit upc
		        	flags |= sharedPrefs.getBoolean("sym_composite_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_composite_upc_enable", false) ? SymbologyFlags.SYMBOLOGY_COMPOSITE_UPC : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_composite_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_composite_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_DATAMATRIX:
					// enable
					flags |= sharedPrefs.getBoolean("sym_datamatrix_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					// min, max
					temp = sharedPrefs.getString("sym_datamatrix_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_datamatrix_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
				case SymbologyID.SYM_EAN8:
					// enable, check char transmit, addenda separator, 2 digit addena, 5 digit addena, addena required
					flags |= sharedPrefs.getBoolean("sym_ean8_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					flags |= sharedPrefs.getBoolean("sym_ean8_check_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
					flags |= sharedPrefs.getBoolean("sym_ean8_addenda_separator_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_SEPARATOR : 0;
					flags |= sharedPrefs.getBoolean("sym_ean8_2_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_2_DIGIT_ADDENDA: 0;
					flags |= sharedPrefs.getBoolean("sym_ean8_5_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_5_DIGIT_ADDENDA : 0;
					flags |= sharedPrefs.getBoolean("sym_ean8_addenda_required_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_REQUIRED : 0;
					break;
		        case SymbologyID.SYM_EAN13:
		        	// enable, check char transmit, addenda separator, 2 digit addena, 5 digit addena, addena required
					flags |= sharedPrefs.getBoolean("sym_ean13_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					flags |= sharedPrefs.getBoolean("sym_ean13_check_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
					flags |= sharedPrefs.getBoolean("sym_ean13_addenda_separator_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_SEPARATOR : 0;
					flags |= sharedPrefs.getBoolean("sym_ean13_2_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_2_DIGIT_ADDENDA: 0;
					flags |= sharedPrefs.getBoolean("sym_ean13_5_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_5_DIGIT_ADDENDA : 0;
					flags |= sharedPrefs.getBoolean("sym_ean13_addenda_required_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_REQUIRED : 0;
					break;
		        case SymbologyID.SYM_INT25:
					// enable, check enable, check transmit
		        	flags |= sharedPrefs.getBoolean("sym_int25_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_int25_check_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_ENABLE : 0;
					flags |= sharedPrefs.getBoolean("sym_int25_check_transmit_enable", false) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
					// min, max
					temp = sharedPrefs.getString("sym_int25_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_int25_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_MAXICODE:
					// enable
		        	flags |= sharedPrefs.getBoolean("sym_maxicode_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_maxicode_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_maxicode_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_MICROPDF:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_micropdf_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_micropdf_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_micropdf_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_PDF417:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_pdf417_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_pdf417_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_pdf417_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_QR:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_qr_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_qr_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_qr_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_HANXIN:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_hanxin_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_hanxin_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_hanxin_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_RSS:
		        	// rss enable, rsl enable, rse enable
		        	flags |= sharedPrefs.getBoolean("sym_rss_rss_enable", false) ? SymbologyFlags.SYMBOLOGY_RSS_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_rss_rsl_enable", false) ? SymbologyFlags.SYMBOLOGY_RSL_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_rss_rse_enable", false) ? SymbologyFlags.SYMBOLOGY_RSE_ENABLE : 0;
		        	// min, max
					temp = sharedPrefs.getString("sym_rss_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_rss_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_UPCA:
					// enable, check transmit, sys num transmit, addenda separator, 2 digit addenda, 5 digit addenda, addenda required
		        	flags |= sharedPrefs.getBoolean("sym_upca_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_check_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_sys_num_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_NUM_SYS_TRANSMIT : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_addenda_separator_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_SEPARATOR : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_2_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_2_DIGIT_ADDENDA : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_5_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_5_DIGIT_ADDENDA : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upca_addenda_required_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_REQUIRED : 0;
		        	flags |= sharedPrefs.getBoolean("sym_translate_upca_to_ean13_enable", false) ? SymbologyFlags.SYMBOLOGY_UPCA_TRANSLATE_TO_EAN13 : 0;		        
		        	break;
		        case SymbologyID.SYM_UPCE1:
		        	// upce1 enable
		        	flags |= sharedPrefs.getBoolean("sym_upce1_upce1_enable", true) ? SymbologyFlags.SYMBOLOGY_UPCE1_ENABLE : 0;
					break;
		        case SymbologyID.SYM_UPCE0:
		        	// enable, upce expanded, char char transmit, num sys transmit, addenda separator, 2 digit addenda, 5 digit addenda, addenda required
		        	flags |= sharedPrefs.getBoolean("sym_upce0_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_upce_expanded_enable", false) ? SymbologyFlags.SYMBOLOGY_EXPANDED_UPCE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_check_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_sys_num_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_NUM_SYS_TRANSMIT : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_addenda_separator_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_SEPARATOR : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_2_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_2_DIGIT_ADDENDA : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_5_digit_addenda_enable", false) ? SymbologyFlags.SYMBOLOGY_5_DIGIT_ADDENDA : 0;
		        	flags |= sharedPrefs.getBoolean("sym_upce0_addenda_required_enable", false) ? SymbologyFlags.SYMBOLOGY_ADDENDA_REQUIRED : 0;
					break;
//		        case SymbologyID.SYM_ISBT:
//					// enable
//		        	flags |= sharedPrefs.getBoolean("sym_isbt_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
//					break;
		        case SymbologyID.SYM_IATA25:
					// enable
		        	flags |= sharedPrefs.getBoolean("sym_iata25_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_iata25_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_iata25_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CODABLOCK:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_codablock_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_codablock_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_codablock_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
				
				
					
		        case SymbologyID.SYM_MSI:
					// enable, check transmit
		        	flags |= sharedPrefs.getBoolean("sym_msi_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_msi_check_transmit_enable", true) ? SymbologyFlags.SYMBOLOGY_CHECK_TRANSMIT : 0;
		        	Log.d(TAG, "sym msi flags = " + flags);
		        	// min, max
		        	temp = sharedPrefs.getString("sym_msi_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_msi_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_TLCODE39:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_tlcode39_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					break;
		        case SymbologyID.SYM_MATRIX25:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_matrix25_enable", true) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_matrix25_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_matrix25_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_KOREAPOST:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_koreapost_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_koreapost_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_koreapost_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_TRIOPTIC:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_trioptic_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					break;
		        case SymbologyID.SYM_CODE32:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_code32_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					break;
		        case SymbologyID.SYM_STRT25:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_strt25_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_strt25_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_strt25_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_CHINAPOST:
		        	// enable
		        	flags |= sharedPrefs.getBoolean("sym_chinapost_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_chinapost_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_chinapost_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_TELEPEN:
					// enable, telepen old style
		        	flags |= sharedPrefs.getBoolean("sym_telepen_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
		        	flags |= sharedPrefs.getBoolean("sym_telepen_telepen_old_style_enable", false) ? SymbologyFlags.SYMBOLOGY_TELEPEN_OLD_STYLE : 0;
		        	// min, max
		        	temp = sharedPrefs.getString("sym_telepen_min", strMinDefault); min = Integer.parseInt(temp);
					temp = sharedPrefs.getString("sym_telepen_max", strMaxDefault); max = Integer.parseInt(temp);
					break;
		        case SymbologyID.SYM_COUPONCODE:
					// enable
		        	flags |= sharedPrefs.getBoolean("sym_couponcode_enable", false) ? SymbologyFlags.SYMBOLOGY_ENABLE : 0;
					break;
				
				default:
					symConfig.Mask = 0; // will not setSymbologyConfig
					break;
			}

			if(bNotSupported)
			{
				bNotSupported = false; // // do nothing, but reset flag
			}
			if(symConfig.Mask == (SymbologyFlags.SYM_MASK_FLAGS | SymbologyFlags.SYM_MASK_MIN_LEN | SymbologyFlags.SYM_MASK_MAX_LEN) ) // Flags & Range
			{
				symConfig.Flags = flags;
				//symConfig.MinLength = min;
				//symConfig.MaxLength = max;
				try 
				{
					m_Decoder.setSymbologyConfig(symConfig);
				} 
				catch (DecoderException e) 
				{
					Log. e(TAG, "1 EXCEPTION SYMID = " + i);
					
				}
			}
			else if(symConfig.Mask == (SymbologyFlags.SYM_MASK_FLAGS)) // Flag Only
			{
				symConfig.Flags = flags;
				try
				{
					m_Decoder.setSymbologyConfig(symConfig);	
				}
				catch (DecoderException e) 
				{
					Log. e(TAG, "2 EXCEPTION SYMID = " + i);
					
				}
			}
			else
			{
				// invalid
			}
		}
		
		SetOcrSettings();
		
		Log. e(TAG, "SetSymbologySettings--");
	}
}
