package com.solaredge.server;

import com.solaredge.fusion.FusionCode;
import com.solaredge.server.response.SlrResponse;

/**
 * Result code returned by service interface. It's returned to upper layer
 * asynchronously through the resultCode parameter of
 * {@link AlaEventListener#handleEvent(int resultCode, SlrResponse response )}.
 */
public class AsyncResultCode {

	/**
	 * In any situation that the returned result was what we wanted is a
	 * failure. We use <code>FAIL</code> to identify this.
	 */
	public static final int FAIL = -1;

	/**
	 * Network error like socket connection time out, etc.
	 */
	public static final int NETWORK_ERROR = -2;

	/**
	 * The signature could not be verified.
	 */
	public static final int SIGNATURE_VERIFICATION_FAIL = -3;

	/**
	 * Some of parameters required was not provided.
	 */
	public static final int INSUFFICIENT_PARAMETERS = -4;

	/**
	 * Some request parameter contain NULL vale which is not allowed.
	 */
	public static final int PARAMETER_VALUE_NULL = -5;

	/**
	 * The request message format error. The caller did not provide sufficient
	 * parameters or some command server not recognize.
	 */
	public final static int BAD_REQUEST = -6;

	public final static int EXCHANGE_FAIL = -7;

	public final static int SVC_EXCEPTION = -8;

	/**
	 * Successfully received the response of request.
	 */
	public static final int SUCCESS = 0;

	public static final int UNDEFINED = 1;

	public static int getResultFromJsonCode(String code) {
		int resultCode = UNDEFINED;

		if (code.equals(FusionCode.JSON_RSP_OK)) {
			resultCode = SUCCESS;
		} else if (code.equals(FusionCode.JSON_RSP_ERROR)) {
			resultCode = FAIL;
		} else if (code.equals(FusionCode.JSON_RSP_SIG_FAIL)) {
			resultCode = SIGNATURE_VERIFICATION_FAIL;
		} else if (code.equals(FusionCode.JSON_RSP_INSUFFICIENT_PARAM)) {
			resultCode = INSUFFICIENT_PARAMETERS;
		} else if (code.equals(FusionCode.JSON_RSP_PARAM_NULL)) {
			resultCode = PARAMETER_VALUE_NULL;
		} else if (code.equals(FusionCode.JSON_RSP_EXCHANGE_FAIL)) {
			resultCode = EXCHANGE_FAIL;
		} else if (code.equals(FusionCode.JSON_RSP_SVC_EXCEPTION)) {
			resultCode = SVC_EXCEPTION;
		}

		return resultCode;
	}

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
		case NETWORK_ERROR:
			readable = "network error";
			break;
		case BAD_REQUEST:
			readable = "bad request";
			break;
		case FAIL:
			readable = "fail";
			break;
		case SIGNATURE_VERIFICATION_FAIL:
			readable = "signature verification fail";
			break;
		case INSUFFICIENT_PARAMETERS:
			readable = "insufficient parameters";
			break;
		case PARAMETER_VALUE_NULL:
			readable = "parameter value null";
			break;
		default:
			break;
		}

		return readable;
	}
}
