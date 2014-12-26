package com.solaredge.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.solaredge.R;
import com.solaredge.entity.Inverter;
import com.solaredge.ui.ModifyInverterActivity;

public class InverterListAdapter extends BaseAdapter {

	private List<Inverter> mItems;
	private Context mContext;

	public InverterListAdapter(Context context) {
		mContext = context;
	}

	public InverterListAdapter(Context context, List<Inverter> list) {
		mContext = context;
		mItems = list;
	}

	public void setItems(List<Inverter> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		mItems = list;
		notifyDataSetChanged();
	}

	public List<Inverter> getItem() {
		return mItems;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.inverter_list_item, parent, false);
			holder = new ViewHolder();
			holder.inverterName = (TextView) convertView
					.findViewById(R.id.t_inverter_name);
			holder.groupNumber = (TextView) convertView
					.findViewById(R.id.t_group_number);
			holder.clusterNumber = (TextView) convertView
					.findViewById(R.id.t_cluster_number);
			holder.angle = (TextView) convertView.findViewById(R.id.t_angle);
			holder.modify = (Button) convertView.findViewById(R.id.b_modify);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.inverterName.setText(mItems.get(position).getInverterName());
		holder.groupNumber.setText(mItems.get(position).getmGroupNumber() + "");
		holder.clusterNumber.setText(mItems.get(position).getmClusterNumber()
				+ "");
		holder.angle.setText(mItems.get(position).getmAngle() + "");
		holder.modify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						ModifyInverterActivity.class);
				mContext.startActivity(intent);
			}
		});
		if (position % 2 == 0) {
			convertView.setBackgroundColor(Color.parseColor("#CCCCCC"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#E4E4E4"));
		}

		return convertView;
	}

	public static class ViewHolder {
		public TextView inverterName;
		public TextView groupNumber;
		public TextView clusterNumber;
		public TextView angle;
		public Button modify;
	}

}
