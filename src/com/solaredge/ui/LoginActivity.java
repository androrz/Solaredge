package com.solaredge.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.AlaResponse;
import com.solaredge.utils.EncryptUtil;
import com.solaredge.utils.LogX;

public class LoginActivity extends BaseActivity {

	@ViewInject(R.id.e_user_name)
	private EditText mUserNameET;

	@ViewInject(R.id.e_user_password)
	private EditText mUserPasswordET;

	@ViewInject(R.id.b_login)
	private Button mLoginBT;

	private String mUserName;
	private String mUserPassword;

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
		mUserName = mUserNameET.getText().toString();
		mUserPassword = mUserPasswordET.getText().toString();
		if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mUserPassword)) {
			showToast("用户名或密码为空！");
			return;
		} else {
			String cipherPassword = EncryptUtil.desCrypto(mUserPassword, "61ABC272");
			LogX.trace("Solar", "cipher->" + cipherPassword);
			mSolarManager.userLogin(mUserName, cipherPassword);
		}

	}

	@Override
	public void handleEvent(int resultCode, AlaResponse response) {
		if (!analyzeAsyncResultCode(resultCode, response)) {
			return;
		}

		int action = response.getResponseEvent();
		JsonResponse jr = response.getResponseContent();
		switch (action) {
		case SvcNames.WSN_USER_LOGIN:
			handleUserLogin(jr);
			break;
		default:
			break;
		}
	}

	private void handleUserLogin(JsonResponse jr) {
		if (jr == null) {
			return;
		}

		jumpToPage(MainActivity.class, true);
	}
}
