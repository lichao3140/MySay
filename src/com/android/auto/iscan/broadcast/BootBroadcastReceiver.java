package com.android.auto.iscan.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.auto.iscan.activity.StartServicesActivity;
import com.android.auto.iscan.utility.ConstantUtil;
import com.android.auto.iscan.utility.Variable;

public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConstantUtil.ISCAN_BOOT_ACTION)) {
			if (Variable.getInstance(context).GetAotuStartState()) {
				Intent ootStartIntent = new Intent(context, StartServicesActivity.class);
				ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(ootStartIntent);
				
			
			}
		}
	}
}
