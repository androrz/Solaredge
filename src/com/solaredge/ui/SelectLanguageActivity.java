package com.solaredge.ui;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.solaredge.R;

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
		mServiceTitle.setText("集能易");
		mLanGroup.check(R.id.r_chinese);
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
		} else if (id == R.id.r_english) {
			mLanChinese.setChecked(false);
			mLanJapnese.setChecked(false);
		} else if (id == R.id.r_japnese) {
			mLanEnglish.setChecked(false);
			mLanChinese.setChecked(false);
		}
	}

}
