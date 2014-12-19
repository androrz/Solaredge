package com.solaredge.server;

import com.solaredge.server.response.SlrResponse;

public interface SolarListener {
	void handleEvent(int resultCode, SlrResponse response);
}
