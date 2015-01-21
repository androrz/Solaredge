package com.solaredge.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.solaredge.R;
import com.solaredge.fusion.FusionField;
import com.solaredge.utils.VUtils;

public class InputCodeDialog extends Dialog {

	private RelativeLayout mPositive;
	private RelativeLayout mNegative;

	private TextView mTitleText;
	private EditText mContentsText;
	private TextView mPositiveText;
	private TextView mNegativeText;

	public static final int SIZE_SMALL = 1;
	public static final int SIZE_NORMAL = 2;
	public static final int SIZE_LARGE = 3;

	public InputCodeDialog(Context context) {
		super(context, R.style.alading_dialog);
		setCustomView(SIZE_NORMAL, true);
	}

	public InputCodeDialog(Context context, int size) {
		super(context, R.style.alading_dialog);
		setCustomView(size, true);
	}

	public InputCodeDialog(Context context, int size, boolean animated) {
		super(context, R.style.alading_dialog);
		setCustomView(size, animated);
	}

	private void setCustomView(int size, boolean animated) {
		View customView = LayoutInflater.from(getContext()).inflate(
				R.layout.input_alert_dialog, null);
		mPositive = (RelativeLayout) customView.findViewById(R.id.r_positive);
		mNegative = (RelativeLayout) customView.findViewById(R.id.r_negtive);
		mTitleText = (TextView) customView.findViewById(R.id.t_dialog_title);
		mContentsText = (EditText) customView
				.findViewById(R.id.e_dialog_content);
		mPositiveText = (TextView) customView
				.findViewById(R.id.t_positive_text);
		mNegativeText = (TextView) customView
				.findViewById(R.id.t_negative_text);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setCanceledOnTouchOutside(false);

		int width = (int) (FusionField.devicePixelsWidth - 15 * FusionField.deviceDensity);
		int height = 0;
		if (size == SIZE_NORMAL) {
			height = LayoutParams.WRAP_CONTENT;
		} else if (size == SIZE_LARGE) {
			height = (int) (FusionField.devicePixelsHeight - 100 * FusionField.deviceDensity);
		}

		mNegative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mContentsText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String macCode = mContentsText.getText().toString();
				if (TextUtils.isEmpty(macCode)) {
					mPositive.setEnabled(false);
					VUtils.disableViewGroup(mPositive, true);
				} else {
					VUtils.enableViewGroup(mPositive);
				}
			}
		});
		VUtils.disableViewGroup(mPositive, true);

		super.setContentView(customView, new LayoutParams(width, height));
	}

	public InputCodeDialog setOnPositiveListener(View.OnClickListener listener) {
		mPositive.setOnClickListener(listener);
		return this;
	}

	public InputCodeDialog setOnNegativeListener(View.OnClickListener listener) {
		mNegative.setOnClickListener(listener);
		return this;
	}

	public String getMacId() {
		return mContentsText.getText().toString();
	}

	public InputCodeDialog setTitleText(String title) {
		mTitleText.setText(title);

		return this;
	}

	public InputCodeDialog setContentText(String content) {
		mContentsText.setText(content);
		return this;
	}

	public InputCodeDialog setContentText(Spanned content) {
		mContentsText.setText(content);
		return this;
	}

	public InputCodeDialog setPositiveText(String positive) {
		mPositiveText.setText(positive);
		return this;
	}

	public InputCodeDialog setNegativeText(String negative) {
		mNegativeText.setText(negative);
		return this;
	}

	public InputCodeDialog setDismissOnTouchOutside(boolean cancel) {
		setCanceledOnTouchOutside(cancel);
		return this;
	}

	public InputCodeDialog setContentTextGravity(int gravity) {
		mContentsText.setGravity(gravity);
		return this;
	}

	public InputCodeDialog hideNegative() {
		mNegative.setVisibility(View.GONE);
		return this;
	}

	public InputCodeDialog setAnimation(int anim) {
		getWindow().setWindowAnimations(anim);
		return this;
	}

}
