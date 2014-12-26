package com.solaredge.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.solaredge.R;
import com.solaredge.entity.PowerStation;
import com.solaredge.ui.InverterListActivity;

public class PowerStationListAdapter extends BaseAdapter {

	private List<PowerStation> mItems;
	private Context mContext;

	public PowerStationListAdapter(Context context) {
		mContext = context;
	}

	public PowerStationListAdapter(Context context, List<PowerStation> list) {
		mContext = context;
		mItems = list;
	}

	public void setItems(List<PowerStation> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		mItems = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mItems == null || mItems.size() == 0) {
			return 0;
		}

		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.power_station_list_item, parent, false);
			holder = new ViewHolder();
			holder.stationName = (TextView) convertView
					.findViewById(R.id.t_station_name);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.stationName.setText(mItems.get(position).getStationName());
		holder.stationName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, InverterListActivity.class);
				intent.putExtra("station_id", mItems.get(position)
						.getStationId());
				intent.putExtra("station_name", mItems.get(position)
						.getStationName());
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	public static class ViewHolder {
		public TextView stationName;
	}

}
