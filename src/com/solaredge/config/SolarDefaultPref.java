package com.solaredge.config;

import android.content.Context;

public class SolarDefaultPref extends BasePref {

	SolarDefaultPref() {
		mSharedPref = mContext.getSharedPreferences(
				AppConfig.PreferenceModule.SOLAR_PREFERENCE_APPLICATION,
				Context.MODE_PRIVATE);
	}

	public boolean getShortCutCreated() {
		return getBooleanKey(AppConfig.PreferenceSolar.SOLAR_SHORT_CUT_CREATED);
	}

	public void setShortCutCreated() {
		putBooleanKey(AppConfig.PreferenceSolar.SOLAR_SHORT_CUT_CREATED, true);
	}

	public String getAppLanguage() {
		return getStringKey(AppConfig.PreferenceSolar.SOLAR_SYSTEM_LANGUAGE);
	}

	public void setAppLanguage(String language) {
		putStringKey(AppConfig.PreferenceSolar.SOLAR_SYSTEM_LANGUAGE, language);
	}

	public String getUserName() {
		return getStringKey(AppConfig.PreferenceSolar.SOLAR_USER_NAME);
	}

	public void setUserName(String language) {
		putStringKey(AppConfig.PreferenceSolar.SOLAR_USER_NAME, language);
	}

	public String getUserPassword() {
		return getStringKey(AppConfig.PreferenceSolar.SOLAR_USER_PASSWORD);
	}

	public void setUserPassword(String language) {
		putStringKey(AppConfig.PreferenceSolar.SOLAR_USER_PASSWORD, language);
	}

	public String getLastStationId() {
		return getStringKey(AppConfig.PreferenceSolar.SOLAR_LAST_STATION_ID);
	}

	public void setLastStationId(String id) {
		putStringKey(AppConfig.PreferenceSolar.SOLAR_LAST_STATION_ID, id);
	}

}
