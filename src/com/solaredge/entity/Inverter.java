package com.solaredge.entity;

public class Inverter {

	private String mInverterId;
	private String mInverterName;

	private int mGroupNumber;
	private int mClusterNumber;
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

	public String getStationId() {
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
}
