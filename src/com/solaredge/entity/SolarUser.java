package com.solaredge.entity;

import com.solaredge.config.AppConfig;
import com.solaredge.config.PreferenceFactory;
import com.solaredge.config.UserPreference;
import com.solaredge.fusion.FusionCode;

public class SolarUser {

	private UserPreference mUserPreference;

	public SolarUser() {
		mUserPreference = (UserPreference) PreferenceFactory
				.get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER);
	}

	public String getUdid() {
		return mUserPreference.getUserUDID();
	}

	public void setUdid(String udid) {
		mUserPreference.setUserUDID(udid);
	}

	public String getMemberID() {
		return mUserPreference.getMemberID();
	}

	public void setMemberID(String mMemberID) {
		mUserPreference.setMemberID(mMemberID);
	}

	public boolean isUserLogin() {
		return !getMemberID().equals(FusionCode.ETY_STR);
	}

	public void clear() {
		UserPreference preference = (UserPreference) PreferenceFactory
				.get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER);
		preference.clear();
	}
}
