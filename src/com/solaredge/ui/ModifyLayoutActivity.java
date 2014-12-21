package com.solaredge.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;

public class ModifyLayoutActivity extends BaseActivity {

	@ViewInject(R.id.b_ok)
	private Button mOkBT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_modify_layout);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initWidgetEvent() {
		// TODO Auto-generated method stub
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		// TODO Auto-generated method stub
		super.initWidgetProperty();
	}

	@OnClick(R.id.b_ok)
	private void onOkClick(View view) {
		finish();
	}
}
