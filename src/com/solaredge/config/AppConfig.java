package com.solaredge.config;

import android.content.Context;

import com.solaredge.fusion.ServerInfo;

public class AppConfig {

	public static String getServerUrl(Context context) {
		return ServerInfo.WEB_SERVICE_URL;
	}

	public static class PreferenceModule {
		public static final String SOLAR_PREFERENCE_USER = "user_property";
		public static final String SOLAR_PREFERENCE_APPLICATION = "solar_property";
	}

	public static class PreferenceUser {
		public static final String USER_ID_KEY = "user_id";
		public static final String USER_UDID_KEY = "user_udid";
	}

	public static class PreferenceSolar {
		public static final String SOLAR_SHORT_CUT_CREATED = "short_cut_created";
	}

}
