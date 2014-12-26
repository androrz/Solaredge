package com.solaredge.ui;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.utils.DbHelp;
import com.solaredge.utils.LogX;
import com.solaredge.view.PanZoomGridView;
import com.solaredge.zxing.CaptureActivity;

public class InverterLayoutActivity extends BaseActivity {

	@ViewInject(R.id.b_modify)
	private Button mModifyBT;

	@ViewInject(R.id.i_scan)
	private ImageButton mScanIB;

	@ViewInject(R.id.p_grid_view)
	private PanZoomGridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_inverter_layout);
		super.onCreate(savedInstanceState);

		try {
			List<Inverter> list = DbHelp.getDbUtils(this).findAll(
					Inverter.class);
			int row = 0, col = 0;
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				col = Math.max(col, inverter.getmClusterNumber());
				row += inverter.getmGroupNumber();
			}
			int[][] matrix = new int[row][col];
			LogX.trace(TAG, "row: " + row + " col: " + col);
			int r = 0;
			for (int i = 0; i < list.size(); i++) {
				Inverter inverter = list.get(i);
				for (int m = 0; m < inverter.getmGroupNumber(); m++) {
					int n = 0;
					for (; n < inverter.getmClusterNumber(); n++) {
						if (inverter.getmAngle() == 0) {
							matrix[r][n] = 0;
						} else {
							matrix[r][n] = 1;
						}
					}
					if (n < col) {
						for (int z = n; z < col; z++) {
							matrix[r][z] = -1;
						}
					}
					r++;
				}
			}
			mGridView.setGridArray(matrix);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();

		mXFunc2.setVisibility(View.VISIBLE);
		mXFunc2.setImageResource(R.drawable.drw_add);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_func2:
			jumpToPage(CaptureActivity.class);
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	@OnClick(R.id.b_modify)
	private void onModifyClick(View view) {
		jumpToPage(ModifyLayoutActivity.class);
	}

	@OnClick(R.id.i_reset)
	private void onResetClick(View view) {
		mGridView.reset();
	}

	@OnClick(R.id.i_scan)
	private void onScanClick(View view) {
		jumpToPage(CaptureActivity.class);
	}

}
