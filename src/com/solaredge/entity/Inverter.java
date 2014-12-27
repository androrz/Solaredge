package com.solaredge.entity;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "table_inverter")
public class Inverter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String mInverterId;

	@Column(column = "station_id")
	private String mStationId;

	@Column(column = "inverter_name")
	private String mInverterName;

	@Column(column = "group_number")
	private int mGroupNumber;

	@Column(column = "cluster_number")
	private int mClusterNumber;

	@Column(column = "angle")
	private int mAngle;

	public Inverter() {

	}

	public Inverter(String mInverterName, int mGroupNumber, int mClusterNumber,
			int mAngle) {
		super();
		this.mInverterName = mInverterName;
		this.mGroupNumber = mGroupNumber;
		this.mClusterNumber = mClusterNumber;
		this.mAngle = mAngle;
	}

	public String getInverterId() {
		return mInverterId;
	}

	public void setInverterId(String mStationId) {
		this.mInverterId = mStationId;
	}

	public String getInverterName() {
		return mInverterName;
	}

	public void setInverterName(String mStationName) {
		this.mInverterName = mStationName;
	}

	public int getmGroupNumber() {
		return mGroupNumber;
	}

	public void setmGroupNumber(int mGroupNumber) {
		this.mGroupNumber = mGroupNumber;
	}

	public int getmClusterNumber() {
		return mClusterNumber;
	}

	public void setmClusterNumber(int mClusterNumber) {
		this.mClusterNumber = mClusterNumber;
	}

	public int getmAngle() {
		return mAngle;
	}

	public void setmAngle(int mAngle) {
		this.mAngle = mAngle;
	}

	public String getmStationId() {
		return mStationId;
	}

	public void setmStationId(String mStationId) {
		this.mStationId = mStationId;
	}

}
