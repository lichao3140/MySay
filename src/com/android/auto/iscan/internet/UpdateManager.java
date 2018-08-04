package com.android.auto.iscan.internet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.auto.iscan.R;
import com.android.auto.iscan.utility.ScanLog;

public class UpdateManager {

	private static final int DOWNLOAD = 1;

	private static final int DOWNLOAD_FINISH = 2;

	private static final int DOWN_ERROR = 3;

	private static final int GET_UNDATAINFO_ERROR = 4;

	private static final int UPDATA_CLIENT = 5;
	private static final int NO_UPDATA = 6;

	private static final int Get_APKINFOR = 7;

	private int serviceCode = 0;

	private Context mContext;
	private boolean cancelUpdate = false;

	private URL url;
	private  String fileName=null;
	private String appkey;
	private String serverUrl;

	private String serverAPkVersion = null;
	private String apkUrl = null;
	
	private String mSavePath;
	
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private int progress;

	public UpdateManager(Context context) {
		this.mContext = context;
		appkey = GetWebData.GetAppKey();
		serverUrl = GetWebData.GetServerUrl();
		try {
			url = new URL(serverUrl + "?appKey=" + appkey);
			

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void Checkupdate(){
		new CheckVersionTask().start();
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPDATA_CLIENT:
				showNoticeDialog();
				break;

			case DOWNLOAD:

				mProgress.setProgress(progress);
				break;

			case NO_UPDATA:

				Toast.makeText(mContext, R.string.soft_update_no,
						Toast.LENGTH_LONG).show();
				break;
			case DOWNLOAD_FINISH:

				installApk();
				break;
			case DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(mContext, "下载新版本失败", 1).show();

				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(mContext, "获取服务器更新信息失败", 1).show();
				break;

			default:
				break;
			}
		};
	};

	private int getAPPVersionCodeFromAPP(Context ctx) {
		int currentVersionCode = 0;
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			String appVersionName = info.versionName; // 版本名
			currentVersionCode = info.versionCode; // 版本号
			System.out.println(currentVersionCode + " " + appVersionName);
		} catch (Exception e) {
			// TODO Auto-generated catch blockd
			e.printStackTrace();
		}
		return currentVersionCode;
	}

	private class CheckVersionTask extends Thread {

		public void run() {
			GetApkUrl();
		}

	}

	private static JSONObject getJSONObject(String str) {
		if (str == null)
			return null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace(System.err);
			return null;
		}
		return jsonObject;
	}

	private boolean isUpdate() {
		// 获取当前软件版本
		int versionCode = getAPPVersionCodeFromAPP(mContext);
		if (serviceCode > versionCode) {
			return true;
		}
		return false;
	}

	private String GetApkUrl() {
		String result = null;
		try {
			result = GetWebData.getInfobyget(url);
			JSONObject jsonObject = getJSONObject(result);
			try {
				JSONObject jsonObject1 = jsonObject.getJSONObject("rtn");
				serviceCode = jsonObject1.getInt("versionCode");
				apkUrl = jsonObject1.getString("url");
				fileName=jsonObject1.getString("fileName");
				if(isUpdate()){
					mHandler.sendEmptyMessage(UPDATA_CLIENT);
				}else{
					mHandler.sendEmptyMessage(NO_UPDATA);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	private void showNoticeDialog() {

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);

		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						showDownloadDialog();
					}
				});

		builder.setNegativeButton(R.string.soft_update_later,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {

		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);

		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();

		downloadApk();
	}

	private void downloadApk() {

		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {

					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					
					URL url = new URL(apkUrl);

					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();

					int length = conn.getContentLength();

					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);

					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, fileName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;

					byte buf[] = new byte[1024];

					do {
						int numread = is.read(buf);
						count += numread;

						progress = (int) (((float) count / length) * 100);

						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);//
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				mHandler.sendEmptyMessage(DOWNLOAD_FINISH);

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mDownloadDialog.dismiss();
		}
	};
	private void installApk() {
		File apkfile = new File(mSavePath,fileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

}
