package com.solaredge.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.view.PanZoomGridView;
import com.solaredge.view.PanZoomGridView.OnGridClickListener;

public class ModifyLayoutActivity extends BaseActivity implements
		OnGridClickListener {

	@ViewInject(R.id.b_ok)
	private Button mOkBT;

	@ViewInject(R.id.i_reset)
	private ImageButton mReset;

	@ViewInject(R.id.p_grid_view)
	private PanZoomGridView mGridView;

	private int mRow = -1;
	private int mCol = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_modify_layout);
		super.onCreate(savedInstanceState);

		int[][] matrix = mSolarManager.getInverterMatrix();
		mGridView.setGridArray(matrix);
		mGridView.setSelectable(true);
		mGridView.addClickListener(this);
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
		mBack.setVisibility(View.GONE);
	}

	@OnClick(R.id.b_ok)
	private void onOkClick(View view) {
		finish();
	}
	
	@OnClick(R.id.b_func2)
	private void onAddClick(View view) {
		finish();
	}

	@OnClick(R.id.i_delete_plate)
	private void onDeletePlateClick(View view) {
		mGridView.deleteGridItem(mRow, mCol);
		mSolarManager.storeDeletedGridItem(mRow, mCol);
	}

	@OnClick(R.id.i_reset)
	private void onResetClick(View view) {
		mGridView.reset();
	}

	@Override
	public void onGridClick(int row, int col) {
		mRow = row;
		mCol = col;
	}
}
