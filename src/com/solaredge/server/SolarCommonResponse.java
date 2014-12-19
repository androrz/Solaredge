package com.solaredge.server;

import com.solaredge.server.response.SlrResponse;

public class SolarCommonResponse extends SlrResponse {
	public void setRequestType(int type) {
		responseEvent = type;
	}
}
