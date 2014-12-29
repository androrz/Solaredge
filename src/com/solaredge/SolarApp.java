package com.solaredge;

import java.util.Locale;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;

import com.solaredge.config.PreferenceFactory;
import com.solaredge.entity.SolarUser;
import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.FusionField;
import com.solaredge.utils.LogX;

public class SolarApp extends Application {
	private static SolarApp sSolarApp;
	private final String TAG = "Solar-App";
	private static HandlerThread mSolarHandlerThread = null;

	synchronized public static SolarApp getApplication() {
		return sSolarApp;
	}

	private static Handler mAppHandler = new AppHandler();

	static class AppHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (sSolarApp == null || msg == null) {
				return;
			}

			switch (msg.what) {
			default:
				break;
			}

		}
	}

	public static synchronized Handler getAppHandler() {
		if (mAppHandler == null) {
			mAppHandler = new AppHandler();
		}
		return mAppHandler;
	}

	/**
	 * Some event like screen rotation will trigger this method, with system
	 * default locale in configuration. Should override the locale to which user
	 * set in application preference.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setLocale();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogX.trace(TAG, "Solar app on create");

		sSolarApp = this;
		setLocale();

		initSystemParams();
		initNetworkState();
		initSolarUser();
	}

	public void setLocale() {
		Locale locale = Locale.getDefault();
		String language = PreferenceFactory.getDefaultPreference()
				.getAppLanguage();
		if (!language.equals("")) {
			locale = new Locale(language);
			Locale.setDefault(locale);
		}
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	private void initSolarUser() {
		FusionField.solarUser = new SolarUser();
		FusionField.solarUser.setUdid(getUdid());
	}

	private String getUdid() {
		String androidId = android.provider.Settings.Secure.getString(
				getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				androidId.hashCode() << 32);
		String uniqueId = deviceUuid.toString();
		LogX.trace(TAG, "Device UDID: " + uniqueId);
		return uniqueId;
	}

	private void initSystemParams() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		FusionField.devicePixelsWidth = dm.widthPixels;
		FusionField.devicePixelsHeight = dm.heightPixels;
		FusionField.deviceDensity = dm.density;

		LogX.trace(TAG, "Device Density: " + FusionField.deviceDensity);
		LogX.trace(TAG, "Device Dimension in Px: "
				+ FusionField.devicePixelsWidth + "*"
				+ FusionField.devicePixelsHeight);
	}

	private void initNetworkState() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetworkInfo != null) {
			FusionField.networkConntected = activeNetworkInfo.isConnected();
			LogX.trace(TAG, "current active network = "
					+ FusionField.networkConntected);
		}
	}

	public static String getAppVersionName(Context context) {
		String pName = FusionCode.packageName;
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					pName, PackageManager.GET_CONFIGURATIONS);
			String versionName = pinfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	/** 接口层调用，获取SolarHandlerThread对象，如果该对象不存在，则新建，如果存在但没启动，则启动 */
	public static HandlerThread getSolarHandlerThread() {
		// 创建并启动xmppHandlerThread.
		createAndStartSolarHandlerThread();
		return mSolarHandlerThread;
	}

	private static synchronized void createAndStartSolarHandlerThread() {
		if (null == mSolarHandlerThread
				|| mSolarHandlerThread.getState() == Thread.State.TERMINATED) {
			mSolarHandlerThread = new HandlerThread("SolarHandlerThread");
			mSolarHandlerThread.start();
		} else if (!mSolarHandlerThread.isAlive()) {
			// 如果线程没有启动，则启动该线程
			mSolarHandlerThread.start();
		}
	}
}
