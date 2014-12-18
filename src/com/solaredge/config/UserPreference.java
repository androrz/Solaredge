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

	public String getUserMobile() {
		return getStringKey(AppConfig.PreferenceUser.USER_MOBILE_KEY);
	}

	public void setUserMobile(String memberID) {
		putStringKey(AppConfig.PreferenceUser.USER_MOBILE_KEY, memberID);
	}

	public void clear() {
		mSharedPref.edit().clear().commit();
	}
}
