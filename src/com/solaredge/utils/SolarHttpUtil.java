package com.solaredge.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.solaredge.config.AppConfig;
import com.solaredge.entity.HttpRequestParam;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.FusionField;
import com.solaredge.server.AsyncResultCode;
import com.solaredge.server.ListenerTransport;
import com.solaredge.server.SolarCommonResponse;
import com.solaredge.server.response.SlrResponse;

public class SolarHttpUtil {
	private HttpUtils mHttpClient = new HttpUtils();
	private static Context mContext;
	private static SolarHttpUtil sInstance;
	private ArrayList<ListenerTransport> mListenerTransports = new ArrayList<ListenerTransport>();

	private static final String TAG = "Solar-HttpClient";

	/**
	 * Get the singleton point exchange manager instance.<br>
	 * The context must be set or illegal argument exception will be thrown.
	 * 
	 * @param context
	 *            The context
	 */
	public synchronized static SolarHttpUtil getInstance(Context context) {
		if (mContext == null) {
			if (null != context) {
				mContext = context.getApplicationContext();
			} else {
				throw new IllegalArgumentException("context can't be null!");
			}
		}

		if (null == sInstance) {
			sInstance = new SolarHttpUtil();
		}
		return sInstance;
	}

	public SolarHttpUtil() {
		mHttpClient.configTimeout(10000);
	}

	public synchronized void sendHttpRequest(
			final HttpRequestParam requestParam, final Bundle extraInfo) {
		// Show a progress bar on current page if necessary.
		sendShowProgressMessage(extraInfo);

		RequestCallBack<String> asyncHandler = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogX.trace(TAG, "FAILURE\n" + arg1);
				handleHttpResultFail(arg0, arg1, extraInfo);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogX.trace(TAG, "SUCCESS\n+++++++++HTTP Response++++++++"
						+ arg0.result);
				handleHttpResultSuccess(arg0.result, extraInfo);
			}
		};

		requestParam.addParam("req_session", FusionField.solarUser.getMemberID());
		requestParam.addParam("req_language", "0");
		requestParam.addParam("format", "json");
		requestParam.addParam("partner", "Tspec");
		requestParam.addParam("v", "1.0");
		RequestParams params = toRequestParam(requestParam);

		String requestUrl = AppConfig.getServerUrl(mContext);
		mHttpClient.send(HttpMethod.POST, requestUrl, params, asyncHandler);
	}

	private RequestParams toRequestParam(HttpRequestParam requestParam) {
		RequestParams params = new RequestParams();
		StringBuilder builder = new StringBuilder();
		List<String> nameList = Arrays.asList(requestParam.paramNameArray());
		Collections.sort(nameList);
		for (String name : nameList) {
			LogX.trace(TAG, name);
			String value = requestParam.getParam(name);
			if (value == null) {
				value = "";
			}

			if (builder.length() != 0) {
				builder.append("&");
			}
			builder.append(name).append("=").append(value);
			
			if (name.equals("password")) {
				value = EncryptUtil.encrypt(value.getBytes(),
						AppConfig.SECURITY_KEY.substring(0, 9).getBytes());
			}
			params.addBodyParameter(name, value);

		}

		String paramsToSign = builder.toString() + "&" + AppConfig.SECURITY_KEY;
		LogX.trace(TAG, "params to sign: " + paramsToSign);
		String signature = EncryptUtil.MD5(paramsToSign);
		params.addBodyParameter("sign", signature);

		LogX.trace(TAG, "---------HTTP REQUEST---------" + builder.toString()
				+ "&sign=" + signature);

		return params;
	}

	public synchronized void sendHttpRequest(HttpRequestParam requestParam,
			final Handler handler, final int what) {
		RequestCallBack<String> asyncHandler = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Message message = handler.obtainMessage(what, arg1);
				handler.sendMessage(message);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogX.d(TAG, "--------------HTTP Response-------------"
						+ arg0.result);

				JsonResponse jsonResponse = null;
				try {
					jsonResponse = new JsonResponse(arg0.result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(handler.obtainMessage(what, jsonResponse));
			}
		};
		RequestParams params = toRequestParam(requestParam);
		String requestUrl = AppConfig.getServerUrl(mContext);
		mHttpClient.send(HttpMethod.POST, requestUrl, params, asyncHandler);
	}

	private void handleHttpResultSuccess(String responseMessage,
			Bundle extraInfo) {
		LogX.d(TAG,
				"-----------HTTP Request Extra-----------"
						+ extraInfo.toString());
		int action = extraInfo.getInt("action");
		int resultCode = AsyncResultCode.SUCCESS;

		SlrResponse response = null;
		try {
			JsonResponse jr = new JsonResponse(responseMessage);
			SolarCommonResponse acr = new SolarCommonResponse();
			acr.setRequestType(action);
			acr.setResponseContent(jr);
			acr.setCloseProgress(extraInfo.getBoolean("closeProgress"));
			response = acr;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		notifyResponse(resultCode, response);
	}

	private void handleHttpResultFail(Throwable throwable, String errorMessage,
			Bundle extraInfo) {
		int resultCode = AsyncResultCode.FAIL;

		SlrResponse ar = new SlrResponse();
		ar.setResponseMessage(errorMessage);
		ar.setCloseProgress(extraInfo.getBoolean("closeProgress"));
		SlrResponse response = ar;

		notifyResponse(resultCode, response);
	}

	private void notifyResponse(int resultCode, SlrResponse response) {
		LogX.trace(TAG, "Listener count: " + mListenerTransports.size());
		LogX.trace(TAG, mListenerTransports.toString());
		for (ListenerTransport transport : mListenerTransports) {
			transport.handleEvent(resultCode, response);
		}
	}

	private void sendShowProgressMessage(Bundle extra) {
		if (extra == null) {
			return;
		}
		boolean showProgress = extra.getBoolean("showProgress");
		if (showProgress) {
			SlrResponse response = new SlrResponse();
			response.setResponseEvent(SlrResponse.RESPONSE_EVENT_SHOW_PROGRESS);
			int resultCode = AsyncResultCode.UNDEFINED;
			notifyResponse(resultCode, response);
		}
	}

	public void addListenerTransport(ListenerTransport transport) {
		if (transport != null) {
			synchronized (mListenerTransports) {
				if (!mListenerTransports.contains(transport)) {
					mListenerTransports.add(transport);
				}
			}
		}
	}

	public void removeListenerTransport(ListenerTransport transport) {
		if (transport != null) {
			synchronized (mListenerTransports) {
				if (mListenerTransports.contains(transport)) {
					mListenerTransports.remove(transport);
				}
			}
		}
	}
}
