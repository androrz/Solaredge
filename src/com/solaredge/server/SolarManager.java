package com.solaredge.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseIntArray;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.solaredge.SolarApp;
import com.solaredge.entity.HttpRequestParam;
import com.solaredge.entity.Inverter;
import com.solaredge.entity.InverterGridItem;
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

	private static final int MSG_STORE_DELETED_GRID_ITEM = 0;
	private static final int MSG_STORE_ADDED_GRID_ITEM = 1;

	private HashMap<String, List<InverterGridItem>> mDeletedGridMap = null;
	private HashMap<String, List<InverterGridItem>> mExtraGridMap = null;

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
		if (!TextUtils.isEmpty(inverterId)) {
			p.addParam("req_id", inverterId);
		}
		p.addParam("req_label", inverterName);
		p.addParam("req_listcount", groupCount + "");
		p.addParam("req_prelistmoudler", clusterCount + "");
		p.addParam("req_tilt", angle + "");

		sendHttpRequest(p, true, true);
	}

	public void deleteInverter(String stationId, String inverterId) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_DELETE_INVERTER);
		p.addParam("req_stationid", stationId);
		p.addParam("req_inverterid", inverterId);

		sendHttpRequest(p, true, true);
	}

	public void setOptimizer(String stationId, String scouter) {
		HttpRequestParam p = new HttpRequestParam(SvcNames.WSN_SET_OPTIMIZER);
		p.addParam("req_stationid", stationId);
		p.addParam("req_scouter", scouter);

		sendHttpRequest(p, true, true);
	}

	public void storeDeletedGridItem(int row, int col) {
		Message message = Message.obtain();
		message.arg1 = row;
		message.arg2 = col;
		message.what = MSG_STORE_DELETED_GRID_ITEM;
		mSolarHandler.sendMessage(message);
	}

	public void storeAddedGridItem(InverterGridItem item) {
		Message message = Message.obtain();
		message.what = MSG_STORE_ADDED_GRID_ITEM;
		message.obj = item;
		mSolarHandler.sendMessage(message);
	}

	public int[][] getInverterMatrix() {
		int[][] matrix = null;
		try {
			List<Inverter> list = DbHelp.getDbUtils(mContext).findAll(
					Inverter.class);
			int maxRow = 0, maxCol = 0;
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				maxCol = Math.max(maxCol, inverter.getmClusterNumber()
						+ getColumnExtraLengthById(inverter.getInverterId()));
				maxRow += inverter.getmGroupNumber();
			}
			LogX.trace(TAG, "maxRow: " + maxRow + ", maxCol: " + maxCol);
			matrix = new int[maxRow][maxCol];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					matrix[i][j] = -1;
				}
			}

			int r = 0; // Total row index
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				// (m, n) is the coordinate of plates of inverter
				for (int m = 0; m < inverter.getmGroupNumber(); m++) {
					int n = 0;
					for (; n < inverter.getmClusterNumber(); n++) {
						if (isInverterGridItemDeleted(inverter.getInverterId(),
								m, n)) {
							matrix[r][n] = -1;
						} else {
							if (inverter.getmAngle() == 0) {
								matrix[r][n] = 0;
							} else {
								matrix[r][n] = 3;
							}
						}
					}

					// add extra optimizer
					List<InverterGridItem> extraItems = getRowExtraOptimizer(
							inverter.getInverterId(), m);
					if (extraItems != null && extraItems.size() > 0) {
						for (int j = 0; j < extraItems.size(); j++, n++) {
							InverterGridItem item = extraItems.get(j);
							if (item.getAngle() == 0) {
								matrix[r][n] = 0;
							} else {
								matrix[r][n] = 3;
							}
						}
					}

					if (n < maxCol) {
						for (int z = n; z < maxCol; z++) {
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

	private int getColumnExtraLengthById(String inverterId) {
		int length = 0;

		if (mExtraGridMap == null) {
			mExtraGridMap = new HashMap<String, List<InverterGridItem>>();
			try {
				List<InverterGridItem> allAddedItemList = DbHelp.getDbUtils(
						mContext).findAll(
						Selector.from(InverterGridItem.class).where("mIsNew",
								"=", "1"));
				if (allAddedItemList == null || allAddedItemList.size() == 0) {
					return 0;
				}
				for (int i = 0; i < allAddedItemList.size(); i++) {
					InverterGridItem item = allAddedItemList.get(i);
					if (!mExtraGridMap.containsKey(item.getInverterId())) {
						List<InverterGridItem> list = new ArrayList<InverterGridItem>();
						list.add(item);
						mExtraGridMap.put(item.getInverterId(), list);
					} else {
						List<InverterGridItem> list = mExtraGridMap.get(item
								.getInverterId());
						list.add(item);
					}
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}

		if (!mExtraGridMap.containsKey(inverterId)) {
			length = 0;
		} else {
			List<InverterGridItem> list = mExtraGridMap.get(inverterId);
			SparseIntArray array = new SparseIntArray();
			for (int i = 0; i < list.size(); i++) {
				InverterGridItem item = list.get(i);
				if (array.indexOfKey(item.getRow()) == -1) {
					array.put(item.getRow(), 1);
				} else {
					int val = array.get(item.getRow());
					val++;
					array.put(item.getRow(), val);
				}
			}
			int maxLength = 0;
			for (int i = 0; i < array.size(); i++) {
				int val = array.valueAt(i);
				maxLength = Math.max(maxLength, val);
			}
			length = maxLength;
		}

		return length;
	}

	private List<InverterGridItem> getRowExtraOptimizer(String inverterId,
			int row) {
		List<InverterGridItem> list = new ArrayList<InverterGridItem>();

		if (mExtraGridMap == null || mExtraGridMap.size() == 0
				|| !mExtraGridMap.containsKey(inverterId)) {
			return null;
		} else {
			List<InverterGridItem> inverterAllList = mExtraGridMap
					.get(inverterId);
			for (int i = 0; i < inverterAllList.size(); i++) {
				InverterGridItem item = inverterAllList.get(i);
				if (item.getRow() == row) {
					list.add(item);
				}
			}
		}

		return list;
	}

	private boolean isInverterGridItemDeleted(String inverterId, int r, int c) {
		boolean isDeleted = false;

		if (mDeletedGridMap == null) {
			mDeletedGridMap = new HashMap<String, List<InverterGridItem>>();
			try {
				List<InverterGridItem> allDeletedItemList = DbHelp.getDbUtils(
						mContext).findAll(
						Selector.from(InverterGridItem.class).where("mIsNew",
								"=", "0"));
				if (allDeletedItemList == null
						|| allDeletedItemList.size() == 0) {
					return false;
				}
				for (int i = 0; i < allDeletedItemList.size(); i++) {
					InverterGridItem item = allDeletedItemList.get(i);
					if (!mDeletedGridMap.containsKey(item.getInverterId())) {
						List<InverterGridItem> list = new ArrayList<InverterGridItem>();
						list.add(item);
						mDeletedGridMap.put(item.getInverterId(), list);
					} else {
						List<InverterGridItem> list = mDeletedGridMap.get(item
								.getInverterId());
						list.add(item);
					}
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}

		if (!mDeletedGridMap.containsKey(inverterId)) {
			isDeleted = false;
		} else {
			List<InverterGridItem> list = mDeletedGridMap.get(inverterId);
			for (int i = 0; i < list.size(); i++) {
				InverterGridItem item = list.get(i);
				if (item.getRow() == r && item.getCol() == c) {
					isDeleted = true;
				}
			}
		}

		return isDeleted;
	}

	private class TissotWorkerHandler extends Handler {

		TissotWorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_STORE_DELETED_GRID_ITEM:
				_handleStoreInverterGridItem(msg.arg1, msg.arg2);
				break;
			case MSG_STORE_ADDED_GRID_ITEM:
				if (msg.obj != null && msg.obj instanceof InverterGridItem) {
					_handleStoreAddedGridItem((InverterGridItem) msg.obj);
				}
			default:
				break;
			}
		}
	}

	public InverterGridItem getInverterGridByCoordinate(int row, int col) {
		InverterGridItem grid = null;

		try {
			List<Inverter> list = DbHelp.getDbUtils(mContext).findAll(
					Inverter.class);
			int r = 0, c = 0;
			int rowOfInverter = row;

			// Compute the coordinate of the inverter where we touched
			for (int i = 0; i < list.size(); i++) {
				Inverter inv = list.get(i);
				c = inv.getmClusterNumber();
				r += inv.getmGroupNumber();
				if (row + 1 <= r) {
					grid = new InverterGridItem();
					grid.setInverterId(inv.getInverterId());
					grid.setInverterName(inv.getInverterName());
					grid.setRow(rowOfInverter);
					grid.setCol(col);
					grid.setUniversalRow(row);
					grid.setUniversalCol(col);
					grid.setIsNew(false);
					break;
				} else {
					rowOfInverter = row - r;
				}
			}

			// Regenerate deleted grid map
			mDeletedGridMap = null;
			isInverterGridItemDeleted("0", 0, 0);
		} catch (DbException e) {
			e.printStackTrace();
		}

		return grid;
	}

	private void _handleStoreInverterGridItem(int row, int col) {
		try {
			List<Inverter> list = DbHelp.getDbUtils(mContext).findAll(
					Inverter.class);
			int r = 0, c = 0;
			int rowOfInverter = row;

			// Compute the coordinate of the inverter where we touched
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				c = inverter.getmClusterNumber();
				r += inverter.getmGroupNumber();
				if (row + 1 <= r) {
					String inverterId = inverter.getInverterId();
					InverterGridItem gridItem = new InverterGridItem();
					gridItem.setInverterId(inverterId);
					gridItem.setRow(rowOfInverter);
					gridItem.setCol(col);
					gridItem.setIsNew(false);

					if (col >= c) { // This is an extra optimizer
						DbHelp.getDbUtils(mContext).delete(
								InverterGridItem.class,
								WhereBuilder.b("mInverterId", "=", inverterId)
										.and("mRow", "=", rowOfInverter)
										.and("mCol", "=", col));

						// Regenerate extra grid map
						mExtraGridMap = null;
						getColumnExtraLengthById("0");
					} else {
						DbHelp.getDbUtils(mContext).save(gridItem);

						// Regenerate deleted grid map
						mDeletedGridMap = null;
						isInverterGridItemDeleted("0", 0, 0);
					}
				} else {
					rowOfInverter = row - r;
				}
			}

			// Regenerate deleted grid map
			mDeletedGridMap = null;
			isInverterGridItemDeleted("0", 0, 0);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void _handleStoreAddedGridItem(InverterGridItem item) {
		try {
			Inverter inverter = DbHelp.getDbUtils(mContext).findById(
					Inverter.class, item.getInverterId());
			int extraColumnOfRow = 0;
			List<InverterGridItem> list = getRowExtraOptimizer(
					item.getInverterId(), item.getRow());
			if (list != null) {
				extraColumnOfRow = list.size();
			}
			item.setCol(inverter.getmClusterNumber() + extraColumnOfRow);

			DbHelp.getDbUtils(mContext).save(item);

			// Regenerate extra grid map
			mExtraGridMap = null;
			getColumnExtraLengthById("0");
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

}
