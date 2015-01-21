package com.solaredge.utils;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.FusionField;

public class DbHelp {

	private static DbUtils mDbUtils;
	private static final String TAG = "Alading-DbHelp";

	public synchronized static DbUtils getDbUtils(Context appContext) {
		if (mDbUtils == null) {
			DbUpgradeListener mUpgradeListener = new DbUpgradeListener() {

				@Override
				public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
					LogX.trace(TAG, "oldVersion: " + oldVersion
							+ " newVersion: " + newVersion);
				}
			};
			mDbUtils = DbUtils.create(appContext, FusionField.dbName,
					FusionCode.DB_VERSION, mUpgradeListener);
		}

		return mDbUtils;
	}

	public static void reset() {
		mDbUtils = null;
	}

}
