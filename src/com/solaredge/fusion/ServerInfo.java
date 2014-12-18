package com.solaredge.fusion;

/**
 * Server configuration.
 */
public class ServerInfo {

	public static final boolean IS_RELEASE = false;

	/**
	 * Configure if log is available in a release version.
	 */
	public static boolean LOG_ENABLE_ON_RELEASE = true;

	private static final String WEB_SERVICE_URL_TEST = "";
	private static String WEB_SERVICE_URL_RELEASE = "";

	public static String WEB_SERVICE_URL = (IS_RELEASE ? WEB_SERVICE_URL_RELEASE
			: WEB_SERVICE_URL_TEST)
			+ "servicehandler.ashx";
}
