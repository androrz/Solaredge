package com.solaredge.entity;

import com.solaredge.config.AppConfig;
import com.solaredge.config.PreferenceFactory;
import com.solaredge.config.UserPreference;
import com.solaredge.fusion.FusionCode;

public class SolarUser {
	private String udid;

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public boolean isUserLogin() {
		return !getMemberID().equals(FusionCode.ETY_STR);
	}

	public String getMemberID() {
		UserPreference preference = (UserPreference) PreferenceFactory
				.get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER);
		return preference.getMemberID();
	}

	public void clear() {
		UserPreference preference = (UserPreference) PreferenceFactory
				.get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER);
		preference.clear();
	}
}
