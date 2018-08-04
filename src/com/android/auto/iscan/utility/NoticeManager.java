package com.android.auto.iscan.utility;

import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.android.auto.iscan.R;
import com.android.auto.iscan.ScannerServices;
import com.android.auto.iscan.activity.ASettingActivity;
import com.android.auto.iscan.activity.SettingActivity;

public class NoticeManager {

	private ScannerServices mContext;
	private boolean NotificationEnabled=false;
	public NoticeManager(ScannerServices context) {
		this.mContext = context;
	}

	
	public void setNotificationEnabled(boolean Enable){
		NotificationEnabled=Enable;
		if(NotificationEnabled){
			showNotification();
		}else{
			CancerNotification();
		}
	}
	
	private void showNotification() {
		Notification notification;
		Intent intent = new Intent();
		intent.setClass(mContext, ASettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext,(new Random().nextInt(100)),
				intent, 0);
		Notification.Builder builder = new Notification.Builder(mContext)
				.setAutoCancel(true).setContentTitle("iScan")
				.setContentText("").setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.iscan)
				.setWhen(System.currentTimeMillis()).setOngoing(true);
		notification = builder.getNotification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;  
		notification.flags |= Notification.FLAG_NO_CLEAR;  
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;  
		mContext.startForeground(0x1982, notification);

	}

	public void CancerNotification() {

		mContext.stopForeground(true);

	}

}
