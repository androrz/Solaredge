package com.solaredge.fusion;

import android.os.Environment;

/**
 * Global constants.
 */
public class FusionCode {
	public static String packageName = "com.solaredge";

	public static String ETY_STR = "";

	// JSON response codes
	public static final String JSON_RSP_ERROR = "0001";
	public static final String JSON_RSP_OK = "0000";
	public static final String JSON_RSP_SIG_FAIL = "1001";
	public static final String JSON_RSP_INSUFFICIENT_PARAM = "1002";
	public static final String JSON_RSP_PARAM_NULL = "1003";
	public static final String JSON_RSP_NO_QUALIFIED_RECORDS = "1111";
	public static final String JSON_RSP_NO_RECORDS = "0002";
	public static final String JSON_RSP_EXCHANGE_FAIL = "8888";
	public static final String JSON_RSP_SVC_EXCEPTION = "9999";

	public static String DB_FILE_NAME = "solar.db";
	public final static int DB_VERSION = 1;

	// File save path definition.
	public static final String SD_CARD_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public final static String SERIALIZE_PATH = SD_CARD_DIR + "/solaredge/serialize";
}
