package com.solaredge.config;

import android.content.Context;

public class UserPreference extends BasePreference {

	public UserPreference() {
		mSharedPref = mContext.getSharedPreferences(
				AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER,
				Context.MODE_PRIVATE);
	}

	public String getMemberID() {
		return getStringKey(AppConfig.PreferenceUser.USER_ID_KEY);
	}

	public void setMemberID(String memberID) {
		putStringKey(AppConfig.PreferenceUser.USER_ID_KEY, memberID);
	}

	public String getUserUDID() {
		return getStringKey(AppConfig.PreferenceUser.USER_UDID_KEY);
	}

	public void setUserUDID(String memberID) {
		putStringKey(AppConfig.PreferenceUser.USER_UDID_KEY, memberID);
	}

	public void clear() {
		mSharedPref.edit().clear().commit();
	}
}
