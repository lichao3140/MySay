package com.zebra.adc.decoder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class BarCodeReader1 {
	static {
		if (android.os.Build.VERSION.SDK_INT >= 23)
			System.loadLibrary("barcodereader60"); // Android 4.4
		else
			System.loadLibrary("barcodereader50");
	}

	int scan_center_mode = 0;
	private static BarCodeReader1 a = null;
	private static final int BCRDR_MSG_PREVIEW_FRAME = 0x000010;
	private static final String TAG = "BarCodeReader";
	private EventHandler mEventHandler;
	private long mNativeContext; // accessed by native methods
	private PreviewCallback mPreviewCallback;
	private boolean mOneShot;
	private boolean mWithBuffer;
	private boolean c = true;
	private Context mContext;
	private native final int native_setup(Object camera_this, int cameraId,
			String packageName);

	private native final void native_release();

	public native final void setPreviewSurface(Surface surface)
	
			throws IOException;
	public static native int getNumberOfReaders();

	private native int Init(String packagename);

	private native byte[] Decode(byte[] data, int var1, int var2,
			int var3);

	public native int setProperty(int var1, int var2);

	private native int getProperty(int var1, int var2);

	public native final boolean previewEnabled();

	public native final void setbrightness(int value);

	public native void setLightsMode(int mode);

	public final void release() {
		native_release();
	}

	public static BarCodeReader1 open(int readerId, Context context) {
		return (new BarCodeReader1(readerId, context));
	}

	BarCodeReader1(int readerId, Context context) {
		Looper aLooper;
		mEventHandler = null;
		aLooper = Looper.myLooper();
		if (null == aLooper)
			aLooper = Looper.getMainLooper();
		if (aLooper != null) {
			mEventHandler = new EventHandler(this, aLooper);
		}
		native_setup(new WeakReference<BarCodeReader1>(this), readerId,
				context.getPackageName());
		Init(context.getPackageName());
	}

	public byte[] scanner_decode(byte[] var1, int var2, int var3) {
		if (!this.isDecodingEnabled()) {
			return null;
		}
		return Decode(var1, var2, var3, scan_center_mode);
	}

	public void setDecodeWindowMode(int nMode) {
		scan_center_mode = nMode;
	}

	public void enableDecoding(boolean var1) {
		this.c = var1;
	}

	public boolean isDecodingEnabled() {
		return this.c;
	}

	public final void setPreviewDisplay(SurfaceHolder holder)
			throws IOException {
		if (holder != null) {
			setPreviewSurface(holder.getSurface());
		} else {
			setPreviewSurface((Surface) null);
		}
	}

	public interface PreviewCallback {

		void onPreviewFrame(byte[] data, BarCodeReader1 reader);
	};

	public native final void setFastMode(boolean flag);

	public native final void startDecode(boolean flag);

	public native final void stopDecode(boolean flag);

	public final void setOneShotPreviewCallback(PreviewCallback cb) {
		mPreviewCallback = cb;
		mOneShot = true;
		mWithBuffer = false;
		setHasPreviewCallback(cb != null, false);
	}

	private native final void setHasPreviewCallback(boolean installed,
			boolean manualBuffer);

	private static void postEventFromNative(Object reader_ref, int what,
			int arg1, int arg2, Object obj) {
		@SuppressWarnings("unchecked")
		BarCodeReader1 c = (BarCodeReader1) ((WeakReference<BarCodeReader1>) reader_ref)
				.get();
		if ((c != null) && (c.mEventHandler != null)) {
			Message m = c.mEventHandler.obtainMessage(what, arg1, arg2, obj);
			c.mEventHandler.sendMessage(m);
		}
	}

	private class EventHandler extends Handler {
		private BarCodeReader1 mReader;

		public EventHandler(BarCodeReader1 rdr, Looper looper) {
			super(looper);
			mReader = rdr;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case BCRDR_MSG_PREVIEW_FRAME:
				if (mPreviewCallback != null) {
					PreviewCallback cb = mPreviewCallback;
					if (mOneShot) {
						mPreviewCallback = null;
					} else if (!mWithBuffer) {
						setHasPreviewCallback(true, false);
					}
					cb.onPreviewFrame((byte[]) msg.obj, mReader);
				}
				return;

			default:
				Log.e(TAG, "Unknown message type " + msg.what);
				return;
			}
		}
	};
	
	
    public final void setPreviewCallback(PreviewCallback cb) {
        mPreviewCallback = cb;
        mOneShot = false;
        mWithBuffer = false;
       
        // Always use one-shot mode. We fake camera preview mode by
        // doing one-shot preview continuously.
        setHasPreviewCallback(cb != null, false);
    }

	public native final void setDisplayOrientation(int degrees);

	private native final void native_setParameters(String params);

	private native final String native_getParameters();

	public void setParameters(Parameters params) {
		native_setParameters(params.flatten());
	}

	/**
	 * Returns the current settings for this Camera service. If modifications
	 * are made to the returned Parameters, they must be passed to
	 * {@link #setParameters(Camera.Parameters)} to take effect.
	 * 
	 * @see #setParameters(Camera.Parameters)
	 */
	public Parameters getParameters() {
		Parameters p = new Parameters();
		String s = native_getParameters();
		p.unflatten(s);
		return p;
	}

	public class Size {
		public Size(int w, int h) {
			width = w;
			height = h;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Size)) {
				return (false);
			}
			Size s = (Size) obj;
			return ((width == s.width) && (height == s.height));
		}

		@Override
		public int hashCode() {
			return ((width * 32713) + height);
		}

		// width of the image
		public int width;
		// height of the image
		public int height;
	};

	 public class Parameters {
	        // Parameter keys to communicate with the camera driver.
	        private static final String KEY_PREVIEW_SIZE = "preview-size";
	        private static final String KEY_PREVIEW_FORMAT = "preview-format";
	        private static final String KEY_PREVIEW_FRAME_RATE = "preview-frame-rate";
	        private static final String KEY_PREVIEW_FPS_RANGE = "preview-fps-range";
	        private static final String KEY_PICTURE_SIZE = "picture-size";
	        private static final String KEY_PICTURE_FORMAT = "picture-format";
	        private static final String KEY_JPEG_THUMBNAIL_SIZE = "jpeg-thumbnail-size";
	        private static final String KEY_JPEG_THUMBNAIL_WIDTH = "jpeg-thumbnail-width";
	        private static final String KEY_JPEG_THUMBNAIL_HEIGHT = "jpeg-thumbnail-height";
	        private static final String KEY_JPEG_THUMBNAIL_QUALITY = "jpeg-thumbnail-quality";
	        private static final String KEY_JPEG_QUALITY = "jpeg-quality";
	        private static final String KEY_ROTATION = "rotation";
	        private static final String KEY_GPS_LATITUDE = "gps-latitude";
	        private static final String KEY_GPS_LONGITUDE = "gps-longitude";
	        private static final String KEY_GPS_ALTITUDE = "gps-altitude";
	        private static final String KEY_GPS_TIMESTAMP = "gps-timestamp";
	        private static final String KEY_GPS_PROCESSING_METHOD = "gps-processing-method";
	        private static final String KEY_WHITE_BALANCE = "whitebalance";
	        private static final String KEY_EFFECT = "effect";
	        private static final String KEY_ANTIBANDING = "antibanding";
	        private static final String KEY_SCENE_MODE = "scene-mode";
	        private static final String KEY_FLASH_MODE = "flash-mode";
	        private static final String KEY_FOCUS_MODE = "focus-mode";
	        private static final String KEY_FOCUS_AREAS = "focus-areas";
	        private static final String KEY_MAX_NUM_FOCUS_AREAS = "max-num-focus-areas";
	        private static final String KEY_FOCAL_LENGTH = "focal-length";
	        private static final String KEY_HORIZONTAL_VIEW_ANGLE = "horizontal-view-angle";
	        private static final String KEY_VERTICAL_VIEW_ANGLE = "vertical-view-angle";
	        private static final String KEY_EXPOSURE_COMPENSATION = "exposure-compensation";
	        private static final String KEY_MAX_EXPOSURE_COMPENSATION = "max-exposure-compensation";
	        private static final String KEY_MIN_EXPOSURE_COMPENSATION = "min-exposure-compensation";
	        private static final String KEY_EXPOSURE_COMPENSATION_STEP = "exposure-compensation-step";
	        private static final String KEY_AUTO_EXPOSURE_LOCK = "auto-exposure-lock";
	        private static final String KEY_AUTO_EXPOSURE_LOCK_SUPPORTED = "auto-exposure-lock-supported";
	        private static final String KEY_AUTO_WHITEBALANCE_LOCK = "auto-whitebalance-lock";
	        private static final String KEY_AUTO_WHITEBALANCE_LOCK_SUPPORTED = "auto-whitebalance-lock-supported";
	        private static final String KEY_METERING_AREAS = "metering-areas";
	        private static final String KEY_MAX_NUM_METERING_AREAS = "max-num-metering-areas";
	        private static final String KEY_ZOOM = "zoom";
	        private static final String KEY_MAX_ZOOM = "max-zoom";
	        private static final String KEY_ZOOM_RATIOS = "zoom-ratios";
	        private static final String KEY_ZOOM_SUPPORTED = "zoom-supported";
	        private static final String KEY_SMOOTH_ZOOM_SUPPORTED = "smooth-zoom-supported";
	        private static final String KEY_FOCUS_DISTANCES = "focus-distances";
	        private static final String KEY_VIDEO_SIZE = "video-size";
	        private static final String KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO =
	                                            "preferred-preview-size-for-video";
	        private static final String KEY_HSVR_PRV_SIZE = "hsvr-prv-size";
	        private static final String KEY_HSVR_PRV_FPS = "hsvr-prv-fps";
	        private static final String KEY_MAX_NUM_DETECTED_FACES_HW = "max-num-detected-faces-hw";
	        private static final String KEY_MAX_NUM_DETECTED_FACES_SW = "max-num-detected-faces-sw";
	        private static final String KEY_RECORDING_HINT = "recording-hint";
	        private static final String KEY_VIDEO_SNAPSHOT_SUPPORTED = "video-snapshot-supported";
	        private static final String KEY_VIDEO_STABILIZATION = "video-stabilization";
	        private static final String KEY_VIDEO_STABILIZATION_SUPPORTED = "video-stabilization-supported";
	        private static final String KEY_PDAF_SUPPORTED = "pdaf-supported";
	        //!++
	        private static final String KEY_ISOSPEED_MODE = "iso-speed";
	        private static final String KEY_FD_MODE = "fd-mode";
	        private static final String KEY_EDGE_MODE = "edge";
	        private static final String KEY_HUE_MODE = "hue";
	        private static final String KEY_SATURATION_MODE = "saturation";
	        private static final String KEY_BRIGHTNESS_MODE = "brightness";
	        private static final String KEY_CONTRAST_MODE = "contrast";
	        private static final String KEY_CAMERA_MODE = "mtk-cam-mode";
	        private static final String KEY_FPS_MODE = "fps-mode";
	        private static final String KEY_RAW_SAVE_MODE = "rawsave-mode";
	        private static final String KEY_FOCUS_ENG_MODE = "afeng-mode";
	        private static final String KEY_FOCUS_ENG_STEP = "afeng-pos";
	        private static final String KEY_FOCUS_ENG_MAX_STEP = "afeng-max-focus-step";
	        private static final String KEY_FOCUS_ENG_MIN_STEP = "afeng-min-focus-step";
	        private static final String KEY_FOCUS_ENG_BEST_STEP = "afeng-best-focus-step";
	        private static final String KEY_RAW_DUMP_FLAG = "afeng_raw_dump_flag";
	        private static final String KEY_PREVIEW_DUMP_RESOLUTION = "prv-dump-res";

	        private static final String KEY_ENG_AE_ENABLE = "ae-e";
	        private static final String KEY_ENG_PREVIEW_SHUTTER_SPEED = "prv-ss";
	        private static final String KEY_ENG_PREVIEW_SENSOR_GAIN = "prv-sr-g";
	        private static final String KEY_ENG_PREVIEW_ISP_GAIN = "prv-isp-g";
	        private static final String KEY_ENG_PREVIEW_AE_INDEX = "prv-ae-i";
	        private static final String KEY_ENG_CAPTURE_SENSOR_GAIN = "cap-sr-g";
	        private static final String KEY_ENG_CAPTURE_ISP_GAIN = "cap-isp-g";
	        private static final String KEY_ENG_CAPTURE_SHUTTER_SPEED = "cap-ss";
	        private static final String KEY_ENG_CAPTURE_ISO = "cap-iso";
	        private static final String KEY_ENG_FLASH_DUTY_VALUE = "flash-duty-value";
	        private static final String KEY_ENG_FLASH_DUTY_MIN = "flash-duty-min";
	        private static final String KEY_ENG_FLASH_DUTY_MAX = "flash-duty-max";
	        private static final String KEY_ENG_ZSD_ENABLE = "eng-zsd-e";
	        private static final String KEY_SENSOR_TYPE = "sensor-type";
	        private static final String KEY_ENG_PREVIEW_FPS = "eng-prv-fps";
	        private static final String KEY_ENG_MSG = "eng-msg";
	        private static final String KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL = "focus-fs-fi";
	        private static final String KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL_MAX = "focus-fs-fi-max";
	        private static final String KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL_MIN = "focus-fs-fi-min";
	        private static final String KEY_ENG_PREVIEW_FRAME_INTERVAL_IN_US = "eng-prv-fius";
	        private static final String KEY_ENG_PARAMETER1 = "eng-p1";
	        private static final String KEY_ENG_PARAMETER2 = "eng-p2";
	        private static final String KEY_ENG_PARAMETER3 = "eng-p3";
	        private static final String KEY_ENG_SAVE_SHADING_TABLE = "eng-s-shad-t";
	        private static final String KEY_ENG_SHADING_TABLE = "eng-shad-t";
	        private static final String KEY_ENG_EV_CALBRATION_OFFSET_VALUE = "ev-cal-o";
	        private static final String KEY_ENG_MFLL_SUPPORTED = "eng-mfll-s";
	        private static final String KEY_ENG_MFLL_ENABLE = "eng-mfll-e";
	        private static final String KEY_ENG_MFLL_PICTURE_COUNT = "eng-mfll-pc";
	        private static final String KEY_ENG_SENOSR_MODE_SLIM_VIDEO1_SUPPORTED = "sv1-s";
	        private static final String KEY_ENG_SENOSR_MODE_SLIM_VIDEO2_SUPPORTED = "sv2-s";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_RESIZE_TO_2M_SUPPORTED = "vdr-r2m-s";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_RESIZE_TO_4K2K_SUPPORTED = "vdr-r4k2k-s";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_CROP_CENTER_2M_SUPPORTED = "vdr-cc2m-s";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_SUPPORTED = "vrd-mfr-s";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_ENABLE = "vrd-mfr-e";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_MIN = "vrd-mfr-min";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_MAX = "vrd-mfr-max";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_RANGE_LOW = "vrd-mfr-low";
	        private static final String KEY_ENG_VIDEO_RAW_DUMP_MANUAL_FRAME_RATE_RANGE_HIGH = "vrd-mfr-high";
	        private static final String KEY_ENG_MTK_AWB_SUPPORTED = "mtk-awb-s";
	        private static final String KEY_ENG_SENSOR_AWB_SUPPORTED = "sr-awb-s";
	        private static final String KEY_ENG_MTK_AWB_ENABLE = "mtk-awb-e";
	        private static final String KEY_ENG_SENSOR_AWB_ENABLE = "sr-awb-e";
	        private static final String KEY_ENG_MTK_SHADING_SUPPORTED = "mtk-shad-s";
	        private static final String KEY_ENG_MTK_1to3_SHADING_SUPPORTED = "mtk-123-shad-s";
	        private static final String KEY_ENG_SENSOR_SHADNING_SUPPORTED = "sr-shad-s";
	        private static final String KEY_ENG_MTK_SHADING_ENABLE = "mtk-shad-e";
	        private static final String KEY_ENG_MTK_1to3_SHADING_ENABLE = "mtk-123-shad-e";
	        private static final String KEY_ENG_SENSOR_SHADNING_ENABLE = "sr-shad-e";

	        private static final String KEY_CAPTURE_MODE = "cap-mode";
	        private static final String KEY_CAPTURE_PATH = "capfname";
	        private static final String KEY_BURST_SHOT_NUM = "burst-num";
	        private static final String KEY_MATV_PREVIEW_DELAY = "tv-delay";
	        private static final String KEY_SENSOR_DEV = "sensor-dev";
	        private static final String KEY_EIS_MODE = "eis-mode";
	        private static final String KEY_AFLAMP_MODE = "aflamp-mode";
	        private static final String KEY_ZSD_MODE = "zsd-mode";
	        private static final String KEY_CONTINUOUS_SPEED_MODE = "continuous-shot-speed";
	        //
	        private static final String KEY_ZSD_SUPPORTED = "zsd-supported";
	    private static final String KEY_MUTE_RECORDING_SOUND = "rec-mute-ogg";
	        // Exposure meter mode for OT
	        private static final String KEY_EXPOSURE_METER_MODE = "exposure-meter";
	        // add for pip
	        private static final String KEY_MAX_FRAME_RATE_ZSD_ON = "pip-fps-zsd-on";
	        private static final String KEY_MAX_FRAME_RATE_ZSD_OFF = "pip-fps-zsd-off";
	        private static final String KEY_DYNAMIC_FRAME_RATE = "dynamic-frame-rate";
	        private static final String KEY_DYNAMIC_FRAME_RATE_SUPPORTED = "dynamic-frame-rate-supported";
	        // add for image refocus
	        private static final String KEY_REFOCUS_JPS_FILE_NAME = "refocus-jps-file-name";
	        private static final String KEY_STEREO_REFOCUS_MODE = "stereo-image-refocus";
	        private static final String KEY_STEREO_DEPTHAF_MODE = "stereo-depth-af";
	        private static final String KEY_STEREO_DISTANCE_MODE = "stereo-distance-measurement";
	    /**
	     * @hide
	    */
	        public static final String WHITE_BALANCE_TUNGSTEN = "tungsten";
	        //!--
	        // Parameter key suffix for supported values.
	        private static final String SUPPORTED_VALUES_SUFFIX = "-values";

	        private static final String TRUE = "true";
	        private static final String FALSE = "false";

	        // Values for white balance settings.
	        public static final String WHITE_BALANCE_AUTO = "auto";
	        public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
	        public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
	        public static final String WHITE_BALANCE_WARM_FLUORESCENT = "warm-fluorescent";
	        public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
	        public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
	        public static final String WHITE_BALANCE_TWILIGHT = "twilight";
	        public static final String WHITE_BALANCE_SHADE = "shade";

	        // Values for color effect settings.
	        public static final String EFFECT_NONE = "none";
	        public static final String EFFECT_MONO = "mono";
	        public static final String EFFECT_NEGATIVE = "negative";
	        public static final String EFFECT_SOLARIZE = "solarize";
	        public static final String EFFECT_SEPIA = "sepia";
	        public static final String EFFECT_POSTERIZE = "posterize";
	        public static final String EFFECT_WHITEBOARD = "whiteboard";
	        public static final String EFFECT_BLACKBOARD = "blackboard";
	        public static final String EFFECT_AQUA = "aqua";

	        // Values for antibanding settings.
	        public static final String ANTIBANDING_AUTO = "auto";
	        public static final String ANTIBANDING_50HZ = "50hz";
	        public static final String ANTIBANDING_60HZ = "60hz";
	        public static final String ANTIBANDING_OFF = "off";

	        //!++
	        // Add for STEREO 3D

	        private static final String ON = "on";
	        private static final String OFF = "off";
	        /**
	         *@hide
	         */
	        private static final String KEY_STEREO3D_PRE = "stereo3d-";
	        /**
	         *@hide
	         */
	        public static final String KEY_STEREO3D_TYPE = "type";
	        /**
	         *@hide
	         */
	        public static final String KEY_STEREO3D_MODE = "mode";
	        /**
	         *@hide
	         */
	        public static final String STEREO3D_TYPE_OFF = "off";
	        /**
	         * Frame sequential
	         *@hide
	         */
	        public static final String STEREO3D_TYPE_FRAMESEQ = "frame_seq";
	        /**
	         * Side-by-side
	         *@hide
	         */
	        public static final String STEREO3D_TYPE_SIDEBYSIDE = "sidebyside";
	        /**
	         * Top-bottom
	         *@hide
	         */
	        public static final String STEREO3D_TYPE_TOPBOTTOM = "topbottom";
	        /**
	         *@hide
	         */
	        private boolean mStereo3DMode = false;
	        //for EIS mode
	        /**
	         * Eletric image stablization on
	         * @hide
	         */
	        public static final String EIS_MODE_ON = "on";
	        /**
	         * Eletric image stablization off
	         * @hide
	         */
	        public static final String EIS_MODE_OFF = "off";
	        //!--

	        // Values for flash mode settings.
	        /**
	         * Flash will not be fired.
	         */
	        public static final String FLASH_MODE_OFF = "off";

	        /**
	         * Flash will be fired automatically when required. The flash may be fired
	         * during preview, auto-focus, or snapshot depending on the driver.
	         */
	        public static final String FLASH_MODE_AUTO = "auto";

	        /**
	         * Flash will always be fired during snapshot. The flash may also be
	         * fired during preview or auto-focus depending on the driver.
	         */
	        public static final String FLASH_MODE_ON = "on";

	        /**
	         * Flash will be fired in red-eye reduction mode.
	         */
	        public static final String FLASH_MODE_RED_EYE = "red-eye";

	        /**
	         * Constant emission of light during preview, auto-focus and snapshot.
	         * This can also be used for video recording.
	         */
	        public static final String FLASH_MODE_TORCH = "torch";

	        /**
	         * Scene mode is off.
	         */
	        public static final String SCENE_MODE_AUTO = "auto";

	        /**
	         * Take photos of fast moving objects. Same as {@link
	         * #SCENE_MODE_SPORTS}.
	         */
	        public static final String SCENE_MODE_ACTION = "action";

	        /**
	         * Take people pictures.
	         */
	        public static final String SCENE_MODE_PORTRAIT = "portrait";

	        /**
	         * Take pictures on distant objects.
	         */
	        public static final String SCENE_MODE_LANDSCAPE = "landscape";

	        /**
	         * Take photos at night.
	         */
	        public static final String SCENE_MODE_NIGHT = "night";

	        /**
	         * Take people pictures at night.
	         */
	        public static final String SCENE_MODE_NIGHT_PORTRAIT = "night-portrait";

	        /**
	         * Take photos in a theater. Flash light is off.
	         */
	        public static final String SCENE_MODE_THEATRE = "theatre";

	        /**
	         * Take pictures on the beach.
	         */
	        public static final String SCENE_MODE_BEACH = "beach";

	        /**
	         * Take pictures on the snow.
	         */
	        public static final String SCENE_MODE_SNOW = "snow";

	        /**
	         * Take sunset photos.
	         */
	        public static final String SCENE_MODE_SUNSET = "sunset";

	        /**
	         * Avoid blurry pictures (for example, due to hand shake).
	         */
	        public static final String SCENE_MODE_STEADYPHOTO = "steadyphoto";

	        /**
	         * For shooting firework displays.
	         */
	        public static final String SCENE_MODE_FIREWORKS = "fireworks";

	        /**
	         * Take photos of fast moving objects. Same as {@link
	         * #SCENE_MODE_ACTION}.
	         */
	        public static final String SCENE_MODE_SPORTS = "sports";

	        /**
	         * Take indoor low-light shot.
	         */
	        public static final String SCENE_MODE_PARTY = "party";

	        /**
	         * Capture the naturally warm color of scenes lit by candles.
	         */
	        public static final String SCENE_MODE_CANDLELIGHT = "candlelight";

	        /**
	         * Applications are looking for a barcode. Camera driver will be
	         * optimized for barcode reading.
	         */
	        public static final String SCENE_MODE_BARCODE = "barcode";

	        /**
	         * Capture a scene using high dynamic range imaging techniques. The
	         * camera will return an image that has an extended dynamic range
	         * compared to a regular capture. Capturing such an image may take
	         * longer than a regular capture.
	         */
	        public static final String SCENE_MODE_HDR = "hdr";

	        /**
	         * Auto-focus mode. Applications should call {@link
	         * #autoFocus(AutoFocusCallback)} to start the focus in this mode.
	         */
	        public static final String FOCUS_MODE_AUTO = "auto";

	        /**
	         * Focus is set at infinity. Applications should not call
	         * {@link #autoFocus(AutoFocusCallback)} in this mode.
	         */
	        public static final String FOCUS_MODE_INFINITY = "infinity";

	        /**
	         * Macro (close-up) focus mode. Applications should call
	         * {@link #autoFocus(AutoFocusCallback)} to start the focus in this
	         * mode.
	         */
	        public static final String FOCUS_MODE_MACRO = "macro";

	        /**
	         * Focus is fixed. The camera is always in this mode if the focus is not
	         * adjustable. If the camera has auto-focus, this mode can fix the
	         * focus, which is usually at hyperfocal distance. Applications should
	         * not call {@link #autoFocus(AutoFocusCallback)} in this mode.
	         */
	        public static final String FOCUS_MODE_FIXED = "fixed";

	        /**
	         * Extended depth of field (EDOF). Focusing is done digitally and
	         * continuously. Applications should not call {@link
	         * #autoFocus(AutoFocusCallback)} in this mode.
	         */
	        public static final String FOCUS_MODE_EDOF = "edof";

	       //!++
	        /*
	         * for Camera mode
	        */
	        /**
	          * @hide
	         */
	        public static final int CAMERA_MODE_NORMAL  = 0;
	        /**
	          * @hide
	         */
	        public static final int CAMERA_MODE_MTK_PRV = 1;
	        /**
	          * @hide
	         */
	        public static final int CAMERA_MODE_MTK_VDO = 2;
	        /**
	          * @hide
	         */
	        public static final int CAMERA_MODE_MTK_VT  = 3;

	        /*
	         * for AF engineer mode
	        */
	        /**
	          * @hide
	         */
	        public static final int FOCUS_ENG_MODE_NONE = 0;
	        /**
	          * @hide
	         */
	        public static final int FOCUS_ENG_MODE_BRACKET = 1;
	        /**
	          * @hide
	         */
	        public static final int FOCUS_ENG_MODE_FULLSCAN = 2;
	        /**
	          * @hide
	         */
	        public static final int FOCUS_ENG_MODE_FULLSCAN_REPEAT = 3;
	        /**
	          * @hide
	         */
	        public static final int FOCUS_ENG_MODE_REPEAT = 4;
	        /**
	          * @hide
	         */
	        public static final String FOCUS_MODE_MANUAL = "manual";
	        /**
	          * @hide
	         */
	        public static final String FOCUS_MODE_FULLSCAN = "fullscan";

	        /**
	          * @hide
	         */
	        public static final int PREVIEW_DUMP_RESOLUTION_NORMAL  = 0;
	        /**
	          * @hide
	         */
	        public static final int PREVIEW_DUMP_RESOLUTION_CROP = 1;


	        // Values for capture mode settings.
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_NORMAL = "normal";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_BEST_SHOT = "bestshot";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_EV_BRACKET_SHOT = "evbracketshot";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_BURST_SHOT = "burstshot";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_SMILE_SHOT = "smileshot";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_GESTURE_SHOT = "gestureshot";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_PANORAMA_SHOT = "autorama";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_HDR = "hdr";
	        /**
	          * @hide
	         */
	        public static final String CAPTURE_MODE_ASD = "asd";
	        /**
	         * @hide
	         */
	        public static final String CAPTURE_MODE_FB = "face_beauty";
	        /**
	         * @hide
	         */
	        public static final String KEY_MAX_NUM_DETECTED_OBJECT = "max-num-ot";
	        /**
	         * @hide
	         */
	        public static final String CAPTURE_MODE_S3D = "single3d";
	        /**
	         * @hide
	         */
	        public static final String CAPTURE_MODE_PANORAMA3D = "panorama3dmode";
	        /**
	         * @hide
	         */
	        public static final String CAPTURE_MODE_CONTINUOUS_SHOT = "continuousshot";

	        // Values for KEY_SENSOR_DEV
	        /**
	         * @hide
	         */
	        public static final String SENSOR_DEV_MAIN = "main";
	        /**
	         * @hide
	         */
	        public static final String SENSOR_DEV_SUB =  "sub";
	        /**
	         * @hide
	         */
	        public static final String SENSOR_DEV_ATV = "atv";
	        //!--

	        /**
	         * Continuous auto focus mode intended for video recording. The camera
	         * continuously tries to focus. This is the best choice for video
	         * recording because the focus changes smoothly . Applications still can
	         * call {@link #takePicture(Camera.ShutterCallback,
	         * Camera.PictureCallback, Camera.PictureCallback)} in this mode but the
	         * subject may not be in focus. Auto focus starts when the parameter is
	         * set.
	         *
	         * <p>Since API level 14, applications can call {@link
	         * #autoFocus(AutoFocusCallback)} in this mode. The focus callback will
	         * immediately return with a boolean that indicates whether the focus is
	         * sharp or not. The focus position is locked after autoFocus call. If
	         * applications want to resume the continuous focus, cancelAutoFocus
	         * must be called. Restarting the preview will not resume the continuous
	         * autofocus. To stop continuous focus, applications should change the
	         * focus mode to other modes.
	         *
	         * @see #FOCUS_MODE_CONTINUOUS_PICTURE
	         */
	        public static final String FOCUS_MODE_CONTINUOUS_VIDEO = "continuous-video";

	        /**
	         * Continuous auto focus mode intended for taking pictures. The camera
	         * continuously tries to focus. The speed of focus change is more
	         * aggressive than {@link #FOCUS_MODE_CONTINUOUS_VIDEO}. Auto focus
	         * starts when the parameter is set.
	         *
	         * <p>Applications can call {@link #autoFocus(AutoFocusCallback)} in
	         * this mode. If the autofocus is in the middle of scanning, the focus
	         * callback will return when it completes. If the autofocus is not
	         * scanning, the focus callback will immediately return with a boolean
	         * that indicates whether the focus is sharp or not. The apps can then
	         * decide if they want to take a picture immediately or to change the
	         * focus mode to auto, and run a full autofocus cycle. The focus
	         * position is locked after autoFocus call. If applications want to
	         * resume the continuous focus, cancelAutoFocus must be called.
	         * Restarting the preview will not resume the continuous autofocus. To
	         * stop continuous focus, applications should change the focus mode to
	         * other modes.
	         *
	         * @see #FOCUS_MODE_CONTINUOUS_VIDEO
	         */
	        public static final String FOCUS_MODE_CONTINUOUS_PICTURE = "continuous-picture";

	        // Indices for focus distance array.
	        /**
	         * The array index of near focus distance for use with
	         * {@link #getFocusDistances(float[])}.
	         */
	        public static final int FOCUS_DISTANCE_NEAR_INDEX = 0;

	        /**
	         * The array index of optimal focus distance for use with
	         * {@link #getFocusDistances(float[])}.
	         */
	        public static final int FOCUS_DISTANCE_OPTIMAL_INDEX = 1;

	        /**
	         * The array index of far focus distance for use with
	         * {@link #getFocusDistances(float[])}.
	         */
	        public static final int FOCUS_DISTANCE_FAR_INDEX = 2;

	        /**
	         * The array index of minimum preview fps for use with {@link
	         * #getPreviewFpsRange(int[])} or {@link
	         * #getSupportedPreviewFpsRange()}.
	         */
	        public static final int PREVIEW_FPS_MIN_INDEX = 0;

	        /**
	         * The array index of maximum preview fps for use with {@link
	         * #getPreviewFpsRange(int[])} or {@link
	         * #getSupportedPreviewFpsRange()}.
	         */
	        public static final int PREVIEW_FPS_MAX_INDEX = 1;

	        // Formats for setPreviewFormat and setPictureFormat.
	        private static final String PIXEL_FORMAT_YUV422SP = "yuv422sp";
	        private static final String PIXEL_FORMAT_YUV420SP = "yuv420sp";
	        private static final String PIXEL_FORMAT_YUV422I = "yuv422i-yuyv";
	        private static final String PIXEL_FORMAT_YUV420P = "yuv420p";
	        private static final String PIXEL_FORMAT_RGB565 = "rgb565";
	        private static final String PIXEL_FORMAT_JPEG = "jpeg";
	        private static final String PIXEL_FORMAT_BAYER_RGGB = "bayer-rggb";

	        /**
	         * Order matters: Keys that are {@link #set(String, String) set} later
	         * will take precedence over keys that are set earlier (if the two keys
	         * conflict with each other).
	         *
	         * <p>One example is {@link #setPreviewFpsRange(int, int)} , since it
	         * conflicts with {@link #setPreviewFrameRate(int)} whichever key is set later
	         * is the one that will take precedence.
	         * </p>
	         */
	        private LinkedHashMap<String, String> mMap;

	        private Parameters() {
	            mMap = new LinkedHashMap<String, String>(128);
	        }

	        /**
	         * Clone parameter from current settings.
	         * @hide
	         * @return the clone parameter
	         */
	        public Parameters copy() {
	            Parameters para = new Parameters();
	            para.mMap = new LinkedHashMap<String, String>(mMap);
	            return para;
	        }
	         /**
	         * Overwrite existing parameters with a copy of the ones from {@code other}.
	         *
	         * <b>For use by the legacy shim only.</b>
	         *
	         * @hide
	         */
	        public void copyFrom(Parameters other) {
	            if (other == null) {
	                throw new NullPointerException("other must not be null");
	            }

	            mMap.putAll(other.mMap);
	        }

	      

	        /**
	         * Value equality check.
	         *
	         * @hide
	         */
	        public boolean same(Parameters other) {
	            if (this == other) {
	                return true;
	            }
	            return other != null && Parameters.this.mMap.equals(other.mMap);
	        }

	        /**
	         * Writes the current Parameters to the log.
	         * @hide
	         * @deprecated
	         */
	        @Deprecated
	        public void dump() {
	            Log.e(TAG, "dump: size=" + mMap.size());
	            for (String k : mMap.keySet()) {
	                Log.e(TAG, "dump: " + k + "=" + mMap.get(k));
	            }
	        }

	        /**
	         * Creates a single string with all the parameters set in
	         * this Parameters object.
	         * <p>The {@link #unflatten(String)} method does the reverse.</p>
	         *
	         * @return a String with all values from this Parameters object, in
	         *         semi-colon delimited key-value pairs
	         */
	        public String flatten() {
	            StringBuilder flattened = new StringBuilder(128);
	            for (String k : mMap.keySet()) {
	                flattened.append(k);
	                flattened.append("=");
	                flattened.append(mMap.get(k));
	                flattened.append(";");
	            }
	            // chop off the extra semicolon at the end
	            flattened.deleteCharAt(flattened.length()-1);
	            return flattened.toString();
	        }

	        /**
	         * Takes a flattened string of parameters and adds each one to
	         * this Parameters object.
	         * <p>The {@link #flatten()} method does the reverse.</p>
	         *
	         * @param flattened a String of parameters (key-value paired) that
	         *                  are semi-colon delimited
	         */
	        public void unflatten(String flattened) {
	            mMap.clear();

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(';');
	            splitter.setString(flattened);
	            for (String kv : splitter) {
	                int pos = kv.indexOf('=');
	                if (pos == -1) {
	                    continue;
	                }
	                String k = kv.substring(0, pos);
	                String v = kv.substring(pos + 1);
	                mMap.put(k, v);
	            }
	        }

	        public void remove(String key) {
	            mMap.remove(key);
	        }

	        /**
	         * Sets a String parameter.
	         *
	         * @param key   the key name for the parameter
	         * @param value the String value of the parameter
	         */
	        public void set(String key, String value) {
	            Log.v(TAG, "set Key = " + key + ", value = " + value);
	            if (key.indexOf('=') != -1 || key.indexOf(';') != -1 || key.indexOf(0) != -1) {
	                Log.e(TAG, "Key \"" + key + "\" contains invalid character (= or ; or \\0)");
	                return;
	            }
	            if (value.indexOf('=') != -1 || value.indexOf(';') != -1 || value.indexOf(0) != -1) {
	                Log.e(TAG, "Value \"" + value + "\" contains invalid character (= or ; or \\0)");
	                return;
	            }

	            put(key, value);
	        }

	        /**
	         * Sets an integer parameter.
	         *
	         * @param key   the key name for the parameter
	         * @param value the int value of the parameter
	         */
	        public void set(String key, int value) {
	            put(key, Integer.toString(value));
	        }

	        private void put(String key, String value) {
	            /*
	             * Remove the key if it already exists.
	             *
	             * This way setting a new value for an already existing key will always move
	             * that key to be ordered the latest in the map.
	             */
	            mMap.remove(key);
	            mMap.put(key, value);
	        }

	        private void set(String key, List<Area> areas) {
	            if (areas == null) {
	                set(key, "(0,0,0,0,0)");
	            } else {
	                StringBuilder buffer = new StringBuilder();
	                for (int i = 0; i < areas.size(); i++) {
	                    Area area = areas.get(i);
	                    Rect rect = area.rect;
	                    buffer.append('(');
	                    buffer.append(rect.left);
	                    buffer.append(',');
	                    buffer.append(rect.top);
	                    buffer.append(',');
	                    buffer.append(rect.right);
	                    buffer.append(',');
	                    buffer.append(rect.bottom);
	                    buffer.append(',');
	                    buffer.append(area.weight);
	                    buffer.append(')');
	                    if (i != areas.size() - 1) buffer.append(',');
	                }
	                set(key, buffer.toString());
	            }
	        }

	        /**
	         * Returns the value of a String parameter.
	         *
	         * @param key the key name for the parameter
	         * @return the String value of the parameter
	         */
	        public String get(String key) {
	            return mMap.get(key);
	        }

	        /**
	         * Returns the value of an integer parameter.
	         *
	         * @param key the key name for the parameter
	         * @return the int value of the parameter
	         */
	        public int getInt(String key) {
	            return Integer.parseInt(mMap.get(key));
	        }

	        /**
	         * Sets the dimensions for preview pictures. If the preview has already
	         * started, applications should stop the preview first before changing
	         * preview size.
	         *
	         * The sides of width and height are based on camera orientation. That
	         * is, the preview size is the size before it is rotated by display
	         * orientation. So applications need to consider the display orientation
	         * while setting preview size. For example, suppose the camera supports
	         * both 480x320 and 320x480 preview sizes. The application wants a 3:2
	         * preview ratio. If the display orientation is set to 0 or 180, preview
	         * size should be set to 480x320. If the display orientation is set to
	         * 90 or 270, preview size should be set to 320x480. The display
	         * orientation should also be considered while setting picture size and
	         * thumbnail size.
	         *
	         * @param width  the width of the pictures, in pixels
	         * @param height the height of the pictures, in pixels
	         * @see #setDisplayOrientation(int)
	         * @see #getCameraInfo(int, CameraInfo)
	         * @see #setPictureSize(int, int)
	         * @see #setJpegThumbnailSize(int, int)
	         */
	        public void setPreviewSize(int width, int height) {
	            String v = Integer.toString(width) + "x" + Integer.toString(height);
	            set((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PREVIEW_SIZE, v);
	        }

	        /**
	         * Returns the dimensions setting for preview pictures.
	         *
	         * @return a Size object with the width and height setting
	         *          for the preview picture
	         */
	        public Size getPreviewSize() {
	            String pair = get((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PREVIEW_SIZE);
	            return strToSize(pair);
	        }

	        /**
	         * Gets the supported preview sizes.
	         *
	         * @return a list of Size object. This method will always return a list
	         *         with at least one element.
	         */
	        public List<Size> getSupportedPreviewSizes() {
	            String str = get((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PREVIEW_SIZE + SUPPORTED_VALUES_SUFFIX);
	            return splitSize(str);
	        }

	        /**
	         * <p>Gets the supported video frame sizes that can be used by
	         * MediaRecorder.</p>
	         *
	         * <p>If the returned list is not null, the returned list will contain at
	         * least one Size and one of the sizes in the returned list must be
	         * passed to MediaRecorder.setVideoSize() for camcorder application if
	         * camera is used as the video source. In this case, the size of the
	         * preview can be different from the resolution of the recorded video
	         * during video recording.</p>
	         *
	         * @return a list of Size object if camera has separate preview and
	         *         video output; otherwise, null is returned.
	         * @see #getPreferredPreviewSizeForVideo()
	         */
	        public List<Size> getSupportedVideoSizes() {
	            String str = get(KEY_VIDEO_SIZE + SUPPORTED_VALUES_SUFFIX);
	            return splitSize(str);
	        }

	        /**
	         * Returns the preferred or recommended preview size (width and height)
	         * in pixels for video recording. Camcorder applications should
	         * set the preview size to a value that is not larger than the
	         * preferred preview size. In other words, the product of the width
	         * and height of the preview size should not be larger than that of
	         * the preferred preview size. In addition, we recommend to choose a
	         * preview size that has the same aspect ratio as the resolution of
	         * video to be recorded.
	         *
	         * @return the preferred preview size (width and height) in pixels for
	         *         video recording if getSupportedVideoSizes() does not return
	         *         null; otherwise, null is returned.
	         * @see #getSupportedVideoSizes()
	         */
	        public Size getPreferredPreviewSizeForVideo() {
	            String pair = get(KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO);
	            return strToSize(pair);
	        }
	        /**
	         * @hide
	         * add for slow motion preview size
	         * @return the list size will tell camera ap to choose right preview size
	         */
	        public Size getPreferredPreviewSizeForSlowMotionVideo() {
	            String pair = get(KEY_HSVR_PRV_SIZE);
	            return strToSize(pair);
	        }
	        /**
	         * @hide
	         * add for slow motion preview size
	         * @return the list size will tell camera ap to choose right preview size
	         */
	        public List<Size> getSupportedSlowMotionVideoSizes() {
	            String str = get(KEY_HSVR_PRV_SIZE + SUPPORTED_VALUES_SUFFIX);
	            return splitSize(str);
	        }
	        /**
	         * <p>Sets the dimensions for EXIF thumbnail in Jpeg picture. If
	         * applications set both width and height to 0, EXIF will not contain
	         * thumbnail.</p>
	         *
	         * <p>Applications need to consider the display orientation. See {@link
	         * #setPreviewSize(int,int)} for reference.</p>
	         *
	         * @param width  the width of the thumbnail, in pixels
	         * @param height the height of the thumbnail, in pixels
	         * @see #setPreviewSize(int,int)
	         */
	        public void setJpegThumbnailSize(int width, int height) {
	            set(KEY_JPEG_THUMBNAIL_WIDTH, width);
	            set(KEY_JPEG_THUMBNAIL_HEIGHT, height);
	        }

	        /**
	         * Returns the dimensions for EXIF thumbnail in Jpeg picture.
	         *
	         * @return a Size object with the height and width setting for the EXIF
	         *         thumbnails
	         */
	        public Size getJpegThumbnailSize() {
	            return new Size(getInt(KEY_JPEG_THUMBNAIL_WIDTH),
	                            getInt(KEY_JPEG_THUMBNAIL_HEIGHT));
	        }

	        /**
	         * Gets the supported jpeg thumbnail sizes.
	         *
	         * @return a list of Size object. This method will always return a list
	         *         with at least two elements. Size 0,0 (no thumbnail) is always
	         *         supported.
	         */
	        public List<Size> getSupportedJpegThumbnailSizes() {
	            String str = get(KEY_JPEG_THUMBNAIL_SIZE + SUPPORTED_VALUES_SUFFIX);
	            return splitSize(str);
	        }

	        /**
	         * Sets the quality of the EXIF thumbnail in Jpeg picture.
	         *
	         * @param quality the JPEG quality of the EXIF thumbnail. The range is 1
	         *                to 100, with 100 being the best.
	         */
	        public void setJpegThumbnailQuality(int quality) {
	            set(KEY_JPEG_THUMBNAIL_QUALITY, quality);
	        }

	        /**
	         * Returns the quality setting for the EXIF thumbnail in Jpeg picture.
	         *
	         * @return the JPEG quality setting of the EXIF thumbnail.
	         */
	        public int getJpegThumbnailQuality() {
	            return getInt(KEY_JPEG_THUMBNAIL_QUALITY);
	        }

	        /**
	         * Sets Jpeg quality of captured picture.
	         *
	         * @param quality the JPEG quality of captured picture. The range is 1
	         *                to 100, with 100 being the best.
	         */
	        public void setJpegQuality(int quality) {
	            set(KEY_JPEG_QUALITY, quality);
	        }

	        /**
	         * Returns the quality setting for the JPEG picture.
	         *
	         * @return the JPEG picture quality setting.
	         */
	        public int getJpegQuality() {
	            return getInt(KEY_JPEG_QUALITY);
	        }

	        /**
	         * Sets the rate at which preview frames are received. This is the
	         * target frame rate. The actual frame rate depends on the driver.
	         *
	         * @param fps the frame rate (frames per second)
	         * @deprecated replaced by {@link #setPreviewFpsRange(int,int)}
	         */
	        @Deprecated
	        public void setPreviewFrameRate(int fps) {
	            set(KEY_PREVIEW_FRAME_RATE, fps);
	        }

	        /**
	         * Returns the setting for the rate at which preview frames are
	         * received. This is the target frame rate. The actual frame rate
	         * depends on the driver.
	         *
	         * @return the frame rate setting (frames per second)
	         * @deprecated replaced by {@link #getPreviewFpsRange(int[])}
	         */
	        @Deprecated
	        public int getPreviewFrameRate() {
	            return getInt(KEY_PREVIEW_FRAME_RATE);
	        }

	        /**
	         * Gets the supported preview frame rates.
	         *
	         * @return a list of supported preview frame rates. null if preview
	         *         frame rate setting is not supported.
	         * @deprecated replaced by {@link #getSupportedPreviewFpsRange()}
	         */
	        @Deprecated
	        public List<Integer> getSupportedPreviewFrameRates() {
	            String str = get(KEY_PREVIEW_FRAME_RATE + SUPPORTED_VALUES_SUFFIX);
	            return splitInt(str);
	        }

	        /**
	         * Sets the minimum and maximum preview fps. This controls the rate of
	         * preview frames received in {@link PreviewCallback}. The minimum and
	         * maximum preview fps must be one of the elements from {@link
	         * #getSupportedPreviewFpsRange}.
	         *
	         * @param min the minimum preview fps (scaled by 1000).
	         * @param max the maximum preview fps (scaled by 1000).
	         * @throws RuntimeException if fps range is invalid.
	         * @see #setPreviewCallbackWithBuffer(Camera.PreviewCallback)
	         * @see #getSupportedPreviewFpsRange()
	         */
	        public void setPreviewFpsRange(int min, int max) {
	            set(KEY_PREVIEW_FPS_RANGE, "" + min + "," + max);
	        }

	        /**
	         * Returns the current minimum and maximum preview fps. The values are
	         * one of the elements returned by {@link #getSupportedPreviewFpsRange}.
	         *
	         * @return range the minimum and maximum preview fps (scaled by 1000).
	         * @see #PREVIEW_FPS_MIN_INDEX
	         * @see #PREVIEW_FPS_MAX_INDEX
	         * @see #getSupportedPreviewFpsRange()
	         */
	        public void getPreviewFpsRange(int[] range) {
	            if (range == null || range.length != 2) {
	                throw new IllegalArgumentException(
	                        "range must be an array with two elements.");
	            }
	            splitInt(get(KEY_PREVIEW_FPS_RANGE), range);
	        }

	        /**
	         * Gets the supported preview fps (frame-per-second) ranges. Each range
	         * contains a minimum fps and maximum fps. If minimum fps equals to
	         * maximum fps, the camera outputs frames in fixed frame rate. If not,
	         * the camera outputs frames in auto frame rate. The actual frame rate
	         * fluctuates between the minimum and the maximum. The values are
	         * multiplied by 1000 and represented in integers. For example, if frame
	         * rate is 26.623 frames per second, the value is 26623.
	         *
	         * @return a list of supported preview fps ranges. This method returns a
	         *         list with at least one element. Every element is an int array
	         *         of two values - minimum fps and maximum fps. The list is
	         *         sorted from small to large (first by maximum fps and then
	         *         minimum fps).
	         * @see #PREVIEW_FPS_MIN_INDEX
	         * @see #PREVIEW_FPS_MAX_INDEX
	         */
	        public List<int[]> getSupportedPreviewFpsRange() {
	            String str = get(KEY_PREVIEW_FPS_RANGE + SUPPORTED_VALUES_SUFFIX);
	            return splitRange(str);
	        }

	        /**
	         * Sets the image format for preview pictures.
	         * <p>If this is never called, the default format will be
	         * {@link android.graphics.ImageFormat#NV21}, which
	         * uses the NV21 encoding format.</p>
	         *
	         * <p>Use {@link Parameters#getSupportedPreviewFormats} to get a list of
	         * the available preview formats.
	         *
	         * <p>It is strongly recommended that either
	         * {@link android.graphics.ImageFormat#NV21} or
	         * {@link android.graphics.ImageFormat#YV12} is used, since
	         * they are supported by all camera devices.</p>
	         *
	         * <p>For YV12, the image buffer that is received is not necessarily
	         * tightly packed, as there may be padding at the end of each row of
	         * pixel data, as described in
	         * {@link android.graphics.ImageFormat#YV12}. For camera callback data,
	         * it can be assumed that the stride of the Y and UV data is the
	         * smallest possible that meets the alignment requirements. That is, if
	         * the preview size is <var>width x height</var>, then the following
	         * equations describe the buffer index for the beginning of row
	         * <var>y</var> for the Y plane and row <var>c</var> for the U and V
	         * planes:
	         *
	         * {@code
	         * <pre>
	         * yStride   = (int) ceil(width / 16.0) * 16;
	         * uvStride  = (int) ceil( (yStride / 2) / 16.0) * 16;
	         * ySize     = yStride * height;
	         * uvSize    = uvStride * height / 2;
	         * yRowIndex = yStride * y;
	         * uRowIndex = ySize + uvSize + uvStride * c;
	         * vRowIndex = ySize + uvStride * c;
	         * size      = ySize + uvSize * 2;</pre>
	         * }
	         *
	         * @param pixel_format the desired preview picture format, defined by
	         *   one of the {@link android.graphics.ImageFormat} constants.  (E.g.,
	         *   <var>ImageFormat.NV21</var> (default), or
	         *   <var>ImageFormat.YV12</var>)
	         *
	         * @see android.graphics.ImageFormat
	         * @see android.hardware.Camera.Parameters#getSupportedPreviewFormats
	         */
	        public void setPreviewFormat(int pixel_format) {
	            String s = cameraFormatForPixelFormat(pixel_format);
	            if (s == null) {
	                throw new IllegalArgumentException(
	                        "Invalid pixel_format=" + pixel_format);
	            }

	            set(KEY_PREVIEW_FORMAT, s);
	        }

	        /**
	         * Returns the image format for preview frames got from
	         * {@link PreviewCallback}.
	         *
	         * @return the preview format.
	         * @see android.graphics.ImageFormat
	         * @see #setPreviewFormat
	         */
	        public int getPreviewFormat() {
	            return pixelFormatForCameraFormat(get(KEY_PREVIEW_FORMAT));
	        }

	        /**
	         * Gets the supported preview formats. {@link android.graphics.ImageFormat#NV21}
	         * is always supported. {@link android.graphics.ImageFormat#YV12}
	         * is always supported since API level 12.
	         *
	         * @return a list of supported preview formats. This method will always
	         *         return a list with at least one element.
	         * @see android.graphics.ImageFormat
	         * @see #setPreviewFormat
	         */
	        public List<Integer> getSupportedPreviewFormats() {
	            String str = get(KEY_PREVIEW_FORMAT + SUPPORTED_VALUES_SUFFIX);
	            ArrayList<Integer> formats = new ArrayList<Integer>();
	            for (String s : split(str)) {
	                int f = pixelFormatForCameraFormat(s);
	                if (f == ImageFormat.UNKNOWN) continue;
	                formats.add(f);
	            }
	            return formats;
	        }

	        /**
	         * <p>Sets the dimensions for pictures.</p>
	         *
	         * <p>Applications need to consider the display orientation. See {@link
	         * #setPreviewSize(int,int)} for reference.</p>
	         *
	         * @param width  the width for pictures, in pixels
	         * @param height the height for pictures, in pixels
	         * @see #setPreviewSize(int,int)
	         *
	         */
	        public void setPictureSize(int width, int height) {
	            String v = Integer.toString(width) + "x" + Integer.toString(height);
	            set((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PICTURE_SIZE, v);
	        }

	        /**
	         * Returns the dimension setting for pictures.
	         *
	         * @return a Size object with the height and width setting
	         *          for pictures
	         */
	        public Size getPictureSize() {
	            String pair = get((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PICTURE_SIZE);
	            return strToSize(pair);
	        }

	        /**
	         * Gets the supported picture sizes.
	         *
	         * @return a list of supported picture sizes. This method will always
	         *         return a list with at least one element.
	         */
	        public List<Size> getSupportedPictureSizes() {
	            String str = get((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_PICTURE_SIZE + SUPPORTED_VALUES_SUFFIX);
	            return splitSize(str);
	        }

	        /**
	         * Sets the image format for pictures.
	         *
	         * @param pixel_format the desired picture format
	         *                     (<var>ImageFormat.NV21</var>,
	         *                      <var>ImageFormat.RGB_565</var>, or
	         *                      <var>ImageFormat.JPEG</var>)
	         * @see android.graphics.ImageFormat
	         */
	        public void setPictureFormat(int pixel_format) {
	            String s = cameraFormatForPixelFormat(pixel_format);
	            if (s == null) {
	                throw new IllegalArgumentException(
	                        "Invalid pixel_format=" + pixel_format);
	            }

	            set(KEY_PICTURE_FORMAT, s);
	        }

	        /**
	         * Returns the image format for pictures.
	         *
	         * @return the picture format
	         * @see android.graphics.ImageFormat
	         */
	        public int getPictureFormat() {
	            return pixelFormatForCameraFormat(get(KEY_PICTURE_FORMAT));
	        }

	        /**
	         * Gets the supported picture formats.
	         *
	         * @return supported picture formats. This method will always return a
	         *         list with at least one element.
	         * @see android.graphics.ImageFormat
	         */
	        public List<Integer> getSupportedPictureFormats() {
	            String str = get(KEY_PICTURE_FORMAT + SUPPORTED_VALUES_SUFFIX);
	            ArrayList<Integer> formats = new ArrayList<Integer>();
	            for (String s : split(str)) {
	                int f = pixelFormatForCameraFormat(s);
	                if (f == ImageFormat.UNKNOWN) continue;
	                formats.add(f);
	            }
	            return formats;
	        }

	        private String cameraFormatForPixelFormat(int pixel_format) {
	            switch(pixel_format) {
	            case ImageFormat.NV16:      return PIXEL_FORMAT_YUV422SP;
	            case ImageFormat.NV21:      return PIXEL_FORMAT_YUV420SP;
	            case ImageFormat.YUY2:      return PIXEL_FORMAT_YUV422I;
	            case ImageFormat.YV12:      return PIXEL_FORMAT_YUV420P;
	            case ImageFormat.RGB_565:   return PIXEL_FORMAT_RGB565;
	            case ImageFormat.JPEG:      return PIXEL_FORMAT_JPEG;
	            default:                    return null;
	            }
	        }

	        private int pixelFormatForCameraFormat(String format) {
	            if (format == null)
	                return ImageFormat.UNKNOWN;

	            if (format.equals(PIXEL_FORMAT_YUV422SP))
	                return ImageFormat.NV16;

	            if (format.equals(PIXEL_FORMAT_YUV420SP))
	                return ImageFormat.NV21;

	            if (format.equals(PIXEL_FORMAT_YUV422I))
	                return ImageFormat.YUY2;

	            if (format.equals(PIXEL_FORMAT_YUV420P))
	                return ImageFormat.YV12;

	            if (format.equals(PIXEL_FORMAT_RGB565))
	                return ImageFormat.RGB_565;

	            if (format.equals(PIXEL_FORMAT_JPEG))
	                return ImageFormat.JPEG;

	            return ImageFormat.UNKNOWN;
	        }

	        /**
	         * Sets the clockwise rotation angle in degrees relative to the
	         * orientation of the camera. This affects the pictures returned from
	         * JPEG {@link PictureCallback}. The camera driver may set orientation
	         * in the EXIF header without rotating the picture. Or the driver may
	         * rotate the picture and the EXIF thumbnail. If the Jpeg picture is
	         * rotated, the orientation in the EXIF header will be missing or 1 (row
	         * #0 is top and column #0 is left side).
	         *
	         * <p>
	         * If applications want to rotate the picture to match the orientation
	         * of what users see, apps should use
	         * {@link android.view.OrientationEventListener} and
	         * {@link android.hardware.Camera.CameraInfo}. The value from
	         * OrientationEventListener is relative to the natural orientation of
	         * the device. CameraInfo.orientation is the angle between camera
	         * orientation and natural device orientation. The sum of the two is the
	         * rotation angle for back-facing camera. The difference of the two is
	         * the rotation angle for front-facing camera. Note that the JPEG
	         * pictures of front-facing cameras are not mirrored as in preview
	         * display.
	         *
	         * <p>
	         * For example, suppose the natural orientation of the device is
	         * portrait. The device is rotated 270 degrees clockwise, so the device
	         * orientation is 270. Suppose a back-facing camera sensor is mounted in
	         * landscape and the top side of the camera sensor is aligned with the
	         * right edge of the display in natural orientation. So the camera
	         * orientation is 90. The rotation should be set to 0 (270 + 90).
	         *
	         * <p>The reference code is as follows.
	         *
	         * <pre>
	         * public void onOrientationChanged(int orientation) {
	         *     if (orientation == ORIENTATION_UNKNOWN) return;
	         *     android.hardware.Camera.CameraInfo info =
	         *            new android.hardware.Camera.CameraInfo();
	         *     android.hardware.Camera.getCameraInfo(cameraId, info);
	         *     orientation = (orientation + 45) / 90 * 90;
	         *     int rotation = 0;
	         *     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	         *         rotation = (info.orientation - orientation + 360) % 360;
	         *     } else {  // back-facing camera
	         *         rotation = (info.orientation + orientation) % 360;
	         *     }
	         *     mParameters.setRotation(rotation);
	         * }
	         * </pre>
	         *
	         * @param rotation The rotation angle in degrees relative to the
	         *                 orientation of the camera. Rotation can only be 0,
	         *                 90, 180 or 270.
	         * @throws IllegalArgumentException if rotation value is invalid.
	         * @see android.view.OrientationEventListener
	         * @see #getCameraInfo(int, CameraInfo)
	         */
	        public void setRotation(int rotation) {
	            if (rotation == 0 || rotation == 90 || rotation == 180
	                    || rotation == 270) {
	                set(KEY_ROTATION, Integer.toString(rotation));
	            } else {
	                throw new IllegalArgumentException(
	                        "Invalid rotation=" + rotation);
	            }
	        }

	        /**
	         * Sets GPS latitude coordinate. This will be stored in JPEG EXIF
	         * header.
	         *
	         * @param latitude GPS latitude coordinate.
	         */
	        public void setGpsLatitude(double latitude) {
	            set(KEY_GPS_LATITUDE, Double.toString(latitude));
	        }

	        /**
	         * Sets GPS longitude coordinate. This will be stored in JPEG EXIF
	         * header.
	         *
	         * @param longitude GPS longitude coordinate.
	         */
	        public void setGpsLongitude(double longitude) {
	            set(KEY_GPS_LONGITUDE, Double.toString(longitude));
	        }

	        /**
	         * Sets GPS altitude. This will be stored in JPEG EXIF header.
	         *
	         * @param altitude GPS altitude in meters.
	         */
	        public void setGpsAltitude(double altitude) {
	            set(KEY_GPS_ALTITUDE, Double.toString(altitude));
	        }

	        /**
	         * Sets GPS timestamp. This will be stored in JPEG EXIF header.
	         *
	         * @param timestamp GPS timestamp (UTC in seconds since January 1,
	         *                  1970).
	         */
	        public void setGpsTimestamp(long timestamp) {
	            set(KEY_GPS_TIMESTAMP, Long.toString(timestamp));
	        }

	        /**
	         * Sets GPS processing method. It will store up to 32 characters
	         * in JPEG EXIF header.
	         *
	         * @param processing_method The processing method to get this location.
	         */
	        public void setGpsProcessingMethod(String processing_method) {
	            set(KEY_GPS_PROCESSING_METHOD, processing_method);
	        }

	        /**
	         * Removes GPS latitude, longitude, altitude, and timestamp from the
	         * parameters.
	         */
	        public void removeGpsData() {
	            remove(KEY_GPS_LATITUDE);
	            remove(KEY_GPS_LONGITUDE);
	            remove(KEY_GPS_ALTITUDE);
	            remove(KEY_GPS_TIMESTAMP);
	            remove(KEY_GPS_PROCESSING_METHOD);
	        }

	        /**
	         * Gets the current white balance setting.
	         *
	         * @return current white balance. null if white balance setting is not
	         *         supported.
	         * @see #WHITE_BALANCE_AUTO
	         * @see #WHITE_BALANCE_INCANDESCENT
	         * @see #WHITE_BALANCE_FLUORESCENT
	         * @see #WHITE_BALANCE_WARM_FLUORESCENT
	         * @see #WHITE_BALANCE_DAYLIGHT
	         * @see #WHITE_BALANCE_CLOUDY_DAYLIGHT
	         * @see #WHITE_BALANCE_TWILIGHT
	         * @see #WHITE_BALANCE_SHADE
	         *
	         */
	        public String getWhiteBalance() {
	            return get(KEY_WHITE_BALANCE);
	        }

	        /**
	         * Sets the white balance. Changing the setting will release the
	         * auto-white balance lock. It is recommended not to change white
	         * balance and AWB lock at the same time.
	         *
	         * @param value new white balance.
	         * @see #getWhiteBalance()
	         * @see #setAutoWhiteBalanceLock(boolean)
	         */
	        public void setWhiteBalance(String value) {
	            String oldValue = get(KEY_WHITE_BALANCE);
	            if (same(value, oldValue)) return;
	            set(KEY_WHITE_BALANCE, value);
	            set(KEY_AUTO_WHITEBALANCE_LOCK, FALSE);
	        }

	        /**
	         * Gets the supported white balance.
	         *
	         * @return a list of supported white balance. null if white balance
	         *         setting is not supported.
	         * @see #getWhiteBalance()
	         */
	        public List<String> getSupportedWhiteBalance() {
	            String str = get(KEY_WHITE_BALANCE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	        /**
	         * Gets the current color effect setting.
	         *
	         * @return current color effect. null if color effect
	         *         setting is not supported.
	         * @see #EFFECT_NONE
	         * @see #EFFECT_MONO
	         * @see #EFFECT_NEGATIVE
	         * @see #EFFECT_SOLARIZE
	         * @see #EFFECT_SEPIA
	         * @see #EFFECT_POSTERIZE
	         * @see #EFFECT_WHITEBOARD
	         * @see #EFFECT_BLACKBOARD
	         * @see #EFFECT_AQUA
	         */
	        public String getColorEffect() {
	            return get(KEY_EFFECT);
	        }

	        /**
	         * Sets the current color effect setting.
	         *
	         * @param value new color effect.
	         * @see #getColorEffect()
	         */
	        public void setColorEffect(String value) {
	            set(KEY_EFFECT, value);
	        }

	        /**
	         * Gets the supported color effects.
	         *
	         * @return a list of supported color effects. null if color effect
	         *         setting is not supported.
	         * @see #getColorEffect()
	         */
	        public List<String> getSupportedColorEffects() {
	            String str = get(KEY_EFFECT + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }


	        /**
	         * Gets the current antibanding setting.
	         *
	         * @return current antibanding. null if antibanding setting is not
	         *         supported.
	         * @see #ANTIBANDING_AUTO
	         * @see #ANTIBANDING_50HZ
	         * @see #ANTIBANDING_60HZ
	         * @see #ANTIBANDING_OFF
	         */
	        public String getAntibanding() {
	            return get(KEY_ANTIBANDING);
	        }

	        /**
	         * Sets the antibanding.
	         *
	         * @param antibanding new antibanding value.
	         * @see #getAntibanding()
	         */
	        public void setAntibanding(String antibanding) {
	            set(KEY_ANTIBANDING, antibanding);
	        }

	        /**
	         * Gets the supported antibanding values.
	         *
	         * @return a list of supported antibanding values. null if antibanding
	         *         setting is not supported.
	         * @see #getAntibanding()
	         */
	        public List<String> getSupportedAntibanding() {
	            String str = get(KEY_ANTIBANDING + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	    //!++
	    /**
	    * @hide
	    * Gets the current Eis mode setting (on/off)
	    * @ return one of EIS_MODE_xxx string constant.
	    * @see #EIS_MODE_ON
	    * @see #EIS_MODE_OFF
	    */
	        public String getEisMode() {
	            return get(KEY_EIS_MODE);
	        }
	    /**
	    * @hide
	    */
	        public void setEisMode(String eis) {
	            set(KEY_EIS_MODE, eis);
	        }
	    /**
	    * @hide
	    */
	        public List<String> getSupportedEisMode() {
	            String str = get(KEY_EIS_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	    /**
	    * @hide
	    * IF environment light is not strong enough, camera will turn on flash while focusing
	    */
	        public String getAFLampMode() {
	            return get(KEY_AFLAMP_MODE);
	        }
	    /**
	    * @hide
	    */
	        public void setAFLampMode(String aflamp) {
	            set(KEY_AFLAMP_MODE, aflamp);
	        }
	    /**
	    * @hide
	    */
	        public List<String> getSupportedAFLampMode() {
	            String str = get(KEY_AFLAMP_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	    //!--

	        /**
	         * Gets the current scene mode setting.
	         *
	         * @return one of SCENE_MODE_XXX string constant. null if scene mode
	         *         setting is not supported.
	         * @see #SCENE_MODE_AUTO
	         * @see #SCENE_MODE_ACTION
	         * @see #SCENE_MODE_PORTRAIT
	         * @see #SCENE_MODE_LANDSCAPE
	         * @see #SCENE_MODE_NIGHT
	         * @see #SCENE_MODE_NIGHT_PORTRAIT
	         * @see #SCENE_MODE_THEATRE
	         * @see #SCENE_MODE_BEACH
	         * @see #SCENE_MODE_SNOW
	         * @see #SCENE_MODE_SUNSET
	         * @see #SCENE_MODE_STEADYPHOTO
	         * @see #SCENE_MODE_FIREWORKS
	         * @see #SCENE_MODE_SPORTS
	         * @see #SCENE_MODE_PARTY
	         * @see #SCENE_MODE_CANDLELIGHT
	         * @see #SCENE_MODE_BARCODE
	         */
	        public String getSceneMode() {
	            return get(KEY_SCENE_MODE);
	        }

	        /**
	         * Sets the scene mode. Changing scene mode may override other
	         * parameters (such as flash mode, focus mode, white balance). For
	         * example, suppose originally flash mode is on and supported flash
	         * modes are on/off. In night scene mode, both flash mode and supported
	         * flash mode may be changed to off. After setting scene mode,
	         * applications should call getParameters to know if some parameters are
	         * changed.
	         *
	         * @param value scene mode.
	         * @see #getSceneMode()
	         */
	        public void setSceneMode(String value) {
	            set(KEY_SCENE_MODE, value);
	        }

	        /**
	         * Gets the supported scene modes.
	         *
	         * @return a list of supported scene modes. null if scene mode setting
	         *         is not supported.
	         * @see #getSceneMode()
	         */
	        public List<String> getSupportedSceneModes() {
	            String str = get(KEY_SCENE_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	        /**
	         * Gets the current flash mode setting.
	         *
	         * @return current flash mode. null if flash mode setting is not
	         *         supported.
	         * @see #FLASH_MODE_OFF
	         * @see #FLASH_MODE_AUTO
	         * @see #FLASH_MODE_ON
	         * @see #FLASH_MODE_RED_EYE
	         * @see #FLASH_MODE_TORCH
	         */
	        public String getFlashMode() {
	            return get(KEY_FLASH_MODE);
	        }

	        /**
	         * Sets the flash mode.
	         *
	         * @param value flash mode.
	         * @see #getFlashMode()
	         */
	        public void setFlashMode(String value) {
	            set(KEY_FLASH_MODE, value);
	        }

	        /**
	         * Gets the supported flash modes.
	         *
	         * @return a list of supported flash modes. null if flash mode setting
	         *         is not supported.
	         * @see #getFlashMode()
	         */
	        public List<String> getSupportedFlashModes() {
	            String str = get(KEY_FLASH_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	        /**
	         * Gets the current focus mode setting.
	         *
	         * @return current focus mode. This method will always return a non-null
	         *         value. Applications should call {@link
	         *         #autoFocus(AutoFocusCallback)} to start the focus if focus
	         *         mode is FOCUS_MODE_AUTO or FOCUS_MODE_MACRO.
	         * @see #FOCUS_MODE_AUTO
	         * @see #FOCUS_MODE_INFINITY
	         * @see #FOCUS_MODE_MACRO
	         * @see #FOCUS_MODE_FIXED
	         * @see #FOCUS_MODE_EDOF
	         * @see #FOCUS_MODE_CONTINUOUS_VIDEO
	         */
	        public String getFocusMode() {
	            return get(KEY_FOCUS_MODE);
	        }

	        /**
	         * Sets the focus mode.
	         *
	         * @param value focus mode.
	         * @see #getFocusMode()
	         */
	        public void setFocusMode(String value) {
	            set(KEY_FOCUS_MODE, value);
	        }

	        /**
	         * Gets the supported focus modes.
	         *
	         * @return a list of supported focus modes. This method will always
	         *         return a list with at least one element.
	         * @see #getFocusMode()
	         */
	        public List<String> getSupportedFocusModes() {
	            String str = get(KEY_FOCUS_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	        /**
	         * Gets the focal length (in millimeter) of the camera.
	         *
	         * @return the focal length. This method will always return a valid
	         *         value.
	         */
	        public float getFocalLength() {
	            return Float.parseFloat(get(KEY_FOCAL_LENGTH));
	        }

	        /**
	         * Gets the horizontal angle of view in degrees.
	         *
	         * @return horizontal angle of view. This method will always return a
	         *         valid value.
	         */
	        public float getHorizontalViewAngle() {
	            return Float.parseFloat(get(KEY_HORIZONTAL_VIEW_ANGLE));
	        }

	        /**
	         * Gets the vertical angle of view in degrees.
	         *
	         * @return vertical angle of view. This method will always return a
	         *         valid value.
	         */
	        public float getVerticalViewAngle() {
	            return Float.parseFloat(get(KEY_VERTICAL_VIEW_ANGLE));
	        }

	        /**
	         * Gets the current exposure compensation index.
	         *
	         * @return current exposure compensation index. The range is {@link
	         *         #getMinExposureCompensation} to {@link
	         *         #getMaxExposureCompensation}. 0 means exposure is not
	         *         adjusted.
	         */
	        public int getExposureCompensation() {
	            return getInt(KEY_EXPOSURE_COMPENSATION, 0);
	        }

	        /**
	         * Sets the exposure compensation index.
	         *
	         * @param value exposure compensation index. The valid value range is
	         *        from {@link #getMinExposureCompensation} (inclusive) to {@link
	         *        #getMaxExposureCompensation} (inclusive). 0 means exposure is
	         *        not adjusted. Application should call
	         *        getMinExposureCompensation and getMaxExposureCompensation to
	         *        know if exposure compensation is supported.
	         */
	        public void setExposureCompensation(int value) {
	            set(KEY_EXPOSURE_COMPENSATION, value);
	        }

	        /**
	         * Gets the maximum exposure compensation index.
	         *
	         * @return maximum exposure compensation index (>=0). If both this
	         *         method and {@link #getMinExposureCompensation} return 0,
	         *         exposure compensation is not supported.
	         */
	        public int getMaxExposureCompensation() {
	            return getInt(KEY_MAX_EXPOSURE_COMPENSATION, 0);
	        }

	        /**
	         * Gets the minimum exposure compensation index.
	         *
	         * @return minimum exposure compensation index (<=0). If both this
	         *         method and {@link #getMaxExposureCompensation} return 0,
	         *         exposure compensation is not supported.
	         */
	        public int getMinExposureCompensation() {
	            return getInt(KEY_MIN_EXPOSURE_COMPENSATION, 0);
	        }

	        /**
	         * Gets the exposure compensation step.
	         *
	         * @return exposure compensation step. Applications can get EV by
	         *         multiplying the exposure compensation index and step. Ex: if
	         *         exposure compensation index is -6 and step is 0.333333333, EV
	         *         is -2.
	         */
	        public float getExposureCompensationStep() {
	            return getFloat(KEY_EXPOSURE_COMPENSATION_STEP, 0);
	        }

	        /**
	         * <p>Sets the auto-exposure lock state. Applications should check
	         * {@link #isAutoExposureLockSupported} before using this method.</p>
	         *
	         * <p>If set to true, the camera auto-exposure routine will immediately
	         * pause until the lock is set to false. Exposure compensation settings
	         * changes will still take effect while auto-exposure is locked.</p>
	         *
	         * <p>If auto-exposure is already locked, setting this to true again has
	         * no effect (the driver will not recalculate exposure values).</p>
	         *
	         * <p>Stopping preview with {@link #stopPreview()}, or triggering still
	         * image capture with {@link #takePicture(Camera.ShutterCallback,
	         * Camera.PictureCallback, Camera.PictureCallback)}, will not change the
	         * lock.</p>
	         *
	         * <p>Exposure compensation, auto-exposure lock, and auto-white balance
	         * lock can be used to capture an exposure-bracketed burst of images,
	         * for example.</p>
	         *
	         * <p>Auto-exposure state, including the lock state, will not be
	         * maintained after camera {@link #release()} is called.  Locking
	         * auto-exposure after {@link #open()} but before the first call to
	         * {@link #startPreview()} will not allow the auto-exposure routine to
	         * run at all, and may result in severely over- or under-exposed
	         * images.</p>
	         *
	         * @param toggle new state of the auto-exposure lock. True means that
	         *        auto-exposure is locked, false means that the auto-exposure
	         *        routine is free to run normally.
	         *
	         * @see #getAutoExposureLock()
	         */
	        public void setAutoExposureLock(boolean toggle) {
	            set(KEY_AUTO_EXPOSURE_LOCK, toggle ? TRUE : FALSE);
	        }

	        /**
	         * Gets the state of the auto-exposure lock. Applications should check
	         * {@link #isAutoExposureLockSupported} before using this method. See
	         * {@link #setAutoExposureLock} for details about the lock.
	         *
	         * @return State of the auto-exposure lock. Returns true if
	         *         auto-exposure is currently locked, and false otherwise.
	         *
	         * @see #setAutoExposureLock(boolean)
	         *
	         */
	        public boolean getAutoExposureLock() {
	            String str = get(KEY_AUTO_EXPOSURE_LOCK);
	            return TRUE.equals(str);
	        }

	        /**
	         * Returns true if auto-exposure locking is supported. Applications
	         * should call this before trying to lock auto-exposure. See
	         * {@link #setAutoExposureLock} for details about the lock.
	         *
	         * @return true if auto-exposure lock is supported.
	         * @see #setAutoExposureLock(boolean)
	         *
	         */
	        public boolean isAutoExposureLockSupported() {
	            String str = get(KEY_AUTO_EXPOSURE_LOCK_SUPPORTED);
	            return TRUE.equals(str);
	        }

	        /**
	         * <p>Sets the auto-white balance lock state. Applications should check
	         * {@link #isAutoWhiteBalanceLockSupported} before using this
	         * method.</p>
	         *
	         * <p>If set to true, the camera auto-white balance routine will
	         * immediately pause until the lock is set to false.</p>
	         *
	         * <p>If auto-white balance is already locked, setting this to true
	         * again has no effect (the driver will not recalculate white balance
	         * values).</p>
	         *
	         * <p>Stopping preview with {@link #stopPreview()}, or triggering still
	         * image capture with {@link #takePicture(Camera.ShutterCallback,
	         * Camera.PictureCallback, Camera.PictureCallback)}, will not change the
	         * the lock.</p>
	         *
	         * <p> Changing the white balance mode with {@link #setWhiteBalance}
	         * will release the auto-white balance lock if it is set.</p>
	         *
	         * <p>Exposure compensation, AE lock, and AWB lock can be used to
	         * capture an exposure-bracketed burst of images, for example.
	         * Auto-white balance state, including the lock state, will not be
	         * maintained after camera {@link #release()} is called.  Locking
	         * auto-white balance after {@link #open()} but before the first call to
	         * {@link #startPreview()} will not allow the auto-white balance routine
	         * to run at all, and may result in severely incorrect color in captured
	         * images.</p>
	         *
	         * @param toggle new state of the auto-white balance lock. True means
	         *        that auto-white balance is locked, false means that the
	         *        auto-white balance routine is free to run normally.
	         *
	         * @see #getAutoWhiteBalanceLock()
	         * @see #setWhiteBalance(String)
	         */
	        public void setAutoWhiteBalanceLock(boolean toggle) {
	            set(KEY_AUTO_WHITEBALANCE_LOCK, toggle ? TRUE : FALSE);
	        }

	        /**
	         * Gets the state of the auto-white balance lock. Applications should
	         * check {@link #isAutoWhiteBalanceLockSupported} before using this
	         * method. See {@link #setAutoWhiteBalanceLock} for details about the
	         * lock.
	         *
	         * @return State of the auto-white balance lock. Returns true if
	         *         auto-white balance is currently locked, and false
	         *         otherwise.
	         *
	         * @see #setAutoWhiteBalanceLock(boolean)
	         *
	         */
	        public boolean getAutoWhiteBalanceLock() {
	            String str = get(KEY_AUTO_WHITEBALANCE_LOCK);
	            return TRUE.equals(str);
	        }

	        /**
	         * Returns true if auto-white balance locking is supported. Applications
	         * should call this before trying to lock auto-white balance. See
	         * {@link #setAutoWhiteBalanceLock} for details about the lock.
	         *
	         * @return true if auto-white balance lock is supported.
	         * @see #setAutoWhiteBalanceLock(boolean)
	         *
	         */
	        public boolean isAutoWhiteBalanceLockSupported() {
	            String str = get(KEY_AUTO_WHITEBALANCE_LOCK_SUPPORTED);
	            return TRUE.equals(str);
	        }

	        /**
	         * Gets current zoom value. This also works when smooth zoom is in
	         * progress. Applications should check {@link #isZoomSupported} before
	         * using this method.
	         *
	         * @return the current zoom value. The range is 0 to {@link
	         *         #getMaxZoom}. 0 means the camera is not zoomed.
	         */
	        public int getZoom() {
	            return getInt(KEY_ZOOM, 0);
	        }

	        /**
	         * Sets current zoom value. If the camera is zoomed (value > 0), the
	         * actual picture size may be smaller than picture size setting.
	         * Applications can check the actual picture size after picture is
	         * returned from {@link PictureCallback}. The preview size remains the
	         * same in zoom. Applications should check {@link #isZoomSupported}
	         * before using this method.
	         *
	         * @param value zoom value. The valid range is 0 to {@link #getMaxZoom}.
	         */
	        public void setZoom(int value) {
	            set(KEY_ZOOM, value);
	        }

	        /**
	         * Returns true if zoom is supported. Applications should call this
	         * before using other zoom methods.
	         *
	         * @return true if zoom is supported.
	         */
	        public boolean isZoomSupported() {
	            String str = get(KEY_ZOOM_SUPPORTED);
	            return TRUE.equals(str);
	        }

	        /**
	         * Gets the maximum zoom value allowed for snapshot. This is the maximum
	         * value that applications can set to {@link #setZoom(int)}.
	         * Applications should call {@link #isZoomSupported} before using this
	         * method. This value may change in different preview size. Applications
	         * should call this again after setting preview size.
	         *
	         * @return the maximum zoom value supported by the camera.
	         */
	        public int getMaxZoom() {
	            return getInt(KEY_MAX_ZOOM, 0);
	        }

	        /**
	         * Gets the zoom ratios of all zoom values. Applications should check
	         * {@link #isZoomSupported} before using this method.
	         *
	         * @return the zoom ratios in 1/100 increments. Ex: a zoom of 3.2x is
	         *         returned as 320. The number of elements is {@link
	         *         #getMaxZoom} + 1. The list is sorted from small to large. The
	         *         first element is always 100. The last element is the zoom
	         *         ratio of the maximum zoom value.
	         */
	        public List<Integer> getZoomRatios() {
	            return splitInt(get(KEY_ZOOM_RATIOS));
	        }

	        /**
	         * Returns true if smooth zoom is supported. Applications should call
	         * this before using other smooth zoom methods.
	         *
	         * @return true if smooth zoom is supported.
	         */
	        public boolean isSmoothZoomSupported() {
	            String str = get(KEY_SMOOTH_ZOOM_SUPPORTED);
	            return TRUE.equals(str);
	        }

	        //!++
	        /**
	         * @hide
	         * Sets the camera mode
	         * @param value: see #CAMERA_MODE_NORMAL, #CAMERA_MODE_MTK_PRV, #CAMERA_MODE_MTK_VDO, #CAMERA_MODE_MTK_VT
	         */
	        public void setCameraMode(int value) {
	            Log.d(TAG, "setCameraMode=" + value);
	            set(KEY_CAMERA_MODE, value);
	        }

	        // ISO
	        /**
	         * @hide
	         * Gets the ISO speed
	         * @return "auto", "100", "200", "400", "800" or "1600"
	         */
	        public String getISOSpeed() {
	            return get(KEY_ISOSPEED_MODE);
	        }
	        /**
	         * @hide
	         * Sets the ISO speed
	         * @param value "auto", "100", "200", "400", "800" or "1600"
	         */
	        public void setISOSpeed(String value) {
	            set(KEY_ISOSPEED_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported ISO speed
	         * get all supported ISO speed
	         */
	        public List<String> getSupportedISOSpeed() {
	            String str = get(KEY_ISOSPEED_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        //OT
	        /**
	         * @hide get max num of objects
	         *
	         *
	         */
	        public int getMaxNumDetectedObjects() {
	            return getInt(Parameters.KEY_MAX_NUM_DETECTED_OBJECT, 0);
	        }

	        // FDMode
	        /**
	         * @hide
	         * Gets the FD mode
	         * @return "on" or "off"
	         */
	        public String getFDMode() {
	            return get(KEY_FD_MODE);
	        }
	        /**
	         * @hide
	         * Sets the FD mode
	         * @param value "on" or "off"
	         */
	        public void setFDMode(String value) {
	            set(KEY_FD_MODE, value);
	        }
	        /**
	         * @hide
	         */
	        public List<String> getSupportedFDMode() {
	            String str = get(KEY_FD_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Edge
	        /**
	         * @hide
	         * @return the edge mode
	         * get the edge mode
	         */
	        public String getEdgeMode() {
	            return get(KEY_EDGE_MODE);
	        }
	        /**
	         * @hide
	         * @param value : the value set for edge mode
	         * set value for edge mode
	         */
	        public void setEdgeMode(String value) {
	            set(KEY_EDGE_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported edge mode
	         * get all supported edge mode
	         */
	        public List<String> getSupportedEdgeMode() {
	            String str = get(KEY_EDGE_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Hue
	        /**
	         * @hide
	         * @return the hue mode
	         * get the hue mode
	         */
	        public String getHueMode() {
	            return get(KEY_HUE_MODE);
	        }
	        /**
	         * @hide
	         * @param value: the value set for hue mode
	         * set value for hue mode
	         */
	        public void setHueMode(String value) {
	            set(KEY_HUE_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported hue mode
	         * get all supported hue mode
	         */
	        public List<String> getSupportedHueMode() {
	            String str = get(KEY_HUE_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Saturation
	        /**
	         * @hide
	         * @return the Saturation mode
	         * get the Saturation mode
	         */
	        public String getSaturationMode() {
	            return get(KEY_SATURATION_MODE);
	        }
	        /**
	         * @hide
	         * @param value: the value set to Saturation
	         * set value for Saturation
	         */
	        public void setSaturationMode(String value) {
	            set(KEY_SATURATION_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported Saturation mode
	         * get all supported Saturation mode
	         */
	        public List<String> getSupportedSaturationMode() {
	            String str = get(KEY_SATURATION_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Brightness
	        /**
	         * @hide
	         * @return the brightness mode
	         * get the brightness mode
	         */
	        public String getBrightnessMode() {
	            return get(KEY_BRIGHTNESS_MODE);
	        }
	        /**
	         * @hide
	         * @param value: the value set to brightness mode
	         * set value for brightness mode
	         */
	        public void setBrightnessMode(String value) {
	            set(KEY_BRIGHTNESS_MODE, value);
	        }
	        /**
	         * @hide
	         */
	        public List<String> getSupportedBrightnessMode() {
	            String str = get(KEY_BRIGHTNESS_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Contrast
	        /**
	         * @hide
	         * @return the contrast mode
	         * get the contrast mode
	         */
	        public String getContrastMode() {
	            return get(KEY_CONTRAST_MODE);
	        }
	        /**
	         * @hide
	         * @param value: the value set to contrast mode
	         * set value for contrast mode
	         */
	        public void setContrastMode(String value) {
	            set(KEY_CONTRAST_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported contrast mode
	         * get all supported contrast mode
	         */
	        public List<String> getSupportedContrastMode() {
	            String str = get(KEY_CONTRAST_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        // Capture mode
	        /**
	         * @hide
	         */
	        public String getCaptureMode() {
	            return get(KEY_CAPTURE_MODE);
	        }
	        /**
	         * @hide
	         * @param value: CAPTURE_MODE_NORMAL, CAPTURE_MODE_BEST_SHOT,
	         *        CAPTURE_MODE_EV_BRACKET_SHOT, CAPTURE_MODE_BURST_SHOT,
	         *        CAPTURE_MODE_SMILE_SHOT, CAPTURE_MODE_PANORAMA_SHOT
	         */
	        public void setCaptureMode(String value) {
	            set(KEY_CAPTURE_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported capture mode
	         * get all supported capture mode
	         */
	        public List<String> getSupportedCaptureMode() {
	            String str = get(KEY_CAPTURE_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }
	        /**
	         * @hide
	         * @param   value: file path, if file path is /sdcard/DCIM/cap00, the output file
	         * will be cap00, cap01, cap02, ...
	         * set the storage path for capture image store
	         */
	        public void setCapturePath(String value) {
	            if (value == null) {
	                remove(KEY_CAPTURE_PATH);
	            } else {
	                set(KEY_CAPTURE_PATH, value);
	            }
	        }
	        /**
	         * @hide
	         * @param  value: should be 4 / 8 / 16
	         * set the burst shot number
	         */
	        public void setBurstShotNum(int value) {
	            set(KEY_BURST_SHOT_NUM, value);
	        }
	        /**
	         * @hide
	         *
	         * Sets the focus engineer mode
	         * mode: FOCUS_ENG_MODE_NONE, FOCUS_ENG_MODE_BRACKET,
	         *       FOCUS_ENG_MODE_FULLSCAN, FOCUS_ENG_MODE_REPEAT
	         */
	        public void setFocusEngMode(int mode) {
	            set(KEY_FOCUS_ENG_MODE, mode);
	        }

	        /**
	         * @hide
	         * @return the step get the best focus
	         * get the step of doing the best focus
	         */
	        public int getBestFocusStep() {
	            return getInt(KEY_FOCUS_ENG_BEST_STEP, 0);
	        }

	        /**
	         * @hide
	         */
	        public void setRawDumpFlag(boolean toggle) {
	            Log.d(TAG, "setRawDumpFlag=" + toggle);
	            set(KEY_RAW_DUMP_FLAG, toggle ? TRUE : FALSE);
	        }

	        /**
	         * @hide
	         */
	        public void setPreviewRawDumpResolution(int value) {
	            Log.d(TAG, "setPreviewRawDumpResolution=" + value);
	            set(KEY_PREVIEW_DUMP_RESOLUTION, value);
	        }

	        /**
	         * @hide
	         * Gets the maximum focus step.
	         *
	         * @return maximum focus step.
	         */
	        public int getMaxFocusStep() {
	            return getInt(KEY_FOCUS_ENG_MAX_STEP , 0);
	        }

	        /**
	         * @hide
	         * Gets the minimum focus step.
	         *
	         * @return minimum focus step.
	         */
	        public int getMinFocusStep() {
	            return getInt(KEY_FOCUS_ENG_STEP, 0);
	        }


	        /**
	         * @hide
	         *
	         * Sets the focus step for BRACKET / FULLSCAN mode
	         */
	        public void setFocusEngStep(int step) {
	            set(KEY_FOCUS_ENG_STEP, step);
	        }

	        /**
	         * @hide
	         *
	         * Sets the Exposure Meter mode for Obejct Tracking
	         */
	        public void setExposureMeterMode(String mode) {
	            set(KEY_EXPOSURE_METER_MODE, mode);
	        }

	        /**
	         * @hide
	         *
	         * Gets the Exposure Meter mode for Obejct Tracking
	         */
	        public String getExposureMeterMode() {
	            return get(KEY_EXPOSURE_METER_MODE);
	        }

	        /**
	         * @hide
	         * Gets types of sensors.
	         *
	         * @return sensor type.
	         */
	        public int getSensorType() {
	            return getInt(KEY_SENSOR_TYPE, 0);
	        }

	        /**
	         * @hide
	         *
	         * Set AE enable/disable in engineer mode
	         */
	        public void setEngAEEnable(int enable) {
	            set(KEY_ENG_AE_ENABLE, enable);
	        }


	        /**
	         * @hide
	         *
	         * Set Flash duty value in engineer mode
	         */
	        public void setEngFlashDuty(int duty) {
	            set(KEY_ENG_FLASH_DUTY_VALUE, duty);
	        }


	        /**
	         * @hide
	         *
	         * Set ZSD mode in engineer mode
	         */
	        public void setEngZSDEnable(int enable) {
	            set(KEY_ENG_ZSD_ENABLE, enable);
	        }

	        /**
	         * @hide
	         *
	         * Get preview shutter speed
	         *
	         */
	        public int getEngPreviewShutterSpeed() {
	            return getInt(KEY_ENG_PREVIEW_SHUTTER_SPEED, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get preview sensor gain
	         *
	         */
	        public int getEngPreviewSensorGain() {
	            return getInt(KEY_ENG_PREVIEW_SENSOR_GAIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get preview isp gain
	         *
	         */
	        public int getEngPreviewISPGain() {
	            return getInt(KEY_ENG_PREVIEW_ISP_GAIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get preview AE PLine index
	         *
	         */
	        public int getEngPreviewAEIndex() {
	            return getInt(KEY_ENG_PREVIEW_AE_INDEX, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get capture sensor gain in preview
	         *
	         */
	        public int getEngCaptureSensorGain() {
	            return getInt(KEY_ENG_CAPTURE_SENSOR_GAIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get capture isp gain in preview
	         *
	         */
	        public int getEngCaptureISPGain() {
	            return getInt(KEY_ENG_CAPTURE_ISP_GAIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get capture shutter speed in preview
	         *
	         */
	        public int getEngCaptureShutterSpeed() {
	            return getInt(KEY_ENG_CAPTURE_SHUTTER_SPEED, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get capture iso after capture
	         *
	         */
	        public int getEngCaptureISO() {
	            return getInt(KEY_ENG_CAPTURE_ISO, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get flash duty minimax value
	         *
	         */
	        public int getEngFlashDutyMin() {
	            return getInt(KEY_ENG_FLASH_DUTY_MIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get flash duty maximum value
	         *
	         */
	        public int getEngFlashDutyMax() {
	            return getInt(KEY_ENG_FLASH_DUTY_MAX, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get preview FPS
	         *
	         */
	        public int getEngPreviewFPS() {
	            return getInt(KEY_ENG_PREVIEW_FPS, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get flash duty maximum value
	         *
	         */
	        public String getEngEngMSG() {
	            return get(KEY_ENG_MSG);
	        }

	        /**
	         * @hide
	         *
	         * Set frame interval in focus full scan in engineer mode
	         */
	        public void setEngFocusFullScanFrameInterval(int n) {
	            set(KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL, n);
	        }

	        /**
	         * @hide
	         *
	         * Get max frame interval in focus full scan in engineer mode
	         */
	        public int getEngFocusFullScanFrameIntervalMax() {
	            return getInt(KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL_MAX, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get min frame interval in focus full scan in engineer mode
	         */
	        public int getEngFocusFullScanFrameIntervalMin() {
	            return getInt(KEY_ENG_FOCUS_FULLSCAN_FRAME_INTERVAL_MIN, 0);
	        }

	        /**
	         * @hide
	         *
	         * Get preview frame interval in us in engineer mode
	         */
	        public int getEngPreviewFrameIntervalInUS() {
	            return getInt(KEY_ENG_PREVIEW_FRAME_INTERVAL_IN_US, 0);
	        }

	        /**
	         * @hide
	         *
	         * Set parameter 1 in engineer mode
	         */
	        public void setEngParameter1(String value) {
	            set(KEY_ENG_PARAMETER1, value);
	        }

	        /**
	         * @hide
	         *
	         * Set parameter 2 in engineer mode
	         */
	        public void setEngParameter2(String value) {
	            set(KEY_ENG_PARAMETER2, value);
	        }

	        /**
	         * @hide
	         *
	         * Set parameter 3 in engineer mode
	         */
	        public void setEngParameter3(String value) {
	            set(KEY_ENG_PARAMETER3, value);
	        }

	        /**
	         * @hide
	         *
	         * Save shading table or not in engineer mode
	         */
	        public void setEngSaveShadingTable(int save) {
	            set(KEY_ENG_SAVE_SHADING_TABLE, save);
	        }

	        /**
	         * @hide
	         *
	         * Specify shading table in engineer mode
	         */
	        public void setEngShadingTable(int shading_table) {
	            set(KEY_ENG_SHADING_TABLE, shading_table);
	        }

	         /**
	         * @hide
	         *
	         * Get in engineer mode
	         */
	        public int getEngEVCalOffset() {
	            return getInt(KEY_ENG_EV_CALBRATION_OFFSET_VALUE, 0);
	        }

	        /**
	         * @hide
	         *
	         * Sets the MATV preview delay time (ms)
	         */
	        public void setMATVDelay(int ms) {
	            set(KEY_MATV_PREVIEW_DELAY, ms);
	        }

	        // Add for Stereo3D Start
	        /**
	         * @hide
	         */
	        public String getStereo3DType() {
	            return get((mStereo3DMode ? KEY_STEREO3D_PRE : "") + KEY_STEREO3D_TYPE);
	        }
	        /**
	         * @hide
	         * @param value: stereo3d_mode:true or false
	         */
	        public void setStereo3DMode(boolean enable) {
	            mStereo3DMode = enable;
	        }
	        // Add for Stereo3D End
	        /**
	         * @hide
	         */
	        public void setContinuousSpeedMode(String value) {
	            set(KEY_CONTINUOUS_SPEED_MODE, value);
	        }
	        //!--
	        /**
	         * @hide
	         * @return the ZSD mode
	         * get the ZSD mode
	         */
	        public String getZSDMode() {
	            return get(KEY_ZSD_MODE);
	        }
	        /**
	         * @hide
	         * Sets ZSD mode on/off
	         * @param value "on" or "off"
	         */
	        public void setZSDMode(String value) {
	            set(KEY_ZSD_MODE, value);
	        }
	        /**
	         * @hide
	         * @return the supported ZSD mode
	         * get all supported ZSD mode
	         */
	        public List<String> getSupportedZSDMode() {
	            String str = get(KEY_ZSD_MODE + SUPPORTED_VALUES_SUFFIX);
	            return split(str);
	        }

	        /**
	         * <p>Gets the distances from the camera to where an object appears to be
	         * in focus. The object is sharpest at the optimal focus distance. The
	         * depth of field is the far focus distance minus near focus distance.</p>
	         *
	         * <p>Focus distances may change after calling {@link
	         * #autoFocus(AutoFocusCallback)}, {@link #cancelAutoFocus}, or {@link
	         * #startPreview()}. Applications can call {@link #getParameters()}
	         * and this method anytime to get the latest focus distances. If the
	         * focus mode is FOCUS_MODE_CONTINUOUS_VIDEO, focus distances may change
	         * from time to time.</p>
	         *
	         * <p>This method is intended to estimate the distance between the camera
	         * and the subject. After autofocus, the subject distance may be within
	         * near and far focus distance. However, the precision depends on the
	         * camera hardware, autofocus algorithm, the focus area, and the scene.
	         * The error can be large and it should be only used as a reference.</p>
	         *
	         * <p>Far focus distance >= optimal focus distance >= near focus distance.
	         * If the focus distance is infinity, the value will be
	         * {@code Float.POSITIVE_INFINITY}.</p>
	         *
	         * @param output focus distances in meters. output must be a float
	         *        array with three elements. Near focus distance, optimal focus
	         *        distance, and far focus distance will be filled in the array.
	         * @see #FOCUS_DISTANCE_NEAR_INDEX
	         * @see #FOCUS_DISTANCE_OPTIMAL_INDEX
	         * @see #FOCUS_DISTANCE_FAR_INDEX
	         */
	        public void getFocusDistances(float[] output) {
	            if (output == null || output.length != 3) {
	                throw new IllegalArgumentException(
	                        "output must be a float array with three elements.");
	            }
	            splitFloat(get(KEY_FOCUS_DISTANCES), output);
	        }

	        /**
	         * Gets the maximum number of focus areas supported. This is the maximum
	         * length of the list in {@link #setFocusAreas(List)} and
	         * {@link #getFocusAreas()}.
	         *
	         * @return the maximum number of focus areas supported by the camera.
	         * @see #getFocusAreas()
	         */
	        public int getMaxNumFocusAreas() {
	            return getInt(KEY_MAX_NUM_FOCUS_AREAS, 0);
	        }

	        /**
	         * <p>Gets the current focus areas. Camera driver uses the areas to decide
	         * focus.</p>
	         *
	         * <p>Before using this API or {@link #setFocusAreas(List)}, apps should
	         * call {@link #getMaxNumFocusAreas()} to know the maximum number of
	         * focus areas first. If the value is 0, focus area is not supported.</p>
	         *
	         * <p>Each focus area is a rectangle with specified weight. The direction
	         * is relative to the sensor orientation, that is, what the sensor sees.
	         * The direction is not affected by the rotation or mirroring of
	         * {@link #setDisplayOrientation(int)}. Coordinates of the rectangle
	         * range from -1000 to 1000. (-1000, -1000) is the upper left point.
	         * (1000, 1000) is the lower right point. The width and height of focus
	         * areas cannot be 0 or negative.</p>
	         *
	         * <p>The weight must range from 1 to 1000. The weight should be
	         * interpreted as a per-pixel weight - all pixels in the area have the
	         * specified weight. This means a small area with the same weight as a
	         * larger area will have less influence on the focusing than the larger
	         * area. Focus areas can partially overlap and the driver will add the
	         * weights in the overlap region.</p>
	         *
	         * <p>A special case of a {@code null} focus area list means the driver is
	         * free to select focus targets as it wants. For example, the driver may
	         * use more signals to select focus areas and change them
	         * dynamically. Apps can set the focus area list to {@code null} if they
	         * want the driver to completely control focusing.</p>
	         *
	         * <p>Focus areas are relative to the current field of view
	         * ({@link #getZoom()}). No matter what the zoom level is, (-1000,-1000)
	         * represents the top of the currently visible camera frame. The focus
	         * area cannot be set to be outside the current field of view, even
	         * when using zoom.</p>
	         *
	         * <p>Focus area only has effect if the current focus mode is
	         * {@link #FOCUS_MODE_AUTO}, {@link #FOCUS_MODE_MACRO},
	         * {@link #FOCUS_MODE_CONTINUOUS_VIDEO}, or
	         * {@link #FOCUS_MODE_CONTINUOUS_PICTURE}.</p>
	         *
	         * @return a list of current focus areas
	         */
	        public List<Area> getFocusAreas() {
	            return splitArea(get(KEY_FOCUS_AREAS));
	        }

	        /**
	         * Sets focus areas. See {@link #getFocusAreas()} for documentation.
	         *
	         * @param focusAreas the focus areas
	         * @see #getFocusAreas()
	         */
	        public void setFocusAreas(List<Area> focusAreas) {
	            set(KEY_FOCUS_AREAS, focusAreas);
	        }

	        /**
	         * Gets the maximum number of metering areas supported. This is the
	         * maximum length of the list in {@link #setMeteringAreas(List)} and
	         * {@link #getMeteringAreas()}.
	         *
	         * @return the maximum number of metering areas supported by the camera.
	         * @see #getMeteringAreas()
	         */
	        public int getMaxNumMeteringAreas() {
	            return getInt(KEY_MAX_NUM_METERING_AREAS, 0);
	        }

	        /**
	         * <p>Gets the current metering areas. Camera driver uses these areas to
	         * decide exposure.</p>
	         *
	         * <p>Before using this API or {@link #setMeteringAreas(List)}, apps should
	         * call {@link #getMaxNumMeteringAreas()} to know the maximum number of
	         * metering areas first. If the value is 0, metering area is not
	         * supported.</p>
	         *
	         * <p>Each metering area is a rectangle with specified weight. The
	         * direction is relative to the sensor orientation, that is, what the
	         * sensor sees. The direction is not affected by the rotation or
	         * mirroring of {@link #setDisplayOrientation(int)}. Coordinates of the
	         * rectangle range from -1000 to 1000. (-1000, -1000) is the upper left
	         * point. (1000, 1000) is the lower right point. The width and height of
	         * metering areas cannot be 0 or negative.</p>
	         *
	         * <p>The weight must range from 1 to 1000, and represents a weight for
	         * every pixel in the area. This means that a large metering area with
	         * the same weight as a smaller area will have more effect in the
	         * metering result.  Metering areas can partially overlap and the driver
	         * will add the weights in the overlap region.</p>
	         *
	         * <p>A special case of a {@code null} metering area list means the driver
	         * is free to meter as it chooses. For example, the driver may use more
	         * signals to select metering areas and change them dynamically. Apps
	         * can set the metering area list to {@code null} if they want the
	         * driver to completely control metering.</p>
	         *
	         * <p>Metering areas are relative to the current field of view
	         * ({@link #getZoom()}). No matter what the zoom level is, (-1000,-1000)
	         * represents the top of the currently visible camera frame. The
	         * metering area cannot be set to be outside the current field of view,
	         * even when using zoom.</p>
	         *
	         * <p>No matter what metering areas are, the final exposure are compensated
	         * by {@link #setExposureCompensation(int)}.</p>
	         *
	         * @return a list of current metering areas
	         */
	        public List<Area> getMeteringAreas() {
	            return splitArea(get(KEY_METERING_AREAS));
	        }

	        /**
	         * Sets metering areas. See {@link #getMeteringAreas()} for
	         * documentation.
	         *
	         * @param meteringAreas the metering areas
	         * @see #getMeteringAreas()
	         */
	        public void setMeteringAreas(List<Area> meteringAreas) {
	            set(KEY_METERING_AREAS, meteringAreas);
	        }

	        /**
	         * Gets the maximum number of detected faces supported. This is the
	         * maximum length of the list returned from {@link FaceDetectionListener}.
	         * If the return value is 0, face detection of the specified type is not
	         * supported.
	         *
	         * @return the maximum number of detected face supported by the camera.
	         * @see #startFaceDetection()
	         */
	        public int getMaxNumDetectedFaces() {
	            return getInt(KEY_MAX_NUM_DETECTED_FACES_HW, 0);
	        }

	        /**
	         * Sets recording mode hint. This tells the camera that the intent of
	         * the application is to record videos {@link
	         * android.media.MediaRecorder#start()}, not to take still pictures
	         * {@link #takePicture(Camera.ShutterCallback, Camera.PictureCallback,
	         * Camera.PictureCallback, Camera.PictureCallback)}. Using this hint can
	         * allow MediaRecorder.start() to start faster or with fewer glitches on
	         * output. This should be called before starting preview for the best
	         * result, but can be changed while the preview is active. The default
	         * value is false.
	         *
	         * The app can still call takePicture() when the hint is true or call
	         * MediaRecorder.start() when the hint is false. But the performance may
	         * be worse.
	         *
	         * @param hint true if the apps intend to record videos using
	         *             {@link android.media.MediaRecorder}.
	         */
	        public void setRecordingHint(boolean hint) {
	            set(KEY_RECORDING_HINT, hint ? TRUE : FALSE);
	        }

	        /**
	         * <p>Returns true if video snapshot is supported. That is, applications
	         * can call {@link #takePicture(Camera.ShutterCallback,
	         * Camera.PictureCallback, Camera.PictureCallback,
	         * Camera.PictureCallback)} during recording. Applications do not need
	         * to call {@link #startPreview()} after taking a picture. The preview
	         * will be still active. Other than that, taking a picture during
	         * recording is identical to taking a picture normally. All settings and
	         * methods related to takePicture work identically. Ex:
	         * {@link #getPictureSize()}, {@link #getSupportedPictureSizes()},
	         * {@link #setJpegQuality(int)}, {@link #setRotation(int)}, and etc. The
	         * picture will have an EXIF header. {@link #FLASH_MODE_AUTO} and
	         * {@link #FLASH_MODE_ON} also still work, but the video will record the
	         * flash.</p>
	         *
	         * <p>Applications can set shutter callback as null to avoid the shutter
	         * sound. It is also recommended to set raw picture and post view
	         * callbacks to null to avoid the interrupt of preview display.</p>
	         *
	         * <p>Field-of-view of the recorded video may be different from that of the
	         * captured pictures. The maximum size of a video snapshot may be
	         * smaller than that for regular still captures. If the current picture
	         * size is set higher than can be supported by video snapshot, the
	         * picture will be captured at the maximum supported size instead.</p>
	         *
	         * @return true if video snapshot is supported.
	         */
	        public boolean isVideoSnapshotSupported() {
	            String str = get(KEY_VIDEO_SNAPSHOT_SUPPORTED);
	            return TRUE.equals(str);
	        }

	        /**
	         * <p>Returns true if pdaf is supported.</p>
	         *
	         * @hide
	         * @return true if pdaf is supported.
	         */
	        public boolean isPdafSupported() {
	            String str = get(KEY_PDAF_SUPPORTED);
	            return TRUE.equals(str);
	        }

	    /**
	         * <p>Whether the recording sound can be disabled.</p>
	         *
	         * @hide
	         */
	        public void enableRecordingSound(String value) {
	            if (value.equals("1") || value.equals("0")) {
	                set(KEY_MUTE_RECORDING_SOUND, value);
	            }
	        }

	        /**
	         * <p>Enables and disables video stabilization. Use
	         * {@link #isVideoStabilizationSupported} to determine if calling this
	         * method is valid.</p>
	         *
	         * <p>Video stabilization reduces the shaking due to the motion of the
	         * camera in both the preview stream and in recorded videos, including
	         * data received from the preview callback. It does not reduce motion
	         * blur in images captured with
	         * {@link Camera#takePicture takePicture}.</p>
	         *
	         * <p>Video stabilization can be enabled and disabled while preview or
	         * recording is active, but toggling it may cause a jump in the video
	         * stream that may be undesirable in a recorded video.</p>
	         *
	         * @param toggle Set to true to enable video stabilization, and false to
	         * disable video stabilization.
	         * @see #isVideoStabilizationSupported()
	         * @see #getVideoStabilization()
	         */
	        public void setVideoStabilization(boolean toggle) {
	            set(KEY_VIDEO_STABILIZATION, toggle ? TRUE : FALSE);
	        }

	        /**
	         * Get the current state of video stabilization. See
	         * {@link #setVideoStabilization} for details of video stabilization.
	         *
	         * @return true if video stabilization is enabled
	         * @see #isVideoStabilizationSupported()
	         * @see #setVideoStabilization(boolean)
	         */
	        public boolean getVideoStabilization() {
	            String str = get(KEY_VIDEO_STABILIZATION);
	            return TRUE.equals(str);
	        }

	        /**
	         * Returns true if video stabilization is supported. See
	         * {@link #setVideoStabilization} for details of video stabilization.
	         *
	         * @return true if video stabilization is supported
	         * @see #setVideoStabilization(boolean)
	         * @see #getVideoStabilization()
	         */
	        public boolean isVideoStabilizationSupported() {
	            String str = get(KEY_VIDEO_STABILIZATION_SUPPORTED);
	            return TRUE.equals(str);
	        }
	    /**
	         * <p>Get pip max frame rate when zsd on.</p>
	         *
	         * @hide
	         */
	        public List<Integer> getPIPFrameRateZSDOn() {
	            String str = get(KEY_MAX_FRAME_RATE_ZSD_ON);
	            return splitInt(str);
	        }
	       /**
	         * <p>Get pip max frame rate when zsd off.</p>
	         *
	         * @hide
	         */
	        public List<Integer> getPIPFrameRateZSDOff() {
	            String str = get(KEY_MAX_FRAME_RATE_ZSD_OFF);
	            return splitInt(str);
	        }
	       /**
	         * Get the dynamic frame rate. return true or false
	         * @hide
	         */
	        public boolean getDynamicFrameRate() {
	            String str = get(KEY_DYNAMIC_FRAME_RATE);
	            return TRUE.equals(str);
	        }
	       /**
	         * set the dynamic frame rate.
	         * @hide
	         */
	        public void setDynamicFrameRate(boolean toggle) {
	            set(KEY_DYNAMIC_FRAME_RATE, toggle ? TRUE : FALSE);
	        }
	        /**
	         * Returns true if dynamic frame rate is supported
	         * @hide
	         */
	        public boolean isDynamicFrameRateSupported() {
	            String str = get(KEY_DYNAMIC_FRAME_RATE_SUPPORTED);
	            return TRUE.equals(str);
	        }
	       /**
	         * <p>set image reforcus jps's fine name.</p>
	         *
	         * @hide
	         */
	        public void setRefocusJpsFileName(String fineName) {
	            set(KEY_REFOCUS_JPS_FILE_NAME, fineName);
	        }

	        /**
	         * set image reforcus switch.</p>
	         *
	         * @hide
	         */
	        public void setRefocusMode(boolean toggle) {
	            set(KEY_STEREO_REFOCUS_MODE, toggle ? ON : OFF);
	        }

	        /**
	         * get image reforcus switch.</p>
	         *
	         * @hide
	         */
	        public String getRefocusMode() {
	            return get(KEY_STEREO_REFOCUS_MODE);
	        }

	        /**
	         * set Depth AF switch.</p>
	         *
	         * @hide
	         */
	        public void setDepthAFMode(boolean toggle) {
	            set(KEY_STEREO_DEPTHAF_MODE, toggle ? ON : OFF);
	        }
	        /**
	         * get Depth AF switch.</p>
	         *@hide
	         *
	         */
	        public String getDepthAFMode() {
	            return get(KEY_STEREO_DEPTHAF_MODE);
	        }

	        /**
	         * set Distance Info.</p>
	         *@hide
	         *
	         */
	        public void setDistanceMode(boolean toggle) {
	            set(KEY_STEREO_DISTANCE_MODE, toggle ? ON : OFF);
	        }

	        /**
	         * get Distance Info.</p>
	         *@hide
	         * @hide
	         */
	        public String getDistanceMode() {
	            return get(KEY_STEREO_DISTANCE_MODE);
	        }

	        // Splits a comma delimited string to an ArrayList of String.
	        // Return null if the passing string is null or the size is 0.
	        private ArrayList<String> split(String str) {
	            if (str == null) return null;

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
	            splitter.setString(str);
	            ArrayList<String> substrings = new ArrayList<String>();
	            for (String s : splitter) {
	                substrings.add(s);
	            }
	            return substrings;
	        }

	        // Splits a comma delimited string to an ArrayList of Integer.
	        // Return null if the passing string is null or the size is 0.
	        private ArrayList<Integer> splitInt(String str) {
	            if (str == null) return null;

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
	            splitter.setString(str);
	            ArrayList<Integer> substrings = new ArrayList<Integer>();
	            for (String s : splitter) {
	                substrings.add(Integer.parseInt(s));
	            }
	            if (substrings.size() == 0) return null;
	            return substrings;
	        }

	        private void splitInt(String str, int[] output) {
	            if (str == null) return;

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
	            splitter.setString(str);
	            int index = 0;
	            for (String s : splitter) {
	                output[index++] = Integer.parseInt(s);
	            }
	        }

	        // Splits a comma delimited string to an ArrayList of Float.
	        private void splitFloat(String str, float[] output) {
	            if (str == null) return;

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
	            splitter.setString(str);
	            int index = 0;
	            for (String s : splitter) {
	                output[index++] = Float.parseFloat(s);
	            }
	        }

	        // Returns the value of a float parameter.
	        private float getFloat(String key, float defaultValue) {
	            try {
	                return Float.parseFloat(mMap.get(key));
	            } catch (NumberFormatException ex) {
	                return defaultValue;
	            }
	        }

	        // Returns the value of a integer parameter.
	        private int getInt(String key, int defaultValue) {
	            try {
	                return Integer.parseInt(mMap.get(key));
	            } catch (NumberFormatException ex) {
	                return defaultValue;
	            }
	        }

	        // Splits a comma delimited string to an ArrayList of Size.
	        // Return null if the passing string is null or the size is 0.
	        private ArrayList<Size> splitSize(String str) {
	            if (str == null) return null;

	            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
	            splitter.setString(str);
	            ArrayList<Size> sizeList = new ArrayList<Size>();
	            for (String s : splitter) {
	                Size size = strToSize(s);
	                if (size != null) sizeList.add(size);
	            }
	            if (sizeList.size() == 0) return null;
	            return sizeList;
	        }

	        // Parses a string (ex: "480x320") to Size object.
	        // Return null if the passing string is null.
	        private Size strToSize(String str) {
	            if (str == null) return null;

	            int pos = str.indexOf('x');
	            if (pos != -1) {
	                String width = str.substring(0, pos);
	                String height = str.substring(pos + 1);
	                return new Size(Integer.parseInt(width),
	                                Integer.parseInt(height));
	            }
	            Log.e(TAG, "Invalid size parameter string=" + str);
	            return null;
	        }

	        // Splits a comma delimited string to an ArrayList of int array.
	        // Example string: "(10000,26623),(10000,30000)". Return null if the
	        // passing string is null or the size is 0.
	        private ArrayList<int[]> splitRange(String str) {
	            if (str == null || str.charAt(0) != '('
	                    || str.charAt(str.length() - 1) != ')') {
	                Log.e(TAG, "Invalid range list string=" + str);
	                return null;
	            }

	            ArrayList<int[]> rangeList = new ArrayList<int[]>();
	            int endIndex, fromIndex = 1;
	            do {
	                int[] range = new int[2];
	                endIndex = str.indexOf("),(", fromIndex);
	                if (endIndex == -1) endIndex = str.length() - 1;
	                splitInt(str.substring(fromIndex, endIndex), range);
	                rangeList.add(range);
	                fromIndex = endIndex + 3;
	            } while (endIndex != str.length() - 1);

	            if (rangeList.size() == 0) return null;
	            return rangeList;
	        }

	        // Splits a comma delimited string to an ArrayList of Area objects.
	        // Example string: "(-10,-10,0,0,300),(0,0,10,10,700)". Return null if
	        // the passing string is null or the size is 0 or (0,0,0,0,0).
	        private ArrayList<Area> splitArea(String str) {
	            if (str == null || str.charAt(0) != '('
	                    || str.charAt(str.length() - 1) != ')') {
	                Log.e(TAG, "Invalid area string=" + str);
	                return null;
	            }

	            ArrayList<Area> result = new ArrayList<Area>();
	            int endIndex, fromIndex = 1;
	            int[] array = new int[5];
	            do {
	                endIndex = str.indexOf("),(", fromIndex);
	                if (endIndex == -1) endIndex = str.length() - 1;
	                splitInt(str.substring(fromIndex, endIndex), array);
	                Rect rect = new Rect(array[0], array[1], array[2], array[3]);
	                result.add(new Area(rect, array[4]));
	                fromIndex = endIndex + 3;
	            } while (endIndex != str.length() - 1);

	            if (result.size() == 0) return null;

	            if (result.size() == 1) {
	                Area area = result.get(0);
	                Rect rect = area.rect;
	                if (rect.left == 0 && rect.top == 0 && rect.right == 0
	                        && rect.bottom == 0 && area.weight == 0) {
	                    return null;
	                }
	            }

	            return result;
	        }
	        private boolean same(String s1, String s2) {
	            if (s1 == null && s2 == null) return true;
	            if (s1 != null && s1.equals(s2)) return true;
	            return false;
	        }
	    };
}
