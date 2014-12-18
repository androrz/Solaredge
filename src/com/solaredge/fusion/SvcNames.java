package com.solaredge.fusion;

import android.util.SparseArray;

public class SvcNames {
	public final static int WSN_UNDEFINED = -1;

	private static SparseArray<String> mInterfaceTypeMapping = new SparseArray<String>();
	static {
	}

	public static String getServiceHandler(int action) {
		return mInterfaceTypeMapping.get(action, null);
	}
}
