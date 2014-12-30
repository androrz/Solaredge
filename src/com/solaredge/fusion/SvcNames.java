package com.solaredge.fusion;

import android.util.SparseArray;

public class SvcNames {
	public final static int WSN_UNDEFINED = -1;
	public final static int WSN_USER_LOGIN = 1;
	public final static int WSN_GET_STATION_LIST = 2;
	public final static int WSN_GET_INVERTERS = 3;
	public final static int WSN_CREATE_INVERTERS = 4;
	public final static int WSN_DELETE_INVERTER = 5;
	public final static int WSN_SET_OPTIMIZER = 6;

	private static SparseArray<String> mInterfaceTypeMapping = new SparseArray<String>();
	static {
		mInterfaceTypeMapping.put(WSN_USER_LOGIN, "login");
		mInterfaceTypeMapping.put(WSN_GET_STATION_LIST, "getstations");
		mInterfaceTypeMapping.put(WSN_GET_INVERTERS, "getinverters");
		mInterfaceTypeMapping.put(WSN_CREATE_INVERTERS, "createinverter");
		mInterfaceTypeMapping.put(WSN_DELETE_INVERTER, "deleteinverter");
		mInterfaceTypeMapping.put(WSN_SET_OPTIMIZER, "createscouter2");
	}

	public static String getServiceHandler(int action) {
		return mInterfaceTypeMapping.get(action, null);
	}
}
