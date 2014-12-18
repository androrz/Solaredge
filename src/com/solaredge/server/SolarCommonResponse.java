package com.solaredge.server;

import com.solaredge.server.response.AlaResponse;

public class SolarCommonResponse extends AlaResponse {
	public void setRequestType(int type) {
		responseEvent = type;
	}
}
