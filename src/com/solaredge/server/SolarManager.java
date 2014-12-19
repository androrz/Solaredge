package com.solaredge.server;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.solaredge.SolarApp;
import com.solaredge.entity.HttpRequestParam;
import com.solaredge.fusion.SvcNames;
import com.solaredge.utils.LogX;
import com.solaredge.utils.SolarHttpUtil;

public class SolarManager {

	private TissotWorkerHandler mSolarHandler = null;
	private SolarHttpUtil mSolarHttpUtil = null;
	private DbUtils dbUtils;

	// Map from AlaListeners to their associated ListenerTransport objects
	private HashMap<SolarListener, ListenerTransport> mListeners = new HashMap<SolarListener, ListenerTransport>();

	private static Context mContext;

	private static SolarManager sInstance;

	private final static String TAG = "Solar-SolarManager";

	private static final int MSG_SAVE_SHOP_CITY = 0;
	private static final int MSG_SAVE_FIX_CITY = 1;

	/**
	 * Get the singleton TissotManager instance.<br>
	 * The context must be set or illegal argument exception will be thrown.
	 * 
	 * @param context
	 *            The context
	 */
	public synchronized static SolarManager getInstance(Context context) {
		if (mContext == null) {
			if (null != context) {
				mContext = context.getApplicationContext();
			} else {
				throw new IllegalArgumentException("context can't be null!");
			}
		}

		if (null == sInstance) {
			sInstance = new SolarManager();
		}
		return sInstance;
	}

	private SolarManager() {
		mSolarHttpUtil = SolarHttpUtil.getInstance(mContext);
		mSolarHandler = new TissotWorkerHandler(SolarApp
				.getSolarHandlerThread().getLooper());
		dbUtils = DbUtils.create(mContext, "tissot");
	}

	void initialize() {
		LogX.trace(TAG, "TissotManager initialize begin...");
	}

	/**
	 * Send the HTTP request through handler which is run on a separate thread.
	 * 
	 * @param param
	 *            - The HTTP request parameters.
	 * @param closeProgress
	 *            - If close the progress dialog after receiving the HTTP
	 *            response.
	 * @param showProgress
	 *            - If show the progress before starting a HTTP request.
	 */
	private void sendHttpRequest(HttpRequestParam param, boolean showProgress,
			boolean closeProgress) {
		Bundle extraInfo = new Bundle();
		extraInfo.putBoolean("showProgress", showProgress);
		extraInfo.putBoolean("closeProgress", closeProgress);
		extraInfo.putInt("action", param.getAction());
		extraInfo.putInt("module", ListenerTransport.TYPE_TISSOT_PUBLIC);

		mSolarHttpUtil.sendHttpRequest(param, extraInfo);
	}

	public int registerCallback(SolarListener listener) {
		return registerCallback(listener, null);
	}

	/**
	 * Register the current activity to be notified when asynchronous request
	 * completes.
	 * <p>
	 * If current activity need to call some APIs of this manager which do time
	 * consuming operation, register the AlaListener implemented by current
	 * activity to this manager to receive task finish notification.
	 * <p>
	 * The calling thread must be a {@link android.os.Looper} thread such as the
	 * main thread of the calling Activity.
	 * <p>
	 * The supplied Looper is used to implement the callback mechanism.
	 * 
	 * @param listener
	 *            A {#link AlaListener} whose {@link AlaListener#handleEvent}
	 *            method will be called for each response reception.
	 * @param looper
	 *            A Looper object whose message queue will be used to implement
	 *            the callback mechanism. If looper is null then the callbacks
	 *            will be called on the main thread.
	 */
	public int registerCallback(SolarListener listener, Looper looper) {
		if (null == listener) {
			LogX.trace(TAG, "listener == null.");
			return SyncResultCode.BAD_REQUEST;
		}

		ListenerTransport transport = mListeners.get(listener);
		if (transport == null) {
			transport = new ListenerTransport(listener, looper);
		}
		mListeners.put(listener, transport);
		mSolarHttpUtil.addListenerTransport(transport);

		return SyncResultCode.SUCCESS;
	}

	/**
	 * Unregister the AlaListener or memory leak might occur.
	 * <p>
	 * Usually register the callback in the life cycle function onResume, and
	 * unregister in onPause.
	 * 
	 * @param listener
	 *            The listener to unregister.
	 */
	public void unregisterCallback(SolarListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener==null");
		}
		ListenerTransport transport = mListeners.remove(listener);
		mSolarHttpUtil.removeListenerTransport(transport);
	}

	public void userLogin(String userName, String userPassword) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_USER_LOGIN);
		p.addParam("password", userPassword);
		p.addParam("username", userName);

		sendHttpRequest(p, true, true);
	}

	public void getStationList() {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_GET_STATION_LIST);

		sendHttpRequest(p, false, true);
	}

	private class TissotWorkerHandler extends Handler {

		TissotWorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			default:
				break;
			}
		}
	}

}
