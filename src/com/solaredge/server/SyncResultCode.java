package com.solaredge.server;

/**
 * Return code of service interface. It's the synchronous return code different
 * from asynchronous return code of {@link AsyncResultCode}.
 */
public class SyncResultCode {

	/**
	 * The parameters not appropriate
	 */
	public final static int BAD_REQUEST = -2;

	public final static int NONETWORK = -1;

	/**
	 * User not registered
	 */
	public final static int NOT_REGISTER = -3;

	/**
	 * Request submitted successfully
	 */
	public final static int SUCCESS = 0;

	public final static int UNDEFINED = -404;

	/**
	 * Return human readable string for return code.
	 * 
	 * @param returnCode
	 *            The code
	 */
	public static String getCodeString(int returnCode) {
		String readable = "";

		switch (returnCode) {
		case SUCCESS:
			readable = "success";
			break;
		case NONETWORK:
			readable = "no network";
			break;
		case BAD_REQUEST:
			readable = "bad request";
			break;
		case NOT_REGISTER:
			readable = "not register";
			break;
		default:
			break;
		}

		return readable;
	}
}
