package com.solaredge.config;

public class PreferenceFactory {
	public static BasePreference get(String preference) {
		if (preference
				.equals(AppConfig.PreferenceModule.SOLAR_PREFERENCE_APPLICATION)) {
			return new SolarDefaultPreference();
		} else if (preference
				.equals(AppConfig.PreferenceModule.SOLAR_PREFERENCE_USER)) {
			return new UserPreference();
		} else {
			throw new IllegalArgumentException(
					"illegal argument, no such preference!");
		}
	}

	public static SolarDefaultPreference getDefaultPreference() {
		return (SolarDefaultPreference) get(AppConfig.PreferenceModule.SOLAR_PREFERENCE_APPLICATION);
	}
}
