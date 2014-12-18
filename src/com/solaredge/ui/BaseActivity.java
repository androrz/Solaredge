package com.solaredge.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.solaredge.R;
import com.solaredge.utils.LogX;
import com.solaredge.view.SolarProgressDialog;

public class BaseActivity extends FragmentActivity implements OnClickListener {

	protected ImageButton mBack;
	protected Button mXFunc;
	protected ImageButton mXFunc2;
	protected TextView mXFunc3;
	protected TextView mServiceTitle;
	protected Toast mToast;
	protected SolarProgressDialog mProgressDialog;
	protected Intent mIntent = null;

	private static final int MSG_DISMISS_PROGRESS_DIALOG = 0;
	private static final int MSG_SHOW_TOAST = 1;
	private static final int MSG_SHOW_PROGRESS_DIALOG = 2;

	protected String TAG = "Solar-BaseActivity";

	public String getClassName() {
		String clazzName = getClass().getName();
		return clazzName;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_back:
			hideInputMethod();
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initCaughtException();
		ViewUtils.inject(this);
		initWidgetProperty();
		initWidgetEvent();

		LogX.trace(TAG, "------------>Page: " + getClassName());
	}

	protected boolean checkForms() {
		return true;
	}

	@Override
	protected void onResume() {
		LogX.trace(TAG, getClassName() + " - onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		LogX.trace(TAG, getClassName() + " - onDestroy");
		super.onDestroy();
	}

	private void initCaughtException() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {

				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				ex.printStackTrace(printWriter);
				String stacktrace = result.toString();

				Log.e(TAG, "_____" + ex.toString() + " " + stacktrace);

				Log.d(TAG, ex.getLocalizedMessage());

				new Thread() {

					@Override
					public void run() {
						Looper.prepare();
						AlertDialog.Builder builder = new Builder(
								BaseActivity.this);
						builder.setMessage(getString(R.string.app_crash));
						builder.setTitle(getString(R.string.app_prompt));
						builder.setNegativeButton(
								getString(R.string.app_confirm),
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										android.os.Process
												.killProcess(android.os.Process
														.myPid());
									}
								});
						builder.create().show();
						Looper.loop();
					}
				}.start();

			}

		});
	}

	protected void addWidgetEventListener(View v) {
		if (v != null)
			v.setOnClickListener(this);
	}

	protected void dismissProgressBar() {
		Message message = Message.obtain();
		message.what = MSG_DISMISS_PROGRESS_DIALOG;
		mBaseHandler.sendMessage(message);
	}

	protected void initWidgetEvent() {
		addWidgetEventListener(mBack);
		addWidgetEventListener(mXFunc);
		addWidgetEventListener(mXFunc2);
		addWidgetEventListener(mXFunc3);
	}

	protected void initWidgetProperty() {
		mBack = (ImageButton) findViewById(R.id.b_back);
		mXFunc = (Button) findViewById(R.id.b_func);
		mXFunc2 = (ImageButton) findViewById(R.id.b_func2);
		mXFunc3 = (TextView) findViewById(R.id.b_func3);
		mServiceTitle = (TextView) findViewById(R.id.t_service);

		mBaseHandler.removeMessages(MSG_DISMISS_PROGRESS_DIALOG);
	}

	protected void showProgressDialog(String title) {
		Message message = Message.obtain();
		message.what = MSG_SHOW_PROGRESS_DIALOG;
		message.obj = title;
		mBaseHandler.sendMessage(message);
	}

	protected void showProgressDialog() {
		showProgressDialog("");
	}

	protected void showToast(int resourceId) {
		showToast(getString(resourceId));
	}

	protected void showToast(String text) {
		Message message = Message.obtain();
		message.what = MSG_SHOW_TOAST;
		message.obj = text;
		mBaseHandler.sendMessage(message);
	}

	/**
	 * General method for starting a new activity either for result or not.
	 * 
	 * @param activityClass
	 *            The activity to start
	 * @param bundle
	 *            Extra information with this intent.
	 * @param isReturn
	 *            If start for result or not
	 * @param requestCode
	 *            The request code.
	 * @param isFinish
	 *            If finish self after start.
	 */
	public void jumpToPage(Class<?> activityClass, Bundle bundle,
			boolean isReturn, int requestCode, boolean isFinish) {
		if (activityClass == null) {
			return;
		}

		Intent intent = new Intent();
		intent.setClass(this, activityClass);

		if (bundle != null) {
			intent.putExtras(bundle);
		}

		if (isReturn) {
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}

		if (isFinish) {
			finish();
		}
	}

	public void jumpToPage(Class<?> activityClass) {
		jumpToPage(activityClass, null, false, 0, false);
	}

	public void jumpToPage(Class<?> activityClass, boolean isFinish) {
		jumpToPage(activityClass, null, false, 0, isFinish);
	}

	public void jumpToPage(Class<?> activityClass, Bundle bundle) {
		jumpToPage(activityClass, bundle, false, 0, false);
	}

	public void jumpToPage(Class<?> activityClass, Bundle bundle,
			boolean isFinish) {
		jumpToPage(activityClass, bundle, false, 0, isFinish);
	}

	public void jumpToMain(int index) {
		Bundle bundle = new Bundle();
		bundle.putInt("tab_index", index);
		jumpToPage(MainActivity.class, bundle);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mProgressDialog != null) {
			mBaseHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
		}
		return super.onKeyDown(keyCode, event);
	}

	protected Handler mBaseHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DISMISS_PROGRESS_DIALOG:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				break;
			case MSG_SHOW_TOAST:
				String text = (String) msg.obj;
				Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_SHOW_PROGRESS_DIALOG:
				if (mProgressDialog == null) {
					mProgressDialog = new SolarProgressDialog(
							BaseActivity.this);
				}

				String content = null;
				if (msg.obj != null && msg.obj instanceof String) {
					content = (String) msg.obj;
					if (!TextUtils.isEmpty(content)) {
						mProgressDialog.setContentText(content);
					}
				}
				mProgressDialog.show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	protected void hideInputMethod() {
		InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (im != null && getCurrentFocus() != null) {
			im.hideSoftInputFromWindow(getCurrentFocus()
					.getApplicationWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
