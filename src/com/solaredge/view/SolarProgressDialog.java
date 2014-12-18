package com.solaredge.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.solaredge.R;
import com.solaredge.fusion.FusionField;

public class SolarProgressDialog extends Dialog {

	private TextView mContentsText;
	private ImageView mProgressIV;

	public SolarProgressDialog(Context context) {
		super(context, R.style.solar_dialog_no_dim);
		setCustomView();
	}

	public void setCustomView() {
		View customView = LayoutInflater.from(getContext()).inflate(
				R.layout.solar_progress_dialog, null);
		mContentsText = (TextView) customView
				.findViewById(R.id.t_dialog_content);
		mProgressIV = (ImageView) customView.findViewById(R.id.i_progress);
		mProgressIV.setImageResource(R.anim.alading_dialog_loading);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		final AnimationDrawable playDrawable = (AnimationDrawable) mProgressIV
				.getDrawable();
		mProgressIV.post(new Runnable() {
			@Override
			public void run() {
				playDrawable.start();
			}
		});

		setCancelable(false);
		setCanceledOnTouchOutside(false);
		super.setContentView(customView, new LayoutParams(
				(int) (100 * FusionField.deviceDensity),
				(int) (100 * FusionField.deviceDensity)));
	}

	public SolarProgressDialog setContentText(String content) {
		mContentsText.setText(content);
		return this;
	}

	public SolarProgressDialog setDismissOnTouchOutside(boolean cancel) {
		setCanceledOnTouchOutside(cancel);
		return this;
	}

}
