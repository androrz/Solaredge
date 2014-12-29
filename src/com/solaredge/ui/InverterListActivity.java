package com.solaredge.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.entity.JsonResponse;
import com.solaredge.fusion.SvcNames;
import com.solaredge.server.response.SlrResponse;
import com.solaredge.ui.adapter.InverterListAdapter;
import com.solaredge.utils.DbHelp;

public class InverterListActivity extends BaseActivity {

	private ListView mList;
	private InverterListAdapter mAdapter;
	private String mStationId;

	@ViewInject(R.id.b_add_inverter)
	private Button mAddInverterBT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_inverter_list);
		super.onCreate(savedInstanceState);

		mStationId = mIntent.getStringExtra("station_id");
		List<Inverter> list = new ArrayList<Inverter>();
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
	protected void onResume() {
		super.onResume();
		mBaseHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mSolarManager.getInverterList(mStationId);
			}
		}, 100);
	}

	@OnClick(R.id.b_add_inverter)
	private void onAddInverterClick(View view) {
		Bundle bundle = new Bundle();
		bundle.putString("station_id", mStationId);
		jumpToPage(ModifyInverterActivity.class, bundle);
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
				inverter.setmStationId(mStationId);
				inverter.setInverterId(object.getString("id"));
				inverter.setInverterName(object.getString("label"));
				inverter.setmGroupNumber(Integer.valueOf(object
						.getString("listcount")));
				inverter.setmClusterNumber(Integer.valueOf(object
						.getString("prelistmoudler")));
				inverter.setmAngle(Integer.valueOf(object.getString("tilt")));
				list.add(inverter);
				try {
					DbHelp.getDbUtils(this).saveOrUpdate(inverter);
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
