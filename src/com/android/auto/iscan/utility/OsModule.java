package com.android.auto.iscan.utility;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class OsModule{
	
	private Context mcontext;
	private Handler mhandler;
	private BroadcastReceiver mBroadcastReceiver;
	
	/*
	 * 函数介绍：类构造函数，初始化
	 * 输入参数：上下文context，消息句柄handler
	 * 输出参数：void
	 */
	public OsModule(Context context, Handler handler){
		
		mcontext = context;
		mhandler = handler;
		checkdir();
		
	}
	
	/*
	 * 函数介绍：打开广播监听函数，注册监听器
	 * 输入参数：void
	 * 输出参数：void
	 */
	public void OpenMonitor(){

		mBroadcastReceiver = new Receive();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Gather.RESPONE);
		mcontext.registerReceiver(mBroadcastReceiver, intentFilter);
	}
	
	/*
	 * 函数介绍：关闭广播监听函数，反注册监听器
	 * 输入参数：void
	 * 输出参数：void
	 */
	public void CloseMonitor(){

		mcontext.unregisterReceiver(mBroadcastReceiver);
	}
	
	/*
	 * 函数介绍：发送广播
	 * 输入参数：时间戳timestamp，请求命令cmd
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, "null", "null", "null", "null");
	}
	
	/*
	 * 函数介绍：发送广播的多态函数
	 * 输入参数：时间戳timestamp，请求命令cmd，请求参数para，请求参数值value
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd, String para, int value){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		_intent.putExtra(para, value);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, para, String.valueOf(value), "null", "null");
	}
	
	

	
	/*
	 * 函数介绍：发送广播的多态函数
	 * 输入参数：时间戳timestamp，请求命令cmd，请求参数para，请求参数值value
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd, String para, boolean value){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		_intent.putExtra(para, value);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, para, String.valueOf(value), "null", "null");
	}

	/*
	 * 函数介绍：发送广播的多态函数
	 * 输入参数：时间戳timestamp，请求命令cmd，请求参数para，请求参数值value
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd, String para, long value){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		_intent.putExtra(para, value);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, para, String.valueOf(value), "null", "null");
	}

	/*
	 * 函数介绍：发送广播的多态函数
	 * 输入参数：时间戳timestamp，请求命令cmd，请求参数para，请求参数值value
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd, String para, String value){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		_intent.putExtra(para, value);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, para, value, "null", "null");
	}
	
	/*
	 * 函数介绍：发送广播的多态函数
	 * 输入参数：时间戳timestamp，请求命令cmd，请求参数para1，请求参数值value1，请求参数para2，请求参数值value2
	 * 输出参数：void
	 */
	public void SendBroadcast(long timestamp, int cmd, String para1, int value1, String para2, String value2){
		Intent _intent = new Intent(Gather.REQUEST);

		_intent.putExtra("Timestamp", timestamp);
		_intent.putExtra("Cmd", cmd);
		_intent.putExtra(para1, value1);
		_intent.putExtra(para2, value2);
		
		mcontext.sendBroadcast(_intent);
		writetolog(Gather.REQUEST, timestamp, cmd, para1, String.valueOf(value1), para2, value2);
	}

	/*
	 * 类介绍：广播监听器，接收广播并传递消息
	 */
	private class Receive extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Gather.RESPONE)){
				
				writetolog(Gather.RESPONE, 
						intent.getLongExtra("Timestamp", -1), 
						intent.getIntExtra("Cmd", -1), 
						intent.getIntExtra("Result", -1), 
						intent.getIntExtra("Status", -1), 
						intent.getStringExtra("Data"), 
						intent.getIntExtra("Errorcode", -1), 
						intent.getStringExtra("Errormessage"));
				
				if ((intent.getLongExtra("Timestamp", -1) > 0) && 
						(intent.getIntExtra("Cmd", -1) > 0)){
					
					Message msg = new Message();
					msg.what = Gather.HANDLER_WHAT_FOR_BROADCAST_RECEIVER;
					Bundle mbundle = new Bundle();
					mbundle.putLong("Timestamp", intent.getLongExtra("Timestamp", -1));
					mbundle.putInt("Cmd", intent.getIntExtra("Cmd", -1));
					mbundle.putInt("Result", intent.getIntExtra("Result", -1));
					mbundle.putInt("Status", intent.getIntExtra("Status", -1));
					mbundle.putString("Data", intent.getStringExtra("Data"));
					mbundle.putInt("Errorcode", intent.getIntExtra("Errorcode", -1));
					mbundle.putString("Errormessage", intent.getStringExtra("Errormessage"));
					msg.setData(mbundle);
					mhandler.sendMessage(msg);
				}
			}
		}
	}
	
	
	/*
	 * 函数介绍：查询目录是否存在，不存在则创建
	 * 输入参数：void
	 * 输出参数：返回值0表示已存在，返回值1表示目录不完整并重新创建成功，返回值2表示目录不完成且重新创建失败
	 */
	private int checkdir(){
		int result = 0;
		String[] PATH = {Gather.WHITELISTDIR, Gather.OSDIR, Gather.LOGDIR};
		for (int i = 0; i < 3; i++){
			File file = new File(Environment.getExternalStorageDirectory().getPath(), PATH[i]);
			if (!file.exists() && !file.isDirectory()){
				if(file.mkdirs()){
					result = 1;
				} else {
					result = 2;
				}
			}
		}
		return result;
	}
	
	/*
	 * 函数介绍：写入日志，发出的广播消息写入日志
	 * 输入参数：广播类型type，时间戳timestamp，请求命令cmd，请求参数para1，请求参数值value1，请求参数para2，请求参数值value2
	 * 输出参数：void
	 */
	private void writetolog(String type, long timestamp, int cmd, String para1, String value1, String para2, String value2){
		
		String LOGPATH = Gather.LOGDIR+"logfile.txt";
		BufferedWriter bWrite;
		try {
			bWrite = new BufferedWriter(
					new OutputStreamWriter(
					new BufferedOutputStream(
					new DataOutputStream(
					new FileOutputStream(
					new File(Environment.getExternalStorageDirectory().getPath(), LOGPATH), true)))));
			bWrite.write(type + ",");
			bWrite.write(timestamp + ",");
			bWrite.write(cmd + ",");
			bWrite.write(para1 + ",");
			bWrite.write(value1 + ",");
			bWrite.write(para2 + ",");
			bWrite.write(value2 + "\n");
			bWrite.flush();
			bWrite.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 函数介绍：写入日志的多态函数，收到的广播消息写入日志
	 * 输入参数：广播类型type，时间戳timestamp，请求命令cmd，执行结果result，返回参数status，返回参数data，错误码errorcode，错误提示信息errormsg
	 * 输出参数：void
	 */
	private void writetolog(String type, long timestamp, int cmd, int result, int status, String data, int errorcode, String errormsg){
		
		String LOGPATH = Gather.LOGDIR+"logfile.txt";
		BufferedWriter bWrite;
		try {
			bWrite = new BufferedWriter(
					new OutputStreamWriter(
					new BufferedOutputStream(
					new DataOutputStream(
					new FileOutputStream(
					new File(Environment.getExternalStorageDirectory().getPath(), LOGPATH), true)))));
			bWrite.write(type + ",");
			bWrite.write(timestamp + ",");
			bWrite.write(cmd + ",");
			bWrite.write(result + ",");
			bWrite.write(status + ",");
			bWrite.write(data + ",");
			bWrite.write(errorcode + ",");
			bWrite.write(errormsg + "\n");
			bWrite.flush();
			bWrite.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
