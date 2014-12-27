package com.solaredge.server;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lidroid.xutils.exception.DbException;
import com.solaredge.SolarApp;
import com.solaredge.entity.HttpRequestParam;
import com.solaredge.entity.Inverter;
import com.solaredge.fusion.SvcNames;
import com.solaredge.utils.DbHelp;
import com.solaredge.utils.LogX;
import com.solaredge.utils.SolarHttpUtil;

public class SolarManager {

	private TissotWorkerHandler mSolarHandler = null;
	private SolarHttpUtil mSolarHttpUtil = null;

	// Map from AlaListeners to their associated ListenerTransport objects
	private HashMap<SolarListener, ListenerTransport> mListeners = new HashMap<SolarListener, ListenerTransport>();

	private static Context mContext;

	private static SolarManager sInstance;

	private final static String TAG = "Solar-SolarManager";

	private static final int MSG_SAVE_SHOP_CITY = 0;

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

		sendHttpRequest(p, true, true);
	}

	public void getInverterList(String stationId) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_GET_INVERTERS);
		p.addParam("req_stationid", stationId);

		sendHttpRequest(p, true, true);
	}

	public void modifyInverter(String stationId, String inverterId,
			String inverterName, int groupCount, int clusterCount, int angle) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_CREATE_INVERTERS);
		p.addParam("req_stationid", stationId);
		p.addParam("req_Id", inverterId);
		p.addParam("req_label", inverterName);
		p.addParam("req_listcount", groupCount + "");
		p.addParam("req_prelistmoudler", clusterCount + "");
		p.addParam("req_tilt", angle + "");

		sendHttpRequest(p, true, true);
	}
	
	public void deleteInverter(String stationId, String inverterId) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_CREATE_INVERTERS);
		p.addParam("req_stationid", stationId);
		p.addParam("req_Id", inverterId);

		sendHttpRequest(p, true, true);
	}
	
	public void setOptimizer(String stationId, String scouter) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_CREATE_INVERTERS);
		p.addParam("req_stationid", stationId);
		p.addParam("req_scouter", scouter);

		sendHttpRequest(p, true, true);
	}

	public int[][] getInverterMatrix() {
		int[][] matrix = null;
		try {
			List<Inverter> list = DbHelp.getDbUtils(mContext).findAll(
					Inverter.class);
			int row = 0, col = 0;
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				col = Math.max(col, inverter.getmClusterNumber());
				row += inverter.getmGroupNumber();
			}
			matrix = new int[row][col];
			LogX.trace(TAG, "row: " + row + " col: " + col);
			int r = 0;
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				for (int m = 0; m < inverter.getmGroupNumber(); m++) {
					int n = 0;
					for (; n < inverter.getmClusterNumber(); n++) {
						if (inverter.getmAngle() == 0) {
							matrix[r][n] = 0;
						} else {
							matrix[r][n] = 1;
						}
					}
					if (n < col) {
						for (int z = n; z < col; z++) {
							matrix[r][z] = -1;
						}
					}
					r++;
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}

		return matrix;
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
