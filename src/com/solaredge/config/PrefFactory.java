package com.solaredge.config;

public class PrefFactory {
	public static BasePref get(String preference) {
		if (preference
				.equals(AppConfig.PreferenceModule.SOLAR_PREFERENCE_APPLICATION)) {
			return new SolarDefaultPref();
		} else if (preference
				.equals(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER)) {
			return new UserPref();
		} else {
			throw new IllegalArgumentException(
					"illegal argument, no such preference!");
		}
	}

	public static SolarDefaultPref getDefaultPref() {
		return (SolarDefaultPref) get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_APPLICATION);
	}
}
