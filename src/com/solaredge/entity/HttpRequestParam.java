package com.solaredge.entity;

import com.lidroid.xutils.http.RequestParams;
import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.SvcNames;
import com.solaredge.utils.LogX;

public class HttpRequestParam {

	private int mAction = SvcNames.WSN_UNDEFINED;
	private StringBuilder mBuilder = null;
	private RequestParams mParams = null;

	private static final String TAG = "Tissot-HttpRequestParam";

	public HttpRequestParam() {
		super();
		mBuilder = new StringBuilder();
		mParams = new RequestParams();
	}

	public HttpRequestParam(int action) {
		super();
		mAction = action;
		mBuilder = new StringBuilder();
		mParams = new RequestParams();
	}

	public RequestParams getParams() {
		LogX.trace(
				TAG,
				"+++++++++HTTP Request Parameters++++++++"
						+ mBuilder.toString());

		return mParams;
	}

	public void addParam(String key, String value) {
		if (value == null) {
			value = FusionCode.ETY_STR;
		}
		mParams.addBodyParameter(key, value);

		// try {
		if (mBuilder.length() != 0) {
			mBuilder.append("&");
		}
		mBuilder.append(key).append("=").append(value);
		// .append(URLEncoder.encode(value, "UTF-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
	}

	public String getParamString() {
		return mBuilder.toString();
	}

	public void addParam(String key, int value) {
		mParams.addBodyParameter(key, String.valueOf(value));
	}

	public int getAction() {
		return mAction;
	}

	public void setAction(int method) {
		mAction = method;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(mAction).append(mParams.toString());

		return builder.toString();
	}
}
