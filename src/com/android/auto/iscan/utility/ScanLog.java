package com.android.auto.iscan.utility;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.content.Context;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class ScanLog {

	private static boolean debug = true;
	private Logger gLogger;
	private static ScanLog instance;

	public static ScanLog getInstance(Context context) {
		if (instance == null) {
			instance = new ScanLog();
			instance.configLog();
			
		}
		return instance;
	}

	public void configLog() {
		final LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "iScan/iscan.txt");
		logConfigurator.setRootLevel(Level.DEBUG);
	//	logConfigurator.setRootLevel(Level.INFO);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		 logConfigurator.setFilePattern(" %m%n");
		logConfigurator.setMaxFileSize(5242880L);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
		gLogger = Logger.getLogger("iscan");
	}
	
	public void LOGD(String var0) {
		if (debug) {
			gLogger.debug(var0);
		}
	}

	public void LOGI(String var0) {
		if (debug) {
			gLogger.info(var0);
		}
	}

	public void LOGE(String var0) {
		if (debug) {
			gLogger.error(var0);
		}
	}
}
