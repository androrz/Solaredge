package com.solaredge.fusion;

import android.util.SparseArray;

public class SvcNames {
	public final static int WSN_UNDEFINED = -1;
	public final static int WSN_USER_LOGIN = 1;
	public final static int WSN_GET_STATION_LIST = 2;
	public final static int WSN_GET_INVERTERS = 3;

	private static SparseArray<String> mInterfaceTypeMapping = new SparseArray<String>();
	static {
		mInterfaceTypeMapping.put(WSN_USER_LOGIN, "login");
		mInterfaceTypeMapping.put(WSN_GET_STATION_LIST, "getstations");
		mInterfaceTypeMapping.put(WSN_GET_INVERTERS, "getinverters");
	}

	public static String getServiceHandler(int action) {
		return mInterfaceTypeMapping.get(action, null);
	}
}
