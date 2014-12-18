package com.solaredge.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.solaredge.fusion.FusionCode;

public class JsonResponse extends JSONObject {

	public JsonResponse(String s) throws JSONException {
		super(s);
	}

	public JSONArray getBodyArray(String filed) {
		JSONArray jArray = null;
		try {
			jArray = getJSONArray(filed);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jArray;
	}

	public String getBodyField(String field) {
		String value = null;

		try {
			value = getString(field);
		} catch (JSONException e) {
			e.printStackTrace();
			return FusionCode.ETY_STR;
		}

		return value;
	}

	public JSONObject getBodyObject(String field) {
		JSONObject value = null;

		try {
			value = getJSONObject(field);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}
}
