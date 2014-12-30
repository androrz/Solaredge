package com.solaredge.ui;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.exception.DbException;
import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.entity.InverterGridItem;
import com.solaredge.utils.DbHelp;

public class AddOptimizerActivity extends BaseActivity {

	private List<Inverter> mInverterList;
	InverterGridItem mGridItem = new InverterGridItem();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_add_optimizer);
		super.onCreate(savedInstanceState);

		mXFunc2.setVisibility(View.VISIBLE);
		mXFunc2.setImageResource(R.drawable.drw_confirm);

		initWheel(R.id.w_cluster_number);
		initWheel(R.id.w_group_number);
		initAngleWheel(R.id.w_angle);
		initInverterWheel(R.id.w_inverter_name);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_func2:
			mGridItem.setInverterId(mInverterList.get(
					getWheel(R.id.w_inverter_name).getCurrentItem())
					.getInverterId());
			mGridItem.setRow(getWheel(R.id.w_group_number).getCurrentItem());
			mGridItem.setCol(getWheel(R.id.w_cluster_number).getCurrentItem());
			mGridItem
					.setmAngle(getWheel(R.id.w_angle).getCurrentItem() == 0 ? 0
							: 90);
			mGridItem.setmIsNew(true);
			mSolarManager.storeAddedGridItem(mGridItem);
			showProgressDialog();
			mBaseHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					dismissProgressBar();
					setResult(RESULT_OK);
					finish();
				}
			}, 500);
			break;

		default:
			break;
		}
		super.onClick(v);
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
			// if (wheel.getId() == R.id.w_group_number) {
			// mGridItem.setRow(newValue);
			// } else if (wheel.getId() == R.id.w_cluster_number) {
			// mGridItem.setCol(newValue);
			// } else if (wheel.getId() == R.id.w_angle) {
			// int val = newValue == 0 ? 0 : 90;
			// mGridItem.setmAngle(val);
			// } else if (wheel.getId() == R.id.w_inverter_name) {
			// mGridItem.setInverterId(mInverterList.get(newValue)
			// .getInverterId());
			// }
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
		wheel.setViewAdapter(new NumericWheelAdapter(this, 1, 30));
		wheel.setCurrentItem(0);

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(false);
	}

	private void initAngleWheel(int id) {
		WheelView wheel = getWheel(id);
		Integer[] items = new Integer[2];
		items[0] = 0;
		items[1] = 90;
		wheel.setViewAdapter(new ArrayWheelAdapter<Integer>(this, items));

		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
		wheel.setCyclic(false);
	}

	private void initInverterWheel(int id) {
		WheelView wheel = getWheel(id);

		mInverterList = new ArrayList<Inverter>();
		try {
			mInverterList = DbHelp.getDbUtils(this).findAll(Inverter.class);

		} catch (DbException e) {
			e.printStackTrace();
		}

		Inverter[] items = mInverterList.toArray(new Inverter[mInverterList
				.size()]);
		wheel.setViewAdapter(new ArrayWheelAdapter<Inverter>(this, items));

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
}
