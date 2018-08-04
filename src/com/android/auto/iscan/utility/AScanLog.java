package com.android.auto.iscan.utility;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.content.Context;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AScanLog {

	private static boolean debug = true;
	private Logger gLogger;
	private static AScanLog instance;

	public static AScanLog getInstance(Context context) {
		if (instance == null) {
			instance = new AScanLog();
			instance.configLog();
		}
		return instance;
	}

	
	public void configLog() {
		final LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "barcode.txt");
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%m%n");
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

	public void disInstance() {
		instance = null;
	}
}
