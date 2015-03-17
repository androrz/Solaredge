package com.solaredge.ui;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.entity.InverterGridItem;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.SlrResponse;
import com.solaredge.utils.DbHelp;

public class ModifyInverterActivity extends BaseActivity {

	@ViewInject(R.id.e_inverter_name)
	private EditText mInverterName;

	@ViewInject(R.id.t_cluster_number)
	private TextView mClusterNumber;

	@ViewInject(R.id.t_group_number)
	private TextView mGroupNumber;

	@ViewInject(R.id.t_angle)
	private TextView mAngle;

	@ViewInject(R.id.b_delete_inverter)
	private Button mDeleteInverterBT;

	private Inverter mInverter = null;
	private String mStationId;
	private int mOriginalGroup = 0;
	private int mOriginalCluster = 0;

	private TextWatcher mTextWatcher = new TextWatcher() {

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
			mInverter.setInverterName(s.toString());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_modify_inverter);
		super.onCreate(savedInstanceState);

		mXFunc2.setVisibility(View.VISIBLE);
		mXFunc2.setImageResource(R.drawable.drw_confirm);

		initWheel(R.id.w_cluster_number);
		initWheel(R.id.w_group_number);
		initArrayWheel(R.id.w_angle);

		Bundle bundle = mIntent.getExtras();
		if (bundle != null && bundle.containsKey("inverter")) {
			mInverter = (Inverter) bundle.getSerializable("inverter");
		}
		mStationId = bundle.getString("station_id");

		if (mInverter != null) {
			getWheel(R.id.w_group_number).setCurrentItem(
					mInverter.getmGroupNumber() - 1);
			getWheel(R.id.w_cluster_number).setCurrentItem(
					mInverter.getmClusterNumber() - 1);
			getWheel(R.id.w_angle).setCurrentItem(
					mInverter.getmAngle() == 0 ? 0 : 1);
			mGroupNumber.setText(mInverter.getmGroupNumber() + "");
			mClusterNumber.setText(mInverter.getmClusterNumber() + "");
			mAngle.setText(mInverter.getmAngle() + "");
			mInverterName.setText(mInverter.getInverterName());
			mOriginalGroup = mInverter.getmGroupNumber();
			mOriginalCluster = mInverter.getmClusterNumber();
		} else {
			mInverter = new Inverter();
			mInverter.setmGroupNumber(1);
			mInverter.setmClusterNumber(1);
			mInverter.setmAngle(0);
			mDeleteInverterBT.setVisibility(View.GONE);
			mOriginalGroup = 1;
			mOriginalCluster = 1;

			mGroupNumber.setText("1");
			mClusterNumber.setText("1");
			mAngle.setText("0");
		}

		mInverterName.addTextChangedListener(mTextWatcher);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_func2:

			if (TextUtils.isEmpty(mInverter.getInverterName())) {
				AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle(R.string.app_prompt)
						.setMessage(R.string.inverter_name_cannot_null)
						.setPositiveButton(R.string.app_ok,
								new OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
				return;
			}

			try {
				List<Inverter> list = DbHelp.getDbUtils(this).findAll(
						Selector.from(Inverter.class)
								.where("inverter_name", "=",
										mInverter.getInverterName())
								.and("mInverterId", "<>",
										mInverter.getInverterId()));
				if (list != null && list.size() > 0) {
					AlertDialog dialog = new AlertDialog.Builder(this)
							.setTitle(R.string.app_prompt)
							.setMessage(R.string.duplicate_inverter)
							.setPositiveButton(R.string.app_ok,
									new OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					dialog.show();
					break;
				}
			} catch (DbException e) {
				e.printStackTrace();
			}

			if (mInverter.getmGroupNumber() != mOriginalGroup
					|| mInverter.getmClusterNumber() != mOriginalCluster) {
				try { // 如果逆变器节点发生了变化，删除与之相关的所有InverterGridItem
					DbHelp.getDbUtils(this).delete(
							InverterGridItem.class,
							WhereBuilder.b("mInverterId", "=",
									mInverter.getInverterId()));
					mSolarManager.clearCache();
				} catch (DbException e) {
					e.printStackTrace();
				}
			}

			mSolarManager.modifyInverter(mStationId, mInverter.getInverterId(),
					mInverter.getInverterName(), mInverter.getmGroupNumber(),
					mInverter.getmClusterNumber(), mInverter.getmAngle());
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	@OnClick(R.id.b_delete_inverter)
	private void onDeleteInverterClick(View view) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.app_prompt)
				.setMessage(R.string.confirm_delete_inverter)
				.setPositiveButton(R.string.app_ok, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mSolarManager.deleteInverter(mInverter.getmStationId(),
								mInverter.getInverterId());
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.app_cancel, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();

	}

	// Wheel scrolled listener
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {

		}

		public void onScrollingFinished(WheelView wheel) {

		}
	};

	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel.getId() == R.id.w_group_number) {
				mGroupNumber.setText(newValue + 1 + "");
				mInverter.setmGroupNumber(newValue + 1);
			} else if (wheel.getId() == R.id.w_cluster_number) {
				mClusterNumber.setText(newValue + 1 + "");
				mInverter.setmClusterNumber(newValue + 1);
			} else if (wheel.getId() == R.id.w_angle) {
				int val = newValue == 0 ? 0 : 90;
				mAngle.setText(val + "");
				mInverter.setmAngle(val);
			}
		}
	};

	/**
	 * Initializes wheel
	 * 
	 * @param id
	 *            the wheel widget Id
	 */
	private void initWheel(int id) {
		WheelView wheel = getWheel(id);
		wheel.setViewAdapter(new NumericWheelAdapter(this, 1, 100));
		wheel.setCurrentItem(0);

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(false);
	}

	private void initArrayWheel(int id) {
		WheelView wheel = getWheel(id);
		Integer[] items = new Integer[2];
		items[0] = 0;
		items[1] = 90;
		wheel.setViewAdapter(new ArrayWheelAdapter<Integer>(this, items));

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(false);
	}

	/**
	 * Returns wheel by Id
	 * 
	 * @param id
	 *            the wheel Id
	 * @return the wheel with passed Id
	 */
	private WheelView getWheel(int id) {
		return (WheelView) findViewById(id);
	}

	@Override
	public void handleEvent(int resultCode, SlrResponse response) {
		if (!analyzeAsyncResultCode(resultCode, response)) {
			return;
		}

		int action = response.getResponseEvent();
		JsonResponse jr = response.getResponseContent();
		switch (action) {
		case SvcNames.WSN_CREATE_INVERTERS:
			if (jr.getBodyField("is_success").equals("1")) {
				finish();
			}
			break;

		case SvcNames.WSN_DELETE_INVERTER:
			if (jr.getBodyField("is_success").equals("1")) {
				try {
					DbHelp.getDbUtils(this).delete(mInverter);
				} catch (DbException e) {
					e.printStackTrace();
				}
				finish();
			}
			break;

		default:
			break;
		}
	}
}
