package com.android.auto.iscan.utility;

public class Gather {

	//Action
	public final static String REQUEST = "extra.mdm.request";
	public final static String RESPONE = "extra.mdm.respone";
	
	//Message
	public static final int HANDLER_WHAT_FOR_BROADCAST_RECEIVER = 120;
	
	//Path
	public final static String IDATADIR = "/IDATA/";
	public final static String MDMDIR = "/IDATA/MDM/";
	public final static String WHITELISTDIR = "/IDATA/MDM/WHITELISTPATH/";
	public final static String OSDIR = "/IDATA/MDM/OSPATH/";
	public final static String LOGDIR = "/IDATA/MDM/LOG/";
	
	//Cmd
	public final static int CMD_SYSTEM_GET_APILEVEL = 0x0001;
	public final static int CMD_SYSTEM_GET_DEVICETYPE = 0x0002;
	public final static int CMD_SYSTEM_GET_DEVICESN = 0x0003;
	public final static int CMD_SYSTEM_SET_REBOOT = 0x0004;
	public final static int CMD_SYSTEM_SET_SHUTDOWN = 0x0005;
	public final static int CMD_SYSTEM_SET_TIME = 0x0006;
	public final static int CMD_SYSTEM_SET_BAREXPAND = 0x0007;
	public final static int CMD_SYSTEM_GET_BAREXPAND = 0x0008;
	public final static int CMD_SYSTEM_SET_USBDEBUG = 0x0009;
	public final static int CMD_SYSTEM_GET_USBDEBUG = 0x000A;
	public final static int CMD_SYSTEM_SET_SCREENLOCK = 0x000B;
	public final static int CMD_SYSTEM_GET_SCREENLOCK = 0x000C;
	public final static int CMD_SYSTEM_SET_SCREENLOCKPASSWORD = 0x000D;
	public final static int CMD_SYSTEM_GET_SWITCHKEYBOARD = 0x000E;
	public final static int CMD_SYSTEM_SET_APKINSTALLWHITELIST = 0x000F;
	public final static int CMD_SYSTEM_GET_APKINSTALLWHITELIST = 0x0010;
	public final static int CMD_SYSTEM_SET_UPDATAAPKWHITELIST = 0x0011;
	public final static int CMD_SYSTEM_SET_APKINSTALLUI = 0x0012;
	public final static int CMD_SYSTEM_SET_APKINSTALLAWITHSILENCE = 0x0013;
	public final static int CMD_SYSTEM_SET_APKUNINSTALL = 0x0014;
	public final static int CMD_SYSTEM_GET_APKUNINSTALL = 0x0015;
	
	public final static int CMD_SYSTEM_SET_HOMEENABLE = 0x0021;
	public final static int CMD_SYSTEM_GET_HOMEENABLE = 0x0022;
	
	public final static int CMD_SYSTEM_SET_INPUTSTRING=0x0023;
	public final static int CMD_SYSTEM_SET_INPUTKEYCODE=0x0024;
	public final static int CMD_SYSTEM_SET_DATAROAM = 0x0025;
	public final static int CMD_SYSTEM_GET_DATAROAM = 0x0026;
	
		
	public final static int CMD_FOTA_SET_OSUPDATA = 0x0101;
		
	public final static int CMD_MODULE_SET_GPS = 0x0201;
	public final static int CMD_MODULE_GET_GPS = 0x0202;
	public final static int CMD_MODULE_SET_WIFI = 0x0203;
	public final static int CMD_MODULE_GET_WIFI = 0x0204;
	public final static int CMD_MODULE_SET_BLUETOOTH = 0x0205;
	public final static int CMD_MODULE_GET_BLUETOOTH = 0x0206;
	public final static int CMD_MODULE_SET_MOBILEDATA = 0x0207;
	public final static int CMD_MODULE_GET_MOBILEDATA = 0x0208;
	public final static int CMD_MODULE_SET_EARPHONEVOL = 0x0209;
	public final static int CMD_SYSTEM_GET_EARPHONEVOL = 0x020A;
	public final static int CMD_MODULE_SET_SPEAKERVOL = 0x020B;
	public final static int CMD_MODULE_GET_SPEAKERVOL = 0x020C;
	public final static int CMD_MODULE_SET_HUMMERVOL = 0x020D;
	public final static int CMD_SYSTEM_GET_HUMMERVOL = 0x020E;
	public final static int CMD_MODULE_SET_CAMERA = 0x020F;
	public final static int CMD_MODULE_SET_PHONE = 0x0210;
	public final static int CMD_MODULE_SET_SMS = 0x0211;
	public final static int CMD_MODULE_SET_BROWSER = 0x0212;

}
