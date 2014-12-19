package com.solaredge.entity;

import java.util.HashMap;

import com.solaredge.fusion.FusionCode;
import com.solaredge.fusion.SvcNames;

public class HttpRequestParam {

	private int mAction = SvcNames.WSN_UNDEFINED;
	private StringBuilder mBuilder = null;
	private HashMap<String, String> mParams = new HashMap<String, String>();

	public HttpRequestParam() {
		super();
		mBuilder = new StringBuilder();
	}

	public HttpRequestParam(int action) {
		super();
		mAction = action;
		mParams.put("action", SvcNames.getServiceHandler(mAction));
		mBuilder = new StringBuilder();
	}

	public HashMap<String, String> getParams() {
		return mParams;
	}

	public void addParam(String key, String value) {
		if (value == null) {
			value = FusionCode.ETY_STR;
		}
		mParams.put(key, value);

		if (mBuilder.length() != 0) {
			mBuilder.append("&");
		}
		mBuilder.append(key).append("=").append(value);
	}
	
	public int getAction() {
		return mAction;
	}

	public void setAction(int method) {
		mAction = method;
	}

	public String getParam(String key) {
		return mParams.get(key);
	}

	public String getParamString() {
		return mBuilder.toString();
	}

	public String[] paramNameArray() {
		String[] nameArray = new String[mParams.size()];
		return mParams.keySet().toArray(nameArray);
	}

	public String[] paramValueArray() {
		String[] valueArray = new String[mParams.size()];
		return mParams.values().toArray(valueArray);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(mAction).append(mParams.toString());

		return builder.toString();
	}
}
