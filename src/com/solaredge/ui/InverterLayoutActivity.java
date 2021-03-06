package com.solaredge.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.view.PanZoomGridView;
import com.solaredge.zxing.CaptureActivity;

public class InverterLayoutActivity extends BaseActivity {

	@ViewInject(R.id.b_modify)
	private Button mModifyBT;

	@ViewInject(R.id.i_scan)
	private ImageButton mScanIB;

	@ViewInject(R.id.p_grid_view)
	private PanZoomGridView mGridView;
	
	private String mStationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_inverter_layout);
		super.onCreate(savedInstanceState);
		
		Bundle bundle = mIntent.getExtras();
		mStationId = bundle.getString("station_id");
	}

	@Override
	protected void onResume() {
		super.onResume();
		int[][] matrix = mSolarManager.getInverterMatrix();
		mGridView.setGridArray(matrix);
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();
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
		Bundle bundle = new Bundle();
		bundle.putString("station_id", mStationId);
		jumpToPage(CaptureActivity.class, bundle);
	}

}
