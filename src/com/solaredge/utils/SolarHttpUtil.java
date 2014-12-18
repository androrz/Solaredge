package com.solaredge.utils;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.solaredge.config.AppConfig;
import com.solaredge.entity.HttpRequestParam;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.AsyncResultCode;
import com.solaredge.server.ListenerTransport;
import com.solaredge.server.SolarCommonResponse;
import com.solaredge.server.response.AlaResponse;

public class SolarHttpUtil {
	private HttpUtils mHttpClient = new HttpUtils();
	private static Context mContext;
	private static SolarHttpUtil sInstance;
	private ArrayList<ListenerTransport> mListenerTransports = new ArrayList<ListenerTransport>();

	private static final String TAG = "Tissot-HttpClient";

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

		int action = extraInfo.getInt("action");
		String requestUrl = AppConfig.getServerUrl(mContext)
				+ SvcNames.getServiceHandler(action);
		LogX.trace(TAG, "requestUrl: " + requestUrl);
		mHttpClient.send(HttpMethod.POST, requestUrl, requestParam.getParams(),
				asyncHandler);
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

		int action = requestParam.getAction();
		String requestUrl = AppConfig.getServerUrl(mContext)
				+ SvcNames.getServiceHandler(action);
		mHttpClient.send(HttpMethod.POST, requestUrl, requestParam.getParams(),
				asyncHandler);
	}

	private void handleHttpResultSuccess(String responseMessage,
			Bundle extraInfo) {
		LogX.d(TAG,
				"-----------HTTP Request Extra-----------"
						+ extraInfo.toString());
		int action = extraInfo.getInt("action");
		int resultCode = AsyncResultCode.SUCCESS;

		AlaResponse response = null;
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

		AlaResponse ar = new AlaResponse();
		ar.setResponseMessage(errorMessage);
		ar.setCloseProgress(extraInfo.getBoolean("closeProgress"));
		AlaResponse response = ar;

		notifyResponse(resultCode, response);
	}

	private void notifyResponse(int resultCode, AlaResponse response) {
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
			AlaResponse response = new AlaResponse();
			response.setResponseEvent(AlaResponse.RESPONSE_EVENT_SHOW_PROGRESS);
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
