package com.solaredge;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.ui.BaseActivity;

public class LoginActivity extends BaseActivity {

	@ViewInject(R.id.e_user_name)
	private EditText mUserNameET;

	@ViewInject(R.id.e_user_password)
	private EditText mUserPasswordET;

	@ViewInject(R.id.b_login)
	private Button mLoginBT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();
	}

	@OnClick(R.id.b_login)
	private void onLoginClick(View view) {
		showToast("aaa");
	}
}
