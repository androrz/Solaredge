package com.solaredge.ui;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.R.integer;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.solaredge.R;

public class ModifyInverterActivity extends BaseActivity {

	@ViewInject(R.id.e_inverter_name)
	private EditText mInverterName;

	@ViewInject(R.id.t_cluster_number)
	private TextView mClusterNumber;

	@ViewInject(R.id.t_group_number)
	private TextView mGroupNumber;

	@ViewInject(R.id.t_angle)
	private TextView mAngle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_modify_inverter);
		super.onCreate(savedInstanceState);

		initWheel(R.id.w_cluster_number);
		initWheel(R.id.w_group_number);
		initArrayWheel(R.id.w_angle);
	}

	// Wheel scrolled flag
	private boolean wheelScrolled = false;

	// Wheel scrolled listener
	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			wheelScrolled = true;
		}

		public void onScrollingFinished(WheelView wheel) {
			wheelScrolled = false;
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (wheel.getId() == R.id.w_group_number) {
				mGroupNumber.setText(newValue + "");
			} else if (wheel.getId() == R.id.w_cluster_number) {
				mClusterNumber.setText(newValue + "");
			} else if (wheel.getId() == R.id.w_angle) {
				int val = newValue == 0 ? 0 : 90;
				mAngle.setText(val + "");
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
		wheel.setViewAdapter(new NumericWheelAdapter(this, 0, 9));
		wheel.setCurrentItem((int) (Math.random() * 10));

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
}
