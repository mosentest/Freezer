package com.ziqi.accessibility;

import java.util.ArrayList;
import java.util.List;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.ziqi.utils.ApplicationUtils;
import com.ziqi.utils.ThreadUtils;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class BgService extends Service {

	public final static String TAG = "BgService";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		preOnStartCommand(intent, flags, startId);
		return super.onStartCommand(intent, flags, startId);
	}

	public void preOnStartCommand(Intent intent, int flags, int startId) {
		ThreadUtils.getInstance(getApplicationContext()).pushRunnable(new ThreadUtils.Task() {
			List<AndroidAppProcess> androidAppProcess;
			List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos;
			List<String> installedApplications;

			@SuppressLint("InlinedApi")
			@Override
			public void onPostExecute(Context context) {
				List<String> killPkgNames = new ArrayList<>();
				if (androidAppProcess != null) {
					for (AndroidAppProcess androidAppProcess : androidAppProcess) {
						String packageName = androidAppProcess.getPackageName();
						if (TextUtils.isEmpty(packageName)) {
							continue;
						}
						if (packageName.contains("systemui") || packageName.contains("launcher") || packageName.equals("com.sohu.inputmethod.sogou") || packageName.equals(context.getPackageName())) {
							continue;
						}
						killPkgNames.add(packageName);
					}
				}
				if (installedApplications != null) {
					for (String packageName : installedApplications) {
						if (packageName.contains("systemui") || packageName.contains("launcher") || packageName.equals("com.sohu.inputmethod.sogou") || packageName.equals(context.getPackageName())) {
							continue;
						}
						killPkgNames.add(packageName);
					}
				}
				MyAccessibilityService.INVOKE_TYPE = MyAccessibilityService.TYPE_KILL_APP;
				for (final String str : killPkgNames) {
					ThreadUtils.getInstance(getApplicationContext()).getHandler().postDelayed(new Runnable() {

						@Override
						public void run() {
							Intent killIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri packageURI = Uri.parse("package:" + str);
							killIntent.setData(packageURI);
							killIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(killIntent);
						}
					}, 2000);
				}
				int pid = Process.myPid();
				android.os.Process.killProcess(pid);
			}

			@Override
			public void doInBackground(Context context) {
				// androidAppProcess =
				// AndroidProcesses.getRunningAppProcesses();
				// runningAppProcessInfos =
				// AndroidProcesses.getRunningAppProcessInfo(context);
				installedApplications = ApplicationUtils.getInstalledApplications(getApplicationContext());
			}

		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	// com.sohu.inputmethod.sogou

}
