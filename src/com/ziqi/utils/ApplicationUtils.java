package com.ziqi.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ApplicationUtils {
	public static List<String> getInstalledApplications(Context context) {
		List<String> pkgName = new ArrayList<>();
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (ApplicationInfo applicationInfo : listAppcations) {
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				continue;
			}
			pkgName.add(applicationInfo.packageName);
		}
		return pkgName;
	}
}
