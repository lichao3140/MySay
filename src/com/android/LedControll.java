package com.android;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

public class LedControll {

	private BarcodeJNI ledcontroll;// = new BarcodeJNI(this);
	private final int IOCTRL_PMU_LED_RED_ON = 0x10001;
	private final int IOCTRL_PMU_LED_RED_OFF = 0x10002;

	private final int IOCTRL_PMU_LED_BLUE_ON = 0x10003;
	private final int IOCTRL_PMU_LED_BLUE_OFF = 0x10004;

	private final int IOCTRL_PMU_LED_GREEN_ON = 0x10005;
	private final int IOCTRL_PMU_LED_GREEN_OFF = 0x10006;

	private static final int NOTIFY_red = 0x1001;
	private static final int NOTIFY_green = 0x1002;
	private static final int NOTIFY_blue = 0x1003;

	private static final int RED_ON = 0xe401;
	private static final int RED_OFF = 0xe501;

	private static final int GREEN_ON = 0xe402;
	private static final int GREEN_OFF = 0xe502;

	private static final int BLUE_ON = 0xe403;
	private static final int BLUE_OFF = 0xe503;

	private static int rgb = 0xff000000;

	final Notification notify = new Notification();

	private NotificationManager notifier = null;

	Method ledSetColor = null;
	Method setUiLight = null;
	Object oIPowerManager;

	private int dev_num = 0;

	private static final int LED_RED = 0;
	private static final int LED_GREEN = 1;
	private static final int LED_BLUE = 2;

	private static final int LIGHT_OFF = 0;
	private static final int LIGHT_ON = 1;

	public LedControll(Context context) {
		ledcontroll = new BarcodeJNI(context);
		dev_num = BarcodeJNI.GetPlatform();
		notifier = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			Class<?> ServiceManager = Class
					.forName("android.os.ServiceManager");
			Method getService = ServiceManager.getMethod("getService",
					java.lang.String.class);
			Object oRemoteService = getService.invoke(null,
					Context.POWER_SERVICE);
			Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
			Method asInterface = cStub.getMethod("asInterface",
					android.os.IBinder.class);
			oIPowerManager = asInterface.invoke(null, oRemoteService);
			if(dev_num == 21)
			ledSetColor = oIPowerManager.getClass().getMethod("ledSetColor",
					int.class);
			if(dev_num == 15||dev_num == 16)
			setUiLight = oIPowerManager.getClass().getMethod("setUiLight",
					int.class, int.class);
		} catch (Exception e) {
			Log.e("012","E::"+e);
		}
	}

	public void setUiLight(int flg, int flg1) {
		try {
			setUiLight.invoke(oIPowerManager, flg, flg1);
		} catch (Exception e) {
			e.printStackTrace();
			
			Log.e("012","E::"+e);
		}
	}

	public void SetLed(int flg) {
		try {
			ledSetColor.invoke(oIPowerManager, flg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SetRedLed(boolean on) {
		if (dev_num == 21) {
			if (on) {
				rgb |= 0xffff0000;

			} else {
				rgb &= 0xff00ffff;
			}
			SetLed(rgb);
			return;
		}

		if (dev_num == 15||dev_num == 16) {
			if (on) {
				setUiLight(LED_RED, LIGHT_ON);

			} else {
				setUiLight(LED_RED, LIGHT_OFF);
			}
			return;
		}

		if (dev_num == 4) {

			notifier.cancelAll();

			notify.flags |= Notification.FLAG_SHOW_LIGHTS;

			if (on) {
				rgb |= 0xffff0000;
				notify.ledARGB = rgb;
			} else {
				rgb &= 0xff00ffff;
				notify.ledARGB = rgb;
			}
			notify.ledOffMS = 0;
			notify.ledOnMS = 1;
			notifier.notify(NOTIFY_red, notify);
			return;
		}

		if (dev_num == 5) {

			if (on) {
				BarcodeJNI.SetGpioState(RED_ON);

			} else {
				BarcodeJNI.SetGpioState(RED_OFF);
			}

		} else {
			if (on) {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_RED_ON);

			} else {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_RED_OFF);

			}

		}

	}

	public void SetGreenLed(boolean on) {

		if (dev_num == 21) {
			if (on) {
				rgb |= 0xff00ff00;

			} else {
				rgb &= 0xffff00ff;

			}
			SetLed(rgb);
			return;
		}

		if (dev_num == 15||dev_num == 16) {
			if (on) {
				setUiLight(LED_GREEN, LIGHT_ON);

			} else {
				setUiLight(LED_GREEN, LIGHT_OFF);
			}
			return;
		}
		if (dev_num == 4) {
			notifier.cancelAll();
			notify.flags |= Notification.FLAG_SHOW_LIGHTS;
			notify.ledOffMS = 0;
			notify.ledOnMS = 1;
			if (on) {
				rgb |= 0xff00ff00;
				notify.ledARGB = rgb;
			} else {
				rgb &= 0xffff00ff;
				notify.ledARGB = rgb;
			}
			notifier.notify(NOTIFY_green, notify);
			return;
		}

		if (dev_num == 5) {

			if (on) {
				BarcodeJNI.SetGpioState(GREEN_ON);

			} else {
				BarcodeJNI.SetGpioState(GREEN_OFF);

			}

		} else {
			if (on) {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_GREEN_ON);

			} else {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_GREEN_OFF);

			}

		}

	}

	public void SetBlueLed(boolean on) {

		if (dev_num == 21) {
			if (on) {
				rgb |= 0xff0000ff;

			} else {
				rgb &= 0xffffff00;

			}
			SetLed(rgb);
			return;
		}

		if (dev_num == 15||dev_num == 16) {
			if (on) {
				setUiLight(LED_BLUE, LIGHT_ON);

			} else {
				setUiLight(LED_BLUE, LIGHT_OFF);
			}
			return;
		}

		if (dev_num == 4) {
			notifier.cancelAll();
			notify.flags |= Notification.FLAG_SHOW_LIGHTS;
			notify.ledOffMS = 0;
			notify.ledOnMS = 1;
			if (on) {
				rgb |= 0xff0000ff;
				notify.ledARGB = rgb;
			} else {
				rgb &= 0xffffff00;
				notify.ledARGB = rgb;
			}
			notifier.notify(NOTIFY_blue, notify);
			return;
		}

		if (dev_num == 5) {
			if (on) {
				BarcodeJNI.SetGpioState(BLUE_ON);

			} else {
				BarcodeJNI.SetGpioState(BLUE_OFF);
			}

		} else {
			if (on) {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_BLUE_ON);

			} else {
				BarcodeJNI.SetGpioState(IOCTRL_PMU_LED_BLUE_OFF);

			}
		}

	}

}
