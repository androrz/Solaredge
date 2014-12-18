package com.solaredge;

import android.os.Bundle;

import com.solaredge.ui.BaseActivity;

public class SelectLanguageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_select_language);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();
		mServiceTitle.setText("集能易");
	}

}
