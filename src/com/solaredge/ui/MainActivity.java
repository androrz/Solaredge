package com.solaredge.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.solaredge.LoginActivity;
import com.solaredge.R;
import com.solaredge.SelectLanguageActivity;
import com.solaredge.entity.PowerStation;
import com.solaredge.fusion.FusionField;
import com.solaredge.ui.adapter.PowerStationListAdapter;

public class MainActivity extends BaseActivity {

	private ListView mList;
	private PowerStationListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		if (!FusionField.solarUser.isUserLogin()) {
			jumpToPage(LoginActivity.class, true);
		}

		List<PowerStation> list = new ArrayList<PowerStation>();
		PowerStation station = new PowerStation();
		station.setStationId("001");
		station.setStationName("赛拉弗");
		list.add(station);

		station = new PowerStation();
		station.setStationId("002");
		station.setStationName("东方电气");
		list.add(station);
		mAdapter = new PowerStationListAdapter(this, list);
		mList.setAdapter(mAdapter);
	}

	@Override
	protected void initWidgetEvent() {
		super.initWidgetEvent();
	}

	@Override
	protected void initWidgetProperty() {
		super.initWidgetProperty();

		mList = (ListView) findViewById(R.id.l_power_station_list);

		mServiceTitle.setText("集能易");
		mXFunc2.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_func2:
			jumpToPage(SelectLanguageActivity.class);
			break;

		default:
			break;
		}
		super.onClick(v);
	}

}
