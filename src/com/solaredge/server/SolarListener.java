package com.solaredge.server;

import com.solaredge.server.response.AlaResponse;

public interface SolarListener {
	void handleEvent(int resultCode, AlaResponse response);
}
