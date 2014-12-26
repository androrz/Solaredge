package com.solaredge.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.entity.JsonResponse;
import com.solaredge.entity.PowerStation;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.SlrResponse;
import com.solaredge.ui.adapter.InverterListAdapter;
import com.solaredge.utils.DbHelp;

public class InverterListActivity extends BaseActivity {

	private ListView mList;
	private InverterListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_inverter_list);
		super.onCreate(savedInstanceState);

		mBaseHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mSolarManager.getInverterList(mIntent
						.getStringExtra("station_id"));
			}
		}, 100);

		List<Inverter> list = new ArrayList<Inverter>();
		// Inverter inverter = new Inverter();
		// inverter.setInverterId("001");
		// inverter.setInverterName("逆变器1");
		// inverter.setmGroupNumber(10);
		// inverter.setmClusterNumber(8);
		// inverter.setmAngle(0);
		// list.add(inverter);
		//
		// inverter = new Inverter();
		// inverter.setInverterId("002");
		// inverter.setInverterName("逆变器2");
		// inverter.setmGroupNumber(5);
		// inverter.setmClusterNumber(10);
		// inverter.setmAngle(90);
		// list.add(inverter);
		mAdapter = new InverterListAdapter(this, list);
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
		mXFunc2.setImageResource(R.drawable.drw_confirm);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_func2:
			jumpToPage(InverterLayoutActivity.class);
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	public void handleEvent(int resultCode, SlrResponse response) {
		if (!analyzeAsyncResultCode(resultCode, response)) {
			return;
		}

		int action = response.getResponseEvent();
		JsonResponse jr = response.getResponseContent();
		switch (action) {
		case SvcNames.WSN_GET_INVERTERS:
			handleStationList(jr);
			break;
		default:
			break;
		}
	}

	private void handleStationList(JsonResponse jr) {
		if (jr == null) {
			return;
		}

		List<Inverter> list = new ArrayList<Inverter>();
		try {
			JSONArray array = jr.getBodyArray("inverters");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				Inverter inverter = new Inverter();
				inverter.setInverterId(object.getString("id"));
				inverter.setInverterName(object.getString("label"));
				inverter.setmGroupNumber(Integer.valueOf(object
						.getString("listcount")));
				inverter.setmClusterNumber(Integer.valueOf(object
						.getString("prelistmoudler")));
				inverter.setmAngle(Integer.valueOf(object.getString("tilt")));
				list.add(inverter);
				try {
					DbHelp.getDbUtils(this).save(inverter);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mAdapter.setItems(list);
	}

}
