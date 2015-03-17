package com.solaredge.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.solaredge.R;
import com.solaredge.SolarApp;
import com.solaredge.config.PrefFactory;

public class SelectLanguageActivity extends BaseActivity implements
		OnCheckedChangeListener {

	@ViewInject(R.id.r_group)
	private RadioGroup mLanGroup;

	@ViewInject(R.id.r_chinese)
	private RadioButton mLanChinese;

	@ViewInject(R.id.r_english)
	private RadioButton mLanEnglish;

	@ViewInject(R.id.r_japnese)
	private RadioButton mLanJapnese;

	private static final String LAN_CHINESE = "zh";
	private static final String LAN_JAPNESE = "ja";
	private static final String LAN_ENGLISH = "en";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_select_language);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();

		mLanChinese.setOnCheckedChangeListener(this);
		mLanEnglish.setOnCheckedChangeListener(this);
		mLanJapnese.setOnCheckedChangeListener(this);
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();
		String lan = PrefFactory.getDefaultPref().getAppLanguage();
		if (TextUtils.isEmpty(lan)) {
			mLanGroup.check(R.id.r_chinese);
		} else {
			if (lan.equals(LAN_CHINESE)) {
				mLanGroup.check(R.id.r_chinese);
			} else if (lan.equals(LAN_JAPNESE)) {
				mLanGroup.check(R.id.r_japnese);
			} else if (lan.equals(LAN_ENGLISH)) {
				mLanGroup.check(R.id.r_english);
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_back:
			setResult(RESULT_OK);
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if (!isChecked) {
			return;
		}

		if (id == R.id.r_chinese) {
			mLanEnglish.setChecked(false);
			mLanJapnese.setChecked(false);
			PrefFactory.getDefaultPref()
					.setAppLanguage(LAN_CHINESE);
		} else if (id == R.id.r_english) {
			mLanChinese.setChecked(false);
			mLanJapnese.setChecked(false);
			PrefFactory.getDefaultPref()
					.setAppLanguage(LAN_ENGLISH);
		} else if (id == R.id.r_japnese) {
			mLanEnglish.setChecked(false);
			mLanChinese.setChecked(false);
			PrefFactory.getDefaultPref()
					.setAppLanguage(LAN_JAPNESE);
		}

		SolarApp.getApplication().setLocale();
		restartActivity();
	}

}
